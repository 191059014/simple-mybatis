package com.hb.mybatis.toolkit;

import com.hb.unic.util.util.ObjectUtils;
import com.hb.unic.util.util.StrUtils;

import java.util.Map;

/**
 * 查询类型
 *
 * @author Mr.huang
 * @since 2020/5/8 11:14
 */
public class SqlTemplate {

    /**
     * 查询语句sql模板
     */
    public static final String SELECT_SQL_TEMPLATE = "select %s from %s %s ";

    /**
     * 查询总条数语句sql模板
     */
    public static final String COUNT_SQL_TEMPLATE = "select count(*) from %s %s ";

    /**
     * 排序语句sql模板
     */
    public static final String SORT_SQL_TEMPLATE = "order by %s ";

    /**
     * 分页查询语句sql模板
     */
    public static final String LIMIT_SQL_TEMPLATE = "limit %s,%s";

    /**
     * 插入sql模板
     */
    public static final String INSERT_SQL_TEMPLATE = "insert into %s (%s) values (%s)";

    /**
     * 更新sql模板
     */
    public static final String UPDATE_SQL_TEMPLATE = "update %s set %s %s";

    /**
     * 删除sql语句模板
     */
    public static final String DELETE_SQL_TEMPLATE = "delete from %s %s";

    /**
     * 条件sql模板
     */
    public static final String PARAM_SQL_TEMPLATE = "#{params.%s}";

    /**
     * 列名sql模板
     */
    public static final String COLUMN_SQL_TEMPLATE = "#{columns.%s}";

    /**
     * 获取总条数sql
     *
     * @return addSql
     */
    public static String getCountSql(String tableName, String whereSql) {
        return String.format(COUNT_SQL_TEMPLATE, tableName, whereSql);
    }

    /**
     * 获取完整的sql，包含排序，包含分页
     *
     * @return addSql
     */
    public static String getSimpleSql(String tableName, String resultColumns, String where, String sort, Integer startRow, Integer pageSize) {
        String resultColumnsSql = StrUtils.hasText(resultColumns) ? resultColumns : "*";
        String selectSql = String.format(SELECT_SQL_TEMPLATE, resultColumnsSql, tableName, where);
        String sortSql = StrUtils.hasText(sort) ? String.format(SORT_SQL_TEMPLATE, sort) : "";
        String limitSql = ObjectUtils.isAnyNotNull(startRow, pageSize) ? String.format(LIMIT_SQL_TEMPLATE, String.format(PARAM_SQL_TEMPLATE, "startRow"), String.format(PARAM_SQL_TEMPLATE, "pageSize")) : "";
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
                propertySb.append(String.format(PARAM_SQL_TEMPLATE, key)).append(", ");
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
                columnSb.append(key).append("=").append(String.format(COLUMN_SQL_TEMPLATE, key)).append(", ");
            }
        });
        // 去掉最后一个逗号
        String cloumnSql = StrUtils.lastBefore(columnSb.toString(), ",");
        return String.format(UPDATE_SQL_TEMPLATE, tableName, cloumnSql, where.getWhereSql());
    }

    /**
     * 构建删除sql语句
     *
     * @param tableName 表名
     * @param where     条件集合
     * @return 删除sql语句
     */
    public static String createDeleteSql(String tableName, Where where) {
        return String.format(DELETE_SQL_TEMPLATE, tableName, where.getWhereSql());
    }

}
