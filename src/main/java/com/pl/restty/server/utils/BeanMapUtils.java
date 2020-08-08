package com.pl.restty.server.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BeanMapUtils {
	
	public static <T> T toBean(Map<String,Object> m,Class<T> beanClass) throws Exception{
		T t = beanClass.newInstance();
		Field[] fs = beanClass.getDeclaredFields();
		for(Field f: fs){
			String fieldName = f.getName();
			Object val = m.get(fieldName);
			f.setAccessible(true);
			Class<?> ft=f.getType();
			if( ft.getName().equals("int") || ft.getName().equals("java.lang.Integer") ){
				f.setInt(t, Integer.valueOf(val+""));
			}	
			else if(ft.getName().equals("long") || ft.getName().equals("java.lang.Long") ){
				f.setLong(t, Long.valueOf(val+""));
			}
			else if(ft.getName().equals("boolean") || ft.getName().equals("java.lang.Boolean") ){
				f.setBoolean(t, Boolean.valueOf(val+""));
			}
			else{
				f.set(t, val);
			}
						
		}
		return t;
	}
	
	public static <T> Map<String,Object> toMap(T bean) throws Exception{
		Field[] fs = bean.getClass().getDeclaredFields();
		Map<String,Object> m = new HashMap<>();
		
		for(Field f: fs){
			String fieldName = f.getName();
			f.setAccessible(true);
			Object val = f.get(bean);
			m.put(fieldName, val);
		}
		return m;
	}
	
//	public static Object convertMap(Class type, Map map)
//            throws IntrospectionException, IllegalAccessException,
//            InstantiationException, InvocationTargetException {
//        BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
//        Object obj = type.newInstance(); // 创建 JavaBean 对象
//
//        // 给 JavaBean 对象的属性赋值
//        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
//        for (int i = 0; i< propertyDescriptors.length; i++) {
//            PropertyDescriptor descriptor = propertyDescriptors[i];
//            String propertyName = descriptor.getName();
//
//            if (map.containsKey(propertyName)) {
//                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
//                Object value = map.get(propertyName);
//
//                Object[] args = new Object[1];
//                args[0] = value;
//
//                descriptor.getWriteMethod().invoke(obj, args);
//            }
//        }
//        return obj;
//    }

}
