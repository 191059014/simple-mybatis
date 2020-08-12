package com.hb.mybatis.util;

import com.hb.mybatis.sql.Where;
import com.hb.unic.util.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 常量
 *
 * @author Mr.huang
 * @since 2020/5/8 13:19
 */
public class SqlUtils {

    /**
     * 生成#{params.paramName}字符串
     *
     * @param paramName 参数名
     * @return 字符串
     */
    public static String createSingleParamSql(String paramName) {
        return StringUtils.joint("#{params.", paramName, "}");
    }

    /**
     * 生成#{cloumns.paramName}字符串
     *
     * @param paramName 参数名
     * @return 字符串
     */
    public static String createSingleColumnSql(String paramName) {
        return StringUtils.joint("#{cloumns.", paramName, "}");
    }

    /**
     * 获取总条数sql
     *
     * @return sql
     */
    public static String getCountSql(String tableName, Where where) {
        String countSql = StringUtils.joint("select count(1) from ", tableName);
        if (where != null) {
            countSql += where.getWhereSql();
        }
        return countSql;
    }

    /**
     * 获取完整的sql，包含排序，包含分页
     *
     * @return sql
     */
    public static String getSimpleSql(String tableName, String resultColumns, Where where, String sort, Integer startRow, Integer pageSize) {
        String resultColumnsSql = "*";
        if (resultColumns != null && !"".equals(resultColumns)) {
            resultColumnsSql = resultColumns;
        }
        String simpleSql = StringUtils.joint("select ", resultColumnsSql, " from ", tableName);
        if (where != null) {
            simpleSql += where.getWhereSql();
        }
        if (sort != null && !"".equals(sort)) {
            simpleSql += " order by " + sort;
        }
        if (startRow != null && startRow > 0 && pageSize != null && pageSize > 0) {
            simpleSql += " limit " + SqlUtils.createSingleParamSql("startRow") + "," + SqlUtils.createSingleParamSql("pageSize");
        }
        return simpleSql;
    }

    /**
     * 把数据库查询结果映射为实体类字段
     *
     * @param queryResult 查询结果
     */
    public static void convertColumnsNameToPropertyName(List<Map<String, Object>> queryResult, Map<String, String> column2PropertyMap) {
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
    public static void convertPropertyNameToColumnName(Map<String, Object> property, Map<String, String> property2ColumnMap) {
        Map<String, Object> columnMap = new HashMap<>();
        property.forEach((key, value) -> {
            columnMap.put(property2ColumnMap.get(key), value);
        });
        property.clear();
        property.putAll(columnMap);
    }

}
