package com.hb.mybatis.helper;

/**
 * 单个where条件
 *
 * @author Mr.huang
 * @since 2020/5/8 11:29
 */
public class SingleWhereCondition {

    // 操作类型
    private SingleWhereBuilder singleWhereBuilder;

    // 字段名
    private String columName;

    // 字段值
    private Object value;

    public SingleWhereCondition(SingleWhereBuilder singleWhereBuilder, String columName, Object value) {
        this.singleWhereBuilder = singleWhereBuilder;
        this.columName = columName;
        this.value = value;
    }

    public SingleWhereBuilder getSingleWhereBuilder() {
        return singleWhereBuilder;
    }

    public String getColumName() {
        return columName;
    }

    public Object getValue() {
        return value;
    }

}
