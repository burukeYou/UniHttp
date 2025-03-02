

对于文件下载类型的接口，可将方法返回值类型定义为以下三种形式之一：

**返回类型说明**：
- `byte[]`：二进制数组形式  
  ▸ 适合小文件下载  
  ▸ 注意：大文件可能导致内存溢出（完整内容会加载到内存中）

- `File`：本地文件对象  
  ▸ 文件已自动保存到临时目录  (临时目录路径 "/tmp/{当前年月日}/")

- `InputStream`：输入流形式  
  ▸ 支持流式传输（按需读取，节省内存）  
  ▸ 注意：需及时关闭流（避免资源泄漏）




```java
  @GetHttpInterface("/user/download")
  byte[]  downloadUser();
  
  @GetHttpInterface("/user/download")
  File downloadUser();
  
  @GetHttpInterface("/user/download")
  InputStream downloadUser();
```


