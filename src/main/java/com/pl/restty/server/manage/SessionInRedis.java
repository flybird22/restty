package com.pl.restty.server.manage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.pl.restty.server.utils.DatetimeUtils;
import com.pl.restty.server.utils.JavaMachineUtils;
import com.pl.restty.server.utils.RandomUtil;
import com.pl.restty.server.utils.SerializeUtils;
import com.pl.restty.server.utils.StringMatcherUtils;

public class SessionInRedis implements HttpSessionWrapper, SessionBuilder {
	private Logger Log = LoggerFactory.getLogger(SessionInRedis.class);
	private static Map<String,JedisPool> redisMap = new HashMap<>();
	String sessionId;
	String connectString=null;
	
	public SessionInRedis(String proxyConnectString) throws Exception {
		// TODO Auto-generated constructor stub
		if(proxyConnectString==null) throw new Exception("SessionInRedis connectString is null");
		this.connectString = proxyConnectString;
		if(redisMap.get(proxyConnectString)!=null) return;
		//redis://127.0.0.0:port/
		String[] cfg = StringMatcherUtils.match(proxyConnectString, "(\\w+)://(.*):(\\d+)/(.*)");
		if(cfg==null) throw new Exception("connect string error, ex: redis://127.0.0.0:port/ ");
		
//		Jedis jedis = new Jedis(cfg[1], Integer.valueOf(cfg[2]));
//		jedis.connect();
//		redisMap.put(proxyConnectString, jedis);		
//		if(jedis.isConnected()) Log.info(proxyConnectString + " connected");
		JedisPool pool = createJedisPool(cfg[1], Integer.valueOf(cfg[2]));
		redisMap.put(proxyConnectString, pool);
		Log.info("SessionInRedis: "+proxyConnectString);
	}
	private JedisPool createJedisPool(String ip, int port){
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(1000);
		config.setMaxWaitMillis(5000);
		config.setMaxIdle(100);
		config.setMinIdle(50);
		JedisPool pool = new JedisPool(config, ip,port);
		return pool;
	}
	
	private Jedis jedis(){
		return redisMap.get(connectString).getResource();
	}
	
	private void jedisRun( Consumer<Jedis> consumer ){
		JedisPool pool = redisMap.get(connectString);
		final Jedis j = pool.getResource();
		try{
			consumer.accept(j);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}finally{
			j.close();
		}
	}
	
	private Object jedisRun( Function<Jedis, Object> function ){
		JedisPool pool = redisMap.get(connectString);
		final Jedis j = pool.getResource();
		try{
			Object ob = function.apply(j);
			return ob;
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}finally{
			j.close();
		}
		return null;
	}

	@Override
	public void createSession() throws Exception {
		// TODO Auto-generated method stub
		sessionId = RandomUtil.randomString(6)+DatetimeUtils.dateFormat("yyMMddHHmmss")+
				JavaMachineUtils.getPid();
//		Map<String,Object> m = new HashMap<>();
//		m.put("create_time", System.currentTimeMillis());
		jedisRun( new Consumer<Jedis>() {
			@Override
			public void accept(Jedis t) {
				// TODO Auto-generated method stub
				t.hset(sessionId, "create_time", System.currentTimeMillis()+"");
				t.hset(sessionId, "expired_time", 60*30+""); //30分钟
			}
		} );
		
	}

	@Override
	public void deleteSession() {
		// TODO Auto-generated method stub
		jedisRun( new Consumer<Jedis>() {
			@Override
			public void accept(Jedis t) {
				// TODO Auto-generated method stub
				t.del(sessionId);
			}
		} );
		
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
	public void set(String key, Object value) throws Exception{
		// TODO Auto-generated method stub
		if(sessionId==null) throw new Exception("sessionId is null");		

		jedisRun( new Consumer<Jedis>() {
			@Override
			public void accept(Jedis t) {
				// TODO Auto-generated method stub
				try {
					t.hset(sessionId.getBytes(),key.getBytes(),SerializeUtils.marshalling(value));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} );
	}

	@Override
	public Object get(String key) throws Exception{
		// TODO Auto-generated method stub
		byte[] buf = (byte[]) jedisRun( new Function<Jedis,Object>() {

			@Override
			public Object apply(Jedis t) {
				// TODO Auto-generated method stub
				byte[] val = t.hget(sessionId.getBytes(),key.getBytes());
				return val;
			}
		}); 
		
		return SerializeUtils.unmarshalling(buf);
//		return redisMap.get(connectString).hget(sessionId, key);
	}
	
	@Override
	public <T> T getT(String key) throws Exception{
		Object ob = get(key);		
		return ob==null ? null : (T)ob;		
	}

	@Override
	public int getExpiredTime() throws Exception{
		// TODO Auto-generated method stub
		return (int) jedisRun( new Function<Jedis,Object>() {

			@Override
			public Object apply(Jedis t) {
				// TODO Auto-generated method stub
				int v = Integer.valueOf(t.hget(sessionId, "expired_time"));
				return v;
			}
		}); 
		 
	}

	@Override
	public void setExpiredTime(int expiredTime)throws Exception {
		// TODO Auto-generated method stub
		
		jedisRun(new Consumer<Jedis>(){
			@Override
			public void accept(Jedis t) {
				// TODO Auto-generated method stub
				t.hset(sessionId, "expired_time", expiredTime+"");		
			}
			
		});
	}

	@Override
	public boolean isExpired() throws Exception{
		// TODO Auto-generated method stub
		return System.currentTimeMillis() > Integer.valueOf(get("create_time")+"")+getExpiredTime();
	}
	
	
	
//	public static void main(String[] args) throws Exception{
//		SessionInRedis si = new SessionInRedis("reids://127.0.0.1:6379/");
//		si.setSessionId("111111");
//		Map<String,String >m1=new HashMap<String,String>(){{
//			put("123","aaa");
//		}};
//		
//		si.set("user", m1);
//		Map name = (Map) si.get("user");
//		System.out.println(name);
//		
//		System.in.read();
//
//	}

}
