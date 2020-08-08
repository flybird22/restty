package com.pl.config;

import java.util.Map;

public interface Configurator {

	public void read(String filename) throws Exception;
	public void write(String filename) throws Exception;
	public Object get(String key);
	public void set(String key,Object value);
	public void register(Map<String,Object> center);
}
