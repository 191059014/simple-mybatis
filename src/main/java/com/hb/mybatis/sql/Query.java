package com.hb.mybatis.sql;

import com.hb.mybatis.SimpleMybatisContext;
import com.hb.mybatis.common.Consts;
import com.hb.mybatis.helper.QueryType;
import com.hb.mybatis.helper.SingleWhereBuilder;
import com.hb.mybatis.helper.SqlBuilderHelper;
import com.hb.unic.util.util.CloneUtils;

import java.util.Map;

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
    private Integer limitStartRows;

    /**
     * 分页每页条数
     */
    private Integer limitPageSize;

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

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 通过实体类添加条件
     *
     * @return Where
     */
    public <T> Query analysisAll(T t) {
        Map<String, Object> allFields = CloneUtils.bean2Map(t);
        if (SimpleMybatisContext.getBooleanValue(Consts.HUMP_MAPPING)) {
            SqlBuilderHelper.convertToUnderlineMap(allFields);
        }
        allFields.forEach((key, value) -> add(QueryType.EQUALS, key, value));
        return this;
    }

    /**
     * 添加条件
     *
     * @param singleWhereBuilder 操作类型
     * @param columnName         字段名
     * @param value              值
     * @return Query
     */
    public Query add(SingleWhereBuilder singleWhereBuilder, String columnName, Object value) {
        this.where.add(singleWhereBuilder, columnName, value);
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
        return baseSql + where.getWhereSql();
    }

    /**
     * 获取简单的sql，包含排序，不包含分页
     *
     * @return sql
     */
    public String getSimpleSql() {
        String baseSql = "select * from " + tableName;
        return baseSql + where.getWhereSql() + buildSortSql();
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
     * 获取查询条件
     *
     * @return map集合
     */
    public Map<String, Object> getParams() {
        Map<String, Object> whereParams = where.getWhereParams();
        if (limitStartRows != null && limitPageSize != null) {
            whereParams.put(SqlBuilderHelper.START_ROWS, limitStartRows);
            whereParams.put(SqlBuilderHelper.PAGE_SIZE, limitPageSize);
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
        return this.limitStartRows != null && this.limitPageSize != null ? " limit " + SqlBuilderHelper.createSingleParamSql("startRows") + "," + SqlBuilderHelper.createSingleParamSql("pageSize") : "";

    }

}
