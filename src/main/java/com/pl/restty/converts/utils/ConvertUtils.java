package com.pl.restty.converts.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class ConvertUtils {


	public static final String Map_Array_key = "Array";
	
	public final static Object findMap(Map<String,Object> srcMap,String parentKey,String findKey){
		Object parent = findMap(srcMap,parentKey);
		if(parent instanceof Map || parent instanceof HashMap){
			Object ob = findMap((Map<String, Object>) parent,findKey);
			return ob;
		}
		else{
			return null;
		}
	}
	/**
	 * 查找map内容，包含字内容
	 * @param srcMap
	 * @param key
	 * @return
	 */
	public final static Object findMap(Map<String,Object> srcMap,String key){
		Object ob=null;
		if(srcMap.containsKey(key)){
			ob=srcMap.get(key);		
			return ob;
		}
		else{
			for(Map.Entry<String, Object> e: srcMap.entrySet()){
				Object v=e.getValue();
//				System.out.println(e.getKey());
				if(v instanceof Map || v instanceof HashMap){
					ob = findMap((Map<String,Object>)v,key);
					if(ob!=null)
						return ob;
				}
				else if( v instanceof List || v instanceof ArrayList ){
					ob=findMapInList((List<Object>)v,key);
					if(ob!=null)
						return ob;
				}
				else{
//					System.out.println(e.getKey());
				}
			}
		}
		return ob;
	}
	
	/**
	 * List<Map>中查找key对应内容,返回第一个
	 * @param obs
	 * @param key
	 * @return
	 */
	public final  static Object findMapInList(List<Object> obs,String key){
		for(Object o: obs){
			if(o instanceof Map || o instanceof HashMap ){
				Object t = findMap((Map<String,Object>)o,key);
				if(t!=null) return t;
			}
			else if(o instanceof List || o instanceof ArrayList ){
				Object t=findMapInList((List<Object>)o,key);
				if(t!=null) return t;
			}
		}
		return null;
	}
	
	/**
	 * 如果返回的是一个list，则 List<Object>=result_map.get("array");
	 * @param result
	 * @return
	 */
	public final  static Map<String,Object> JsonStringToMap(String result){
		if(result==null || result.length()<1){
			return null;
		}
		
		Map<String, Object> result_map=null;
		List<Object> result_list;
		Object json = new JSONTokener(result).nextValue();
		if(json instanceof JSONObject){
			result_map=jsonObjectToMap((JSONObject)json);						
		}		
		else if(json instanceof JSONArray){
			result_list=jsonObjectToList((JSONArray)json);
			result_map = new HashMap<String,Object>();
			result_map.put(Map_Array_key, result_list);
		}
		
		return result_map;
	}
	
	/**
	 * JSONObject to Map<String,Object>
	 * @param json
	 * @return
	 */
	public final  static Map<String,Object> jsonObjectToMap(JSONObject json){
		Map<String,Object>m=new HashMap<String,Object>();
		Set<String> entrys=json.keySet();
		for(String k: entrys){
			Object val=json.get(k);
			if(val instanceof JSONObject){
				m.put(k, jsonObjectToMap((JSONObject)val));
			}
			else if(val instanceof JSONArray){
				m.put(k, jsonObjectToList((JSONArray)val));
			}
			else{
				m.put(k, val);
			}
		}
		return m;		
	}
	
	/**
	 * JSONArray to List<Object>
	 * @param array
	 * @return
	 */
	public final  static List<Object> jsonObjectToList( JSONArray array ){
		List<Object> lst = new ArrayList<Object>();
		for(int i=0;i<array.size();i++){
			Object val=array.get(i);
			if(val instanceof JSONObject){
				lst.add(jsonObjectToMap((JSONObject)val));
			}
			else if(val instanceof JSONArray){
				lst.add(jsonObjectToList((JSONArray)val));
			}
			else{
				lst.add(val);
			}
		}
		return lst;
	}
	
	public final  static Map<String,Object>xmlStringToMap(String result) throws Exception{
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Document doc=DocumentHelper.parseText(result);
		Element em=doc.getRootElement();
//		resultMap.put(em.getName(), null);
		xmlToMap(em,resultMap);
		return resultMap;
	}
	
	public final static void xmlToMap(Element em,Map<String,Object> map){		
		
		String name = em.getName();
		String text = em.getText().trim();
		if(text.length()==0){ //has child
			Map<String,Object> newMap=new HashMap<String,Object>();
			map.put(name, newMap);
			Iterator<Element> i=em.elements().iterator();
			while(i.hasNext()){
				Element child=i.next();
				xmlToMap(child,newMap);
			}
		}
		else{
			map.put(name, text);
		}
				
	}
	
	public final static String makeKeyValuePaired(Map<String,Object> m){
		if(m==null){
			System.out.println("TextMaker.makeKeyValuePaired.error");
			return null;
		}
		StringBuilder sb=new StringBuilder();
		int i=0;
		for(String s:m.keySet()){
			sb.append(s+"="+m.get(s));
			if(i<m.size()-1){
				sb.append("&");
			}
			i++;
		}
		
		return sb.toString();
	}
	
	public static String RandomNumber(int n) {
		StringBuilder sb = new StringBuilder();		
		for(int i=0;i<n;i++ ) {
			int a = (int)(10*(Math.random()));
//			System.out.print(a[i]);
			sb.append(a);
		}

		return sb.toString();
	}
	
}

