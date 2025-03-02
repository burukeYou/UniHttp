



# 1、二进制上传
`@BodyBinaryPar注解` 用于标记Http请求体内容为二进制形式: 对应content-type为 `application/octet-stream`

**支持的方法参数类型说明**： InputStream、File、InputStreamSource、byte[]

```java
    @PostHttpInterface
    BaseRsp<String> getUser(@BodyBinaryPar InputStream value,         
                            @BodyBinaryPar File user,                   
                            @BodyBinaryPar InputStreamSource map);    
```



# 2、表单上传
`BodyMultiPartPar注解` 用于标记Http请求体内容为复杂表单形式, 并自动设置请求的Content-type为 `multipart/form-data`

**支持的方法参数类型说明**：
- **单个表单参数**： 普通值、`文件类型`（File、InputStream、byte[]、内置的HttpFile对象）
- **多个表单参数**：自定义对象（自动展开字段）、 Map（自动展开键值对）、集合、数组


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

                    @BodyMultiPartPar UploadReq req,                   // 自定义对象
                    @BodyMultiPartPar Map<String,Object> map // Map
                    )      
```




## 2.1、表单结构生成规则

**1. 参数类型处理**：
- **普通值/文件类型**： 按单个表单字段处理
- **对象/Map类型**：自动展开为多个表单字段，若字段值为文件类型则自动识别为文件字段

**2. 文件上传特性**：
- 通过注解`@BodyMultiPartPar`可分别指定表单字段名和上传文件名
- 使用的内置`HttpFile`对象支持在调用时动态传参设置文件名，避免在注解中硬编码


## 2.2、生成示例

1、对于 `@BodyMultiPartPar("name") String value` 生成的表单为 name=value(文本类型)

2、对于 `@BodyMultiPartPar("logoImg") InputStream value` 生成的表单为 logoImg=value(文件类型)

3、对于  `@BodyMultiPartPar("name") String[] valueArr`  生成的表单为 
```
  name=valueArr[0](文本类型)
  name=valueArr[1](文本类型)
  name=valueArr[2](文本类型)
```

即对于集合或者数组类型，会将每个元素生成一个表单键值对， 键的名字均是注解配置的参数名或者方法参数名。
而对于Map类型，会自动展开所有键值对作为表单的键值对


4、对于自定义对象，`@BodyMultiPartPar UploadReq req`， 会将所有的字段名和字段值转为为表单键值对,比如有如下类

```java
public class UploadReq {

    String name = "张三";

    @BodyMultiPartPar("img1") // 指定生成的表单的key名，否则默认为字段名
    File userImg;

    @BodyMultiPartPar(value = "logoX", fileName="log.xlsx") // 指定上传的文件名   
    File[] logoImg;

}
```

上面UploadReq类生成的表单结构如下：
```
    name=张三
    img1=userImg字段值
    logoX=logoImg[0](文件类型)（上传文件名：log.xlsx）
    logoX=logoImg[1](文件类型)（上传文件名：log.xlsx）
    logoX=logoImg[1](文件类型)（上传文件名：log.xlsx）
```


## 2.3、指定上传文件名
@BodyMultiPartPar注解里支持指定上传的文件名，如果需要在调用时动态传上传文件名，则推荐使用内置的HttpFile对象上传指定

