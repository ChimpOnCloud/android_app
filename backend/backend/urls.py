"""backend URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from android_server import views
from django.conf import settings
from django.conf.urls.static import static

urlpatterns = [
    path('admin/', admin.site.urls),
    path('register/', views.register_view),
    path('login/',  views.login_view),
    path('changeUserinfo/', views.change_userinfo),
    path('uploadAvatar/', views.upload_avatar),
    path('getAvatar/<str:targetName>/', views.get_avatar),
    path('searchUser/', views.search_user),
    path('handleFollowuser/', views.handle_followuser),
    path('handleunFollowuser/', views.handle_unfollowuser),
    path('showSubscribedlist/', views.show_subscribelist),
    path('addMessageToChat/', views.add_message_to_chat),
    path('findRelatedChatUsers/', views.find_related_chat_users),
    path('getRelatedMessages/', views.get_related_messages),
    path('postPublish/', views.post_publish),
    path('getAllPosts/', views.get_all_posts),
    path('getSearchedPyq/', views.get_searched_pyq),
    path('getPostsWithConstraints/', views.get_all_posts_with_constraints),
    path('handleLike/', views.handle_like),
    path('getFollowuser/', views.get_followuser),
    path('getAllMessages/', views.get_all_messages),
    path('getUserPosts/', views.get_user_posts),
    path('getAuthor/', views.get_author),
    path('getCertainPost/', views.get_certain_post),
    path('handleShoucang/', views.handle_shoucang),
    path('handleComment/', views.handle_comment),
    path('getLikedPosts/', views.get_liked_posts),
    path('showFollowers/', views.show_followers),
    path('uploadImages/', views.upload_images),
    path('clearTmp/', views.clear_tmp),
    # path('isFollow/', views.is_follow),
]

urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
