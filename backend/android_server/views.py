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
    print(avatar_relative_path)
    media_index = avatar_relative_path.rfind('media/') + len('media/')
    avatar_path = os.path.join(
        settings.MEDIA_ROOT, avatar_relative_path[media_index:])

    # 检查文件是否存在
    if not os.path.exists(avatar_path):
        return HttpResponse('Avatar not found', status=404)

    # 返回头像文件
    return FileResponse(open(avatar_path, 'rb'), content_type='')


def search_user(request):
    if request.method == 'POST':
        user_data = json.loads(request.body)
        potential_users = account.objects.filter(
            username__icontains=user_data['targetName'])
        if not potential_users:
            return HttpResponse('notfound')
        else:
            return_dict = {}
            for i, user in enumerate(potential_users):
                return_dict['ID' + str(i)] = user.ID
                return_dict['username' + str(i)] = user.username
                return_dict['password' + str(i)] = user.password
                return_dict['nickname' + str(i)] = user.nickname
                return_dict['introduction' + str(i)] = user.introduction
            print(return_dict)
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


def get_followuser(request):
    if request.method == 'POST':
        user_data = json.loads(request.body)
        dst_user = account.objects.filter(
            username=user_data['dstusername'])
        dst_user_dict = dst_user.first().__dict__
        src_user = account.objects.filter(
            username=user_data['srcusername'])
        src_user_dict = src_user.first().__dict__
        if not followperson.objects.filter(followedpersonID=dst_user_dict["ID"], followerID=src_user_dict['ID']):
            return HttpResponse('ok')
        else:
            print(dst_user_dict['username'])
            return HttpResponse('followed')


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


def post_publish(request):
    if request.method == 'POST':
        post_data = json.loads(request.body)
        post_username = post_data['username']
        post_user_dict = account.objects.filter(
            username=post_username).first().__dict__
        pyq.objects.create(title=post_data['titleString'], content=post_data['contentString'],
                           tag=post_data['tagString'], location=post_data['locationString'],
                           username=post_user_dict['username'], userID=post_user_dict['ID'])
        return HttpResponse('ok')


def get_all_posts(request):
    if request.method == 'POST':
        # for i, obj in enumerate(comment.objects.all()):
        #     obj.delete()
        # return HttpResponse('ok')
        post_name_dict = {}
        post_name_dict['#默认话题'] = 0
        post_name_dict['#校园资讯'] = 1
        post_name_dict['#二手交易'] = 2
        post_name_dict['#思绪随笔'] = 3
        post_name_dict['#吐槽盘点'] = 4
        return_dict = {}
        posts = pyq.objects.all()
        for i, post in enumerate(posts):
            userID = post.__dict__['userID']
            username = account.objects.filter(
                ID=userID).first().__dict__['username']
            return_dict['tag' +
                        str(i)] = str(post_name_dict[post.__dict__['tag']])
            return_dict['title' + str(i)] = post.__dict__['title']
            return_dict['content' + str(i)] = post.__dict__['content']
            return_dict['posttime' + str(i)] = str(post.__dict__['posttime'])
            return_dict['username' + str(i)] = username
            return_dict['location' + str(i)] = post.__dict__['location']
            return_dict['id' + str(i)] = post.__dict__['ID']
            return_dict['like_number' +
                        str(i)] = len(post.like_account_contain.all())
            return_dict['shoucang_number' +
                        str(i)] = len(post.shoucang_account_contain.all())
            return_dict['comment_number' +
                        str(i)] = len(post.comment_contain.all())
            for j, m_comment in enumerate(post.comment_contain.all()):
                # print(m_comment.__dict__)
                m_userid = m_comment.__dict__['comment_userid']
                m_username = account.objects.filter(
                    ID=m_userid).first().__dict__['username']
                return_dict['commentcontent' +
                            str(i) + 'number' + str(j)] = m_comment.__dict__['comment_content']
                return_dict['commentusername' +
                            str(i) + 'number' + str(j)] = m_username
        return_dict['num'] = len(posts)

    return HttpResponse(json.dumps(return_dict))


def get_liked_posts(request):
    if request.method == 'POST':
        post_name_dict = {}
        post_name_dict['#默认话题'] = 0
        post_name_dict['#校园资讯'] = 1
        post_name_dict['#二手交易'] = 2
        post_name_dict['#思绪随笔'] = 3
        post_name_dict['#吐槽盘点'] = 4
        return_dict = {}

        user_data = json.loads(request.body)
        username = user_data['username']
        user = account.objects.filter(username=username).first()
        user_id = user.__dict__['ID']
        m_posts = pyq.objects.all()
        posts = []
        for post in m_posts:
            # print(post.shoucang_account_contain.all())
            if user in post.shoucang_account_contain.all():
                posts.append(post)
        for i, post in enumerate(posts):
            userID = post.__dict__['userID']
            username = account.objects.filter(
                ID=userID).first().__dict__['username']
            return_dict['tag' +
                        str(i)] = str(post_name_dict[post.__dict__['tag']])
            return_dict['title' + str(i)] = post.__dict__['title']
            return_dict['content' + str(i)] = post.__dict__['content']
            return_dict['posttime' + str(i)] = str(post.__dict__['posttime'])
            return_dict['username' + str(i)] = username
            return_dict['location' + str(i)] = post.__dict__['location']
            return_dict['id' + str(i)] = post.__dict__['ID']
            return_dict['like_number' +
                        str(i)] = len(post.like_account_contain.all())
            return_dict['shoucang_number' +
                        str(i)] = len(post.shoucang_account_contain.all())
            return_dict['comment_number' +
                        str(i)] = len(post.comment_contain.all())
            for j, m_comment in enumerate(post.comment_contain.all()):
                # print(m_comment.__dict__)
                m_userid = m_comment.__dict__['comment_userid']
                m_username = account.objects.filter(
                    ID=m_userid).first().__dict__['username']
                return_dict['commentcontent' +
                            str(i) + 'number' + str(j)] = m_comment.__dict__['comment_content']
                return_dict['commentusername' +
                            str(i) + 'number' + str(j)] = m_username
        return_dict['num'] = len(posts)

    return HttpResponse(json.dumps(return_dict))


def get_all_posts_with_constraints(request):
    if request.method == 'POST':
        post_data = json.loads(request.body)
        onlyCheckSubscribed = post_data['onlyCheckSubscribed']
        tag = post_data['tag']
        post_name_dict = {}
        post_name_dict['#默认话题'] = 0
        post_name_dict['#校园资讯'] = 1
        post_name_dict['#二手交易'] = 2
        post_name_dict['#思绪随笔'] = 3
        post_name_dict['#吐槽盘点'] = 4
        return_dict = {}
        posts = pyq.objects.all()
        cnt = 0
        # print(post_name_dict[tag])
        for i, post in enumerate(posts):
            if onlyCheckSubscribed == "true":
                srcusername = post_data['srcUsername']
                dstusername = post.__dict__['username']
                srcID = account.objects.filter(
                    username=srcusername).first().__dict__['ID']
                dstID = account.objects.filter(
                    username=dstusername).first().__dict__['ID']
                potential_follow = followperson.objects.filter(
                    followerID=srcID, followedpersonID=dstID)
                if not potential_follow:
                    continue
            if str(tag) != '不限':
                if post_name_dict[post.__dict__['tag']] != post_name_dict[str(tag)]:
                    continue
            return_dict['tag' +
                        str(cnt)] = str(post_name_dict[post.__dict__['tag']])
            return_dict['title' + str(cnt)] = post.__dict__['title']
            return_dict['content' + str(cnt)] = post.__dict__['content']
            return_dict['posttime' + str(cnt)] = str(post.__dict__['posttime'])
            return_dict['username' + str(cnt)] = post.__dict__['username']
            return_dict['location' + str(cnt)] = post.__dict__['location']
            return_dict['id' + str(i)] = post.__dict__['ID']
            return_dict['like_number' +
                        str(i)] = len(post.like_account_contain.all())
            cnt = cnt + 1
    return HttpResponse(json.dumps(return_dict))


def get_searched_pyq(request):
    if request.method == 'POST':
        post_name_dict = {}
        post_name_dict['#默认话题'] = 0
        post_name_dict['#校园资讯'] = 1
        post_name_dict['#二手交易'] = 2
        post_name_dict['#思绪随笔'] = 3
        post_name_dict['#吐槽盘点'] = 4
        post_data = json.loads(request.body)
        target_name = post_data['targetName']
        target_kind = post_data['targetKind']
        print(target_name)
        return_dict = {}
        # 模糊搜索
        if target_kind == 'title':
            return_posts = pyq.objects.filter(title__icontains=target_name)
        elif target_kind == 'content':
            return_posts = pyq.objects.filter(content__icontains=target_name)
        elif target_kind == 'username':
            return_posts = pyq.objects.filter(username__icontains=target_name)
        elif target_kind == 'tag':
            return_posts = pyq.objects.filter(tag__icontains=target_name)
        for i, post in enumerate(return_posts):
            userID = post.__dict__['userID']
            username = account.objects.filter(
                ID=userID).first().__dict__['username']
            return_dict['tag' +
                        str(i)] = str(post_name_dict[post.__dict__['tag']])
            return_dict['title' + str(i)] = post.__dict__['title']
            return_dict['content' + str(i)] = post.__dict__['content']
            return_dict['posttime' + str(i)] = str(post.__dict__['posttime'])
            return_dict['username' + str(i)] = username
            return_dict['location' + str(i)] = post.__dict__['location']
            return_dict['id' + str(i)] = post.__dict__['ID']
            return_dict['like_number' +
                        str(i)] = len(post.like_account_contain.all())
            return_dict['shoucang_number' +
                        str(i)] = len(post.shoucang_account_contain.all())
            return_dict['comment_number' +
                        str(i)] = len(post.comment_contain.all())
            for j, m_comment in enumerate(post.comment_contain.all()):
                # print(m_comment.__dict__)
                return_dict['commentcontent' +
                            str(i) + 'number' + str(j)] = m_comment.__dict__['comment_content']
                return_dict['commentusername' +
                            str(i) + 'number' + str(j)] = m_comment.__dict__['comment_username']
        return_dict['num'] = len(return_posts)
        if len(return_dict) == 0:
            return HttpResponse('notfound')
        return HttpResponse(json.dumps(return_dict))


def handle_like(request):
    if request.method == 'POST':
        pyq_data = json.loads(request.body)
        pyq_id = pyq_data['pyqID']
        username = pyq_data['username']
        like_user = account.objects.filter(username=username).first()
        m_pyq = pyq.objects.filter(ID=pyq_id).first()
        if like_user in m_pyq.like_account_contain.all():  # 取消赞
            m_pyq.like_account_contain.remove(like_user)
            return HttpResponse('cancellike')
        else:
            m_pyq.like_account_contain.add(like_user)
            return HttpResponse('addlike')


def handle_shoucang(request):
    if request.method == 'POST':
        pyq_data = json.loads(request.body)
        pyq_id = pyq_data['pyqID']
        username = pyq_data['username']
        like_user = account.objects.filter(username=username).first()
        m_pyq = pyq.objects.filter(ID=pyq_id).first()
        if like_user in m_pyq.shoucang_account_contain.all():  # 取消赞
            m_pyq.shoucang_account_contain.remove(like_user)
            return HttpResponse('cancelshoucang')
        else:
            m_pyq.shoucang_account_contain.add(like_user)
            return HttpResponse('addshoucang')


def handle_comment(request):
    if request.method == 'POST':
        pyq_data = json.loads(request.body)
        pyq_id = pyq_data['pyqID']
        username = pyq_data['username']
        like_user = account.objects.filter(username=username).first()
        m_pyq = pyq.objects.filter(ID=pyq_id).first()
        m_comment = pyq_data['commentString']
        m_userid = account.objects.filter(
            username=username).first().__dict__['ID']
        new_comment = comment.objects.create(comment_username=username,
                                             comment_content=m_comment,
                                             comment_userid=m_userid)
        m_pyq.comment_contain.add(new_comment)
        return HttpResponse('ok')


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


def get_all_messages(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        src_user_dict = account.objects.filter(
            username=data['srcUsername']).first().__dict__
        dst_user_dict = account.objects.filter(
            username=data['dstUsername']).first().__dict__
        target_chat = chat.objects.filter(
            from_id=src_user_dict['ID'], oppo_id=dst_user_dict['ID']).first()
        messages = target_chat.msg_contain.all()
        return_dict = {}
        for i, msg in enumerate(messages):
            msg_dict = msg.__dict__
            return_dict['msg' + str(i)] = msg_dict['msg_content']
            return_dict['is_send' + str(i)] = msg_dict['is_send']
        return HttpResponse(json.dumps(return_dict))


def get_user_posts(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        post_name_dict = {}
        post_name_dict['#默认话题'] = 0
        post_name_dict['#校园资讯'] = 1
        post_name_dict['#二手交易'] = 2
        post_name_dict['#思绪随笔'] = 3
        post_name_dict['#吐槽盘点'] = 4
        username = data['username']
        ID = account.objects.filter(username=username).first().__dict__['ID']
        posts = pyq.objects.filter(ID=ID)
        return_dict = {}
        for i, post in enumerate(posts):
            userID = post.__dict__['userID']
            username = account.objects.filter(
                ID=userID).first().__dict__['username']
            return_dict['tag' +
                        str(i)] = str(post_name_dict[post.__dict__['tag']])
            return_dict['title' + str(i)] = post.__dict__['title']
            return_dict['content' + str(i)] = post.__dict__['content']
            return_dict['posttime' + str(i)] = str(post.__dict__['posttime'])
            return_dict['username' + str(i)] = username
            return_dict['location' + str(i)] = post.__dict__['location']
            return_dict['id' + str(i)] = post.__dict__['ID']
            return_dict['like_number' +
                        str(i)] = len(post.like_account_contain.all())
            return_dict['shoucang_number' +
                        str(i)] = len(post.shoucang_account_contain.all())
            return_dict['comment_number' +
                        str(i)] = len(post.comment_contain.all())
            for j, m_comment in enumerate(post.comment_contain.all()):
                # print(m_comment.__dict__)
                return_dict['commentcontent' +
                            str(i) + 'number' + str(j)] = m_comment.__dict__['comment_content']
                return_dict['commentusername' +
                            str(i) + 'number' + str(j)] = m_comment.__dict__['comment_username']
            return_dict['num'] = len(posts)
        if not posts:
            return HttpResponse('error')
        return HttpResponse(json.dumps(return_dict))


def get_author(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        username = data['username']
        user_dict = account.objects.filter(username=username).first().__dict__
        return HttpResponse(json.dumps(user_dict))


def get_certain_post(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        username = data['username']
        post_ID = data['postID']
        user_ID = account.objects.filter(
            username=username).first().__dict__['ID']
        post = pyq.objects.filter(ID=post_ID).first()
        post_like_relation = post.like_account_contain.filter(ID=user_ID)
        post_shoucang_relation = post.shoucang_account_contain.filter(
            ID=user_ID)
        return_dict = {}
        if post_like_relation:
            return_dict['thumbsup'] = 'yes'
        else:
            return_dict['thumbsup'] = 'no'
        if post_shoucang_relation:
            return_dict['like'] = 'yes'
        else:
            return_dict['like'] = 'no'
        print(return_dict)
        return HttpResponse(json.dumps(return_dict))


# def is_follow(request):
#     if request.method == 'POST':
#         json_data = json.loads(request.body)
#         dst_username = json_data['dstUsername']
#         src_username = json_data['srcUsername']
#         dst_ID = account.objects.filter(
#             username=dst_username).first().__dict__['ID']
#         src_ID = account.objects.filter(
#             username=src_username).first().__dict__['ID']
#         potential_follow = followperson.objects.filter(
#             followerID=src_ID, followedpersonID=dst_ID)
#         print(dst_ID, src_ID)
#         if not potential_follow:
#             return HttpResponse('notfollowed')
#         else:
#             return HttpResponse('followed')


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
