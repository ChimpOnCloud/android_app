from django.core.exceptions import ObjectDoesNotExist
from django.shortcuts import render
from django.http import HttpResponse
from android_server.models import *
import json
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

        potential_chat = chat.objects.filter(
            from_id=from_user_dict['ID'], oppo_id=to_user_dict['ID'])
        chat_msg_cnt = 0
        if not potential_chat:
            potential_chat = chat.objects.create(
                msg_cnt=0, from_id=from_user_dict['ID'], oppo_id=to_user_dict['ID'])
        else:
            chat_msg_cnt = potential_chat.first().__dict__['msg_cnt']

        new_msg = message.objects.create(
            msg_content=msg_json['msgContent'], msg_ID=chat_msg_cnt)

        # for i, obj in enumerate(chat.objects.all()):
        #     obj.delete()
        # for i, obj in enumerate(message.objects.all()):
        #     obj.delete()
        potential_chat = chat.objects.filter(
            from_id=from_user_dict['ID'], oppo_id=to_user_dict['ID'])
        potential_chat.update(msg_cnt=chat_msg_cnt + 1)
        print(new_msg.__dict__)
        return HttpResponse('success')
