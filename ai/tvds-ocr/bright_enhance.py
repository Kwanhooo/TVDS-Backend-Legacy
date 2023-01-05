# File    :   bright_enhance.py
# Time    :   2022/10/28 9:05
# Author  :   Ouyang Yiqi
# def bright_Img(path, ga, flag):
#     """
#     亮度增强 Tensorlayer
#     :param path:
#     :param ga: ga为gamma值，>1亮度变暗，<1亮度变亮
#     :param flag:True: 亮度值为(1-ga,1+ga)
#                 False:亮度值为ga,默认为1
#     :return: 亮度增强后的图像
#     """
#     image = tl.vis.read_image(path)
#     tenl_img = tl.prepro.brightness(image, gamma=ga, is_random=flag)
#     return tenl_img

from PIL import Image, ImageStat
import cv2
import random
import math
import numpy as np
import tensorlayer as tl


def hisColor_Img(path):
    """
    对图像直方图均衡化
    :param path: 图片路径
    :return: 直方图均衡化后的图像
    """
    img = cv2.imread(path)
    ycrcb = cv2.cvtColor(img, cv2.COLOR_BGR2YCR_CB)
    channels = cv2.split(ycrcb)
    cv2.equalizeHist(channels[0], channels[0])  # equalizeHist(in,out)
    cv2.merge(channels, ycrcb)
    img_eq = cv2.cvtColor(ycrcb, cv2.COLOR_YCR_CB2BGR)
    return img_eq


def image_brightness(rgb_image):
    """
    检测图像亮度(基于RMS)
    """
    stat = ImageStat.Stat(rgb_image)
    r, g, b = stat.rms
    return math.sqrt(0.241 * (r ** 2) + 0.691 * (g ** 2) + 0.068 * (b ** 2))


def bright_Img(path, ga, flag):
    """
    亮度增强 Tensorlayer
    :param path: 地址
    :param ga: ga为gamma值，>1亮度变暗，<1亮度变亮
    :param flag:True: 亮度值为(1-ga,1+ga)
                False:亮度值为ga,默认为1
    :return: 亮度增强后的图像
    """
    image = tl.vis.read_image(path)
    tenl_img = tl.prepro.brightness(image, gamma=ga, is_random=flag)
    return tenl_img


def bright_enhance(img_path, target_bright=80, only_enhance=True):
    gamma = 1
    img = tl.vis.read_image(img_path)
    img_bright = image_brightness(Image.open(img_path))
    if only_enhance:
        if img_bright > target_bright:
            return Image.fromarray(img)

    delta = 0.02
    # while bright_abs > threshold:
    #     if img_bright > target_bright:
    #         gamma += delta
    #         img = tl.prepro.brightness(img, gamma)
    #         img_bright = image_brightness(Image.fromarray(img))
    #         bright_abs = abs(img_bright - target_bright)
    #     else:
    #         gamma -= delta
    #         img = tl.prepro.brightness(img, gamma)
    #         img_bright = image_brightness(Image.fromarray(img))
    #         bright_abs = abs(img_bright - target_bright)
    while img_bright < target_bright:
        gamma -= delta
        img = tl.prepro.brightness(img, gamma)
        img_bright = image_brightness(Image.fromarray(img))

    return Image.fromarray(img)


def auto_bright(img_template_path, img_target_path, only_enhance=True):
    """
    将目标图像亮度对齐模板图像
    :param only_enhance: 是否只对较暗的图像进行增强
    :param img_target_path: 目标图像路径
    :param img_template_path: 模板图像路径
    :return: 经过光照增强后的目标图像 PIL 格式
    """
    gamma = 1
    # image_template = tl.vis.read_image(img_template_path)
    image_target = tl.vis.read_image(img_target_path)
    image_template = Image.open(img_template_path)
    image_template_brightness = image_brightness(image_template)
    image_target_brightness = image_brightness(Image.open(img_target_path))

    # print("模板图像的亮度值为:{}".format(image_template_brightness))
    # print("目标图像的初始亮度值为:{}".format(image_target_brightness))

    if only_enhance:
        if image_target_brightness > image_template_brightness:
            return Image.fromarray(image_target)
    threshold = 2
    delta = 0.02
    bright_abs = abs(image_target_brightness - image_template_brightness)
    while bright_abs > threshold:
        if image_target_brightness > image_template_brightness:
            gamma += delta
            image_target = tl.prepro.brightness(image_target, gamma)
            image_target_brightness = image_brightness(Image.fromarray(image_target))
            bright_abs = abs(image_target_brightness - image_template_brightness)
        else:
            gamma -= delta
            image_target = tl.prepro.brightness(image_target, gamma)
            image_target_brightness = image_brightness(Image.fromarray(image_target))
            bright_abs = abs(image_target_brightness - image_template_brightness)
    # print("目标图像的最终亮度值为:{}".format(image_target_brightness))
    return Image.fromarray(image_target)


if __name__ == '__main__':
    # image = Image.open("./images/template_5327501.jpg")
    # image2 = Image.open("./images/20220123045_2_37.jpg")
    # # image2.show('123')
    # bright = image_brightness(image)
    # bright2 = image_brightness(image2)
    # #   img_bright = bright_Img("./images/20220123045_2_37.jpg", 0.7, False)
    # img_bright = bright_Img("./images/20220123045_2_37.jpg", 1.5, False)
    # img_bright = Image.fromarray(img_bright)
    # img_bright.show()
    # print(bright)
    # print(bright2)
    # print(image_brightness(img_bright))
    # img_bright.save("./images/2_37_bright.jpg")

    img_template_path = "./images/template_5327501.jpg"
    img_target_path = "./images/20220123045_2_37.jpg"
    img_target = auto_bright(img_template_path, img_target_path)
    img_target.save("./images/target.jpg")

    # img_his = hisColor_Img(img_target_path)
    # cv2.imshow("123", img_his)
    # cv2.imwrite("./images/img_his.jpg", img_his)
    # cv2.waitKey(0)
    # print(image_brightness(Image.open("./images/img_his.jpg")))

