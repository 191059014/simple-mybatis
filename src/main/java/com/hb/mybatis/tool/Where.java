package com.hb.mybatis.tool;

import com.hb.mybatis.common.Consts;
import com.hb.mybatis.enums.QueryType;
import com.hb.unic.logger.Logger;
import com.hb.unic.logger.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * where条件
 *
 * @author Mr.Huang
 * @version v0.1, Where.java, 2020/5/26 10:14, create by huangbiao.
 */
public class Where {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Where.class);

    /**
     * sql语句
     */
    private StringBuilder whereSql = new StringBuilder();

    /**
     * 参数集合
     */
    private Map<String, Object> params = new HashMap<>(16);

    /**
     * 构建查询条件对象
     *
     * @return Where
     */
    public static Where build() {
        return new Where().addSql(" where " + Consts.RECORD_STATUS_COLUMN + " = " + Consts.RECORD_VALID);
    }

    /**
     * 等于
     *
     * @param columnName 列名
     * @param value      值
     * @return Where
     */
    public Where equal(String columnName, Object value) {
        this.buildSingleWhereCondition(QueryType.EQUAL, columnName, value);
        return this;
    }

    /**
     * 不等于
     *
     * @param columnName 列名
     * @param value      值
     * @return Where
     */
    public Where notEqual(String columnName, Object value) {
        this.buildSingleWhereCondition(QueryType.NOT_EQUAL, columnName, value);
        return this;
    }

    /**
     * 大于
     *
     * @param columnName 列名
     * @param value      值
     * @return Where
     */
    public Where maxThan(String columnName, Object value) {
        this.buildSingleWhereCondition(QueryType.MAX_THAN, columnName, value);
        return this;
    }

    /**
     * 小于
     *
     * @param columnName 列名
     * @param value      值
     * @return Where
     */
    public Where minThan(String columnName, Object value) {
        this.buildSingleWhereCondition(QueryType.MIN_THAN, columnName, value);
        return this;
    }

    /**
     * 大于等于
     *
     * @param columnName 列名
     * @param value      值
     * @return Where
     */
    public Where maxEqualThan(String columnName, Object value) {
        this.buildSingleWhereCondition(QueryType.MAX_EQUAL_THAN, columnName, value);
        return this;
    }

    /**
     * 小于等于
     *
     * @param columnName 列名
     * @param value      值
     * @return Where
     */
    public Where minEqualThan(String columnName, Object value) {
        this.buildSingleWhereCondition(QueryType.MIN_EQUAL_THAN, columnName, value);
        return this;
    }

    /**
     * 模糊匹配
     *
     * @param columnName 列名
     * @param value      值
     * @return Where
     */
    public Where like(String columnName, Object value) {
        this.buildSingleWhereCondition(QueryType.LIKE, columnName, value);
        return this;
    }

    /**
     * in条件
     *
     * @param columnName 列名
     * @param collection 集合值
     * @return Where
     */
    public Where in(String columnName, Collection collection) {
        this.buildSingleWhereCondition(QueryType.IN, columnName, collection);
        return this;
    }

    /**
     * between and条件
     *
     * @param columnName 列名
     * @param value0     第一个值
     * @param value1     第二个值
     * @return Where
     */
    public Where betweenAnd(String columnName, Object value0, Object value1) {
        this.buildSingleWhereCondition(QueryType.BETWEEN_AND, columnName, new Object[]{value0, value1});
        return this;
    }

    /**
     * and
     *
     * @return Where
     */
    public Where and() {
        this.whereSql.append(" and ");
        return this;
    }

    /**
     * or
     *
     * @return Where
     */
    public Where or() {
        this.whereSql.append(" or ");
        return this;
    }

    /**
     * 左括号
     *
     * @return Where
     */
    public Where leftBracket() {
        this.whereSql.append(" ( ");
        return this;
    }

    /**
     * 左括号
     *
     * @return Where
     */
    public Where rightBracket() {
        this.whereSql.append(" ) ");
        return this;
    }

    /**
     * 添加sql
     *
     * @return Where
     */
    public Where addSql(String sql) {
        this.whereSql.append(sql);
        return this;
    }

    /**
     * 添加param
     *
     * @return Where
     */
    public Where addParam(String columnName, Object value) {
        this.params.put(columnName, value);
        return this;
    }

    /**
     * 添加param
     *
     * @return Where
     */
    public Where addParams(Map<String, Object> addParams) {
        this.params.putAll(addParams);
        return this;
    }

    /**
     * 获取where完整sql
     *
     * @return addSql
     */
    public String getWhereSql() {
        return this.whereSql.toString();
    }

    /**
     * 获取where查询条件参数
     *
     * @return map集合
     */
    public Map<String, Object> getWhereParams() {
        return this.params;
    }

    /**
     * and条件查询，如果value为空或空字符串，则不进行任何操作
     *
     * @param queryType  查询类型
     * @param columnName 列名
     * @param value      值
     * @return Where
     */
    public Where andCondition(QueryType queryType, String columnName, Object value) {
        if (value != null && value.toString().trim().length() > 0) {
            and();
            buildSingleWhereCondition(queryType, columnName, value);
        }
        return this;
    }

    /**
     * 构建单个where条件sql语句
     *
     * @param queryType  查询条件类型
     * @param columnName 列名
     * @param value      条件值
     */
    private void buildSingleWhereCondition(QueryType queryType, String columnName, Object value) {
        if (QueryType.IN.equals(queryType)) {
            Collection collection = (Collection) value;
            int size = collection.size();
            int index = 0;
            StringBuilder inSql = new StringBuilder();
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                String columnNameWithIndex = columnName + "_" + index;
                inSql.append(String.format(SqlTemplate.PARAM_SQL_TEMPLATE, columnNameWithIndex));
                if (index != size - 1) {
                    inSql.append(",");
                }
                this.params.put(columnNameWithIndex, iterator.next());
                index++;
            }
            this.whereSql.append(String.format(queryType.getSqlTemplate(), columnName, inSql));
        } else if (QueryType.BETWEEN_AND.equals(queryType)) {
            Object[] objArr = (Object[]) value;
            String columnName0 = columnName + "_0";
            String columnName1 = columnName + "_1";
            this.params.put(columnName0, objArr[0]);
            this.params.put(columnName1, objArr[1]);
            this.whereSql.append(String.format(queryType.getSqlTemplate(), columnName, columnName0, columnName1));
        } else if (QueryType.LIKE.equals(queryType)) {
            this.params.put(columnName, value);
            this.whereSql.append(String.format(queryType.getSqlTemplate(), columnName, value));
        } else {
            this.params.put(columnName, value);
            this.whereSql.append(String.format(queryType.getSqlTemplate(), columnName, queryType.getSqlTemplate(), value));
        }
    }

}

    