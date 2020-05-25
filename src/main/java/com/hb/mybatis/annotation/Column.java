package com.hb.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段注解
 *
 * @author Mr.Huang
 * @version v0.1, Column.java, 2020/5/25 14:56, create by huangbiao.
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /**
     * 字段名
     *
     * @return 字段名
     */
    String value();

}
