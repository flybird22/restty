package com.pl.restty.server.handlers;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.pl.restty.server.ContextWrapper;
import com.pl.restty.server.resource.MimeType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class HttpResponseWrapper {

	FullHttpResponse response;
	String content="";
	HttpResponseStatus status=HttpResponseStatus.OK;
	ChannelHandlerContext ctx;
	Map<String,String> headers = new HashMap<>();
	boolean isDenied = false; // for filter deny request
	byte[] outputBuffer=null;
	
	public HttpResponseWrapper(ContextWrapper context) {
		// TODO Auto-generated constructor stub
		this.ctx = context.ctx;		
		if(context.request.headers().contains(HttpHeaderNames.CONNECTION) &&
				context.request.headers().get(HttpHeaderNames.CONNECTION).equals(HttpHeaderValues.KEEP_ALIVE)){
    		headers().put(HttpHeaderNames.CONNECTION+"", HttpHeaderValues.KEEP_ALIVE+"");
    	}
		headers.put(HttpHeaderNames.CONTENT_TYPE+"", MimeType.HTML);
	}

	public Map<String,String>headers(){
		return headers;
	}
	
	public void setHeader(String key,String value){
		headers.put(key, value);
	}
	
	public void setContent(String str){
		content = str;
		try {
			headers.put(HttpHeaderNames.CONTENT_LENGTH+"",content.getBytes("utf-8").length+"");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void status( HttpResponseStatus code ){
		status = code;
	}

	public void release(){
		headers.clear();
		headers=null;
		content=null;
	}

	public boolean isDenied() {
		return isDenied;
	}

	public void setDenied(boolean isDenied) {
		this.isDenied = isDenied;
		status(HttpResponseStatus.FORBIDDEN);
	}

	public void setContentType(String type) {
		// TODO Auto-generated method stub
		headers().put(HttpHeaderNames.CONTENT_TYPE+"", type);
	}

	public void setOutputBuffer(byte[] buf) {
		// TODO Auto-generated method stub
		outputBuffer = buf;
	}

	public void redirect(String uri) {
		// TODO Auto-generated method stub
		headers().put(HttpHeaderNames.LOCATION+"", uri);
		status(HttpResponseStatus.FOUND);
	}

	
}
