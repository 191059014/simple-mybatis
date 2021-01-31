package com.hb.mybatis.base;

import com.alibaba.fastjson.JSON;
import com.hb.mybatis.annotation.Column;
import com.hb.mybatis.annotation.Table;
import com.hb.mybatis.common.Consts;
import com.hb.mybatis.enums.QueryType;
import com.hb.mybatis.mapper.BaseMapper;
import com.hb.mybatis.tool.SqlTemplate;
import com.hb.mybatis.tool.Where;
import com.hb.unic.util.tool.Assert;
import com.hb.unic.util.util.CloneUtils;
import com.hb.unic.util.util.Pagination;
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

/**
 * ========== dml操作数据库类 ==========
 *
 * @author Mr.huang
 * @version com.hb.cp.dao.base.DmlMapperImpl.java, v1.0
 * @date 2019年10月12日 14时09分
 */
public class DmlMapperImpl<T, PK, BK> implements InitializingBean, IDmlMapper<T, PK, BK> {

    /**
     * the constant logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DmlMapperImpl.class);

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
     * 主键字段名
     */
    private String pk;

    /**
     * 业务主键字段名
     */
    private String bk;

    /**
     * 实体字段 -> 数据库字段映射集合
     */
    private Map<String, String> property2ColumnMap = new HashMap<>(16);

    /**
     * 数据库字段 -> 实体字段映射集合
     */
    private Map<String, String> column2PropertyMap = new HashMap<>(16);

    /**
     * 根据数据库主键查询
     *
     * @param id 数据库主键
     * @return 单条数据
     */
    @Override
    public T selectByPk(PK id) {
        Where where = Where.build().equal(pk, id);
        List<T> tList = this.selectList(null, where, null, null, null);
        return CollectionUtils.isEmpty(tList) ? null : tList.get(0);
    }

    /**
     * 根据业务主键查询
     *
     * @param businessKey 业务主键
     * @return 单条数据
     */
    @Override
    public T selectByBk(BK businessKey) {
        Assert.hasText(bk, "NoBusinessKey: " + entityClass.getName());
        Where where = Where.build().equal(bk, businessKey);
        List<T> tList = this.selectList(null, where, null, null, null);
        return CollectionUtils.isEmpty(tList) ? null : tList.get(0);
    }

    /**
     * 条件查询单条数据
     *
     * @param where where条件
     * @return 单条数据
     */
    @Override
    public T selectOne(Where where) {
        List<T> tList = this.selectList(null, where, null, null, null);
        return CollectionUtils.isEmpty(tList) ? null : tList.get(0);
    }

    /**
     * 条件查询数据集合
     *
     * @param where where条件
     * @return 数据集合
     */
    @Override
    public List<T> selectList(Where where) {
        return selectList(null, where, null, null, null);
    }

    /**
     * 条件查询数据集合
     *
     * @param where where条件
     * @param sort  排序
     * @return 数据集合
     */
    @Override
    public List<T> selectList(Where where, String sort) {
        return selectList(null, where, sort, null, null);
    }

    /**
     * 条件查询数据集合
     *
     * @param where    where条件
     * @param sort     排序
     * @param startRow 开始行
     * @param pageSize 每页条数
     * @return 数据集合
     */
    @Override
    public List<T> selectList(Where where, String sort, Integer startRow, Integer pageSize) {
        return selectList(null, where, sort, startRow, pageSize);
    }

    /**
     * 条件查询数据集合
     *
     * @param resultColumns 结果集对应所有列，多个用逗号分隔
     * @param where         where条件
     * @param sort          排序
     * @param startRow      开始行
     * @param pageSize      每页条数
     * @return 数据集合
     */
    @Override
    public List<T> selectList(String resultColumns, Where where, String sort, Integer startRow, Integer pageSize) {
        String simpleSql = SqlTemplate.getSimpleSql(tableName, resultColumns, where.getWhereSql(), sort, startRow, pageSize);
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(simpleSql, where.getWhereParams());
        List<Map<String, Object>> propertyMap = convertColumnsNameToPropertyName(queryResult);
        return JSON.parseArray(JSON.toJSONString(propertyMap), entityClass);
    }

    /**
     * 查询总条数
     *
     * @param where where条件
     * @return 总条数
     */
    @Override
    public int selectCount(Where where) {
        String countSql = SqlTemplate.getCountSql(tableName, where == null ? "" : where.getWhereSql());
        Map<String, Object> params = where == null ? null : where.getWhereParams();
        return baseMapper.selectCount(countSql, params);
    }

    /**
     * 分页查询集合
     *
     * @param where    where条件
     * @param startRow 开始行
     * @param pageSize 每页条数
     * @return 分页集合
     */
    @Override
    public Pagination<T> selectPages(Where where, Integer startRow, Integer pageSize) {
        return selectPages(null, where, null, startRow, pageSize);
    }

    /**
     * 分页查询集合
     *
     * @param where    where条件
     * @param sort     排序
     * @param startRow 开始行
     * @param pageSize 每页条数
     * @return 分页集合
     */
    @Override
    public Pagination<T> selectPages(Where where, String sort, Integer startRow, Integer pageSize) {
        return selectPages(null, where, sort, startRow, pageSize);
    }

    /**
     * 分页查询集合
     *
     * @param resultColumns 结果集对应所有列，多个用逗号分隔
     * @param where         where条件
     * @param sort          排序
     * @param startRow      开始行
     * @param pageSize      每页条数
     * @return 分页集合
     */
    @Override
    public Pagination<T> selectPages(String resultColumns, Where where, String sort, Integer startRow, Integer pageSize) {
        Assert.notNull(sort, "startRow is null");
        Assert.notNull(sort, "pageSize is null");
        int count = selectCount(where);
        List<T> list = selectList(resultColumns, where, sort, startRow, pageSize);
        return new Pagination<>(list, count, startRow, pageSize);
    }

    /**
     * 自定义sql语句动态查询，要求写全where前面的sql
     *
     * @param sqlStatement where前面的sql语句
     * @param conditionMap 查询条件值集合
     * @return 结果集合
     */
    @Override
    public List<Map<String, Object>> customSelect(String sqlStatement, Map<String, Object> conditionMap) {
        Assert.hasText(sqlStatement, "sqlStatementBeforeWhere is null");
        return baseMapper.dynamicSelect(sqlStatement, conditionMap);
    }

    /**
     * 自定义sql语句动态查询，要求写全where前面的sql
     *
     * @param sqlStatement where前面的sql语句
     * @param conditionMap 查询条件值集合
     * @param tClass       将结果集转换的类
     * @return T
     */
    @Override
    public List<T> customSelect(String sqlStatement, Map<String, Object> conditionMap, Class<T> tClass) {
        List<Map<String, Object>> mapList = customSelect(sqlStatement, conditionMap);
        return JSON.parseArray(JSON.toJSONString(mapList), tClass);
    }

    /**
     * 选择性插入
     *
     * @param entity 实体类对象
     * @return 插入行数
     */
    @Override
    public int insert(T entity) {
        Assert.notNull(entity, "entity of insert is null");
        Map<String, Object> property = CloneUtils.bean2Map(entity);
        Map<String, Object> columnMap = convertPropertyNameToColumnName(property);
        String sqlStatement = SqlTemplate.createInsertSql(this.tableName, columnMap);
        return baseMapper.insertSelective(sqlStatement, columnMap);
    }

    /**
     * 选择性更新
     *
     * @param entity 实体类对象
     * @param where  条件
     * @return 更新行数
     */
    @Override
    public int update(T entity, Where where) {
        Map<String, Object> property = CloneUtils.bean2Map(entity);
        return update(property, where);
    }

    /**
     * 通过ID更新
     *
     * @param id     id
     * @param entity 更新的信息
     * @return 单条数据
     */
    @Override
    public int updateByPk(PK id, T entity) {
        Assert.notNull(id, "id is null");
        Where where = Where.build().equal(pk, id);
        return update(entity, where);
    }

    /**
     * 通过业务主键更新
     *
     * @param businessKey id
     * @param entity      需要更新的信息
     * @return 影响行数
     */
    @Override
    public int updateByBk(BK businessKey, T entity) {
        Assert.hasText(bk, "NoBusinessKey: " + entityClass.getName());
        Assert.notNull(businessKey, "businessKey is null");
        Where where = Where.build().equal(bk, businessKey);
        return update(entity, where);
    }

    /**
     * 条件删除（逻辑删除）
     *
     * @param where 条件
     * @return 删除行数
     */
    @Override
    public int logicDelete(Where where, Map<String, Object> updateProperty) {
        updateProperty.put(Consts.RECORD_STATUS_PROPERTY, Consts.RECORD_INVALID);
        return update(updateProperty, where);
    }

    /**
     * 通过主键删除（逻辑删除）
     *
     * @param id                 id主键
     * @param withUpdateProperty 一同需要被更新的属性
     * @return 删除行数
     */
    @Override
    public int logicDeleteByPk(PK id, Map<String, Object> withUpdateProperty) {
        Assert.notNull(id, "id is null");
        Where where = Where.build().andCondition(QueryType.EQUAL, pk, id);
        return logicDelete(where, withUpdateProperty);
    }

    /**
     * 通过业务主键删除（逻辑删除）
     *
     * @param businessKey        业务主键
     * @param withUpdateProperty 一同需要被更新的属性
     * @return 删除行数
     */
    @Override
    public int logicDeleteByBk(BK businessKey, Map<String, Object> withUpdateProperty) {
        Assert.hasText(bk, "NoBusinessKey: " + entityClass.getName());
        Assert.notNull(businessKey, "businessKey is null");
        Where where = Where.build().andCondition(QueryType.EQUAL, bk, businessKey);
        return logicDelete(where, withUpdateProperty);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        /*
         * 获取泛型类
         */
        Type type = getClass().getGenericSuperclass();
        Assert.ifTrueThrows(!(type instanceof ParameterizedType), "NoEntityType: " + type);
        entityClass = ((Class) ((ParameterizedType) type).getActualTypeArguments()[0]);
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
                    pk = columnName;
                }
                if (column.isBk()) {
                    bk = columnName;
                }
            }
            // 字段映射
            property2ColumnMap.put(propertyName, columnName);
            // 字段映射
            column2PropertyMap.put(columnName, propertyName);
        }
        Assert.hasText(pk, "NoPrimaryKey: " + entityClass.getName());
        Assert.ifTrueThrows(property2ColumnMap.isEmpty() || column2PropertyMap.isEmpty(), "NoFieldEntity: " + entityClass.getName());
    }

    /**
     * 把数据库查询结果映射为实体类字段
     *
     * @param queryResult 数据库字段集合
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
     * @param property 实体类字段集合
     * @return map 数据库字段集合
     */
    private Map<String, Object> convertPropertyNameToColumnName(Map<String, Object> property) {
        Map<String, Object> columnMap = new HashMap<>();
        property.forEach((key, value) -> {
            columnMap.put(property2ColumnMap.get(key), value);
        });
        return columnMap;
    }

    /**
     * 选择性更新
     *
     * @param updateProperty 需要更新的实体类字段集合
     * @param where          条件
     * @return 更新行数
     */
    private int update(Map<String, Object> updateProperty, Where where) {
        Assert.ifTrueThrows(where == null || where.getWhereSql() == null || "".equals(where.getWhereSql()), "where conditions is empty");
        Assert.notNull(updateProperty == null || updateProperty.isEmpty(), "updateProperty is null");
        Map<String, Object> columnMap = convertPropertyNameToColumnName(updateProperty);
        String sqlStatement = SqlTemplate.createUpdateSql(this.tableName, columnMap, where);
        return baseMapper.updateBySelective(sqlStatement, columnMap, where.getWhereParams());
    }

}
