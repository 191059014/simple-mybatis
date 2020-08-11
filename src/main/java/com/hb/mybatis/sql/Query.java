package com.hb.mybatis.sql;

import com.hb.mybatis.helper.SinglePropertyBuilder;
import com.hb.mybatis.util.SqlUtils;

import java.util.Map;

/**
 * 查询条件
 *
 * @author Mr.huang
 * @since 2020/5/8 9:49
 */
public class Query {

    /**
     * 排序
     */
    private String sort;

    /**
     * 分页开始行
     */
    private Integer startRow;

    /**
     * 分页每页条数
     */
    private Integer pageSize;

    /**
     * where条件
     */
    private Where where = Where.build();

    /**
     * 构建QueryCondition对象
     *
     * @return Query
     */
    public static Query build() {
        return new Query();
    }

    /**
     * 通过实体类添加条件
     *
     * @return Where
     */
    public <T> Query add(T t) {
        where.add(t);
        return this;
    }

    /**
     * 增加sql语句
     *
     * @param sql sql语句
     * @return Where
     */
    public Query sql(String sql) {
        where.sql(sql);
        return this;
    }

    /**
     * 添加条件
     *
     * @param singlePropertyBuilder 操作类型
     * @param columnName            字段名
     * @param value                 值
     * @return QueryCondition
     */
    public Query add(SinglePropertyBuilder singlePropertyBuilder, String columnName, Object value) {
        where.add(singlePropertyBuilder, columnName, value);
        return this;
    }

    /**
     * 排序
     *
     * @param sort 排序条件
     * @return Query
     */
    public Query orderBy(String sort) {
        this.sort = sort;
        return this;
    }

    /**
     * 分页
     *
     * @param startRows 开始行数
     * @param pageSize  每页条数
     * @return Query
     */
    public Query limit(int startRows, int pageSize) {
        this.startRow = startRows;
        this.pageSize = pageSize;
        return this;
    }

    /**
     * 获取查询条件
     *
     * @return map集合
     */
    public Map<String, Object> getParams() {
        return where.getWhereParams();
    }

    /**
     * 获取总条数sql
     *
     * @return sql
     */
    public String getCountSql(String tableName) {
        return SqlUtils.getCountSql(tableName, where);
    }

    /**
     * 获取完整的sql，包含排序，包含分页
     *
     * @return sql
     */
    public String getSimpleSql(String tableName) {
        return SqlUtils.getSimpleSql(tableName, where, sort, startRow, pageSize);
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getStartRow() {
        return startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
