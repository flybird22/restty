package com.pl.restty.server.template;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import com.pl.restty.server.route.StaticFileFilter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;


public class FreeMarkerEngine extends TemplateEngine{

private Configuration configuration;
	
	public FreeMarkerEngine() {
        this.configuration = createDefaultConfiguration();
    }
	
	public FreeMarkerEngine(Configuration configuration) {
        this.configuration = configuration;
    }

	private Configuration createDefaultConfiguration() {
		// TODO Auto-generated method stub
		Configuration configuration = new Configuration(new Version(2, 3, 23));
//        configuration.setClassForTemplateLoading(FreeMarkerEngine.class, "");
		File dir=new File(StaticFileFilter.locationPath+"template");
		if(!dir.exists()){
			System.out.println("template file path set error");
			dir.mkdirs();
		}
		try {
			configuration.setDirectoryForTemplateLoading(dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return configuration;
	}

	@Override
	public String render(ModelAndView modelAndView) {
		// TODO Auto-generated method stub
		try {
            StringWriter stringWriter = new StringWriter();
            Template template = configuration.getTemplate(modelAndView.getViewName());
            template.process(modelAndView.getModel(), stringWriter);
            return stringWriter.toString();
        } catch (IOException | TemplateException e) {
            throw new IllegalArgumentException(e);
        }
	}

}
