# Generated by Django 4.0.4 on 2023-06-18 08:08

from django.db import migrations, models
import django.utils.timezone


class Migration(migrations.Migration):

    dependencies = [
        ('android_server', '0032_block'),
    ]

    operations = [
        migrations.AddField(
            model_name='block',
            name='id',
            field=models.BigAutoField(auto_created=True, default=django.utils.timezone.now, primary_key=True, serialize=False, verbose_name='ID'),
            preserve_default=False,
        ),
        migrations.AlterField(
            model_name='block',
            name='user_ID',
            field=models.IntegerField(default=0),
        ),
    ]
