package com.pl.restty.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.pl.config.ConfigRegisterCenter;
import com.pl.config.ConfiguratorFactory;
import com.pl.events.EventGroup;
import com.pl.events.TimerQueue;
import com.pl.restty.converts.parse.RequestJsontoMapHandler;
import com.pl.restty.server.HttpServer;
import com.pl.restty.server.config.Configuration;
import com.pl.restty.server.handlers.ServletHandler;
import com.pl.restty.server.handlers.StaticFileHandler;
import com.pl.restty.server.route.Route;
import com.pl.restty.server.route.Routes;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.ConcurrentSet;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final int port = 7777; //设置服务端端口
	
    public static void main( String[] args ) throws Exception
    {
    	EventGroup groups = new EventGroup();
    	TimerQueue.getDispacher().setEventGroup(groups).start();
    	AppContext.eventGroup = groups;
    	
    	ConfiguratorFactory.create( System.getProperty("user.dir")+"/cp.yml" )
    		.register(ConfigRegisterCenter.board());
    	System.out.println(ConfigRegisterCenter.board());
    	
    
    	Configuration config = new Configuration();
    	config.setPort(port);
    	config.setScanFilterPackage( "com.pl.restty.test.controllers" );
    	config.setScanRoutePackage( "com.pl.restty.test.controllers" );
    	config.setScanProviderPackage( "com.pl.restty.test.controllers" );
    	config.setWebRoot( System.getProperty("user.dir")+"/webroot/" );
    	config.getProviders().add(new HashMap<String,String>() {{
    		put("getUserinfo","zookeeper://gitbird.com:2181/usermanage/");
    	}});
//    	config.setSessionProxy("redis://127.0.0.1:6379/");
//    	config.setSessionPersistence(true);
    	
    	HttpServer server = new HttpServer(config);
    	server.addRequestWorks( Arrays.asList( 
    			StaticFileHandler.class,
    			RequestJsontoMapHandler.class,
    			ServletHandler.class ))
    		.start();;
    	
//    	Set<String> whites = new ConcurrentSet<>();
//    	whites.add("127.0.0.1");    	
//    	server.setWhitelist(whites);
//    	server.start();
        
    }
}
