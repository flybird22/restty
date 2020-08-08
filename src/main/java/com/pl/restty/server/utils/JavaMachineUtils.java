package com.pl.restty.server.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JavaMachineUtils {

	public static final int getPid() {
		try {
			RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
			Field jvm = runtime.getClass().getDeclaredField("jvm");
			jvm.setAccessible(true);
			Object mgmt = jvm.get(runtime);
			Method pidMethod = mgmt.getClass().getDeclaredMethod("getProcessId");
			pidMethod.setAccessible(true);
			int pid = (Integer) pidMethod.invoke(mgmt);
			return pid;
		} catch (Exception e) {
			return -1;
		}
	}
	
	public static void main(String[] a){
//		System.out.println(JavaMachineUtils.getPid());
		String sessionId = RandomUtil.randomString(6)+"-"+DatetimeUtils.dateFormat("yyMMddHHmmss")
				+"-"+JavaMachineUtils.getPid();
		System.out.println(sessionId);
	}

	public final static int availableProcessors() {
		// TODO Auto-generated method stub
		return Runtime.getRuntime().availableProcessors();
	}
}
