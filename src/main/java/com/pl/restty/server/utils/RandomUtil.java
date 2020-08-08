package com.pl.restty.server.utils;

import java.util.Random;
import java.util.RandomAccess;

public class RandomUtil {
	
	public static final String randomString(int len){
		String ss = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		int size = ss.length();
		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		for(int i=0;i<len; i++){			
			int idx = r.nextInt(size); //
			sb.append(ss.substring(idx, idx+1));
		}
		String rs = sb.toString();
		sb.setLength(0);sb=null;
		r=null;
		return rs;
	}
	
	public static int randomInt( int max){
		Random r = new Random();
		return r.nextInt(max);
	}
	
	public static String randomNumber( int len){
		Random r = new Random();
		// 获得随机数  1.233241*10的len次方
	    double pross = (1 + r.nextDouble()) * Math.pow(10, len);
	    String fixed = String.valueOf(pross);
	    return fixed.substring(1, len + 1);
	}
	
//	public static final int randomInt
	public static void main(String[] a){
		System.out.println( RandomUtil.randomInt(2) );
		System.out.println(RandomUtil.randomString(6));
		System.out.println(RandomUtil.randomNumber(6));
	}
}
