package com.hb.mybatis.enumutil;

/**
 * ========== 记录状态 ==========
 *
 * @author Mr.huang
 * @version com.hb.cp.model.enumutil.RecordStateEnum.java, v1.0
 * @date 2019年09月02日 18时49分
 */
public enum RecordStateEnum {

    VALID(0, "有效"),
    INVALID(1, "无效");

    private Integer value;
    private String name;

    RecordStateEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

}
