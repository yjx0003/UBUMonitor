package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
	
	private static Properties properties;
	private static String path;
	
	public static void initialize(String path) throws IOException {
		properties = new Properties();

		File file = new File(path);
		if (!file.isFile() && !file.createNewFile()) {
			LOGGER.error("No se ha podido crear el fichero properties: " + path);
		} else { // si existe el fichero properties inicializamos los valores
			try (InputStream in = new FileInputStream(file)) {

				properties.load(in);
				Config.path = path;
			} catch (IOException e) {
				LOGGER.error("No se ha podido cargar " + path);
			}
		}
	}
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public static String getProperty(String key,String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
	
	public static void save(String path) {
		File file = new File(path);
		try (FileOutputStream out = new FileOutputStream(file)) {
			properties.store(out, null);

		} catch (IOException e) {
			LOGGER.error("No se ha podido guardar el fichero {}", file.getAbsolutePath());
		}
	}
	
	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
		
	}
	
	public static void save() {
		save(path);
	}


	
	
	
}
