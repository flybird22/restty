package com.pl.restty.server.template;

public abstract class TemplateEngine {

	public String render(Object object) {
		ModelAndView modelAndView = (ModelAndView) object;
        return render(modelAndView);
	}
	
	public ModelAndView modelAndView(Object model, String viewName) {
        return new ModelAndView(model, viewName);
    }
	
	public abstract String render(ModelAndView modelAndView);
	
}
