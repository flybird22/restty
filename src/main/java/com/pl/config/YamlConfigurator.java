package com.pl.config;

import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.pl.restty.server.utils.StringUtils;
import com.pl.restty.server.utils.YamlUtils;

public class YamlConfigurator implements Configurator {

	Map<String,Object> configMap=null;
	String configFileName=null;
	@Override
	public void read(String filename) throws Exception {
		// TODO Auto-generated method stub
		configFileName = StringUtils.getFilename(filename);
		configMap = YamlUtils.toMap(filename);
	}

	@Override
	public Object get(String key) {
		// TODO Auto-generated method stub
		return configMap==null?null:configMap.get(key);
	}

	@Override
	public void set(String key, Object value) {
		// TODO Auto-generated method stub
		configMap.put(key, value);
	}

	@Override
	public void write(String filename) throws Exception {
		// TODO Auto-generated method stub
		YamlUtils.toYaml(filename, Map.class);
	}

	@Override
	public void register(Map<String, Object> center) {
		// TODO Auto-generated method stub
		center.put(configFileName, this);
	}

}
