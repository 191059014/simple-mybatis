package com.hb.mybatis.base;

import com.hb.mybatis.helper.Where;
import com.hb.unic.util.util.Pagination;

import java.util.List;
import java.util.Map;

/**
 * mapper超级接口
 *
 * @version v0.1, 2020/9/2 17:47, create by huangbiao.
 */
public interface IDmlMapper<T, PK, BK> {

    /**
     * 根据数据库主键查询
     *
     * @param id 数据库主键
     * @return 单条数据
     */
    T selectByPk(PK id);

    /**
     * 根据业务主键查询
     *
     * @param businessKey 业务主键
     * @return 单条数据
     */
    T selectByBk(BK businessKey);

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
    Pagination<T> selectPages(Object whereCondition, Integer startRow, Integer pageSize);

    /**
     * 分页查询集合
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @param sort           排序
     * @param startRow       开始行
     * @param pageSize       每页条数
     * @return 分页集合
     */
    Pagination<T> selectPages(Object whereCondition, String sort, Integer startRow, Integer pageSize);

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
    Pagination<T> selectPages(String resultColumns, Object whereCondition, String sort, Integer startRow, Integer pageSize);

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
    int insert(T entity);

    /**
     * 选择性更新
     *
     * @param entity 实体类对象
     * @param where  条件
     * @return 更新行数
     */
    int update(T entity, Where where);

    /**
     * 通过ID更新
     *
     * @param id     id
     * @param entity 更新的信息
     * @return 单条数据
     */
    int updateByPk(PK id, T entity);

    /**
     * 通过业务主键更新
     *
     * @param businessKey id
     * @param entity      需要更新的信息
     * @return 影响行数
     */
    int updateByBk(BK businessKey, T entity);

    /**
     * 条件删除（物理删除）
     *
     * @param where 条件
     * @return 删除行数
     * @see com.hb.mybatis.base.IDmlMapper#logicDelete(com.hb.mybatis.helper.Where)
     */
    int delete(Where where);

    /**
     * 通过主键删除（物理删除）
     *
     * @param id id集合
     * @return 单条数据
     * @see com.hb.mybatis.base.IDmlMapper#logicDeleteByPk(java.lang.Object)
     */
    int deleteByPk(PK id);

    /**
     * 通过业务主键删除（物理删除）
     *
     * @param businessKey 业务主键
     * @return 单条数据
     * @see com.hb.mybatis.base.IDmlMapper#logicDeleteByBk(java.lang.Object)
     */
    int deleteByBk(BK businessKey);

    /**
     * 条件删除（逻辑删除）
     *
     * @param where 条件
     * @return 删除行数
     */
    int logicDelete(Where where);

    /**
     * 通过主键删除（逻辑删除）
     *
     * @param id id主键
     * @return 删除行数
     */
    int logicDeleteByPk(PK id);

    /**
     * 通过业务主键删除（逻辑删除）
     *
     * @param businessKey 业务主键
     * @return 删除行数
     */
    int logicDeleteByBk(BK businessKey);

}

    