package com.pl.restty.server.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.pl.restty.server.utils.BeanMapUtils;
import com.pl.restty.server.utils.YamlUtils;

public class Configuration {
	private String webRoot="";
	private String scanRoutePackage=null;
	private String scanFilterPackage=null;
	private String scanProviderPackage=null;
	private int port=7777;
	private String engineClassName="com.pl.restty.server.template.FreeMarkerEngine";
	private boolean cachedStaticFiles=false;
	private String sessionProxy="local"; //disable -disable;local-本地session;redis-remote session
	private boolean sessionPersistence=false;
	private List<Map<String,String>> providers=null;
//	public String name="aaa";
//	private List<String> list = Arrays.asList("rose","jone","alice");
	
	public List<Map<String, String>> getProviders() {
		return providers==null ? new CopyOnWriteArrayList<>() : providers;
	}
	public void setProviders(List<Map<String, String>> providers) {
		this.providers = providers;
	}
	public String getWebRoot() {
		return webRoot;
	}
	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}
	public String getScanRoutePackage() {
		return scanRoutePackage;
	}
	public void setScanRoutePackage(String scanRoutePackage) {
		this.scanRoutePackage = scanRoutePackage;
	}
	public String getScanFilterPackage() {
		return scanFilterPackage;
	}
	public void setScanFilterPackage(String scanFilterPackage) {
		this.scanFilterPackage = scanFilterPackage;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getEngineClassName() {
		return engineClassName;
	}
	public void setEngineClassName(String engineClassName) {
		this.engineClassName = engineClassName;
	}
	public boolean isCachedStaticFiles() {
		return cachedStaticFiles;
	}
	public void setCachedStaticFiles(boolean cachedStaticFiles) {
		this.cachedStaticFiles = cachedStaticFiles;
	}
	public String getScanProviderPackage() {
		return scanProviderPackage;
	}
	public void setScanProviderPackage(String scanProviderPackage) {
		this.scanProviderPackage = scanProviderPackage;
	}
	public String getSessionProxy() {
		return sessionProxy;
	}
	public void setSessionProxy(String sessionProxy) {
		this.sessionProxy = sessionProxy;
	}
	
	public final static Configuration createby(String yaml) throws Exception{
		if(yaml==null) throw new NullPointerException("Configuration.crateby yaml is null");
//		Configuration cfg = yamlTobean(yaml,Configuration.class);	
		Map m=YamlUtils.toMap(yaml);
		Configuration cfg = BeanMapUtils.toBean(m, Configuration.class);
		return cfg;
	}
	
	public boolean isSessionPersistence() {
		return sessionPersistence;
	}
	public void setSessionPersistence(boolean sessionPersistence) {
		this.sessionPersistence = sessionPersistence;
	}

	
	
	
	
}
