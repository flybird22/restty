package com.pl.events;

import java.util.EventListener;
import java.util.function.Consumer;

public class BaseEventListener implements EventListener {
	Consumer<Event> consumer;
	String eventName;
	
	public BaseEventListener(String eventName,Consumer<Event> consumer){
		this.consumer = consumer;
		this.eventName=eventName;
	}
	
	public void handle_event(Event event){
		consumer.accept(event);
	}

	public String getEventName() {
		// TODO Auto-generated method stub
		return eventName;
	}

}
