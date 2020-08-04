package com.hb.mybatis.sql;

import com.hb.mybatis.helper.QueryType;
import com.hb.mybatis.helper.SinglePropertyBuilder;
import com.hb.mybatis.helper.SqlBuilder;
import com.hb.unic.util.util.CloneUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * where条件
 *
 * @author Mr.Huang
 * @version v0.1, Where.java, 2020/5/26 10:14, create by huangbiao.
 */
public class Where {

    /**
     * sql语句
     */
    private String whereSql;

    /**
     * 参数集合
     */
    private Map<String, Object> params = new HashMap<>(16);

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
    public <T> Where add(T t) {
        Map<String, Object> allFields = CloneUtils.bean2Map(t);
//        SqlBuilder.convertPropertyNameToColumnName(allFields, t.getClass().getName());
        allFields.forEach((key, value) -> add(QueryType.EQUALS, key, value));
        return this;
    }

    /**
     * 增加sql语句
     *
     * @param sql sql语句
     * @return Where
     */
    public Where sql(String sql) {
        whereSql += sql;
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
    public Where add(SinglePropertyBuilder singlePropertyBuilder, String columnName, Object value) {
        if (value != null && !"".equals(value.toString())) {
            whereSql += singlePropertyBuilder.buildSql(columnName, value);
            params.put(columnName, value);
        }
        return this;
    }

    /**
     * 通过queryList来生成sql
     *
     * @return sql
     */
    public String getWhereSql() {
        return this.whereSql;
    }

    /**
     * 获取where查询条件参数
     *
     * @return map集合
     */
    public Map<String, Object> getWhereParams() {
        return this.params;
    }

}

    