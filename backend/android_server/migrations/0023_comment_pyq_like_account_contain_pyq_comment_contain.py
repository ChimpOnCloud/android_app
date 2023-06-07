# Generated by Django 4.0.4 on 2023-06-07 10:55

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('android_server', '0022_remove_pyq_avatar'),
    ]

    operations = [
        migrations.CreateModel(
            name='comment',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('comment_username', models.CharField(max_length=250)),
                ('comment_content', models.CharField(default='', max_length=250)),
            ],
        ),
        migrations.AddField(
            model_name='pyq',
            name='like_account_contain',
            field=models.ManyToManyField(to='android_server.account'),
        ),
        migrations.AddField(
            model_name='pyq',
            name='comment_contain',
            field=models.ManyToManyField(to='android_server.comment'),
        ),
    ]
