from django.db import models

# Create your models here.


class followperson(models.Model):
    ID = models.AutoField(primary_key=True)
    followerID = models.IntegerField()  # 关注的人
    followedpersonID = models.IntegerField()  # 被关注的人


class account(models.Model):
    avatar = models.ImageField(
        upload_to='avatar', default='', verbose_name='头像')
    ID = models.AutoField(primary_key=True)
    username = models.CharField(max_length=25, verbose_name='用户名')
    password = models.CharField(max_length=25, verbose_name='密码')
    nickname = models.CharField(max_length=25, verbose_name='昵称')
    introduction = models.CharField(max_length=50, verbose_name='简介')


class image(models.Model):
    image_content = models.IntegerField(primary_key=True)


class comment(models.Model):
    comment_username = models.CharField(max_length=250)
    comment_content = models.CharField(default="", max_length=250)


class pyq(models.Model):
    # avatar = models.IntegerField(verbose_name='头像')
    tag = models.CharField(verbose_name='tag', max_length=25)
    username = models.CharField(max_length=25, verbose_name='用户名')
    userID = models.IntegerField(default=0)
    ID = models.AutoField(primary_key=True)
    posttime = models.DateTimeField(auto_now_add=True, verbose_name='发表时间')
    title = models.CharField(max_length=25, verbose_name='标题')
    content = models.CharField(max_length=500, verbose_name='内容')
    location = models.CharField(max_length=250, verbose_name='地点')
    image_contain = models.ManyToManyField(image)
    like_account_contain = models.ManyToManyField(account)
    comment_contain = models.ManyToManyField(comment)


class message(models.Model):
    msg_content = models.CharField(max_length=500, verbose_name='消息内容')
    msg_ID = models.IntegerField(verbose_name='消息序号')
    msg_time = models.DateTimeField(auto_now_add=True, verbose_name='发表时间')
    is_send = models.BooleanField(default=True)


class chat(models.Model):
    ID = models.AutoField(primary_key=True)
    msg_contain = models.ManyToManyField(message)
    from_id = models.IntegerField()
    oppo_id = models.IntegerField(verbose_name='对面ID')
    msg_cnt = models.IntegerField(verbose_name='消息数量')


class chats(models.Model):
    user_ID = models.AutoField(primary_key=True)
    chat_contain = models.ManyToManyField(chat)
