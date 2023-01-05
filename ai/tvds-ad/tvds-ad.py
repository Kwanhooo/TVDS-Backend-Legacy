import sys

import numpy as np
import timm
import torch
import torch.nn as nn
from PIL import Image
from sklearn.covariance import LedoitWolf
from sklearn.neighbors import KernelDensity
from torch.utils.data import Dataset, DataLoader
from torchvision import transforms


# 密度估计 (通过计算输入图像的特征和所有正常图像的特征距离判断异常分数)
class Density(object):
    def fit(self, embeddings):
        raise NotImplementedError

    def predict(self, embeddings):
        raise NotImplementedError


class GaussianDensityTorch(object):
    def fit(self, embeddings):
        self.mean = torch.mean(embeddings, axis=0)
        self.inv_cov = torch.Tensor(LedoitWolf().fit(embeddings.cpu()).precision_, device="cpu")

    def predict(self, embeddings):
        distances = self.mahalanobis_distance(embeddings, self.mean, self.inv_cov)
        return distances

    @staticmethod
    def mahalanobis_distance(
            values: torch.Tensor, mean: torch.Tensor, inv_covariance: torch.Tensor
    ) -> torch.Tensor:
        assert values.dim() == 2
        assert 1 <= mean.dim() <= 2
        assert len(inv_covariance.shape) == 2
        assert values.shape[1] == mean.shape[-1]
        assert mean.shape[-1] == inv_covariance.shape[0]
        assert inv_covariance.shape[0] == inv_covariance.shape[1]

        if mean.dim() == 1:  # Distribution mean.
            mean = mean.unsqueeze(0)
        x_mu = values - mean  # batch x features
        # Same as dist = x_mu.t() * inv_covariance * x_mu batch wise
        dist = torch.einsum("im,mn,in->i", x_mu, inv_covariance, x_mu)
        return dist.sqrt()


class GaussianDensitySklearn():

    def fit(self, embeddings):
        # estimate KDE parameters
        # use grid search cross-validation to optimize the bandwidth
        self.kde = KernelDensity(kernel='gaussian', bandwidth=1).fit(embeddings)

    def predict(self, embeddings):
        scores = self.kde.score_samples(embeddings)

        # invert scores, so they fit to the class labels for the auc calculation
        scores = -scores

        return scores


# 自定义dataset
class MVTecAT(Dataset):

    def __init__(self, path, size, transform=None, mode="pred"):
        self.path = path  # 文件路径
        self.transform = transform
        self.mode = mode
        self.size = size

        if self.mode == "pred":  # 读取测试图像
            self.imgs = Image.open(path).resize((size, size)).convert("RGB")

    def __len__(self):
        return 1

    def __getitem__(self, idx):
        if self.mode == "pred":  # transform预处理，封装
            img = self.imgs
            if self.transform is not None:
                img = self.transform(img)
            return img


# 异常检测模型
class APSeg(nn.Module):
    def __init__(self, pretrained=True):
        super(APSeg, self).__init__()

        # code with timm
        self.fe = timm.create_model('convnext_base_384_in22ft1k', pretrained=pretrained, features_only=True)
        self.n_class = 2
        self.relu = nn.ReLU(inplace=True)
        self.deconv1 = nn.ConvTranspose2d(1024, 512, kernel_size=3, stride=2, padding=1, dilation=1, output_padding=1)
        self.bn1 = nn.BatchNorm2d(512)
        self.deconv2 = nn.ConvTranspose2d(512, 256, kernel_size=3, stride=2, padding=1, dilation=1, output_padding=1)
        self.bn2 = nn.BatchNorm2d(256)
        self.deconv3 = nn.ConvTranspose2d(256, 128, kernel_size=3, stride=2, padding=1, dilation=1, output_padding=1)
        self.bn3 = nn.BatchNorm2d(128)
        self.deconv4 = nn.ConvTranspose2d(128, 64, kernel_size=3, stride=2, padding=1, dilation=1, output_padding=1)
        self.bn4 = nn.BatchNorm2d(64)

        self.deconv5 = nn.ConvTranspose2d(64, 64, kernel_size=3, stride=2, padding=1, dilation=1, output_padding=1)
        self.bn5 = nn.BatchNorm2d(64)
        self.classifier5 = nn.Conv2d(64, self.n_class, kernel_size=1)

        self.classifier = nn.Conv2d(64, self.n_class, kernel_size=1)

        self.norm = nn.LayerNorm(1024, eps=1e-6)
        self.deconv = nn.ConvTranspose2d(2, 2, kernel_size=3, stride=2, padding=1, dilation=1, output_padding=1)
        self.linear = nn.Linear(1024, 512)

    def forward(self, x):

        embeds = self.fe(x)

        emb = self.norm(embeds[-1].mean([-2, -1]))
        # emb = self.linear(emb)

        # RDN module
        score = self.bn1(self.relu(self.deconv1(embeds[3])))
        score = score + embeds[2]
        score = self.bn2(self.relu(self.deconv2(score)))
        score = score + embeds[1]
        score = self.bn3(self.relu(self.deconv3(score)))
        score = score + embeds[0]
        score = self.bn4(self.relu(self.deconv4(score)))

        score = self.bn5(self.relu(self.deconv5(score)))
        score = self.classifier5(score)

        return score, emb

    def freeze_resnet(self):
        for param in self.fe.parameters():
            param.requires_grad = False

    def unfreeze(self):
        for param in self.parameters():
            param.requires_grad = True


# 每种零部件的异常阈值
thresholds = {"bearing": 20.0, "spring": 60.0}


def detect(path=None, size=256, device='cpu', model_path=None, bearing_npy=None):
    """

    :param path: 输入零部件图片路径
    :param size: 输入图片预处理后尺寸
    :param device: cuda or cpu
    :return:
    """
    # transforms预处理
    test_transform = transforms.Compose([])
    test_transform.transforms.append(transforms.Resize((size, size)))
    test_transform.transforms.append(transforms.ToTensor())
    test_transform.transforms.append(transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]))

    # dataset, dataloader
    test_datasets = MVTecAT(path, size, transform=test_transform, mode="pred")
    dataloader_test = DataLoader(test_datasets, batch_size=1, shuffle=False, num_workers=0)

    # 读取模型
    # model_path = f"model/spring.tch"
    # model_path = f"/home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/tvds-ad/model/spring.tch"
    weights = torch.load(model_path, map_location='cpu')
    model = APSeg(pretrained=False)
    model.load_state_dict(weights)
    model.to(device)
    model.eval()

    features = []
    with torch.no_grad():

        for x in dataloader_test:
            _, embed = model(x.to(device))  # 提取到的图像特征
            features.append(embed.cpu())

    features = torch.cat(features)

    # train_embed = np.load('logs/bearing.npy')  # 读取正常图像的表征(先这样写吧)
    train_embed = np.load(bearing_npy)  # 读取正常图像的表征(先这样写吧)
    train_embed = torch.from_numpy(train_embed)
    train_embed = torch.nn.functional.normalize(train_embed, p=2, dim=1)

    # 标准化
    train_embed = torch.nn.functional.normalize(train_embed, p=2, dim=1)
    features = torch.nn.functional.normalize(features, p=2, dim=1)

    # 距离估计
    density = GaussianDensityTorch()
    density.fit(train_embed)
    scores = density.predict(features)  # 输入图像异常得分

    if scores > thresholds["spring"]:  # 和阈值比较
        print(f"DEFECT")
        # open('logs/spring.txt', 'w').write('1')
    else:
        print(f"NORMAL")
        # open('logs/spring.txt', 'w').write('0')


if __name__ == '__main__':
    # 命令行输入图片路径
    path = sys.argv[1]
    model_path = sys.argv[2]
    bearing_npy = sys.argv[3]
    detect(path=path, model_path=model_path, bearing_npy=bearing_npy)
