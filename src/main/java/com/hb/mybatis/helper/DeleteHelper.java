package com.hb.mybatis.helper;

import com.hb.mybatis.util.SqlBuilderUtils;

import java.util.Map;

/**
 * 删除sql工具类
 *
 * @author Mr.huang
 * @since 2020/5/7 17:03
 */
public class DeleteHelper extends AbstractSqlHelper {

    /**
     * 构建删除sql语句
     *
     * @param tableName  表名
     * @param conditions 条件集合
     * @return 删除sql语句
     */
    public static String buildDeleteSelectiveSql(String tableName, Map<String, String> conditions) {
        assertNotEmpty(conditions, "delete conditions cannot empty");
        StringBuilder sb = new StringBuilder("delete from " + tableName);
        StringBuilder whereSb = new StringBuilder(" where 1=1");
        conditions.forEach((key, value) -> {
            if (value != null && !"".equals(value)) {
                whereSb.append(SqlBuilderUtils.AND).append(key).append(SqlBuilderUtils.EQUALS).append(SqlBuilderUtils.createSingleParamSql(key));
            }
        });
        sb.append(whereSb.toString());
        return sb.toString();
    }

}
