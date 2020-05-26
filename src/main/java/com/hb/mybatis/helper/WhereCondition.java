package com.hb.mybatis.helper;

import com.hb.mybatis.util.SqlBuilderUtils;
import com.hb.unic.util.util.ReflectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * where条件
 *
 * @author Mr.Huang
 * @version v0.1, WhereCondition.java, 2020/5/26 10:14, create by huangbiao.
 */
public class WhereCondition {

    /**
     * 查询条件集合
     */
    private List<SingleWhereCondition> whereConditionList = new ArrayList<>(16);

    /**
     * 构建WhereCondition对象
     *
     * @return WhereCondition
     */
    public static WhereCondition build() {
        return new WhereCondition();
    }

    /**
     * 通过实体类添加条件
     *
     * @return WhereCondition
     */
    public <T> WhereCondition analysisEntityCondition(T t) {
        Map<String, Object> allFields = ReflectUtils.getAllFieldsExcludeStatic(t);
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
    public WhereCondition addCondition(SingleWhereBuilder singleWhereBuilder, String columnName, Object value) {
        if (value != null && !"".equals(value.toString())) {
            this.whereConditionList.add(new SingleWhereCondition(singleWhereBuilder, columnName, value));
        }
        return this;
    }

    /**
     * 通过queryList来生成sql
     *
     * @return sql
     */
    public String getWhereSql() {
        StringBuilder sb = new StringBuilder(SqlBuilderUtils.WHERE_TRUE);
        whereConditionList.forEach(query -> {
            sb.append(query.getSingleWhereBuilder().buildSql(query.getColumName(), query.getValue()));
        });
        return sb.toString();
    }

    /**
     * 获取where查询条件参数
     *
     * @return map集合
     */
    public Map<String, Object> getWhereParams() {
        Map<String, Object> conditions = new HashMap<>();
        whereConditionList.forEach(query -> {
            conditions.putAll(query.getSingleWhereBuilder().buildConditions(query.getColumName(), query.getValue()));
        });
        return conditions;
    }

    /**
     * 是否为空
     *
     * @return true为空
     */
    public boolean isEmpty() {
        return !(whereConditionList.size() > 0);
    }

}

    