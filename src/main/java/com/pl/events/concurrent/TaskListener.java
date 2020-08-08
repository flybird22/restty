package com.pl.events.concurrent;

import java.util.EventListener;

public interface TaskListener extends EventListener {
	public <T> void onComplete(TaskFuture<T> f) throws Exception;
}
