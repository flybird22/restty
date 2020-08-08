package com.pl.restty.server.handlers;

import com.pl.restty.server.route.StaticFileFilter;

public class StaticFileHandler implements RequestWorker {

	@Override
	public boolean handle(HttpRequestWrapper request,
			HttpResponseWrapper response) throws Exception {
		// TODO Auto-generated method stub
		boolean isConsume = StaticFileFilter.handle(request,response);
		if(isConsume){
			return true;
		}
		return false;
	}

}
