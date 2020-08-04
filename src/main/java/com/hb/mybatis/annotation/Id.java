package com.hb.mybatis.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * id注解
 *
 * @version v0.1, 2020/8/4 9:26, create by huangbiao.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {

    @AliasFor("columnName")
    String value() default "";

    @AliasFor("value")
    String columnName() default "";

}
