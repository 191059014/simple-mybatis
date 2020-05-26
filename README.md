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
List<T> dynamicSelect(Class<T> entityClass, QueryCondition query)
int selectCount(Class<T> entityClass, QueryCondition query)
PagesResult<T> selectPages(Class<T> entityClass, QueryCondition query)
List<Map<String, Object>> customSelect(String sqlStatementBeforeWhere, WhereCondition whereCondition)
int insertBySelective(T entity)
int updateBySelective(T entity, WhereCondition whereCondition)
int deleteBySelective(Class<T> entityClass, WhereCondition whereCondition)
```
## 示例
```
QueryCondition queryCondition = QueryCondition.build()
                .addCondition(QueryType.EQUALS, "age", 18)
                .addCondition(QueryType.LIKE, "user_name", "zhangsan")
                .addCondition(QueryType.IN, "sex", Arrays.asList("M", "F"))
                .orderBy("createTime desc")
                .limit(1, 10);
List<User> userList = dmlMapper.dynamicSelect(User.class, queryCondition);
PagesResult<List<User>> pageResult = dmlMapper.selectPages(User.class, queryCondition);
```


