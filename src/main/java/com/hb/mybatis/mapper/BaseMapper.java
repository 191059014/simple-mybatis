package com.hb.mybatis.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * ========== mapper基类 ==========
 *
 * @author Mr.huang
 * @version com.hb.cp.dao.mapper.BaseMapper.java, v1.0
 * @date 2019年10月12日 11时30分
 */
public interface BaseMapper {

    /**
     * 动态条件查询集合
     *
     * @param sqlStatement sql语句
     * @param params       条件集合
     * @return 结果集合
     */
    List<Map<String, Object>> dynamicSelect(@Param("sqlStatement") String sqlStatement, @Param("params") Map<String, Object> params);

    /**
     * 查询总条数
     *
     * @param sqlStatement sql语句
     * @param params       条件集合
     * @return 结果集合
     */
    int selectCount(@Param("sqlStatement") String sqlStatement, @Param("params") Map<String, Object> params);

    /**
     * 选择性插入
     *
     * @param sqlStatement sql语句
     * @param params       条件集合
     * @return 插入的条数
     */
    int insertSelective(@Param("sqlStatement") String sqlStatement, @Param("params") Map<String, String> params);

    /**
     * 选择性更新
     *
     * @param sqlStatement sql语句
     * @param cloumns      属性值集合
     * @param params       条件集合
     * @return 更新的条数
     */
    int updateSelectiveByPrimaryKey(@Param("sqlStatement") String sqlStatement, @Param("columns") Map<String, String> cloumns, @Param("params") Map<String, Object> params);

    /**
     * 选择性删除
     *
     * @param sqlStatement sql语句
     * @param params       条件集合
     * @return 更新的条数
     */
    int deleteBySelective(@Param("sqlStatement") String sqlStatement, @Param("params") Map<String, String> params);

}
