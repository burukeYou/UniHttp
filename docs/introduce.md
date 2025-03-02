UniHttp
-------
![travis](https://travis-ci.org/nRo/DataFrame.svg?branch=master)
[![License](http://img.shields.io/badge/license-apache%202-brightgreen.svg)](https://github.com/burukeYou/fast-retry/blob/main/LICENSE)


UniHttp 是一个声明式 HTTP 请求对接框架，帮助开发者快速完成第三方 HTTP 接口对接。通过简洁的配置即可实现接口复用，无需关注底层请求实现细节：
- 自动处理请求参数序列化
- 智能管理 HTTP 连接池
- 内置响应结果反序列化
- 异常处理统一封装

其配置方式与 `Spring Controller` 注解风格相似，但采用反向配置模式实现。框架核心优势在于：
1. 高内聚的接口定义 - 将渠道配置、请求参数、响应处理聚合在单一接口
2. 声明式编程模型 - 通过注解驱动替代传统过程式编码
3. 可扩展架构 - 支持自定义渠道注解和生命周期钩子

与传统 HTTP 客户端（如 HttpClient/OkHttp）的区别：
- 非替代关系而是更高层抽象
- 专注业务对接而非协议实现
- 当前底层仍基于 OkHttp 实现
- 支持通过 SPI 扩展请求处理流程


# 特征
框架特色能力：
- ✅ 多生命周期钩子（预处理/后处理/异常处理）
- ✅ 自定义序列化策略
- ✅ 动态请求头管理
- ✅ 多路渠道配置支持
- ✅ 响应结果自动类型转换



# 使用

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

甚至可能会是这样的, 里面简单的几个配置便实现了重试、异步接口、SSL认证等逻辑，更多高级的功能见使用文档


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