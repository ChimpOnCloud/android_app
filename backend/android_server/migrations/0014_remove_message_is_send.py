# Generated by Django 4.0.4 on 2023-06-05 08:23

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('android_server', '0013_message_is_send'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='message',
            name='is_send',
        ),
    ]
