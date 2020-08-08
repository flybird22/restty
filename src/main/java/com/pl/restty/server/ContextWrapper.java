package com.pl.restty.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.pl.restty.server.config.Configuration;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class ContextWrapper {
	public Configuration serverConfig =null;
	public ChannelHandlerContext ctx;
	public FullHttpRequest request;
	public Map<String,Object> attributes=new ConcurrentHashMap<String, Object>();
	
	public static ContextWrapper create() {
		// TODO Auto-generated method stub
		return new ContextWrapper();
	}
	
	public ContextWrapper(){
		if(serverConfig==null)
			serverConfig = HttpServer.INSTANCE.getConfig();
	}
}
