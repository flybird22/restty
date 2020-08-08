package com.pl.restty.server.handlers.ipfilter;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.util.NetUtil;

import java.net.InetSocketAddress;




public class IpAddrFilterHandler extends RuleBasedIpFilter {
	
	public IpAddrFilterHandler(IpFilterRule... rules) {
		super(rules);
		// TODO Auto-generated constructor stub
		
	}



	@Override
	protected ChannelFuture channelRejected(ChannelHandlerContext ctx,
			InetSocketAddress remoteAddress) {
		// TODO Auto-generated method stub
		return super.channelRejected(ctx, remoteAddress);
	}

	

	@Override
	protected boolean accept(ChannelHandlerContext ctx,
			InetSocketAddress remoteAddress) throws Exception {
		// TODO Auto-generated method stub
//		System.out.println(remoteAddress.getAddress().getHostAddress());
//		System.out.println(NetUtil.isValidIpV4Address(remoteAddress.getAddress().getHostAddress())?"IPV4":"IPV6");
		return super.accept(ctx, remoteAddress);		
	}

	@Override
	protected void channelAccepted(ChannelHandlerContext ctx,
			InetSocketAddress remoteAddress) {
		// TODO Auto-generated method stub
		super.channelAccepted(ctx, remoteAddress);
	}



	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		super.channelRead(ctx, msg);
	}
	
	

}
