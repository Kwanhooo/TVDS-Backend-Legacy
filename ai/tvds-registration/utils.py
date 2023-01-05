# File    :   utils.py
# Time    :   2022/10/28 15:55
# Author  :   Ouyang Yiqi
import pathlib
import random
import sys
from pathlib import Path
import re
import glob
import cv2
import numpy as np
import os
from ocr import orc_rec
import json
from bright_enhance import auto_bright, bright_enhance


def OCR(src_path, is_bright_enhance=True):
    try:
        split_path = src_path.split("/")
        inspec_no = split_path[-2]
        detail_info = split_path[-1].split('.')[0].split('_')
        inspec_time = detail_info[0][:8]
        seat_no = detail_info[1]
        car_number = detail_info[2]
    except IndexError:
        return "error input src_path"

    temp_img_path = './temp.jpg'
    ocr_results = []

    if is_bright_enhance:
        img_temp = bright_enhance("./images/template/X70/template.jpg")
        img = cv2.cvtColor(np.asarray(img_temp), cv2.COLOR_RGB2BGR)
        img = cv2.resize(img, (img.shape[1] // 10, img.shape[0] // 10), interpolation=cv2.INTER_AREA)
        cv2.imwrite(temp_img_path, img)
        words_results = orc_rec(temp_img_path)
        os.remove(temp_img_path)
    else:
        words_results = orc_rec(src_path)

    for results in words_results:
        text = results['words']
        ocr_results.append(text)

    pattern_car_number = re.compile(r'\d{7}$')
    pattern_car_type = re.compile(r'X70')

    is_match_type = False  # 是否匹配到了型号
    car_id = ''
    for text in ocr_results:
        match_obj_number = pattern_car_number.search(text)
        match_obj_type = pattern_car_type.search(text)
        if match_obj_number is not None:
            car_id += match_obj_number.group()
        if match_obj_type is not None:
            is_match_type = True

    if is_match_type:
        car_type = "X70"
    else:
        car_type = "none"
    rt = '_'.join([inspec_no, inspec_time, seat_no, car_number, car_type, car_id])
    return rt


def ocr_with_save(img_dir, save_dir=None):
    """
    用orc识别列车图像的车型和车号
    :param img_dir: 要识别的图像的文件夹
    :param save_dir: 结果以.json文件的形式保存，默认保存路径与img_dir一致
    :return:
    """
    if save_dir is None:
        save_dir = img_dir

    files = os.listdir(img_dir)
    temp_img_path = str(pathlib.Path(img_dir).with_name('temp_img.jpg'))
    ocr_results = dict()
    for file in files:
        if Path(file).suffix != ".jpg" and Path(file).suffix != ".png":
            continue
        file_path = img_dir + "/" + file
        ocr_results[file] = []
        # img = cv2.imread(file_path)
        # img = cv2.resize(img, (img.shape[1] // 10, img.shape[0] // 10), interpolation=cv2.INTER_AREA)
        # cv2.imwrite(temp_img_path, img)

        img_temp = auto_bright("./images/template/X70/template.jpg", file_path)
        img = cv2.cvtColor(np.asarray(img_temp), cv2.COLOR_RGB2BGR)
        img = cv2.resize(img, (img.shape[1] // 10, img.shape[0] // 10), interpolation=cv2.INTER_AREA)
        cv2.imwrite(temp_img_path, img)

        words_results = orc_rec(temp_img_path)
        os.remove(temp_img_path)
        for results in words_results:
            text = results['words']
            ocr_results[file].append(text)
    # json_dict = json.dumps(ocr_results)
    with open(img_dir + "/orc_result.json", "w", encoding='utf-8') as f:
        json.dump(ocr_results, f, indent=2, sort_keys=True, ensure_ascii=False)

    orc_finale_result = dict()
    pattern_car_number = re.compile(r'\d{7}$')
    pattern_car_type = re.compile(r'X70')
    for key, values in ocr_results.items():
        is_match_type = False  # 是否匹配到了型号
        car_number = ''
        for value in values:
            match_obj_number = pattern_car_number.search(value)
            match_obj_type = pattern_car_type.search(value)
            if match_obj_number is not None:
                car_number += match_obj_number.group()
            if match_obj_type is not None:
                is_match_type = True
        if is_match_type:
            car_type = "X70"
        else:
            car_type = "None"
        car_info = {'car_type': car_type, 'car_number': car_number}
        orc_finale_result[key] = car_info

    with open(img_dir + "/orc_final_result.json", "w", encoding='utf-8') as f:
        json.dump(orc_finale_result, f, indent=2, sort_keys=True, ensure_ascii=False)


def save_parameter(paras: dict, path):
    fileName = path + "/参数.txt"
    file = open(fileName, 'a', encoding='utf-8')
    for para in paras.items():
        file.write("{} = {}\n".format(para[0], para[1]))
    file.close()


def increment_path(path, exist_ok=False, sep='_', mkdir=True):
    """
    Increment file or directory path, i.e. runs/exp --> runs/exp{sep}2, runs/exp{sep}3, ... etc.
    :param path: file or directory path to increment
    :param exist_ok: existing project/name ok, do not increment
    :param sep: separator for directory name
    :param mkdir: create directory
    :return: incremented path
    """
    path = Path(path)  # os-agnostic
    if path.exists() and not exist_ok:
        suffix = path.suffix
        path = path.with_suffix('')
        dirs = glob.glob(f"{path}{sep}*")  # similar paths
        matches = [re.search(rf"%s{sep}(\d+)" % path.stem, d) for d in dirs]
        i = [int(m.groups()[0]) for m in matches if m]  # indices
        n = max(i) + 1 if i else 2  # increment number
        path = Path(f"{path}{sep}{n}{suffix}")  # update path
    dir_ = path if path.suffix == '' else path.parent  # directory
    if not dir_.exists() and mkdir:
        dir_.mkdir(parents=True, exist_ok=True)  # make directory
    return path


def draw_vertical_line(img, step=20):
    row, column = img.shape[0], img.shape[1]
    img_result = img.copy()
    for i in range(0, column, step):
        draw_vertical_dot_line(img_result, i)
    return img_result


def draw_vertical_dot_line(img, column_index, step=5):
    row = img.shape[0]
    color = (random.randint(0, 255), random.randint(0, 255), random.randint(0, 255))
    for i in range(0, row - step, step):
        if i % 2 == 0:
            cv2.line(img, (column_index, i), (column_index, i + step), color=color)


def draw_matches_vstack(img1, img2, kp1, kp2, matches):
    """
    with vertical stack  to paint matches between img1 and img2
    :param img1: BGR image source,(usually by cv2.imread() return)
    :param img2: BGR image source,(usually by cv2.imread() return)
    :param kp1: key point for img1
    :param kp2: key point for img2
    :param matches: matches
    :return: a picture with matches painted , type: np.array
    """

    h1, w1, c1 = img1.shape
    h2, w2, c2 = img2.shape

    t_img1 = img1.copy()
    t_img2 = img2.copy()
    if w1 - w2 > 0:
        patch = np.zeros((h2, w1 - w2, c2), dtype=img2.dtype)
        t_img2 = np.hstack((img2, patch))
    if w1 - w2 < 0:
        patch = np.zeros((h1, w2 - w1, c1), dtype=img1.dtype)
        t_img1 = np.hstack((img1, patch))

    points1 = np.zeros((len(matches), 2), dtype=float)
    points2 = np.zeros((len(matches), 2), dtype=float)

    kp1_draw = []
    kp2_draw = []

    for i in range(len(matches)):
        points1[i, :] = kp1[matches[i].queryIdx].pt
        points2[i, :] = kp2[matches[i].trainIdx].pt  # 最佳匹配特征点位置
        kp1_draw.append(kp1[matches[i].queryIdx])
        kp2_draw.append(kp2[matches[i].trainIdx])

    img1_keypoints = np.zeros_like(t_img1)
    cv2.drawKeypoints(t_img1, kp1_draw, img1_keypoints)
    img2_keypoints = np.zeros_like(t_img2)
    cv2.drawKeypoints(t_img2, kp2_draw, img2_keypoints)
    img_result = np.vstack((img1_keypoints, img2_keypoints))

    # img_result = np.vstack((img1, img2))
    assert len(points1) == len(points2), "length of points_1 must be equal with length of points_2"
    for i in range(len(points1)):
        color = (random.randint(0, 255), random.randint(0, 255), random.randint(0, 255))
        cv2.line(img_result, (int(points1[i][0]), int(points1[i][1])),
                 (int(points2[i][0]), int(points2[i][1] + h1)), color=color)
    return img_result


def crop_parts(img_path, jsonfile, save_dir="./images/parts/"):
    """
    根据模板分割零部件
    :param img_path: 图片地址
    :param jsonfile:模板图像零部件坐标
    :param save_dir: 保存零部件的文件夹
    :return:
    """

    img = cv2.imread(img_path)
    with open(jsonfile, encoding="utf-8") as f:
        results = json.load(f)
    # 根据图片名创建保存该图片零部件的文件夹，以过检号命名
    img_name = Path(img_path).stem
    name_split = img_name.split('_')
    # if not os.path.exists(save_dir + name_split[0] + '/'):
    #     os.makedirs(save_dir + name_split[0] + '/')
    save_path = save_dir + name_split[0] + '_' + name_split[1] + '_' + name_split[2] + '/'
    print(save_path)
    if not os.path.exists(save_path):
        os.makedirs(save_path)

    # 读取模零部件坐标，分割零件
    for part_name, part_indexes in results.items():
        for part_number, part_index in part_indexes.items():
            left_up = part_index['left_up']
            right_bottom = part_index['right_bottom']
            crop = img[left_up[1]:right_bottom[1], left_up[0]:right_bottom[0]].copy()
            crop_name = part_name + "_" + part_number + ".jpg"
            cv2.imwrite(save_path + crop_name, crop)
    return True


def calc_abc_from_line_2d(x0, y0, x1, y1):
    """
    计算直线的a,b,c值， ax+by+c=0, 直线由(x0,y0),(x1,y1)确定
    :param x0:
    :param y0:
    :param x1:
    :param y1:
    :return:
    """
    a = y0 - y1
    b = x1 - x0
    c = x0 * y1 - x1 * y0
    return a, b, c


def get_line_cross_point(line1, line2):
    # x1y1x2y2
    a0, b0, c0 = calc_abc_from_line_2d(*line1)
    a1, b1, c1 = calc_abc_from_line_2d(*line2)
    D = a0 * b1 - a1 * b0
    if D == 0:
        return None
    x = (b0 * c1 - b1 * c0) / D
    y = (a1 * c0 - a0 * c1) / D
    # print(x, y)
    return x, y


def is_cross_point_in_line(point, line):
    x0, y0, x1, y1 = line
    x, y = point
    if (x - x0) * (x - x1) <= 0:
        return True
    return False


if __name__ == "__main__":
    # img_path = "/home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/tvds-registration/images/aligned/3907_2_1.jpg"
    # save_dir = "/home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/tvds-registration/images/parts/"
    # jsonfile = "/home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/tvds-registration/images/template/X70/part_index.json"
    img_path = sys.argv[1]
    save_dir = sys.argv[2]
    jsonfile = sys.argv[3]
    print(crop_parts(img_path=img_path, jsonfile=jsonfile, save_dir=save_dir))
