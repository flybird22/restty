package com.pl.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.pl.restty.server.utils.StringUtils;

public class ConfiguratorFactory {
	static final Map<String,Class<? extends Configurator>> map ;
	static{
		map = new ConcurrentHashMap<>();
		map.put("yml", YamlConfigurator.class);
	}

	public static <T> Configurator create( String filename ) throws Exception{
		String extName = StringUtils.getFileExtensionName(filename);
		Configurator o = map.get(extName).newInstance();
		o.read(filename);
		return o;
	}

//	public static Configurator load(String filename) {
//		// TODO Auto-generated method stub
//		String extName = StringUtils.getFileExtensionName(filename);		
//		return create(extName);
//	}
	
	
	public static void main(String[] args) throws Exception {
		Configurator cfg = ConfiguratorFactory.create(System.getProperty("user.dir")+"/server.yml");
		Map m = (Map) cfg.get("providers");
		
		System.out.println(m.get("getUserinfo"));
	}
}
