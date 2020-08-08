package com.pl.restty.converts.parse;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.pl.restty.converts.utils.ConvertUtils;
import com.pl.restty.server.handlers.HttpRequestWrapper;
import com.pl.restty.server.handlers.HttpResponseWrapper;
import com.pl.restty.server.handlers.RequestWorker;

public class RequestJsontoMapHandler implements RequestWorker {

	@Override
	public boolean handle(HttpRequestWrapper request,
			HttpResponseWrapper response) throws Exception {
		// TODO Auto-generated method stub
		if(request.getRequestJson()!=null){
			if(request.getRequestJson() instanceof JSONObject){
				Map<String,Object>m=ConvertUtils.jsonObjectToMap((JSONObject) request.getRequestJson());
				request.setRequestMap(m);
			}
			else if(request.getRequestJson() instanceof JSONArray){
				List<Object> ls = ConvertUtils.jsonObjectToList( (JSONArray) request.getRequestJson() );
				request.setRequestList(ls);
			}
		}
		return false;
	}

}
