package com.hb.mybatis.helper;

import java.util.Map;

/**
 * 更新sql工具类
 *
 * @author Mr.huang
 * @since 2020/5/7 17:02
 */
public class UpdateHelper extends AbstractSqlHelper {

    /**
     * 构建更新sql语句
     *
     * @param tableName  表名
     * @param property   字段集合
     * @param conditions 条件集合
     * @return 更新sql语句
     */
    public static String buildUpdateSelectiveSql(String tableName, Map<String, String> property, Map<String, Object> conditions) {
        StringBuilder sb = new StringBuilder("update " + tableName + " set ");
        StringBuilder cloumSb = new StringBuilder();
        property.forEach((key, value) -> {
            if (value != null) {
                cloumSb.append(key).append("=").append("#{cloumns.").append(key).append("}").append(",");
            }
        });
        StringBuilder whereSb = new StringBuilder(" where 1=1");
        conditions.forEach((key, value) -> whereSb.append(makeAndEqualsCondition(key, value)));
        String cloumnSql = cloumSb.toString().substring(0, cloumSb.toString().length() - 1);
        sb.append(cloumnSql).append(whereSb.toString());
        return sb.toString();
    }

}
