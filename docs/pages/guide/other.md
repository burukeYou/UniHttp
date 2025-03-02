




# 自动拆包处理


当接口返回的JSON响应中存在大量**JSON字符串字段（非标准JSON对象）**时，在反序列化时会导致：
- 只能使用String类型接收`JSON字符串字段`
- 需要手动进行二次反序列化非常不便
- 嵌套结构处理尤为繁琐


UniHttp提供自动化拆包方案，只需简单配置即可实现从`JSON字符串` => `JSON对象`的智能转换,以下列典型响应为例：

```json
{
  "son": "{\"detail\":\"{\\\"level\\\":\\\"三年级\\\",\\\"count\\\":3}\"}",
  "id": 1,
  "nums": "[1,2,3,4]",
  "users": "[{\"name\":\"zs01\"},{\"name\":\"zs02\"}]",
  "info": "{\"orderNo\":\"12345\"}",
  "configs": [
    {
      "detail": "{\"id\":3}"
    },
    {
      "detail": "{\"id\":4}"
    }
  ]
}
```

为了能更好反序列化成我们需要的对象类型，可按如下方式进行配置需要进行转化的json字段路径，该路径的json字符串会被自动解析为json对象

```java
    // jsonPathUnPack 配置需要进行转换的json路径
    @PostHttpInterface(path = "/user-web/save")
    @HttpResponseCfg(jsonPathUnPack =  {"$.bbq","$.nums","$.configs[*].detail","$.id","$.info","$.users","$.son","$.son.detail"})
    String del05();
```

经过上述配置后，则上面的原始Json响应会被处理成下面这样，  这样你只需要定义对应的类进行反序列化接收即可

```json
{
    "configs":[
        {"detail":{"id":3}},
        {"detail":{"id":4}}
    ],
    "son":{"detail":{"level":"三年级","count":3}},
    "id":1,
    "nums":[1,2,3,4],
    "users":[
        {"name":"zs01"},
        {"name":"zs02"}
    ],
    "info":{"orderNo":"12345"}
}

```