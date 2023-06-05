from django.core.exceptions import ObjectDoesNotExist
from django.shortcuts import render
from django.http import HttpResponse, FileResponse
from android_server.models import *
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
        potential_new_user = account.objects.filter(
            username=full_user_data['newUsername'])
        if not potential_user:
            return HttpResponse('not registered yet!')
        elif full_user_data['newUsername'] == full_user_data['oldUsername']:
            account.objects.filter(username=full_user_data['oldUsername']).update(
                username=full_user_data['newUsername'], password=full_user_data['newPassword'], nickname=full_user_data['newNickname'], introduction=full_user_data['newIntroduction'])
            return HttpResponse('ok')
        elif potential_new_user and full_user_data['newPassword'] == full_user_data['oldPassword'] and full_user_data['newNickname'] == full_user_data['oldNickname'] and full_user_data['newIntroduction'] == full_user_data['oldIntroduction']:
            return HttpResponse('repeated username!')
        else:
            account.objects.filter(username=full_user_data['oldUsername']).update(
                username=full_user_data['newUsername'], password=full_user_data['newPassword'], nickname=full_user_data['newNickname'], introduction=full_user_data['newIntroduction'])
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
    avatar_path = os.path.join(
        settings.MEDIA_ROOT, avatar_relative_path[media_index:])
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
            return HttpResponse(json.dumps(return_dict))


def show_subscribelist(request):
    if request.method == 'POST':
        user_data = json.loads(request.body)
        potential_user = account.objects.filter(
            username=user_data['srcUsername'])  # a list contains at most 1 element
        if not potential_user:
            return HttpResponse('error')

        else:
            follow_relations = followperson.objects.filter(
                followerID=potential_user.first().__dict__['ID'])
            return_dict = {}
            for i, follow_relation in enumerate(follow_relations):
                followedpersonID = follow_relation.__dict__[
                    'followedpersonID']
                followedpersonUsername = account.objects.filter(
                    ID=followedpersonID).first().__dict__['username']
                return_dict[i] = followedpersonUsername
            return HttpResponse(json.dumps(return_dict))


def handle_followuser(request):
    if request.method == 'POST':
        # for i, obj in enumerate(followperson.objects.all()):
        #     obj.delete()
        # return HttpResponse('hellop')
        user_data = json.loads(request.body)

        follow_relation_cnt = len(followperson.objects.all())

        dst_user = account.objects.filter(
            username=user_data['dstusername'])
        dst_user_dict = dst_user.first().__dict__
        src_user = account.objects.filter(
            username=user_data['srcusername'])
        src_user_dict = src_user.first().__dict__

        potential_follow = followperson.objects.filter(
            followerID=src_user_dict['ID'], followedpersonID=dst_user_dict['ID'])
        if potential_follow:  # already followed
            return HttpResponse('followed')

        followperson.objects.create(
            followerID=src_user_dict['ID'], followedpersonID=dst_user_dict['ID'], ID=follow_relation_cnt)

        # 如果chat没有这两个人的聊天框，创建一个
        # chat永远成对出现，苦难与鲜花同时盛开
        potential_chat = chat.objects.filter(
            from_id=dst_user_dict['ID'], oppo_id=src_user_dict['ID'])
        if not potential_chat:
            chat.objects.create(
                from_id=dst_user_dict['ID'], oppo_id=src_user_dict['ID'], msg_cnt=0)
            chat.objects.create(
                oppo_id=dst_user_dict['ID'], from_id=src_user_dict['ID'], msg_cnt=0)
        return HttpResponse('ok')


def handle_unfollowuser(request):
    if request.method == 'POST':
        user_data = json.loads(request.body)

        dst_user = account.objects.filter(
            username=user_data['dstusername'])
        dst_user_dict = dst_user.first().__dict__
        src_user = account.objects.filter(
            username=user_data['srcusername'])
        src_user_dict = src_user.first().__dict__

        potential_follow = followperson.objects.filter(
            followerID=src_user_dict['ID'], followedpersonID=dst_user_dict['ID'])
        if not potential_follow:  # already unfollowed
            return HttpResponse('unfollowed')

        followperson.objects.get(
            followerID=src_user_dict['ID'], followedpersonID=dst_user_dict['ID']).delete()
        for i, obj in enumerate(followperson.objects.all()):
            followperson.objects.filter(ID=obj.__dict__['ID']).update(ID=i)
        return HttpResponse('ok')


# def get_chat_users(request):
#     if request.method == 'POST':
#         user_data = json.loads(request.body)
#         potential_user = account.objects.filter(
#             username=user_data['srcUsername'])  # a list contains at most 1 element
#         if not potential_user:
#             return HttpResponse('error')


def add_message_to_chat(request):
    if request.method == 'POST':
        msg_json = json.loads(request.body)
        from_user = account.objects.filter(
            username=msg_json['fromUser'])
        from_user_dict = from_user.first().__dict__
        # if not existing such a chat, create one
        to_user = account.objects.filter(username=msg_json['toUser'])
        to_user_dict = to_user.first().__dict__
        # print(from_user_dict)
        # print(to_user_dict)
        potential_chat_list = chats.objects.filter(
            user_ID=from_user_dict['ID'])
        if not potential_chat_list:
            potential_chat_list = chats.objects.create(
                user_ID=from_user_dict['ID'])

        # chat永远是成对出现的
        potential_chat = chat.objects.filter(
            from_id=from_user_dict['ID'], oppo_id=to_user_dict['ID'])
        chat_msg_cnt = 0
        if not potential_chat:
            potential_chat = chat.objects.create(
                msg_cnt=0, from_id=from_user_dict['ID'], oppo_id=to_user_dict['ID'])
            chat.objects.create(
                msg_cnt=0, oppo_id=from_user_dict['ID'], from_id=to_user_dict['ID'])
        else:
            chat_msg_cnt = potential_chat.first().__dict__['msg_cnt']

        # 处理新消息
        # send和rcv各一个新消息
        new_msg_send = message.objects.create(
            msg_content=msg_json['msgContent'], msg_ID=chat_msg_cnt, is_send=True)
        new_msg_rcv = message.objects.create(
            msg_content=msg_json['msgContent'], msg_ID=chat_msg_cnt, is_send=False)

        # 两个消息所对应的聊天cnt加一
        potential_send_chat = chat.objects.filter(
            from_id=from_user_dict['ID'], oppo_id=to_user_dict['ID'])
        potential_send_chat.update(msg_cnt=chat_msg_cnt + 1)
        potential_rcv_chat = chat.objects.filter(
            oppo_id=from_user_dict['ID'], from_id=to_user_dict['ID'])
        potential_rcv_chat.update(msg_cnt=chat_msg_cnt + 1)
        # 创建消息和聊天的关系
        send_chat = chat.objects.get(
            from_id=from_user_dict['ID'], oppo_id=to_user_dict['ID'])
        send_chat.msg_contain.add(new_msg_send)
        rcv_chat = chat.objects.get(
            oppo_id=from_user_dict['ID'], from_id=to_user_dict['ID'])
        rcv_chat.msg_contain.add(new_msg_rcv)

        # chats暂时弃用
        # potential_chat_list = potential_chat_list.first()
        # potential_chat_list.chat_contain.add(m_chat)
        # print(potential_chat_list)
        # for i, obj in enumerate(chat.objects.all()):
        #     obj.delete()
        # for i, obj in enumerate(message.objects.all()):
        #     obj.delete()
        return HttpResponse('success')


def find_related_chat_users(request):
    if request.method == 'POST':
        # first we find subscribers and create certain chats.
        user_data = json.loads(request.body)
        potential_user = account.objects.get(
            username=user_data['curUsername'])
        if not potential_user:
            return HttpResponse('error')

        else:
            # 关注的时候就已经创建了聊天框！！！关注关系足够找到所有需要的聊天框
            # 找到所有curUser关注的人
            follow_relations = followperson.objects.filter(
                followerID=potential_user.__dict__['ID'])
            return_dict = {}
            potential_chats = chats.objects.filter(
                user_ID=potential_user.__dict__['ID'])
            if not potential_chats:
                chats.objects.create(user_ID=potential_user.__dict__['ID'])
            # 对每一个关注的人
            for i, follow_relation in enumerate(follow_relations):
                # 被关注者的ID
                followedpersonID = follow_relation.__dict__[
                    'followedpersonID']
                # 通过ID找到这个人的username
                followedpersonUsername = account.objects.filter(
                    ID=followedpersonID).first().__dict__['username']
                return_dict[i] = followedpersonUsername
            # 还要找已经关注了他的人
            follow_relations = followperson.objects.filter(
                followedpersonID=potential_user.__dict__['ID'])
            for i, follow_relation in enumerate(follow_relations):
                followerID = follow_relation.__dict__[
                    'followerID']
                followerUsername = account.objects.filter(
                    ID=followerID).first().__dict__['username']
                return_dict[i] = followerUsername
            # for i, obj in enumerate(chat.objects.all()):
            #     obj.delete()
            # for i, obj in enumerate(message.objects.all()):
            #     obj.delete()
            # for i, obj in enumerate(chats.objects.all()):
            #     obj.delete()
            # for i, obj in enumerate(followperson.objects.all()):
            #     obj.delete()
            # return HttpResponse('error')
            return HttpResponse(json.dumps(return_dict))


def get_related_messages(request):
    if request.method == 'POST':
        user_data = json.loads(request.body)
        # TODO: return related messages
        src_user_dict = account.objects.filter(
            username=user_data['srcUsername']).first().__dict__
        dst_user_dict = account.objects.filter(
            username=user_data['dstUsername']).first().__dict__

        target_chat = chat.objects.get(
            from_id=src_user_dict['ID'], oppo_id=dst_user_dict['ID'])
        return_dict = {}
        for i, msg in enumerate(target_chat.msg_contain.all()):
            msg_dict = msg.__dict__
            return_dict['msg' + str(i)] = msg_dict['msg_content']
            return_dict['is_send' + str(i)] = msg_dict['is_send']
        return HttpResponse(json.dumps(return_dict))


def deleteall():
    for i, obj in enumerate(chat.objects.all()):
        obj.delete()
    for i, obj in enumerate(message.objects.all()):
        obj.delete()
    for i, obj in enumerate(chats.objects.all()):
        obj.delete()
    for i, obj in enumerate(followperson.objects.all()):
        obj.delete()
    print('delete!')
