# UniHttp
-------
![travis](https://travis-ci.org/nRo/DataFrame.svg?branch=master)
[![License](http://img.shields.io/badge/license-apache%202-brightgreen.svg)](https://github.com/burukeYou/fast-retry/blob/main/LICENSE)


UniHttp 是一个声明式 HTTP 请求对接框架，帮助开发者快速完成第三方 HTTP 接口对接。通过简洁的配置即可实现接口复用，无需关注底层请求实现细节：

与传统 HTTP 客户端（如 HttpClient/OkHttp）的区别：
- 非替代关系而是更高层抽象
- 专注业务对接而非协议实现
- 支持通过 SPI 扩展请求处理流程
- 提供更简洁易用的HTTP配置


# 特征
- ✅ 声明式使用，标准化入口
- ✅ 配置为主，实现为辅
- ✅ 异步接口
- ✅ 异步重试
- ✅ SSL认证
- ✅ 超时配置
- ✅ 数据模型绑定
- ✅ 多生命周期处理（预处理/后处理/异常处理）



# 使用后

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