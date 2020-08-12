package com.hb.mybatis.sql;

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
     * 结果
     */
    private String resultColumns;

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
    private Where where = null;

    public Query(Where where) {
        if (where == null) {
            where = Where.build();
        }
        this.where = where;
    }

    /**
     * 构建QueryCondition对象
     *
     * @return Query
     */
    public static Query build(Where where) {
        return new Query(where);
    }

    /**
     * 结果列表
     *
     * @param resultColumns 列名集合，按逗号分开
     * @return Query
     */
    public Query resultColumns(String resultColumns) {
        this.resultColumns = resultColumns;
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
        return SqlUtils.getSimpleSql(tableName, resultColumns, where, sort, startRow, pageSize);
    }

    public Integer getStartRow() {
        return startRow;
    }

    public Integer getPageSize() {
        return pageSize;
    }

}
