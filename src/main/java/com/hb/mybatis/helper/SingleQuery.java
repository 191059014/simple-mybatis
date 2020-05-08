package com.hb.mybatis.helper;

/**
 * 单个查询信息
 *
 * @author Mr.huang
 * @since 2020/5/8 11:29
 */
public class SingleQuery {

    // 操作类型
    private QueryBuilder queryBuilder;

    // 字段名
    private String key;

    // 字段值
    private Object value;

    public SingleQuery(QueryBuilder queryBuilder, String key, Object value) {
        this.queryBuilder = queryBuilder;
        this.key = key;
        this.value = value;
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

}
