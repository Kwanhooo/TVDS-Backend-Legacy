# File    :   ocr.py
# Time    :   2022/11/14 10:48
# Author  :   Ouyang Yiqi

# coding=utf-8

import sys
import json
import base64
import time

# 保证兼容python2以及python3
from urllib.request import urlopen
from urllib.request import Request
from urllib.error import URLError
from urllib.parse import urlencode
from urllib.parse import quote_plus

IS_PY3 = sys.version_info.major == 3
# 防止https证书校验不正确
import ssl

ssl._create_default_https_context = ssl._create_unverified_context

API_KEY = 'RW2IBBigIDdqk0KkdmZaWayn'

SECRET_KEY = '7NncOfz56A0G3Pr4pDcGXfXhspvOMP9R'

OCR_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic"

"""  TOKEN start """
TOKEN_URL = 'https://aip.baidubce.com/oauth/2.0/token'

"""
    获取token
"""


def fetch_token():
    global result_str
    params = {'grant_type': 'client_credentials',
              'client_id': API_KEY,
              'client_secret': SECRET_KEY}
    post_data = urlencode(params)
    if (IS_PY3):
        post_data = post_data.encode('utf-8')
    req = Request(TOKEN_URL, post_data)
    try:
        f = urlopen(req, timeout=5)
        result_str = f.read()
    except URLError as err:
        print(err)
    if (IS_PY3):
        result_str = result_str.decode()

    result = json.loads(result_str)

    if ('access_token' in result.keys() and 'scope' in result.keys()):
        if not 'brain_all_scope' in result['scope'].split(' '):
            print('please ensure has check the  ability')
            exit()
        return result['access_token']
    else:
        print('please overwrite the correct API_KEY and SECRET_KEY')
        exit()


"""
    读取文件
"""


def read_file(image_path):
    f = None
    try:
        f = open(image_path, 'rb')
        return f.read()
    except:
        print('read image file fail')
        return None
    finally:
        if f:
            f.close()


"""
    调用远程服务
"""


def request(url, data):
    req = Request(url, data.encode('utf-8'))
    has_error = False
    try:
        f = urlopen(req)
        result_str = f.read()
        if (IS_PY3):
            result_str = result_str.decode()
        return result_str
    except URLError as err:
        print(err)


def orc_rec(path):
    token = fetch_token()

    # 拼接通用文字识别高精度url
    image_url = OCR_URL + "?access_token=" + token
    file_content = read_file(path)
    result = request(image_url, urlencode({'image': base64.b64encode(file_content)}))
    result_json = json.loads(result)
    return result_json["words_result"]

    # text = ""
    # for words_result in result_json["words_result"]:
    #     text = text + words_result["words"]
    # print(text)


if __name__ == '__main__':
    words_result = orc_rec("./images/template_5327501_small.jpg")

