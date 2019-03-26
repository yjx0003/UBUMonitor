package controllers.ubugrades;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;
import model.BBDD;
import model.Course;
import model.DescriptionFormat;
import model.EnrolledUser;
import model.GradeItem;
import model.Group;
import model.ItemType;
import model.MoodleUser;
import model.Role;
import model.mod.Module;
import model.mod.ModuleType;
import webservice.WebService;
import webservice.core.CoreCourseGetContents;
import webservice.core.CoreEnrolGetEnrolledUsers;
import webservice.core.CoreEnrolGetUsersCourses;
import webservice.core.CoreUserGetUsersByField;
import webservice.core.CoreUserGetUsersByField.Field;
import webservice.gradereport.GradereportUserGetGradeItems;
import webservice.gradereport.GradereportUserGetGradesTable;

public class CreatorUBUGradesController {

	static final Logger logger = LoggerFactory.getLogger(CreatorUBUGradesController.class);

	private static BBDD BBDD;

	private static final String FOLDER_ICON = "icon fa fa-folder fa-fw icon itemicon";
	private static final Pattern NIVEL = Pattern.compile("level(\\d+)");

	public static void setBBDD(BBDD BBDD) {
		CreatorUBUGradesController.BBDD = BBDD;
	}

	public static List<EnrolledUser> createEnrolledUsers(int courseid) throws IOException {

		WebService ws = CoreEnrolGetEnrolledUsers.newBuilder(courseid).build();

		String response = ws.getResponse();

		JSONArray users = new JSONArray(response);

		List<EnrolledUser> enrolledUsers = new ArrayList<EnrolledUser>();

		for (int i = 0; i < users.length(); i++) {
			JSONObject user = users.getJSONObject(i);
			enrolledUsers.add(createEnrolledUser(user));
		}
		return enrolledUsers;

	}

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

		if (BBDD.containsRole(roleid)) {
			return BBDD.getRoleById(roleid);
		}
		Role role = new Role(jsonObject.getInt("roleid"), jsonObject.getString("name"),
				jsonObject.getString("shortname"));

		BBDD.putRole(role);
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

		if (BBDD.containsGroup(groupid)) {
			return BBDD.getGroupById(groupid);
		}

		String name = jsonObject.getString("name");
		String description = jsonObject.getString("description");
		DescriptionFormat descriptionFormat = DescriptionFormat.get(jsonObject.getInt("descriptionformat"));

		Group group = new Group(groupid, name, description, descriptionFormat);

		BBDD.putGroup(group);

		return group;

	}

	public static EnrolledUser createEnrolledUser(JSONObject user) {

		int id = user.getInt("id");

		Map<Integer, EnrolledUser> users = BBDD.getUsers();

		if (users.containsKey(id)) {
			return users.get(id);
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
				.build();

		List<Course> courses = createCourses(user.optJSONArray("enrolledcourses"));
		courses.forEach(course -> course.addEnrolledUser(enrolledUser));
		enrolledUser.setEnrolledcourses(courses);

		List<Role> roles = createRoles(user.optJSONArray("roles"));
		roles.forEach(role -> role.addEnrolledUser(enrolledUser));
		enrolledUser.setRoles(roles);

		List<Group> groups = createGroups(user.optJSONArray("groups"));
		groups.forEach(group -> group.addEnrolledUser(enrolledUser));
		users.put(id, enrolledUser);

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

		if (BBDD.containsCourse(id)) {
			return BBDD.getCourseById(id);
		}

		String shortName = jsonObject.getString("shortname");
		String fullName = jsonObject.getString("fullname");

		String idNumber = jsonObject.optString("idnumber");
		String summary = jsonObject.optString("summary");
		DescriptionFormat summaryFormat = DescriptionFormat.get(jsonObject.optInt("summaryformat"));

		Course course = new Course(id, shortName, fullName, idNumber, summary, summaryFormat);

		BBDD.putCourse(course);

		return course;

	}

	public static Module createModule(JSONObject jsonObject) {

		if (jsonObject == null)
			return null;

		int cmid = jsonObject.getInt("id");

		if (BBDD.containsModule(cmid)) {

			return BBDD.getCourseModuleById(cmid);
		}
		String modname = jsonObject.getString("modname");

		Module module = ModuleType.createInstance(modname);

		attributesModule(jsonObject, module);

		BBDD.putModule(module);

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

	public static MoodleUser createMoodleUser(String username) throws IOException {
		WebService ws = new CoreUserGetUsersByField(Field.USERNAME, username);
		String response = ws.getResponse();

		JSONObject jsonObject = new JSONArray(response).getJSONObject(0);

		MoodleUser moodleUser = new MoodleUser();
		moodleUser.setId(jsonObject.getInt("id"));

		moodleUser.setUserName(jsonObject.optString("username"));

		moodleUser.setFullName(jsonObject.optString("fullname"));

		moodleUser.setEmail(jsonObject.optString("email"));

		moodleUser.setFirstAccess(Instant.ofEpochSecond(jsonObject.optLong("firstaccess")));

		moodleUser.setLastAccess(Instant.ofEpochSecond(jsonObject.optLong("lastaccess")));

		moodleUser.setUserPhoto(new Image(jsonObject.optString("profileimageurlsmall")));

		moodleUser.setTimezone(jsonObject.optString("timezone"));

		List<Course> courses = getUserCourses(moodleUser.getId());
		moodleUser.setCourses(courses);

		return moodleUser;
	}

	public static List<Course> getUserCourses(int userid) throws IOException {
		WebService ws = new CoreEnrolGetUsersCourses(userid);
		String response = ws.getResponse();
		JSONArray jsonArray = new JSONArray(response);
		return createCourses(jsonArray);

	}

	public static List<Module> createModules(int courseid) throws IOException {

		WebService ws = CoreCourseGetContents.newBuilder(courseid)
				.setExcludecontents(true)
				.build();
		String response = ws.getResponse();

		JSONArray jsonArray = new JSONArray(response);
		List<Module> modulesList = new ArrayList<Module>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject section = jsonArray.getJSONObject(i);
			// por si se quiere crear una clase section

			JSONArray modules = section.getJSONArray("modules");

			for (int j = 0; j < modules.length(); j++) {
				JSONObject mJson = modules.getJSONObject(j);

				modulesList.add(createModule(mJson));

			}
		}

		return modulesList;

	}

	public static List<GradeItem> createGradeItems(int courseid) throws IOException {

		WebService ws = new GradereportUserGetGradesTable(courseid);

		String response = ws.getResponse();

		JSONObject jsonObject = new JSONObject(response);

		List<GradeItem> gradeItems = createHierarchyGradeItems(jsonObject);
		ws = new GradereportUserGetGradeItems(courseid);
		response = ws.getResponse();
		jsonObject = new JSONObject(response);
		setBasicAttributes(gradeItems, jsonObject);
		setEnrolledUserGrades(gradeItems, jsonObject);

		return gradeItems;
	}

	public static List<GradeItem> createHierarchyGradeItems(JSONObject jsonObject) throws IOException {

		JSONObject table = jsonObject.getJSONArray("tables").getJSONObject(0);

		int maxDepth = table.getInt("maxdepth") + 1;

		GradeItem[] categories = new GradeItem[maxDepth];

		JSONArray tabledata = table.getJSONArray("tabledata");

		List<GradeItem> gradeItems = new ArrayList<GradeItem>();
		for (int i = 0; i < tabledata.length(); i++) {

			JSONObject item = tabledata.optJSONObject(i); // linea del gradereport

			if (item == null) // grade item no visible
				continue;

			JSONObject itemname = item.getJSONObject("itemname");
			int nivel = getNivel(itemname.getString("class"));

			Document content = Jsoup.parseBodyFragment(itemname.getString("content"));

			GradeItem gradeItem = new GradeItem();
			BBDD.putGradeItem(gradeItem);
			gradeItem.setLevel(nivel);
			gradeItem.setItemname(content.text());
			Element element;
			if ((element = content.selectFirst("i")) != null && element.className().equals(FOLDER_ICON)) {
				gradeItem.setItemname(content.text());
				categories[nivel] = gradeItem;
				setFatherAndChildren(categories, nivel, gradeItem);

			} else if (categories[nivel] != null) {

				gradeItems.add(categories[nivel]);
				categories[nivel] = null;

			} else {

				gradeItems.add(gradeItem);
				setFatherAndChildren(categories, nivel, gradeItem);
			}

		}

		return gradeItems;
	}

	public static void setBasicAttributes(List<GradeItem> gradeItems, JSONObject jsonObject) throws IOException {

		JSONObject usergrade = jsonObject.getJSONArray("usergrades").getJSONObject(0);

		Course course = BBDD.getCourseById(usergrade.getInt("courseid"));

		JSONArray gradeitems = usergrade.getJSONArray("gradeitems");

		if (gradeitems.length() != gradeItems.size()) {
			logger.error(
					"El tamaño de las lineas del calificador no son iguales: de la funcion gradereport_user_get_grade_items es de tamaño"
							+ gradeitems.length() + "y de la funcion gradereport_user_get_grades_table se obtiene: "
							+ gradeItems.size());
			throw new IllegalStateException(
					"El tamaño de las lineas del calificador no son iguales: de la funcion gradereport_user_get_grade_items es de tamaño"
							+ gradeitems.length() + "y de la funcion gradereport_user_get_grades_table se obtiene: "
							+ gradeItems.size());
		}

		for (int i = 0; i < gradeitems.length(); i++) {
			JSONObject gradeitem = gradeitems.getJSONObject(i);
			GradeItem gradeItem = gradeItems.get(i);

			gradeItem.setCourse(course);
			gradeItem.setId(gradeitem.getInt("id"));

			String itemtype = gradeitem.getString("itemtype");

			ItemType itemType = ItemType.get(itemtype);
			gradeItem.setItemtype(itemType);

			if (itemType == ItemType.MOD) {
				Module module = BBDD.getCourseModuleById(gradeitem.optInt("cmid"));
				gradeItem.setModule(module);
				ModuleType moduleType = ModuleType.get(gradeitem.getString("itemmodule"));
				gradeItem.setItemModule(moduleType);
			}

			gradeItem.setWeightraw(gradeitem.optDouble("weightraw"));

			gradeItem.setGrademin(gradeitem.optDouble("grademin"));
			gradeItem.setGrademax(gradeitem.optDouble("grademax"));

		}

	}

	public static void setEnrolledUserGrades(List<GradeItem> gradeItems, JSONObject jsonObject) {
		JSONArray usergrades = jsonObject.getJSONArray("usergrades");

		for (int i = 0; i < usergrades.length(); i++) {

			JSONObject usergrade = usergrades.getJSONObject(i);

			EnrolledUser enrolledUser = BBDD.getEnrolledUserById(usergrade.getInt("userid"));

			JSONArray gradeitems = usergrade.getJSONArray("gradeitems");
			for (int j = 0; j < gradeitems.length(); j++) {
				JSONObject gradeitem = gradeitems.getJSONObject(j);
				GradeItem gradeItem = gradeItems.get(j);

				gradeItem.addUserGrade(enrolledUser, gradeitem.optDouble("graderaw"));

			}
		}

	}

	private static void setFatherAndChildren(GradeItem[] categories, int nivel, GradeItem gradeItem) {
		if (nivel > 1) {
			GradeItem padre = categories[nivel - 1];
			gradeItem.setFather(padre);
			padre.addChildren(gradeItem);
		}
	}

	/**
	 * Busca el nivel jerarquico del grade item dentro del calificador. Por ejemplo
	 * "level1 levelodd oddd1 b1b b1t column-itemname", devolvería 1.
	 * 
	 * @param stringClass
	 *            el string del key "class" de "itemname"
	 * @return el nivel
	 */
	private static int getNivel(String stringClass) {
		Matcher matcher = NIVEL.matcher(stringClass);
		if (matcher.find()) {
			return Integer.valueOf(matcher.group(1));
		}
		throw new IllegalStateException("No se encuentra el nivel en " + stringClass
				+ ", probablemente haya cambiado el formato de las tablas.");
	}

}
