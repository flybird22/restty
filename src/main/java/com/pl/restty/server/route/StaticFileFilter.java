package com.pl.restty.server.route;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.zip.GZIPOutputStream;

import com.pl.restty.server.handlers.HttpRequestWrapper;
import com.pl.restty.server.handlers.HttpResponseWrapper;
import com.pl.restty.server.resource.MimeType;
import com.pl.restty.server.utils.IOUtils;



public class StaticFileFilter {
	public static String locationPath=null;
	
	public static boolean handle(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception {
		// TODO Auto-generated method stub
		if(!MimeType.isStaticResource(request.uri())){
        	return false;
        }
		if(locationPath==null) throw new Exception("StaticFileFilter.locationPath is null");
		OutputFile(request,response);
		return true;
	}

	private static void OutputFile(HttpRequestWrapper request, HttpResponseWrapper response) {
		// TODO Auto-generated method stub
		String realPath = resourcePathByRequest(request.uri());
		if(realPath==null) {
			response.status(HttpResponseStatus.NOT_FOUND);			
			return;
		}
		String type=MimeType.getMimeType(request.uri());
		if(type!=null) response.setContentType(type);
		try {
			InputStream is = new FileInputStream(realPath);
//			IOUtils.copy(is, response.getOutputStream());
			String accept = request.raw().headers().get("Accept-Encoding");
			if(accept!=null && Arrays.asList(accept.split(",")).stream().anyMatch(StringMatch)){
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				GZIPOutputStream gzip = new GZIPOutputStream(os);
				IOUtils.copy(is, gzip);
				gzip.close();
				response.setOutputBuffer(os.toByteArray());				
				response.setHeader("Content-Encoding", GZIP);
				os.close();
			}
			else{
				byte[] buf = IOUtils.toByteArray(is);
				response.setOutputBuffer(buf);
			}
			is.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	private static String resourcePathByRequest(String uri) {
		// TODO Auto-generated method stub
		String filePath = uri.split("\\?")[0];
		if(filePath.startsWith("/"))
			filePath = filePath.substring(1);
		
		String realPath = locationPath + filePath;
		File f = new File(realPath);
		if(f.exists() && f.isFile() && f.canRead()){
			f=null;
			return realPath;
		}
		return null;
	}
	
	private static final String GZIP = "gzip";
	private static Predicate<String> StringMatch = (s) ->{
		if(s == null) return false;
		return s.contains(GZIP);
	};
//	private static class StringMatch implements Predicate<String> {
//        @Override
//        public boolean test(String s) {
//            if (s == null) {
//                return false;
//            }
//
//            return s.contains(GZIP);
//        }
//    }

}
