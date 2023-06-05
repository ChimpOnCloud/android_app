# Generated by Django 4.0.4 on 2023-06-01 18:00

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('android_server', '0006_account_avatar'),
    ]

    operations = [
        migrations.CreateModel(
            name='followperson',
            fields=[
                ('ID', models.AutoField(default=0, primary_key=True, serialize=False)),
                ('followerID', models.IntegerField()),
                ('followedpersonID', models.IntegerField()),
            ],
        ),
        migrations.RemoveField(
            model_name='chats',
            name='chat_contain',
        ),
        migrations.RemoveField(
            model_name='pyq',
            name='image_contain',
        ),
        migrations.DeleteModel(
            name='chat',
        ),
        migrations.DeleteModel(
            name='chats',
        ),
        migrations.DeleteModel(
            name='image',
        ),
        migrations.DeleteModel(
            name='message',
        ),
        migrations.DeleteModel(
            name='pyq',
        ),
    ]