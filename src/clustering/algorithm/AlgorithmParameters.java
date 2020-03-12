package clustering.algorithm;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.controlsfx.control.PropertySheet;

import clustering.util.SimplePropertySheetItem;

public class AlgorithmParameters {

	private Map<String, PropertySheet.Item> parameters;

	public AlgorithmParameters() {
		parameters = new LinkedHashMap<>();
	}

	public void addParameter(String name, Object value, String tooltip) {
		SimplePropertySheetItem c = new SimplePropertySheetItem(name, value, tooltip);
		parameters.put(name, c);
	}

	public Collection<PropertySheet.Item> getPropertyItems() {
		return parameters.values();
	}

	@SuppressWarnings("unchecked")
	public <V> V getValue(String name) {
		return (V) parameters.get(name).getValue();
	}

	@Override
	public String toString() {
		return parameters.toString();
	}
}
