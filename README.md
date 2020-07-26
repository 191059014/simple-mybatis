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
PPagesResult<T> selectPages(Class<T> entityClass, QueryCondition query)
List<Map<String, Object>> customSelect(String sqlStatementBeforeWhere, WhereCondition where)
int insertBySelective(T entity)
int updateBySelective(T entity, WhereCondition where)
int deleteBySelective(Class<T> entityClass, WhereCondition where)
```
## 实体类加@Table，@Column注解，配置表名和字段映射
@Table注解是必须的，要设置表名；  
```$xslt
/**
 * 用户表
 */
@Table("t_user")
public class User {

    // 用户名
    private String userName;

    // 密码
    private String password;

}
注意：项目默认实体类和数据库表字段是驼峰映射，若要取消驼峰映射，则增加配置simple.mybatis.hump_mapping=false
```
## 示例
- 查询
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
- 新增
```
User user = new User();
user.setUserName("zhangsan");
user.setPassword("123456");
int addRows = dmlMapper.insertBySelective(user);
```
- 修改
```
User user = new User();
user.setUserName("zhangsan");
user.setPassword("123456789");
WhereCondition where = WhereCondition.build()
                                    .addCondition(QueryType.EQUALS, "user_name", "zhangsan");
int updateRows = dmlMapper.updateBySelective(user, where);
```
- 删除
```
WhereCondition where = WhereCondition.build()
                                    .addCondition(QueryType.EQUALS, "user_name", "zhangsan");
int deleteRows = dmlMapper.deleteBySelective(CouponConfigDO.class, where);
```
## 其他
更多实用的方法、小工具，请重点查看QueryCondition，QueryType，WhereCondition，SqlBuilderUtils等类。

