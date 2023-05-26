from django.db import models

# Create your models here.


class account(models.Model):
    ID = models.AutoField(primary_key=True)
    username = models.CharField(max_length=25, verbose_name='用户名')
    password = models.CharField(max_length=25, verbose_name='密码')
    nickname = models.CharField(max_length=25, verbose_name='昵称')
    introduction = models.CharField(max_length=50, verbose_name='简介')


class image(models.Model):
    image_content = models.IntegerField(primary_key=True)


class pyq(models.Model):
    avatar = models.IntegerField(verbose_name='头像')
    username = models.CharField(max_length=25, verbose_name='用户名')
    ID = models.AutoField(primary_key=True)
    posttime = models.DateTimeField(auto_now_add=True, verbose_name='发表时间')
    title = models.CharField(max_length=25, verbose_name='标题')
    content = models.CharField(max_length=500, verbose_name='内容')
    image_contain = models.ManyToManyField(image)


class message(models.Model):
    msg_content = models.CharField(max_length=500, verbose_name='消息内容')
    msg_ID = models.IntegerField(verbose_name='消息序号')
    msg_time = models.DateTimeField(auto_now_add=True, verbose_name='发表时间')


class chat(models.Model):
    msg_contain = models.ManyToManyField(message)
    oppo_id = models.AutoField(verbose_name='对面ID', primary_key=True)
    msg_cnt = models.IntegerField(verbose_name='消息数量')


class chats(models.Model):
    chat_contain = models.ManyToManyField(chat)
