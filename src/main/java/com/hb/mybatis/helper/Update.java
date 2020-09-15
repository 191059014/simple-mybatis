package com.hb.mybatis.helper;

import com.hb.unic.util.util.StringUtils;

import java.util.Map;

/**
 * 更新sql工具类
 *
 * @author Mr.huang
 * @since 2020/5/7 17:02
 */
public class Update {

    /**
     * 构建更新sql语句
     *
     * @param tableName 表名
     * @param property  字段集合
     * @param where     条件集合
     * @return 更新sql语句
     */
    public static String buildSelectiveSql(String tableName, Map<String, Object> property, Where where) {
        StringBuilder sb = new StringBuilder("update " + tableName + " set ");
        StringBuilder cloumSb = new StringBuilder();
        property.forEach((key, value) -> {
            if (value != null) {
                cloumSb.append(key).append("=").append(SqlBuilder.createSingleColumnSql(key)).append(", ");
            }
        });
        // 去掉最后一个逗号
        String cloumnSql = StringUtils.lastBefore(cloumSb.toString(), ",");
        sb.append(cloumnSql).append(where.getWhereSql());
        return sb.toString();
    }

}
