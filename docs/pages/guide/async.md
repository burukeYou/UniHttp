
UniHttp支持将接口配置成异步接口，当服务端处理时间很长时`避免请求超时`， 也能防止本地阻塞大量线程，提高吞吐量。

想要将接口配置成异步接口很简单，只需将接口返回值定义成Future类型包装即可（支持Future、CompletableFuture）， 如果是void方法需要在 @HttpInterface注解上的async字段配置为true。

例子：

```java
   @PostHttpInterface(path = "/user-web/get",
                       async = true  // 配置成异步接口)
    void add();

    // 异步模式
    @PostHttpInterface(path = "/user-web/get"
    Future<UserInfo> getUser();
    
    // 异步模式
    @PostHttpInterface(path = "/user-web/get")
    CompletableFuture<SystemInfo> getSystem();

```

如果不关心异步接口的执行结果，可将方法返回值声明为 void 类型，如需获取异步操作结果， 可将方法返回值定义成 Future 或者 CompletableFuture 都可。 推荐使用  CompletableFuture的whenComplete方法去拿到异步接口的结果，这样也不会阻塞当前线程。 