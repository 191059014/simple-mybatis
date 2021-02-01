package com.hb.mybatis.enums;

/**
 * 条件类型
 *
 * @version v0.1, 2020/8/11 22:16, create by huangbiao.
 */
public enum QueryType {

    EQUAL(" %s = #{params.%s} "),
    NOT_EQUAL(" %s != #{params.%s}"),
    MAX_THAN(" %s > #{params.%s} "),
    MIN_THAN(" %s < #{params.%s} "),
    MAX_EQUAL_THAN(" %s >= #{params.%s} "),
    MIN_EQUAL_THAN(" %s <= #{params.%s} "),
    LIKE(" %s like concat(%s,'%%') "),
    IN(" %s in (%s) "),
    BETWEEN_AND(" %s between #{params.%s} and #{params.%s} "),;

    /**
     * sql模板
     */
    private String sqlTemplate;

    QueryType(String sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    public String getSqlTemplate() {
        return sqlTemplate;
    }

}
