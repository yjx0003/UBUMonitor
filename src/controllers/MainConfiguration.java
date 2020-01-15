package controllers;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.controlsfx.control.PropertySheet;

import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;

public class MainConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, Object> map = new LinkedHashMap<>();
	private List<CustomPropertyItem> properties = new ArrayList<>();

	public MainConfiguration() {
		createItem("basic", "My Text", "Same text"); // Creates a TextField in property sheet
		createItem("basic", "My Date", LocalDate.of(2016, Month.JANUARY, 1)); // Creates a DatePicker
		createItem("misc", "My Boolean", false); // Creates a CheckBox
		createItem("misc", "My Number", 500); // Creates a NumericField
		createItem("misc", "My Color", Color.ALICEBLUE); // Creates a ColorPicker
	}

	private void createItem(String category, String name, Object value) {
		String key = category + "." + name;
		properties.add(new CustomPropertyItem(key, category, name));
		map.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String category, String name) {
		return (T) map.get(category + "." + name);

	}

	public List<CustomPropertyItem> getProperties() {
		return properties;
	}

	private class CustomPropertyItem implements PropertySheet.Item, Serializable{

		private static final long serialVersionUID = 1L;
		private String key;
		private String category, name;

		public CustomPropertyItem(String key, String category, String name) {
			this.key = key;
			this.category = category;
			this.name = name;
		}

		@Override
		public Class<?> getType() {
			return map.get(key).getClass();
		}

		@Override
		public String getCategory() {
			return category;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getDescription() {
			// doesn't really fit into the map
			return null;
		}

		@Override
		public Object getValue() {
			return map.get(key);
		}

		@Override
		public void setValue(Object value) {
			map.put(key, value);
		}

		@Override
		public Optional<ObservableValue<? extends Object>> getObservableValue() {
			return Optional.empty();
		}

	}
}