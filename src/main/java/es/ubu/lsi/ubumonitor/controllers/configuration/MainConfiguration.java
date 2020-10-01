package es.ubu.lsi.ubumonitor.controllers.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.controlsfx.control.PropertySheet;
import org.json.JSONArray;
import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.LastActivity;
import es.ubu.lsi.ubumonitor.model.LastActivityFactory;
import es.ubu.lsi.ubumonitor.model.Role;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.Charsets;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.MaskImage;
import es.ubu.lsi.ubumonitor.util.StopWord;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import es.ubu.lsi.ubumonitor.view.chart.VisNetwork;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class MainConfiguration {

	public static final String HORIZONTAL_MODE = "horizontalMode";
	private static final String VALUE = "value";
	public static final String GENERAL = "general";
	private Map<String, CustomPropertyItem> properties = new LinkedHashMap<>();
	private Set<String> categories = new HashSet<>();

	public MainConfiguration() {
		setDefaultValues();

	}

	public void setDefaultValues() {
		properties.clear();
		categories.clear();
		createItem(GENERAL, "alertDaysElapsed", 3);
		createItem(GENERAL, "charset", Charsets.UTF_8);
		createItem(GENERAL, "chartBackgroundColor", Color.web("#FFFFFF", 0.0));
		createItem(GENERAL, "cutGrade", 5.0);
		createItem(GENERAL, "limitLevelGradeItem", 1);
		createItem(GENERAL, "borderLength", 10);
		createItem(GENERAL, "borderSpace", 5);
		createItem(GENERAL, "legendActive", true);
		createItem(GENERAL, "generalActive", true);
		createItem(GENERAL, "groupActive", true);
		createItem(GENERAL, "listCharts", FXCollections.observableArrayList(ChartType.getNonDefaultValues()),
				ChartType.class);
		createItem(GENERAL, "initialRoles", FXCollections.observableArrayList(Controller.getInstance()
				.getActualCourse()
				.getStudentRole()), Role.class);
		createItem(GENERAL, "initialGroups", FXCollections.observableArrayList(new ArrayList<Group>()), Group.class);
		createItem(GENERAL, "initialLastActivity",
				FXCollections.observableArrayList(LastActivityFactory.DEFAULT.getAllLastActivity()),
				LastActivity.class);
		createItem(GENERAL, "initialTypeTimes", TypeTimes.YEAR_WEEK);

		createItem(GENERAL, "displayYScaleTitle", true);
		createItem(GENERAL, "displayXScaleTitle", true);
		createItem(GENERAL, "fontColorYScaleTitle", Color.BLACK);
		createItem(GENERAL, "fontColorXScaleTitle", Color.BLACK);

		createItem(ChartType.TOTAL_BAR, HORIZONTAL_MODE, false);
		createItem(ChartType.STACKED_BAR, "calculateMax", false);
		createItem(ChartType.HEAT_MAP, "calculateMax", true);
		createItem(ChartType.HEAT_MAP, "useQuartile", false);
		createItem(ChartType.HEAT_MAP, "zeroValue", Color.web("#f78880"));
		createItem(ChartType.HEAT_MAP, "firstInterval", Color.web("#f4e3ae"));
		createItem(ChartType.HEAT_MAP, "secondInterval", Color.web("#fff033"));
		createItem(ChartType.HEAT_MAP, "thirdInterval", Color.web("#b5ff33"));
		createItem(ChartType.HEAT_MAP, "fourthInterval", Color.web("#38e330"));
		createItem(ChartType.HEAT_MAP, "moreMax", Color.web("#67b92e"));
		createItem(ChartType.BOXPLOT_LOG, HORIZONTAL_MODE, true);
		createItem(ChartType.VIOLIN_LOG, HORIZONTAL_MODE, true);
		createItem(ChartType.BOXPLOT_LOG_TIME, HORIZONTAL_MODE, false);
		createItem(ChartType.VIOLIN_LOG_TIME, HORIZONTAL_MODE, false);
		createItem(ChartType.CUM_LINE, "calculateMax", false);

		createItem(ChartType.MEAN_DIFF, "calculateMax", false);
		createItem(ChartType.MEAN_DIFF, "zeroLineColor", Color.web("#DC143C"));
		createItem(ChartType.MEAN_DIFF, "zeroLineWidth", 3);

		createItem(ChartType.SESSION, "timeInterval", 60);

		createItem(ChartType.BOXPLOT, HORIZONTAL_MODE, false);

		createItem(ChartType.VIOLIN, HORIZONTAL_MODE, false);

		createItem(ChartType.GRADE_REPORT_TABLE, "failGradeColor", Color.web("#DC143C"));
		createItem(ChartType.GRADE_REPORT_TABLE, "passGradeColor", Color.web("#2DC214"));

		createItem(ChartType.CALIFICATION_BAR, HORIZONTAL_MODE, false);
		createItem(ChartType.CALIFICATION_BAR, "emptyGradeColor", Color.web("#D3D3D3", 0.3));
		createItem(ChartType.CALIFICATION_BAR, "failGradeColor", Color.web("#DC143C", 0.3));
		createItem(ChartType.CALIFICATION_BAR, "passGradeColor", Color.web("#2DC214", 0.3));

		createItem(ChartType.ACTIVITIES_TABLE, "firstInterval", Color.web("#f78880"));
		createItem(ChartType.ACTIVITIES_TABLE, "secondInterval", Color.web("#f4e3ae"));
		createItem(ChartType.ACTIVITIES_TABLE, "thirdInterval", Color.web("#fff033"));
		createItem(ChartType.ACTIVITIES_TABLE, "fourthInterval", Color.web("#b5ff33"));
		createItem(ChartType.ACTIVITIES_TABLE, "moreMax", Color.web("#38e330"));

		createItem(ChartType.BUBBLE, "limitDays", 14);
		createItem(ChartType.BUBBLE, "firstInterval", Color.web("#b5ff33"));
		createItem(ChartType.BUBBLE, "secondInterval", Color.web("#fff033"));
		createItem(ChartType.BUBBLE, "thirdInterval", Color.web("#f4e3ae"));
		createItem(ChartType.BUBBLE, "fourthInterval", Color.web("#f78880"));

		createItem(ChartType.BUBBLE_LOGARITHMIC, "firstInterval", Color.web("#b5ff33"));
		createItem(ChartType.BUBBLE_LOGARITHMIC, "secondInterval", Color.web("#fff033"));
		createItem(ChartType.BUBBLE_LOGARITHMIC, "thirdInterval", Color.web("#f4e3ae"));
		createItem(ChartType.BUBBLE_LOGARITHMIC, "fourthInterval", Color.web("#f78880"));

		createItem(ChartType.FORUM_BAR, HORIZONTAL_MODE, false);
		createItem(ChartType.FORUM_BAR, "tab.forum", Color.web("#efc9af", 0.3));

		createItem(ChartType.FORUM_USER_POST_BAR, "text.discussioncreation", Color.web("#efc9af", 0.3));
		createItem(ChartType.FORUM_USER_POST_BAR, "text.replies", Color.web("#104c91", 0.3));

		createItem(ChartType.FORUM_NETWORK, "showNonConnected", true);
		createItem(ChartType.FORUM_NETWORK, "usePhoto", true);
		createItem(ChartType.FORUM_NETWORK, "useInitialNames", true);
		createItem(ChartType.FORUM_NETWORK, "showNumberPosts", true);
		createItem(ChartType.FORUM_NETWORK, "physicsAfterDraw", true);
		createItem(ChartType.FORUM_NETWORK, "edges.dashes", false);
		createItem(ChartType.FORUM_NETWORK, "edges.arrows.to.scaleFactor", 0.75);
		createItem(ChartType.FORUM_NETWORK, "edges.scaling.min", 1);
		createItem(ChartType.FORUM_NETWORK, "edges.scaling.max", 10);
		createItem(ChartType.FORUM_NETWORK, "nodes.borderWidth", 1);
		createItem(ChartType.FORUM_NETWORK, "nodes.scaling.min", 20);
		createItem(ChartType.FORUM_NETWORK, "nodes.scaling.max", 40);
		createItem(ChartType.FORUM_NETWORK, "physics.solver", VisNetwork.Solver.FORCE_ATLAS_2_BASED);
		createItem(ChartType.FORUM_NETWORK, "physics.barnesHut.theta", 0.5);
		createItem(ChartType.FORUM_NETWORK, "physics.barnesHut.gravitationalConstant", -2000);
		createItem(ChartType.FORUM_NETWORK, "physics.barnesHut.centralGravity", 0.3);
		createItem(ChartType.FORUM_NETWORK, "physics.barnesHut.springLength", 95);
		createItem(ChartType.FORUM_NETWORK, "physics.barnesHut.springConstant", 0.04);
		createItem(ChartType.FORUM_NETWORK, "physics.barnesHut.damping", 0.09);
		createItem(ChartType.FORUM_NETWORK, "physics.barnesHut.avoidOverlap", 0.0);
		createItem(ChartType.FORUM_NETWORK, "physics.forceAtlas2Based.theta", 0.5);
		createItem(ChartType.FORUM_NETWORK, "physics.forceAtlas2Based.gravitationalConstant", -50);
		createItem(ChartType.FORUM_NETWORK, "physics.forceAtlas2Based.centralGravity", 0.01);
		createItem(ChartType.FORUM_NETWORK, "physics.forceAtlas2Based.springLength", 100);
		createItem(ChartType.FORUM_NETWORK, "physics.forceAtlas2Based.springConstant", 0.08);
		createItem(ChartType.FORUM_NETWORK, "physics.forceAtlas2Based.damping", 0.4);
		createItem(ChartType.FORUM_NETWORK, "physics.forceAtlas2Based.avoidOverlap", 0.0);
		createItem(ChartType.FORUM_NETWORK, "physics.repulsion.nodeDistance", 100);
		createItem(ChartType.FORUM_NETWORK, "physics.repulsion.centralGravity", 0.2);
		createItem(ChartType.FORUM_NETWORK, "physics.repulsion.springLength", 200);
		createItem(ChartType.FORUM_NETWORK, "physics.repulsion.springConstant", 0.05);
		createItem(ChartType.FORUM_NETWORK, "physics.repulsion.damping", 0.09);
		createItem(ChartType.FORUM_NETWORK, "interaction.keyboard", true);
		createItem(ChartType.FORUM_NETWORK, "interaction.multiselect", true);
		createItem(ChartType.FORUM_NETWORK, "interaction.navigationButtons", true);
		createItem(ChartType.FORUM_NETWORK, "interaction.tooltipDelay", 300);
		createItem(ChartType.FORUM_NETWORK, "layout.randomSeed", "");
		createItem(ChartType.FORUM_NETWORK, "layout.clusterThreshold", 150);

		createItem(ChartType.FORUM_POSTS, "usePhoto", true);
		createItem(ChartType.FORUM_POSTS, "colorContains", Color.web("#00FF00"));
		createItem(ChartType.FORUM_POSTS, "colorNotContains", Color.web("#FF0000"));
		createItem(ChartType.FORUM_POSTS, "nodes.borderWidth", 3);
		createItem(ChartType.FORUM_POSTS, "edges.width", 3);
		createItem(ChartType.FORUM_POSTS, "interaction.keyboard", true);
		createItem(ChartType.FORUM_POSTS, "interaction.multiselect", true);
		createItem(ChartType.FORUM_POSTS, "interaction.navigationButtons", true);
		createItem(ChartType.FORUM_POSTS, "interaction.tooltipDelay", 300);

		createItem(ChartType.FORUM_WORD_CLOUD, "stopWords", StopWord.getStopWordValues(Locale.getDefault()));
		createItem(ChartType.FORUM_WORD_CLOUD, "chartBackgroundColor", Color.web("#FFFFFF", 0.0));
		createItem(ChartType.FORUM_WORD_CLOUD, "wordFrequencesToReturn", 50);
		createItem(ChartType.FORUM_WORD_CLOUD, "padding", 2);
		createItem(ChartType.FORUM_WORD_CLOUD, "minWordLength", 3);
		createItem(ChartType.FORUM_WORD_CLOUD, "maxWordLength", 32);
		createItem(ChartType.FORUM_WORD_CLOUD, "minFont", 10);
		createItem(ChartType.FORUM_WORD_CLOUD, "maxFont", 40);
		createItem(ChartType.FORUM_WORD_CLOUD, "backGroundImage", MaskImage.RECTANGLE);

		createItem(ChartType.FORUM_USER_POST_BAR, HORIZONTAL_MODE, false);

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
					ids = roles.stream()
							.map(Role::getRoleId)
							.collect(Collectors.toList());
				} else if (property.clazz == Group.class) {
					ObservableList<Group> groups = (ObservableList<Group>) property.getValue();
					ids = groups.stream()
							.map(Group::getGroupId)
							.collect(Collectors.toList());
				} else if (property.clazz == LastActivity.class) {
					ObservableList<LastActivity> lastActivity = (ObservableList<LastActivity>) property.getValue();
					ids = lastActivity.stream()
							.map(LastActivity::getIndex)
							.collect(Collectors.toList());
				} else if (property.clazz == ChartType.class) {
					ObservableList<ChartType> chartTypes = (ObservableList<ChartType>) property.getValue();
					ids = ChartType.getNonDefaultValues()
							.stream()
							.filter(e -> !chartTypes.contains(e))
							.map(ChartType::getId)
							.collect(Collectors.toList());

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

	public void createItem(String category, String name, Object value, Class<?> clazz, Tabs tab) {
		String key = convertToKey(category, name);
		if (!categories.contains(category)) {
			categories.add(category);
		}

		properties.put(key, new CustomPropertyItem(categories.size(), category, name, value, clazz, tab));

	}

	public void overrideItem(String category, String name, Object value) {
		overrideItem(category, name, value, value.getClass());
	}

	public void overrideItem(String category, String name, Object value, Class<?> clazz) {
		CustomPropertyItem property = properties.get(convertToKey(category, name));

		if (property != null && (property.getValue() instanceof ObservableList || property.getValue()
				.getClass()
				.equals(clazz))) {
			property.setValue(value);
			property.setClass(clazz);

		}

	}

	public void createItem(ChartType category, String name, Object value) {

		createItem(category.name(), name, value, value.getClass(), category.getTab());
	}

	public void createItem(String category, String name, Object value) {
		createItem(category, name, value, value.getClass(), null);

	}

	public void createItem(String category, String name, Object value, Class<?> clazz) {
		createItem(category, name, value, clazz, null);

	}

	public void createItem(String category, String name, Object value, Tabs tab) {
		createItem(category, name, value, value.getClass(), tab);

	}

	public <T> void setValue(String category, String name, T value) {
		properties.get(convertToKey(category, name))
				.setValue(value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String category, String name) {
		return (T) properties.get(convertToKey(category, name))
				.getValue();

	}

	private String convertToKey(String category, String name) {
		return category + "." + name;
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(ChartType category, String name) {
		return (T) properties.get(category.name() + "." + name)
				.getValue();

	}

	public <T> T getValue(String category, String name, T defaultValue) {
		if (properties.containsKey(convertToKey(category, name))) {
			return getValue(category, name);
		}
		return defaultValue;
	}

	public <T> T getValue(ChartType category, String name, T defaultValue) {
		return getValue(category.name(), name, defaultValue);
	}

	public Collection<CustomPropertyItem> getProperties(Tabs tab) {

		return properties.values()
				.stream()
				.filter(p -> p.tab == tab)
				.collect(Collectors.toList());
	}

	public class CustomPropertyItem implements PropertySheet.Item {

		private Tabs tab;
		private int order;
		private String name;
		private Object value;
		private Class<?> clazz;
		private String category;

		public CustomPropertyItem(int order, String category, String name, Object value, Class<?> clazz, Tabs tab) {
			this.order = order;
			this.category = category;
			this.name = name;
			this.value = value;
			this.clazz = clazz;
			this.tab = tab;
		}

		public void setClass(Class<?> clazz) {
			this.clazz = clazz;

		}

		@Override
		public Class<?> getType() {
			return clazz;
		}

		@Override
		public String getCategory() {
			return String.format("%2d. %s", order, I18n.get(category));
		}

		@Override
		public String getName() {
			return I18n.get(name);
		}

		@Override
		public String getDescription() {
			return getName();
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public void setValue(Object value) {

			this.value = value;

		}

		public int getOrder() {
			return order;
		}

		@Override
		public Optional<ObservableValue<? extends Object>> getObservableValue() {
			return Optional.empty();
		}

		public Tabs getTab() {

			return tab;
		}

	}

}