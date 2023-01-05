# File    :   image_registration.py
# Time    :   2022/11/3 10:48
# Author  :   Ouyang Yiqi
import os
import random
import sys
import time
from pathlib import Path

import cv2
import numpy as np
from scipy import stats
from sklearn.cluster import dbscan

from bright_enhance import bright_enhance
from utils import get_line_cross_point, is_cross_point_in_line, calc_abc_from_line_2d


def PCA(des1, des2):
    """
    :param des1: 模板图像的特征向量
    :param des2: 待匹配图像的特征向量
    :return:
    """
    # des2_all = np.concatenate(des2_list,axis=0) #对多副图，先concat所有sift特征
    # U,S,V = np.linalg.svd(des2_all)
    U, S, V = np.linalg.svd(des2)
    # des的shape为（sift_nb，128）sift_nb表示图像中检测到的sift特征数量，128代表维度
    # print(des2.shape) # (1078, 128) des1.shape (1237, 128) 计算量为1078*1237*128*N张图像
    size_projected = 32  # 选取前32维的，对结果影响只有1-2%
    projector = V[:, :size_projected]  # V的维度 (128, 128)，对角线特征值
    # for i in range(len(des2_list)):
    #    des2_list[i] = des2_list[i].dot(projector)
    des2_new = des2.dot(projector)  # des2_new.shape (1078, 32) 计算量为1078*1237*32*N张图像
    des1_new = des1.dot(projector)
    return des1_new, des2_new


def detect_with_savekp(img, detector):
    """
    :param img:
    :param detector:
    :return:
    """
    kp, des = detector.detectAndCompute(img, None)

    img_kp = np.zeros_like(img)
    cv2.drawKeypoints(img, kp, img_kp)

    return kp, des, img_kp


def getMatch(des1, des2, is_PCA, match_sample_ratio=1.0, sort_result=True):
    descriptors1, descriptors2 = des1.copy(), des2.copy()
    if is_PCA:
        descriptors1, descriptors2 = PCA(des1, des2)
    bf = cv2.BFMatcher(crossCheck=True)
    start_time = time.time()
    matches = bf.match(descriptors1, descriptors2)
    end_time = time.time()
    # print("是否降维?", is_PCA)
    # print("完成特征匹配，耗时: {:.2f}秒".format(end_time - start_time))
    if sort_result:
        matches = sorted(matches, key=lambda x: x.distance)
    match_sample_num = int(match_sample_ratio * len(matches))
    sample_matches = matches[:match_sample_num]
    # sample_matches = random.sample(matches, match_sample_num)
    return sample_matches


def cross_validation(points_1, points_2):
    """通过交叉验证法，剔除错误匹配"""
    assert len(points_1) == len(points_2), "length of points_1 must be equal with length of points_2"
    length = len(points_1)
    cross_info = {key: value for key in range(length) for value in [[] for i in range(length)]}
    for i in range(length):
        line1 = [*points_1[i], *points_2[i]]
        for j in range(i + 1, length):
            if i == j:
                continue
            line2 = [*points_1[j], *points_2[j]]
            cross_point = get_line_cross_point(line1, line2)
            if cross_point is None:
                continue
            if is_cross_point_in_line(cross_point, line1) or is_cross_point_in_line(cross_point, line2):
                cross_info[i].append(j)
                cross_info[j].append(i)
    cross_info = sorted(cross_info.items(), key=lambda x: len(x[1]), reverse=True)
    while True:
        match_index, cross_points = cross_info.pop(0)
        if len(cross_points) == 0:
            break
        for i in range(len(cross_info)):
            if match_index in cross_info[i][1]:
                cross_info[i][1].remove(match_index)
        cross_info = sorted(cross_info, key=lambda x: len(x[1]), reverse=True)
    good_match_index = [x[0] for x in cross_info]
    # good_points_1 = points_1[good_match_index]
    # good_points_2 = points_2[good_match_index]
    return good_match_index


def rough_culing(kp1, kp2, matches, height=204):
    """
    :param height:
    :param kp1: 待配准图像kp
    :param kp2: 模板图像kp
    :param matches:
    :return:
    """

    # Todo 通过计算交点个数
    point_1 = np.zeros((len(matches), 2), dtype=float)
    point_2 = np.zeros((len(matches), 2), dtype=float)

    # cv2.imwrite(str(increment_path(save_path + "/SampleMatches.jpg")), img_matches)

    for i, match in enumerate(matches):
        point_1[i, :] = kp1[match.queryIdx].pt
        point_2[i, :] = kp2[match.trainIdx].pt  # 最佳匹配特征点位置

    # 1.通过y值进行粗剔除
    y_tolerance = 5
    first_select = []
    for i in range(len(point_1)):
        if abs(point_1[i][1] - point_2[i][1]) < y_tolerance:
            first_select.append(i)

    matches_select_first = []
    for index in first_select:
        matches_select_first.append(matches[index])

    point_1 = point_1[first_select].copy()
    point_2 = point_2[first_select].copy()
    point_2[:, 1] += height

    # 2.交叉验证法剔除错误匹配
    second_select = cross_validation(point_1, point_2)  # 加上图片的高度，方便计算斜率等

    bt_matches = []
    for index in second_select:
        bt_matches.append(matches_select_first[index])

    bt_point_1 = point_1[second_select]
    bt_point_2 = point_2[second_select]

    return bt_point_1, bt_point_2, bt_matches


def cal_gradient_for_matches(points_1, points_2):
    assert len(points_1) == len(points_2), "length of points_1 must be equal with length of points_2"
    length = len(points_1)
    gradients = []
    for i in range(length):
        line = [*points_1[i], *points_2[i]]
        a, b, c = calc_abc_from_line_2d(*line)
        k = 0  # 实际上当b=0时，斜率不存在，但是这里为了方便后续处理，且由于这里的直线不可能有k=0的直线，因此用k=0代表斜率不存在
        if b != 0:
            k = - a / b
            if abs(k) > 1000:
                k = 1 / k
        gradients.append(k)
    return np.array(gradients)


def correct_distortion(points_1, points_2, matches, tolerance_scale=0.01, tolerance_error=1, iter_num=100):
    assert len(points_1) == len(points_2), "length of points_1 must be equal with length of points_2"
    good_scale = 1
    good_indexes = []
    good_points_1 = None
    good_points_2 = None
    min_loss = 9999
    while iter_num > 0:
        scale = cal_scale(points_1.copy(), points_2.copy(), sample_round=1, sample_ratio=0.5)  # 抽样计算畸变
        correct_points_1 = points_1.copy()
        # 如果畸变大小过大，则进行修正
        if abs(scale - 1) > tolerance_scale:
            correct_points_1[:, 0] = correct_points_1[:, 0] * scale

        gradients = cal_gradient_for_matches(correct_points_1.copy(), points_2.copy())  # 计算修正后的点与模板图像上的匹配点的斜率
        min_samples = int(0.2 * len(gradients))
        cor_samples, org_labels = dbscan(gradients.reshape(-1, 1), eps=0.05, min_samples=min_samples)  # 对斜率作聚类
        org_index = np.argwhere(org_labels != -1)[:, 0]
        labels = org_labels[org_labels != -1]
        if len(labels) == 0:
            continue
        cluser_index = stats.mode(labels)[0][0]  # 求出聚类中最大的哪个类别
        #  Todo:用模拟退火的思想接受第二聚类
        temp_index = np.argwhere(labels == cluser_index)[:, 0]
        final_indexes = org_index[temp_index]

        final_points_1 = correct_points_1[final_indexes]
        final_points_2 = points_2[final_indexes]

        mean_1 = np.mean(final_points_1, 0)  # 计算原图像特征点的x,y方向的均值，求得特征点的中心点的坐标
        mean_2 = np.mean(final_points_2, 0)  # 待检测图像向上面一样处理
        offset = mean_1[0] - mean_2[0]
        temp_points = final_points_1 - [offset, 0]
        residual = (temp_points - final_points_2)[:, 0]
        # weight = 1 / (math.exp(len(final_points_1)) - 1)
        weight = (len(points_1) - len(final_points_1)) / len(points_1)
        loss = (np.sum(np.square(residual)) / len(final_points_1)) * weight
        if loss < min_loss:
            min_loss = loss
            good_points_1 = final_points_1.copy()
            good_points_2 = final_points_2.copy()
            good_scale = scale
            good_indexes = final_indexes.copy()
        print("iter:{0} scale={1} loss={2}".format(100 - iter_num, scale, loss))
        iter_num -= 1

    good_match = []
    for good_index in good_indexes:
        good_match.append(matches[good_index])
    return good_points_1, good_points_2, good_match, good_scale, min_loss


def fine_culing(points_1, points_2, matches):
    assert len(points_1) == len(points_2), "length of points_1 must be equal with length of points_2"
    length = len(points_1)
    gradient_pos = []
    gradient_neg = []
    gradient_not_exit = []
    gradient = [gradient_pos, gradient_neg, gradient_not_exit]
    for i in range(length):
        line = [*points_1[i], *points_2[i]]
        a, b, c = calc_abc_from_line_2d(*line)
        k = 0  # 实际上当b=0时，斜率不存在，但是这里为了方便后续处理，且由于这里的直线不可能有k=0的直线，因此用k=0代表斜率不存在
        if b != 0:
            k = - a / b
        if k > 0:
            gradient_pos.append((i, k))
        elif k < 0:
            gradient_neg.append((i, k))
        else:
            gradient_not_exit.append((i, k))
    tag = np.argmax([len(gradient_pos), len(gradient_neg), len(gradient_not_exit)])  # 斜率为正、负、或者不存在，选择最多的那个
    indexes = [x[0] for x in gradient[tag]]
    gradients = np.array([x[1] for x in gradient[tag]])

    final_points_1 = points_1[indexes]
    final_points_2 = points_2[indexes]

    #  Todo:通过计算出的缩放比校正图像

    cor_samples, lables = dbscan(gradients.reshape(-1, 1), eps=0.1, min_samples=4)  # 对斜率作聚类
    cluser_index = stats.mode(lables)[0][0]  # 求出聚类中最大的那个类别
    final_indexes = np.argwhere(lables == cluser_index)[:, 0]

    final_points_1 = final_points_1[final_indexes]
    final_points_2 = final_points_2[final_indexes]

    final_match = []
    for final_index in final_indexes:
        final_match.append(matches[indexes[final_index]])
    return final_points_1, final_points_2, final_match


def cal_scale(points_1, points_2, sample_round=20, sample_ratio=0.8):
    assert len(points_1) == len(points_2), "length of points_1 must be equal with length of points_2"
    sample_num = int(len(points_1) * sample_ratio)
    index_range = [i for i in range(len(points_1))]
    while True:
        good_sample_num = 0
        scale = 0
        test_scale = 0
        is_good_sample = True
        for i in range(sample_round):
            s_scale = 0
            random_select = random.sample(index_range, sample_num)
            select_points_1 = points_1[random_select].copy()
            select_points_2 = points_2[random_select].copy()
            mean_1 = np.mean(select_points_1, 0)
            mean_2 = np.mean(select_points_2, 0)
            select_points_1 = select_points_1 - mean_1
            select_points_2 = select_points_2 - mean_2
            for j in range(sample_num):
                if (select_points_1[j][0] * select_points_2[j][0]) < 0 or (
                        select_points_1[j][1] * select_points_2[j][1]) < 0:
                    is_good_sample = False
                    break
                s_scale += select_points_2[j][0] / select_points_1[j][0]
            if is_good_sample:
                good_sample_num += 1
                sum_x_1 = np.sum(abs(select_points_1[:, 0]))
                sum_x_2 = np.sum(abs(select_points_2[:, 0]))
                scale += sum_x_2 / sum_x_1
                test_scale += s_scale / sample_num
        if good_sample_num != 0:
            avg_scale = scale / good_sample_num
            return avg_scale


def img_registration(point_1, point_2, img1, img2, src_path, template_path, scale=1,
                     recover_to_defaut_size=False, is_fill=False):
    """
    :param is_fill:
    :param scale:
    :param recover_to_defaut_size: 是否复原到原图像大小
    :param template_path:
    :param src_path: 目标图像名
    :param point_1:
    :param point_2:
    :param img1: 目标图像
    :param img2: 模板图像
    :return:
    """
    if len(point_1) == 0 or len(point_2) == 0:
        return None, None

    row, column, = img1.shape[0], img1.shape[1]  # 得到图像的大小
    row_t, column_t = img2.shape[0], img2.shape[1]
    column = int(column * scale)
    mean_1 = np.mean(point_1, 0)  # 计算原图像特征点的x,y方向的均值，求得特征点的中心点的坐标
    mean_2 = np.mean(point_2, 0)  # 待检测图像向上面一样处理

    left_intrp = int(mean_2[0])  # 模板图像左端到中心点（模板图像特征点中心）的距离
    right_intrp = column_t - int(mean_2[0])  # 模板图像右端到中心点的距离
    left_cut = 0  # 目标图像待裁剪的位置——左
    right_cut = column  # 目标图像待裁剪的位置——右
    # 目标图像不完整时，需要填充
    left_patch_num = 0  # 左边填充的偏移量
    right_patch_num = 0  # 右边填充的偏移量

    # 目标图像左边超出范围时，调整左边裁剪位置，反之左边不完整计算右边需要填充的偏移量
    if int(mean_1[0]) > left_intrp:
        left_cut = int(mean_1[0]) - left_intrp
    else:
        left_patch_num = left_intrp - int(mean_1[0])

    # 目标图像右边超出范围时，调整右边裁剪位置，反之右边不完整则计算右边需要填充的偏移量
    if column - int(mean_1[0]) > right_intrp:
        right_cut = int(mean_1[0]) + right_intrp
    else:
        right_patch_num = right_intrp + int(mean_1[0]) - column
    right_cut = right_cut + right_patch_num + left_patch_num

    src_img = cv2.imread(src_path)
    src_img = cv2.resize(src_img, (int(src_img.shape[1] * scale), src_img.shape[0]), interpolation=cv2.INTER_AREA)

    template_img = cv2.imread(template_path)

    # 是否复原到原图像大小
    if recover_to_defaut_size:
        left_cut = left_cut * 10
        right_cut = right_cut * 10
        left_patch_num = left_patch_num * 10
        right_patch_num = right_patch_num * 10
    else:
        src_img = cv2.resize(src_img, (src_img.shape[1] // 10, src_img.shape[0] // 10), interpolation=cv2.INTER_AREA)
        template_img = cv2.resize(template_img, (template_img.shape[1] // 10, template_img.shape[0] // 10),
                                  interpolation=cv2.INTER_AREA)
    path = Path(src_path)
    suffix = path.suffix
    stem = path.stem
    src_info = stem.split('_')

    # left_patch = np.zeros((src_img.shape[0], left_patch_num, 3), dtype=img1.dtype)      # 原图像为RGB图像，所以Patch应该为3通道
    # right_patch = np.zeros((src_img.shape[0], right_patch_num, 3), dtype=img1.dtype)

    left_patch = None
    right_patch = None

    if is_fill:
        if left_patch_num > 0:
            src_info_copy = src_info.copy()
            src_info_copy[-1] = str(int(src_info_copy[-1]) - 1)
            new_name = "_".join(src_info_copy) + suffix
            left_img_path = str(path.with_name(new_name))
            if os.path.exists(left_img_path):
                left_img = cv2.imread(left_img_path)
                left_img = cv2.resize(left_img, (int(left_img.shape[1] * scale), left_img.shape[0]),
                                      interpolation=cv2.INTER_AREA)
                if not recover_to_defaut_size:
                    left_img = cv2.resize(left_img, (left_img.shape[1] // 10, left_img.shape[0] // 10),
                                          interpolation=cv2.INTER_AREA)
                shape = left_img.shape
                left_patch = left_img[:, shape[1] - left_patch_num:].copy()
            else:
                shape = list(template_img.shape)
                shape[1] = left_patch_num
                left_patch = np.zeros(shape)

        if right_patch_num > 0:
            src_info_copy = src_info.copy()
            src_info_copy[-1] = str(int(src_info_copy[-1]) + 1)
            new_name = "_".join(src_info_copy) + suffix
            right_img_path = str(path.with_name(new_name))
            if os.path.exists(right_img_path):
                right_img = cv2.imread(right_img_path)
                right_img = cv2.resize(right_img, (int(right_img.shape[1] * scale), right_img.shape[0]),
                                       interpolation=cv2.INTER_AREA)
                if not recover_to_defaut_size:
                    right_img = cv2.resize(right_img, (right_img.shape[1] // 10, right_img.shape[0] // 10),
                                           interpolation=cv2.INTER_AREA)
                right_patch = right_img[:, 0:right_patch_num].copy()
            else:
                shape = list(template_img.shape)
                shape[1] = right_patch_num
                right_patch = np.zeros(shape)

    else:
        if left_patch_num > 0:
            shape = list(template_img.shape)
            shape[1] = left_patch_num
            left_patch = np.zeros(shape)
        if right_patch_num > 0:
            shape = list(template_img.shape)
            shape[1] = right_patch_num
            right_patch = np.zeros(shape)

    img_temp = src_img
    if left_patch is not None:
        img_temp = np.hstack((left_patch, img_temp))
    if right_patch is not None:
        img_temp = np.hstack((img_temp, right_patch))

    # img_temp = np.hstack((left_patch, src_img, right_patch))
    img_aligned = img_temp[:, left_cut:right_cut]
    img_vconcat = np.vstack((img_aligned, template_img))
    # 计算配准后图像与模板图像的互信息
    # MI = cal_normalized_mutual_info(img_aligned, template_img)
    return img_aligned, img_vconcat


# 通过滑动窗口的方式，对窗口中的特征点合并
def keypoint_merge(kps, des, row, column, window_size=(50, 50), stride=None, region=None):
    if region is None:
        region = [0, 389, 204, 1135]
    if stride is None:
        stride = window_size
    x_start = region[1]
    x_end = region[3]
    h_slide_num = int((x_end - x_start - window_size[0]) / stride[0])
    v_slide_num = int((row - window_size[1]) / stride[1])
    result_index = []
    other_index = set([i for i in range(len(kps))])
    for j in range(v_slide_num + 1):
        for i in range(h_slide_num + 1):
            indexes = []
            window_start = x_start + i * stride[0], j * stride[1]
            window_end = x_start + i * stride[0] + window_size[0], j * stride[1] + window_size[1]
            for k in range(len(kps)):
                if window_start[0] < kps[k].pt[0] < window_end[0] and window_start[1] < kps[k].pt[1] < window_end[1]:
                    indexes.append(k)
                    if k in other_index:
                        other_index.remove(k)
                if kps[k].pt[0] > window_end[0] and kps[k].pt[1] > window_end[1]:
                    break
            if len(indexes) == 0:
                continue
            points = cv2.KeyPoint_convert(kps, keypointIndexes=indexes)
            mean = np.mean(points, 0)
            center_index = 0
            min_value = 999
            for m, point in enumerate(points):
                distance = np.sum((point - mean) ** 2)
                if distance < min_value:
                    min_value = distance
                    center_index = m
            result_index.append(indexes[center_index])
    result_index = list(set(result_index))  # 去重
    result_index.extend(other_index)
    new_kps = []
    for index in result_index:
        new_kps.append(kps[index])
    new_des = des[result_index]
    return new_kps, new_des


def registration(src_path="./images/test/template_stretch.jpg",
                 template_path="/home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/tvds-registration/images/template/X70/template.jpg",
                 save_path="./images/aligned/",
                 merge_region=None,
                 method="SIFT",
                 is_PCA=False,
                 is_bright_enhance=True,
                 match_sample_ratio=0.8,
                 stride=(30, 10),
                 recover_to_default_size=True,
                 is_fill=True,
                 ):
    img_template = cv2.imread(template_path)
    img_template = cv2.resize(img_template, (img_template.shape[1] // 10, img_template.shape[0] // 10),
                              interpolation=cv2.INTER_AREA)

    row, column = img_template.shape[0], img_template.shape[1]
    detector = None
    if method == "SIFT":
        detector = cv2.SIFT_create()
    kp_t, des_t, img_template_kp = detect_with_savekp(img_template, detector)
    kp_t_merged, des_t_merged = keypoint_merge(kp_t, des_t, row, column, stride=stride, region=merge_region)

    split_path = src_path.split("/")
    try:
        base_name = split_path[-2] + "_" + split_path[-1].split("_")[1] + "_" + split_path[-1].split("_")[2]
    except IndexError:
        base_name = "irregular.jpg"

    if is_bright_enhance:
        img = bright_enhance(src_path)
        img_src = cv2.cvtColor(np.asarray(img), cv2.COLOR_RGB2BGR)
    else:
        img_src = cv2.imread(src_path)
    img_src = cv2.resize(img_src, (img_src.shape[1] // 10, img_src.shape[0] // 10),
                         interpolation=cv2.INTER_AREA)

    kp_s, des_s, img_src_kp = detect_with_savekp(img_src, detector)
    matches = getMatch(des_s, des_t_merged, is_PCA, match_sample_ratio)
    bt_point_s, bt_point_t, bt_matches = rough_culing(kp_s, kp_t_merged, matches, height=row)
    final_point_s, final_point_t, final_matches = fine_culing(bt_point_s, bt_point_t, bt_matches)

    img_aligned, img_vconcat = img_registration(point_1=final_point_s, point_2=final_point_t, img1=img_src,
                                                img2=img_template, src_path=src_path,
                                                template_path=template_path,
                                                recover_to_defaut_size=recover_to_default_size,
                                                is_fill=is_fill)
    if img_aligned is None:
        return False
    result_save_path = save_path + base_name
    # print(save_path)
    # print
    if not os.path.exists(save_path):
        os.makedirs(save_path)
    cv2.imwrite(result_save_path, img_aligned)
    return True


if __name__ == '__main__':
    # src_path = "/home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/tvds-registration/images/3907/20220123001_2_1.jpg"
    # save_path = "/home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/tvds-registration/images/aligned/"
    src_path = sys.argv[1]
    save_path = sys.argv[2]
    template_path = sys.argv[3]
    print(registration(src_path=src_path, save_path=save_path,template_path=template_path))
