package com.pl.restty.server.handlers;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.jboss.netty.handler.ipfilter.IpSubnet;

import com.pl.restty.server.HttpServer;
import com.pl.restty.test.flow.ParseRequestHandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class HttpServerFilter extends ChannelInitializer<SocketChannel>{

	HttpServer http_server_=null;
	public HttpServerFilter(HttpServer httpServer) {
		// TODO Auto-generated constructor stub
		http_server_ = httpServer;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		if( !allowedAccess(ch.remoteAddress()) ){
			ch.close();
			return;
		}
		// TODO Auto-generated method stub
		ChannelPipeline cp = ch.pipeline();
//		cp.addLast("encoder",new HttpResponseEncoder());
//        cp.addLast("decoder",new HttpRequestDecoder());
//		cp.addLast("ipfilter",new IpAddrFilterHandler( getRules() ));
		cp.addLast("codec", new HttpServerCodec());
		cp.addLast("aggregator",
                new HttpObjectAggregator(1024*1024)); //最大内容尺寸
//		cp.addLast("request",new HttpServerHandler());
//		cp.addLast("request",new RequestHandler());
		//ParseRequestHandler
		cp.addLast("request",new RequestHandler(http_server_));
		
	}
	
	private boolean allowedAccess(InetSocketAddress sockAddr){
		String remoteIp = sockAddr.getHostString();
		//all is allowed
		if( HttpServer.INSTANCE.getWhitelist()==null){
			return true;
		}
		//is allowed
		if(HttpServer.INSTANCE.getWhitelist().contains(remoteIp)){
			return true;
		}
		
		// is not allowed		
		return false;
	}

//	@Override
//	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//		// TODO Auto-generated method stub
//		super.handlerAdded(ctx);	
//		System.out.println("handlerAdded: "+ctx.pipeline().toMap());
//		
//	}
	
	public IpFilterRule[] getRules() throws UnknownHostException{
		String[] ip = (String[]) Arrays.asList("127.0.0.1").toArray();
		int count = 1;
		IpFilterRule[] ipf = new IpFilterRule[count];
		for(int i=0;i<count;i++){
//			ipf[i] = (IpFilterRule) new IpSubnetFilterRule(ip[i], 32, IpFilterRuleType.ACCEPT);
			ipf[i] = (IpFilterRule) new IpSubnetFilterRule(ip[i], 32, IpFilterRuleType.REJECT);
		}
		
//		IpSubnetFilterRule[] ipf = new IpSubnetFilterRule[count];
//		for( int i=0;i<count;i++){
//			ipf[i] = new IpSubnetFilterRule(ip[i],16,IpFilterRuleType.REJECT);
//		}
		return ipf;

	}

}
