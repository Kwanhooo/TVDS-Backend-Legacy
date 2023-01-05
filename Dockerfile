# 基本镜像，ubuntu:22.04
FROM ubuntu:22.04

# 跳过时区选择（必须执行，否则后续构建镜像时会卡死到时区选择）
ENV DEBIAN_FRONTEND=noninteractive

# 编码格式
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

# 拷贝并解压jdk包
COPY jdk-8u351-linux-x64.tar.gz /opt/
RUN mkdir /usr/lib/jvm
RUN tar -xzvf /opt/jdk-8u351-linux-x64.tar.gz -C /usr/lib/jvm
RUN rm -rf /opt/jdk-8u351-linux-x64.tar.gz

# 配置Java环境
ENV JAVA_HOME /usr/lib/jvm/jdk1.8.0_351
ENV JRE_HOME ${JAVA_HOME}/jre
ENV CLASSPATH .:${JAVA_HOME}/lib:${JRE_HOME}/lib
ENV PATH ${JAVA_HOME}/bin:$PATH
RUN java -version

# 指定用户
USER root

# 切换时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# 切换到国内镜像源（镜像、容器内部的）
RUN  sed -i s@/archive.ubuntu.com/@/mirrors.aliyun.com/@g /etc/apt/sources.list
RUN  apt-get clean

RUN set -x; apt-get update \
   && apt-get install sudo \
   && sudo apt-get update \
   && sudo apt install -y xz-utils


### Python环境 ###
# AI 目录
RUN mkdir -p /home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/

# 复制源代码
COPY ai-code.tar.xz /home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/
RUN tar -xf /home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/ai-code.tar.xz -C /home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/
RUN rm -f /home/kwanho/Workspace/Workspace-TVDS/TVDS-AI/ai-code.tar.xz

# 复制venv
COPY tvds.tar.xz /root/
RUN mkdir -p /root/tvds-venv/
RUN tar -xf /root/tvds.tar.xz -C /root/tvds-venv/
RUN rm -f /root/tvds.tar.xz
################

# jar包
COPY ruoyi-admin/target/ruoyi-admin.jar /root/

# 暴露端口
EXPOSE 8080

# 容器启动时执行的命令
CMD ["java", "-jar", "/root/ruoyi-admin.jar"]

# 容器启动命令
