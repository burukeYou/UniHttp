

# 1、简介
UniHttp提供了 `@SslCfg `注解，支持配置SSL的证书、keyStore、使用的加密套件、协议版本，以及支持`单向`和`双向`的SSL认证配置。   并且无论是证书和keyStore都支持直接配置文件内容或者文件路径。 `并且该注解的所有属性值都支持配置成环境变量`

该注解可配置到具体接口类上，自定义的@HttpApi注解上、方法上等等 . 比如

```java
// 具体HttpApi接口类上
@SslCfg 
@HttpApi
interfacte UserSeviceAPI {
}

// 方法上 
 @GetHttpInterface("/ssl-web/get01")
 @SslCfg 
 BaseRsp<String> get01(@QueryPar("name") String name);

// 自定义的@HttpApi组合注解上
@HttpApi
@SslCfg 
public @interface MyHttpApi {

}
```

#  2、单向认证
单向认证支持直接配置 证书certificate 或者 密钥库keysotre。 并且支持配置成文件路径、或者文件base64内容 ,  会动态识别和取值。

配法1、使用证书文件配置信任证书:
```java
// 方式1） 配置证书文件的内容
@SslCfg(trustCertificate = "信任证书文件的base64内容")

// 方式2） 配置证书文件的路径
@SslCfg(trustCertificate = "classpath:ssl/server.crt")

// 方式3）配置证书的环境变量，从环境变量取值. 
// 请确保配置了该环境变量为证书文路径件或者证书内容
@SslCfg(trustCertificate = "${channel.mtuan.ssl.cert}")

```

配法2、没有证书文件，也可以使用keystore文件配置信任证书.

```java
@SslCfg(
    // 配置keystore文件路径 （当前同上也可配置文件base64内容、或者环境变量）
	trustStore = "classpath:ssl/server01.p12",
	// keyspre文件类型。 在jdk8不配默认是 jks . 而在jdk11默认是 PKCS12
	trustStoreType="PKCS12",
	// keystore文件密码
	trustStorePassword = "文件密码"
)
```

配法3、如果啥证书也没有，也可以配置直接关闭SSL验证。


```java
@SslCfg(
    // 关闭信任证书校验、会信任所有证书  
	closeCertificateTrustVerify=true,
	// 关闭域名校验，信任所有域名。  否则会校验证书SAN或者CN
	closeHostnameVerify=true
)
```


# 3、双向认证
双向认证相比单向认证需要多提供一个keystore文件并且里面包含客户端自己私钥和公钥证书， 所以信任证书的配置此处不再描述，同单向认证一致。

配法1、分别配置 证书和私钥
```java
@SslCfg(
        // 客户端的公钥证书    （当前同上也可配置文件base64内容、或者环境变量）
        certificate = "classpath:ssl/client.crt",
        // 客户端的私钥文件      （当前同上也可配置文件base64内容、或者环境变量）
        certificatePrivateKey = "classpath:ssl/client.key"
)
```


配法2、如果已经放到keysotre文件里也可以直接配置keystore密钥库文件
```java
@SslCfg(
        // keystore文件路径    （当前同上也可配置文件base64内容、或者环境变量）
        keyStore = "classpath:ssl2/ca_client.pkcs12",
        // keyspre文件类型。 在jdk8不配默认是 jks . 而在jdk11默认是 PKCS12
        keyStoreType = "PKCS12",
        // keystore文件密码
        keyStorePassword = "文件密码",
        // 使用的key条目， 如果不配置默认使用第一个密钥条目。
        keyAlias = "key条目别名",
        // key条目密码
        keyPassword = "key条目密码"
)
```


#  4、自定义HttpClient时处理

如果说提供的 @SslCfg配置注解满足不了，也可通过[自定义OkHttp客户端](pages/guide/httpClient.md)在里面加载SSL协议的逻辑， 这个不再描述具体看其官网。
