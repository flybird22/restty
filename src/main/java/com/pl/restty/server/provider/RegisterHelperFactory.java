package com.pl.restty.server.provider;

import com.pl.restty.server.HttpServer;

public class RegisterHelperFactory {

	public static RegisterHelper create(String identifier) {
		// TODO Auto-generated method stub
		if(identifier.equals("zookeeper")){
			return new ZkClientHelper();
		}
		else if( identifier.equals("config") ) {
			return new ConfigHelperAdapter();
		}
		
		return null;
	}

}
