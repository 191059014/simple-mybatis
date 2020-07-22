package com.hb.mybatis.helper;

import com.hb.mybatis.util.SqlBuilderUtils;
import com.hb.unic.util.util.ReflectUtils;

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

    /**
     * where条件
     */
    private WhereCondition whereCondition = WhereCondition.build();

    /**
     * 构建QueryCondition对象
     *
     * @return QueryCondition
     */
    public static QueryCondition build() {
        return new QueryCondition();
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 通过实体类添加条件
     *
     * @return WhereCondition
     */
    public <T> QueryCondition analysisEntityCondition(T t) {
        Map<String, Object> allFields = ReflectUtils.getAllFieldsExcludeStaticAndFinal(t);
        allFields.forEach((key, value) -> addCondition(QueryType.EQUALS, key, value));
        return this;
    }


    /**
     * 添加条件
     *
     * @param singleWhereBuilder 操作类型
     * @param columnName         字段名
     * @param value              值
     * @return QueryCondition
     */
    public QueryCondition addCondition(SingleWhereBuilder singleWhereBuilder, String columnName, Object value) {
        this.whereCondition.addCondition(singleWhereBuilder, columnName, value);
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
        String baseSql = "select count(1) from " + tableName;
        return baseSql + whereCondition.getWhereSql();
    }

    /**
     * 获取简单的sql，不包含分页
     *
     * @return sql
     */
    public String getSimpleSql() {
        String baseSql = "select * from " + tableName;
        return baseSql + whereCondition.getWhereSql() + buildSortSql();
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
        Map<String, Object> whereParams = whereCondition.getWhereParams();
        if (limitStartRows != null && limitPageSize != null) {
            whereParams.put(SqlBuilderUtils.START_ROWS, limitStartRows);
            whereParams.put(SqlBuilderUtils.PAGE_SIZE, limitPageSize);
        }
        return whereParams;
    }

    public Integer getLimitStartRows() {
        return limitStartRows;
    }

    public Integer getLimitPageSize() {
        return limitPageSize;
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
