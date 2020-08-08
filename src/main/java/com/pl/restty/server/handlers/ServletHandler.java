package com.pl.restty.server.handlers;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import com.pl.restty.server.HttpServer;
import com.pl.restty.server.annotation.AnnoMethodInfo;
import com.pl.restty.server.annotation.UrlComponent;
import com.pl.restty.server.route.Filters;
import com.pl.restty.server.route.Routes;
import com.pl.restty.server.route.StaticFileFilter;
import com.pl.restty.server.template.ModelAndView;
import com.pl.restty.server.template.TemplateEngine;

public class ServletHandler implements RequestWorker{

	public static ServletHandler create() {
		// TODO Auto-generated method stub
		return new ServletHandler();
	}
//	public void forward(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception{
//		handle(request,response);
//	}
	
	public boolean handle(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception{
		
		boolean b=false;	
		
		AnnoMethodInfo ami = Routes.getRouteMethod(request);
		if(ami !=null ){
			b=runHandler(request,response,ami);
		}else{
			response.status(HttpResponseStatus.NOT_FOUND);
			response.setContent("404 not found");
		}
		
		return b;
		
	}
	
	
	
	private boolean runHandler(HttpRequestWrapper request,
			HttpResponseWrapper response, AnnoMethodInfo ami) throws Exception {
		// TODO Auto-generated method stub		
		Object instance = Class.forName(ami.className).newInstance();
		if(instance!=null){
			Method m=instance.getClass().getDeclaredMethod(ami.methodName, ami.methodParamTypes);
			/**
			 * 获取方法的参数值，根据类型和url地址中的可变参数{}
			 */
			Object[] args = new Object[ami.methodParamTypes.length];
			int i=0;
			Parameter[] ps=m.getParameters();
			for(Parameter param : m.getParameters()){
//				System.out.println(param.getName());
				if(param.getType().equals( HttpRequestWrapper.class )){
					args[i]=request;
				}
				else if(param.getType().equals( HttpResponseWrapper.class )){
					args[i]=response;
				}
				//如果正则表达式参数存在，则从url中获取对应的数值
				//param.getName() 必须在1.8以上编译才可以 
				//preferences-》Java Compiler->设置模块字节码版本1.8 eclipse勾选参数名
				else if(ami.regexParams!=null && ami.regexParams.contains(param.getName())){
					String val = UrlComponent.getValueByRegexName(
							request.uri(),ami.annoKeyValues.get("path"),param.getName());
					args[i] = val;
				}
				i++;
			}
			
			Object ob=m.invoke(instance, args);
			if(ob instanceof ModelAndView && HttpServer.INSTANCE!=null){
				//templateEngine
//				String engineClassName = ami.annoKeyValues.get("templateEngine");
//				TemplateEngine engine = (TemplateEngine) Class.forName("com.pl.restty.server.template."+engineClassName).newInstance();
				String content = HttpServer.INSTANCE.getTemplateEngine().render(ob);
				response.setContent(content);
			}
			else if(ob instanceof String){
				response.setContent((String) ob);
			}
			
			//session
			if(request.getSession()!=null){
				String sessionId = request.getSession().getSessionId();
				response.headers().put("Set-Cookie", "sessionId="+sessionId);
			}
			return true;
		}
		return false;
	}
	
	
}
