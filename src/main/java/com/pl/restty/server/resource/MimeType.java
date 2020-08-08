package com.pl.restty.server.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.sf.json.JSONObject;



public class MimeType {

	public static final String TEXT = "text/plain; charset=UTF-8";
	public static final String HTML = "text/html; charset=UTF-8";
	public static final String XML = "text/xml; charset=UTF-8";
	public static final String HTTP = "message/http; charset=UTF-8";
	public static final String JSON = "application/json; charset=UTF-8";
	public static final String FORM = "application/x-www-form-urlencoded";
	public static final String FORM_DATA = "multipart/form-data";
	
	
    final static String CONTENT_TYPE = "Content-Type";

    private static volatile boolean guessingOn = true;

    private static Map<String, String> mappings = new HashMap();

    static {
        mappings.put("au", "audio/basic");
        mappings.put("avi", "video/msvideo,video/avi,video/x-msvideo");
        mappings.put("bmp", "image/bmp");
        mappings.put("bz2", "application/x-bzip2");
        mappings.put("css", "text/css");
        mappings.put("dtd", "application/xml-dtd");
        mappings.put("doc", "application/msword");
        mappings.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mappings.put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        mappings.put("eot", "application/vnd.ms-fontobject");
        mappings.put("es", "application/ecmascript");
        mappings.put("exe", "application/octet-stream");
        mappings.put("gif", "image/gif");
        mappings.put("gz", "application/x-gzip");
        mappings.put("ico", "image/x-icon");
        mappings.put("hqx", "application/mac-binhex40");
        mappings.put("htm", "text/html");
        mappings.put("html", "text/html");
        mappings.put("jar", "application/java-archive");
        mappings.put("jpg", "image/jpeg");
        mappings.put("js", "application/javascript");
        mappings.put("json", "application/json");
        mappings.put("midi", "audio/x-midi");
        mappings.put("mp3", "audio/mpeg");
        mappings.put("mpeg", "video/mpeg");
        mappings.put("ogg", "audio/vorbis,application/ogg");
        mappings.put("otf", "application/font-otf");
        mappings.put("pdf", "application/pdf");
        mappings.put("pl", "application/x-perl");
        mappings.put("png", "image/png");
        mappings.put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
        mappings.put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        mappings.put("ppt", "application/vnd.ms-powerpointtd");
        mappings.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        mappings.put("ps", "application/postscript");
        mappings.put("qt", "video/quicktime");
        mappings.put("ra", "audio/x-pn-realaudio,audio/vnd.rn-realaudio");
        mappings.put("rar", "application/x-rar-compressed");
        mappings.put("ram", "audio/x-pn-realaudio,audio/vnd.rn-realaudio");
        mappings.put("rdf", "application/rdf,application/rdf+xml");
        mappings.put("rtf", "application/rtf");
        mappings.put("sgml", "text/sgml");
        mappings.put("sit", "application/x-stuffit");
        mappings.put("sldx", "application/vnd.openxmlformats-officedocument.presentationml.slide");
        mappings.put("svg", "image/svg+xml");
        mappings.put("swf", "application/x-shockwave-flash");
        mappings.put("tgz", "application/x-tar");
        mappings.put("tiff", "image/tiff");
        mappings.put("tsv", "text/tab-separated-values");
        mappings.put("ttf", "application/font-ttf");
        mappings.put("txt", "text/plain");
        mappings.put("wav", "audio/wav,audio/x-wav");
        mappings.put("woff", "application/font-woff");
        mappings.put("woff2", "application/font-woff2");
        mappings.put("xlam", "application/vnd.ms-excel.addin.macroEnabled.12");
        mappings.put("xls", "application/vnd.ms-excel");
        mappings.put("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12");
        mappings.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        mappings.put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        mappings.put("xml", "application/xml");
        mappings.put("zip", "application/zip,application/x-compressed-zip");
    }

    public static void register(String extension, String mimeType) {
        mappings.put(extension, mimeType);
    }

    public static void disableGuessing() {
        guessingOn = false;
    }

//    public static String fromResource(AbstractFileResolvingResource resource) {
//        String filename = Optional.ofNullable(resource.getFilename()).orElse("");
//        return getMimeType(filename);
//    }
    
    public static boolean isStaticResource(String filename){
    	String fileExtension = filename.replaceAll("^.*\\.(.*)$", "$1");
    	return mappings.containsKey(fileExtension);
    }

    public static String getMimeType(String filename) {
        String fileExtension = filename.replaceAll("^.*\\.(.*)$", "$1");
        return mappings.getOrDefault(fileExtension, "application/octet-stream");
    }

    protected static String fromPathInfo(String pathInfo) {
        return getMimeType(pathInfo);
    }

    protected static boolean shouldGuess() {
        return guessingOn;
    }
    
 
//    public static void main(String[] a){
////    	System.out.println( MimeType.getMimeType("json") );
//    	JSONObject json = JSONObject.fromObject("{\"message\":\"aaa\"}");
//    	System.out.println(json);
//    }

	public static boolean isJsonType(String headValue) {
		// TODO Auto-generated method stub
		if(headValue==null) return false;
		return headValue.contains( mappings.get("json") );
	}
	
	//isFormType
	public static boolean isFormType(String headValue) {
		// TODO Auto-generated method stub
		if(headValue==null) return false;
		return headValue.contains( FORM );
	}
	
	public static boolean isFormDataType(String headValue) {
		// TODO Auto-generated method stub
		if(headValue==null) return false;
		return headValue.contains( FORM_DATA );
	}
	
	public static boolean isXmlType(String headValue) {
		// TODO Auto-generated method stub
		return headValue.contains( mappings.get("xml") );
	}
	
	public static boolean isTextType(String headValue){
		return headValue.contains( TEXT );
	}
}
