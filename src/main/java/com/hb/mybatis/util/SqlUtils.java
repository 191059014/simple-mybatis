package com.hb.mybatis.util;

import com.hb.mybatis.sql.Where;

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

    // 列名前缀
    public static final String COLUMS_NAME = "columns";

    // 参数名前缀
    public static final String PARAMS_NAME = "params";

    // 点
    public static final String DOT = ".";

    // 逗号
    public static final String COMMA = ",";

    // 美元符号
    public static final String DOLLAR_SYMBOL = "#";

    // 左小括号
    public static final String LEFT_SMALL_BRACKET = "(";

    // 右小括号
    public static final String RIGHT_SMALL_BRACKET = ")";

    // 左中括号
    public static final String LEFT_MIDDLE_BRACKET = "{";

    // 右中括号
    public static final String RIGHT_MIDDLE_BRACKET = "}";

    // 百分号
    public static final String PERCENT = "%";

    // and符号
    public static final String AND = " and ";

    // order by
    public static final String ORDER_BY = " order by ";

    // limit
    public static final String LIMIT = " limit ";

    // startRows
    public static final String START_ROWS = "startRows";

    // pageSize
    public static final String PAGE_SIZE = "pageSize";

    // 等于符号
    public static final String EQUALS = "=";

    // 单引号
    public static final String SINGLE_QUOTATION_MARK = "'";

    // 字符串连接
    public static final String CONCAT = "concat";

    // where语句
    public static final String WHERE_TRUE = " where 1=1 ";

    /**
     * 生成#{params.paramName}字符串
     *
     * @param paramName 参数名
     * @return 字符串
     */
    public static String createSingleParamSql(String paramName) {
        return DOLLAR_SYMBOL + LEFT_MIDDLE_BRACKET + PARAMS_NAME + DOT + paramName + RIGHT_MIDDLE_BRACKET;
    }

    /**
     * 生成#{cloumns.paramName}字符串
     *
     * @param paramName 参数名
     * @return 字符串
     */
    public static String createSingleColumnSql(String paramName) {
        return DOLLAR_SYMBOL + LEFT_MIDDLE_BRACKET + COLUMS_NAME + DOT + paramName + RIGHT_MIDDLE_BRACKET;
    }

    /**
     * 获取总条数sql
     *
     * @return sql
     */
    public static String getCountSql(String tableName, Where where) {
        String countSql = "select count(1) from" + tableName;
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
    public static String getSimpleSql(String tableName, Where where, String sort, int startRow, int pageSize) {
        String simpleSql = "select * from " + tableName;
        if (where != null) {
            simpleSql += where.getWhereSql();
        }
        if (sort != null && !"".equals(sort)) {
            simpleSql += " order by " + sort;
        }
        if (startRow > 0 && pageSize > 0) {
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
