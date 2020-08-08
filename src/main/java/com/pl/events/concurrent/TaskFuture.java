package com.pl.events.concurrent;

import java.util.concurrent.Future;

public interface TaskFuture<T> extends Future<T>{

	TaskFuture<T> addListener(TaskListener taskListener);
	void await() throws InterruptedException;
	
}
