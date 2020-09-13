package com.hb.mybatis.helper;

import com.hb.mybatis.enums.QueryType;
import com.hb.unic.util.tool.Assert;
import com.hb.unic.util.util.StringUtils;

/**
 * 查询类型
 *
 * @author Mr.huang
 * @since 2020/5/8 11:14
 */
public class SqlBuilder {

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
     * 生成#{cloumns.columnName}字符串
     *
     * @param columnName 参数名
     * @return 字符串
     */
    public static String createSingleColumnSql(String columnName) {
        return StringUtils.joint("#{cloumns.", columnName, "}");
    }

    /**
     * 构建单个查询条件
     *
     * @param queryType  查询类型
     * @param columnName 列名
     * @param objArr     参数
     * @return sql
     */
    public static String create(QueryType queryType, String columnName, Object... objArr) {
        String sql = "";
        switch (queryType) {
            case EQUAL:
                sql = equal(columnName);
                break;
            case NOT_EQUAL:
                sql = notEqual(columnName);
                break;
            case MAX_THAN:
                sql = maxThan(columnName);
                break;
            case MIN_THAN:
                sql = minThan(columnName);
                break;
            case MAX_EQUAL_THAN:
                sql = maxEqualThan(columnName);
                break;
            case MIN_EQUAL_THAN:
                sql = minEqualThan(columnName);
                break;
            case LIKE:
                sql = like(columnName);
                break;
            case IN:
                Assert.notNull(objArr, "params is null");
                sql = in(columnName, Integer.parseInt(objArr[0].toString()));
                break;
            case BETWEEN_AND:
                sql = betweenAnd(columnName);
                break;
            default:
                break;
        }
        return sql;
    }

    // 等于
    public static String equal(String columnName) {
        return StringUtils.joint(columnName, " = ", createSingleParamSql(columnName));
    }

    // 不等于
    public static String notEqual(String columnName) {
        return StringUtils.joint(columnName, " != ", createSingleParamSql(columnName));
    }

    // 大于
    public static String maxThan(String columnName) {
        return StringUtils.joint(columnName, " > ", createSingleParamSql(columnName));
    }

    // 小于
    public static String minThan(String columnName) {
        return StringUtils.joint(columnName, " < ", createSingleParamSql(columnName));
    }

    // 大于等于
    public static String maxEqualThan(String columnName) {
        return StringUtils.joint(columnName, " >= ", createSingleParamSql(columnName));
    }

    // 小于等于
    public static String minEqualThan(String columnName) {
        return StringUtils.joint(columnName, " <= ", createSingleParamSql(columnName));
    }

    // 模糊匹配
    public static String like(String columnName) {
        return StringUtils.joint(columnName, " like concat(" + createSingleParamSql(columnName) + "'%')");
    }

    // 包含
    public static String in(String columnName, int length) {
        StringBuilder sb = new StringBuilder();
        sb.append(columnName).append(" in (");
        for (int i = 0; i < length; i++) {
            sb.append(createSingleParamSql(columnName + i));
            if (i != length - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    // 范围
    public static String betweenAnd(String columnName) {
        return StringUtils.joint(columnName, " between " + createSingleParamSql(columnName + 0) + " and " + createSingleParamSql(columnName + 1));
    }

}
