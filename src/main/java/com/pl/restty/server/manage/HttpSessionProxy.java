package com.pl.restty.server.manage;

import com.pl.restty.server.HttpServer;

/**
 * createSession deleteSession getValue setValue deleteValue isExpired
 * @author pei
 *
 */
public class HttpSessionProxy {
	private static HttpSessionProxy instance_=null;
	String proxyConnectString=null;
	
	/**
	 * redis://127.0.0.1:6379/appname
	 * @param proxyConnectString
	 */
	public static HttpSessionProxy instance(){
		if(instance_==null){
			instance_ = new HttpSessionProxy();
//			instance_.connect(null);
		}
		return instance_;
	}
	
	/**
	 * HttpServer 外部调用 
	 * @param proxyConnectString
	 */
	public void connect(String proxyConnectString){		
		this.proxyConnectString = proxyConnectString;	
		
	}
	
	private Object proxy() throws Exception{
		Object proxy=null;
		if(proxyConnectString==null || proxyConnectString.equals("local")){
			proxy = new SessionInLocal();
		}
		else if(proxyConnectString.contains("redis")){
			proxy  = new SessionInRedis(proxyConnectString);			
		}
		return proxy;
	}

	private SessionBuilder create() throws Exception {
		// TODO Auto-generated method stub
		SessionBuilder sb = (SessionBuilder) proxy();
		sb.createSession();
		return sb;
	}

	public HttpSessionWrapper get(String sessionId) throws Exception {
		// TODO Auto-generated method stub
		HttpSessionWrapper session=null;
		if(sessionId==null) {
			session = (HttpSessionWrapper) create();
		}
		else{
			session =(HttpSessionWrapper) proxy();
			session.setSessionId(sessionId);
		}
		return session;
	}

	
}
