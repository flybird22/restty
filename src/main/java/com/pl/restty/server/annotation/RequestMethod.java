package com.pl.restty.server.annotation;

import io.netty.handler.codec.http.HttpMethod;

public enum RequestMethod {
	
	GET,POST,DELETE,PUT,HEAD,OPTIONS,TRACE,CONNECT,LINK,UNLINK;
	
	public static boolean equal(HttpMethod m1,RequestMethod m2){
		
		if(m1.toString().equals(m2.toString())){
			return true;
		}
		return false;		
	}
	
//	@Override
//	public boolean equals(String method){
//		return this.name().equals(method);
//	}
	
	@Override
	public String toString(){
		return this.name();
	}
	
	public void test(){
		
	}
	

}
