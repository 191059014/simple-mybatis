package com.hb.mybatis.helper;

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
        StringBuilder sb = new StringBuilder("insert into " + tableName);
        StringBuilder cloumSb = new StringBuilder(" (");
        StringBuilder propertySb = new StringBuilder(" values (");
        for (Map.Entry<String, String> entry : property.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null) {
                continue;
            }
            cloumSb.append(key).append(",");
            propertySb.append("#{params." + key + "}").append(",");
        }
        String cloumnSql = cloumSb.toString().substring(0, cloumSb.toString().length() - 1) + ")";
        String propertySql = propertySb.toString().substring(0, propertySb.toString().length() - 1) + ")";
        sb.append(cloumnSql).append(propertySql);
        return sb.toString();
    }

}
