package com.hb.mybatis.helper;

import java.util.Map;

/**
 * sql辅助工具抽象类
 *
 * @author Mr.huang
 * @since 2020/5/7 17:04
 */
public abstract class AbstractSqlHelper {

    /**
     * 记录状态字段名
     */
    protected static final String RECORDSTATUS = "record_Status";

    /**
     * 组装条件
     *
     * @param key   字段名
     * @param value 字段值
     * @return 条件sql片段
     */
    protected static String makeAndEqualsCondition(String key, Object value) {
        if (value != null) {
            return " and " + key + "=" + "#{params." + key + "}";
        }
        return "";
    }

    /**
     * 组装查询排序条件sql片段
     *
     * @param sort 排序
     * @return 排序的sql片段
     */
    protected static String makeSortCondition(String sort) {
        if (sort != null && !"".equals(sort)) {
            return " order by " + sort;
        }
        return "";
    }

    /**
     * 组装分页条件sql片段
     *
     * @param startRow 开始行
     * @param pageNum  每页条数
     * @return 分页sql片段
     */
    protected static String makePagesCondition(Integer startRow, Integer pageNum) {
        if (startRow != null && pageNum != null) {
            return " limit #{startRow},#{pageNum} ";
        }
        return "";
    }

    /**
     * 不为空判断
     *
     * @param map     map集合
     * @param message 错误消息
     */
    protected static void assertNotEmpty(Map map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

}
