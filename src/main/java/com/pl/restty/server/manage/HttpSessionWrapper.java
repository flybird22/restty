package com.pl.restty.server.manage;

public interface HttpSessionWrapper {
	public void setSessionId(String sessionId);
	public String getSessionId();
	public void set(String key,Object value)throws Exception;
	public Object get(String key) throws Exception;
	public int getExpiredTime()throws Exception;
	public void setExpiredTime(int expiredTime)throws Exception;
	public boolean isExpired()throws Exception;
	public <T> T getT(String key) throws Exception;
	
}
