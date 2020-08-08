package com.pl.restty.converts.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.AttributeKey;

import com.pl.restty.server.handlers.HttpRequestWrapper;
import com.pl.restty.server.handlers.HttpResponseWrapper;
import com.pl.restty.server.handlers.RequestWorker;
import com.pl.restty.server.resource.MimeType;
import com.pl.restty.server.utils.YamlUtils;

public class RequestTexttoMapHandler implements RequestWorker {

	/**
	 * phone-mobile;mb-mb;
	 */
	@Override
	public boolean handle(HttpRequestWrapper request,
			HttpResponseWrapper response) throws Exception {
		// TODO Auto-generated method stub
//		String contentType = request.headers().get( HttpHeaderNames.CONTENT_TYPE );
//		if( !(MimeType.isTextType(contentType) || MimeType.isFormType(contentType)) ){
//			Map<String,String> m = request.getRequstMap();		 
//			String chid="18";
//			Map<String,String> channelConfig = ChannelConfigCenter.getConfig(chid);
//			Map<String,String> convert_m = RequestConvertUtils.toInternalMap(channelConfig,m);
//			request.getContext().attributes.put("convert", convert_m);
//			
//		}
//		
		return false;
	}
	
	
	public static void mainx(String[] args) throws IOException{
		
		Map<String,Object> m1 = new LinkedHashMap<String,Object>(){{
			put("id", "18");
			put("ch_name", "test");
			put("ch_account", "admin@test");
			put("ch_password", "123456");
			put("ch_key", "0d10d895c6654e4cb055edf5f7ad61c5");
			put("ch_callbackurl", "http://127.0.0.1:9000/sync18");
			put("order",new HashMap<String,Object>(){{								
				put("request",new HashMap<String,Object>(){{
					put("url","http://127.0.0.1:9000/sp.do");
					put("content_type","text");
					put("params",new HashMap<String,Object>(){{
						put("orderNo","orderid");
						put("mobile","mobile");
						put("userName","ch_account");
						put("sign",new HashMap<String,Object>(){{
							put("match","sign");
							put("method","md5");
							put("class","MD5SignField");
							put("source","userName=?&userPwd=??&mobile=?|userName,userPwd,ch_key,mobile");
						}});
					}});					
				}});
				put("response",new HashMap<String,Object>(){{
					put("content_type","json");
					put("params",new HashMap<String,Object>(){{
						put("code",new HashMap<String,Object>(){{
							put("match","status");
							put("success","1");
						}});
						put("tip","message");
					}});
				}});
			}});
			put("callback",new HashMap<String,Object>(){{					
				put("request",new HashMap<String,Object>(){{
					put("content_type","text");
					put("decode","urldecode");
					put("params",new HashMap<String,Object>(){{
						put("orderNo","orderid");
						put("resCode",new HashMap<String,Object>(){{
							put("match","report_status");
							put("success","00");
						}});
						put("redMsg","message");						
					}});					
				}});
			}});
		}};
		
		List<Map<String,Object>> lst = new ArrayList<>();
		lst.add(m1);
		
		YamlUtils.toYaml(System.getProperty("user.dir")+"/gw-18.yml", m1);
		
		Map<String,Object> m2 = YamlUtils.toMap(System.getProperty("user.dir")+"/gw-18.yml");
		System.out.println(m2);
		YamlUtils.toYaml(System.getProperty("user.dir")+"/gw-18-1.yml", m2);
		
		YamlUtils.toYaml(System.getProperty("user.dir")+"/gw-18-3.yml", lst);
		
		List ls2 = YamlUtils.toObject(System.getProperty("user.dir")+"/gw-18-3.yml",
				List.class);
		System.out.println( ls2 );
	}
	
//	public static Map<String,String> testMap(){
//		
//		return new HashMap<String,String>(){{
//			put("id",Thread.currentThread().getId()+"");
//		}};
//	}
	
//	public static void testMap1(Map<String,String> m){		
//		m.put("id",Thread.currentThread().getId()+"");
//	}
	
//	public static void main(String[] args) throws IOException, InterruptedException{
//		
//		Callable<Integer>[] tasks = new Callable[10];
//		for(int i=0;i<10;i++){
//			tasks[i] = ()->{
//				Map<String,String> m=new HashMap<>();
//				testMap1(m);
//				System.out.println(Thread.currentThread().getId()+" "+m.toString() +" "+
//						(Thread.currentThread().getId()==Integer.valueOf( m.get("id")+"" )));
//				return 0;
//			};
//		}
//		
//		Executors.newFixedThreadPool(10).invokeAll(Arrays.asList(tasks));
//		
//	}
}
