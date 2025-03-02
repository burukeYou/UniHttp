

`@HttpInterface注解` 用于配置一个接口的参数，包括请求方式、请求路径、请求头、请求cookie、请求查询参数等等


并且内置了以下请求方式的@HttpInterface，不必再每次手动指定请求方式
- @PostHttpInterface
- @PutHttpInterface
- @DeleteHttpInterface
- @GetHttpInterface



常见的参数配置含义如下: 
```java
    @PostHttpInterface(
            // 接口url,  支持配置环境变量占位符
            url="http://localhost" ,
            // 请求路径,  支持配置环境变量占位符
            path = "/getUser",
            // 请求头,  value支持配置环境变量占位符
            headers = {"clientType: sys-app","userId=99","appId: ${channel.appId}"},
            // url查询参数 ,  value支持配置环境变量占位符
            params = {"name=周杰伦","age=1","appId=${channel.appId}"},
            // url查询参数拼接字符串
            paramStr = "a=1&b=2&c=3&d=哈哈&e=%E7%89%9B%E9%80%BC",
            // cookie 字符串
            cookie = "name=1;sessionId=999"，
            // 请求体格式. 如果使用了 @Par注解标记请求体不需要手动指定
            contentType= "appliaction/json;chartset=utf-8"
    )
    void getUser();
```




