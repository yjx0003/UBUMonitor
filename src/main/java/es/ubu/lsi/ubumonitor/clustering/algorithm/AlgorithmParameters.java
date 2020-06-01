package es.ubu.lsi.ubumonitor.clustering.algorithm;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.controlsfx.control.PropertySheet.Item;

import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.util.SimplePropertySheetItem;

public class AlgorithmParameters {

	private Map<ClusteringParameter, Item> parameters = new LinkedHashMap<>();

	public void addParameter(ClusteringParameter parameter, Object value) {
		Item item = new SimplePropertySheetItem(parameter.getName(), value, parameter.getDescription());
		parameters.put(parameter, item);
	}

	public void setParameter(ClusteringParameter parameter, Object value) {
		parameters.get(parameter).setValue(value);
	}

	@SuppressWarnings("unchecked")
	public <V> V getValue(ClusteringParameter parameter) {
		Item item = parameters.get(parameter);
		return item == null ? null : (V) item.getValue();
	}
	
	public Collection<Item> getPropertyItems() {
		return parameters.values();
	}

	@Override
	public String toString() {
		return parameters.toString();
	}
}
