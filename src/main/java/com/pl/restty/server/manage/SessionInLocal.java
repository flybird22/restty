package com.pl.restty.server.manage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pl.restty.server.utils.DatetimeUtils;
import com.pl.restty.server.utils.JavaMachineUtils;
import com.pl.restty.server.utils.RandomUtil;

public class SessionInLocal implements HttpSessionWrapper, SessionBuilder {
	String sessionId;
	private Logger Log = LoggerFactory.getLogger(SessionInLocal.class);
	
	@Override
	public void createSession() {
		// TODO Auto-generated method stub
		sessionId = RandomUtil.randomString(6)+DatetimeUtils.dateFormat("yyMMddHHmmss")+
				JavaMachineUtils.getPid();
		Map<String,Object> m = new HashMap<>();
		m.put("create_time", System.currentTimeMillis());
		SessionStored.instance().sessions().put(sessionId,m);
		Log.info( "SessionInLocal.createSession" );
	}

	@Override
	public void deleteSession() {
		// TODO Auto-generated method stub
		SessionStored.instance().sessions().remove(sessionId);
	}

	@Override
	public void setSessionId(String sessionId) {
		// TODO Auto-generated method stub
		this.sessionId = sessionId;
	}

	@Override
	public String getSessionId() {
		// TODO Auto-generated method stub
		return sessionId;
	}

	@Override
	public void set(String key, Object value) {
		// TODO Auto-generated method stub
		if(SessionStored.instance().sessions().get(sessionId)==null){
			Map<String,Object> m = new HashMap<>();
			m.put("create_time", System.currentTimeMillis());
			m.put(key,value);
			SessionStored.instance().sessions().put(sessionId,m);
		}
		else{
			SessionStored.instance().sessions().get(sessionId).put(key, value);
		}				
	}

	@Override
	public Object get(String key) {
		// TODO Auto-generated method stub
		return SessionStored.instance().sessions().get(sessionId).get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getT(String key) throws Exception {
		// TODO Auto-generated method stub
		return (T)SessionStored.instance().sessions().get(sessionId).get(key);
	}
	
	@Override
	public int getExpiredTime() {
		// TODO Auto-generated method stub
		return (int) SessionStored.instance().sessions().get(sessionId).get("expired_time");
	}

	@Override
	public void setExpiredTime(int expiredTime) {
		// TODO Auto-generated method stub
		SessionStored.instance().sessions().get(sessionId).put("expired_time",expiredTime);
	}

	@Override
	public boolean isExpired() {
		// TODO Auto-generated method stub
		long create_t = (long) SessionStored.instance().sessions().get(sessionId).get("create_time");
		int expired_t = getExpiredTime();		
		return System.currentTimeMillis() > (create_t + expired_t);
	}


	
}
