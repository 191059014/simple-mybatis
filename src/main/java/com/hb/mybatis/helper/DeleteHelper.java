package com.hb.mybatis.helper;

import com.hb.mybatis.enumutil.RecordStateEnum;

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
    public static String buildDeleteSelectiveSql(String tableName, Map<String, Object> conditions) {
        StringBuilder sb = new StringBuilder("update " + tableName + " set " + RECORDSTATUS + "=" + RecordStateEnum.INVALID.getValue());
        StringBuilder whereSb = new StringBuilder(" where 1=1");
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            whereSb.append(" and ").append(key).append("=").append("#{params.").append(key).append("}");
        }
        sb.append(whereSb.toString());
        return sb.toString();
    }

}
