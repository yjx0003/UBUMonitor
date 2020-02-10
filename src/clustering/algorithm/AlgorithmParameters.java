package clustering.algorithm;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.controlsfx.control.PropertySheet;

import controllers.I18n;
import javafx.beans.value.ObservableValue;

public class AlgorithmParameters {

	private Map<String, PropertySheet.Item> parameters;

	public AlgorithmParameters() {
		parameters = new LinkedHashMap<>();
	}

	public void addParameter(String name, Object value, String tooltip) {
		ParameterItem c = new ParameterItem(name, value, tooltip);
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

	private static class ParameterItem implements PropertySheet.Item {

		private String name;
		private Object value;
		private String tooltip;

		public ParameterItem(String name, Object value, String tooltip) {
			this.name = name;
			this.value = value;
			this.tooltip = tooltip;
		}

		@Override
		public String getCategory() {
			return null;
		}

		@Override
		public String getName() {
			return I18n.get(name);
		}

		@Override
		public String getDescription() {
			return I18n.get(tooltip);
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public void setValue(Object value) {
			this.value = value;
		}

		@Override
		public Optional<ObservableValue<? extends Object>> getObservableValue() {
			return Optional.empty();
		}

		@Override
		public Class<?> getType() {
			return value.getClass();
		}

		@Override
		public String toString() {
			return name + "=" + value;
		}
	}
}
