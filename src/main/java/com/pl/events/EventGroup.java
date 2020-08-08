package com.pl.events;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import com.pl.events.concurrent.DefaultTaskPromise;
import com.pl.events.concurrent.Task;
import com.pl.events.concurrent.TaskFuture;
import com.pl.events.concurrent.TaskPromise;
import com.pl.restty.server.utils.JavaMachineUtils;

public class EventGroup {
	
	private static EventGroup groups = null;
	public final static int GROUP_SIZE;
	
	static{
		GROUP_SIZE=JavaMachineUtils.availableProcessors()*2;
	}
	
	private int nThreads=0;
	private ExecutorService executor;

	public EventGroup(){
		this(GROUP_SIZE);
	}
	
	public EventGroup(int nThreads) {
		// TODO Auto-generated constructor stub
		this.nThreads = nThreads;
		executor = Executors.newFixedThreadPool(nThreads);
	}

	public ExecutorService executor(){
		return this.executor;
	}


	public void shutdown(){
		if(!executor.isShutdown())
			executor.shutdown();
	}
	

	public void run(Runnable r) {
		// TODO Auto-generated method stub
		executor.execute(r);
	}

	public <T> void run(T t, Consumer<T> consumer){
		executor.execute(()->{
			consumer.accept(t);
		});
	}

	public <T> TaskFuture<T> submit( Task<T> task){
		TaskPromise<T> promise = new DefaultTaskPromise<>(); 		
		executor.execute(()->{
			try {
				T t=task.call();
				promise.setSuccess(t);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				promise.setFailure(e);
			}
		});
		return promise;
	}
	
	public <T> TaskFuture<T> submit(Runnable task){
		TaskPromise<T> promise = new DefaultTaskPromise<>();
		executor.execute(()->{
			try {
				promise.setTaskThread(Thread.currentThread());
				task.run();
				promise.setSuccess("success");
			}
			catch(Exception e)
			{
				promise.setFailure(e);
			}
		});
		return promise;
	}
//	
//	public static void main(String[] a) throws InterruptedException, ExecutionException{
//		
//		EventGroup group =new EventGroup();
//		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
//		for(int i=0;i<10;i++){
//			queue.add(i+"...hello");
//		}
//		
//		group.run(queue, q->{
//			while(q.size()>0){
//				String s = q.poll();
//				System.out.println(s);
//			}
//		});
//		
////		ExecutorService ess = Executors.newFixedThreadPool(4);
////		List<Callable<String>> tasks = new ArrayList<>();
////		for(int i=0;i<4;i++){
////			tasks.add( new Callable<String>() {
////				@Override
////				public String call() throws Exception {
////					// TODO Auto-generated method stub
////					Thread.sleep(5000);
////					return "success";
////				}
////			} );
////		}
////		List<Future<String>> fs=null;
////		
////		try{
////			fs = ess.invokeAll(tasks);
//////			fs =ess.invokeAll(tasks, 4000, TimeUnit.MILLISECONDS);
////		}catch(Exception e){
////			System.out.println();
////			System.out.println(e.getMessage()+"|"+e.toString());
////		}
////		
////		for(Future<String> f:fs){
////			try{
////				System.out.print(f.get()+"|");
////			}catch(Exception e){
////				System.out.print("failed|");
////			}
////			
////		}
////		System.out.println();
////		System.out.println("submit-collection-2:");
////		
////		if(!ess.isShutdown())
////			ess.shutdown();
//	}

}
