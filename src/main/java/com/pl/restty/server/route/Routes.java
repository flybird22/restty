package com.pl.restty.server.route;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pl.restty.server.annotation.AnnoMethodInfo;
import com.pl.restty.server.annotation.AnnotationUtil;
import com.pl.restty.server.annotation.Controller;
import com.pl.restty.server.annotation.PackageUtil;
import com.pl.restty.server.annotation.UrlComponent;
import com.pl.restty.server.annotation.UrlMapping;
import com.pl.restty.server.handlers.HttpRequestWrapper;



public class Routes {
	private static List<AnnoMethodInfo> routes;
	
	public static void scanPackage(String packageName) throws Exception {
		// TODO Auto-generated method stub
		routes = AnnotationUtil.getAnnotations(packageName, Controller.class, UrlMapping.class);
	}
	
	public static List<AnnoMethodInfo> routes(){
		return routes;
	}
	
	public static AnnoMethodInfo getRouteMethod(HttpRequestWrapper request){
		for(AnnoMethodInfo am : routes){
			String uri = request.path();			
			//验证请求内容类型Content-Type
			String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
			String defType = am.annoKeyValues.get("contentType");

			/**
			 * 预设的path，可能存在正则表达式 如： aaa/bbb/{id}/file.html
			 */
			String path = am.annoKeyValues.get("path");
			if( UrlComponent.isRegexUrl(path) ){
				String pathPrefix = path.substring(0,path.indexOf("{"));
				if(!uri.startsWith(pathPrefix)) continue;
				String pathSuffix = path.substring( path.indexOf("}") + 1 );
				//前后缀都符合，且存在{param}的数值,避免/user/add匹配到/user/{id}/add的模式
				if(pathSuffix!=null && uri.endsWith(pathSuffix) 
						&& uri.length()>(pathPrefix.length()+pathSuffix.length()) 
						//限定方法一致 get=get post=post
						&& am.annoKeyValues.get("method").equals(request.method().toString())
						//限定的请求内容一致，或者不限定内容
						&& (defType.length()==0 || (contentType!=null && contentType.contains(defType)))){
					return am;
				}
				else{
					return null;
				}
			}
			
			if(path.equals(uri) && am.annoKeyValues.get("method").equals(request.method().toString())
					//限定的请求内容一致，或者不限定内容，如果限定了内容，则必须吻合
					&& (defType.length()==0 || (contentType!=null && contentType.contains(defType)))){
				return am;
			}
		}
		return null;
	}
	

	
	
	
//	public static void main(String[] args) throws Exception{
////		List<AnnoMethodInfo> routes = Routes.getAnnotations("com.pl.restty.controllers", Controller.class, UrlMapping.class);
////		for(AnnoMethodInfo ai : routes){
////			System.out.println( ai );
////		}
//		
//	}
	

}
