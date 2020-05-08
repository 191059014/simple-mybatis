package com.hb.mybatis.util;

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

    // recordStatus
    public static final String RECORD_STATUS = "recordStatus";

    // recordStatus无效
    public static final int RECORD_STATUS_INVALID = 0;

    // recordStatus有效
    public static final int RECORD_STATUS_VALID = 1;

    // 等于符号
    public static final String EQUALS = "=";

    // 单引号
    public static final String SINGLE_QUOTATION_MARK = "'";

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
     * 生成带“记录状态”为“有效”的sql
     *
     * @return 带有效状态的sql
     */
    public static String createValidRecordStatusSql() {
        return RECORD_STATUS + EQUALS + RECORD_STATUS_VALID;
    }

    /**
     * 生成带“记录状态”为“无效”的sql
     *
     * @return 带有效状态的sql
     */
    public static String createInValidRecordStatusSql() {
        return RECORD_STATUS + EQUALS + RECORD_STATUS_INVALID;
    }

}
