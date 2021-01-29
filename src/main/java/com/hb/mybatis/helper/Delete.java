package com.hb.mybatis.helper;

/**
 * 删除sql工具类
 *
 * @author Mr.huang
 * @since 2020/5/7 17:03
 */
public class Delete {

    /**
     * 删除sql语句模板
     */
    public static final String DELETE_SQL_TEMPLATE = "delete from %s %s";

    /**
     * 构建删除sql语句
     *
     * @param tableName
     *            表名
     * @param where
     *            条件集合
     * @return 删除sql语句
     */
    public static String buildSql(String tableName, Where where) {
        return String.format(DELETE_SQL_TEMPLATE, tableName, where.getWhereSql());
    }

}
