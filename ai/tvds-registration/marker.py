import json
import sys

from tensorlayer.prepro import cv2


def draw(target_image, lu, rb):
    img = cv2.imread(target_image)

    # 绘制矩形框
    cv2.rectangle(img, lu, rb, (0, 0, 255), 20)
    # 标注文字
    cv2.putText(img, 'wheel', (lu[0], lu[1] - 30), cv2.FONT_HERSHEY_SIMPLEX, 4, (0, 0, 255), 6)

    # 显示图像
    cv2.imwrite(target_image, img, [cv2.IMWRITE_JPEG_QUALITY, 95])
    cv2.waitKey(0)
    cv2.destroyAllWindows()


if __name__ == '__main__':
    # jsonfile = "/home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/tvds-registration/images/template/X70/part_index.json"
    # i = '/home/kwanho/Workspace/Workspace-TVDS/TVDS-Backend/ai/tvds-registration/images/template/X70/template.jpg'
    # o = '/home/kwanho/Workspace/Workspace-TVDS/TVDS-Backend/ai/tvds-registration/rectangle.jpg'
    jsonfile = sys.argv[1]
    i = sys.argv[2]
    o = sys.argv[3]

    # 复制
    origin = cv2.imread(i)

    cv2.imwrite(o, origin, [cv2.IMWRITE_JPEG_QUALITY, 100])
    # 绘制
    with open(jsonfile, encoding="utf-8") as f:
        data = json.load(f)
    for key, value in data['wheel'].items():
        left_up = value['left_up']
        right_bottom = value['right_bottom']
        draw(o, left_up, right_bottom)
    print(True)
