package com.pl.restty.server.handlers.ipfilter;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * 改写替换AbstractRemoteAddressFilter 因为该类没有对register等进行处理
 * 使用: ipfilter = new AbstractIpFilterHandler();pipeline.addlast(ipfilter);
 * 重载ipfilter的Accept方法
 * @author pei
 *
 */

public abstract class AbstractIpFilterHandler extends ChannelInboundHandlerAdapter {
	private final AttributeKey<IpFilterDecision> decisionKey = AttributeKey.valueOf(getClass().getName());
	
	public enum IpFilterDecision {
	    REJECTED
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
//		super.channelRegistered(ctx);
		final InetSocketAddress ipAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		if (!accept(ipAddress)) {
            // the channel might be active already
            if (ctx.channel().isActive()) {
                handleRejected(ctx);
            } else {
                // if the channel is not active yet, store the decision for later use
                // in #channelActive(ChannelHandlerContext ctx)
                Attribute<IpFilterDecision> decision = ctx.channel().attr(decisionKey);
                decision.set(IpFilterDecision.REJECTED);            	
                super.channelRegistered(ctx);
            }
        } else {
            super.channelRegistered(ctx);
        }
	}
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final IpFilterDecision decision = ctx.channel().attr(decisionKey).get();
         if (decision == IpFilterDecision.REJECTED) {
            handleRejected(ctx);
        } else {
            super.channelActive(ctx);
        }
    }
	
	private void handleRejected(ChannelHandlerContext ctx) {
        final InetSocketAddress ipAddress = (InetSocketAddress) ctx.channel().remoteAddress();
         ChannelFuture rejectedFuture = rejected(ctx, ipAddress);
        if (rejectedFuture != null) {
            rejectedFuture.addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.close();
        }
    }
	
	/**
     * This method is called immediately after a {@link io.netty.channel.Channel} gets registered.
     *
     * @return Return true if connections from this IP address and port should be accepted. False otherwise.
     */
    protected abstract boolean accept(InetSocketAddress ipAndPort) throws Exception;
     /**
     * This method is called if ipAndPort gets rejected by {@link #accept(InetSocketAddress)}.
     * You should override it if you would like to handle (e.g. respond to) rejected addresses.
     *
     * @return A {@link ChannelFuture} if you perform I/O operations, so that
     *         the {@link Channel} can be closed once it completes. Null otherwise.
     */
    protected abstract ChannelFuture rejected(ChannelHandlerContext ctx, InetSocketAddress ipAndPort);

}
