package com.hb.mybatis.core.impl;

import com.alibaba.fastjson.JSON;
import com.hb.mybatis.annotation.Column;
import com.hb.mybatis.annotation.Table;
import com.hb.mybatis.core.IDmlMapper;
import com.hb.mybatis.mapper.BaseMapper;
import com.hb.mybatis.toolkit.SqlTemplate;
import com.hb.mybatis.toolkit.Where;
import com.hb.unic.util.tool.Assert;
import com.hb.unic.util.util.CloneUtils;
import com.hb.unic.util.util.Pagination;
import com.hb.unic.util.util.ReflectUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ========== dml操作数据库类 ==========
 *
 * @author Mr.huang
 * @version com.hb.cp.dao.base.DmlMapperImpl.java, v1.0
 * @date 2019年10月12日 14时09分
 */
public class DmlMapperImpl<T> implements IDmlMapper<T> {

    /**
     * the constant logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DmlMapperImpl.class);

    /**
     * mapper基类
     */
    @Resource
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
     * 主键字段名
     */
    private String idColumnName;

    /**
     * 实体字段 -> 数据库字段映射集合
     */
    private Map<String, String> property2ColumnMap = new HashMap<>(16);

    /**
     * 数据库字段 -> 实体字段映射集合
     */
    private Map<String, String> column2PropertyMap = new HashMap<>(16);

    @Override
    public T selectById(Serializable id) {
        Where where = Where.build().equal(idColumnName, id);
        List<T> tList = this.selectList(null, where, null, null, null);
        return CollectionUtils.isEmpty(tList) ? null : tList.get(0);
    }

    @Override
    public T selectOne(Where where) {
        List<T> tList = this.selectList(null, where, null, null, null);
        return CollectionUtils.isEmpty(tList) ? null : tList.get(0);
    }

    @Override
    public List<T> selectList(Where where) {
        return selectList(null, where, null, null, null);
    }

    @Override
    public List<T> selectList(Where where, String sort) {
        return selectList(null, where, sort, null, null);
    }

    @Override
    public List<T> selectList(Where where, String sort, Integer startRow, Integer pageSize) {
        return selectList(null, where, sort, startRow, pageSize);
    }

    @Override
    public List<T> selectList(String resultColumns, Where where, String sort, Integer startRow, Integer pageSize) {
        String simpleSql =
            SqlTemplate.getSimpleSql(tableName, resultColumns, where.getWhereSql(), sort, startRow, pageSize);
        where.addParam("startRow", startRow);
        where.addParam("pageSize", pageSize);
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(simpleSql, where.getWhereParams());
        List<Map<String, Object>> propertyMap = convertColumnsNameToPropertyName(queryResult);
        return JSON.parseArray(JSON.toJSONString(propertyMap), entityClass);
    }

    @Override
    public int selectCount(Where where) {
        String countSql = SqlTemplate.getCountSql(tableName, where.getWhereSql());
        Map<String, Object> params = where.getWhereParams();
        return baseMapper.selectCount(countSql, params);
    }

    @Override
    public Pagination<T> selectPages(Where where, Integer startRow, Integer pageSize) {
        return selectPages(null, where, null, startRow, pageSize);
    }

    @Override
    public Pagination<T> selectPages(Where where, String sort, Integer startRow, Integer pageSize) {
        return selectPages(null, where, sort, startRow, pageSize);
    }

    @Override
    public Pagination<T> selectPages(String resultColumns, Where where, String sort, Integer startRow,
        Integer pageSize) {
        Assert.notNull(startRow, "startRow is null");
        Assert.notNull(pageSize, "pageSize is null");
        int count = selectCount(where);
        List<T> list = selectList(resultColumns, where, sort, startRow, pageSize);
        return new Pagination<>(list, count, startRow, pageSize);
    }

    @Override
    public List<Map<String, Object>> customSelect(String sqlStatement, Map<String, Object> conditionMap) {
        Assert.hasText(sqlStatement, "sqlStatementBeforeWhere is null");
        return baseMapper.dynamicSelect(sqlStatement, conditionMap);
    }

    @Override
    public List<T> customSelect(String sqlStatement, Map<String, Object> conditionMap, Class<T> tClass) {
        Assert.notNull(tClass, "target class is null");
        List<Map<String, Object>> mapList = customSelect(sqlStatement, conditionMap);
        return JSON.parseArray(JSON.toJSONString(mapList), tClass);
    }

    @Override
    public int insert(T entity) {
        Assert.notNull(entity, "entity is null");
        Map<String, Object> property = CloneUtils.bean2Map(entity);
        Map<String, Object> columnMap = convertPropertyNameToColumnName(property);
        String sqlStatement = SqlTemplate.createInsertSql(this.tableName, columnMap);
        return baseMapper.insertSelective(sqlStatement, columnMap);
    }

    @Override
    public int updateById(Serializable id, T entity) {
        Assert.notNull(id, "id is null");
        return update(Where.build().equal(idColumnName, id), entity);
    }

    @Override
    public int update(Where where, T entity) {
        Assert.notNull(where, "where condition is null");
        Assert.notNull(entity, "entity is null");
        return updateByMap(where, CloneUtils.bean2Map(entity));
    }

    @Override
    public int updateByMap(Where where, Map<String, Object> propertyMap) {
        Assert.notNull(where, "where condition is null");
        Assert.notNull(propertyMap, "propertyMap is null");
        Map<String, Object> columnMap = convertPropertyNameToColumnName(propertyMap);
        String sqlStatement = SqlTemplate.createUpdateSql(this.tableName, columnMap, where);
        return baseMapper.updateBySelective(sqlStatement, columnMap, where.getWhereParams());
    }

    @Override
    public int deleteById(Serializable id) {
        Assert.notNull(id, "id is null");
        return delete(Where.build().and().equal(idColumnName, id));
    }

    @Override
    public int delete(Where where) {
        Assert.notNull(where, "where condition is null");
        String deleteSql = SqlTemplate.createDeleteSql(tableName, where);
        return baseMapper.deleteBySelective(deleteSql, where.getWhereParams());
    }

    @PostConstruct
    public void init() {
        /*
         * 获取泛型类
         */
        Type type = getClass().getGenericSuperclass();
        Assert.ifTrueThrows(!(type instanceof ParameterizedType), "NoEntityType: " + type);
        entityClass = ((Class)((ParameterizedType)type).getActualTypeArguments()[0]);
        /*
         * 获取表名
         */
        Table table = entityClass.getAnnotation(Table.class);
        Assert.notNull(table, "EmptyTableName: " + entityClass.getName());
        tableName = table.value();
        Field[] allFields = ReflectUtils.getAllFields(entityClass);
        for (Field field : allFields) {
            String propertyName = field.getName();
            Column column = field.getAnnotation(Column.class);
            String columnName = propertyName;
            if (column != null) {
                String value = column.value();
                columnName = !"".equals(value) ? value : columnName;
                if (column.isPk()) {
                    idColumnName = columnName;
                }
            }
            // 字段映射
            property2ColumnMap.put(propertyName, columnName);
            // 字段映射
            column2PropertyMap.put(columnName, propertyName);
        }
        Assert.hasText(idColumnName, "NoPrimaryKey: " + entityClass.getName());
        Assert.ifTrueThrows(property2ColumnMap.isEmpty() || column2PropertyMap.isEmpty(),
            "NoFieldEntity: " + entityClass.getName());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("load table [{}] -> [{}]", tableName, entityClass);
        }
    }

    /**
     * 把数据库查询结果映射为实体类字段
     *
     * @param queryResult
     *            数据库字段集合
     * @return list 实体类字段集合
     */
    private List<Map<String, Object>> convertColumnsNameToPropertyName(List<Map<String, Object>> queryResult) {
        List<Map<String, Object>> propertyMapList = new ArrayList<>();
        queryResult.forEach(map -> {
            Map<String, Object> rowMap = new HashMap<>();
            map.forEach((key, value) -> {
                rowMap.put(column2PropertyMap.get(key), value);
            });
            propertyMapList.add(rowMap);
        });
        return propertyMapList;
    }

    /**
     * 把实体类字段映射为数据库查询结果
     *
     * @param property
     *            实体类字段集合
     * @return map 数据库字段集合
     */
    private Map<String, Object> convertPropertyNameToColumnName(Map<String, Object> property) {
        Map<String, Object> columnMap = new HashMap<>();
        property.forEach((key, value) -> {
            columnMap.put(property2ColumnMap.get(key), value);
        });
        return columnMap;
    }

}
