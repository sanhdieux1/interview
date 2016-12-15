package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;


public class PropertiesUtil {
	final static Logger logger = Logger.getLogger(PropertiesUtil.class);

	public static String getString(String name) {
		Properties prop = new Properties();
		
		try (InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream("messages.properties")) {
			prop.load(input);
		} catch (IOException e) {
			logger.warn("Cannot open messages.proerties");
			//ignore exception
		}
		String result = prop.getProperty(name);
		if(result == null){
			logger.warn("Cannot found property name:" + name);
		}
		return result;
	}
}