package controllers;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Component;
import model.Event;

/**
 * Clase encargada de traducir los elementos del resource bundle.
 * @author Yi Peng Ji
 *
 */
public class I18n {

	static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private static ResourceBundle rb;

	/**
	 * Modifica el resource bundle
	 * @param rb el nuevo resource bundle
	 */
	public static void setResourceBundle(ResourceBundle rb) {
		I18n.rb = rb;
	}

	/**
	 * Devuelve el resource bundle.
	 * @return el resource bundle actual
	 */
	public static ResourceBundle getResourceBundle() {
		return rb;
	}

	/**
	 * Devuelve la traducir a partir de la key
	 * @param key key
	 * @return el valor asociado a esa key, o la propia key si no existe el valor
	 */
	public static String get(String key) {

		return getOrDefault(key, key);
	}

	/**
	 * Devuelve la traduccion de un componente.
	 * @param component componente a traducir
	 * @return componente traducido
	 */
	public static String get(Component component) {

		return getOrDefault("component." + component, component.getName());
	}

	/**
	 * Traduce el evento del log
	 * @param event evento a traducir
	 * @return evento traducido
	 */
	public static String get(Event event) {
		return getOrDefault("eventname." + event, event.getName());
	}

	/**
	 * Devuelve el valor de la key o el de defecto si no existe en el resource bundle.
	 * @param key key
	 * @param defaultValue valor por defecto
	 * @return el valor de la key o el valor por defecto
	 */
	private static String getOrDefault(String key, String defaultValue) {
		if (rb.containsKey(key)) {
			return rb.getString(key);
		}
		logger.error("No existe entrada en el resource bundle la key: " + key);
		return defaultValue;
	}
}
