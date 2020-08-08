package com.pl.restty.server.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



public class AnnotationUtil {

	/**
	 * 
	 * @param packageName
	 * @param scanAnnoClass 检测注解类 如：@@Controller
	 * @param scanAnnoMethod 注解方法类 @@RequestMapping
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static final List<AnnoMethodInfo> getAnnotations(String packageName, 
			Class<? extends Annotation> scanAnnoClass,
			Class<? extends Annotation> scanAnnoMethod
			) 
			throws Exception{
		
		List<AnnoMethodInfo> infos = new ArrayList<>();
		
		List<String> classNames = PackageUtil.getClassName(packageName, true);
		for(String class_name:classNames){
			Class<?> c=AnnotationUtil.class.getClassLoader().loadClass(class_name);
			//class上是否有controller注解,没有继续；有则解析方法注解是否有@UrlMapping
			if(c.isAnnotationPresent( scanAnnoClass )){
				for( Method method : c.getDeclaredMethods() ){
					if(method.isAnnotationPresent( scanAnnoMethod )){
						Annotation anno = method.getAnnotation(scanAnnoMethod);
						if(anno==null) continue;
						Class<?>[] paramClassTypes = method.getParameterTypes();
						AnnoMethodInfo ami = AnnoMethodInfo.newInstance();
						ami.className = class_name;
						ami.methodName = method.getName();
						ami.methodAnnoName = anno.toString();
						ami.methodParamTypes = paramClassTypes;
						ami.methodReturnType = method.getReturnType();
						ami.annoKeyValues = new HashMap<>();
						InvocationHandler invo = Proxy.getInvocationHandler(anno);
						Field field = invo.getClass().getDeclaredField("memberValues");
						field.setAccessible(true); 
//		                field.get(invo);
						/**
						 * path=/aaa/bbb,method=Request.GET
						 */
//						ami.annoKeyValues.putAll((Map<String,String>) field.get(invo));
						Map<String,Object>fm= (Map<String, Object>) field.get(invo);
						for(Entry<String,Object> e: fm.entrySet()){
							ami.annoKeyValues.put(e.getKey(), e.getValue().toString());
						}
						/**
						 * 解析key-value中的value是否存在类似{id}的正则表达式
						 * path=/aaa/bbb/{id}
						 */
						for(Entry<String,String> e: ami.annoKeyValues.entrySet()){
						
							if( UrlComponent.isRegexUrl( e.getValue().toString() ) ){
								List<String> ps = UrlComponent.getRegExParamNames(e.getValue());
								ami.regexParams=ps;
							}
						}
						infos.add(ami);
					}
				}
			}
		}
		return infos;
	}
	
	
}
