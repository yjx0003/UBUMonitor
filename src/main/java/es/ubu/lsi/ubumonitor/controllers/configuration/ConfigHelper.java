package es.ubu.lsi.ubumonitor.controllers.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigHelper.class);

	private static Properties properties;
	private static String path;

	public static void initialize(String path) throws IOException {
		properties = new Properties();

		File file = new File(path);
		if (!file.isFile() && !file.createNewFile()) {
			LOGGER.error("No se ha podido crear el fichero properties: {} ", path);
		} else { // si existe el fichero properties inicializamos los valores
			try (InputStream in = new FileInputStream(file)) {

				properties.load(in);
				ConfigHelper.path = path;
			} catch (IOException e) {
				LOGGER.error("No se ha podido cargar {} ", path);
			}
		}
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public static int getProperty(String key, int defaultValue) {
		return Integer.valueOf(properties.getProperty(key, Integer.toString(defaultValue)));
	}

	public static void save(String path) {
		File file = new File(path);
		try (FileOutputStream out = new FileOutputStream(file)) {
			properties.store(out, null);
			LOGGER.info("config guardado");
		} catch (IOException e) {
			LOGGER.error("No se ha podido guardar el fichero {}", file.getAbsolutePath());
		}
	}

	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);

	}

	public static void setProperty(String key, int value) {
		properties.setProperty(key, Integer.toString(value));
	}
	
	public static void setArray(String key, List<String> array) {
		setProperty(key, new JSONArray(array).toString());
	}
	
	public static List<String> getArray(String key) {
		return new JSONArray(getProperty(key, "[]")).toList().stream().map(Object::toString).collect(Collectors.toList());
	}

	public static void save() {
		save(path);
	}

	private ConfigHelper() {
	}

}
