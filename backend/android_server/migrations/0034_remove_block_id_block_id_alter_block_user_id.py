# Generated by Django 4.0.4 on 2023-06-18 08:09

from django.db import migrations, models
import django.utils.timezone


class Migration(migrations.Migration):

    dependencies = [
        ('android_server', '0033_block_id_alter_block_user_id'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='block',
            name='id',
        ),
        migrations.AddField(
            model_name='block',
            name='ID',
            field=models.AutoField(default=django.utils.timezone.now, primary_key=True, serialize=False),
            preserve_default=False,
        ),
        migrations.AlterField(
            model_name='block',
            name='user_ID',
            field=models.IntegerField(),
        ),
    ]
