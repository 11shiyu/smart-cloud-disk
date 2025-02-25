package com.yupi.yupicturebackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Target 注解生效范围，ElementType.METHOD 在方法内生效
 * Retention(RetentionPolicy.RUNTIME) 在运行时生效
 * 这是使用的 自定义注解，声明式编程 不需要具体实现方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须有某个角色 （也就是用户 必须登录）
     */
    String mustRole() default "";
}
