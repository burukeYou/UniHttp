
# 1、引入依赖
建议使用最新版本 [版本列表](https://central.sonatype.com/artifact/io.github.burukeyou/uniapi-http/versions)

```xml
    <dependency>
      <groupId>io.github.burukeyou</groupId>
      <artifactId>uniapi-http</artifactId>
      <version>0.2.3</version>
    </dependency>
```


如果是非spring环境，请手动再依赖spring-context,  否则请忽略

```xml
 <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.3.23</version>
</dependency>
```


# 2、接口定义


```java
@HttpApi(url = "http://localhost:8080/user-web")
interface UserHttpAPI {
    
   @GetHttpInterface("/info/getUser")
   UserInfo getUserInfo();

}
```

1. **声明接口类**：使用`@HttpApi(url = "http://localhost:8080/user-web")`注解定义HTTP接口元信息
   - `url`属性指定服务端基准地址
2. **定义接口方法**：在方法上使用`@GetHttpInterface("/info/getUser")`注解 去 绑定GET请求路径
3. **指定返回值类型**：方法返回类型对应HTTP响应体的反序列化目标类型
   - 默认使用fastjson2进行反序列化（可配置其他JSON框架）
   - 若无响应内容可定义为`void`类型



至此我们就对接了 一个请求方式为 Get 、 请求地址为 "http://localhost:8080/user-web", 响应体内容为 UserInfo 的Http接口



# 3、包扫描路径

```java

/**
 *  httApi的包扫描路径
 */
@UniAPIScan("com.xxxx.http.api")
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class,args);
    }
}

```

在Spring配置类上使用`@UniAPIScan`注解配置扫描路径，框架会自动完成以下操作：
1. 扫描指定包路径下所有带`@HttpApi`注解的接口
2. 生成接口的动态代理实现类, 并代理对象注册到Spring容器中
3. 代理类主要逻辑就是屏蔽底层Http请求从 发送 -> 响应 -> 反序列化 的全部过程




使用示例（通过@Autowired自动注入即可使用）：

```java
@Service
class UserService {
    
    // 代理对象已由Spring容器管理
    @Autowired
    private UserHttpAPI userHttpApi;
    
} 

```

