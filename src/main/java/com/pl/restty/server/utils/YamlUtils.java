package com.pl.restty.server.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlConfig.WriteConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

public class YamlUtils {

	public static Map toMap(String yaml) throws IOException{
		YamlReader reader = new YamlReader(new FileReader(yaml));
		Map obj = reader.read(Map.class);
		reader.close();
		return obj;		
	}
	
	public static <T> T toObject(String yaml, Class<T> class_t) throws IOException{
		YamlReader reader = new YamlReader(new FileReader(yaml));
		T obj = reader.read(class_t);
		reader.close();
		return obj;		
	}
	
	public static <T> void toYaml(String yaml, T t) throws IOException{
		YamlWriter writer = new YamlWriter( new FileWriter(yaml) );		
		writer.write(t);
		writer.close();
	}
	
}
