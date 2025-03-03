

# 1、重试功能

UniHttp内部实现了两种重试机制，一种是简单的同步重试机制， 底层原理就是 `for循环 + sleep`去重试,   另一种重试机制是一个高吞吐量的异步重试机制， 底层原理是基于我的另一个重试框架[FastRetry](https://github.com/burukeYou/fast-retry)实现。 这两种重试机制机制分别配套了两个`重试注解@HttpRetry` 和 `@HttpFastRetry`, 下面分别介绍如何使用。

# 2、@HttpRetry使用
这是一个简单的同步重试机制， 底层原理就是 `for循环 + sleep`去重试

```java
   // 配置最大重试次数为3， 当发生任意异常时，在1000毫秒后进行重试，
   @HttpRetry(maxAttempts = 3,delay = 1000)
    BaseRsp<String > getUser1(@QueryPar("key") String value);

   // 配置最大重试次数为4，当发送IoException时，在5000毫秒后进行重试
    @HttpRetry(maxAttempts = 4,delay = 5000, include = IoException.class)
    BaseRsp<String > getUser2(@QueryPar("key") String value);
```

- maxAttempts：  最大重试次数
- delay： 重试的间隔时间（毫秒）
- include：  发生指定异常才重试， 不指定默认所有异常都会进行重试


# 3、@HttpFastRetry使用

该机制基于异步重试框架[FastRetry](https://github.com/burukeYou/fast-retry)实现， 所以需要额外引入 FastRetry的核心包依赖(0.3.2版本及以上)

```xml
    <dependency>
      <groupId>io.github.burukeyou</groupId>
      <artifactId>fast-retry-core</artifactId>
      <version>0.3.2</version>
    </dependency>
```

## 3.1、使用
```java
    // 配置最大重试次数为3，当发生任意异常时，在2000毫秒后进行重试，
    @HttpFastRetry(maxAttempts = 3,delay = 2000)
    BaseRsp<String > get1(@QueryPar("key") String value);

   // 配置最大重试次数为4，当发生IoException时，在5000毫秒后进行重试
    @HttpFastRetry(maxAttempts = 4,delay = 5000, include = IoException.class)
    BaseRsp<String > getUser2(@QueryPar("key") String value);
```

- maxAttempts：  最大重试次数
- delay： 重试的间隔时间（毫秒）
- include：  发生指定异常才重试， 不指定默认所有异常都会进行重试




## 3.2、重试策略
@HttpFastRetry 的 policy参数支持指定不同的重试策略， 重试策略能够<mark>根据响应结果</mark>判断是否进行重试。 目前支持三种不同类型的重试策略： `HttpRetryResultPolicy`、`HttpRetryResponsePolicy`、`HttpRetryInterceptorPolicy`
-  policy参数值加载逻辑，优先从Spring容器获取，加载不到则手动new创建该类去使用




#### 3.2.1、HttpRetryResultPolicy
该策略主要用于根据HTTP响应结果判断是否进行重试， 

实现HttpRetryResultPolicy接口， 根据响应结果UserInfo的status字段判断是否进行重试

```java
   // 实现HttpRetryResultPolicy
    public static class MyPolicy1 implements HttpRetryResultPolicy<UserInfo> {
      //  当用户状态为处理中则继续重试
      // 返回true表示继续重试、返回false表示停止重试
        @Override
        public boolean canRetry(UserInfo info）{
        	return info.getStatus() == "处理中"
        }
    }
```


#### 3.2.2、HttpRetryResponsePolicy
该策略主要用于根据HTTP响应结果判断是否进行重试， 

实现HttpRetryResponsePolicy接口， 根据响应结果UserInfo的status字段判断是否进行重试

```java
 // 实现HttpRetryResponsePolicy
   public static class MyPolicy2 implements HttpRetryResponsePolicy<UserInfo> {
      // 返回true表示继续重试、返回false表示停止重试
        @Override
        public boolean canRetry(ResultInvocation<UserInfo> arg）{.     
           // 获取结果
        	UserInfop info = arg.getBodyResult();
        	// 获取请求信息
        	UniHttpRequest request = invocation.getRequest();
			// 获取响应信息
            UniHttpResponse response = invocation.getResponse();
            // 获取当前执行次数
            long curExecuteCount = invocation.getCurExecuteCount();
  
  			// 当用户状态为处理中则继续重试
        	return info.getStatus() == "处理中"
        }
    }
```




### 3.2.3、HttpRetryInterceptorPolicy策略
该策略则提供更加细粒度的重试控制， 包括每次重试前、重试成功后、重试失败后的控制

```java
    public static class MyPolicy3 implements HttpRetryInterceptorPolicy<UserInfo> {
      
      // 重试之前回调： 如果返回false则停止执行、否则继续执行
        @Override
        public boolean beforeExecute(UniHttpRequest uniHttpRequest, HttpRetryInvocation invocation) throws Exception {
            log.info("执行重试之前 当前执行次数:{}",invocation.getCurExecuteCount());
            HttpFastRetry httpFastRetry = invocation.getHttpFastRetry();
            return HttpRetryInterceptorPolicy.super.beforeExecute(uniHttpRequest, invocation);
        }

      // 重试发生异常时回调：  如果返回false则停止执行、否则继续执行
        @Override
        public boolean afterExecuteFail(Exception exception, UniHttpRequest uniHttpRequest, HttpRetryInvocation invocation) throws Exception {
            log.info("执行重试失败后 当前执行次数:{}",invocation.getCurExecuteCount());
            return HttpRetryInterceptorPolicy.super.afterExecuteFail(exception, uniHttpRequest, invocation);
        }

     // 重试成功后回调: 如果返回false则停止执行、否则继续执行
        @Override
        public boolean afterExecuteSuccess(UserInfo info, UniHttpRequest uniHttpRequest, UniHttpResponse uniHttpResponse, HttpRetryInvocation invocation) {
            log.info("执行成功后: {} 当前执行次数:{}", JSON.toJSONString(bodyResult),invocation.getCurExecuteCount());          
            // 如果为处理中继续执行重试
            return info.getStatus() == "处理中";
        }
    }
```

最后配置搭配@HttpFastRetry注解上
```java
  // 直接配置到注解上的policy参数上
  @HttpFastRetry(maxAttempts = 4,policy = MyPolicy1.class)
  Future<UserInfo> get42(@QueryPar("key") String value);
  
  // 直接配置到注解上的policy参数上
  @HttpFastRetry(maxAttempts = 4,policy = MyPolicy2.class)
  Future<UserInfo> get42(@QueryPar("key") String value);

  // 直接配置到注解上的policy参数上
  @HttpFastRetry(maxAttempts = 4,policy = MyPolicy3.class)
  Future<UserInfo> get42(@QueryPar("key") String value);

```


### 3.2.4、多种配置方式
除了上面介绍的将策略配置到注解上， 还支持其他配置方式

1）： 配置到方法参数上, 在调用时再手动new匿名函数传递

```java
@HttpApi
public interface UserService {
    @HttpFastRetry(maxAttempts = 4)
    Future<UserInfo> getUser(String value, HttpRetryResultPolicy<UserInfo> policy);
}

 
```

调用时匿名函数手动传递

```java
 UserService.getUser("value", userInfo ->  userInfo.getStatus()== "处理中"）
```
