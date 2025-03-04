


UniHttp
-------
![travis](https://travis-ci.org/nRo/DataFrame.svg?branch=master)
[![License](http://img.shields.io/badge/license-apache%202-brightgreen.svg)](https://github.com/burukeYou/fast-retry/blob/main/LICENSE)


一个声明式的Http请求对接框架，能以极快的方式完成对一个第三方Http接口的对接和使用，只要配置一下即可重复使用，
不需要开发者去关注如何发送一个请求，如何去传递Http请求参数，以及如何对请求结果进行处理和反序列化，这些框架都帮你一一实现 
就像配置 `Spring的Controller` 那样简单，只不过相当于是反向配置而已

该框架更注重于如何保持高内聚和可读性高的代码情况下与快速第三方渠道接口进行对接和集合，
而非像传统编程式的Http请求客户端（比如HttpClient、Okhttp）那样专注于如何去发送和响应一个Http请求，二者并不冲突只是注重点不一样，UniHttp 目前底层也是用的Okhttp去发送请求。
与其说的是对接的Http接口，不如说是对接的第三方渠道，UniHttp可支持自定义接口渠道方HttpAPI注解以及一些自定义的对接和交互行为 ，为此扩展了发送和响应和反序列化一个Http请求接口的各种生命周期钩子需要开发者去自定义实现。



# 2、快速开始
## 2.1、引入依赖

建议使用最新版本 [版本列表](https://central.sonatype.com/artifact/io.github.burukeyou/uniapi-http/versions)

```xml
    <dependency>
      <groupId>io.github.burukeyou</groupId>
      <artifactId>uniapi-http</artifactId>
      <version>0.2.3</version>
    </dependency>
```

如果是非spring环境、还需手动引入spring-context依赖

```xml
 <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.3.23</version>
</dependency>
```


## 2.2、对接接口
在类上标记@HttpAPI注解，用户表示定义一个Http接口的信息, 然后就可以去编写每个方法去对接每个接口。 
如下面两个方法的配置则对接了 GET http://localhost:8080/getUser和 POST http://localhost:8080/addUser 两个接口
方法返回值定义成Http响应body对应的类型即可，默认会使用fastjson反序列化Http响应body的值为该类型对象。

```java
@HttpApi(url = "http://localhost:8080")
interface UserHttpApi {
    
   @GetHttpInterface("/getUser")
   BaseRsp<String> getUser(@QueryPar("name") String param,@HeaderPar("userId") Integer id);
    
   @PostHttpInterface("/addUser")
   BaseRsp<Add4DTO> addUser(@BodyJsonPar Add4DTO req);
   
}
```

@QueryPar 表示将参数值放到Http请求的查询参数内

@HeaderPar    表示将参数值放到Http请求的请求头里

@BodyJsonPar 表示将参数值放到Http请求body内，并且content-type是application/json


1、getUser方法最终构建的Http请求报文为
```
GET http://localhost:8080/getUser?name=param
Header:
    userId: id
```
 
 
2、addUser最终构建的Http请求报文为 
```
        POST:  http://localhost:8080/addUser 
        Header: 
            Content-Type:   application/json
        Body:
            {"id":1,"name":"jay"}
```



## 2.3、声明定义的HttpAPI的包扫描路径

@UniAPIScan("com.xxx.demo.api") 会自动为改接口生成代理对象并且注入到Spring容器中，
之后只需要像使用Spring的其他bean一样，依赖注入使用即可

### 2.4 依赖注入使用即可

```java
@Service
class UserAppService {
    
    @Autowired
    private UserHttpApi userHttpApi;
    
    public void doSomething(){
        userHttpApi.getUser("jay",3);
    }
} 

```

# 3、文档地址
更多高级功能和具体功能特性见文档: 

[使用文档](https://burukeyou.github.io/UniHttp)


# 4、赞赏
-------

纯个人维护，为爱发电， 如果有任何问题或者需求请提issue，会很快修复和发版

开源不易，目前待业中，如果觉得有用可以微信扫码鼓励支持下作者感谢!🙏


 <img src="docs/img/weChatShare.png" width = 200 height = 200 />




