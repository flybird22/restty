package com.pl.restty.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented //文档  
@Retention(RetentionPolicy.RUNTIME) //在运行时可以获取  
@Target({ ElementType.TYPE, ElementType.METHOD}) //作用到类，方法，接口上等  
@Inherited //子类会继承  

/**
 * 参数验证，暂时无法确定响应什么内容，某些自定义的rest服务，如果参数错误，应该返回json或者xml
 * 所以无法预先定义响应内容
 * error="{message:'错误的参数'}"
 * @author pei
 *
 */
public @interface ParamValid {

	String name();
	ParamValidMethod method();
	String error() default "";
}
