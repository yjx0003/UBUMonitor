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

/**
 * Static class storing global app configuration, can read and save in JSON
 * format.
 * 
 * @author Yi Peng Ji
 *
 */
public class ConfigHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigHelper.class);

	private static JSONObject properties;
	private static String path;

	/**
	 * Initialize JSON properties with the path, if the path not existes then
	 * creates it. If can't creates the file use empty configuration.
	 * 
	 * @param path where is the path
	 */
	public static void initialize(String path) {

		File file = new File(path);
		try {
			if (!file.isFile() && !file.createNewFile()) {
				LOGGER.error("No se ha podido crear el fichero properties: {} ", path);
				properties = new JSONObject();
			} else { // si existe el fichero properties inicializamos los valores
				try (InputStream in = new FileInputStream(file)) {
					ConfigHelper.path = path;
					properties = new JSONObject(new JSONTokener(in));
				}
			}
		} catch (Exception e) {
			LOGGER.error("No se ha podido crear el fichero properties: {} ", path);
			properties = new JSONObject();
		}

	}

	/**
	 * Get String property configuration using the key
	 * @param key the key of the configuration
	 * @return value of the property
	 */
	public static String getProperty(String key) {
		return properties.optString(key);
	}
	
	/**
	 * Get String property, if not exists return defaultValue
	 * @param key the key of the configuration
	 * @param defaultValue default value in case not exists the key
	 * @return value of the property if key exist, otherwise the default value
	 */
	public static String getProperty(String key, String defaultValue) {
		return properties.optString(key, defaultValue);
	}

	/**
	 * Get double value with the key, or default value if not exist
	 * @param key the key of the configuration
	 * @param defaultValue default value if not exist the property
	 * @return double value
	 */
	public static double getProperty(String key, double defaultValue) {
		return properties.optDouble(key, defaultValue);
	}

	/**
	 * Get integer value with the key, or default value if not exist
	 * @param key the key of the configuration
	 * @param defaultValue default value if not exist the property
	 * @return integer value
	 */
	public static int getProperty(String key, int defaultValue) {
		return properties.optInt(key, defaultValue);
	}

	/**
	 * Get boolean value with the key, or default value if not exist
	 * @param key the key of the configuration
	 * @param defaultValue default value if not exist the property
	 * @return boolean value
	 */
	public static boolean getProperty(String key, boolean defaultValue) {
		return properties.optBoolean(key, defaultValue);
	}
	
	/**
	 * Set value for the key property
	 * @param key key
	 * @param value value to set
	 */
	public static void setProperty(String key, Object value) {
		properties.put(key, value);
	}
	
	/**
	 * Put collection as value for the key property
	 * @param key the key
	 * @param array array to save
	 */
	public static void setArray(String key, Collection<String> array) {
		properties.put(key, array);
	}

	/**
	 * Return array for the key, if not exist return empty JSONArray
	 * @param key the key
	 * @return the array for the key proprety if exists, otherwise empty array
	 */
	public static JSONArray getArray(String key) {
		JSONArray array = properties.optJSONArray(key);
		if (array == null) {
			JSONArray jsonArray = new JSONArray();
			properties.put(key, jsonArray);
			return jsonArray;
		}
		return array;
	}

	/**
	 * Add to the array the value, if not exist array for this key, it will create after append.
	 * @param key the key
	 * @param value value to add
	 */
	public static void appendArray(String key, Object value) {
		properties.append(key, value);
	}

	/**
	 * Check if has key in the configuration.
	 * @param key the key
	 * @return true if exists, otherwise false
	 */
	public static boolean has(String key) {
		return properties.has(key);
	}

	
	/**
	 * Save the file configuration in the default path
	 */
	public static void save() {
		save(path);
	}
	
	/**
	 * Save the file in the specified path
	 * @param path the path where save the configuration
	 */
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
