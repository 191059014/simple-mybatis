package com.hb.mybatis.base;

import com.hb.mybatis.annotation.Column;
import com.hb.mybatis.annotation.Id;
import com.hb.mybatis.annotation.Table;
import com.hb.mybatis.common.Consts;
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
     * 表名
     */
    private String tableName;

    /**
     * 实体类
     */
    private Class<T> entityClass;

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
     * 条件查询单条数据
     *
     * @param ID id集合
     * @return 单条数据
     */
    public T selectById(ID id) {
        Query query = Query.build();
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
        convertColumnsNameToPropertyName(queryResult, this.entityClass.getName());
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
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(query.getFullSql(this.tableName), query.getParams());
        convertColumnsNameToPropertyName(queryResult, this.entityClass.getName());
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
     * 选择性插入
     *
     * @param entity 实体类对象
     * @return 插入行数
     */
    public int insertBySelective(T entity) {
        Assert.notNull(entity, "entity of insert is null");
        Map<String, Object> property = CloneUtils.bean2Map(entity);
        convertPropertyNameToColumnName(property, this.entityClass.getName());
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
        convertPropertyNameToColumnName(property, this.entityClass.getName());
        String sqlStatement = Update.buildSelectiveSql(this.tableName, property, where);
        return baseMapper.updateBySelective(sqlStatement, property, where.getWhereParams());
    }

    /**
     * 条件查询单条数据
     *
     * @param ID id集合
     * @return 单条数据
     */
    public T updateById(ID id) {
        Query query = Query.build();
        List<T> tList = this.selectList(query);
        return CollectionUtils.isEmpty(tList) ? null : tList.get(0);
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
     * @param ID id集合
     * @return 单条数据
     */
    public T deleteById(ID id) {
        Query query = Query.build();
        List<T> tList = this.selectList(query);
        return CollectionUtils.isEmpty(tList) ? null : tList.get(0);
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

    /**
     * 逻辑删除
     *
     * @param whereCondition 条件
     * @return 删除行数
     */
    public int logicDeleteById(ID id) {
        try {
            T t = entityClass.newInstance();
            ReflectUtils.setPropertyValue(Consts.RECORD_STATUS, Consts.RECORD_STATUS_INVALID, t);
            return updateBySelective(t, Where.build());
        } catch (Exception e) {
            LOGGER.error("logicDelete error：{}", LogExceptionWapper.getStackTrace(e));
            return 0;
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        /**
         * 获取泛型类
         */
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            this.entityClass = ((Class) ((ParameterizedType) type).getActualTypeArguments()[0]);
        } else {
            throw new Exception("NoEntityType");
        }
        // 获取表名
        Table table = this.entityClass.getAnnotation(Table.class);
        if (table != null) {
            this.tableName = table.value();
        } else {
            throw new Exception("EmptyTableName: " + this.entityClass.getName());
        }
        Field[] allFields = ReflectUtils.getAllFields(this.entityClass);
        Stream.of(allFields).forEach(field -> {
            String propertyName = field.getName();
            String columnName = field.getName();
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columnName = column.value();
            }
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                columnName = id.value();
                this.primaryKey = columnName;
            }
            // 字段映射
            this.property2ColumnMap.put(propertyName, columnName);
            // 字段映射
            this.column2PropertyMap.put(columnName, propertyName);
        });
        if (this.primaryKey == null) {
            throw new Exception("NonePrimaryKey: " + this.entityClass.getName());
        }
    }

    /**
     * 把数据库查询结果映射为实体类字段
     *
     * @param queryResult 查询结果
     */
    public void convertColumnsNameToPropertyName(List<Map<String, Object>> queryResult, String className) {
        List<Map<String, Object>> propertyMapList = new ArrayList<>();
        queryResult.forEach(map -> {
            Map<String, Object> rowMap = new HashMap<>();
            map.forEach((key, value) -> {
                rowMap.put(column2PropertyMap.get(key), value);
            });
            propertyMapList.add(rowMap);
        });
        queryResult.clear();
        queryResult.addAll(propertyMapList);
    }

    /**
     * 把实体类字段映射为数据库查询结果
     *
     * @param property 属性集合
     */
    public void convertPropertyNameToColumnName(Map<String, Object> property, String className) {
        Map<String, Object> columnMap = new HashMap<>();
        property.forEach((key, value) -> {
            columnMap.put(property2ColumnMap.get(key), value);
        });
        property.clear();
        property.putAll(columnMap);
    }


}
