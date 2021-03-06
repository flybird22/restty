package com.pl.restty.server.annotation;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageUtil {

	
	/** 
     * 获取某包下（包括该包的所有子包）所有类 
     * @param packageName 包名 
     * @return 类的完整名称 
	 * @throws Exception 
     */  
    public static List<String> getClassName(String packageName) throws Exception {
    	if(packageName==null) throw new Exception("PackageUtil.getClassName packageName is null");
        return getClassName(packageName, true);  
    }  
    
    /** 
     * 获取某包下所有类 
     * @param packageName 包名 
     * @param childPackage 是否遍历子包 
     * @return 类的完整名称 
     * @throws Exception 
     */  
    public static List<String> getClassName(String packageName, boolean childPackage) throws Exception {
    	if(packageName==null) throw new Exception("PackageUtil.getClassName packageName is null");
        List<String> fileNames = null;  
        ClassLoader loader = Thread.currentThread().getContextClassLoader();  
        String packagePath = packageName.replace(".", "/");  
        URL url = loader.getResource(packagePath);  
        if (url != null) {  
            String type = url.getProtocol();  
            if (type.equals("file")) {  
                fileNames = getClassNameByFile(packageName,url.getPath(), null, childPackage);  
            } else if (type.equals("jar")) {  
                fileNames = getClassNameByJar(packageName,url.getPath(), childPackage);  
            }  
        } else {  
            fileNames = getClassNameByJars(packageName,((URLClassLoader) loader).getURLs(), packagePath, childPackage);  
        }  
        return fileNames;  
    }  
    
    /** 
     * 从项目文件获取某包下所有类 
     * @param filePath 文件路径 
     * @param className 类名集合 
     * @param childPackage 是否遍历子包 
     * @return 类的完整名称 
     */  
    private static List<String> getClassNameByFile(String packageName,String filePath, List<String> className, boolean childPackage) {  
        List<String> myClassName = new ArrayList<String>();  
        File file = new File(filePath);  
        File[] childFiles = file.listFiles();  
        for (File childFile : childFiles) {  
            if (childFile.isDirectory()) {  
                if (childPackage) {  
                	String childPath=childFile.getPath();
                	childPath=childPath.replace("\\", "/");  
                	String subdir = childPath.substring( childPath.lastIndexOf("/")+1,childPath.length() );
                	String subPackage = packageName+"."+subdir;
                    myClassName.addAll(getClassNameByFile(subPackage,childFile.getPath(), myClassName, childPackage));  
                }  
            } else {  
                String childFilePath = childFile.getPath();  
                if (childFilePath.endsWith(".class")) {  
//                    childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));  
//                    childFilePath = childFilePath.replace("\\", ".");  
//                    myClassName.add(childFilePath);
                	childFilePath = childFilePath.replace("\\", "/");  
                	String class_name=childFilePath.substring(childFilePath.lastIndexOf("/")+1,childFilePath.lastIndexOf("."));
                	myClassName.add(packageName+"."+class_name);
                }  
            }  
        }  
  
        return myClassName;  
    }  
    
    /** 
     * 从jar获取某包下所有类 
     * @param jarPath jar文件路径 
     * @param childPackage 是否遍历子包 
     * @return 类的完整名称 
     */  
    private static List<String> getClassNameByJar(String packageName,String jarPath, boolean childPackage) {  
        List<String> myClassName = new ArrayList<String>();  
        String[] jarInfo = jarPath.split("!");  
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));  
        String packagePath = jarInfo[1].substring(1);  
        try {  
            JarFile jarFile = new JarFile(jarFilePath);  
            Enumeration<JarEntry> entrys = jarFile.entries();  
            while (entrys.hasMoreElements()) {  
                JarEntry jarEntry = entrys.nextElement();  
                String entryName = jarEntry.getName();  
                if (entryName.endsWith(".class")) {  
                    if (childPackage) {  
                        if (entryName.startsWith(packagePath)) {  
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));  
                            myClassName.add(entryName);  
                        }  
                    } else {  
                        int index = entryName.lastIndexOf("/");  
                        String myPackagePath;  
                        if (index != -1) {  
                            myPackagePath = entryName.substring(0, index);  
                        } else {  
                            myPackagePath = entryName;  
                        }  
                        if (myPackagePath.equals(packagePath)) {  
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));  
                            myClassName.add(entryName);  
                        }  
                    }  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return myClassName;  
    }  
  
    /** 
     * 从所有jar中搜索该包，并获取该包下所有类 
     * @param urls URL集合 
     * @param packagePath 包路径 
     * @param childPackage 是否遍历子包 
     * @return 类的完整名称 
     */  
    private static List<String> getClassNameByJars(String packageName,URL[] urls, String packagePath, boolean childPackage) {  
        List<String> myClassName = new ArrayList<String>();  
        if (urls != null) {  
            for (int i = 0; i < urls.length; i++) {  
                URL url = urls[i];  
                String urlPath = url.getPath();  
                // 不必搜索classes文件夹  
                if (urlPath.endsWith("classes/")) {  
                    continue;  
                }  
                String jarPath = urlPath + "!/" + packagePath;  
                myClassName.addAll(getClassNameByJar(packageName,jarPath, childPackage));  
            }  
        }  
        return myClassName;  
    }  
    
//    public static void main(String[] args) throws Exception {  
//        String packageName = "com.jh.flow.controllers";  
//        // List<String> classNames = getClassName(packageName);  
//        List<String> classNames = getClassName(packageName, false);  
//        if (classNames != null) {  
//            for (String className : classNames) {  
//                System.out.println(className);  
//            }  
//        }  
//    } 
}

