package com.pl.restty.server.route;

import java.util.ArrayList;
import java.util.List;

import com.pl.restty.server.annotation.AnnoMethodInfo;
import com.pl.restty.server.annotation.AnnotationUtil;
import com.pl.restty.server.annotation.BeforeFilter;
import com.pl.restty.server.annotation.Controller;
import com.pl.restty.server.annotation.UrlMapping;

public class Filters {
	private static List<AnnoMethodInfo> filters;
	
	public static final List<AnnoMethodInfo> filters(){
		return filters;
	}
	
	public static void scanPackage(String packageName) throws Exception {
		// TODO Auto-generated method stub
		filters = AnnotationUtil.getAnnotations(packageName, Controller.class, BeforeFilter.class);
	}
	
//	public static List<AnnoMethodInfo> getFilterMethod(String uri){
//		List<AnnoMethodInfo> ams= new ArrayList<>();
//		for(AnnoMethodInfo am : filters){
//			
//		}
//	}
}
