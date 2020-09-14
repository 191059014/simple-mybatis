package com.hb.mybatis.helper;

import com.hb.mybatis.common.Consts;
import com.hb.mybatis.enums.QueryType;
import com.hb.unic.util.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
     * sql语句
     */
    private List<String> sqlList = new ArrayList<>(8);

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
     * 添加多个查询条件
     * <p>
     * 1、key为数据库列名，value为值
     * 2、所有条件均用equal操作符
     * 3、字段与字段之间均用and
     *
     * @param map 条件集合
     * @return Where
     */
    public <T> Where addAll(Map<String, Object> map) {
        map.forEach((key, value) -> {
            if (value != null && !"".equals(value.toString())) {
                if (sqlList.size() > 0) {
                    and();
                }
                sqlList.add(SqlBuilder.create(QueryType.EQUAL, key));
                params.put(key, value);
            }
        });
        return this;
    }

    /**
     * 添加单个条件
     *
     * @param key   名称
     * @param value 值
     * @return 当前对象
     */
    public Where addSingleParam(String key, Object value) {
        params.put(key, value);
        return this;
    }

    /**
     * 增加sql语句
     *
     * @param sql sql语句
     * @return Where
     */
    public Where sql(String sql, Map<String, Object> params) {
        sqlList.add(sql);
        this.params.putAll(params);
        return this;
    }

    /**
     * 左括号
     *
     * @return Where
     */
    public Where leftBracket() {
        sqlList.add(" ( ");
        return this;
    }

    /**
     * 左括号
     *
     * @return Where
     */
    public Where rightBracket() {
        sqlList.add(" ) ");
        return this;
    }

    /**
     * and
     *
     * @return Where
     */
    public Where and() {
        sqlList.add(" and ");
        return this;
    }

    /**
     * or
     *
     * @return Where
     */
    public Where or() {
        sqlList.add(" or ");
        return this;
    }

    /**
     * 添加单个查询条件
     * <p>
     * 1、字段与字段之间是用and还是or需要手动填写
     *
     * @param queryType  操作类型
     * @param columnName 字段名
     * @param value      值
     * @return QueryCondition
     */
    public Where addSingle(QueryType queryType, String columnName, Object value) {
        if (value != null && !"".equals(value.toString())) {
            if (QueryType.IN.equals(queryType)) {
                Collection collection = (Collection) value;
                sqlList.add(SqlBuilder.create(queryType, columnName, collection.size()));
                Iterator iterator = collection.iterator();
                int index = 0;
                while (iterator.hasNext()) {
                    params.put(columnName + index, iterator.next());
                    index++;
                }
            } else {
                sqlList.add(SqlBuilder.create(queryType, columnName));
                params.put(columnName, value);
            }
        }
        return this;
    }

    /**
     * 通过sqlList来生成sql
     *
     * @return sql
     */
    public String getWhereSql() {
        if (sqlList.size() > 0) {
            if (!sqlList.contains(SqlBuilder.create(QueryType.EQUAL, Consts.RECORD_STATUS, Consts.RECORD_STATUS_VALID))) {
                this.and().addSingle(QueryType.EQUAL, Consts.RECORD_STATUS, Consts.RECORD_STATUS_VALID);
            }
            return " where " + StringUtils.joint(sqlList.toArray(new String[0]));
        }
        return "";
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

    