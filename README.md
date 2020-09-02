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
- 实体类继承DmlMapper，就拥有了DmlMapper类所有方法
```
public class UserDao extends DmlMapper<User>
```
DmlMapper类方法如下:  
```
T selectById(Object id)
T selectOne(Object whereCondition)
List<T> selectList(Object whereCondition)
List<T> selectList(Object whereCondition, String sort)
List<T> selectList(Object whereCondition, String sort, Integer startRow, Integer pageSize)
List<T> selectList(String resultColumns, Object whereCondition, String sort, Integer startRow, Integer pageSize)
int selectCount(Object whereCondition)
PageResult<T> selectPages(Object whereCondition, Integer startRow, Integer pageSize)
PageResult<T> selectPages(Object whereCondition, String sort, Integer startRow, Integer pageSize)
PageResult<T> selectPages(String resultColumns, Object whereCondition, String sort, Integer startRow, Integer pageSize)
List<Map<String, Object>> customSelect(String sqlStatementBeforeWhere, Where where)
int insertBySelective(T entity)
int updateBySelective(T entity, Where where)
int updateById(Object id, T entity)
int deleteBySelective(Where where)
int deleteById(Object id)
int logicDelete(Where where)
int logicDeleteById(Object id)
```
## 实体类加@Table，@Column，@Id注解，配置表名、字段映射、主键
其中，@Table和@Id注解是必须的，要设置表名和id主键；  

## 其他
更多实用的方法、小工具，请重点查看DmlMapper，SqlBuilder，Where，Delete，Insert，Update等类。

