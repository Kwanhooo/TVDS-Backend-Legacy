接口说明：
1. 车厢型号与车厢ID识别：utils.py - ORC(src_path, is_bright_enhance=True)
    参数说明：
        - src_path:需要识别的图片路径
        - is_bright_enhance:是否进行光照增强，默认为True，可不用管
    返回值：
        字符串类型，返回值为：过检号_过检时间_机位_车厢号_车型_车厢ID
    ex:
        输入为 XXXX/3907/20220123028_2_1.jpg 输出为 3907_20220123_2_1_X70_XXXXXXXX
    【注】现在的输入路径需要按照上述格式，否则可能会返回 “erro input src_path”

2. 图像配准：image_registration.py - registration(src_path="./images/test/template_stretch.jpg",
                                                template_path="./images/template/X70/template.jpg",
                                                save_path="./images/aligned/",
                                                merge_region=None,
                                                method="SIFT",
                                                is_PCA=False,
                                                is_bright_enhance=True,
                                                match_sample_ratio=0.8,
                                                stride=(30, 10),
                                                recover_to_default_size=True,
                                                is_fill=True,)
    参数说明：
        - src_path:要进行配准的图片路径
        - template_path:模板图像路径
        - save_path:配准后图片的保存地址
        - merge_region:这个参数先不管，已经在方法内部写死，只针对X70型车厢，后续有需要再修改
        - method:不用管，默认
        - is_PCA:默认
        - is_bright_enhance:默认
        - match_sample_ratio:默认
        - stride:默认
        - recover_to_default_size:默认
        - is_fill:默认
    返回值：
        bool类型， True or False : True 表示配准成功，会生成对应配准后的图片。 False表示配准失败，此时无图片生成

3. 零部件分割：utils.py crop_parts(img_path, jsonfile, img=None, save_dir="./images/parts/")
    参数说明：
        - img_path:图片路径 （这里处理的图片是配准后输出的图片即registration中生成的图片
        - jsonfile:json文件路径（包含零部件的位置信息）
        - save_dir:零部件保存地址，精确到哪个文件夹下就行
    返回值：无