# simple-mybatis
让简单的crud操作告别繁琐的sql（可以防sql注入），同时也支持自己写mybatis的xml文件。
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
```
public <T> List<T> dynamicSelect(Class<T> entityClass, QueryCondition query)
public <T> int selectCount(QueryCondition query)
public <T> PagesResult<T> selectPages(Class<T> entityClass, QueryCondition query)
public <T> List<T> customSelect(String sqlStatement, Class<T> entityClass, Map<String, Object> conditions)
public <T> int insertBySelective(String tableName, T entity)
public <T> int updateBySelective(String tableName, T entity, Map<String, Object> conditions)
public int deleteBySelective(String tableName, Map<String, Object> conditions)
```
## 示例
```
QueryCondition queryCondition = QueryCondition.build("t_user")
                .addCondition(QueryType.EQUALS, "age", 18)
                .addCondition(QueryType.LIKE, "user_name", "zhangsan")
                .addCondition(QueryType.IN, "sex", Arrays.asList("M", "F"))
                .orderBy("createTime desc")
                .limit(1, 10);
List<User> userList = dmlMapper.dynamicSelect(User.class, queryCondition);
PagesResult<List<User>> pageResult = dmlMapper.selectPages(User.class, queryCondition);
```
## 其他
- 如果数据库字段为user_name，实体类字段为userName，则调用以上api的时候需要在mybatis-config.xml增加驼峰映射配置：
```
<configuration>
    <settings>
        // 允许驼峰映射 
        <setting name="mapUnderscoreToCamelCase" value="true"/>
    </settings>
    // 自定义map类型的驼峰映射逻辑 
    <objectWrapperFactory type="com.hb.mybatis.config.MapWrapperFactory"/>
</configuration>
```

