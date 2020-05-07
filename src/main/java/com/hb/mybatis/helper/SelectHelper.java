package com.hb.mybatis.helper;


import com.hb.mybatis.enumutil.RecordStateEnum;

import java.util.Map;

/**
 * ========== 拼装sql辅助类 ==========
 *
 * @author Mr.huang
 * @version com.hb.cp.dao.base.SelectHelper.java, v1.0
 * @date 2019年10月12日 14时19分
 */
public class SelectHelper extends AbstractSqlHelper {

    /**
     * 构建查询语句
     *
     * @param tableName  表名
     * @param conditions 条件集合
     * @param sort       排序
     * @param startRow   开始行数
     * @param pageNum    每页条数
     * @return sql
     */
    public static String buildSelectSelectiveSql(String tableName, Map<String, Object> conditions, String sort, Integer startRow, Integer pageNum) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeSelectBase(tableName));
        if (conditions != null && !conditions.isEmpty()) {
            conditions.forEach((key, value) -> sb.append(makeAndEqualsCondition(key, value)));
        }
        sb.append(makeSortCondition(sort));
        sb.append(makePagesCondition(startRow, pageNum));
        return sb.toString();
    }

    /**
     * 组装最基本的select语句片段
     *
     * @param tableName 表名
     * @return 基本的select语句
     */
    public static String makeSelectBase(String tableName) {
        return "select * from " + tableName + " where " + RECORDSTATUS + "=" + RecordStateEnum.VALID.getValue();
    }

    /**
     * 构建查询总条数sql语句
     *
     * @param tableName  表名
     * @param conditions 条件集合
     * @return 查询总条数sql语句
     */
    public static String buildSelectCountSelectiveSql(String tableName, Map<String, Object> conditions) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from ").append(tableName).append(" where ").append(RECORDSTATUS).append("=").append(RecordStateEnum.VALID.getValue());
        if (conditions != null && !conditions.isEmpty()) {
            conditions.forEach((key, value) -> sb.append(makeAndEqualsCondition(key, value)));
        }
        return sb.toString();
    }

}
