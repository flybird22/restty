package com.pl.events;

public class SimpleTimer {
	
	private String name=null;
	private boolean stop = false;
	
	public SimpleTimer(){
		this(SimpleTimer.class.getSimpleName());
	}
	
	public SimpleTimer(String name){
		this.name = name;
	}
	
	public void stop(){
		this.stop  = true;
	}
	
	public void schedule(int intervalms,Runnable task){
		new Thread(()->{
			Thread.currentThread().setName(name);
			while(!stop){
				try {				
					task.run();
					Thread.sleep(intervalms);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}).start();
	}
	
	public void schedule(int intervalms,int maxTry, Runnable task){
		new Thread(()->{
			Thread.currentThread().setName(name);
			int n=0;
			while(!stop && n<maxTry){
				try {				
					task.run();
					Thread.sleep(intervalms);
					n++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}).start();
	}
	
	public void once(Runnable task){
		new Thread(()->{
			Thread.currentThread().setName(name);
			task.run();
		}).start();
	}
	
	public void once(int delayms,Runnable task){
		new Thread(()->{
			try {
				Thread.currentThread().setName(name);
				Thread.sleep(delayms);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			task.run();
		}).start();
	}

}
