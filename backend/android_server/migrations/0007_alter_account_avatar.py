# Generated by Django 4.1.4 on 2023-05-29 10:24

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('android_server', '0006_account_avatar'),
    ]

    operations = [
        migrations.AlterField(
            model_name='account',
            name='avatar',
            field=models.ImageField(blank=True, upload_to='avatar/%Y%m%d/'),
        ),
    ]
