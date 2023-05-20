## 前端文档  
主要是描述一下包含的类以及待实现部分的接口
### 类描述   
#### 1. 通用类  
以下类在各个前端页面均有可能用到：  
* user：用户类，包含用户的基本信息，如用户名、密码等，目前仿照后端中的定义添加了五个参数。  
* * 接口：除了get与set之外还有一个判等函数，判等的依据是两个user对象的用户名和ID均相同。  
* * 实例：user被用于其余对象的构造，在homepage中放置了一个静态公共变量User，表示当前登录的用户。  
#### 2. chat  
* message：消息类，包含消息的基本信息：发送者（user）、接收者（user）、消息内容（String）。
* * 接口：无特殊  
* * 实例：message被用于chat中的聊天记录。
* chat：聊天类，包含聊天的基本信息：聊天的对方（user）、聊天记录（message的Arraylist）。
* * 接口：一个insert接口允许插入一条message进入聊天记录。  
* * 实例：chat又被用于chatList中。  
* chatList：聊天列表类，包含聊天列表的基本信息：聊天列表（chat的Arraylist），count（int）。  
* * 接口：仍有一个insert接口，允许插入一个chat进入聊天列表。get函数允许按位置取出一个chat。  
* * 实例：activity_chat中的聊天列表由一个chatList表示，此处等待后端进行连接和数据传输。  
### 常量描述  
#### 1. String  
app/res/values/strings.xml中定义了一些常量，作为全局变量方便访问。  
* ipv4：服务器的IP地址，用于测试时的修改，建议连接时都使用该常量。port默认为8000，没有单独设置常量。  
* project_name：项目名称，用于在初始页面显示，可能与app_name的功能重复了，之后再考虑项目名称问题。  
