# Generated by Django 4.0.4 on 2023-06-05 08:19

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('android_server', '0012_alter_chat_msg_contain_delete_chat_msg_contain'),
    ]

    operations = [
        migrations.AddField(
            model_name='message',
            name='is_send',
            field=models.BooleanField(default=True),
        ),
    ]
