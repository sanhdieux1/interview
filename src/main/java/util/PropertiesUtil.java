package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;


public class PropertiesUtil {
	final static Logger logger = Logger.getLogger(PropertiesUtil.class);
	public static Properties prop = new Properties();
	private static PropertiesUtil INSTANCE = new PropertiesUtil();
	private PropertiesUtil(){
		prop.putAll(load("messages.properties"));
		prop.putAll(load("databases.properties"));
	}
	public String getString(String name) {
		String result = prop.getProperty(name);
		return result;
	}
	public static PropertiesUtil getInstance(){
		return INSTANCE;
	}
	private Properties load(String file){
		Properties prop = new Properties();
		try (InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream(file)) {
			prop.load(input);
		} catch (IOException e) {
			logger.warn("Cannot open messages.proerties");
			//ignore exception
		}
		return prop;
	}
}