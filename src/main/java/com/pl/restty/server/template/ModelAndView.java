package com.pl.restty.server.template;

public class ModelAndView {
	
	Object model;
	String viewName;
	
	public ModelAndView(Object model, String viewName){
		this.model = model;
		this.viewName = viewName;		
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

}
