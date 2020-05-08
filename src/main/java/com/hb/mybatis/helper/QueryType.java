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
public enum QueryType implements QueryBuilder {

    // 等于
    EQUALS(" = ", "") {
        @Override
        public String buildSql(String key, Object value) {
            return SqlBuilderUtils.AND + key + getSymbolPrefix() + SqlBuilderUtils.createSingleParamSql(key);
        }

        @Override
        public Map<String, Object> buildConditions(String key, Object value) {
            return SqlBuilderUtils.createSingleConditionMap(key, value);
        }

    },
    // 不等于
    NOT_EQUALS(" != ", "") {
        @Override
        public String buildSql(String key, Object value) {
            return SqlBuilderUtils.AND + key + getSymbolPrefix() + SqlBuilderUtils.createSingleParamSql(key);
        }

        @Override
        public Map<String, Object> buildConditions(String key, Object value) {
            return SqlBuilderUtils.createSingleConditionMap(key, value);
        }

    },
    // 大于
    MAX_THAN(" > ", "") {
        @Override
        public String buildSql(String key, Object value) {
            return SqlBuilderUtils.AND + key + getSymbolPrefix() + SqlBuilderUtils.createSingleParamSql(key);
        }

        @Override
        public Map<String, Object> buildConditions(String key, Object value) {
            return SqlBuilderUtils.createSingleConditionMap(key, value);
        }

    },
    // 小于
    MIN_THAN(" < ", "") {
        @Override
        public String buildSql(String key, Object value) {
            return SqlBuilderUtils.AND + key + getSymbolPrefix() + SqlBuilderUtils.createSingleParamSql(key);
        }

        @Override
        public Map<String, Object> buildConditions(String key, Object value) {
            return SqlBuilderUtils.createSingleConditionMap(key, value);
        }

    },
    // 模糊匹配
    LIKE(" like ", "") {
        @Override
        public String buildSql(String key, Object value) {
            return SqlBuilderUtils.AND + key + getSymbolPrefix() + SqlBuilderUtils.SINGLE_QUOTATION_MARK + value + SqlBuilderUtils.PERCENT + SqlBuilderUtils.SINGLE_QUOTATION_MARK;
        }

        @Override
        public Map<String, Object> buildConditions(String key, Object value) {
            return SqlBuilderUtils.createSingleConditionMap(key, value);
        }

    },
    // 包含
    IN(" in ", "") {
        @Override
        public String buildSql(String key, Object value) {
            StringBuilder sb = new StringBuilder();
            if (value instanceof List) {
                sb.append(SqlBuilderUtils.AND).append(key).append(getSymbolPrefix()).append(SqlBuilderUtils.LEFT_SMALL_BRACKET);
                List<Object> paramList = (List<Object>) value;
                for (int i = 0; i < paramList.size(); i++) {
                    sb.append(SqlBuilderUtils.createSingleParamSql(key + i));
                    if (i != paramList.size() - 1) {
                        sb.append(SqlBuilderUtils.COMMA);
                    }
                }
                sb.append(SqlBuilderUtils.RIGHT_SMALL_BRACKET);
            }
            return sb.toString();
        }

        @Override
        public Map<String, Object> buildConditions(String key, Object value) {
            Map<String, Object> map = new HashMap<>();
            if (value instanceof List) {
                List<Object> paramList = (List<Object>) value;
                for (int i = 0; i < paramList.size(); i++) {
                    map.putAll(SqlBuilderUtils.createSingleConditionMap(key + i, paramList.get(i)));
                }
            }
            return map;
        }

    },
    // 范围
    BETWEEN_AND(" between ", " and ") {
        @Override
        public String buildSql(String key, Object value) {
            return SqlBuilderUtils.AND + key + getSymbolPrefix() + SqlBuilderUtils.createSingleParamSql(key + 0) + getSymbolSuffix() + SqlBuilderUtils.createSingleParamSql(key + 1);
        }

        @Override
        public Map<String, Object> buildConditions(String key, Object value) {
            Map<String, Object> map = new HashMap<>();
            if (value instanceof List) {
                List<Object> paramList = (List<Object>) value;
                map.putAll(SqlBuilderUtils.createSingleConditionMap(key + 0, paramList.get(0)));
                map.putAll(SqlBuilderUtils.createSingleConditionMap(key + 1, paramList.get(1)));
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
