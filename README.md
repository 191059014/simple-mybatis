# simple-mybatis
让简单的crud操作告别繁琐的sql，同时也支持自己写mybatis的xml文件。
## 引入方法
首先下载项目，然后依赖pom文件，配置扫描config/service-simplemybatis-context.xml。
- 依赖pom文件
```
<dependency>
    <groupId>com.hb.mybatis</groupId>
    <artifactId>simple-mybatis</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
- 注入DmlMapper对象
```
@Autowired
private DmlMapper dmlMapper;
```
DmlMapper对象方法如下:  
<img src="https://github.com/191059014/simple-mybatis/tree/master/src/main/resources/image/dmlMapper.jpg" width="50%">
