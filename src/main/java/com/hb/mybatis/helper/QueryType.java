package com.hb.mybatis.helper;

import com.hb.mybatis.util.SqlBuilderUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询类型
 *
 * @author Mr.huang
 * @since 2020/5/8 11:14
 */
public enum QueryType implements SingleWhereBuilder {

    // 等于
    EQUALS(" = ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilderUtils.AND + columnName + getSymbolPrefix() + SqlBuilderUtils.createSingleParamSql(columnName);
        }

        @Override
        public Map<String, Object> buildConditions(String columnName, Object value) {
            return SqlBuilderUtils.createSingleConditionMap(columnName, value);
        }

    },
    // 不等于
    NOT_EQUALS(" != ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilderUtils.AND + columnName + getSymbolPrefix() + SqlBuilderUtils.createSingleParamSql(columnName);
        }

        @Override
        public Map<String, Object> buildConditions(String columnName, Object value) {
            return SqlBuilderUtils.createSingleConditionMap(columnName, value);
        }

    },
    // 大于
    MAX_THAN(" > ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilderUtils.AND + columnName + getSymbolPrefix() + SqlBuilderUtils.createSingleParamSql(columnName);
        }

        @Override
        public Map<String, Object> buildConditions(String columnName, Object value) {
            return SqlBuilderUtils.createSingleConditionMap(columnName, value);
        }

    },
    // 小于
    MIN_THAN(" < ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilderUtils.AND + columnName + getSymbolPrefix() + SqlBuilderUtils.createSingleParamSql(columnName);
        }

        @Override
        public Map<String, Object> buildConditions(String columnName, Object value) {
            return SqlBuilderUtils.createSingleConditionMap(columnName, value);
        }

    },

    // 大于等于
    MAX_EQUALS_THAN(" >= ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilderUtils.AND + columnName + getSymbolPrefix() + SqlBuilderUtils.createSingleParamSql(columnName);
        }

        @Override
        public Map<String, Object> buildConditions(String columnName, Object value) {
            return SqlBuilderUtils.createSingleConditionMap(columnName, value);
        }

    },
    // 小于等于
    MIN_EQUALS_THAN(" <= ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilderUtils.AND + columnName + getSymbolPrefix() + SqlBuilderUtils.createSingleParamSql(columnName);
        }

        @Override
        public Map<String, Object> buildConditions(String columnName, Object value) {
            return SqlBuilderUtils.createSingleConditionMap(columnName, value);
        }

    },
    // 模糊匹配
    LIKE(" like ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilderUtils.AND + columnName + getSymbolPrefix() + SqlBuilderUtils.CONCAT + SqlBuilderUtils.LEFT_SMALL_BRACKET + SqlBuilderUtils.createSingleParamSql(columnName) + SqlBuilderUtils.COMMA + SqlBuilderUtils.SINGLE_QUOTATION_MARK + SqlBuilderUtils.PERCENT + SqlBuilderUtils.SINGLE_QUOTATION_MARK + SqlBuilderUtils.RIGHT_SMALL_BRACKET;
        }

        @Override
        public Map<String, Object> buildConditions(String columnName, Object value) {
            return SqlBuilderUtils.createSingleConditionMap(columnName, value);
        }

    },
    // 包含
    IN(" in ", "") {
        @Override
        public String buildSql(String columnName, Object value) {
            StringBuilder sb = new StringBuilder();
            if (value instanceof List) {
                sb.append(SqlBuilderUtils.AND).append(columnName).append(getSymbolPrefix()).append(SqlBuilderUtils.LEFT_SMALL_BRACKET);
                List<Object> paramList = (List<Object>) value;
                for (int i = 0; i < paramList.size(); i++) {
                    sb.append(SqlBuilderUtils.createSingleParamSql(columnName + i));
                    if (i != paramList.size() - 1) {
                        sb.append(SqlBuilderUtils.COMMA);
                    }
                }
                sb.append(SqlBuilderUtils.RIGHT_SMALL_BRACKET);
            }
            return sb.toString();
        }

        @Override
        public Map<String, Object> buildConditions(String columnName, Object value) {
            Map<String, Object> map = new HashMap<>();
            if (value instanceof List) {
                List<Object> paramList = (List<Object>) value;
                for (int i = 0; i < paramList.size(); i++) {
                    map.putAll(SqlBuilderUtils.createSingleConditionMap(columnName + i, paramList.get(i)));
                }
            }
            return map;
        }

    },
    // 范围
    BETWEEN_AND(" between ", " and ") {
        @Override
        public String buildSql(String columnName, Object value) {
            return SqlBuilderUtils.AND + columnName + getSymbolPrefix() + SqlBuilderUtils.createSingleParamSql(columnName + 0) + getSymbolSuffix() + SqlBuilderUtils.createSingleParamSql(columnName + 1);
        }

        @Override
        public Map<String, Object> buildConditions(String columnName, Object value) {
            Map<String, Object> map = new HashMap<>();
            if (value instanceof List) {
                List<Object> paramList = (List<Object>) value;
                map.putAll(SqlBuilderUtils.createSingleConditionMap(columnName + 0, paramList.get(0)));
                map.putAll(SqlBuilderUtils.createSingleConditionMap(columnName + 1, paramList.get(1)));
            }
            return map;
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
