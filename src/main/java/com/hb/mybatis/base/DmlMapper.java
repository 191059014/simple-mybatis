package com.hb.mybatis.base;

import com.hb.mybatis.annotation.Column;
import com.hb.mybatis.annotation.Id;
import com.hb.mybatis.annotation.Table;
import com.hb.mybatis.common.Consts;
import com.hb.mybatis.helper.EntityMetaCache;
import com.hb.mybatis.helper.QueryType;
import com.hb.mybatis.mapper.BaseMapper;
import com.hb.mybatis.model.PageResult;
import com.hb.mybatis.sql.Delete;
import com.hb.mybatis.sql.Insert;
import com.hb.mybatis.sql.Query;
import com.hb.mybatis.sql.Update;
import com.hb.mybatis.sql.Where;
import com.hb.mybatis.util.SqlUtils;
import com.hb.unic.logger.util.LogExceptionWapper;
import com.hb.unic.util.tool.Assert;
import com.hb.unic.util.util.CloneUtils;
import com.hb.unic.util.util.ReflectUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * ========== dml操作数据库类 ==========
 *
 * @author Mr.huang
 * @version com.hb.cp.dao.base.DmlMapper.java, v1.0
 * @date 2019年10月12日 14时09分
 */
public class DmlMapper<ID, T> implements InitializingBean {

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
     * 实体类
     */
    private String entityName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 主键字段名
     */
    private String primaryKey;

    /**
     * 实体字段 -> 数据库字段映射集合
     */
    private Map<String, String> property2ColumnMap = new HashMap<>(16);

    /**
     * 数据库字段 -> 实体字段映射集合
     */
    private Map<String, String> column2PropertyMap = new HashMap<>(16);

    /**
     * 实体信息缓存
     */
    private static final Map<String, EntityMetaCache> CACHE = new HashMap<>();

    /**
     * 获取类信息
     *
     * @param className 类名
     * @return 类信息
     */
    public static EntityMetaCache getEntityMeta(String className) {
        EntityMetaCache entityMetaCache = CACHE.get(className);
        if (entityMetaCache == null) {
            LOGGER.info("{} is not managed", className);
        }
        return entityMetaCache;
    }

    /**
     * 条件查询单条数据
     *
     * @param id id集合
     * @return 单条数据
     */
    public T selectById(ID id) {
        Query query = Query.build();
        query.add(QueryType.EQUALS, primaryKey, id);
        List<T> tList = this.selectList(query);
        return CollectionUtils.isEmpty(tList) ? null : tList.get(0);
    }

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
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(query.getSimpleSql(this.tableName), query.getParams());
        convertColumnsNameToPropertyName(queryResult);
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
        return baseMapper.selectCount(query.getCountSql(this.tableName), query.getParams());
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
        int count = baseMapper.selectCount(query.getCountSql(this.tableName), query.getParams());
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(query.getSimpleSql(this.tableName), query.getParams());
        convertColumnsNameToPropertyName(queryResult);
        List<T> tList = CloneUtils.maps2Beans(queryResult, entityClass);
        return new PageResult<>(tList, count, query.getStartRow(), query.getPageSize());
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
     * 选择性插入
     *
     * @param entity 实体类对象
     * @return 插入行数
     */
    public int insertBySelective(T entity) {
        Assert.notNull(entity, "entity of insert is null");
        Map<String, Object> property = CloneUtils.bean2Map(entity);
        convertPropertyNameToColumnName(property);
        String sqlStatement = Insert.buildSelectiveSql(this.tableName, property);
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
        Assert.ifTrueThrows(where == null || where.getWhereSql() == null || "".equals(where.getWhereSql()), "where conditions of update is empty");
        Map<String, Object> property = CloneUtils.bean2Map(entity);
        convertPropertyNameToColumnName(property);
        String sqlStatement = Update.buildSelectiveSql(this.tableName, property, where);
        return baseMapper.updateBySelective(sqlStatement, property, where.getWhereParams());
    }

    /**
     * 条件查询单条数据
     *
     * @param id     id
     * @param entity 更新的信息
     * @return 单条数据
     */
    public int updateById(ID id, T entity) {
        Where where = Where.build().add(QueryType.EQUALS, primaryKey, id);
        return updateBySelective(entity, where);
    }

    /**
     * 选择性删除，物理删除，逻辑删除请使用logicDelete
     *
     * @param where 条件
     * @return 删除行数
     */
    public int deleteBySelective(Where where) {
        Assert.notNull(entityClass, "entityClass is null");
        Assert.ifTrueThrows(where == null || where.getWhereSql() == null || "".equals(where.getWhereSql()), "where conditions of delete is empty");
        String sqlStatement = Delete.buildSelectiveSql(this.tableName, where);
        return baseMapper.deleteBySelective(sqlStatement, where.getWhereParams());
    }

    /**
     * 条件查询单条数据
     *
     * @param id id集合
     * @return 单条数据
     */
    public int deleteById(ID id) {
        Where where = Where.build().add(QueryType.EQUALS, primaryKey, id);
        return deleteBySelective(where);
    }

    /**
     * 逻辑删除
     *
     * @param where 条件
     * @return 删除行数
     */
    public int logicDelete(Where where) {
        try {
            T t = entityClass.newInstance();
            ReflectUtils.setPropertyValue(Consts.RECORD_STATUS, Consts.RECORD_STATUS_INVALID, t);
            return updateBySelective(t, where);
        } catch (Exception e) {
            LOGGER.error("logicDelete error：{}", LogExceptionWapper.getStackTrace(e));
            return 0;
        }

    }

    /**
     * 逻辑删除
     *
     * @param id id主键
     * @return 删除行数
     */
    public int logicDeleteById(ID id) {
        Where where = Where.build().add(QueryType.EQUALS, primaryKey, id);
        return logicDelete(where);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        /**
         * 获取泛型类
         */
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            entityClass = ((Class) ((ParameterizedType) type).getActualTypeArguments()[0]);
        } else {
            throw new Exception("NoEntityType: " + type);
        }
        entityName = entityClass.getName();
        // 获取表名
        Table table = entityClass.getAnnotation(Table.class);
        if (table == null) {
            throw new Exception("EmptyTableName: " + entityName);
        }
        Field[] allFields = ReflectUtils.getAllFields(entityClass);
        for (int i = 0; i < allFields.length; i++) {
            Field field = allFields[i];
            String propertyName = field.getName();
            String columnName = field.getName();
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columnName = column.value();
            }
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                columnName = id.value();
                primaryKey = columnName;
            }
            // 字段映射
            property2ColumnMap.put(propertyName, columnName);
            // 字段映射
            column2PropertyMap.put(columnName, propertyName);
        }

        Stream.of(allFields).forEach(field -> {

        });
        if (primaryKey == null) {
            throw new Exception("NonePrimaryKey: " + entityName);
        }
        if (property2ColumnMap.isEmpty() || column2PropertyMap.isEmpty()) {
            throw new Exception("NoneFieldEntity: " + entityName);
        }
        CACHE.put(entityName, new EntityMetaCache(entityName, table.value(), primaryKey, property2ColumnMap, column2PropertyMap));
    }

    /**
     * 把数据库查询结果映射为实体类字段
     *
     * @param queryResult 查询结果
     */
    private void convertColumnsNameToPropertyName(List<Map<String, Object>> queryResult) {
        SqlUtils.convertColumnsNameToPropertyName(queryResult, column2PropertyMap);
    }

    /**
     * 把实体类字段映射为数据库查询结果
     *
     * @param property 属性集合
     */
    private void convertPropertyNameToColumnName(Map<String, Object> property) {
        SqlUtils.convertPropertyNameToColumnName(property, property2ColumnMap);
    }

}
