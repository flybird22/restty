package com.pl.restty.server.handlers;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.pl.restty.server.annotation.AnnoMethodInfo;
import com.pl.restty.server.route.Filters;

public class ServletFilter implements RequestWorker {

	@Override
	public boolean handle(HttpRequestWrapper request,
			HttpResponseWrapper response) throws Exception {
		// TODO Auto-generated method stub
		boolean b=false;
		for(AnnoMethodInfo ami : Filters.filters()){
			b=runFileter(request,response,ami);
			if(b){				
				return true;
			}
		}	
		
		return false;
	}

	private boolean runFileter(HttpRequestWrapper request,
			HttpResponseWrapper response, AnnoMethodInfo ami) throws Exception {
		// TODO Auto-generated method stub
		Object instance = Class.forName(ami.className).newInstance();
		if(instance==null) throw new Exception("cannot instance class "+ami.className);
		
		Method m=instance.getClass().getDeclaredMethod(ami.methodName, ami.methodParamTypes);
		Object[] args = new Object[ami.methodParamTypes.length];

		int i=0;
		Parameter[] ps=m.getParameters();
		for(Parameter param : m.getParameters()){
			if(param.getType().equals( HttpRequestWrapper.class )){
				args[i]=request;
			}
			else if(param.getType().equals( HttpResponseWrapper.class )){
				args[i]=response;
			}			
			i++;
		}
		
		return (boolean) m.invoke(instance, args);
		
	}
	
}
