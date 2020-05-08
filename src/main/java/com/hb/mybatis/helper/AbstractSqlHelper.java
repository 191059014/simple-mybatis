package com.hb.mybatis.helper;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * sql辅助工具抽象类
 *
 * @author Mr.huang
 * @since 2020/5/7 17:04
 */
public abstract class AbstractSqlHelper {

    /**
     * 不为空判断
     *
     * @param map     map集合
     * @param message 错误消息
     */
    protected static void assertNotEmpty(Map map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        AtomicBoolean allNull = new AtomicBoolean(true);
        map.forEach((key, value) -> {
            if (value != null) {
                allNull.set(false);
            }
        });
        if (allNull.get()) {
            throw new IllegalArgumentException(message);
        }
    }

}
