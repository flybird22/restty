package com.pl.restty.server.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlComponent {
	
	public String path=null;
	public Map<String,String> params=null;
	
	
	public static final UrlComponent parse(String uri) {
		// TODO Auto-generated method stub
		final String[] components = uri.split("\\?");
		final String path = components[0];
		final String params = components.length>1?components[1]:null;
		UrlComponent urlcomp = new UrlComponent(); 
//		urlcomp.paths = path==null?null : path.split("/");
		urlcomp.path = path;
		
		if(params!=null){
			Map<String,String> m=new HashMap<String,String>();
			String[] kv = params.split("&");
			for(String s : kv){
				String[] _s = s.split("=");
				m.put(_s[0], _s[1]);
			}
			urlcomp.params=m;
		}
		
		return urlcomp;
	}

/**
 * 
 * @param uri /aaa/bbb/1234455/delete
 * @param routePath /aaa/bbb/{id}/{action}
 * @param paramName id
 * @return
 */
	public static String getValueByRegexName(String uri, String routePath,String paramName) {
		// TODO Auto-generated method stub
		String[] source = uri.split("/");
		String[] route = routePath.split("/");
		//查找参数索引，获取对应的数值
		for(int i=0;i<route.length;i++){
			//route中找到{id}
			if( route[i].equals( "{"+paramName+"}" ) ){
				return source[i];
			}
		}
		return null;
	}
	
	public static boolean isRegexUrl(String path){
		if(path.indexOf("{")>0){
			return true;
		}
		return false;
	}
	
	/**
	 * {id}/{user}/{name}
	 * @param url
	 * @return [id,user,name]
	 */
	public static List<String> getRegExParamNames(String url){
		List<String> names=new ArrayList<String>();
		Pattern p = Pattern.compile( "\\{(\\w+)\\}" );
		Matcher m = p.matcher(url);
		while(m.find()) {
//	         System.out.println(m.group(1));
			names.add( m.group(1) );
		}
		return names;
	}
	
//	public static void main(String[] args){
//		String path="/user/add?name=john";
//		UrlComponent.parse(path);
////		String path="/aaa/bbb/{id}/{ss}";
////		String url = "/aaa/bbb/123456/";
////		
////		Pattern p = Pattern.compile( "\\{(\\w+)\\}" );
////		Matcher m = p.matcher(path);
////		int count=0;
////		System.out.println("groupCount "+m.groupCount());
////
////		while(m.find()) {
////	         System.out.println(m.group(1));	         
////	      }
//	}

}
