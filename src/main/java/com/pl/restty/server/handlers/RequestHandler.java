package com.pl.restty.server.handlers;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pl.restty.server.ContextWrapper;
import com.pl.restty.server.HttpServer;

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
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class RequestHandler extends ChannelInboundHandlerAdapter {
	
	private Logger Log = LoggerFactory.getLogger(RequestHandler.class);
	ContextWrapper context=ContextWrapper.create();
	//set is no sequnce
//	Set<RequestWorker> workers = new ConcurrentSet<>();
	List<RequestWorker> workers = new CopyOnWriteArrayList<>();
	final HttpServer server_;
	
	public RequestHandler(HttpServer httpServer) {
		// TODO Auto-generated constructor stub
		server_ = httpServer;
	}


	public void initRequestWorkers() throws Exception{
		List<Class<? extends RequestWorker>> defWorks = server().getDefineRequestWorks();
		if(defWorks==null || defWorks.isEmpty()) {
			workers.add( new StaticFileHandler() );
			workers.add( new ServletHandler() );
		}
		else {
			for(int i=0;i<defWorks.size();i++) {
				Class<?> t = defWorks.get(i);
				if(t==null) continue;
				workers.add( (RequestWorker) t.newInstance() );
			}
		}
				
	}
	
	public HttpServer server() {
		return server_;
	}

	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelActive(ctx);
		initRequestWorkers();
//		Log.info("initRequestWorkers.workers.size="+workers.size());
	}
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if(! (msg instanceof FullHttpRequest)){
			super.channelRead(ctx, msg);
            return;
        }
    	
    	
    	context.ctx =ctx;
    	context.request = (FullHttpRequest) msg;
    	
    	HttpRequestWrapper request = new HttpRequestWrapper(context);	
    	HttpResponseWrapper response = new HttpResponseWrapper(context);
    	
    	handleWork(request,response);
    	
	}
	
	public void doWorkline(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception{
		for(RequestWorker worker : workers){
    		boolean deal = worker.handle(request,response);
    		if(deal) break;
    	}
	}
	
	public void handleWork(HttpRequestWrapper request, HttpResponseWrapper response){
		try{
			doWorkline(request,response);        	
    		if(request.getForwardUrl()!=null){
    			request.setForwardUrl(null);
    			doWorkline(request,response);
    		}
    	}
    	catch(Exception e){
    		response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    		e.printStackTrace();
    		Log.info("handleWork "+e.toString());
    	}
    	finally{
    		if(request!=null) request.release();
    	}
    	
    	writeFlush(response.ctx,response);
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

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.handlerRemoved(ctx);
		workers.clear();
		workers=null;
		context=null;
	}

	public List<RequestWorker> getWorkers() {
		return workers;
	}

	

}
