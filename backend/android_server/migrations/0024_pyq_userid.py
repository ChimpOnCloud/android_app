# Generated by Django 4.0.4 on 2023-06-09 13:42

from django.db import migrations, models
import django.utils.timezone


class Migration(migrations.Migration):

    dependencies = [
        ('android_server', '0023_comment_pyq_like_account_contain_pyq_comment_contain'),
    ]

    operations = [
        migrations.AddField(
            model_name='pyq',
            name='userID',
            field=models.IntegerField(default=django.utils.timezone.now),
            preserve_default=False,
        ),
    ]