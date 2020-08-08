package com.pl.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigRegisterCenter {

	static final Map<String,Object> map = new ConcurrentHashMap<>();
	
	public static final Map<String,Object> board() {
		// TODO Auto-generated method stub
		return map;
	}

}
