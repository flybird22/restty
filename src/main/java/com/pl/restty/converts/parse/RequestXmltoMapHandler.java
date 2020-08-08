package com.pl.restty.converts.parse;

import java.util.Map;

import com.pl.restty.converts.utils.ConvertUtils;
import com.pl.restty.server.handlers.HttpRequestWrapper;
import com.pl.restty.server.handlers.HttpResponseWrapper;
import com.pl.restty.server.handlers.RequestWorker;
import com.pl.restty.server.utils.StringUtils;

public class RequestXmltoMapHandler implements RequestWorker {

	@Override
	public boolean handle(HttpRequestWrapper request,
			HttpResponseWrapper response) throws Exception {
		// TODO Auto-generated method stub
		String xmlString = request.content();
		if(StringUtils.isEmpty(xmlString))
			return false;
		Map<String,Object> m=ConvertUtils.xmlStringToMap(xmlString);
		request.setRequestMap(m);
		
		return false;
	}

}
