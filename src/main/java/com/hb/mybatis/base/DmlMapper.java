package com.hb.mybatis.base;

import com.hb.mybatis.SimpleMybatisContext;
import com.hb.mybatis.common.Consts;
import com.hb.mybatis.helper.SqlBuilderHelper;
import com.hb.mybatis.mapper.BaseMapper;
import com.hb.mybatis.model.PageResult;
import com.hb.mybatis.sql.Delete;
import com.hb.mybatis.sql.Insert;
import com.hb.mybatis.sql.Query;
import com.hb.mybatis.sql.Update;
import com.hb.mybatis.sql.Where;
import com.hb.unic.logger.util.LogExceptionWapper;
import com.hb.unic.util.tool.Assert;
import com.hb.unic.util.util.CloneUtils;
import com.hb.unic.util.util.ReflectUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * ========== dml操作数据库类 ==========
 *
 * @author Mr.huang
 * @version com.hb.cp.dao.base.DmlMapper.java, v1.0
 * @date 2019年10月12日 14时09分
 */
public class DmlMapper<T> implements InitializingBean {

    /**
     * the constant logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DmlMapper.class);

    /**
     * mapper基类
     */
    @Autowired
    private BaseMapper baseMapper;

    /**
     * 实体类
     */
    private Class<T> entityClass;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 条件查询单条数据
     *
     * @param query Query查询对象
     * @return 单条数据
     */
    public T selectOne(Query query) {
        List<T> tList = this.selectList(query);
        return CollectionUtils.isEmpty(tList) ? null : tList.get(0);
    }

    /**
     * 条件查询数据集合
     *
     * @param query Query查询对象
     * @return 集合
     */
    public List<T> selectList(Query query) {
        Assert.notNull(entityClass, "entityClass is null");
        Assert.notNull(query, "query sql is null");
        query.setTableName(tableName);
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(query.getSimpleSql(), query.getParams());
        if (SimpleMybatisContext.getBooleanValue(Consts.HUMP_MAPPING)) {
            SqlBuilderHelper.convertToHumpMapList(queryResult);
        }
        return CloneUtils.maps2Beans(queryResult, entityClass);
    }

    /**
     * 查询总条数
     *
     * @param query 查询条件
     * @return 总条数
     */
    public int selectCount(Query query) {
        Assert.notNull(entityClass, "entityClass is null");
        Assert.notNull(query, "query sql is null");
        query.setTableName(tableName);
        return baseMapper.selectCount(query.getCountSql(), query.getParams());
    }

    /**
     * 分页查询集合
     *
     * @param query 查询对象
     * @return 分页集合
     */
    public PageResult<T> selectPages(Query query) {
        Assert.notNull(entityClass, "entityClass is null");
        Assert.notNull(query, "query sql is null");
        query.setTableName(tableName);
        int count = baseMapper.selectCount(query.getCountSql(), query.getParams());
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(query.getFullSql(), query.getParams());
        if (SimpleMybatisContext.getBooleanValue(Consts.HUMP_MAPPING)) {
            SqlBuilderHelper.convertToHumpMapList(queryResult);
        }
        List<T> tList = CloneUtils.maps2Beans(queryResult, entityClass);
        return new PageResult<>(tList, count, query.getLimitStartRows(), query.getPageSize());
    }

    /**
     * 自定义sql语句动态查询，要求写全where前面的sql
     *
     * @param sqlStatementBeforeWhere where前面的sql语句
     * @param where                   where条件
     * @return 结果集合
     */
    public List<Map<String, Object>> customSelect(String sqlStatementBeforeWhere, Where where) {
        Assert.hasText(sqlStatementBeforeWhere, "sqlStatementBeforeWhere is null");
        Assert.notNull(where, "where sql is null");
        String fullSql = sqlStatementBeforeWhere + where.getWhereSql();
        return baseMapper.dynamicSelect(fullSql, where.getWhereParams());
    }

    /**
     * 自定义sql语句动态查询，要求写全where前面的sql，并按结果类型映射到对应的实体类
     *
     * @param sqlStatementBeforeWhere where前面的sql语句
     * @param where                   where条件
     * @param resultClass             需要将结果映射到实体类
     * @return 结果集合
     */
    public <TO> List<TO> customSelect(String sqlStatementBeforeWhere, Where where, Class<TO> resultClass) {
        List<Map<String, Object>> queryResult = customSelect(sqlStatementBeforeWhere, where);
        if (SimpleMybatisContext.getBooleanValue(Consts.HUMP_MAPPING)) {
            SqlBuilderHelper.convertToHumpMapList(queryResult);
        }
        return CloneUtils.maps2Beans(queryResult, resultClass);
    }

    /**
     * 选择性插入
     *
     * @param entity 实体类对象
     * @return 插入行数
     */
    public int insertBySelective(T entity) {
        Assert.notNull(entity, "entity of insert is null");
        Map<String, Object> property = CloneUtils.bean2Map(entity);
        if (SimpleMybatisContext.getBooleanValue(Consts.HUMP_MAPPING)) {
            SqlBuilderHelper.convertToUnderlineMap(property);
        }
        String sqlStatement = Insert.buildSelectiveSql(tableName, property);
        return baseMapper.insertSelective(sqlStatement, property);
    }

    /**
     * 选择性更新
     *
     * @param entity 实体类对象
     * @param where  条件
     * @return 更新行数
     */
    public int updateBySelective(T entity, Where where) {
        Assert.notNull(entity, "entity of update is null");
        Assert.ifTrueThrows(where.isEmpty(), "where conditions of update is empty");
        Map<String, Object> property = CloneUtils.bean2Map(entity);
        if (SimpleMybatisContext.getBooleanValue(Consts.HUMP_MAPPING)) {
            SqlBuilderHelper.convertToUnderlineMap(property);
        }
        String sqlStatement = Update.buildSelectiveSql(tableName, property, where);
        return baseMapper.updateBySelective(sqlStatement, property, where.getWhereParams());
    }

    /**
     * 选择性删除，物理删除，逻辑删除请使用updateBySelective
     *
     * @param where 条件
     * @return 删除行数
     */
    public int deleteBySelective(Where where) {
        Assert.notNull(entityClass, "entityClass is null");
        Assert.ifTrueThrows(where.isEmpty(), "where conditions of delete is empty");
        String sqlStatement = Delete.buildSelectiveSql(tableName, where);
        return baseMapper.deleteBySelective(sqlStatement, where.getWhereParams());
    }

    /**
     * 逻辑删除
     *
     * @param whereCondition 条件
     * @return 删除行数
     */
    public int logicDelete(Where whereCondition) {
        try {
            T t = entityClass.newInstance();
            ReflectUtils.setPropertyValue(Consts.RECORD_STATUS, Consts.RECORD_STATUS_INVALID, t);
            return updateBySelective(t, whereCondition);
        } catch (Exception e) {
            LOGGER.error("logicDelete error：{}", LogExceptionWapper.getStackTrace(e));
            return 0;
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 获取泛型类
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            this.entityClass = ((Class) ((ParameterizedType) type).getActualTypeArguments()[0]);
        } else {
            throw new Exception("NoEntityType");
        }
        // 获取表名
        this.tableName = SqlBuilderHelper.getTableName(entityClass);
    }

}
