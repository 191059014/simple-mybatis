package com.hb.mybatis.helper;

import com.hb.mybatis.util.SqlBuilderUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询条件
 *
 * @author Mr.huang
 * @since 2020/5/8 9:49
 */
public class QueryCondition {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 查询条件集合
     */
    private List<SingleQuery> queryList = new ArrayList<>();

    /**
     * 排序
     */
    private String sort;

    /**
     * 分页开始行
     */
    private Integer limitStartRows;

    /**
     * 分页每页条数
     */
    private Integer limitPageSize;

    public QueryCondition(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 构建QueryCondition对象
     *
     * @return QueryCondition
     */
    public static QueryCondition build(String tableName) {
        if (tableName == null || "".equals(tableName)) {
            throw new IllegalArgumentException("tableName cannot empty");
        }
        return new QueryCondition(tableName);
    }

    /**
     * 添加条件
     *
     * @param queryBuilder 操作类型
     * @param columnName          字段名
     * @param value        值
     * @return QueryCondition
     */
    public QueryCondition addCondition(QueryBuilder queryBuilder, String columnName, Object value) {
        this.queryList.add(new SingleQuery(queryBuilder, columnName, value));
        return this;
    }

    /**
     * 排序
     *
     * @param sort 排序条件
     * @return QueryCondition
     */
    public QueryCondition orderBy(String sort) {
        this.sort = sort;
        return this;
    }

    /**
     * 分页
     *
     * @param startRows 开始行数
     * @param pageSize  每页条数
     * @return QueryCondition
     */
    public QueryCondition limit(int startRows, int pageSize) {
        this.limitStartRows = startRows;
        this.limitPageSize = pageSize;
        return this;
    }

    /**
     * 获取总条数sql
     *
     * @return sql
     */
    public String getCountSql() {
        String baseSql = "select count(1) from " + tableName + " where 1=1 ";
        return baseSql + buildSqlByQueryList();
    }

    /**
     * 获取简单的sql，不包含分页
     *
     * @return sql
     */
    public String getSimpleSql() {
        String baseSql = "select * from " + tableName + " where 1=1 ";
        return baseSql + buildSqlByQueryList() + buildSortSql();
    }

    /**
     * 获取完整的sql，包含分页
     *
     * @return sql
     */
    public String getFullSql() {
        return getSimpleSql() + buildPagesSql();
    }

    /**
     * 获取查询条件
     *
     * @return map集合
     */
    public Map<String, Object> getParams() {
        Map<String, Object> conditions = new HashMap<>();
        queryList.forEach(query -> {
            conditions.putAll(query.getQueryBuilder().buildConditions(query.getColumName(), query.getValue()));
        });
        if (limitStartRows != null && limitPageSize != null) {
            conditions.put(SqlBuilderUtils.START_ROWS, limitStartRows);
            conditions.put(SqlBuilderUtils.PAGE_SIZE, limitPageSize);
        }
        return conditions;
    }

    public Integer getLimitStartRows() {
        return limitStartRows;
    }

    public Integer getLimitPageSize() {
        return limitPageSize;
    }

    /**
     * 通过queryList来生成sql
     *
     * @return sql
     */
    private String buildSqlByQueryList() {
        StringBuilder sb = new StringBuilder();
        queryList.forEach(query -> {
            sb.append(query.getQueryBuilder().buildSql(query.getColumName(), query.getValue()));
        });
        return sb.toString();
    }

    /**
     * 构建排序sql
     *
     * @return 排序sql
     */
    private String buildSortSql() {
        return this.sort != null && !"".equals(this.sort) ? " order by " + this.sort : "";
    }

    /**
     * 构建分页sql
     *
     * @return 分页sql
     */
    private String buildPagesSql() {
        return this.limitStartRows != null && this.limitPageSize != null ? " limit " + SqlBuilderUtils.createSingleParamSql("startRows") + "," + SqlBuilderUtils.createSingleParamSql("pageSize") : "";

    }

}
