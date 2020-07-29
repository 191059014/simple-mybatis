package com.hb.mybatis.base;

import com.hb.mybatis.common.Consts;
import com.hb.mybatis.model.PageResult;
import com.hb.mybatis.sql.Query;
import com.hb.mybatis.sql.Where;
import com.hb.unic.logger.Logger;
import com.hb.unic.logger.LoggerFactory;
import com.hb.unic.logger.util.LogExceptionWapper;
import com.hb.unic.util.util.ReflectUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * service的公共抽象类
 *
 * @version v0.1, 2020/7/24 14:50, create by huangbiao.
 */
public abstract class SimpleMybatisService {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMybatisService.class);

    /**
     * 公共数据库操作类
     */
    @Autowired
    private DmlMapper dmlMapper;

    /**
     * 通过QueryCondition来查询
     *
     * @param entityClass 实体类
     * @param query       QueryCondition查询对象
     * @return 集合
     */
    protected <T> T selectOne(Class<T> entityClass, Query query) {
        return dmlMapper.selectOne(entityClass, query);
    }

    /**
     * 通过QueryCondition来查询
     *
     * @param entityClass 实体类
     * @param query       QueryCondition查询对象
     * @return 集合
     */
    protected <T> List<T> selectList(Class<T> entityClass, Query query) {
        return dmlMapper.selectList(entityClass, query);
    }

    /**
     * 查询总条数
     *
     * @param entityClass 实体类
     * @param query       查询条件
     * @return 总条数
     */
    protected <T> int selectCount(Class<T> entityClass, Query query) {
        return dmlMapper.selectCount(entityClass, query);
    }

    /**
     * 分页查询集合
     *
     * @param entityClass 实体类
     * @param query       查询对象
     * @return 分页集合
     */
    protected <T> PageResult<T> selectPages(Class<T> entityClass, Query query) {
        return dmlMapper.selectPages(entityClass, query);
    }

    /**
     * 自定义sql语句动态查询，要求写全where前面的sql
     *
     * @param sqlStatementBeforeWhere where前面的sql语句
     * @param whereCondition          where条件
     * @return 结果集合
     */
    protected List<Map<String, Object>> customSelect(String sqlStatementBeforeWhere, Where whereCondition) {
        return dmlMapper.customSelect(sqlStatementBeforeWhere, whereCondition);
    }

    /**
     * 选择性插入
     *
     * @param entity 实体类对象
     * @return 插入行数
     */
    protected <T> int insertBySelective(T entity) {
        return dmlMapper.insertBySelective(entity);
    }

    /**
     * 选择性更新
     *
     * @param entity         实体类对象
     * @param whereCondition 条件
     * @return 更新行数
     */
    protected <T> int updateBySelective(T entity, Where whereCondition) {
        return dmlMapper.updateBySelective(entity, whereCondition);
    }

    /**
     * 逻辑删除
     *
     * @param entityClass    实体类对象
     * @param whereCondition 条件
     * @return 删除行数
     */
    protected <T> int logicDelete(Class<T> entityClass, Where whereCondition) {
        try {
            T t = entityClass.newInstance();
            ReflectUtils.setPropertyValue(Consts.RECORD_STATUS, Consts.RECORD_STATUS_INVALID, t);
            return dmlMapper.updateBySelective(t, whereCondition);
        } catch (Exception e) {
            LOGGER.error("逻辑删除异常：{}", LogExceptionWapper.getStackTrace(e));
            return 0;
        }

    }

}

    