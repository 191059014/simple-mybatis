package com.hb.mybatis.base;

import com.hb.mybatis.annotation.Column;
import com.hb.mybatis.helper.DeleteHelper;
import com.hb.mybatis.helper.InsertHelper;
import com.hb.mybatis.helper.QueryCondition;
import com.hb.mybatis.helper.UpdateHelper;
import com.hb.mybatis.helper.WhereCondition;
import com.hb.mybatis.mapper.BaseMapper;
import com.hb.mybatis.model.PagesResult;
import com.hb.mybatis.util.SqlBuilderUtils;
import com.hb.unic.util.tool.Assert;
import com.hb.unic.util.util.CloneUtils;
import com.hb.unic.util.util.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
     * 实体类名和数据库列名映射的缓存
     */
    private final Map<String, Map<String, String>> fieldColumnMappingsCache = new ConcurrentHashMap(64);

    /**
     * mapper基类
     */
    @Autowired
    private BaseMapper baseMapper;

    /**
     * 通过QueryCondition来查询
     *
     * @param entityClass 实体类
     * @param query       QueryCondition查询对象
     * @param <T>         实体类
     * @return 集合
     */
    public <T> List<T> dynamicSelect(Class<T> entityClass, QueryCondition query) {
        query.setTableName(SqlBuilderUtils.getTableName(entityClass));
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(query.getSimpleSql(), query.getParams());
        queryResult.forEach((map -> this.handleFieldColumnMapping(entityClass, map, false)));
        return CloneUtils.maps2Beans(queryResult, entityClass);
    }

    /**
     * 查询总条数
     *
     * @param entityClass 实体类
     * @param query       查询条件
     * @return 总条数
     */
    public <T> int selectCount(Class<T> entityClass, QueryCondition query) {
        query.setTableName(SqlBuilderUtils.getTableName(entityClass));
        return baseMapper.selectCount(query.getCountSql(), query.getParams());
    }

    /**
     * 分页查询集合
     *
     * @param entityClass 实体类
     * @param query       查询对象
     * @return 分页集合
     */
    public <T> PagesResult<T> selectPages(Class<T> entityClass, QueryCondition query) {
        query.setTableName(SqlBuilderUtils.getTableName(entityClass));
        int count = baseMapper.selectCount(query.getCountSql(), query.getParams());
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(query.getFullSql(), query.getParams());
        queryResult.forEach((map -> this.handleFieldColumnMapping(entityClass, map, false)));
        List<T> tList = CloneUtils.maps2Beans(queryResult, entityClass);
        return new PagesResult<>(tList, count, query.getLimitStartRows(), query.getLimitPageSize());
    }

    /**
     * 自定义sql语句动态查询，要求写全where前面的sql
     *
     * @param sqlStatementBeforeWhere where前面的sql语句
     * @param whereCondition          where条件
     * @return 结果集合
     */
    public List<Map<String, Object>> customSelect(String sqlStatementBeforeWhere, WhereCondition whereCondition) {
        String fullSql = sqlStatementBeforeWhere + whereCondition.getWhereSql();
        return baseMapper.dynamicSelect(fullSql, whereCondition.getWhereParams());
    }

    /**
     * 选择性插入
     *
     * @param entity 实体类对象
     * @return 插入行数
     */
    public <T> int insertBySelective(T entity) {
        Assert.assertNotNull(entity, "insertBySelective: entity cannot be null");
        Map<String, Object> property = ReflectUtils.getAllFieldsExcludeStatic(entity);
        this.handleFieldColumnMapping(entity.getClass(), property, true);
        String sqlStatement = InsertHelper.buildInsertSelectiveSql(SqlBuilderUtils.getTableName(entity.getClass()), property);
        return baseMapper.insertSelective(sqlStatement, property);
    }

    /**
     * 选择性更新
     *
     * @param entity         实体类对象
     * @param whereCondition 条件
     * @return 更新行数
     */
    public <T> int updateBySelective(T entity, WhereCondition whereCondition) {
        Assert.assertNotNull(entity, "updateBySelective: entity cannot be null");
        Assert.assertTrueThrows(whereCondition.isEmpty(), "updateBySelective: where conditions cannot empty");
        Map<String, Object> property = ReflectUtils.getAllFieldsExcludeStatic(entity);
        this.handleFieldColumnMapping(entity.getClass(), property, true);
        String sqlStatement = UpdateHelper.buildUpdateSelectiveSql(SqlBuilderUtils.getTableName(entity.getClass()), property, whereCondition);
        return baseMapper.updateBySelective(sqlStatement, property, whereCondition.getWhereParams());
    }

    /**
     * 选择性删除
     *
     * @param entityClass    实体类对象
     * @param whereCondition 条件
     * @return 删除行数
     */
    public <T> int deleteBySelective(Class<T> entityClass, WhereCondition whereCondition) {
        Assert.assertTrueThrows(whereCondition.isEmpty(), "deleteBySelective: where conditions cannot empty");
        String sqlStatement = DeleteHelper.buildDeleteSelectiveSql(SqlBuilderUtils.getTableName(entityClass), whereCondition);
        return baseMapper.deleteBySelective(sqlStatement, whereCondition.getWhereParams());
    }

    /**
     * 填充实体类字段名和数据库字段的映射
     *
     * @param entityClass     实体类
     * @param waitTransferMap 等待被转换的map集合
     * @param getColumn       是获取表中的列名，还是获取实体类字段名
     * @param <T>             实体类类型
     */
    private <T> void handleFieldColumnMapping(Class<T> entityClass, Map<String, Object> waitTransferMap, boolean getColumn) {
        Map<String, String> fieldColumnMappings = this.fieldColumnMappingsCache.get(entityClass.getName());
        if (fieldColumnMappings == null) {
            fieldColumnMappings = new HashMap<>(16);
            Field[] allFields = ReflectUtils.getAllFields(entityClass);
            for (Field field : allFields) {
                String columnName = field.getName();
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    columnName = column.value();
                }
                fieldColumnMappings.put(field.getName(), columnName);
            }
            this.fieldColumnMappingsCache.put(entityClass.getName(), fieldColumnMappings);
        }
        Map<String, Object> columnMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : waitTransferMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String finalKey = fieldColumnMappings.get(key);
            if (!getColumn) {
                for (Map.Entry<String, String> fieldColumnEntry : fieldColumnMappings.entrySet()) {
                    if (key.equals(fieldColumnEntry.getValue())) {
                        finalKey = fieldColumnEntry.getKey();
                        break;
                    }
                }
            }
            columnMap.put(finalKey, value);
        }
        waitTransferMap.clear();
        waitTransferMap.putAll(columnMap);
    }

}
