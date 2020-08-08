package com.pl.restty.server.handlers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Map.Entry;

import com.pl.restty.server.ContextWrapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
	    private String result="";
	    /*
	     * 收到消息时，返回信息
	     */
	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    	if(! (msg instanceof FullHttpRequest)){
				super.channelRead(ctx, msg);
	            return;
	        }
	    	ContextWrapper context=ContextWrapper.create();
	    	context.ctx =ctx;
	    	context.request = (FullHttpRequest) msg;
	    	
	    	HttpRequestWrapper request=null;
	    	HttpResponseWrapper response = new HttpResponseWrapper(context);
	    	try{	    		
		    	request = new HttpRequestWrapper(context);		    	
	    
				ServletHandler.create().handle(request,response);				
				writeFlush(ctx,response);

			}
			catch(Exception e){		
				System.out.println("channelRead: "+e.toString());
				response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR);
				response.setContent(HttpResponseStatus.INTERNAL_SERVER_ERROR+" INTERNAL_SERVER_ERROR");
				writeFlush(ctx,response);
			}
			finally {
				if(request!=null) request.release();
				else ReferenceCountUtil.release(msg);
		    }
	    }   
	  

	    /*
	     * 建立连接时，返回消息
	     */
	    @Override
	    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//	        System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
	        ctx.writeAndFlush("客户端"+ InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
	        super.channelActive(ctx);
	    }
	    
	    public void writeFlush(ChannelHandlerContext ctx,HttpResponseWrapper responseWrapper) {
			// TODO Auto-generated method stub
	    	
			ByteBuf b = null;
			if(responseWrapper.outputBuffer!=null)
				b = Unpooled.copiedBuffer(responseWrapper.outputBuffer);				
			else
				b = Unpooled.copiedBuffer(responseWrapper.content, CharsetUtil.UTF_8);
			
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseWrapper.status,b);
			for(Entry<String,String> e:responseWrapper.headers.entrySet()){
				response.headers().set(e.getKey(), e.getValue());
			}
			
			ChannelFuture f= ctx.writeAndFlush(response);
			if(responseWrapper.headers.containsKey(HttpHeaderNames.CONNECTION) && 
					responseWrapper.headers.get(HttpHeaderNames.CONNECTION).equals(HttpHeaderValues.KEEP_ALIVE)){
				
			}else{
				f.addListener(ChannelFutureListener.CLOSE);
			}
			responseWrapper.release();
		}

	    
}
