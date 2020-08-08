package com.pl.events.concurrent;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Predicate;

import com.pl.events.EventGroup;
import com.pl.restty.server.utils.RandomUtil;

public class Test {

	public static Thread findThread(long threadId) {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while(group != null) {
            Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
            int count = group.enumerate(threads, true);
            for(int i = 0; i < count; i++) {
                if(threadId == threads[i].getId()) {
                    return threads[i];
                }
            }
            group = group.getParent();
        }
        return null;
    }

	static long subThreadId=0;
	public static void main(String[] a) throws IOException, InterruptedException{
//		TaskPromise <String>promise = new DefaultTaskPromise<String>();
//		TaskFuture<String> future = promise.getFuture();
//		future.addListener( new TaskListener<String>(){
//
//			@Override
//			public <T> void onComplete(TaskFuture<T> f) throws Exception {
//				// TODO Auto-generated method stub
//				System.out.println(f.isDone()?f.get():"not done");
//			}
//
//			
//			
//		} );
		
//		new Thread(()->{
//			try {
//				Thread.sleep(2000);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			promise.setSuccess("success");
//		}).start();
		EventGroup group = new EventGroup();
		group.submit(()->{
			System.out.println("call...start");
			long start = System.currentTimeMillis();
			int r=RandomUtil.randomInt(5)+1;
//			Thread.sleep(r*1000);
			System.out.println("call...return, cost: "+ (System.currentTimeMillis() - start)/1000 +"s");
			return "Success";
			
		}).addListener(new TaskListener() {
			@Override
			public <T> void onComplete(TaskFuture<T> f) throws Exception {
				// TODO Auto-generated method stub
				System.out.println("listener.onComplete: "+f.get());
			}
		}).await();
		
		System.out.println("main Thread: "+Thread.currentThread().getId());
		
		
		TaskFuture<String> ff=group.submit(()->{
			long tid = Thread.currentThread().getId();
			System.out.println("currentThread: "+tid);
			subThreadId = tid;
			int i=0;
			boolean stop=false;
			while(i<100 && !stop){
				try {
					System.out.println("--thread-"+tid+"-- "+i);
					Thread.sleep(1000);							
				} catch (Exception e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					if(e instanceof InterruptedException) stop=true;
				}
				i++;
			}
//			return "";
		});
		
//		System.out.println();
//		Thread.getAllStackTraces().keySet().stream().forEach(t->{
//			System.out.print("\t"+t.getId());
//		});
//		
//		System.out.println();
//		Thread tt = Thread.getAllStackTraces().keySet().stream().filter(t->{
//			if(t.getId() == subThreadId) return true;
//			return false;
//		}).findFirst().orElse(null);
//		
//		if(tt!=null && tt.isAlive()){
//			tt.interrupt();
//		}
//		
		Thread.sleep(2000);
		System.out.println("mainThread: "+Thread.currentThread().getId());
		ff.cancel(true);
		
		System.out.println("isDone="+ff.isDone());
		
		group.shutdown();
//		future.await();
//		System.in.read();
		
	}
}
