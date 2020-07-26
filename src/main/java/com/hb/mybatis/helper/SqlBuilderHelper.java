package com.hb.mybatis.helper;

import com.hb.mybatis.annotation.Table;
import com.hb.unic.util.tool.Assert;
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
public class SqlBuilderHelper {

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
    public static final String WHERE_TRUE = " where 1=1";

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
     * 根据实体类获取表名
     *
     * @param entityClass 实体类
     * @param <T>         实体类类型
     * @return 表名
     */
    public static <T> String getTableName(Class<T> entityClass) {
        Table entityClassAnnotation = entityClass.getAnnotation(Table.class);
        Assert.notNull(entityClassAnnotation, entityClass + " without annotation of com.hb.mybatis.annotation.Table");
        String tableName = entityClassAnnotation.value();
        Assert.notBlank(tableName, "cannot get tableName from " + entityClass);
        return tableName;
    }

    /**
     * 把map的key转换为驼峰形式的key
     *
     * @param property 待转换的map
     */
    public static void convertToUnderlineMap(Map<String, Object> property) {
        if (property == null || property.isEmpty()) {
            return;
        }
        Map<String, Object> humpProperty = new HashMap<>();
        property.forEach((key, value) -> {
            humpProperty.put(StringUtils.hump2Underline(key), value);
        });
        property.clear();
        property.putAll(humpProperty);
    }

    /**
     * 把list中map中的key为下划线转换为驼峰
     *
     * @param list 待转换的list
     */
    public static void convertToHumpMapList(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<Map<String, Object>> humpMapList = new ArrayList<>();
        list.forEach(map -> {
            Map<String, Object> humpMap = new HashMap<>();
            map.forEach((key, value) -> {
                humpMap.put(StringUtils.underline2Hump(key), value);
            });
            humpMapList.add(humpMap);
        });
        list.clear();
        list.addAll(humpMapList);
    }

    /**
     * map中的key转换为驼峰
     *
     * @param map 待转换的map
     */
    public static void convertToHumpMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        List<Map<String, Object>> humpMapList = new ArrayList<>();
        Map<String, Object> humpMap = new HashMap<>();
        map.forEach((key, value) -> {
            humpMap.put(StringUtils.underline2Hump(key), value);
        });
        map.clear();
        map.putAll(humpMap);
    }

}
