package com.srwing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * Created by srwing
 * Date: 2022/5/19
 * Email: 694177407@qq.com
 */
//声明注解是放到什么上面的，作用域：TYPE为类
@Target(ElementType.TYPE)
//注解在代码中存在多久取决于这个生命周期
//代码的生命周期：源码期，编译期，运行期，CLASS：为编译期
@Retention(RetentionPolicy.CLASS)
public @interface BindPath {
    //map中的key的类型
    String value();
}
