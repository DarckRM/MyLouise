# MyLouise Project API应用

### 简介

基于 Java jdk11，Spring Boot，MySQL，VUE3 实现的轻量API应用，主要是配合<a url="https://github.com/Mrs4s/go-cqhttp">go-cqhttp</a>的接口标准实现QQ群聊机器人

Naive Saito是前端项目

### 快速部署

#### My Louise 部分

使用 IDEA IDE 以及 Maven 自动完成包管理，在  `application.yml` 完成数据库的配置，数据库文件为 `mylouise.sql`

#### Naive Saito 部分

```bash
npm install
npm run dev
```

或者

```bash
yarn install
yarn run dev
```

### 本地开发

#### MyLouise API部分

首先是准备开发环境，目前需要的运行环境有JDK11和MySQL8，在本地环境上安装好即可，在application.yml中配置相关的数据源以及数据源连接信息，这里推荐对配置文件进行加密。需要注意的是，这个项目是配合cqhttp一起使用的，是作为服务的API应用，因此还会有一些关于cqhttp的事项。

###### 在application.yml文件中

```yaml
server:
#服务器端口
    port: 

spring:
    #配置数据源
    datasource:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: 
        username: 
        password: 

#配置MybatisPlus
mybatis-plus:
    configuration:
        #关闭表字段下划线自动转驼峰导致实体类字段无法匹配
        map-underscore-to-camel-case: false
    mapper-locations: classpath*:xml
    global-config:
        db-config:
            #表名前缀
            table-prefix: t_
```

Spring-Boot的标准配置，要说明的是Mybatis-Plus，为了简化和数据库的交互使用了Mybatis-Plus，具体的使用方法见官方文档，这里说一下这几条配置的原因

```yaml
#关闭表字段下划线自动转驼峰导致实体类字段无法匹配
map-underscore-to-camel-case: false
```

假如你在数据库中的一张表内存在以下划线分割的字段，MybatisPlus会自动将查询结果字段以驼峰命名法去匹配你的实体类，如

> 字段名: user_createtime 匹配的实体类名: userCreatetime

```yaml
#表名前缀
table-prefix: t_
```

MybatisPlus有一个BaseMapper接口，你可以将你写的Mapper继承自这个接口，这个接口会根据你传入的实体类作为泛型实现很多自动化的CURD操作，但是自动拼装的SQL语句中默认是以实体类来命名的，如

> 实体类名: User 自动生成的SQL语句: SELECT * FROM user

加上此条配置即可在自动生成的表名前加上前缀。

当然，具体的实现细节还有个人偏好就需要各位自行查看官方文档了。

###### 在LouiseConfig.properties文件中

```yaml
#Louise运行时加载一些默认配置

#go-cqhttp监听地址
BASE_BOT_URL = cqhttp运行的URL以及端口号，在介绍cqhttp的部分会提到
#go-cqhttp上报密钥
HTTP_POST_KEY = 在介绍cqhttp的部分会提到

#Louise的一些报错信息
LOUISE.unknown_command = 没有听懂诶，如果需要帮助的话请说!help
LOUISE.thirdApi_request_failed = 露易兹请求第三方API失败了！
#SourceNAO API相关
SOURCENAO_API = https://saucenao.com/search.php
SOURCENAO_API_KEY = 修改为你的SOURCENAO_API
```

SourceNAO是实现以图搜图的一个第三方API，因此想要实现搜图功能的话这条配置必不可少，当然，你也可以去选择其它的公共API调用，具体方法可以参考源码。

此项相关的配置项需要你自己去SourceNAO网站获取，流程简单，只需要注册即可获取到API，具体的请求响应参数可以阅读官方API帮助或者参考我写的代码（丑

注册：https://saucenao.com/user.php

API Key：https://saucenao.com/user.php?page=search-api

#### GO-CQHTTP部分

**注意，请先注册一个QQ账号，配置文件中会用到**

*基于 [Mirai](https://github.com/mamoe/mirai) 以及 [MiraiGo](https://github.com/Mrs4s/MiraiGo) 的 [OneBot](https://github.com/howmanybots/onebot/blob/master/README.md) Golang 原生实现* 

项目地址 https://github.com/Mrs4s/go-cqhttp

官方文档 https://docs.go-cqhttp.org/

关于怎么部署这个项目官方文档写的非常清楚了，简单启动非常简单，我这里说一下配置文件和事件过滤器的相关事项。

###### 配置文件

其实官方提供的默认配置文件的注释说明了很多信息，但是有一些地方还是直观解释一下比较好。现在假设你已经跟着官方的快速开始完成bot的启动了，在bot的根目录下打开config.yml文件。

```yaml
account: # 账号相关
#这部分没啥好说的，官方config说明足够详细，就是encrypt最好保持false
```

```yaml
heartbeat:
  # disabled: false # 是否开启心跳事件上报
  # 心跳频率, 单位秒
  # -1 为关闭心跳
  interval: 5
#推荐可以延长一点心跳的间隔时间 10s - 15s 都可以，最好别关
```

```yaml
message:
  # 上报数据类型
  # 可选: string,array
  # 推荐选择string吧，API应用也可以直接解析成JSONObject对象（需要GSON依赖）（主要是我没试过array）
  post-format: string
  # 是否忽略无效的CQ码, 如果为假将原样发送
  # CQ码实现建议看文档关于CQ码的介绍，可以理解成一种将你的信息解析成QQ可以理解的格式，或者直接指定一些QQ   # 的功能，举个栗子，发送请求JSON参数{"reply":"[CQ:processImage, file="url"]"}就可以在QQ里发送这个URL指   # 定的图片了，如果发送图片失败则会直接显示CQ码原文
  ignore-invalid-cqcode: false
```

```yaml
# 默认中间件锚点
default-middlewares: &default
  # 访问密钥, 强烈推荐在公网的服务器设置
  access-token: ''
  # 事件过滤器文件目录
  # 编写默认上报信息的过滤器，满足条件的消息才会上报到API应用
  filter: ''
```

```yaml
servers:
  # HTTP 通信设置
  - http:
	  # 在API应用的application.yml中就需要填上这里的配置
      # 服务端监听地址
      host: 127.0.0.1
      # 服务端监听端口
      port: 5700
      # 反向HTTP超时时间, 单位秒
      # 最小值为5，小于5将会忽略本项设置
      # 可以适当调高，如果API应用调用第三方接口耗时较长可能会导致bot一直无法成功请求服务
      timeout: 5
      middlewares:
        <<: *default # 引用默认中间件
      # 反向HTTP POST地址列表
      # 这个就是说你的API应用监听的地址和端口，也就是bot接收到的消息或者是事件会上报给这个地方
      # secret是bot的上报请求会带上一些加密的请求头参数，API应用可以根据这个来判断请求是否来自bot
      # 具体算法见官方文档
      post:
      	url: '' # 地址
      	secret: ''           # 密钥
```

###### 过滤器

官方也有文档介绍过滤器如何配置，还有具体规则，这里还是举例说明

> bot的目录结构 这是我自己的根据自己偏好更改
>
> ```text
> .
> ├── go-cqhttp
> ├── config.yml (旧版: config.hjson)
> ├── device.json
> ├── filter
> 	└── filters.json
> ├── logs
> │   └── xx-xx-xx.log
> └── data
>     ├── images
>     │   └── xxxx.processImage
>     └── db
> ```
>
> ```yaml
> # config.yml
> # 默认中间件锚点
> default-middlewares: &default
>   # 事件过滤器文件目录
>   # 编写默认上报信息的过滤器，满足条件的消息才会上报到API应用
>   filter: 'filter/filters.json'
> ```
>
> ```json
> // filter.json
> // 规则含义 允许上报以半角!开头或者meta_event_type为心跳检测的消息
> {
>     ".or" : 
>     [
>         {
>             "raw_message": {
>                 ".regex": "^!"
>             }
>         }, 
>         {
>             "meta_event_type": "heartbeat"
>         }
>     ]
>     
> }
> ```

## 开发进度

1. 监听群聊信息

2. 发送随机图片

#### 2021年8月6日14:51:39

返回结果的简单封装

#### 2021年8月7日22:04:49

1. 实现用户注册，未注册情况下不能调用功能
2. 返回简单的帮助信息图片
3. 初步接收上传文件上报的接口
4. 加入MySQL

#### 2022年4月16日22:20:25

目前存在的问题

- 日志输出格式
- WebSocket和前端通信存在问题
- 部分CUDR功能尚未完成
