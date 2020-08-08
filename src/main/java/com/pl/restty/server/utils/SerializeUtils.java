package com.pl.restty.server.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

public class SerializeUtils {

	public static byte[] marshalling(Object object) throws IOException{
		MarshallingConfiguration configuration = new MarshallingConfiguration();		
		MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
		marshaller.start(Marshalling.createByteOutput(out));
	    marshaller.writeObject(object);
	    marshaller.finish();
	    byte[] buf = out.toByteArray();
	    out.close();
		return buf;
	}
	
	public static String marshallingToHexString(Object object) throws IOException{
		byte[] buf = marshalling(object);
		return bytesToHex(buf);
	}
	
	public static Object unmarshallingHexString(String hexString) throws IOException, ClassNotFoundException{
		byte[] buf = hexToBytes(hexString);
		return unmarshalling(buf);
	}
	
	public static Object unmarshalling(byte[] buf) throws IOException, ClassNotFoundException{
		MarshallingConfiguration configuration = new MarshallingConfiguration();		
		MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		unmarshaller.start(Marshalling.createByteInput(bais));
		Object obj = unmarshaller.readObject();
		unmarshaller.finish();
		bais.close();
		return obj;
	}
	
	public static <T> T unmarshallingT(byte[] buf) throws IOException, ClassNotFoundException{
		Object obj = unmarshalling(buf);
		if(obj==null) return null;
		return (T)obj;
	}
	
	
	public static final String bytesToHex(byte[] bArray) {   
	    StringBuffer sb = new StringBuffer(bArray.length);   
	    String sTemp;   
	    for (int i = 0; i < bArray.length; i++) {   
	     sTemp = Integer.toHexString(0xFF & bArray[i]);   
	     if (sTemp.length() < 2)   
	      sb.append(0);   
	     sb.append(sTemp);   
	    }   
	    return sb.toString();   
	} 
	
	public static byte[] hexToBytes(String inHex){  
	    int hexlen = inHex.length();  
	    byte[] result;  
	    if (hexlen % 2 == 1){  
	        //奇数  
	        hexlen++;  
	        result = new byte[(hexlen/2)];  
	        inHex="0"+inHex;  
	    }else {  
	        //偶数  
	        result = new byte[(hexlen/2)];  
	    }  
	    int j=0;  
	    for (int i = 0; i < hexlen; i+=2){  
	        result[j]=(byte)Integer.parseInt((inHex.substring(i,i+2)),16);  
	        j++;  
	    }  
	    return result;   
	}
	
	
	
	public static void main(String[] args) throws Exception{
//		Map<String,String> m = new HashMap<>();
//		m.put("name", "birdxxxx");
////		
//		byte[] b = SerialUtils.marshalling(m);
//
//		String storedStr = SerialUtils.marshallingToHexString(m);
//		Map m2 = (Map) SerialUtils.unmarshallingHexString(storedStr);
//		
//		System.out.println(m2);

		System.out.println( bytesToHex( "^^".getBytes()) );
		
	}

	public static void tofile(Object obj,String storedfile) throws IOException {
		// TODO Auto-generated method stub
		byte[] dat = marshalling(obj);
		FileOutputStream os = new FileOutputStream(storedfile);
		os.write(dat);
		os.close();
		dat=null;
	}
	
	public static Object fromfile(String storedfile) throws IOException, ClassNotFoundException {
		File f = new File(storedfile);
		if(!f.exists()){
			f=null;return null;
		}
		FileInputStream is = new FileInputStream(storedfile);
		byte[] buf = new byte[(int) f.length()];
		is.read(buf);
		is.close();		
		Object obj = unmarshalling(buf);
		return obj;
	}
	
}
