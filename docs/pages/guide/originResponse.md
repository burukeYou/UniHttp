


如果需要拿到原始的Http响应报文，只需要在方法返回值用HttpResponse包装返回即可。

```java
    @PostHttpInterface("/user-web/get")
    HttpResponse<UserInfo> get();
```

示例中，此时`HttpResponse<UserInfo>`里的泛型UserInfo才是代表接口实际返回的响应体内容，后续可直接手动获取

通过 HttpResponse 对象可获取以下响应要素：
- HTTP 状态码
- 响应头信息
- 响应Cookie集合
- 反序列化后的响应体





