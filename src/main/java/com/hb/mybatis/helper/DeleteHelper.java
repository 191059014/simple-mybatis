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
        if (conditions != null && !conditions.isEmpty()) {
            conditions.forEach((key, value) -> whereSb.append(makeAndEqualsCondition(key, value)));
        }
        sb.append(whereSb.toString());
        return sb.toString();
    }

}
