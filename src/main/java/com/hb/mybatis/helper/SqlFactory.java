package com.hb.mybatis.helper;

import com.hb.mybatis.enums.QueryType;
import com.hb.unic.util.util.StringUtils;

/**
 * 查询类型
 *
 * @author Mr.huang
 * @since 2020/5/8 11:14
 */
public class SqlFactory {

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
        return StringUtils.joint(columnName, " = ", "#{params.", columnName, "}");
    }

    // 不等于
    public static String notEqual(String columnName) {
        return StringUtils.joint(columnName, " != ", "#{params.", columnName, "}");
    }

    // 大于
    public static String maxThan(String columnName) {
        return StringUtils.joint(columnName, " > ", "#{params.", columnName, "}");
    }

    // 小于
    public static String minThan(String columnName) {
        return StringUtils.joint(columnName, " < ", "#{params.", columnName, "}");
    }

    // 大于等于
    public static String maxEqualThan(String columnName) {
        return StringUtils.joint(columnName, " >= ", "#{params.", columnName, "}");
    }

    // 小于等于
    public static String minEqualThan(String columnName) {
        return StringUtils.joint(columnName, " <= ", "#{params.", columnName, "}");
    }

    // 模糊匹配
    public static String like(String columnName) {
        return StringUtils.joint(columnName, " like concat(#{params.", columnName, "},'%')");
    }

    // 包含
    public static String in(String columnName, int length) {
        StringBuilder sb = new StringBuilder();
        sb.append(columnName).append("in (");
        for (int i = 0; i < length; i++) {
            sb.append("#{params.").append(columnName + i).append("}");
            if (i != length - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    // 范围
    public static String betweenAnd(String columnName) {
        return StringUtils.joint(columnName, " between #{params.", columnName + 0, "} and #{params.", columnName + 1, "}");
    }

}
