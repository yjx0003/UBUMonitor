package controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.controlsfx.control.PropertySheet;

import controllers.charts.ChartType;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;

public class MainConfiguration {
	private Map<String, Object> map = new HashMap<>();
	private List<CustomPropertyItem> properties = new ArrayList<>();
	private List<String> categoriesOrder = new ArrayList<>();

	public MainConfiguration() {
		createItem("General", "Cutoff", 5.0);
		createItem(ChartType.HEAT_MAP, "Calculate max", false);
		createItem(ChartType.HEAT_MAP, "Zero Value", Color.web("#f78880")); // Creates a CheckBox
		createItem(ChartType.HEAT_MAP, "First Interval", Color.web("#f4e3ae"));
		createItem(ChartType.HEAT_MAP, "Second Interval", Color.web("#fff033"));
		createItem(ChartType.HEAT_MAP, "Third Interval", Color.web("#b5ff33"));
		createItem(ChartType.HEAT_MAP, "Fourth Interval", Color.web("#38e330"));
		createItem(ChartType.HEAT_MAP, "More than max", Color.web("#67b92e"));
		createItem(ChartType.BOXPLOT, "Horizontal mode", false);
		
		createItem(ChartType.VIOLIN, "Horizontal mode", false);
		
	}

	private void createItem(String category, String name, Object value) {
		String key = category + "." + name;
		if (!categoriesOrder.contains(category)) {
			categoriesOrder.add(category);
		}
		properties.add(new CustomPropertyItem(key, category, name));
		map.put(key, value);

	}

	private void createItem(ChartType category, String name, Object value) {
		createItem(I18n.get(category), name, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String category, String name) {
		return (T) map.get(category + "." + name);

	}

	public List<CustomPropertyItem> getProperties() {
		return properties;
	}

	private class CustomPropertyItem implements PropertySheet.Item, Serializable {

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
			return categoriesOrder.indexOf(category) + 1 + ". " + category;
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