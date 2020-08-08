package com.pl.restty.test.flow;

import com.pl.restty.converts.parse.RequestJsontoMapHandler;
import com.pl.restty.converts.parse.RequestTexttoMapHandler;
import com.pl.restty.converts.parse.RequestXmltoMapHandler;
import com.pl.restty.server.HttpServer;
import com.pl.restty.server.handlers.RequestHandler;
import com.pl.restty.server.handlers.ServletFilter;
import com.pl.restty.server.handlers.ServletHandler;
import com.pl.restty.server.handlers.StaticFileHandler;

public class ParseRequestHandler extends RequestHandler {
	
public ParseRequestHandler(HttpServer httpServer) {
		super(httpServer);
		// TODO Auto-generated constructor stub
	}

/**
 * 1. get custom id from requestMap
 */
	@Override
	public void initRequestWorkers() {
		// TODO Auto-generated method stub
//		super.initRequestWorkers();
		getWorkers().add( new StaticFileHandler() );
		getWorkers().add( new RequestTexttoMapHandler() );
		getWorkers().add( new RequestJsontoMapHandler() );		
		getWorkers().add( new RequestXmltoMapHandler() );
		//参数检测需要结合url地址做判断，每隔接口的参数含义不同
//		getWorkers().add( new ParamsCheckHandler() );
		//成功的数据请求 转化为map后，检测成功后存放在 redis/log/kafka/mq，或者hdfs，需要根据配置文件
//		getWorkers().add( new ParamsCheckSuccSink() );
//		getWorkers().add( new ParamsCheckSuccSinkRedis() );
//		getWorkers().add( new ParamsCheckSuccSinkLog() );
//		getWorkers().add( new ParamsCheckSuccSinkHdfs() );
//		getWorkers().add( new ParamsCheckSuccSinkNfs() );
		getWorkers().add( new ServletFilter() );
		getWorkers().add( new ServletHandler() );	
	}
	
	

}
