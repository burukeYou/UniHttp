# 1、简介
除了可以在`@HttpInterface注解`上指定静态请求参数外，Unittp 还提供了各种`@Par注解`（注解名后缀带Par） 去动态构建请求的各种参数，具体支持的请求参数构建注解如下：

| 参数注解           | 描述                     |
|--------------------|--------------------------|
| @QueryPar          | URL查询参数              |
| @PathPar           | URL路径变量参数              |
| @HeaderPar         | 请求头参数               |
| @CookiePar         | Cookie参数               |
| @BodyJsonPar       | JSON请求体 （application/json）              |
| @BodyTextPar       | 字符串文本请求体 （test/plain）         |
| @BodyBinaryPar     | 二进制请求体 （application/octet-stream）            |
| @BodyFormPar       | 表单请求体    （application/x-www-form-urlencoded）           |
| @BodyMultiPartPar  | 复杂表单请求体 （multipart/form-data）        |
| @BodyXmlPar        | XML请求体      （application/xml）          |
| @ComposePar        | 参数组合注解           |


为方便说明，下文所描述的`普通值类型`，表示的是基本类型、包装类型、字符串等数据类型

# 2、URL查询参数
`@QueryPar注解` 用于标记HTTP请求URL查询参数

**支持的方法参数类型说明**：
- **单个查询参数支持**：普通值、集合
- **多个查询参数支持**：对象（自动展开字段）或Map（自动展开键值对）


```java
    @PostHttpInterface
    void getUser(@QueryPar("id")  String id,  //  普通值   
                 @QueryPar("ids") List<Integer> idsList, //  普通值集合
                 @QueryPar User user,  // 对象
                 @QueryPar Map<String,Object> map); // Map

    
```

当方法参数类型为`对象或Map时`，会自动展开为多个键值对形式的查询参数，可传递多个查询参数，其中：
- 对象类型：以字段名作为参数名，字段值作为参数值
- Map类型：以键（key）作为参数名，值（value）作为参数值

若方法参数类型为`非对象或Map时`，会被视作为单个查询参数处理， 规则为
- 以方法参数名或者QueryPar注解配置的名字作为参数名， 方法参数值作为参数值


# 3、URL路径参数
`@PathPar注解` 用于标记Http请求路径变量参数

```java
    @PostHttpInterface("/getUser/{userId}/detail")
    UserInfo getUser(@PathPar("userId")  String id); 
```

最终会将 参数id值 替换到 请求路径的 "/getUser/{userId}/detail" 的 {userId} 上


# 4、请求头参数
`@HeaderPar注解` 用于标记HTTP请求头参数

**支持的方法参数类型说明**：
- **单个请求头参数**： 普通值
- **多个请求头参数**：对象（自动展开字段）或Map（自动展开键值对）


```java
    @PostHttpInterface
    BaseRsp<String> getUser(@HeaderPar("id")  String id,  //  普通值   
                            @HeaderPar User user,  // 对象
                            @HeaderPar Map<String,Object> map); // Map

    
```



# 5、Cookie参数
`@CookiePar注解` 用于标记HTTP请求头的cookie参数


**支持的方法参数类型说明**：
- **单个cookie参数**： 字符串、内置的`com.burukeyou.uniapi.http.support.Cookie`对象
- **多个cookie参数**：对象（自动展开字段）、Map（自动展开键值对）、Cookie对象列表、多个Cookie键值对的拼接字符串


```java
    @PostHttpInterface
    void getUser(@CookiePar("id")  String par1,  //   字符串
                 @CookiePar String par2,  //  多个Cookie键值对的拼接字符串
                 @CookiePar Map<String,Object> map, // Map
                 @CookiePar UserReq req, // 自定义对象
                 @CookiePar  Cookie cookieObj,  // 单个Cookie对象 
                 @CookiePar List<Cookie> cookieList) // Cookie列表                 
```


**方法参数为不同类型时的隐式处理规则**：

**1. 为字符串等类型时：**
   - 如果指定参数名（如`@CookiePar("id")`）
     - 处理成单个Cookie（`id=参数值`）
   - 如果未指定参数名 
     - 会先判断是不是`多个Cookie键值对的拼接字符串`（如`name=value;name2=value2`），如果是则解析为多个cookie键值对去发送请求, 如果不是， 则还是处理成单个Cookie（`par2=参数值`），并且以方法参数名作为键。

**2.为Map类型：**
- 自动展开键值对作为多个cookie键值对

**3.为自定义对象类型时**
- 自动展开类的所有字段作为多个cookie键值对

**4.显示使用内置Cookie对象、Cookie列表**
- 不处理，以上传的cookie为准

# 6、请求体
下面的请求体标记注解会自动设置对应的Content-type请求头，如果想要修改可以在注解 `@HttpInterface`的contentType参数上手动指定

## 6.1、JSON请求体
`@BodyJsonPar注解`  用于标记Http请求体内容为JSON形式, 并自动设置请求的Content-type为 `application/json`


**支持的方法参数类型说明**：对象、对象集合、Map、普通值、普通值集合


```java
    @PostHttpInterface
    BaseRsp<String> getUser(@BodyJsonPar  String id,                //  普通值
                            @BodyJsonPar  String[] id               //  普通值集合
                            @BodyJsonPar List<User> userList,       // 对象集合
                            @BodyJsonPar User user,                  // 对象
                            @BodyJsonPar Map<String,Object> map);    // Map
```

序列化和反序列化默认用的是Fastjson2，所以如果想指定别名，可以在字段上标记 @JSONField 注解取别名.


**请求体的JSON结构生成的隐式处理规则如下**：

1、序列化和反序列化默认用的是Fastjson2，所以如果想指定别名，可以在字段上标记 @JSONField 注解取别名.

2、如果@BodyJsonPar注解`不指定参数名时`， 直接将参数值JSON序列化后作为最终要发送的Http请求体。

3、如果@BodyJsonPar注解`指定参数名时`: 
- 比如 `@BodyJsonPar("name")` ，则会被自动处理成json格式 `{"name":"实际参数值"}`，
- 指定的参数名还可以是具体的json路径， 这样就会生成深层次的json结构.  比如 `@BodyJsonPar("$.bbq.kk.age")`,  会被自动处理成json格式`{"bbq":{"kk":{"user":"实际参数值"}}}`,  下

4、如果方法参数里指定多个@BodyJsonPar， 会自动将处理后的多个JSON合并成一个JSON请求体。 下面是具体案例：

```java
 public class UserInfo {
    private Long id;
    private String name;
 }

    @PostHttpInterface("/user-web/post")
    BaseRsp<UserInfo> getUserInfo(@BodyJsonPar UserInfo req,
                                  @BodyJsonPar("sex") String sex,
                                  @BodyJsonPar("$.bbq.kk.user") Integer userAge);

```

上面这个方法在使用时，会依次对 req、sex、age三个方法参数进行序列化分别得到以下三个json格式

req参数:    `{"id":"1',"name":"jay"}`
sex参数:   `{"sex": "男"}`
userAge参数:    `{"bbq":{"kk":{"user": 35}}}`

然后会将三个参数的JSON合并成一个JSON变成

```json
{
    "name": "jay",
    "id": 1,
    "sex": "男",
    "bbq": {
        "kk": {
            "user": "999"
        }
    }
}
```


## 6.2、Text请求体
`@BodyTextPar` 用于标记Http请求体内容为TEXT文本形式, 并自动设置请求的Content-type为 `test/plain`

**支持的方法参数类型说明**： Object
- 会调用Object.toString() 转成字符串进行传输，请确保这这是你期望的格式

```java
    @PostHttpInterface
    BaseRsp<String> getUser(@BodyTextPar  String id);  
```


## 6.3、表单form请求体
`@BodyFormPar注解` 用于标记Http请求体内容为普通表单形式, 并自动设置请求的Content-type为 `application/x-www-form-urlencoded`


**支持的方法参数类型说明**：
- **单个表单参数**： 普通值
- **多个表单参数**：对象（自动展开字段）或 Map（自动展开键值对）


```java
    @PostHttpInterface
    BaseRsp<String> getUser(@BodyFormPar("name") String value,         //  普通值
                            @BodyFormPar User user,                   // 对象
                            @BodyFormPar Map<String,Object> map);    // Map
```


## 6.4、表单Multipart请求体
`BodyMultiPartPar注解` 用于标记Http请求体内容为复杂表单形式, 并自动设置请求的Content-type为 `multipart/form-data`


**支持的方法参数类型说明**：
- **单个表单参数**： 普通值、`文件类型`（File、InputStream、byte[]、内置的HttpFile对象）
- **多个表单参数**：对象（自动展开字段）、 Map（自动展开键值对）、集合、数组


```java
    @PostHttpInterface
    void uploadFile(@BodyMultiPartPar("name") String value,         //  单个表单-文本值
                    @BodyMultiPartPar("img1") byte[] value1,         //  单个表单-文件二进制
                    @BodyMultiPartPar("logoImg") InputStream value3,      //  单个表单-文件流
                    @BodyMultiPartPar("logoImg") File value4,      //  单个表单-文件对象
                    @BodyMultiPartPar HttpFile file,   //  单个表单-内置的文件对象

                    @BodyMultiPartPar("name") String[] value,       //  多个表单-文本值
                    @BodyMultiPartPar("img1") List<byte[]> value,  //  多个表单-文件二进制
                    @BodyMultiPartPar("img2") InputStream[] value,  //  多个表单-文件流
                    @BodyMultiPartPar("img3") File[] value,  //  多个表单-文件流
                    @BodyMultiPartPar List<HttpFile> file,   //  多个表单-内置的文件对象

                    @BodyMultiPartPar User user,                   // 对象
                    @BodyMultiPartPar Map<String,Object> map // Map
                    )      
```


**表单结构生成的隐式处理规则如下**：

**1. 参数类型处理**：
- **普通值/文件类型**： 按单个表单字段处理
- **对象/Map类型**：自动展开为多个表单字段，若字段值为文件类型则自动识别为文件字段

**2. 文件上传特性**：
- 通过注解`@BodyMultiPartPar`可分别指定表单字段名和上传文件名
- 使用的内置`HttpFile`对象支持在调用时动态传参设置文件名，避免在注解中硬编码



## 6.5、XML请求体
`@BodyXmlPar注解` 用于标记Http请求体内容为XML形式, 并自动设置请求的Content-type为 `application/xml`

**支持的方法参数类型说明**：  自定义类

```java
    @PostHttpInterface
    BaseRsp<String> getUser(@BodyXmlPar UserInfoReq req);    
```

默认使用的是JAXB去进行XML序列化， 如果是JDK9以上版本需要手动引入如下 JAXB 依赖， JDK8则可忽略

```xml

    <dependency>
      <groupId>javax.xml.bind</groupId>
      <artifactId>jaxb-api</artifactId>
      <version>2.3.1</version>
    </dependency>
```


# 7、@ComposePar注解
这个注解本身不是对Http请求内容的配置，仅用于标记一个对象，然后会对该对象内的所有标记了`其他@Par注解的字段`进行嵌套解析处理，
目的是减少方法参数数量，支持都内聚到一起配置

支持以下方法参数类型:  自定义对象

```java
    @PostHttpInterface
    BaseRsp<String> getUser(@ComposePar UserReq req);    
```

比如UserReq里面的字段可以嵌套标记其他@Par注解，具体支持的标记类型和逻辑与前面一致
```java
class UserReq {

    // 查询参数
    @QueryPar
    private Long id;

    // 请求头
    @HeaderPar
    private String name;

    // json请求体
    @BodyJsonPar
    private Add4DTO req;

    // cookie请求头
    @CookiePar
    private String cook;
}
```




