package es.ubu.lsi.ubumonitor.controllers.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigHelper.class);

	private static JSONObject properties;
	private static String path;

	public static void initialize(String path) throws IOException {

		File file = new File(path);
		if (!file.isFile() && !file.createNewFile()) {
			LOGGER.error("No se ha podido crear el fichero properties: {} ", path);
			properties = new JSONObject();
		} else { // si existe el fichero properties inicializamos los valores
			try (InputStream in = new FileInputStream(file)) {
				ConfigHelper.path = path;
				properties = new JSONObject(new JSONTokener(in));

			} catch (Exception e) {
				LOGGER.error("No se ha podido cargar ", e);
				properties = new JSONObject();
			}
		}

	}

	public static String getProperty(String key) {
		return properties.optString(key);
	}

	public static String getProperty(String key, String defaultValue) {
		return properties.optString(key, defaultValue);
	}

	public static int getProperty(String key, int defaultValue) {
		return properties.optInt(key, defaultValue);
	}

	public static boolean getProperty(String key, boolean defaultValue) {
		return properties.optBoolean(key, defaultValue);
	}

	public static void setProperty(String key, String value) {
		properties.put(key, value);

	}

	public static void setProperty(String key, int value) {
		properties.put(key, value);
	}

	public static void setProperty(String key, boolean value) {
		properties.put(key, value);
	}

	public static void setArray(String key, Collection<String> array) {
		properties.put(key, array);
	}

	public static JSONArray getArray(String key) {
		JSONArray array = properties.optJSONArray(key);
		if (array == null) {
			JSONArray jsonArray =  new JSONArray();
			properties.put(key, jsonArray);
			return jsonArray;
		}
		return array;
	}

	public static void appendArray(String key, Object value) {
		properties.append(key, value);
	}

	public static void save() {
		save(path);
	}

	public static void save(String path) {

		try (FileWriter file = new FileWriter(path)) {

			file.write(properties.toString(4));

			LOGGER.info("config guardado");
		} catch (IOException e) {
			LOGGER.error("No se ha podido guardar el fichero {}", path);
		}
	}

	private ConfigHelper() {
	}

}
