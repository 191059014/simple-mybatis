package com.hb.mybatis.base;

import com.hb.mybatis.annotation.Column;
import com.hb.mybatis.annotation.Table;
import com.hb.mybatis.common.Consts;
import com.hb.mybatis.enums.QueryType;
import com.hb.mybatis.helper.*;
import com.hb.mybatis.mapper.BaseMapper;
import com.hb.mybatis.model.PageResult;
import com.hb.unic.logger.util.LogExceptionWapper;
import com.hb.unic.util.tool.Assert;
import com.hb.unic.util.util.CloneUtils;
import com.hb.unic.util.util.ReflectUtils;
import com.hb.unic.util.util.StringUtils;
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
        Where where = Where.build().addSingle(QueryType.EQUAL, pk, id);
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
        Assert.notHasText(bk, "NoBusinessKey: " + entityClass.getName());
        Where where = Where.build().addSingle(QueryType.EQUAL, bk, businessKey);
        List<T> tList = this.selectList(null, where, null, null, null);
        return CollectionUtils.isEmpty(tList) ? null : tList.get(0);
    }

    /**
     * 条件查询单条数据
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @return 单条数据
     */
    @Override
    public T selectOne(Object whereCondition) {
        List<T> tList = this.selectList(null, whereCondition, null, null, null);
        return CollectionUtils.isEmpty(tList) ? null : tList.get(0);
    }

    /**
     * 条件查询数据集合
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @return 数据集合
     */
    @Override
    public List<T> selectList(Object whereCondition) {
        return selectList(null, whereCondition, null, null, null);
    }

    /**
     * 条件查询数据集合
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @param sort           排序
     * @return 数据集合
     */
    @Override
    public List<T> selectList(Object whereCondition, String sort) {
        return selectList(null, whereCondition, sort, null, null);
    }

    /**
     * 条件查询数据集合
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @param sort           排序
     * @param startRow       开始行
     * @param pageSize       每页条数
     * @return 数据集合
     */
    @Override
    public List<T> selectList(Object whereCondition, String sort, Integer startRow, Integer pageSize) {
        return selectList(null, whereCondition, sort, startRow, pageSize);
    }

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
    @Override
    public List<T> selectList(String resultColumns, Object whereCondition, String sort, Integer startRow, Integer pageSize) {
        Where where = getWhereFromObject(whereCondition);
        String simpleSql = getSimpleSql(resultColumns, where, sort, startRow, pageSize);
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(simpleSql, where == null ? null : where.getWhereParams());
        List<Map<String, Object>> propertyMap = convertColumnsNameToPropertyName(queryResult);
        return CloneUtils.maps2Beans(propertyMap, entityClass);
    }

    /**
     * 查询总条数
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @return 总条数
     */
    @Override
    public int selectCount(Object whereCondition) {
        Where where = getWhereFromObject(whereCondition);
        return baseMapper.selectCount(getCountSql(where), where == null ? null : where.getWhereParams());
    }

    /**
     * 分页查询集合
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @param startRow       开始行
     * @param pageSize       每页条数
     * @return 分页集合
     */
    @Override
    public PageResult<T> selectPages(Object whereCondition, Integer startRow, Integer pageSize) {
        return selectPages(null, whereCondition, null, startRow, pageSize);
    }

    /**
     * 分页查询集合
     *
     * @param whereCondition where条件，只能是com.hb.mybatis.sql.Where或者T类型
     * @param sort           排序
     * @param startRow       开始行
     * @param pageSize       每页条数
     * @return 分页集合
     */
    @Override
    public PageResult<T> selectPages(Object whereCondition, String sort, Integer startRow, Integer pageSize) {
        return selectPages(null, whereCondition, sort, startRow, pageSize);
    }

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
    @Override
    public PageResult<T> selectPages(String resultColumns, Object whereCondition, String sort, Integer startRow, Integer pageSize) {
        Assert.notNull(sort, "startRow is null");
        Assert.notNull(sort, "pageSize is null");
        Where where = getWhereFromObject(whereCondition);
        int count = selectCount(where);
        List<T> list = selectList(resultColumns, where, sort, startRow, pageSize);
        return new PageResult<>(list, count, startRow, pageSize);
    }

    /**
     * 自定义sql语句动态查询，要求写全where前面的sql
     *
     * @param sqlStatementBeforeWhere where前面的sql语句
     * @param where                   where条件
     * @return 结果集合
     */
    @Override
    public List<Map<String, Object>> customSelect(String sqlStatementBeforeWhere, Where where) {
        Assert.notHasText(sqlStatementBeforeWhere, "sqlStatementBeforeWhere is null");
        String fullSql = sqlStatementBeforeWhere + where == null ? "" : where.getWhereSql();
        return baseMapper.dynamicSelect(fullSql, where == null ? null : where.getWhereParams());
    }

    /**
     * 选择性插入
     *
     * @param entity 实体类对象
     * @return 插入行数
     */
    @Override
    public int insertBySelective(T entity) {
        Assert.notNull(entity, "entity of insert is null");
        Map<String, Object> property = CloneUtils.bean2Map(entity);
        Map<String, Object> columnMap = convertPropertyNameToColumnName(property);
        String sqlStatement = Insert.buildSelectiveSql(this.tableName, columnMap);
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
    public int updateBySelective(T entity, Where where) {
        Assert.ifTrueThrows(where == null || where.getWhereSql() == null || "".equals(where.getWhereSql()), "where conditions is empty");
        Assert.notNull(entity, "entity is null");
        Map<String, Object> property = CloneUtils.bean2Map(entity);
        Map<String, Object> columnMap = convertPropertyNameToColumnName(property);
        String sqlStatement = Update.buildSelectiveSql(this.tableName, columnMap, where);
        return baseMapper.updateBySelective(sqlStatement, property, where.getWhereParams());
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
        Assert.notNull(entity, "entity of update is null");
        Where where = Where.build().addSingle(QueryType.EQUAL, pk, id);
        return updateBySelective(entity, where);
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
        Assert.notHasText(bk, "NoBusinessKey: " + entityClass.getName());
        Assert.notNull(businessKey, "businessKey is null");
        Assert.notNull(entity, "entity of update is null");
        Where where = Where.build().addSingle(QueryType.EQUAL, bk, businessKey);
        return updateBySelective(entity, where);
    }

    /**
     * 条件删除（物理删除）
     *
     * @param where 条件
     * @return 删除行数
     * @see com.hb.mybatis.base.IDmlMapper#logicDelete(com.hb.mybatis.helper.Where)
     */
    @Override
    public int deleteBySelective(Where where) {
        Assert.ifTrueThrows(where == null || where.getWhereSql() == null || "".equals(where.getWhereSql()), "where conditions of delete is empty");
        String sqlStatement = Delete.buildSelectiveSql(this.tableName, where);
        return baseMapper.deleteBySelective(sqlStatement, where.getWhereParams());
    }

    /**
     * 通过主键删除（物理删除）
     *
     * @param id id集合
     * @return 单条数据
     * @see com.hb.mybatis.base.IDmlMapper#logicDeleteByPk(java.lang.Object)
     */
    @Override
    public int deleteByPk(PK id) {
        Assert.notNull(id, "id is null");
        Where where = Where.build().addSingle(QueryType.EQUAL, pk, id);
        return deleteBySelective(where);
    }

    /**
     * 通过业务主键删除（物理删除）
     *
     * @param businessKey 业务主键
     * @return 单条数据
     * @see com.hb.mybatis.base.IDmlMapper#logicDeleteByBk(java.lang.Object)
     */
    @Override
    public int deleteByBk(BK businessKey) {
        Assert.notHasText(bk, "NoBusinessKey: " + entityClass.getName());
        Assert.notNull(businessKey, "businessKey is null");
        Where where = Where.build().addSingle(QueryType.EQUAL, bk, businessKey);
        return deleteBySelective(where);
    }

    /**
     * 条件删除（逻辑删除）
     *
     * @param where 条件
     * @return 删除行数
     */
    @Override
    public int logicDelete(Where where) {
        Assert.ifTrueThrows(where == null || where.getWhereSql() == null || "".equals(where.getWhereSql()), "where conditions is empty");
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
     * 通过主键删除（逻辑删除）
     *
     * @param id id主键
     * @return 删除行数
     */
    @Override
    public int logicDeleteByPk(PK id) {
        Assert.notNull(id, "id is null");
        Where where = Where.build().addSingle(QueryType.EQUAL, pk, id);
        return logicDelete(where);
    }

    /**
     * 通过业务主键删除（逻辑删除）
     *
     * @param businessKey 业务主键
     * @return 删除行数
     */
    @Override
    public int logicDeleteByBk(BK businessKey) {
        Assert.notHasText(bk, "NoBusinessKey: " + entityClass.getName());
        Assert.notNull(businessKey, "businessKey is null");
        Where where = Where.build().addSingle(QueryType.EQUAL, bk, businessKey);
        return logicDelete(where);
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
                columnName = !"".equals(columnName) ? value : columnName;
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
        Assert.notHasText(pk, "NoPrimaryKey: " + entityClass.getName());
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
     * 获取完整的sql，包含排序，包含分页
     *
     * @return sql
     */
    private String getSimpleSql(String resultColumns, Where where, String sort, Integer startRow, Integer pageSize) {
        String resultColumnsSql = "*";
        if (resultColumns != null && !"".equals(resultColumns)) {
            resultColumnsSql = resultColumns;
        }
        String simpleSql = StringUtils.joint("select ", resultColumnsSql, " from ", tableName);
        simpleSql += where == null ? "" : where.getWhereSql();
        simpleSql += StringUtils.hasText(sort) ? " order by " + sort : "";
        if (startRow != null && pageSize != null) {
            simpleSql += " limit " + SqlBuilder.createSingleParamSql("startRow") + "," + SqlBuilder.createSingleParamSql("pageSize");
            if (where != null) {
                where.addSingleParam("startRow", startRow).addSingleParam("pageSize", pageSize);
            }
        }
        return simpleSql;
    }

    /**
     * 获取总条数sql
     *
     * @return sql
     */
    private String getCountSql(Where where) {
        String countSql = StringUtils.joint("select count(1) from ", tableName);
        if (where != null) {
            countSql += where.getWhereSql();
        }
        return countSql;
    }

    /**
     * 从Object解析where条件
     *
     * @param whereCondition 条件对象
     * @return Where
     */
    private Where getWhereFromObject(Object whereCondition) {
        if (whereCondition == null) {
            return null;
        }
        if (whereCondition instanceof Where) {
            return (Where) whereCondition;
        } else if (entityClass.getName().equals(whereCondition.getClass().getName())) {
            Map<String, Object> propertyMap = CloneUtils.bean2Map(whereCondition);
            Map<String, Object> columnMap = convertPropertyNameToColumnName(propertyMap);
            return Where.build().addAll(columnMap);
        } else {
            Assert.ifTrueThrows(true, "whereCondition must be " + Where.class.getName() + " or " + entityClass.getName());
            return null;
        }
    }

}
