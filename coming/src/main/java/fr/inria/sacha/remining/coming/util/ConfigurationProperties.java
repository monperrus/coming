package fr.inria.sacha.remining.coming.util;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurationProperties {

	public static Properties properties;

	static{
		  FileInputStream propFile;
		try {
			properties = new Properties();
			propFile = new FileInputStream("configuration.properties");

				properties.load(propFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	}

	public static String getProperty(String key){
		return properties.getProperty(key);
	}

	public static Integer getPropertyInteger(String key){
		return Integer.valueOf(properties.getProperty(key));
	}

	public static Boolean getPropertyBoolean(String key){
		return Boolean.valueOf(properties.getProperty(key));
	}
	public static Double getPropertyDouble(String key){
		return Double.valueOf(properties.getProperty(key));
	}

	public static void main(String[] s){
		String ss = ConfigurationProperties.properties.getProperty("test");
		System.out.println("-->"+ss);
	}
}
