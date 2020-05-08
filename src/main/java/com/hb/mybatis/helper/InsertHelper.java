package com.hb.mybatis.helper;

import com.hb.mybatis.util.SqlBuilderUtils;

import java.util.Map;

/**
 * 插入sql工具类
 *
 * @author Mr.huang
 * @since 2020/5/7 17:03
 */
public class InsertHelper extends AbstractSqlHelper {

    /**
     * 构建插入sql语句
     *
     * @param tableName 表名
     * @param property  字段集合
     * @return 插入sql语句
     */
    public static String buildInsertSelectiveSql(String tableName, Map<String, String> property) {
        assertNotEmpty(property, "insert columns cannot empty");
        StringBuilder sb = new StringBuilder("insert into " + tableName);
        // 插入的列
        StringBuilder cloumSb = new StringBuilder(SqlBuilderUtils.LEFT_SMALL_BRACKET);
        // 插入的列对应的值
        StringBuilder propertySb = new StringBuilder(" values " + SqlBuilderUtils.LEFT_SMALL_BRACKET);
        property.forEach((key, value) -> {
            if (value != null) {
                cloumSb.append(key).append(SqlBuilderUtils.COMMA);
                propertySb.append(SqlBuilderUtils.createSingleParamSql(key)).append(SqlBuilderUtils.COMMA);
            }
        });
        // 去掉末尾的逗号
        String cloumnSql = cloumSb.toString().substring(0, cloumSb.toString().length() - 1) + ")";
        String propertySql = propertySb.toString().substring(0, propertySb.toString().length() - 1) + ")";
        sb.append(cloumnSql).append(propertySql);
        return sb.toString();
    }

}
