# Generated by Django 4.0.4 on 2023-06-09 13:44

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('android_server', '0025_alter_pyq_userid'),
    ]

    operations = [
        migrations.AlterField(
            model_name='pyq',
            name='userID',
            field=models.IntegerField(default=0),
        ),
    ]
