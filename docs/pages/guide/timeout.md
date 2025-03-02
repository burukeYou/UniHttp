

[自定义HTTP客户端](pages/guide/httpClient.md)虽然可以配置超时时间，但是无法针对每个接口做不同的超时配置。
这时需要使用  @HttpCallCfg 注解来针对每个接口进行配置

```java
    @PostHttpInterface
    @HttpCallCfg(callTimeout = 3000, //  整个接口调用（连接+写入+读取）的超时时间
                 connectTimeout = 3000, // 进行握手连接的超时时间
                 writeTimeout = 3000,  // 进行写数据到服务端的超时时间
                 readTimeout = 3000)  // 从服务器读数据的超时时间
    BaseRsp<String> getUser();    
```


