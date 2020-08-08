package com.pl.events;

import java.util.HashSet;
import java.util.Set;

public class EventSource {

	Set<BaseEventListener> listeners = new HashSet<>();
	EventGroup groups;
	public EventSource(EventGroup groups){
		this.groups = groups;
	}
	
	public void addListener(BaseEventListener listener){
		listeners.add(listener);
	}
	
	

	private void trigger(String eventName) {
		// TODO Auto-generated method stub
		Event evt = new Event(eventName, this);
		for(BaseEventListener listener : listeners){
			if(listener.getEventName().equals(eventName)){
				groups.run(()->{
					listener.handle_event(evt);
				});
			}
		}
	}
	

	
	public static void main(String[] args){
		EventSource es = new EventSource( new EventGroup() );
		es.addListener(new BaseEventListener("click",e->{
			System.out.println(e.getName());
		}));
		
		es.trigger("select");
	}
}
