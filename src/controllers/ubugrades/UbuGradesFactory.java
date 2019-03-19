package controllers.ubugrades;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Course;
import model.DescriptionFormat;
import model.EnrolledUser;
import model.Group;
import model.Role;
import model.mod.Module;
import model.mod.ModuleType;

public class UbuGradesFactory {

	



	public static List<Role> createRoles(JSONArray jsonArray) {

		if (jsonArray == null)
			return null;

		List<Role> roles = new ArrayList<Role>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			roles.add(createRole(jsonObject));
		}
		return roles;
	}

	public static Role createRole(JSONObject jsonObject) {

		if (jsonObject == null)
			return null;

		int roleid = jsonObject.getInt("roleid");

		if (GettersUBUGrades.roles.containsKey(roleid)) {
			return GettersUBUGrades.roles.get(roleid);
		}
		Role role = new Role(jsonObject.getInt("roleid"), jsonObject.getString("name"),
				jsonObject.getString("shortname"));
		GettersUBUGrades.roles.put(roleid, role);
		return role;

	}



	public static List<Group> createGroups(JSONArray jsonArray) {

		if (jsonArray == null)
			return null;

		List<Group> groups = new ArrayList<Group>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			groups.add(createGroup(jsonObject));
		}
		return groups;
	}

	public static Group createGroup(JSONObject jsonObject) {

		if (jsonObject == null)
			return null;

		int groupid = jsonObject.getInt("id");

		if (GettersUBUGrades.groups.containsKey(groupid)) {
			return GettersUBUGrades.groups.get(groupid);
		}

		String name = jsonObject.getString("name");
		String description = jsonObject.getString("description");
		DescriptionFormat descriptionFormat = DescriptionFormat.get(jsonObject.getInt("descriptionFormat"));

		Group group = new Group(groupid, name, description, descriptionFormat);

		GettersUBUGrades.groups.put(groupid, group);
		
		
		
		return group;

	}

	public static Module createModule(JSONObject jsonObject) {

		if (jsonObject == null)
			return null;

		int cmid = jsonObject.getInt("id");

		if (GettersUBUGrades.modules.containsKey(cmid)) {

			return GettersUBUGrades.modules.get(cmid);
		}
		String modname = jsonObject.getString("modname");

		Module module = ModuleType.createInstance(modname);

		attributesModule(jsonObject, module);
		
		GettersUBUGrades.modules.put(cmid, module);

		return module;

	}

	public static void attributesModule(JSONObject jsonObject, Module module) {

		module.setId(jsonObject.getInt("id"));
		module.setUrl(jsonObject.optString("url"));
		module.setName(jsonObject.optString("name"));
		module.setInstance(jsonObject.optInt("instance"));
		module.setDescription(jsonObject.optString("description"));
		module.setVisible(jsonObject.optInt("visible"));
		module.setUservisible(jsonObject.optBoolean("uservisible"));
		module.setVisibleoncoursepage(jsonObject.optInt("visibleoncoursepage"));
		module.setModicon(jsonObject.optString("modicon"));
		module.setModname(jsonObject.getString("modname"));
		module.setIndent(jsonObject.optInt("indent"));
	}

	public static EnrolledUser createEnrolledUser(JSONObject user) {

		int id = user.getInt("id");
		if (GettersUBUGrades.users.containsKey(id)) {
			return GettersUBUGrades.users.get(id);
		}

		EnrolledUser enrolledUser = EnrolledUser.newBuilder()
				.setId(id)
				.setFirstname(user.optString("firstname"))
				.setLastname(user.optString("lastname"))
				.setFullname(user.optString("fullname"))
				.setFirstaccess(user.optInt("firstaccess"))
				.setLastaccess(user.optInt("lastaccess"))
				.setDescription(user.optString("description"))
				.setDescriptionformat(user.optInt("descriptionformat"))
				.setCity(user.optString("city"))
				.setCountry(user.optString("country"))
				.setProfileimageurlsmall(user.optString("profileimageurlsmall"))
				.setProfileimageurl(user.optString("profileimageurl"))
				.setRoles(createRoles(user.optJSONArray("roles")))
				.setGroups(createGroups(user.optJSONArray("groups")))
				.setEnrolledcourses(createCourses(user.optJSONArray("enrolledcourses ")))
				.build();
		
		GettersUBUGrades.users.put(id, enrolledUser);
		
		return enrolledUser;

	}

	public static List<Course> createCourses(JSONArray jsonArray) {
		if (jsonArray == null)
			return null;

		List<Course> courses = new ArrayList<Course>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			courses.add(createCourse(jsonObject));
		}
		return courses;

	}

	public static Course createCourse(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;

		int id = jsonObject.getInt("id");

		if (GettersUBUGrades.courses.containsKey(id)) {
			return GettersUBUGrades.courses.get(id);
		}

		String shortName = jsonObject.getString("shortname");
		String fullName = jsonObject.getString("fullname");

		String idNumber = jsonObject.optString("idnumber");
		String summary = jsonObject.optString("summary");
		DescriptionFormat summaryFormat = DescriptionFormat.get(jsonObject.optInt("summaryformat"));

		Course course = new Course(id, shortName, fullName, idNumber, summary, summaryFormat);

		GettersUBUGrades.courses.put(id, course);

		return course;

	}

}
