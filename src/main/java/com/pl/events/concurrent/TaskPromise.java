package com.pl.events.concurrent;

public interface TaskPromise<T> extends TaskFuture<T> {

	TaskFuture<T> getFuture();
	void setSuccess(Object object);
	void setFailure(Object object);
	void setTaskThread(Thread currentThread);

}
