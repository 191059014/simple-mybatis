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
- 子类类继承DmlMapperImpl类，接口也继承IDmlMapper接口，就拥有了DmlMapper类所有方法
```
@Service
public class SysUserServiceImpl extends DmlMapperImpl<SysUserDO> implements ISysUserService {}

public interface ISysUserService extends IDmlMapper<SysUserDO> {}
```
IDmlMapper接口方法如下:  
```
package com.hb.mybatis.base;

import com.hb.mybatis.toolkit.Where;
import com.hb.mybatis.model.PageResult;

import java.util.List;
import java.util.Map;

/**
 * mapper超级接口
 *
 * @version v0.1, 2020/9/2 17:47, create by huangbiao.
 */
public interface IDmlMapper<T> {

    /**
     * 条件查询单条数据
     *
     * @param id id集合
     * @return 单条数据
     */
    T selectById(Object id);

    /**
     * 条件查询单条数据
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @return 单条数据
     */
    T selectOne(Object whereCondition);

    /**
     * 条件查询数据集合
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @return 数据集合
     */
    List<T> selectList(Object whereCondition);

    /**
     * 条件查询数据集合
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @param sort           排序
     * @return 数据集合
     */
    List<T> selectList(Object whereCondition, String sort);

    /**
     * 条件查询数据集合
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @param sort           排序
     * @param startRow       开始行
     * @param pageSize       每页条数
     * @return 数据集合
     */
    List<T> selectList(Object whereCondition, String sort, Integer startRow, Integer pageSize);

    /**
     * 条件查询数据集合
     *
     * @param resultColumns  结果集对应所有列，多个用逗号分隔
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @param sort           排序
     * @param startRow       开始行
     * @param pageSize       每页条数
     * @return 数据集合
     */
    List<T> selectList(String resultColumns, Object whereCondition, String sort, Integer startRow, Integer pageSize);

    /**
     * 查询总条数
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @return 总条数
     */
    int selectCount(Object whereCondition);

    /**
     * 分页查询集合
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @param startRow       开始行
     * @param pageSize       每页条数
     * @return 分页集合
     */
    PageResult<T> selectPages(Object whereCondition, Integer startRow, Integer pageSize);

    /**
     * 分页查询集合
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @param sort           排序
     * @param startRow       开始行
     * @param pageSize       每页条数
     * @return 分页集合
     */
    PageResult<T> selectPages(Object whereCondition, String sort, Integer startRow, Integer pageSize);

    /**
     * 分页查询集合
     *
     * @param resultColumns  结果集对应所有列，多个用逗号分隔
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @param sort           排序
     * @param startRow       开始行
     * @param pageSize       每页条数
     * @return 分页集合
     */
    PageResult<T> selectPages(String resultColumns, Object whereCondition, String sort, Integer startRow, Integer pageSize);

    /**
     * 自定义sql语句动态查询，要求写全where前面的sql
     *
     * @param sqlStatementBeforeWhere where前面的sql语句
     * @param where                   where条件
     * @return 结果集合
     */
    List<Map<String, Object>> customSelect(String sqlStatementBeforeWhere, Where where);

    /**
     * 选择性插入
     *
     * @param entity 实体类对象
     * @return 插入行数
     */
    int insertBySelective(T entity);

    /**
     * 选择性更新
     *
     * @param entity 实体类对象
     * @param where  条件
     * @return 更新行数
     */
    int updateBySelective(T entity, Where where);

    /**
     * 通过ID更新
     *
     * @param id     id
     * @param entity 更新的信息
     * @return 单条数据
     */
    int updateById(Object id, T entity);

    /**
     * 选择性删除，物理删除，逻辑删除请使用logicDelete
     *
     * @param where 条件
     * @return 删除行数
     */
    int deleteBySelective(Where where);

    /**
     * 条件查询单条数据
     *
     * @param id id集合
     * @return 单条数据
     */
    int deleteById(Object id);

    /**
     * 逻辑删除
     *
     * @param where 条件
     * @return 删除行数
     */
    int logicDelete(Where where);

    /**
     * 逻辑删除
     *
     * @param id id主键
     * @return 删除行数
     */
    int logicDeleteById(Object id);

}

    
```
## 实体类加@Table，@Column，@Id注解，配置表名、字段映射、主键
其中，@Table和@Id注解是必须的，要设置表名和id主键；  

## 其他
更多实用的方法、小工具，请重点查看DmlMapper，SqlBuilder，Where，Delete，Insert，Update等类。

