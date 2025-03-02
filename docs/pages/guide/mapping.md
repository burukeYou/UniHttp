

# 1、数据绑定模型与映射
`@ModelBinding注解 `用于标记在请求和响应对象上面， 会自动解析该对象的所有字段， 如果字段包含指定的相关注解会自动执行相关数据绑定逻辑，减少手动传递。 目前支持 `@Value、@JsonPathMapping` 绑定

# 2、环境变量绑定 @Value
`@Value`  注解是Spring中自动填充环境变量的注解， 但是只有该类注入到容器在会生效， 而`@ModelBinding` 会延续这种功能， 每次在请求发送和响应时自动填充环境变量。  `@ModelBinding注解`支持标记在方法参数上、方法上、以及Class类上 都会生效

假设有以下请求参数类
```java
import org.springframework.beans.factory.annotation.Value;

class UserDTO {

   // 配置环境变量的值
   @Value("${channel.appId}")
   private String appId;

}
```

将@ModelBinding 标记在需要进行自动数据绑定的请求参数上，就会执行自动绑定逻辑.  如果不想每个参数都指定@ModelBinding， 也可直接在UserDTO类上标记
```java
    @PostHttpInterface("/user-web/add4")
    BaseRsp<Add4DTO> add4(@ModelBinding @BodyJsonPar UserDTO req，
    				      @ModelBinding @QueryPar    UserDTO req2);
```

同理响应数据也支持标记@ModelBinding， 但是只需标记在方法上， 会对方法的返回值进行数据绑定。 这里方法返回值是指响应体反序列化后的值UserDTO比如而非广义上指的方法返回值HttpResponse<UserDTO> 之类

```java
   // 会对 返回值UserDTO进行数据绑定
   @PostHttpInterface("/user-web/add4")
   @ModelBinding
   UserDTO get05();

   // 会对 返回值UserDTO进行数据绑定, 而非HttpResponse
   @PostHttpInterface("/user-web/add4")
   @ModelBinding
   HttpResponse<UserDTO> get05();

   // 会对 返回值UserDTO进行数据绑定, 而非Future
   @PostHttpInterface("/user-web/add4")
   @ModelBinding
   Future<UserDTO> get05();
   
```


# 3、参数映射绑定-  @JsonPathMapping
此该注解仅对 json格式请求体和json格式响应体生效，  该 注解@JsonPathMapping 用于 json路径的参数映射， 实现json的结构和我们定义的类结构不一致的的序列化和反序列化的功能。 `可以理解为 @JSNOField 注解指定别名进行序列化和反序列化的 升级版`。  只不过这个`别名`可以是一个json路径 。 基于该注解可以做请求体结构和 响应体的结构的映射转换。 实现扁平化参数、标准化参数、提取自己需要的参数等功能


@JsonPathMapping 标记在`请求体`和`响应体`时会有不同的功效， 具体如下：

## 3.1、标记在请求体类的字段上时

有如下请求体
```java
// 请求体
public class StuJsonReq {
	private Integer id;

   // 标记在请求体类的字段上时：
	@JsonPathMapping("$.user.name")
    private String name;
}

```

@ModelBinding 标记需要进行数据绑定的参数上面。
```java
    @PostHttpInterface(path = "/xxxx")
    void get06(@ModelBinding @BodyJsonPar StuJsonReq req);
```

这样在发送请求时， StuReq 实际被序列化的结构就是 下面
```json
{
	"id":1
	"user": {"name":"jay"}
}
```
而非传统的
```json
{
	"id":1
	"name":"jay"
}
```

<mark>说白就是序列化指定别名，只是这个别名可以是json路径</mark>

## 3.2 标记在响应体类的字段上时：

假设有如下响应类

```java

public class StuJsonRsp {
	private Integer id;

	@JsonPathMapping("$.user.name")
    private String name;
}

```

@ModelBinding 标记需要对返回值进行数据绑定
```java
    @PostHttpInterface(path = "/xxxx")
    @ModelBinding 
    StuJsonRsp get06();
```


这时会将 响应体json的 $.user.name 路径的值反序列化给到StuJsonRsp的name字段。

即如下响应体是能够成功反序列化成 StuJsonRsp。
```json
{
	"id":1
	"user": {"name":"jay"}
}
```


## 3.3、标记在方法返回值上时：
@JsonPathMapping 也支持标记在方法返回值上， 这样该json路径的值 会作为最终的响应的json字符串去进行反序列化成为方法返回值， 相当于只提取某一个json进行反序列化


```java
	   @PostHttpInterface(path = "/user-web/del05")
	   @JsonPathMapping("$.data")
	   StuDTO get04();
  ```

如原始返回结构是如下，如果我们只想要 $.data的数据，就可以使用 @JsonPathMapping("$.data")进行配置。
```json
{
  "requestTime":""
  "data":{"name":"jay","age":3}
}
```

# 4、嵌套模型绑定
如果需要绑定的模型存在嵌套层级，需要进行二次绑定标记，不然不会处理。
比如假设以下模型对象BaseResult,  需要对info字段和data字段里面的字段也进行数据绑定， 则需要在字段上面标记 `@ModelBinding`注解， 这样就会对里面的字段进行数据绑定
```java
@Getter
@Setter
public class BaseResult<R> {

    @JsonPathMapping("$.info.orderNo")
    private int[] nums333;

    @ModelBinding
    private UserInfo info;
    
    @ModelBinding
    private R data;
}

public class UserInfo{

	@JsonPathMapping("$.info.orderNo")
    private String name2;

}

```