package com.pl.restty.server.manage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pl.events.SimpleTimer;
import com.pl.events.TimerQueue;
import com.pl.restty.server.HttpServer;
import com.pl.restty.server.utils.DatetimeUtils;
import com.pl.restty.server.utils.JavaMachineUtils;
import com.pl.restty.server.utils.RandomUtil;
import com.pl.restty.server.utils.SerializeUtils;

public class SessionStored {
	private static SessionStored instance_ = null;
	private Map<String, Map<String,Object> > sessions=new ConcurrentHashMap<>();
	String storedfile=System.getProperty("user.dir")+"/session.dat";
	SimpleTimer timer;
	private Logger Log = LoggerFactory.getLogger(SessionStored.class);
	public static SessionStored instance(){
		if(instance_==null){
			instance_ = new SessionStored();
			instance_.init();
		}
		return instance_;
	}

	private void init(){
		new Thread(()->{
			try {
				Map m=(Map) SerializeUtils.fromfile(storedfile);
				if(m!=null)
					sessions.putAll(m);
				
				if( HttpServer.INSTANCE.getConfig().isSessionPersistence() ){
					TimerQueue.getDispacher().add(60*1000, ()->{
						Log.info("persistence snapshot");
//						persistence();
					});
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
	}
	
	public Map<String, Map<String,Object>> sessions() {
		// TODO Auto-generated method stub
		return sessions;
	}


	public void persistence() {
		// TODO Auto-generated method stub
		
		try {
			SerializeUtils.tofile(sessions,storedfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
