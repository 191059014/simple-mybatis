package com.hb.mybatis.helper;

/**
 * sql构建接口
 *
 * @author Mr.huang
 * @since 2020/5/8 11:12
 */
public interface SinglePropertyBuilder {

    /**
     * 构建sql
     *
     * @param columnName 字段名
     * @param objArr     参数，此参数
     * @return sql语句
     */
    String buildSql(String columnName, Object... objArr);

}
