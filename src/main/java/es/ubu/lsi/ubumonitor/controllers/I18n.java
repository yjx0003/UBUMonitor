package es.ubu.lsi.ubumonitor.controllers;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.charts.ChartType;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.TypeTimes;
import es.ubu.lsi.ubumonitor.model.Component;
import es.ubu.lsi.ubumonitor.model.Event;
import es.ubu.lsi.ubumonitor.model.ModuleType;

/**
 * Clase encargada de traducir los elementos del resource bundle.
 * 
 * @author Yi Peng Ji
 *
 */
public class I18n {

	private static final Logger LOGGER = LoggerFactory.getLogger(I18n.class);
	private static ResourceBundle rb;

	/**
	 * Modifica el resource bundle
	 * 
	 * @param rb
	 *            el nuevo resource bundle
	 */
	public static void setResourceBundle(ResourceBundle rb) {
		I18n.rb = rb;
	}

	/**
	 * Devuelve el resource bundle.
	 * 
	 * @return el resource bundle actual
	 */
	public static ResourceBundle getResourceBundle() {
		return rb;
	}

	/**
	 * Devuelve la traducir a partir de la key
	 * 
	 * @param key
	 *            key
	 * @return el valor asociado a esa key, o la propia key si no existe el valor
	 */
	public static String get(String key) {

		return getOrDefault(key, key);
	}

	/**
	 * Devuelve la traduccion de un componente.
	 * 
	 * @param component
	 *            componente a traducir
	 * @return componente traducido
	 */
	public static String get(Component component) {

		return getOrDefault("log.component." + component, component.getName());
	}

	/**
	 * Traduce el evento del log
	 * 
	 * @param event
	 *            evento a traducir
	 * @return evento traducido
	 */
	public static String get(Event event) {
		return getOrDefault("log.event." + event, event.getName());
	}

	/**
	 * Traduce el tipo de modulo
	 * 
	 * @param moduleType
	 *            tipo de modulo
	 * @return la traduccion si existe, o el {@link ModuleType#toString()} si no
	 *         existe.
	 */
	public static String get(ModuleType moduleType) {
		return getOrDefault("module." + moduleType.getModName(), moduleType.getModName());
	}
	
	/**
	 * Traduce el tipo de tiempo
	 * @param typeTimes tipo de tiempo
	 * @return la traducción o {@link TypeTimes#toString()} si no existe
	 */
	public static String get(TypeTimes typeTimes) {
		return getOrDefault("choiceBox." + typeTimes, typeTimes.toString());
	}

	/**
	 * Devuelve el valor de la key o el de defecto si no existe en el resource
	 * bundle.
	 * 
	 * @param key
	 *            key
	 * @param defaultValue
	 *            valor por defecto
	 * @return el valor de la key o el valor por defecto
	 */
	private static String getOrDefault(String key, String defaultValue) {
		if (key !=null && rb.containsKey(key)) {
			return rb.getString(key);
		}
		LOGGER.warn("No existe entrada en el resource bundle la key: {}", key);
		return defaultValue;

	}

	private I18n() {
		throw new UnsupportedOperationException();
	}

	public static String get(ChartType category) {
		return getOrDefault(category.name(), category.name());
	}



}
