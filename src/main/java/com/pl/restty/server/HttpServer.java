package com.pl.restty.server;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pl.restty.server.config.Configuration;
import com.pl.restty.server.handlers.HttpServerFilter;
import com.pl.restty.server.handlers.RequestWorker;
import com.pl.restty.server.manage.HttpSessionProxy;
import com.pl.restty.server.manage.SessionStored;
import com.pl.restty.server.provider.Providers;
import com.pl.restty.server.route.Filters;
import com.pl.restty.server.route.Route;
import com.pl.restty.server.route.Routes;
import com.pl.restty.server.route.StaticFileFilter;
import com.pl.restty.server.template.TemplateEngine;
import com.pl.restty.server.utils.IpAddressUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueDomainSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpServer {
	private final Logger Log = LoggerFactory.getLogger(HttpServer.class);
	private ChannelFuture channel;
	private final EventLoopGroup masterGroup;
	private final EventLoopGroup slaveGroup;
	private Configuration config = new Configuration();
	private TemplateEngine engine=null;
	private Set<String> whiteList=null;
	List<Class<? extends RequestWorker>> defRequestWorks=null;
	  
	Supplier<EventLoopGroup> getLoopGroup = ()->{
		
		if(KQueue.isAvailable())
			return new KQueueEventLoopGroup();
		else if(Epoll.isAvailable())
			return new EpollEventLoopGroup();
		else
			return new NioEventLoopGroup();		
	};
	
	public static HttpServer INSTANCE =null;
	
	public HttpServer(int port) {
		// TODO Auto-generated constructor stub
		INSTANCE = this;
		masterGroup = getLoopGroup.get();
		slaveGroup = getLoopGroup.get();
	    config.setPort(port);
	}
	
	public HttpServer(Configuration config){
		this.config = config;
		INSTANCE = this;
		masterGroup = getLoopGroup.get();
		slaveGroup = getLoopGroup.get();
	}
	
	public void init() throws Exception{
		StaticFileFilter.locationPath = config.getWebRoot();
		Routes.scanPackage(config.getScanRoutePackage()); 
    	Filters.scanPackage(config.getScanFilterPackage());
    	engine = (TemplateEngine) Class.forName(config.getEngineClassName()).newInstance();
    	if( config.getSessionProxy().equals("local") ){
    		SessionStored.instance();
    	}
	}
	
	public void start() throws Exception {
    	
    	init();
		Runtime.getRuntime().addShutdownHook(new Thread()
	    {
			@Override
			public void run() { shutdown(); }
	    });
		
		Class<? extends ServerChannel> channelClass = getNioClass();
    
		try{
			
			final ServerBootstrap bootstrap =
			        new ServerBootstrap()
						.option(ChannelOption.SO_BACKLOG, 1024)
//						.option(ChannelOption.SO_REUSEADDR, true)
						.group(masterGroup, slaveGroup)
						.channel(channelClass)						
						.childHandler(new HttpServerFilter(this))						
						.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			
			
			channel = bootstrap.bind(config.getPort());
			Log.info("NettyHttpServer-服务端启动成功,端口是:"+config.getPort());

			
			
			new Thread(()->{
	    		serverStartup();
	    	}).start();
			
			channel.channel().closeFuture().sync();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			slaveGroup.shutdownGracefully();
		    masterGroup.shutdownGracefully();
		    
		}
    
	}
	
	public final Class<? extends ServerChannel> getNioClass(){
		Class<? extends ServerChannel> channelClass =  NioServerSocketChannel.class;
		
		if(Epoll.isAvailable()) {
			channelClass = EpollServerSocketChannel.class;
			Log.info("设置NIO模式为：Epoll");
		}
		else if (KQueue.isAvailable()){
			channelClass = KQueueServerSocketChannel.class;
			Log.info("设置NIO模式为：kqueue");
		}
		else {
			Log.info("设置NIO模式为：默认模式");
		}
		
		return channelClass;
	}
	
	public void shutdown(){
		if(config.getSessionProxy().equals("local") && 
				config.isSessionPersistence()){
			SessionStored.instance().persistence();
		}
		Log.info("...shut down server");
	}
	
	private void serverStartup(){
		try {
			if(config.getScanProviderPackage()!=null){
				String myAddress=IpAddressUtil.getLocalHostLANAddress().getHostAddress();
				Providers.scanPackageAndRegister(config.getScanProviderPackage(),
						myAddress, config.getPort());
//				System.out.println("检测服务提供者并注册：本机服务地址 "+myAddress+":"+config.getPort());
			}
			
			if(config.getSessionProxy()!=null && !config.getSessionProxy().equals("disable"))
				HttpSessionProxy.instance().connect(config.getSessionProxy());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void setWebRoot(String webRoot) {
		config.setWebRoot(webRoot.endsWith("/")?webRoot:webRoot+"/"); 
		StaticFileFilter.locationPath = config.getWebRoot();
		
	}

	

	public void setScanRoutePackage(String scanRoutePackage) {
		config.setScanRoutePackage(scanRoutePackage);
	}


	public void setScanFilterPackage(String scanFilterPackage) {
		config.setScanFilterPackage(scanFilterPackage);
	}

	public Configuration getConfig() {
		return config;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	public void setTemplateEngine(String engineClassName){
		config.setEngineClassName(engineClassName);
	}

	public TemplateEngine getTemplateEngine() {
		// TODO Auto-generated method stub
		return engine;
	}

	public HttpServer setWhitelist(Set<String> iplist){
		whiteList = iplist;
		return this;
	}

	public Set<String> getWhitelist() {
		// TODO Auto-generated method stub
		return whiteList;
	}

	public List<Class<? extends RequestWorker>> getDefineRequestWorks() {
		// TODO Auto-generated method stub
		return defRequestWorks;
	}
	
	/**
	 * 构建处理序列 可以针对请求做特殊处理，等同于filter
	 * HttpServer server = new HttpServer();
	 * server.addRequestWork( StaticFileHandler.class );
	 * server.addRequestWork( ServletHandler.class );
	 * 
	 * @param t
	 */
	public HttpServer addRequestWork(Class<? extends RequestWorker> t) {
		if(defRequestWorks==null) {
			defRequestWorks = new CopyOnWriteArrayList<>();
		}
		defRequestWorks.add(t);
		return this;
	}
	
	public HttpServer addRequestWorks( List<Class<? extends RequestWorker>> workers ) {
		if(defRequestWorks==null) {
			defRequestWorks = new CopyOnWriteArrayList<>();
		}
		defRequestWorks.addAll(workers);
		return this;
	}
	
}
