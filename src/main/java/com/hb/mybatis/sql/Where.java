package com.hb.mybatis.sql;

import com.hb.mybatis.SimpleMybatisContext;
import com.hb.mybatis.common.Consts;
import com.hb.mybatis.helper.QueryType;
import com.hb.mybatis.helper.SingleWhereBuilder;
import com.hb.mybatis.helper.SingleWhereCondition;
import com.hb.mybatis.helper.SqlBuilderHelper;
import com.hb.unic.util.util.CloneUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * where条件
 *
 * @author Mr.Huang
 * @version v0.1, Where.java, 2020/5/26 10:14, create by huangbiao.
 */
public class Where {

    /**
     * 查询条件集合
     */
    private List<SingleWhereCondition> whereConditionList = new ArrayList<>(16);

    /**
     * 构建WhereCondition对象
     *
     * @return Where
     */
    public static Where build() {
        return new Where();
    }

    /**
     * 通过实体类添加条件
     *
     * @return Where
     */
    public <T> Where analysisAll(T t) {
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
     * @return QueryCondition
     */
    public Where add(SingleWhereBuilder singleWhereBuilder, String columnName, Object value) {
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
        StringBuilder sb = new StringBuilder(SqlBuilderHelper.WHERE_TRUE);
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

    