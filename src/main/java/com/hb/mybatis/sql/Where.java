package com.hb.mybatis.sql;

import com.hb.mybatis.base.DmlMapper;
import com.hb.mybatis.enums.QueryType;
import com.hb.mybatis.helper.SqlFactory;
import com.hb.mybatis.util.SqlUtils;
import com.hb.unic.util.util.CloneUtils;
import com.hb.unic.util.util.StringUtils;

import java.util.*;

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
     * 通过实体类添加条件
     *
     * @return Where
     */
    public <T> Where add(T t) {
        Map<String, Object> allFields = CloneUtils.bean2Map(t);
        SqlUtils.convertPropertyNameToColumnName(allFields, DmlMapper.getEntityMeta(t.getClass().getName()).getProperty2ColumnMap());
        allFields.forEach((key, value) -> add(QueryType.EQUAL, key, value));
        return this;
    }

    /**
     * 增加sql语句
     *
     * @param sql sql语句
     * @return Where
     */
    public Where sql(String sql) {
        sqlList.add(sql);
        return this;
    }

    /**
     * 添加条件
     *
     * @param queryType  操作类型
     * @param columnName 字段名
     * @param value      值
     * @return QueryCondition
     */
    public Where add(QueryType queryType, String columnName, Object value) {
        if (value != null && !"".equals(value.toString())) {
            if (sqlList.size() > 0) {
                sqlList.add(" and ");
            }
            if (QueryType.IN.equals(queryType)) {
                Collection collection = (Collection) value;
                sqlList.add(SqlFactory.create(queryType, columnName, collection.size()));
                Iterator iterator = collection.iterator();
                int index = 0;
                while (iterator.hasNext()) {
                    params.put(columnName + index, iterator.next());
                    index++;
                }
            } else {
                sqlList.add(SqlFactory.create(queryType, columnName));
                params.put(columnName, value);
            }
        }
        return this;
    }

    /**
     * 通过queryList来生成sql
     *
     * @return sql
     */
    public String getWhereSql() {
        if (sqlList.size() > 0) {
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

    