package com.hb.mybatis.helper;

import com.hb.mybatis.enums.QueryType;
import com.hb.unic.util.tool.Assert;
import com.hb.unic.util.util.ObjectUtils;
import com.hb.unic.util.util.StrUtils;

import java.util.Map;

/**
 * 查询类型
 *
 * @author Mr.huang
 * @since 2020/5/8 11:14
 */
public class SqlBuilder {

    /**
     * 查询语句sql模板
     */
    private static final String SELECT_SQL_TEMPLATE = "select %s from %s %s ";

    /**
     * 排序语句sql模板
     */
    private static final String SORT_SQL_TEMPLATE = "order by %s ";

    /**
     * 分页查询语句sql模板
     */
    private static final String LIMIT_SQL_TEMPLATE = "limit %s,%s";

    /**
     * 插入sql模板
     */
    private static final String INSERT_SQL_TEMPLATE = "insert into %s (%s) values (%s)";

    /**
     * 更新sql模板
     */
    private static final String UPDATE_SQL_TEMPLATE = "update %s set %s %s";

    /**
     * 删除sql语句模板
     */
    private static final String DELETE_SQL_TEMPLATE = "delete from %s %s";

    /**
     * 参数sql模板
     */
    public static final String PARAM_SQL_TEMPLATE = "#{params.%s}";

    /**
     * 列名sql模板
     */
    public static final String COLUMN_SQL_TEMPLATE = "#{columns.%s}";

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
        return StrUtils.joint(columnName, " = ", createSingleParamSql(columnName));
    }

    // 不等于
    public static String notEqual(String columnName) {
        return StrUtils.joint(columnName, " != ", createSingleParamSql(columnName));
    }

    // 大于
    public static String maxThan(String columnName) {
        return StrUtils.joint(columnName, " > ", createSingleParamSql(columnName));
    }

    // 小于
    public static String minThan(String columnName) {
        return StrUtils.joint(columnName, " < ", createSingleParamSql(columnName));
    }

    // 大于等于
    public static String maxEqualThan(String columnName) {
        return StrUtils.joint(columnName, " >= ", createSingleParamSql(columnName));
    }

    // 小于等于
    public static String minEqualThan(String columnName) {
        return StrUtils.joint(columnName, " <= ", createSingleParamSql(columnName));
    }

    // 模糊匹配
    public static String like(String columnName) {
        return StrUtils.joint(columnName, " like concat(", createSingleParamSql(columnName), ",'%')");
    }

    // 包含
    public static String in(String columnName, int length) {
        String inSqlTemplate = " %s in (%s) ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(createSingleParamSql(columnName + i));
            if (i != length - 1) {
                sb.append(",");
            }
        }
        return String.format(inSqlTemplate, columnName, sb.toString());
    }

    // 范围
    public static String betweenAnd(String columnName) {
        return StrUtils.joint(columnName, " between ", createSingleParamSql(columnName + 0), " and ", createSingleParamSql(columnName + 1));
    }

    /**
     * 生成#{params.paramName}字符串
     *
     * @param paramName 参数名
     * @return 字符串
     */
    public static String createSingleParamSql(String paramName) {
        return StrUtils.joint("#{params.", paramName, "}");
    }

    /**
     * 生成#{cloumns.columnName}字符串
     *
     * @param columnName 参数名
     * @return 字符串
     */
    public static String createSingleColumnSql(String columnName) {
        return StrUtils.joint("#{columns.", columnName, "}");
    }

    /**
     * 获取总条数sql
     *
     * @return sql
     */
    public static String getCountSql(String tableName, String where) {
        return StrUtils.joint("select count(1) from ", tableName, where);
    }

    /**
     * 获取完整的sql，不包含排序，不包含分页
     *
     * @return sql
     */
    public static String getSimpleSql(String tableName, String resultColumns, String where) {
        return getSimpleSql(tableName, resultColumns, where, null, null, null);
    }

    /**
     * 获取完整的sql，包含排序，不包含分页
     *
     * @return sql
     */
    public static String getSimpleSql(String tableName, String resultColumns, String where, String sort) {
        return getSimpleSql(tableName, resultColumns, where, sort, null, null);
    }

    /**
     * 获取完整的sql，包含排序，包含分页
     *
     * @return sql
     */
    public static String getSimpleSql(String tableName, String resultColumns, String where, String sort, Integer startRow, Integer pageSize) {
        String resultColumnsSql = StrUtils.hasText(resultColumns) ? resultColumns : "*";
        String selectSql = String.format(SELECT_SQL_TEMPLATE, resultColumnsSql, tableName, where);
        String sortSql = StrUtils.hasText(sort) ? String.format(SORT_SQL_TEMPLATE, sort) : "";
        String limitSql = ObjectUtils.isAnyNotNull(startRow, pageSize) ? String.format(LIMIT_SQL_TEMPLATE, SqlBuilder.createSingleParamSql("startRow"), SqlBuilder.createSingleParamSql("pageSize")) : "";
        return StrUtils.joint(selectSql, sortSql, limitSql);
    }

    /**
     * 构建插入sql语句
     *
     * @param tableName 表名
     * @param property  字段集合
     * @return 插入sql语句
     */
    public static String createInsertSql(String tableName, Map<String, Object> property) {
        // 插入的列
        StringBuilder columnSb = new StringBuilder();
        // 插入的列对应的值
        StringBuilder propertySb = new StringBuilder();
        property.forEach((key, value) -> {
            if (value != null) {
                columnSb.append(key).append(", ");
                propertySb.append(SqlBuilder.createSingleParamSql(key)).append(", ");
            }
        });
        // 去掉末尾的逗号
        String cloumnSql = StrUtils.lastBefore(columnSb.toString(), ", ");
        String propertySql = StrUtils.lastBefore(propertySb.toString(), ", ");
        return String.format(INSERT_SQL_TEMPLATE, tableName, cloumnSql, propertySql);
    }

    /**
     * 构建更新sql语句
     *
     * @param tableName 表名
     * @param property  字段集合
     * @param where     条件集合
     * @return 更新sql语句
     */
    public static String createUpdateSql(String tableName, Map<String, Object> property, Where where) {
        StringBuilder columnSb = new StringBuilder();
        property.forEach((key, value) -> {
            if (value != null) {
                columnSb.append(key).append("=").append(SqlBuilder.createSingleColumnSql(key)).append(", ");
            }
        });
        // 去掉最后一个逗号
        String cloumnSql = StrUtils.lastBefore(columnSb.toString(), ",");
        return String.format(UPDATE_SQL_TEMPLATE, tableName, cloumnSql, where.getWhereSql());
    }

    /**
     * 构建删除sql语句
     *
     * @param tableName
     *            表名
     * @param where
     *            条件集合
     * @return 删除sql语句
     */
    public static String createDeleteSql(String tableName, Where where) {
        return String.format(DELETE_SQL_TEMPLATE, tableName, where.getWhereSql());
    }

}
