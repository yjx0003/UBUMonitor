package controllers.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.controlsfx.control.PropertySheet;
import org.json.JSONArray;
import org.json.JSONObject;

import controllers.Controller;
import controllers.I18n;
import controllers.charts.ChartType;
import controllers.ubulogs.TypeTimes;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import model.Group;
import model.LastActivity;
import model.LastActivityFactory;
import model.Role;

public class MainConfiguration {

	private static final String VALUE = "value";
	public static final String GENERAL = "general";
	private Map<String, Object> map = new LinkedHashMap<>();
	private Map<String, CustomPropertyItem> properties = new LinkedHashMap<>();
	private Map<String, Integer> categoriesOrder = new HashMap<>();

	public MainConfiguration() {
		setDefaultValues();

	}

	public void setDefaultValues() {
		
		
		createItem(GENERAL, "cutGrade", 5.0);
		createItem(GENERAL, "borderLength", 10);
		createItem(GENERAL, "borderSpace", 5);
		createItem(GENERAL, "legendActive", true);
		createItem(GENERAL, "generalActive", true);
		createItem(GENERAL, "groupActive", true);
		createItem(GENERAL, "initialRoles",
				FXCollections.observableArrayList(Controller.getInstance().getActualCourse().getStudentRole()),
				Role.class);
		createItem(GENERAL, "initialGroups", FXCollections.observableArrayList(new ArrayList<Group>()), Group.class);
		createItem(GENERAL, "initialLastActivity",
				FXCollections.observableArrayList(LastActivityFactory.getAllLastActivity()), LastActivity.class);
		createItem(GENERAL, "initialTypeTimes", TypeTimes.YEAR_WEEK);
		
		createItem(GENERAL, "displayYScaleTitle", true);
		createItem(GENERAL, "displayXScaleTitle", true);
		createItem(GENERAL, "fontColorYScaleTitle", Color.BLACK);
		createItem(GENERAL, "fontColorXScaleTitle", Color.BLACK);
		
		createItem(ChartType.STACKED_BAR, "calculateMax", false);
		createItem(ChartType.HEAT_MAP, "calculateMax", false);
		createItem(ChartType.HEAT_MAP, "zeroValue", Color.web("#f78880"));
		createItem(ChartType.HEAT_MAP, "firstInterval", Color.web("#f4e3ae"));
		createItem(ChartType.HEAT_MAP, "secondInterval", Color.web("#fff033"));
		createItem(ChartType.HEAT_MAP, "thirdInterval", Color.web("#b5ff33"));
		createItem(ChartType.HEAT_MAP, "fourthInterval", Color.web("#38e330"));
		createItem(ChartType.HEAT_MAP, "moreMax", Color.web("#67b92e"));

		createItem(ChartType.CUM_LINE, "calculateMax", false);

		createItem(ChartType.MEAN_DIFF, "calculateMax", false);
		createItem(ChartType.MEAN_DIFF, "zeroLineColor", Color.web("#DC143C"));
		createItem(ChartType.MEAN_DIFF, "zeroLineWidth", 3);

		createItem(ChartType.BOXPLOT, "horizontalMode", false);
		createItem(ChartType.BOXPLOT, "tooltipDecimals", 2);
		createItem(ChartType.VIOLIN, "horizontalMode", false);
		createItem(ChartType.VIOLIN, "tooltipDecimals", 2);

		createItem(ChartType.GRADE_REPORT_TABLE, "failGradeColor", Color.web("#DC143C"));
		createItem(ChartType.GRADE_REPORT_TABLE, "passGradeColor", Color.web("#2DC214"));
		createItem(ChartType.CALIFICATION_BAR, "emptyGradeColor", Color.web("#D3D3D3", 0.3));
		createItem(ChartType.CALIFICATION_BAR, "failGradeColor", Color.web("#DC143C", 0.3));
		createItem(ChartType.CALIFICATION_BAR, "passGradeColor", Color.web("#2DC214", 0.3));
	}

	@SuppressWarnings("unchecked")
	public String toJson() {
		JSONArray jsonArray = new JSONArray();
		for (CustomPropertyItem property : properties.values()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("category", property.category);
			jsonObject.put("name", property.name);
			jsonObject.put("class", property.clazz);
			if (property.getValue() instanceof ObservableList<?>) {
				List<Integer> ids = Collections.emptyList();
				if (property.clazz == Role.class) {
					ObservableList<Role> roles = (ObservableList<Role>) property.getValue();
					ids = roles.stream().map(Role::getRoleId).collect(Collectors.toList());
				} else if (property.clazz == Group.class) {
					ObservableList<Group> groups = (ObservableList<Group>) property.getValue();
					ids = groups.stream().map(Group::getGroupId).collect(Collectors.toList());
				} else if (property.clazz == LastActivity.class) {
					ObservableList<LastActivity> lastActivity = (ObservableList<LastActivity>) property.getValue();
					ids = lastActivity.stream().map(LastActivity::getIndex).collect(Collectors.toList());
				}
				jsonObject.put(VALUE, ids);
			} else if (property.getValue() instanceof Color) {

				Color color = (Color) property.getValue();

				jsonObject.put(VALUE,
						Arrays.asList(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity()));
			} else {
				jsonObject.put(VALUE, property.getValue());
			}

			jsonArray.put(jsonObject);
		}

		return jsonArray.toString();
	}

	public void fromJson(String properties) {
		JSONArray jsonArray = new JSONArray(properties);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			ConfigurationConsumer.consume(this, jsonObject);

		}
	}

	public void createItem(String category, String name, Object value, Class<?> clazz) {
		String key = category + "." + name;
		if (!categoriesOrder.containsKey(category)) {
			categoriesOrder.put(category, categoriesOrder.size() + 1);
		}
		properties.put(key, new CustomPropertyItem(key, category, name, clazz));
		map.put(key, value);

	}

	public void overrideItem(String category, String name, Object value) {
		overrideItem(category, name, value, value.getClass());
	}
	
	public void overrideItem(String category, String name, Object value, Class<?> clazz) {
		if (map.containsKey(category + "." + name)) {
			createItem(category, name, value, clazz);
		}
	}

	public void createItem(ChartType category, String name, Object value) {
		createItem(category.name(), name, value);
	}

	public void createItem(String category, String name, Object value) {
		createItem(category, name, value, value.getClass());

	}

	public <T> void setValue(String category, String name, T value) {
		map.put(category + "." + name, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String category, String name) {
		return (T) map.get(category + "." + name);

	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(ChartType category, String name) {
		return (T) map.get(category.name() + "." + name);

	}

	public <T> T getValue(String category, String name, T defaultValue) {
		if (map.containsKey(category + "." + name)) {
			return getValue(category, name);
		}
		return defaultValue;
	}

	public <T> T getValue(ChartType category, String name, T defaultValue) {
		return getValue(category.name(), name, defaultValue);
	}

	public Collection<CustomPropertyItem> getProperties() {
		return properties.values();
	}

	private class CustomPropertyItem implements PropertySheet.Item {

		private String key;
		private String category; 
		String name;
		private Class<?> clazz;

		public CustomPropertyItem(String key, String category, String name, Class<?> clazz) {
			this.key = key;
			this.category = category;
			this.name = name;
			this.clazz = clazz;
		}

		@Override
		public Class<?> getType() {
			return clazz;
		}

		@Override
		public String getCategory() {
			return categoriesOrder.get(category) + ". " + I18n.get(category);
		}

		@Override
		public String getName() {
			return I18n.get(name);
		}

		@Override
		public String getDescription() {
			// TODO
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

	public Map<String, Object> getMap() {
		return map;
	}

}