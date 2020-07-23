package com.hb.mybatis;

import com.hb.mybatis.common.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文
 *
 * @version v0.1, 2020/7/22 17:00, create by huangbiao.
 */
@Primary
@Component
public class SimpleMybatisContext implements InitializingBean {

    /**
     * The LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMybatisContext.class);

    /**
     * 应用名称
     */
    @Value("${simple.mybatis.hump_mapping:true}")
    private String humpMapping;

    /**
     * 配置集合
     */
    private static Map<String, String> maps = new HashMap<>(8);

    @Override
    public void afterPropertiesSet() throws Exception {
        maps.put(Consts.HUMP_MAPPING, humpMapping);
        LOGGER.info("SimpleMybatisContext accept properties: {}", maps);
    }

    /**
     * 获取属性值
     *
     * @param key 属性名
     * @return 值
     */
    public static String getValue(String key) {
        return maps.get(key);
    }

    /**
     * 获取属性值
     *
     * @param key 属性名
     * @return 值
     */
    public static boolean getBooleanValue(String key) {
        return Boolean.parseBoolean(maps.get(key));
    }

}

    