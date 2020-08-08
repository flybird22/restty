package com.pl.events.concurrent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DefaultTaskPromise<T> implements TaskPromise<T> {

	public static enum PromiseResult{SUCCESS,FAILED};
	
	T result_;
//	Set<TaskListener<T>> listeners = new ConcurrentSkipListSet<>();
	List<TaskListener> listeners = new CopyOnWriteArrayList<>();
	boolean done=false;
	boolean cancel = false;
	Thread taskThread=null;
	Object lock=new Object();
	
	@Override
	public TaskFuture<T> addListener(TaskListener taskListener) {
		// TODO Auto-generated method stub
		listeners.add(taskListener);
		if(isDone()){
			notifyListener();
		}
		return this;
	}

	private void notifyListener() {
		// TODO Auto-generated method stub
		for(TaskListener l : listeners){
			try {
				l.onComplete(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void await() throws InterruptedException {
		// TODO Auto-generated method stub
		if(isDone()) return;
		if (Thread.interrupted()) {
            throw new InterruptedException(toString());
        }
		synchronized (this) {
			while(!isDone()){
				try{
					wait();
				}
				finally{}
			}
		}
	}

	@Override
	public TaskFuture<T> getFuture() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void setSuccess(Object object) {
		// TODO Auto-generated method stub		
		done = true;
		result_ = (T) object;
		notifyListener();	
		synchronized (this) {
			notifyAll();
		}
	}
	
	@Override
	public void setFailure(Object object) {
		// TODO Auto-generated method stub
//		result_ = PromiseResult.FAILED;
		done = true;
		result_ = (T) ((object instanceof Exception) ? "exception":object);
		notifyListener();
		synchronized (this) {
			notifyAll();
		}
	}


	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO Auto-generated method stub
		if(taskThread!=null && taskThread.isAlive()){
			taskThread.interrupt();
			cancel=true;
		}
		notifyListener();
//		System.out.println("thread check: " + (Thread.currentThread() == taskThread) +
//				"\tcurrentid="+Thread.currentThread().getId()+", taskid="+taskThread.getId() );
		return cancel;
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return cancel;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return (done | cancel);
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		if(isDone())
			return result_;
		else{
			wait();
			done = true;
			return result_;
		}
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		// TODO Auto-generated method stub
		long newtime = unit==TimeUnit.SECONDS ? timeout*1000 : (
				unit==TimeUnit.MILLISECONDS ? timeout : 5000);
		try{
			if(isDone())
				return result_;
			
			wait(newtime);
			done = true;
			return result_;
			
		}catch(InterruptedException e){
			throw new InterruptedException("time out: "+this);
		}
		
	}

	@Override
	public void setTaskThread(Thread currentThread) {
		// TODO Auto-generated method stub
		this.taskThread = currentThread;
	}

	
	


}
