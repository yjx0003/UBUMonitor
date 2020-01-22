package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.scene.paint.Color;
import model.Group;
import model.LastActivity;
import model.LastActivityFactory;
import model.Role;
import model.SubDataBase;

public class ConfigurationConsumer {

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
	}

	public static void consume(MainConfiguration mainConfiguration, JSONObject jsonObject) {

		CONSUMER_MAP.getOrDefault(jsonObject.getString("class"), ConfigurationConsumer::manageDefault)
				.accept(mainConfiguration, jsonObject);

	}

	private static void manageDefault(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		//do nothing
	}

	private static void manageString(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		mainConfiguration.overrideItem(jsonObject.getString("category"), jsonObject.getString("name"),
				jsonObject.getString("value"));
	}

	private static void manageBoolean(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		mainConfiguration.overrideItem(jsonObject.getString("category"), jsonObject.getString("name"),
				jsonObject.getBoolean("value"));

	}

	private static void manageInteger(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		mainConfiguration.overrideItem(jsonObject.getString("category"), jsonObject.getString("name"),
				jsonObject.getInt("value"));

	}

	private static void manageDouble(MainConfiguration mainConfiguration, JSONObject jsonObject) {

		mainConfiguration.overrideItem(jsonObject.getString("category"), jsonObject.getString("name"),
				jsonObject.getDouble("value"));
	}

	private static void manageColor(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		JSONArray colorArray = jsonObject.getJSONArray("value");
		Color color = Color.color(colorArray.getDouble(0), colorArray.getDouble(1), colorArray.getDouble(2),
				colorArray.getDouble(3));
		mainConfiguration.overrideItem(jsonObject.getString("category"), jsonObject.getString("name"), color);
	}

	private static void manageLastActivity(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		JSONArray jsonArray = jsonObject.getJSONArray("value");
		List<LastActivity> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(LastActivityFactory.getActivity(jsonArray.getInt(i)));
		}
		mainConfiguration.overrideItem(jsonObject.getString("category"), jsonObject.getString("name"),
				FXCollections.observableList(list), LastActivity.class);
	}

	private static void manageGroup(MainConfiguration mainConfiguration, JSONObject jsonObject) {

		SubDataBase<Group> groupsDB = Controller.getInstance().getDataBase().getGroups();
		JSONArray groupsArray = jsonObject.getJSONArray("value");
		List<Group> groups = new ArrayList<>();
		for (int i = 0; i < groupsArray.length(); i++) {
			groups.add(groupsDB.getById(groupsArray.getInt(i)));
		}
		mainConfiguration.overrideItem(jsonObject.getString("category"), jsonObject.getString("name"),
				FXCollections.observableList(groups), Group.class);

	}

	private static void manageRole(MainConfiguration mainConfiguration, JSONObject jsonObject) {
		SubDataBase<Role> rolesDB = Controller.getInstance().getDataBase().getRoles();
		JSONArray rolesArray = jsonObject.getJSONArray("value");
		List<Role> roles = new ArrayList<>();
		for (int i = 0; i < rolesArray.length(); i++) {
			roles.add(rolesDB.getById(rolesArray.getInt(i)));
		}
		mainConfiguration.overrideItem(jsonObject.getString("category"), jsonObject.getString("name"),
				FXCollections.observableList(roles), Role.class);
	}

}
