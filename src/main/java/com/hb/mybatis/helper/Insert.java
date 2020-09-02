package com.hb.mybatis.helper;

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
        StringBuilder cloumSb = new StringBuilder("(");
        // 插入的列对应的值
        StringBuilder propertySb = new StringBuilder(" values (");
        property.forEach((key, value) -> {
            if (value != null) {
                cloumSb.append(key).append(", ");
                propertySb.append(SqlBuilder.createSingleParamSql(key)).append(", ");
            }
        });
        // 去掉末尾的逗号
        String cloumnSql = StringUtils.lastBefore(cloumSb.toString(), ", ") + ")";
        String propertySql = StringUtils.lastBefore(propertySb.toString(), ", ") + ")";
        sb.append(cloumnSql).append(propertySql);
        return sb.toString();
    }

}
