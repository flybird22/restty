package com.pl.restty.server.annotation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AnnoMethodInfo {
	public String className;
	public String methodName;
	public Class<?>[] methodParamTypes;
	public Class<?> methodReturnType;
	public String methodAnnoName;
	public Map<String,String> annoKeyValues;
	public List<String>regexParams;
	
	public static AnnoMethodInfo newInstance(){
		return new AnnoMethodInfo();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
//		return super.toString();
		return Arrays.asList(className,methodName,Arrays.asList(methodParamTypes),
				methodReturnType,methodAnnoName,annoKeyValues).toString();
	}
	
	

}
