package com.hb.mybatis.util;

import com.hb.mybatis.annotation.Table;
import com.hb.unic.util.tool.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量
 *
 * @author Mr.huang
 * @since 2020/5/8 13:19
 */
public class SqlBuilderUtils {

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
     * 组装条件集合
     *
     * @param key   key
     * @param value value
     * @return map
     */
    public static Map<String, Object> createSingleConditionMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /**
     * 生成${params.paramName}字符串
     *
     * @param paramName 参数名
     * @return 字符串
     */
    public static String createSingleParamSql(String paramName) {
        return DOLLAR_SYMBOL + LEFT_MIDDLE_BRACKET + PARAMS_NAME + DOT + paramName + RIGHT_MIDDLE_BRACKET;
    }

    /**
     * 生成${cloumns.paramName}字符串
     *
     * @param paramName 参数名
     * @return 字符串
     */
    public static String createSingleColumnSql(String paramName) {
        return DOLLAR_SYMBOL + LEFT_MIDDLE_BRACKET + COLUMS_NAME + DOT + paramName + RIGHT_MIDDLE_BRACKET;
    }

    /**
     * 根据实体类获取表名
     *
     * @param entityClass 实体类
     * @param <T>         实体类类型
     * @return 表名
     */
    public static <T> String getTableName(Class<T> entityClass) {
        Table entityClassAnnotation = entityClass.getAnnotation(Table.class);
        Assert.assertNotNull(entityClassAnnotation, "cannot get table name from " + entityClass);
        String tableName = entityClassAnnotation.value();
        Assert.assertNotEmpty(tableName, "cannot get table name from " + entityClass);
        return tableName;
    }

}
