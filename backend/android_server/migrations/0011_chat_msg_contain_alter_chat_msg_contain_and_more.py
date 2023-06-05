# Generated by Django 4.0.4 on 2023-06-04 09:16

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('android_server', '0010_chat_message_chats_chat_msg_contain'),
    ]

    operations = [
        migrations.CreateModel(
            name='chat_msg_contain',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
            ],
        ),
        migrations.AlterField(
            model_name='chat',
            name='msg_contain',
            field=models.ManyToManyField(through='android_server.chat_msg_contain', to='android_server.message'),
        ),
        migrations.AddField(
            model_name='chat_msg_contain',
            name='chat',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='android_server.chat'),
        ),
        migrations.AddField(
            model_name='chat_msg_contain',
            name='msg',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='android_server.message'),
        ),
    ]