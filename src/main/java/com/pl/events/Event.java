package com.pl.events;

import java.util.EventObject;

public class Event extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name="";
	
	public Event(String name,Object source) {
		super(source);
		// TODO Auto-generated constructor stub
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
