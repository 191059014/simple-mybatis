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
package com.hb.mybatis.core;

import com.hb.mybatis.toolkit.Where;
import com.hb.unic.util.util.Pagination;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * mapper超级接口
 *
 * @version v0.1, 2020/9/2 17:47, create by huangbiao.
 */
public interface IDmlMapper<T> {

    /**
     * 根据数据库主键查询
     *
     * @param id
     *            数据库主键
     * @return 结果
     */
    T selectById(Serializable id);

    /**
     * 条件查询单条数据
     *
     * @param whereCondition
     *            where条件
     * @return 结果
     */
    T selectOne(Where whereCondition);

    /**
     * 条件查询数据集合
     *
     * @param whereCondition
     *            where条件
     * @return 结果
     */
    List<T> selectList(Where whereCondition);

    /**
     * 条件查询数据集合
     *
     * @param whereCondition
     *            where条件
     * @param sort
     *            排序
     * @return 结果
     */
    List<T> selectList(Where whereCondition, String sort);

    /**
     * 条件查询数据集合
     *
     * @param whereCondition
     *            where条件
     * @param sort
     *            排序
     * @param startRow
     *            开始行
     * @param pageSize
     *            每页条数
     * @return 结果
     */
    List<T> selectList(Where whereCondition, String sort, Integer startRow, Integer pageSize);

    /**
     * 条件查询数据集合
     *
     * @param resultColumns
     *            结果集对应所有列，多个用逗号分隔
     * @param whereCondition
     *            where条件
     * @param sort
     *            排序
     * @param startRow
     *            开始行
     * @param pageSize
     *            每页条数
     * @return 数据集合
     */
    List<T> selectList(String resultColumns, Where whereCondition, String sort, Integer startRow, Integer pageSize);

    /**
     * 查询总条数
     *
     * @param whereCondition
     *            where条件
     * @return 总条数
     */
    int selectCount(Where whereCondition);

    /**
     * 分页查询集合
     *
     * @param whereCondition
     *            where条件
     * @param startRow
     *            开始行
     * @param pageSize
     *            每页条数
     * @return 分页集合
     */
    Pagination<T> selectPages(Where whereCondition, Integer startRow, Integer pageSize);

    /**
     * 分页查询集合
     *
     * @param whereCondition
     *            where条件
     * @param sort
     *            排序
     * @param startRow
     *            开始行
     * @param pageSize
     *            每页条数
     * @return 分页集合
     */
    Pagination<T> selectPages(Where whereCondition, String sort, Integer startRow, Integer pageSize);

    /**
     * 分页查询集合
     *
     * @param resultColumns
     *            结果集对应所有列，多个用逗号分隔
     * @param whereCondition
     *            where条件
     * @param sort
     *            排序
     * @param startRow
     *            开始行
     * @param pageSize
     *            每页条数
     * @return 分页集合
     */
    Pagination<T> selectPages(String resultColumns, Where whereCondition, String sort, Integer startRow,
        Integer pageSize);

    /**
     * 自定义sql语句动态查询，要求写全where前面的sql
     *
     * @param sqlStatement
     *            where前面的sql语句
     * @param conditionMap
     *            查询条件取值集合
     * @return 结果集合
     */
    List<Map<String, Object>> customSelect(String sqlStatement, Map<String, Object> conditionMap);

    /**
     * 自定义sql语句动态查询，要求写全where前面的sql
     *
     * @param sqlStatement
     *            where前面的sql语句
     * @param conditionMap
     *            查询条件值集合
     * @param tClass
     *            将结果集转换的类
     * @return T
     */
    List<T> customSelect(String sqlStatement, Map<String, Object> conditionMap, Class<T> tClass);

    /**
     * 选择性插入
     *
     * @param entity
     *            实体类对象
     * @return 插入行数
     */
    int insert(T entity);

    /**
     * 通过ID更新
     *
     * @param id
     *            id
     * @param entity
     *            更新的信息
     * @return 影响的行数
     */
    int updateById(Serializable id, T entity);

    /**
     * 选择性更新
     *
     * @param where
     *            条件
     * @param entity
     *            实体类对象
     * @return 更新行数
     */
    int update(Where where, T entity);

    /**
     * 通过map更新
     *
     * @param where
     *            条件
     * @param propertyMap
     *            更新的信息
     * @return 影响的行数
     */
    int updateByMap(Where where, Map<String, Object> propertyMap);

    /**
     * 通过主键删除
     * 
     * @param id
     *            主键
     * @return 影响的行数
     */
    int deleteById(Serializable id);

    /**
     * 通过Where删除
     * 
     * @param where
     *            where条件对象
     * @return 影响的行数
     */
    int delete(Where where);

}

    
```
## 实体类加@Table，@Column注解，配置表名、字段映射
其中，@Table和@Column注解是必须的，要设置表名和id主键；  

## 其他
更多实用的方法、小工具，请重点查看IDmlMapper接口，Where，SqlTemplate等类。

