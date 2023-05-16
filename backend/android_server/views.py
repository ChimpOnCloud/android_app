from django.core.exceptions import ObjectDoesNotExist
from django.shortcuts import render
from django.http import HttpResponse
from android_server.models import account
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
                username=user_data['username'], password=user_data['password'])
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
                return HttpResponse('ok')  # successfully logined
            else:
                return HttpResponse('wrong password')
    elif request.method == 'GET':
        return HttpResponse('GET')
