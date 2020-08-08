package com.pl.events;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.impl.Log4jLoggerFactory;

public class TimerQueue {
	public enum TimerType {ONCE,INTERVAL};
	
	private List<TimerObject> queue = new CopyOnWriteArrayList<>();	
	
	private ExecutorService es = Executors.newSingleThreadExecutor();
	private EventGroup groups;
	private boolean stop=false;
	private static TimerQueue dispacher_ = null;
	
	class TimerObject{
		TimerType type;
		int interval;
		Runnable task;
		long lasttime;
		public int delay=0;
		public boolean isExpired(){
			return (System.currentTimeMillis()-lasttime) > interval;
		}
	}
	
	
	private TimerQueue(){}

	public void add(int interval, Runnable task) {
		// TODO Auto-generated method stub
		add(0,interval,task);		
	}
	
	public void add(int delay,int interval, Runnable task) {
		// TODO Auto-generated method stub
		TimerObject tob = new TimerObject();
		tob.interval = interval;
		tob.delay = delay;
		tob.task = task;
		tob.type = TimerType.INTERVAL;
		tob.lasttime=System.currentTimeMillis();
		queue().add(tob);
		
	}
	
	public void addOnce(Runnable task){
		TimerObject tob = new TimerObject();
		tob.interval = 0;
		tob.task = task;
		tob.type = TimerType.ONCE;
		tob.lasttime=System.currentTimeMillis();
		queue().add(tob);
		
	}

	private <T> List<T> queue() {
		// TODO Auto-generated method stub
		return (List<T>) queue;
	}

	
	public static TimerQueue getDispacher() {
		// TODO Auto-generated method stub
		if(dispacher_==null){
			dispacher_ = new TimerQueue();			
		}
		return dispacher_;
	}

	public void start() throws Exception {
		// TODO Auto-generated method stub
		if(groups==null) throw new Exception("TimerQueue.EventGroup is null, must invoke setEventGroup");
			
		es.execute(()->{
			while(!stop){				
				try {
					select();
					Thread.sleep(200);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
	}
	
	public void stop(){
		stop = true;
		es.shutdown();
		queue.clear();
		queue = null;
	}
	
	private void select(){
		
		for(TimerObject ob : queue){
			if( ob.type == TimerType.ONCE ){
				queue.remove(ob);						
				groups.run(ob.task);
			}
			if( ob.type == TimerType.INTERVAL && ob.isExpired() ){
				ob.lasttime=System.currentTimeMillis();
				groups.run(()->{
					try {
						Thread.sleep( ob.delay);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ob.task.run();
				});
			}
		}
	}
	
	public TimerQueue setEventGroup(EventGroup eventGroup) {
		// TODO Auto-generated method stub
		this.groups = eventGroup;
		return this;
	}

	public static void main(String[] args) throws Exception{
		TimerQueue.getDispacher().setEventGroup(new EventGroup());
		TimerQueue.getDispacher().start();
		TimerQueue.getDispacher().add(2000,()->{
			System.out.println("hello interval");
		});
		TimerQueue.getDispacher().addOnce(()->{
			System.out.println("hello once");
		});
	}

	

}
