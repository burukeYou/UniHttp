# UniHttp
-------
![travis](https://travis-ci.org/nRo/DataFrame.svg?branch=master)
[![License](http://img.shields.io/badge/license-apache%202-brightgreen.svg)](https://github.com/burukeYou/fast-retry/blob/main/LICENSE)


UniHttp 是一个声明式HTTP框架，帮助开发者快速完成第三方HTTP接口接入。通过简洁的定义，再配合各种高级功能特性即可实现各种复杂接口的接入，无需关注底层实现细节。

# 特征
- ✅ 声明式使用，标准化入口
- ✅ 配置为主，实现为辅
- ✅ 异步接口
- ✅ 异步重试
- ✅ SSL认证
- ✅ 超时配置
- ✅ 数据模型映射
- ✅ 多种数据格式（JSON/XML）
- ✅ 多生命周期处理（请求前/请求后/异常处理）




# 使用之前

如果没有使用UniHttp， 你的代码`可能`会是这样的

```java
@Component
public class UserInfoAPI {

    private final String url = "https://wwww.xxx.com/user-web";

    private UserInfo getUserInfo(String id){
       return HttpUtil.url(url)
                      .path("/list/info")
                      .param("id",id);
                      .get()
                      .parse(UserInfo.class);
    }
} 

```

# 使用之后

但如果使用 `UniHttp`后 你的代码会是这样的

```java
@HttpApi(uurl = "https://wwww.xxx.com/user-web")
public interface UserInfoAPI {

    @GetHttpInterface("/list/info")
    UserInfo getUserInfo(String id);
} 
```

甚至可能会是这样的, 里面简单的几个配置便实现了重试、异步接口、SSL认证等逻辑，更多高级的功能见[使用文档](pages/guide/quick-start.md)


```java
// 声明对接某支付接口
@WeChatPayAPI
public interface UserInfoAPI {

    // 定义异常重试次数
    @HttpFastRetry(maxAttempts = 3, delay = 2000)
    @GetHttpInterface("/list/info")
    CompletableFuture<UserInfo> getUserInfo2(String id);

    // Future异步接口
    @GetHttpInterface("/list/info")
    CompletableFuture<UserInfo> getUserInfo1(String id);

    // 配置 SSL认证
    @SslCfg(trustStore = "${channel.weChat.ssl.trustStore}")
    @GetHttpInterface("/list/info")
    UserInfo getUserInfo(String id);

} 


```