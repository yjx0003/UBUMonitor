package es.ubu.lsi.ubumonitor.clustering.algorithm;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.controlsfx.control.PropertySheet.Item;

import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.util.SimplePropertySheetItem;

/**
 * Clase que contiene los parámetros y sus valores de un algortimo.
 * 
 * @author Xing Long Ji
 *
 */
public class AlgorithmParameters {

	private Map<ClusteringParameter, Item> parameters = new LinkedHashMap<>();

	/**
	 * Añade un nuevo parámetro.
	 * 
	 * @param parameter parámetro
	 * @param value     valor inicial
	 */
	public void addParameter(ClusteringParameter parameter, Object value) {
		Item item = new SimplePropertySheetItem(parameter.getName(), value, parameter.getDescription());
		parameters.put(parameter, item);
	}

	/**
	 * Establece un parámetro a un valor concreto.
	 * 
	 * @param parameter parámetro
	 * @param value     nuevo valor
	 */
	public void setParameter(ClusteringParameter parameter, Object value) {
		parameters.get(parameter).setValue(value);
	}

	/**
	 * Devulve el valor actual del parámetro.
	 * 
	 * @param <V>       tipo de dato del parametro
	 * @param parameter parámetro
	 * @return el valor del parametro o null si no existe
	 */
	@SuppressWarnings("unchecked")
	public <V> V getValue(ClusteringParameter parameter) {
		Item item = parameters.get(parameter);
		return item == null ? null : (V) item.getValue();
	}

	/**
	 * Devulve una colección con los parametros como Items.
	 * 
	 * @return una colección de items
	 */
	public Collection<Item> getPropertyItems() {
		return parameters.values();
	}

	@Override
	public String toString() {
		return parameters.toString();
	}
}
