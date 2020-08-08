package com.pl.restty.server.provider;

import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.security.Provider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.attribute.HashAttributeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pl.restty.server.HttpServer;
import com.pl.restty.server.annotation.AnnoMethodInfo;
import com.pl.restty.server.annotation.AnnotationUtil;
import com.pl.restty.server.annotation.BeforeFilter;
import com.pl.restty.server.annotation.Controller;
import com.pl.restty.server.annotation.RequestMethod;
import com.pl.restty.server.annotation.ServiceRegister;
import com.pl.restty.server.annotation.UrlMapping;
import com.pl.restty.server.utils.IpAddressUtil;

public class Providers {
	private static Logger Log = LoggerFactory.getLogger(Providers.class);
	private static List<AnnoMethodInfo> providers;
	public static void scanPackage(String scanProviderPackage) throws Exception {
		// TODO Auto-generated method stub
		providers = AnnotationUtil.getAnnotations(scanProviderPackage, Controller.class, ServiceRegister.class);
	}
	
	public static void scanPackageAndRegister(String scanProviderPackage,String myServerAddress,int myServerPort) throws Exception{
		scanPackage(scanProviderPackage);
		register(myServerAddress, myServerPort);
	}
	
	public static void register(String providerAddress,int providerPort) throws Exception{
		final List<AnnoMethodInfo> ams = Providers.providers;
		for( AnnoMethodInfo ami: ams ){
//			System.out.println(ami);
			//1. get register center path, ex: zookeeper://127.0.0.1:2181/usermanage/
			String centerUrl = ami.annoKeyValues.get("path");
//			String connectString = getConnectString(centerUrl);
//			if(connectString==null) {
//				Log.info("providers.register.connectString is null, [@ServiceRegister] " + centerUrl);
//				continue;
//			}
			Map<String,String> centerConfig = getCenterConfig(centerUrl);			
			
			//2. get api UrlMapping path: path=/getUserinfo
			Method method = Class.forName(ami.className).getDeclaredMethod(ami.methodName, ami.methodParamTypes);
			UrlMapping mapping = method.getDeclaredAnnotation(UrlMapping.class);
			String path = mapping.path();
//			RequestMethod md = mapping.method();
			String configData = "http://"+providerAddress+":"+providerPort+path;
			//3. registerToServiceCenter
			final RegisterHelper helper = RegisterHelperFactory.create(centerConfig.get("identifier"));
			helper.register(centerConfig.get("ip"),centerConfig.get("port"),centerConfig.get("path"),
					configData);
			Log.info("服务："+configData+" 注册到"+centerUrl);
//			helper.release();
			centerConfig.clear();
			centerConfig=null;
		}
	}
	
	/**
	 * 暂时不成熟的方式
	 * @param connUrl
	 * @return
	 */
	public final static String getConnectString( String connUrl ) {
		if(connUrl.startsWith("config")) {
//			@ServiceRegister(path="config://server.providers.getUserinfo",type="TEMP")
			String[] attrString = connUrl.substring(9).split("\\.");
			Object aa = HttpServer.INSTANCE;
			List<Map<String,String>> list = HttpServer.INSTANCE.getConfig().getProviders();
			Optional<Map<String,String>> op0 = list.stream().filter(m->{
				if(m==null) return false;
				Optional<Entry<String,String>> op = m.entrySet().stream().findFirst();
				if(op.isPresent() && op.get().getKey().equals( attrString[2] )) {
					return true;
				}
				return false;
			}).findFirst();
			if( op0.isPresent() )
				return op0.get().get(attrString[2]);
			return null;
			
		}
		else {
			return connUrl;
		}
			
	}

	/**
	 * path=zookeeper://127.0.0.1:2181/usermanage/
	 * @param centerUrl
	 * @return
	 */
	public final static Map<String,String> getCenterConfig(String centerUrl){
		Map<String,String> map = new HashMap<>();
		Pattern p = Pattern.compile("(\\w+)://(.*):(\\d+)/(.*)");
		Matcher m=p.matcher(centerUrl);
//		while(m.find()){
//			System.out.println(m.group(1));
//		}
		m.find();
		map.put("identifier", m.group(1)); //zookeeper Eureka ...custom center
		map.put("ip", m.group(2));
		map.put("port", m.group(3));
		map.put("path", "/"+m.group(4));
//		System.out.println("count: "+m.groupCount()+"\n"+map);		
		return map;
	}
	

	
//	public static void main(String[] args) throws Exception{
//		Providers.scanPackage("com.pl.restty.test.controllers");
//		Providers.register("127.0.0.1",7777);
//	}
}
