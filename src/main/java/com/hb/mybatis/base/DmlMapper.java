package com.hb.mybatis.base;

import com.hb.mybatis.SimpleMybatisContext;
import com.hb.mybatis.common.Consts;
import com.hb.mybatis.sql.*;
import com.hb.mybatis.mapper.BaseMapper;
import com.hb.mybatis.model.PageResult;
import com.hb.mybatis.helper.SqlBuilderHelper;
import com.hb.unic.util.tool.Assert;
import com.hb.unic.util.util.CloneUtils;
import com.hb.unic.util.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ========== dml操作数据库类 ==========
 *
 * @author Mr.huang
 * @version com.hb.cp.dao.base.DmlMapper.java, v1.0
 * @date 2019年10月12日 14时09分
 */
@Component
public class DmlMapper {

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
     * 条件查询单条数据
     *
     * @param entityClass 实体类
     * @param query       Query查询对象
     * @param <T>         实体类
     * @return 单条数据
     */
    public <T> T selectOne(Class<T> entityClass, Query query) {
        List<T> tList = this.selectList(entityClass, query);
        return CollectionUtils.isEmpty(tList) ? null : tList.get(0);
    }

    /**
     * 条件查询数据集合
     *
     * @param entityClass 实体类
     * @param query       Query查询对象
     * @param <T>         实体类
     * @return 集合
     */
    public <T> List<T> selectList(Class<T> entityClass, Query query) {
        Assert.notNull(entityClass,"entityClass is null");
        Assert.notNull(query,"query sql is null");
        query.setTableName(SqlBuilderHelper.getTableName(entityClass));
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(query.getSimpleSql(), query.getParams());
        if (SimpleMybatisContext.getBooleanValue(Consts.HUMP_MAPPING)) {
            SqlBuilderHelper.convertToHumpMapList(queryResult);
        }
        return CloneUtils.maps2Beans(queryResult, entityClass);
    }

    /**
     * 查询总条数
     *
     * @param entityClass 实体类
     * @param query       查询条件
     * @return 总条数
     */
    public <T> int selectCount(Class<T> entityClass, Query query) {
        Assert.notNull(entityClass,"entityClass is null");
        Assert.notNull(query,"query sql is null");
        query.setTableName(SqlBuilderHelper.getTableName(entityClass));
        return baseMapper.selectCount(query.getCountSql(), query.getParams());
    }

    /**
     * 分页查询集合
     *
     * @param entityClass 实体类
     * @param query       查询对象
     * @return 分页集合
     */
    public <T> PageResult<T> selectPages(Class<T> entityClass, Query query) {
        Assert.notNull(entityClass,"entityClass is null");
        Assert.notNull(query,"query sql is null");
        query.setTableName(SqlBuilderHelper.getTableName(entityClass));
        int count = baseMapper.selectCount(query.getCountSql(), query.getParams());
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(query.getFullSql(), query.getParams());
        if (SimpleMybatisContext.getBooleanValue(Consts.HUMP_MAPPING)) {
            SqlBuilderHelper.convertToHumpMapList(queryResult);
        }
        List<T> tList = CloneUtils.maps2Beans(queryResult, entityClass);
        return new PageResult<>(tList, count, query.getLimitStartRows(), query.getLimitPageSize());
    }

    /**
     * 自定义sql语句动态查询，要求写全where前面的sql
     *
     * @param sqlStatementBeforeWhere where前面的sql语句
     * @param where                   where条件
     * @return 结果集合
     */
    public List<Map<String, Object>> customSelect(String sqlStatementBeforeWhere, Where where) {
        Assert.notBlank(sqlStatementBeforeWhere,"sqlStatementBeforeWhere is null");
        Assert.notNull(where,"where sql is null");
        String fullSql = sqlStatementBeforeWhere + where.getWhereSql();
        return baseMapper.dynamicSelect(fullSql, where.getWhereParams());
    }

    /**
     * 选择性插入
     *
     * @param entity 实体类对象
     * @return 插入行数
     */
    public <T> int insertBySelective(T entity) {
        Assert.notNull(entity, "entity of insert is null");
        Map<String, Object> property = CloneUtils.bean2Map(entity);
        if (SimpleMybatisContext.getBooleanValue(Consts.HUMP_MAPPING)) {
            SqlBuilderHelper.convertToUnderlineMap(property);
        }
        String sqlStatement = Insert.buildSelectiveSql(SqlBuilderHelper.getTableName(entity.getClass()), property);
        return baseMapper.insertSelective(sqlStatement, property);
    }

    /**
     * 选择性更新
     *
     * @param entity 实体类对象
     * @param where  条件
     * @return 更新行数
     */
    public <T> int updateBySelective(T entity, Where where) {
        Assert.notNull(entity, "entity of update is null");
        Assert.ifTrueThrows(where.isEmpty(), "where conditions of update is empty");
        Map<String, Object> property = CloneUtils.bean2Map(entity);
        if (SimpleMybatisContext.getBooleanValue(Consts.HUMP_MAPPING)) {
            SqlBuilderHelper.convertToUnderlineMap(property);
        }
        String sqlStatement = Update.buildSelectiveSql(SqlBuilderHelper.getTableName(entity.getClass()), property, where);
        return baseMapper.updateBySelective(sqlStatement, property, where.getWhereParams());
    }

    /**
     * 选择性删除，物理删除，逻辑删除请使用updateBySelective
     *
     * @param entityClass 实体类对象
     * @param where       条件
     * @return 删除行数
     */
    public <T> int deleteBySelective(Class<T> entityClass, Where where) {
        Assert.notNull(entityClass,"entityClass is null");
        Assert.ifTrueThrows(where.isEmpty(), "where conditions of delete is empty");
        String sqlStatement = Delete.buildSelectiveSql(SqlBuilderHelper.getTableName(entityClass), where);
        return baseMapper.deleteBySelective(sqlStatement, where.getWhereParams());
    }

}
