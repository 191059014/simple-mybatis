package com.hb.mybatis.sql;

import com.hb.mybatis.util.SqlUtils;
import com.hb.unic.util.util.StringUtils;

import java.util.Map;

/**
 * 插入sql工具类
 *
 * @author Mr.huang
 * @since 2020/5/7 17:03
 */
public class Insert {

    /**
     * 构建插入sql语句
     *
     * @param tableName 表名
     * @param property  字段集合
     * @return 插入sql语句
     */
    public static String buildSelectiveSql(String tableName, Map<String, Object> property) {
        StringBuilder sb = new StringBuilder("insert into " + tableName);
        // 插入的列
        StringBuilder cloumSb = new StringBuilder(SqlUtils.LEFT_SMALL_BRACKET);
        // 插入的列对应的值
        StringBuilder propertySb = new StringBuilder(" values " + SqlUtils.LEFT_SMALL_BRACKET);
        property.forEach((key, value) -> {
            if (value != null) {
                cloumSb.append(key).append(SqlUtils.COMMA);
                propertySb.append(SqlUtils.createSingleParamSql(key)).append(SqlUtils.COMMA);
            }
        });
        // 去掉末尾的逗号
        String cloumnSql = StringUtils.lastBefore(cloumSb.toString(),SqlUtils.COMMA) + ")";
        String propertySql = StringUtils.lastBefore(propertySb.toString(),SqlUtils.COMMA) + ")";
        sb.append(cloumnSql).append(propertySql);
        return sb.toString();
    }

}
