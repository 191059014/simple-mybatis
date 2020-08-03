package com.hb.mybatis.sql;

import com.hb.mybatis.helper.SqlBuilderHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 查询条件
 *
 * @author Mr.huang
 * @since 2020/5/8 9:49
 */
public class Query {

    /**
     * 表名
     */
    private String tableName;

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
     * and过滤条件
     */
    private List<Where> andFilters = new ArrayList<>();

    /**
     * or过滤条件集合
     */
    private List<Where> orFilters = new ArrayList<>();

    /**
     * 构建QueryCondition对象
     *
     * @return Query
     */
    public static Query build() {
        return new Query();
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 添加and过滤器
     *
     * @param where 条件
     * @return 对象本身
     */
    public Query andFilter(Where...whereArr) {
        return this;
    }

    /**
     * 添加or过滤器
     *
     * @param where 条件
     * @return 对象本身
     */
    public Query orFilter(Where where) {
        this.orFilters.add(where);
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
     * 获取总条数sql
     *
     * @return sql
     */
    public String getCountSql() {
        String baseSql = "select count(1) from " + tableName;
        return baseSql + getWhereSql();
    }

    /**
     * 获取简单的sql，包含排序，不包含分页
     *
     * @return sql
     */
    public String getSimpleSql() {
        String baseSql = "select * from " + tableName;
        return baseSql + getWhereSql() + buildSortSql();
    }

    /**
     * 获取完整的sql，包含排序，包含分页
     *
     * @return sql
     */
    public String getFullSql() {
        return getSimpleSql() + buildPagesSql();
    }

    /**
     * 获取where条件
     *
     * @return sql
     */
    public String getWhereSql() {
        StringBuilder sb = new StringBuilder(" where 1=1 ");
        if (this.andFilters.size() > 0) {
            this.andFilters.forEach(where -> {
                sb.append(" and (");
                sb.append(where.getWhereSql());
                sb.append(" ) ");
            });
        }
        if (this.orFilters.size() > 0) {
            this.orFilters.forEach(where -> {
                sb.append(" or (");
                sb.append(where.getWhereSql());
                sb.append(" ) ");
            });
        }
        return sb.toString();
    }

    /**
     * 获取查询条件
     *
     * @return map集合
     */
    public Map<String, Object> getParams() {
        Map<String, Object> whereParams = new HashMap<>();
        if (this.andFilters.size() > 0) {
            this.andFilters.forEach(where -> {
                whereParams.putAll(where.getWhereParams());
            });
        }
        if (this.orFilters.size() > 0) {
            this.orFilters.forEach(where -> {
                whereParams.putAll(where.getWhereParams());
            });
        }
        if (startRow != null && pageSize != null) {
            whereParams.put(SqlBuilderHelper.START_ROWS, startRow);
            whereParams.put(SqlBuilderHelper.PAGE_SIZE, pageSize);
        }
        return whereParams;
    }

    public Integer getLimitStartRows() {
        return startRow;
    }

    public Integer getPageSize() {
        return pageSize;
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
        return this.startRow != null && this.pageSize != null ? " limit " + SqlBuilderHelper.createSingleParamSql("startRow") + "," + SqlBuilderHelper.createSingleParamSql("pageSize") : "";

    }

}
