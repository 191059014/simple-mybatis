package com.hb.mybatis.config;

import com.hb.unic.util.util.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;

import java.util.Map;

/**
 * map结果集包装类
 *
 * @author Mr.huang
 * @since 2020/5/9 13:40
 */
public class MyMapWrapper extends MapWrapper {

    MyMapWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject, map);
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return useCamelCaseMapping && name.contains("_") ? StringUtils.underline2Hump(name) : name;
    }

}
