package com.pl.restty.server.handlers;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import com.pl.restty.server.ContextWrapper;
import com.pl.restty.server.manage.SessionStored;
import com.pl.restty.server.manage.HttpSessionProxy;
import com.pl.restty.server.manage.HttpSessionWrapper;
import com.pl.restty.server.resource.MimeType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;

public class HttpRequestWrapper {
	FullHttpRequest request;
	String remoteIpAddr;
	int port;
	Object requestJson=null;
	Map<String,Object> requestMap=null;
	List<Object> requestList=null;
	Set<Cookie> cookies;
	HttpSessionWrapper session=null;
	ContextWrapper context=null;
	String forwardUrl=null;
	
	public HttpRequestWrapper(ContextWrapper context) throws Exception{
		this.context = context;
		request = context.request;
		remoteIpAddr = request.headers().get("X-Forwarded-For");
		if(remoteIpAddr==null){
			InetSocketAddress addr = (InetSocketAddress) context.ctx.channel().remoteAddress(); //InetAddress
	    	remoteIpAddr = addr.getAddress().getHostAddress();
	    	port = addr.getPort();
		}

		if(MimeType.isJsonType( getContentType() ) ){
			String content_ = content();
			if( !StringUtils.isEmpty(content_)){
				Object json = new JSONTokener(content_).nextValue();
				if(json instanceof JSONObject)
					requestJson = JSONObject.fromObject(content_);
				else if(json instanceof JSONArray)
					requestJson = JSONArray.fromObject(content_);
					
			}
		}
		else if(MimeType.isFormType( getContentType() )){			
			String content_ = content();
			if(content_!=null){
				requestMap = new HashMap<>();
//				System.out.println(content_);
				String[] kvs = content_.split("&");
				for(String kv : kvs){
					String[] kk = kv.split("=");
					requestMap.put(kk[0], kk[1]);
				}
			}

		}
		
		if(uri().indexOf("?")>0){
			String params = request.uri().split("\\?")[1];
			if(params!=null && params.length()>0){
				String[] kvs = params.split("&");
				if(requestMap==null) requestMap=new HashMap<>();
				for(String kv : kvs){
					String[] kk = kv.split("=");
					requestMap.put(kk[0], kk[1]);
				}
			}
			
		}
		
	}
	

	private String getContentType() {
		// TODO Auto-generated method stub
		return headers().get(HttpHeaderNames.CONTENT_TYPE);
	}

	public String content(){
		return request.content().toString(CharsetUtil.UTF_8);
	}
	
	public FullHttpRequest raw(){
		return request;
	}
	
	public String remoteAddr(){
		return this.remoteIpAddr ;
	}
	
	public int port(){
		return port;
	}
	
	public HttpHeaders headers(){
		return request.headers();
	}


	public HttpMethod method(){
		return request.method();
	}
	
	public String uri(){
		return request.uri();
	}
	
	public String path(){
		int pos = request.uri().indexOf("?"); 
		return pos > 0 ? request.uri().substring(0,pos) : request.uri();
	}

	public void release() {
		// TODO Auto-generated method stub	
		if(request.refCnt()>0)
			request.release();
		remoteIpAddr = null;
	}

	public ServletHandler getDispatcher(String uri) {
		// TODO Auto-generated method stub
		request.setUri(uri);
		return new ServletHandler();
	}

	public Object getRequestJson() {
		return requestJson;
	}

	public Map<String, Object> getRequestMap() {
		return requestMap;
	}
	
	public void setRequestMap(Map<String,Object> m){
		requestMap = m;
	}
	
	public List<Object> getRequstList() {
		return requestList;
	}
	
	public void setRequestList(List<Object> lst){
		requestList = lst;
	}

	public HttpSessionWrapper getSession() throws Exception{
		if(context.serverConfig.equals("disable")){
			return null;
		}
		if(session==null){
			String sessionId = getCookieValue("sessionId");
			session = HttpSessionProxy.instance().get(sessionId);			
		}		
		return session;
	}

	public Set<Cookie> getCookie() {
		// TODO Auto-generated method stub
		if(cookies!=null) return cookies;
		String _cookie=headers().get(HttpHeaderNames.COOKIE);
		if(_cookie==null) return null;
		cookies = ServerCookieDecoder.LAX.decode(_cookie);
		return cookies;
	}
	
	
	private String getCookieValue(String cookieName){
		if(getCookie()==null) return null;
		Optional<String> val = getCookie()
				.stream().filter(cookie->cookie.name().equals(cookieName))
				.findFirst().map(Cookie::value);
		if(val.isPresent()) return val.get();
		return null;
	}


	public void forward(String uri) {
		// TODO Auto-generated method stub
		forwardUrl = uri;
		request.setUri(uri);
	}


	public String getForwardUrl() {
		return forwardUrl;
	}


	public void setForwardUrl(String forwardUrl) {
		this.forwardUrl = forwardUrl;
	}


	public ContextWrapper getContext() {
		return context;
	}


	
	
	
	
//	public String getCookieField(String key){
//		Set<Cookie> cookies = getCookie();
//		if(cookies==null) return null;
//		
//		for(Cookie c : cookies){
//			if(c.name().equals(key)) return c.value();			
//		}
//		return null;
//	}
	
}
