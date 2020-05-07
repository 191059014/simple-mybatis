package com.hb.mybatis.base;

import com.hb.mybatis.helper.SqlHelper;
import com.hb.mybatis.mapper.BaseMapper;
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
     * 查询唯一结果集
     *
     * @param tableName   表名
     * @param entityClass 实体类
     * @param conditions  条件
     * @param sort        排序
     * @return 唯一结果
     */
    public <T> T selectOne(String tableName, Class<T> entityClass, Map<String, Object> conditions, String sort) {
        String sqlStatement = SqlHelper.buildSelectSelectiveSql(tableName, conditions, sort, null, null);
        Map<String, Object> result = baseMapper.selectOne(sqlStatement, conditions);
        return CloneUtils.map2Bean(result, entityClass);
    }

    /**
     * 查询集合
     *
     * @param tableName   表名
     * @param entityClass 实体类
     * @param conditions  条件
     * @param sort        排序
     * @return 结果集合
     */
    public <T> List<T> selectList(String tableName, Class<T> entityClass, Map<String, Object> conditions, String sort) {
        String sqlStatement = SqlHelper.buildSelectSelectiveSql(tableName, conditions, sort, null, null);
        List<Map<String, Object>> result = baseMapper.selectList(sqlStatement, conditions);
        return CloneUtils.maps2Beans(result, entityClass);
    }

    /**
     * 查询总条数
     *
     * @param tableName  表名
     * @param conditions 条件
     * @return 总条数
     */
    public <T> int selectCount(String tableName, Map<String, Object> conditions) {
        String sqlStatement = SqlHelper.buildSelectCountSelectiveSql(tableName, conditions);
        return baseMapper.selectCount(sqlStatement, conditions);
    }

    /**
     * 分页查询集合
     *
     * @param tableName   表名
     * @param entityClass 实体类
     * @param conditions  条件
     * @param sort        排序
     * @param startRow    开始行数
     * @param pageNum     每页数量
     * @return 分页集合
     */
    public <T> List<T> selectPages(String tableName, Class<T> entityClass, Map<String, Object> conditions, String sort, Integer startRow, Integer pageNum) {
        String sqlStatement = SqlHelper.buildSelectSelectiveSql(tableName, conditions, sort, startRow, pageNum);
        List<Map<String, Object>> result = baseMapper.selectPages(sqlStatement, conditions, startRow, pageNum);
        return CloneUtils.maps2Beans(result, entityClass);
    }

    /**
     * 自定义sql语句动态查询
     *
     * @param sqlStatement sql语句
     * @param entityClass  实体类
     * @param conditions   条件
     * @return 结果集合
     */
    public <T> List<T> dynamicSelect(String sqlStatement, Class<T> entityClass, Map<String, Object> conditions) {
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
        String sqlStatement = SqlHelper.buildInsertSelectiveSql(tableName, property);
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
        String sqlStatement = SqlHelper.buildUpdateSelectiveSql(tableName, property, conditions);
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
        String sqlStatement = SqlHelper.buildDeleteSelectiveSql(tableName, conditions);
        return baseMapper.deleteBySelective(sqlStatement, conditions);
    }

}
