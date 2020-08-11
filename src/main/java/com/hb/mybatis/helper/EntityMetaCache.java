package com.hb.mybatis.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体类信息缓存
 *
 * @version v0.1, 2020/8/11 16:10, create by huangbiao.
 */
public class EntityMetaCache {

    /**
     * 实体类
     */
    private String entityName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 主键字段名
     */
    private String primaryKey;

    /**
     * 实体字段 -> 数据库字段映射集合
     */
    private Map<String, String> property2ColumnMap = new HashMap<>(16);

    /**
     * 数据库字段 -> 实体字段映射集合
     */
    private Map<String, String> column2PropertyMap = new HashMap<>(16);

    public EntityMetaCache() {
    }

    public EntityMetaCache(String entityName, String tableName, String primaryKey, Map<String, String> property2ColumnMap, Map<String, String> column2PropertyMap) {
        this.entityName = entityName;
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.property2ColumnMap = property2ColumnMap;
        this.column2PropertyMap = column2PropertyMap;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Map<String, String> getProperty2ColumnMap() {
        return property2ColumnMap;
    }

    public void setProperty2ColumnMap(Map<String, String> property2ColumnMap) {
        this.property2ColumnMap = property2ColumnMap;
    }

    public Map<String, String> getColumn2PropertyMap() {
        return column2PropertyMap;
    }

    public void setColumn2PropertyMap(Map<String, String> column2PropertyMap) {
        this.column2PropertyMap = column2PropertyMap;
    }
}

    