package com.pl.restty.server.utils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.CharsetUtil;

import java.util.function.Function;
import java.util.function.Supplier;

import net.sf.json.JSONObject;

import com.pl.restty.server.handlers.HttpRequestWrapper;
import com.pl.restty.server.resource.MimeType;


public class RequestUtil {
	
	
	public static  Function<HttpRequestWrapper, JSONObject> parseJsonContent = (request)->{
		String headValue = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
		if(MimeType.isJsonType(headValue)){
			String jsonString;
			try {
				JSONObject json = JSONObject.fromObject(request.content());
				return json;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
		return null;
		
	};
}
