# File    :   utils.py
# Time    :   2022/10/28 15:55
# Author  :   Ouyang Yiqi
import sys
from pathlib import Path
import re
import glob
import cv2
import numpy as np
import os
from ocr import orc_rec
from bright_enhance import bright_enhance


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

    temp_img_path = str(increment_path('./temp.jpg'))
    ocr_results = []

    if is_bright_enhance:
        img_temp = bright_enhance(src_path)
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
    print('inspec_no: ', inspec_no)
    print('inspec_time: ', inspec_time)
    print('seat_no: ', seat_no)
    print('car_number: ', car_number)
    print('car_type: ', car_type)
    print('car_id: ', car_id)
    print('final result: ', rt)
    return rt


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


if __name__ == "__main__":
    path = sys.argv[1]
    # path = '/home/kwanho/blob/3907_20220123001_2_1.jpg'
    # path = '/home/kwanho/Workspace/Workspace-TVDS/TVDS-Backend/blob/origin/3909/20220111111_2_2.jpg'
    info = OCR(path)
    print(info)