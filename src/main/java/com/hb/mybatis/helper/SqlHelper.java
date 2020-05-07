package com.hb.mybatis.helper;


import com.hb.mybatis.enumutil.RecordStateEnum;

import java.util.Map;

/**
 * ========== 拼装sql辅助类 ==========
 *
 * @author Mr.huang
 * @version com.hb.cp.dao.base.SqlHelper.java, v1.0
 * @date 2019年10月12日 14时19分
 */
public class SqlHelper {

    /**
     * 记录状态字段名
     */
    private static final String RECORDSTATUS = "record_Status";

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
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                sb.append(makeSelectCondition(entry.getKey(), entry.getValue()));
            }
        }
        sb.append(makeSelectSort(sort));
        sb.append(makePages(startRow, pageNum));
        return sb.toString();
    }

    /**
     * 组装条件
     *
     * @param key   字段名
     * @param value 字段值
     * @return 条件sql片段
     */
    public static String makeSelectCondition(String key, Object value) {
        if (value != null) {
            return " and " + key + "=" + "#{params." + key + "}";
        }
        return "";
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
     * 组装查询排序条件sql片段
     *
     * @param sort 排序
     * @return 排序的sql片段
     */
    public static String makeSelectSort(String sort) {
        if (sort != null && !"".equals(sort)) {
            return " order by " + sort;
        }
        return "";
    }

    /**
     * 组装分页条件sql片段
     *
     * @param startRow 开始行
     * @param pageNum  每页条数
     * @return 分页sql片段
     */
    public static String makePages(Integer startRow, Integer pageNum) {
        if (startRow != null && pageNum != null) {
            return " limit #{startRow},#{pageNum} ";
        }
        return "";
    }

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
        for (Map.Entry<String, String> entry : property.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null) {
                continue;
            }
            cloumSb.append(key).append("=").append("#{cloumns.").append(key).append("}").append(",");
        }
        StringBuilder whereSb = new StringBuilder(" where 1=1");
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            whereSb.append(" and ").append(key).append("=").append("#{params.").append(key).append("}");
        }
        String cloumnSql = cloumSb.toString().substring(0, cloumSb.toString().length() - 1);
        sb.append(cloumnSql).append(whereSb.toString());
        return sb.toString();
    }

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

    /**
     * 构建查询总条数sql语句
     *
     * @param tableName  表名
     * @param conditions 条件集合
     * @return 查询总条数sql语句
     */
    public static String buildSelectCountSelectiveSql(String tableName, Map<String, Object> conditions) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from " + tableName + " where " + RECORDSTATUS + "=" + RecordStateEnum.VALID.getValue());
        if (conditions != null && !conditions.isEmpty()) {
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                sb.append(makeSelectCondition(entry.getKey(), entry.getValue()));
            }
        }
        return sb.toString();
    }

}
