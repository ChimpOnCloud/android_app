from django.core.exceptions import ObjectDoesNotExist
from django.shortcuts import render
from django.http import HttpResponse, FileResponse
from android_server.models import account
from django.conf import settings
import json
import os

# Create your views here.


def register_view(request):
    if request.method == 'POST':
        user_data = json.loads(request.body)
        potential_user = account.objects.filter(username=user_data['username'])
        if potential_user:
            return HttpResponse('repeated!')
        else:
            account.objects.create(
                username=user_data['username'], password=user_data['password'], nickname="your nickname", introduction="you can edit your introduction here")
        return HttpResponse('succeeded')
    elif request.method == 'GET':
        return HttpResponse('GET')


def login_view(request):
    if request.method == 'POST':
        user_data = json.loads(request.body)  # from frontend
        potential_user = account.objects.filter(
            username=user_data['username'])  # a list contains at most 1 element
        if not potential_user:
            return HttpResponse('not registered yet!')
        else:
            user_dict = potential_user.first().__dict__  # from database
            if user_dict['password'] == user_data['password']:
                return_dict = {}
                return_dict['username'] = user_dict['username']
                return_dict['password'] = user_dict['password']
                return_dict['nickname'] = user_dict['nickname']
                return_dict['introduction'] = user_dict['introduction']
                return_dict['status'] = 'ok'
                # successfully logined
                return HttpResponse(json.dumps(return_dict))
            else:
                return HttpResponse('wrong password')
    elif request.method == 'GET':
        return HttpResponse('GET')


def change_userinfo(request):
    if request.method == 'POST':
        full_user_data = json.loads(request.body)  # from frontend
        potential_user = account.objects.filter(
            username=full_user_data['oldUsername'])  # a list contains at most 1 element
        if not potential_user:
            return HttpResponse('not registered yet!')
        else:
            target_user = account.objects.get(
                username=full_user_data['oldUsername'])
            target_user.username = full_user_data['newUsername']
            target_user.password = full_user_data['newPassword']
            target_user.nickname = full_user_data['newNickname']
            target_user.introduction = full_user_data['newIntroduction']
            target_user.save()
            return HttpResponse('ok')

def upload_avatar(request):
    avatar_file = request.FILES.get('image')
    oldusername = request.POST.get('oldUsername')
    target_user = account.objects.get(username=oldusername)

    file_path = "media/avatar/" + avatar_file.name
    with open(file_path, 'wb') as f:
        for chunk in avatar_file.chunks():
            f.write(chunk)

    target_user.avatar = file_path
    target_user.save()
    return HttpResponse('ok')

def get_avatar(request, targetName):
    # 根据用户ID获取用户对象
    try:
        target_user = account.objects.get(username=targetName)
    except account.DoesNotExist:
        return HttpResponse('User not found', status=404)

    # 获取头像文件路径
    avatar_relative_path = target_user.avatar.url
    media_index = avatar_relative_path.rfind('media/') + len('media/')
    avatar_path = os.path.join(settings.MEDIA_ROOT, avatar_relative_path[media_index:])
    # avatar_path = Path(target_user.avatar.path)

    # 打印目标用户名和头像路径
    print("Target username:", targetName)
    print("Avatar path:", avatar_path)
    
    # 检查文件是否存在
    if not os.path.exists(avatar_path):
        return HttpResponse('Avatar not found', status=404)

    # 返回头像文件
    return FileResponse(open(avatar_path, 'rb'), content_type='')

def search_user(request):
    if request.method == 'POST':
        user_data = json.loads(request.body)
        potential_user = account.objects.filter(
            username=user_data['targetName'])
        if not potential_user:
            return HttpResponse('notfound')
        else:
            target_user = account.objects.get(username=user_data['targetName'])
            return_dict = {}
            return_dict['ID'] = target_user.ID
            return_dict['username'] = target_user.username
            return_dict['password'] = target_user.password
            return_dict['nickname'] = target_user.nickname
            return_dict['introduction'] = target_user.introduction
            print(target_user.username)
            return HttpResponse(json.dumps(return_dict))
