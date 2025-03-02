
# 1、简介
内置使用的是OkHttp的HTTP客户端，下面介绍如何自定义HTTP客户端

# 2、自定义OkHttp客户端

## 2.1、全局OkHttp客户端
如果要自定义全局的Okhttp客户端,实现 `GlobalOkHttpClientFactory接口`并注入spring的bean即可，以此达到替换掉框架内置的默认OkHttp客户端配置

```java
@Component
public class MyGlobalOkHttpClientFactory implements GlobalOkHttpClientFactory {
    private  final OkHttpClient client;

    public MyGlobalOkHttpClientFactory() {
        this.client = new OkHttpClient.Builder()
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(20,10, TimeUnit.MINUTES))
                .build();
    }
    
    @Override
    public OkHttpClient getHttpClient() {
        return client;
    }
}

```

## 2.2、接口级的OkHttp客户端
@HttpApi也可自定义的不同Okhttp客户端， 可以实现OkHttpClientFactory接口并注入Spring，然后在@HttpApi注解上指定该实现类即可。
比如
```java
@Component
public class UserChannelOkHttpClientFactory implements OkHttpClientFactory {
    private  final OkHttpClient client;

    public UserChannelOkHttpClientFactory() {
        this.client = new OkHttpClient.Builder()
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(20,10, TimeUnit.MINUTES))
                .build();
        log.info("UserChannelOkHttpClientFactory client:{}",client);
    }
    
    @Override
    public OkHttpClient getHttpClient() {
        return client;
    }
}
```

然后在配置到@HttpApi的httpClient属性即可
```java
@HttpApi(httpClient = UserChannelOkHttpClientFactory.class)
interface UserServiceApi {
    
}
```
