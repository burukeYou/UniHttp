

# 1、@HttpApi注解
在前面的章节中，我们已经使用`@HttpApi`注解来声明HTTP服务接口。该注解主要应用于接口类级别，具有以下核心功能：

1. **接口代理** - 标记了该注解的接口类，会被自动代理实现HTTP请求发送的全周期过程
2. **扩展配置**：
   - 可定义基础的请求URL地址
   - 可指定自定义HTTP客户端
   - 可指定自定义的JSON/XML序列化方式
   - 可实现自定义HTTP代理逻辑
3. **生命周期管理** - 通过处理器（HttpApiProcessor）实现请求/响应全流程的定制化处理


# 2、自定义HttpApi组合注解
当想要修改@HttpApi 注解的名字， 或者需要配置默认参数时，或者减少配置其他注解时， 原生@HttpApi注解的重复配置会显得繁琐。为此提供自定义HttpApi注解功能，并且复合组装其他注解，比如@SslCfg、@HttpCallCfg

**实现规则**： 创建自定义注解：并在注解类上标注@HttpApi即可， 然后就可以在@HttpApi上设置默认参数， 也可以重写@HttpApi注解的参数， 并且可以配置其他注解比如@@SslCfg、@HttpCallCfg
-  参数继承规则
   - 使用`@AliasFor(annotation = HttpApi.class)`标注需要覆盖的父注解字段，字段名需与父注解保持一致（若需改名需显式指定value参数）

整个过程在实现上有点类似 “注解（组合）继承”， 只是Java中只支持类继承不支持注解继承，所以只好通过@AliasFor注解去“人为”的实现继承注解

下面自定义了继承 `@HttpApi 注解`实现了一个@BaiDuHttpApi注解。

```java
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@HttpApi(
    jsonConverter = MyJsnonConverter.class, // 指定使用的json序列化方式
    processor = MyHttpApiProcessor.class, // 指定使用的生命周期处理器
    httpClient = MyOkHttpClientFactory.class // 指定使用的 OkHttp Client
)
@SslCfg // 配置SSL认证相关逻辑，具体见 [SSL认证章节](pages/guide/https.cmd)
public @interface BaiDuHttpApi {

    // 重写 HttpApi注解的url属性,并指定新的默认值
    // 不指定value表示重写的属性名与当前自定义的注解的属性名一致
    @AliasFor(annotation = HttpApi.class)
    String url() default "${channel.baidu.url}";
    
    // 重写HttpApi注解的url属性,并指定新的默认值
    // 指定value表示重写的属性名 达到修改属性名的效果
    @AliasFor(annotation = HttpApi.class, value="url")
    String address() default "${channel.baidu.url}";
    
}

```

自定义 @BaiDuHttpApi注解后的使用与原生@HttpApi注解一致,标记在需要代理的接口类即可

```java
@BaiDuHttpApi
public interface UserInfoAPI {
    
    @GetHttpInterface("/get")
    UserInfo getUser();
}

```


# 3、HttpApiProcessor

## 3.1 简介
HttpApiProcessor 定义了处理HTTP请求接口的`全生命周期钩子`，包含请求发送、响应处理及反序列化等关键环节。开发者可通过实现这些钩子来自定义业务对接逻辑

## 3.2 生命周期
目前提供8个核心生命周期钩子，完整处理流程如下：

``` 
                  postBeforeHttpRequest                (请求预处理阶段) 请求初始化后的二次处理
                         |
                         V
                postBeforeSendHttpRequest              (请求准备阶段) 请求最终发送前的最后回调
                         ｜
                         V
                  postSendingHttpRequest                (请求发送阶段) 同步请求发送时的实时回调
                         |
                         V
                   postAfterHttpResponse               （响应接收阶段）请求完成后的统一回调（含异常处理）
                         |
                         V
               postAfterHttpResponseBodyString          (响应解析阶段) 原始响应文本的后置处理
                         |
                         V
           postAfterHttpResponseBodyStringDeserialize  （反序列化阶段） 响应文本反序列化为目标对象
                         |
                         V
              postAfterHttpResponseBodyResult           (结果处理阶段) 目标对象的后置处理
                         |
                         V
              postAfterMethodReturnValue                (方法返回值处理阶段) 最终返回值加工
```

1. **发送请求前阶段**
   - `postBeforeHttpRequest`：发送前请求体的后置处理（如签名计算、参数加密）
   - `postBeforeSendHttpRequest`：请求准备发送前的回调

2. **请求执行阶段**
   - `postSendingHttpRequest`：同步请求发送时的自定义处理（注意：异步请求不触发）

3. **响应阶段**
   - `postAfterHttpResponse`：统一响应回调，不管成功还是失败都会回调此方法，如果失败会返回异常信息，没有异常信息则为成功（建议用于日志记录、耗时统计）
   - `postAfterHttpResponseBodyString`：原始响应文本处理（如数据解密、格式清洗）
   - `postAfterHttpResponseBodyStringDeserialize`：自定义反序列化逻辑（覆盖默认解析）
   - `postAfterHttpResponseBodyResult`：目标对象的后置处理（如默认值填充、数据补全）
   - `postAfterMethodReturnValue`：方法返回值最终加工（类似AOP后置处理）


HttpApiProcessor接口的方法关键参数说明
- `UniHttpRequest`：原始请求信息
- `UniHttpResponse`：原始响应信息
- `HttpApiMethodInvocation`：被代理的方法




## 3.3 使用步骤
开发者可以通过继承HttpApiProcessor实现自己的定制化逻辑


1、 先实现HttpApiProcessor

```java
public class MyHttpApiProcessor implements HttpApiProcessor<Annotation> {
    // 自定义覆盖实现父类的钩子方法
}
```

自定义实现的HttpApiProcessor<T>的泛型参数表示用于处理哪些注解上，除了可以是通用的Annotation注解对象， 也可以是具体的 @HttpApi注解对象，以及自定义的@HttpApi注解，比如下面
```java
// 原生的HttpApi注解
public class MyHttpApiProcessor implements HttpApiProcessor<HttpApi> {
    // 自定义覆盖实现父类的钩子方法
}

// 自定义的HttpApi注解，比如前面的 @BaiDuHttpApi
public class MyHttpApiProcessor implements HttpApiProcessor<BaiDuHttpApi> {
    // 自定义覆盖实现父类的钩子方法
}
```


2、 配置生效 

支持以下两种配置方式：
- 在@HttpApi注解中配置  （类级别配置）
- 在具体的@HttpInterface注解中局部配置 （方法级别配置）


```java

// 配到HttpApi注解上
@HttpApi(processor = MyHttpApiProcessor.class)
public interface UserAPI {

}

@HttpApi
public interface UserAPI {

    // 配置具体代理接口上
    @PostHttpInterface(path = "/get", processor = MyHttpApiProcessor.class)
    void getUser();
}
```

**自定义HttpApiProcessor的加载规则**
- 优先从Spring容器中查找MyHttpApiProcessor实例，存在则直接使用。若容器中不存在，则通过默认构造器创建新实例



## 3.4 忽略钩子
当我们类上配置了自定义的HttpApiProcessor钩子时默认会对该类的所有接口方法生效，如果说想要指定某些方法不执行该 HttpApiProcessor钩子 或者 具体的钩子方法， 可以通过@FilterProcessor注解 可以配置

配置代码举例：

```java
// 在类上指定了自定义的处理器MyHttpApiProcessor
@HttpApi(processor=MyHttpApiProcessor)
public interface WeatherServiceApi {

	// 配置此接口不走 HttpApiProcessor的所有回调钩子
    @FilterProcessor(ignoreAll = true)
    @PostHttpInterface(path = "/mtuan/weather/getToken")
    HttpResponse<String> getToken(@);
	   
	// 配置此接口不走 HttpApiProcessor#postSendingHttpRequest 钩子逻辑
    @FilterProcessor(ignoreSending = true)
    @PostHttpInterface(path = "/mtuan/weather/getToken")
    HttpResponse<String> getToken(@);

	// 配置此接口不走 HttpApiProcessor的所有发送请求前钩子
    @FilterProcessor(excludeMethods = ProcessorMethod.LIST_BEFORE_METHOD)
    @PostHttpInterface(path = "/mtuan/weather/getToken")
    HttpResponse<String> getToken();

}
```


# 4、使用建议
针对每个对接的不同的第三方接口服务提供商，建议都自定义一个与该服务商对应的 `@HttpApi注解`（当然也可以定义一个全局通用的）， 并搭配实现`HttpApiProcessor`与该服务提供商的定制化交互逻辑。

比如对接了`某微支付`、`某墨天气`两个第三方接口服务提供商

**针对某微支付可以创建**

```java
// 1、创建某微支付的 @HttpApi注解， 并配置一些通用逻辑，注解里也可以增加一些渠道的业务参数
@HttpApi(
    processor = WxHttpApiProcessor.class, // 配置某微支付的渠道交互逻辑
    url = "${channel.wx.url}" // 配置域名地址
)
public @interface WxHttpApi {

    // 业务参数 appiId
    String appId() default "${channel.wx.appId}"
    
    // 业务参数 商户号
    String memberNo default "01";

}

// 2、实现某微支付的渠道交互逻辑
public class WxHttpApiProcessor implements HttpApiProcessor<WxHttpApi> {
    // 自定义覆盖实现父类的钩子方法
}
```


**针对某墨天气可以创建**

```java
// 1、创建某墨天气的 @HttpApi注解， 并配置一些通用逻辑，注解里也可以增加一些渠道的业务参数
@HttpApi(
    processor = MojiHttpApiProcessor.class, // 配置某墨天气的渠道交互逻辑
    url = "${channel.moji.url}" // 配置域名地址
)
public @interface MoJiHttpApi {

    // 业务参数 appiId
    String appId() default "${channel.wx.appId}"
    
    // 业务参数 商户号
    String memberNo default "01";

}

// 2、实现某微支付的渠道交互逻辑
public class MojiHttpApiProcessor implements HttpApiProcessor<MoJiHttpApi> {
    // 自定义覆盖实现父类的钩子方法
}
```

之后就可以使用 `@WxHttpApi` 和 `@MoJiHttpApi` 去愉快的对接每个具体的HTTP接口了, 比如

**对接某微支付接口集**
```java
@WxHttpApi  // 标记使用的渠道
public interface WxAPI {

    /**
     * 创建支付订单
     * @return 支付订单号
     */
    @PostHttpInterface("/payment/create")
    PaymentOrder createOrder(@Body CreateOrderRequest request);

    /**
     * 查询支付状态
     */
    @GetHttpInterface("/payment/query/{orderId}")
    PaymentStatus queryOrder(@Path("orderId") String orderId);
}

```


**对接某墨天气接口集**
```java
 @MoJiHttpApi  // 标记使用的渠道
public interface MojiWeatherAPI {

    /**
     * 获取实时天气
     * @param cityCode              国家行政区划代码
     */
    @GetHttpInterface("/v3/weather/now")
    WeatherData getRealtimeWeather(@QueryPar("city") String cityCode);

    /**
     * 查询空气质量指数
     */
    @GetHttpInterface("/v3/air/now")
    AirQuality getAirQuality(@QueryPar("location") String locationId);  

}

```


当然也可以定义的一个全局通用的Common HttpApi注解， 然后基于绑定的HttpApiProcessor实现通用的处理逻辑， 比如将接口信息配置到数据库， 执行前从数据库拉去组装参数后再发送， 响应后可以再记录日志到数据库，以及做一些自定义的响应处理。



