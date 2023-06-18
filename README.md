# android_app
移动应用软件开发大作业
## 用户部署

在本地部署时，首先获取本机的IP地址。

打开前端文件夹中Frontend/app/src/main/res/values/strings.xml，将这个文件第三行中的内容改为自己的IP地址加上:8080。

后端运行：在命令行中启动

`python manage.py runserver xxx.xxx.xxx.xxx:8000`

前端运行：在Android Studio中启动即可
