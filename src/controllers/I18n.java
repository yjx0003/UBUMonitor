package controllers;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Component;
import model.Event;

public class I18n {

	static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private static ResourceBundle rb;

	public static void setResourceBundle(ResourceBundle rb) {
		I18n.rb = rb;
	}

	public static ResourceBundle getResourceBundle() {
		return rb;
	}

	public static String get(String key) {

		return getOrDefault(key, key);
	}

	public static String get(Component component) {

		return getOrDefault("component." + component, component.getName());
	}

	public static String get(Event event) {
		return getOrDefault("eventname." + event, event.getName());
	}

	private static String getOrDefault(String key, String defaultValue) {
		if (rb.containsKey(key)) {
			return rb.getString(key);
		}
		logger.error("No existe entrada en el resource bundle la key: " + key);
		return defaultValue;
	}
}
