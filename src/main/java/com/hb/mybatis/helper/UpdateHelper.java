package com.hb.mybatis.helper;

import com.hb.mybatis.util.SqlBuilderUtils;

import java.util.Map;

/**
 * 更新sql工具类
 *
 * @author Mr.huang
 * @since 2020/5/7 17:02
 */
public class UpdateHelper {

    /**
     * 构建更新sql语句
     *
     * @param tableName      表名
     * @param property       字段集合
     * @param whereCondition 条件集合
     * @return 更新sql语句
     */
    public static String buildUpdateSelectiveSql(String tableName, Map<String, Object> property, WhereCondition whereCondition) {
        StringBuilder sb = new StringBuilder("update " + tableName + " set ");
        StringBuilder cloumSb = new StringBuilder();
        property.forEach((key, value) -> {
            if (value != null) {
                cloumSb.append(key).append(SqlBuilderUtils.EQUALS).append(SqlBuilderUtils.createSingleColumnSql(key)).append(SqlBuilderUtils.COMMA);
            }
        });
        // 去掉最后一个逗号
        String cloumnSql = cloumSb.toString().substring(0, cloumSb.toString().length() - 1);
        sb.append(cloumnSql).append(whereCondition.getWhereSql());
        return sb.toString();
    }

}
