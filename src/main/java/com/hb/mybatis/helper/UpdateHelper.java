package com.hb.mybatis.helper;

import com.hb.mybatis.util.SqlBuilderUtils;

import java.util.Map;
import java.util.Objects;

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
        assertNotEmpty(property, "update colums cannot empty");
        assertNotEmpty(property, "update conditions cannot empty");
        StringBuilder sb = new StringBuilder("update " + tableName + " set ");
        StringBuilder cloumSb = new StringBuilder();
        property.forEach((key, value) -> {
            if (value != null) {
                cloumSb.append(key).append(SqlBuilderUtils.EQUALS).append(SqlBuilderUtils.createSingleParamSql(key)).append(SqlBuilderUtils.COMMA);
            }
        });
        StringBuilder whereSb = new StringBuilder(" where 1=1");
        conditions.forEach((key, value) -> {
            if (value != null) {
                whereSb.append(SqlBuilderUtils.AND).append(key).append(SqlBuilderUtils.EQUALS).append(SqlBuilderUtils.createSingleParamSql(key));
            }
        });
        // 去掉最后一个逗号
        String cloumnSql = cloumSb.toString().substring(0, cloumSb.toString().length() - 1);
        sb.append(cloumnSql).append(whereSb.toString());
        return sb.toString();
    }

}
