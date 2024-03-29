# 版本日志

## 2021年8月9日19:00:10 V1.0

目前已经具有的功能

```markdown
1. 请求LoliconAPI查询涩图
	1.1 根据结果匹配返回完整结果
2. 请求sourceNAO进行搜图
	2.1 匹配来自Pixiv的图以及信息
	2.2 匹配来自Danbooru的图以及信息
	2.3 匹配来自Gelbooru的图以及信息
	2.4 匹配来自Twitter的图以及信息
3. 用户相关
	3.1 实现用户注册机制
	3.2 更新用户的文件上传以及涩图请求记录
4. 帮助信息
	4.1 返回设置好的帮助图片
```

下个版本想要更新的内容

```markdown
1. 根据remid的网关实现常规的REST风格接口请求，减少API应用解析命令的性能消耗，属于比较重要的更新。网关已经实现了反向HTTP POST鉴权，安全性UP(已完成)
2. 实现从Gelbooru以及更多接口获得图片
3. 匹配更多的搜图来源信息（主要是目前几个已经实现的来源的优化，在此基础上再额外匹配两个吧）
~~4. 借助网关实现服务熔断措施，在API应用宕机或是更新部署时转到熔断服务进行处理~~
5. 初步完成记录群友上传文件以及其附加信息，还有更新用户数据(差不多吧)
6. 给用户信息添加更多的数据(差不多吧)
7. 添加额外的识图API（Ascii2d.net）(已完成)
8. 返回信息的模块化，统一到LouiseConfig.properties中(完成的差不多了)
9. 配置文件的加密，盐值
10. 搜图API的异步请求实现(已完成)
```

## 2022年4月18日08:38:40 V1.7

目前的系统情况

```markdown
1. 通过Websocket实现前端实时获取后端运行状态以及日志输出
2. 优化了前端的 主页 和 配置信息 页面的自适应布局
3. 修复了前端无法正确自适应窗口高度的问题
4. 修复了前端Interval函数无法正常停止，导致反复请求空Websocket抛出空对象异常的问题
5. 修复了后端Websocket连接池计数异常的问题
6. 修复了数据库UTF-8字符集无法解析emoji字符串，导致用户无法正常注册的问题
7. 新增了请求Gelbooru获取post页面前十张缩略图的功能
```

预计更新的内容

```markdown
1. 首页添加更多的实时数据图表
	.添加目前图片数据库总数，占用空间
	.目前用户数
2. 实现基于图片内容的图片检索功能
3. 实现多线程下载图片的功能
4. 实现图片的上传功能
5. 修复前端剩下的未实现的API
6. 优化日志的输出形式
7. 优化CQ-HTTP上报消息的处理方式
8. 上传系统的Banner图
```

## 2022年5月21日14:43:04

快要交毕设了，目前需要紧急完成的功能

```markdown
1. 图片库管理功能
2. 角色功能页面修复
3. 使用QQ进行图片搜索
```

## 2022年10月23日00:38:01

!command -> !command%

!command/param

!command [param]

!command/param [param]

## 2022年11月1日01:52:30

目前 MyLouise 小结，目前已经完成了很多最初对 MyLouise 框架设计的构想，例如 `插件功能`  `动态定时任务` `动态配置文件` 等，但是由于早些时间技术的不熟练，经验的缺乏以及粗糙的功能设计，使得现在 ML 系统存在很多潜在的问题，考虑到现在框架已经可以提供很好的新功能开发支持，因此接下来的开发的重心将会放在优化代码上

```markdown
1. 过滤器拦截器部分逻辑复杂，考虑重构
2. 对 go-cqhttp 部分的 API 支持较少，需要补充完整
3. 部分新增的框架支持功能还未做前端适配
4. 对于多线程的应用需要优化
5. 使用了过多的静态变量，以及包装类型，造成了大量的内存开销（特别是动态配置以及各种字符串的构造）
6. 需要统一网络请求的标准，根据环境使用统一的请求方式，最好是完成一个网络请求的封装
7. 需要绘制一张清晰的架构图来进行系统优化
8. 数据库设计混乱不堪，最好全部重新整理
9. 帮助文档最好是自动生成
10. 日志输出不统一
11. 异常处理不统一
```

