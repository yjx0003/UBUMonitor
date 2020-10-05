package es.ubu.lsi.ubumonitor.controllers.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.LastActivity;
import es.ubu.lsi.ubumonitor.model.LastActivityFactory;
import es.ubu.lsi.ubumonitor.model.Role;
import es.ubu.lsi.ubumonitor.model.SubDataBase;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.MaskImage;
import es.ubu.lsi.ubumonitor.util.StopWord;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;

public class ConfigurationConsumer {

	private static final String NAME = "name";
	private static final String CATEGORY = "category";
	private static final String VALUE = "value";
	private static final Map<String, BiConsumer<MainConfiguration, JSONObject>> CONSUMER_MAP = new HashMap<>();
	static {
		CONSUMER_MAP.put(Role.class.toString(), ConfigurationConsumer::manageRole);
		CONSUMER_MAP.put(Group.class.toString(), ConfigurationConsumer::manageGroup);
		CONSUMER_MAP.put(LastActivity.class.toString(), ConfigurationConsumer::manageLastActivity);
		CONSUMER_MAP.put(Color.class.toString(), ConfigurationConsumer::manageColor);
		CONSUMER_MAP.put(Double.class.toString(), ConfigurationConsumer::manageDouble);
		CONSUMER_MAP.put(Integer.class.toString(), ConfigurationConsumer::manageInteger);
		CONSUMER_MAP.put(Boolean.class.toString(), ConfigurationConsumer::manageBoolean);
		CONSUMER_MAP.put(String.class.toString(), ConfigurationConsumer::manageString);
		CONSUMER_MAP.put(TypeTimes.class.toString(), ConfigurationConsumer::manageTypeTimes);
		CONSUMER_MAP.put(ChartType.class.toString(), ConfigurationConsumer::manageChartType);
		CONSUMER_MAP.put(StopWord.class.toString(), ConfigurationConsumer::manageStopWordsLanguage);
		CONSUMER_MAP.put(MaskImage.class.toString(), ConfigurationConsumer::manageMaskImage);
	}

	public static void consume(MainConfiguration mainConfiguration, JSONObject jsonObject) {

		CONSUMER_MAP.getOrDefault(jsonObject.getString("class"), ConfigurationConsumer::manageDefault)
				.accept(mainConfiguration, jsonObject);

	}

	private static void manageDefault(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		// do nothing
	}

	private static <E extends Enum<E>> void manageEnum(MainConfiguration mainConfiguration, JSONObject jsonObject,
			Class<E> clazz, E defaultValue) {
		E value;
		try {
			value = Enum.valueOf(clazz, jsonObject.getString(VALUE));

		} catch (IllegalArgumentException e) {
			value = defaultValue;
		}
		mainConfiguration.overrideItem(jsonObject.getString(CATEGORY), jsonObject.getString(NAME), value);

	}

	private static void manageStopWordsLanguage(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		manageEnum(mainConfiguration, jsonObject, StopWord.class, StopWord.ENGLISH);
	}

	private static void manageMaskImage(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		manageEnum(mainConfiguration, jsonObject, MaskImage.class, MaskImage.RECTANGLE);
	}

	private static void manageTypeTimes(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		manageEnum(mainConfiguration, jsonObject, TypeTimes.class, TypeTimes.YEAR_WEEK);
	}

	private static void manageString(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		mainConfiguration.overrideItem(jsonObject.getString(CATEGORY), jsonObject.getString(NAME),
				jsonObject.getString(VALUE));
	}

	private static void manageBoolean(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		mainConfiguration.overrideItem(jsonObject.getString(CATEGORY), jsonObject.getString(NAME),
				jsonObject.getBoolean(VALUE));

	}

	private static void manageInteger(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		mainConfiguration.overrideItem(jsonObject.getString(CATEGORY), jsonObject.getString(NAME),
				jsonObject.getInt(VALUE));

	}

	private static void manageDouble(MainConfiguration mainConfiguration, JSONObject jsonObject) {

		mainConfiguration.overrideItem(jsonObject.getString(CATEGORY), jsonObject.getString(NAME),
				jsonObject.getDouble(VALUE));
	}

	private static void manageColor(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		JSONArray colorArray = jsonObject.getJSONArray(VALUE);
		Color color = Color.color(colorArray.getDouble(0), colorArray.getDouble(1), colorArray.getDouble(2),
				colorArray.getDouble(3));
		mainConfiguration.overrideItem(jsonObject.getString(CATEGORY), jsonObject.getString(NAME), color);
	}

	private static void manageLastActivity(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		JSONArray jsonArray = jsonObject.getJSONArray(VALUE);
		List<LastActivity> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(LastActivityFactory.DEFAULT.getActivity(jsonArray.getInt(i)));
		}
		mainConfiguration.overrideItem(jsonObject.getString(CATEGORY), jsonObject.getString(NAME),
				FXCollections.observableList(list), LastActivity.class);
	}

	private static void manageGroup(MainConfiguration mainConfiguration, JSONObject jsonObject) {

		SubDataBase<Group> groupsDB = Controller.getInstance()
				.getDataBase()
				.getGroups();
		JSONArray groupsArray = jsonObject.getJSONArray(VALUE);
		List<Group> groups = new ArrayList<>();
		for (int i = 0; i < groupsArray.length(); i++) {
			groups.add(groupsDB.getById(groupsArray.getInt(i)));
		}
		mainConfiguration.overrideItem(jsonObject.getString(CATEGORY), jsonObject.getString(NAME),
				FXCollections.observableList(groups), Group.class);

	}

	private static void manageRole(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		SubDataBase<Role> rolesDB = Controller.getInstance()
				.getDataBase()
				.getRoles();
		JSONArray rolesArray = jsonObject.getJSONArray(VALUE);
		List<Role> roles = new ArrayList<>();
		for (int i = 0; i < rolesArray.length(); i++) {
			roles.add(rolesDB.getById(rolesArray.getInt(i)));
		}
		mainConfiguration.overrideItem(jsonObject.getString(CATEGORY), jsonObject.getString(NAME),
				FXCollections.observableList(roles), Role.class);
	}

	private static void manageChartType(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		JSONArray jsonArray = jsonObject.getJSONArray(VALUE);
		List<ChartType> hiddenCharts = new ArrayList<>();
		List<ChartType> listCharts = new ArrayList<>(ChartType.getNonDefaultValues());

		for (int i = 0; i < jsonArray.length(); i++) {
			hiddenCharts.add(ChartType.getById(jsonArray.getInt(i)));
		}
		listCharts.removeAll(hiddenCharts);
		mainConfiguration.overrideItem(jsonObject.getString(CATEGORY), jsonObject.getString(NAME),
				FXCollections.observableList(listCharts), ChartType.class);
	}

	private ConfigurationConsumer() {
		throw new UnsupportedOperationException();
	}

}
