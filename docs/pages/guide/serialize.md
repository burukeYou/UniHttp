

# 自定义json序列化方式
在序列化json请求体和反序列化json响应体默认使用的是 `Fastjson2`.    如果想要修改其他序列化方式。可以实现 JsonSerializeConverter 接口，并配置到 @HttpApi注解上

自定义json序列化逻辑

```java
@Component
public class MyJsonSerializeConverter implements JsonSerializeConverter {

    @Override
    public String serialize(Object object) {
        return com.alibaba.fastjson2.JSON.toJSONString(object);
    }

    @Override
    public Object deserialize(String json, Type type) {
        return com.alibaba.fastjson2.JSON.parseObject(json, type);
    }

}

```

配置到@HttpApi注解上

```java
@HttpApi(jsonConverter = MyJsonSerializeConverter.class)
interface UserAPI {

}
```

#  自定义XML序列化方式
在序列化xml请求体和反序列化xml响应体默认使用的是 `JAXB`.    如果想要修改其他序列化方式。可以实现 XmlSerializeConverter 接口，并配置到 @HttpApi注解上

自定义XML序列化逻辑

```java
@Component
public class MyXMLSerializeConverter implements XmlSerializeConverter {

    @Override
    public String serialize(Object object) {
        // todo 
    }

    @Override
    public Object deserialize(String json, Type type) {
       // todo 
    }

}

```

配置到@HttpApi注解上

```java
@HttpApi(xmlConverter = MyXMLSerializeConverter.class)
interface UserAPI {

}
```
