from django.db import models

# Create your models here.


class account(models.Model):
    ID = models.AutoField(primary_key=True)
    username = models.CharField(max_length=25, verbose_name='用户名')
    password = models.CharField(max_length=25, verbose_name='密码')
    nickname = models.CharField(max_length=25, verbose_name='昵称')
    introduction = models.CharField(max_length=50, verbose_name='简介')
