package com.hb.mybatis.helper;

import java.util.List;

/**
 * 查询类型
 *
 * @author Mr.huang
 * @since 2020/5/8 11:14
 */
public enum QueryType implements SinglePropertyBuilder {

    // 等于
    EQUALS(" = ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilder.AND + columnName + getSymbolPrefix() + SqlBuilder.createSingleParamSql(columnName);
        }
    },
    // 不等于
    NOT_EQUALS(" != ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilder.AND + columnName + getSymbolPrefix() + SqlBuilder.createSingleParamSql(columnName);
        }
    },
    // 大于
    MAX_THAN(" > ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilder.AND + columnName + getSymbolPrefix() + SqlBuilder.createSingleParamSql(columnName);
        }
    },
    // 小于
    MIN_THAN(" < ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilder.AND + columnName + getSymbolPrefix() + SqlBuilder.createSingleParamSql(columnName);
        }
    },
    // 大于等于
    MAX_EQUALS_THAN(" >= ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilder.AND + columnName + getSymbolPrefix() + SqlBuilder.createSingleParamSql(columnName);
        }
    },
    // 小于等于
    MIN_EQUALS_THAN(" <= ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilder.AND + columnName + getSymbolPrefix() + SqlBuilder.createSingleParamSql(columnName);
        }
    },
    // 模糊匹配
    LIKE(" like ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilder.AND + columnName + getSymbolPrefix() + SqlBuilder.CONCAT + SqlBuilder.LEFT_SMALL_BRACKET + SqlBuilder.createSingleParamSql(columnName) + SqlBuilder.COMMA + SqlBuilder.SINGLE_QUOTATION_MARK + SqlBuilder.PERCENT + SqlBuilder.SINGLE_QUOTATION_MARK + SqlBuilder.RIGHT_SMALL_BRACKET;
        }
    },
    // 包含
    IN(" in ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            StringBuilder sb = new StringBuilder();
            if (value instanceof List) {
                sb.append(SqlBuilder.AND).append(columnName).append(getSymbolPrefix()).append(SqlBuilder.LEFT_SMALL_BRACKET);
                List<Object> paramList = (List<Object>) value;
                for (int i = 0; i < paramList.size(); i++) {
                    sb.append(SqlBuilder.createSingleParamSql(columnName + i));
                    if (i != paramList.size() - 1) {
                        sb.append(SqlBuilder.COMMA);
                    }
                }
                sb.append(SqlBuilder.RIGHT_SMALL_BRACKET);
            }
            return sb.toString();
        }
    },
    // 范围
    BETWEEN_AND(" between ", " and ") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilder.AND + columnName + getSymbolPrefix() + SqlBuilder.createSingleParamSql(columnName + 0) + getSymbolSuffix() + SqlBuilder.createSingleParamSql(columnName + 1);
        }
    };

    // 操作符号前缀
    private String symbolPrefix;

    // 操作符号后缀
    private String symbolSuffix;

    QueryType(String symbolPrefix, String symbolSuffix) {
        this.symbolPrefix = symbolPrefix;
        this.symbolSuffix = symbolSuffix;
    }

    public String getSymbolPrefix() {
        return symbolPrefix;
    }

    public String getSymbolSuffix() {
        return symbolSuffix;
    }

}
