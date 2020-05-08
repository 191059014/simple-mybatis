package com.hb.mybatis.base;

import com.hb.mybatis.helper.DeleteHelper;
import com.hb.mybatis.helper.InsertHelper;
import com.hb.mybatis.helper.QueryCondition;
import com.hb.mybatis.helper.UpdateHelper;
import com.hb.mybatis.mapper.BaseMapper;
import com.hb.mybatis.model.PagesResult;
import com.hb.unic.util.util.CloneUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * ========== dml操作数据库类 ==========
 *
 * @author Mr.huang
 * @version com.hb.cp.dao.base.DmlMapper.java, v1.0
 * @date 2019年10月12日 14时09分
 */
@Component
public class DmlMapper {

    /**
     * mapper基类
     */
    @Autowired
    private BaseMapper baseMapper;

    /**
     * 通过QueryCondition来查询
     *
     * @param entityClass 实体类
     * @param query       QueryCondition查询对象
     * @param <T>         实体类
     * @return 集合
     */
    public <T> List<T> dynamicSelect(Class<T> entityClass, QueryCondition query) {
        List<Map<String, Object>> result = baseMapper.dynamicSelect(query.getSimpleSql(), query.getParams());
        return CloneUtils.maps2Beans(result, entityClass);
    }

    /**
     * 查询总条数
     *
     * @param query 查询条件
     * @return 总条数
     */
    public <T> int selectCount(QueryCondition query) {
        return baseMapper.selectCount(query.getCountSql(), query.getParams());
    }

    /**
     * 分页查询集合
     *
     * @param entityClass 实体类
     * @param query       查询对象
     * @return 分页集合
     */
    public <T> PagesResult<T> selectPages(Class<T> entityClass, QueryCondition query) {
        int count = selectCount(query);
        List<Map<String, Object>> queryResult = baseMapper.dynamicSelect(query.getSimpleSql(), query.getParams());
        List<T> tList = CloneUtils.maps2Beans(queryResult, entityClass);
        return new PagesResult<>(tList, count, query.getLimitStartRows(), query.getLimitPageSize());
    }

    /**
     * 自定义sql语句动态查询，要求写全sql
     *
     * @param sqlStatement sql语句
     * @param entityClass  实体类
     * @param conditions   条件
     * @return 结果集合
     */
    public <T> List<T> customSelect(String sqlStatement, Class<T> entityClass, Map<String, Object> conditions) {
        List<Map<String, Object>> result = baseMapper.dynamicSelect(sqlStatement, conditions);
        return CloneUtils.maps2Beans(result, entityClass);
    }

    /**
     * 选择性插入
     *
     * @param tableName 表名
     * @param entity    实体类对象
     * @return 插入行数
     */
    public <T> int insertBySelective(String tableName, T entity) {
        if (entity == null) {
            return 0;
        }
        Map<String, String> property = CloneUtils.bean2Map(entity);
        if (property == null || property.isEmpty()) {
            return 0;
        }
        String sqlStatement = InsertHelper.buildInsertSelectiveSql(tableName, property);
        return baseMapper.insertSelective(sqlStatement, property);
    }

    /**
     * 选择性更新
     *
     * @param tableName  表名
     * @param entity     实体类对象
     * @param conditions 条件
     * @return 更新行数
     */
    public <T> int updateBySelective(String tableName, T entity, Map<String, Object> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return 0;
        }
        Map<String, String> property = CloneUtils.bean2Map(entity);
        if (property == null || property.isEmpty()) {
            return 0;
        }
        String sqlStatement = UpdateHelper.buildUpdateSelectiveSql(tableName, property, conditions);
        return baseMapper.updateSelectiveByPrimaryKey(sqlStatement, property, conditions);
    }

    /**
     * 选择性删除
     *
     * @param tableName  表名
     * @param conditions 条件
     * @return 删除行数
     */
    public int deleteBySelective(String tableName, Map<String, Object> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return 0;
        }
        String sqlStatement = DeleteHelper.buildDeleteSelectiveSql(tableName, conditions);
        return baseMapper.deleteBySelective(sqlStatement, conditions);
    }

}
