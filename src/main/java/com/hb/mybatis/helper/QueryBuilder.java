package com.hb.mybatis.helper;

import java.util.Map;

/**
 * sql构建接口
 *
 * @author Mr.huang
 * @since 2020/5/8 11:12
 */
public interface QueryBuilder {

    /**
     * 构建sql
     *
     * @param columnName 字段名
     * @param value      字段值
     * @return sql语句
     */
    String buildSql(String columnName, Object value);

    /**
     * 构建条件
     *
     * @param columnName 字段名
     * @param value      查询条件值
     * @return 条件集合
     */
    Map<String, Object> buildConditions(String columnName, Object value);

}
