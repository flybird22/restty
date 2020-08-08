package com.pl.restty.server.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringMatcherUtils {
	
	public static String[] match(String source,String regex){
		Matcher m=Pattern.compile(regex).matcher(source);
		if(!m.find()) return null;
		String[] result = new String[m.groupCount()];
		for(int i=0; i< m.groupCount(); i++){
			result[i] = m.group(i+1);
		}
		return result;
	}

	public static void main(String[] args){
		System.out.println( StringMatcherUtils.match("reids://127.0.0.1:6777", "(\\w+)://(.*):(\\d+)/(.*)") );
	}
}
