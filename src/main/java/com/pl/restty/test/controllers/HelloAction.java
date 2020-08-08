package com.pl.restty.test.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;

import com.pl.config.ConfigRegisterCenter;
import com.pl.restty.server.annotation.BeforeFilter;
import com.pl.restty.server.annotation.Controller;
import com.pl.restty.server.annotation.ParamValid;
import com.pl.restty.server.annotation.ParamValidMethod;
import com.pl.restty.server.annotation.RequestMethod;
import com.pl.restty.server.annotation.ServiceRegister;
import com.pl.restty.server.annotation.UrlMapping;
import com.pl.restty.server.annotation.UrlParam;
import com.pl.restty.server.handlers.HttpRequestWrapper;
import com.pl.restty.server.handlers.HttpResponseWrapper;
import com.pl.restty.server.resource.MimeType;
import com.pl.restty.server.template.ModelAndView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.xml.transform.stream.StreamSource;
@Controller
public class HelloAction {

	@UrlMapping(method=RequestMethod.GET,path="/")
	public void index(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception{
		request.forward("/index.html");
	}
	
//	@ParamConvert(source="json",dest="map")
	@UrlMapping(method=RequestMethod.GET,path="/test",contentType="")	
	public void test(HttpRequestWrapper request, HttpResponseWrapper response){
		response.setContent("test-GET-text/plain ");		
	}
	
	@UrlMapping(method=RequestMethod.GET,path="/user/{id}/add")
	public void index(HttpRequestWrapper request, HttpResponseWrapper response,String id){
		response.setContent("user add "+id);
	}
	
	@UrlMapping(method=RequestMethod.POST,path="/post")
	public void testpost(HttpRequestWrapper request, HttpResponseWrapper response,String id){
		response.setContent("post test received:" + request.getRequestMap());
		
	}
	
	@UrlMapping(method=RequestMethod.POST,path="/login")
	public void login(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception{
		
		request.getSession().set("user", request.getRequestMap().get("user"));
		request.getSession().set("password", request.getRequestMap().get("password"));
		response.redirect("/main");
	}
	
	@UrlMapping(method=RequestMethod.GET,path="/main")
	public ModelAndView users(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception{
		
		Map<String,String> m=new HashMap<>();
		m.put("name", request.getSession().get("user")+"");
		m.put("password", request.getSession().get("password")+"");
		return new ModelAndView(m, "hello.ftl");
	}
	
	
//	@UrlMapping(method=RequestMethod.GET,path="/users",templateEngine="FreeMarkerEngine")
	@UrlMapping(method=RequestMethod.GET,path="/users")
	public ModelAndView template_test(HttpRequestWrapper request, HttpResponseWrapper response){
		Map<String,String> m=new HashMap<>();
		m.put("name", "bird");
		return new ModelAndView(m, "hello.ftl");
	}
	
	
	/**
	 * @ServiceRegister 最好采用域名，因为ip地址可能变化
	 * @param request
	 * @param response
	 * @return
	 */
	@ServiceRegister(path="zookeeper://192.168.1.100:2181/usermanage/",type="TEMP")
//	@ServiceRegister(path="config://server.providers.getUserinfo",type="TEMP")
	@UrlMapping(method=RequestMethod.GET,path="/getUserinfo")
	public ModelAndView SOAGetUserinfo(HttpRequestWrapper request, HttpResponseWrapper response){
		Map<String,String> m=new HashMap<>();
		m.put("name", "bird");
		return new ModelAndView(m, "hello.ftl");
	}
	
//	@ParamValid(name="mobile",method=ParamValidMethod.notNull)
	@UrlMapping(method=RequestMethod.GET,path="/flow/cp.do",contentType="")	
	public void cp(HttpRequestWrapper request, HttpResponseWrapper response){
		
		response.setContent( request.getRequestMap().toString() );
	}
	
	@UrlMapping(method=RequestMethod.GET,path="/header",contentType="")	
	public void header(HttpRequestWrapper request, HttpResponseWrapper response){
		
		String s=request.headers().entries().stream().map((e)->{
			return e.getKey()+"="+e.getValue();
		}).collect(Collectors.joining("\n")).toString();
		
		response.headers().put(HttpHeaderNames.CONTENT_TYPE+"", MimeType.TEXT);
		String res = response.headers().entrySet().stream().map((e)->{
			return e.getKey()+"="+e.getValue();
		}).collect(Collectors.joining("\n")).toString();
		
		response.setContent("-----request.header:----\n"+s+"\n\n"
				+ "----respones.header----\n"+res);
		
	}
	
	
	@BeforeFilter
	public boolean filter(HttpRequestWrapper request, HttpResponseWrapper response){
//		System.out.println("filter...");
		if(request.uri().equals("/aaa")){
			response.setContent("BeforeFilter: "+HttpResponseStatus.FORBIDDEN +"");
			response.setDenied(true);
			return true;
		}
		return false;
	}
	
//	public static void main(String[] a) throws IOException{
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		os.write(12);
//		os.write("abc".getBytes());
//		byte[] buf = os.toByteArray();
//		for(byte b: buf){
//			System.out.print(b);
//		}
//	}
}
