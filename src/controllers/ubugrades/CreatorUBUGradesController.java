package controllers.ubugrades;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import javafx.scene.image.Image;
import model.Course;
import model.CourseCategory;
import model.CourseModule;
import model.DescriptionFormat;
import model.EnrolledUser;
import model.GradeItem;
import model.Group;
import model.ModuleType;
import model.MoodleUser;
import model.Role;
import model.Section;
import webservice.WebService;
import webservice.core.CoreCourseGetCategories;
import webservice.core.CoreCourseGetContents;
import webservice.core.CoreEnrolGetEnrolledUsers;
import webservice.core.CoreEnrolGetUsersCourses;
import webservice.core.CoreUserGetUsersByField;
import webservice.core.CoreUserGetUsersByField.Field;
import webservice.gradereport.GradereportUserGetGradeItems;
import webservice.gradereport.GradereportUserGetGradesTable;

/**
 * Clase encargada de usar las funciones de la REST API de Moodle para conseguir
 * los datos de los usuarios
 * 
 * @author Yi Peng Ji
 *
 */
public class CreatorUBUGradesController {

	private static final String FULLNAME = "fullname";

	private static final String DESCRIPTIONFORMAT = "descriptionformat";

	private static final String DESCRIPTION = "description";

	private static final Logger LOGGER = LoggerFactory.getLogger(CreatorUBUGradesController.class);

	private static final Controller CONTROLLER = Controller.getInstance();

	/**
	 * Icono de carpeta, indica que el grade item es de categoria.
	 */
	private static final String FOLDER_ICON = "icon fa fa-folder fa-fw icon itemicon";

	/**
	 * Nivel de jearquia del grade Item
	 */
	private static final Pattern NIVEL = Pattern.compile("level(\\d+)");

	/**
	 * Busca los cursos matriculados del alumno.
	 * 
	 * @param userid
	 *            id del usuario
	 * @return lista de cursos matriculados por el usuario
	 * @throws IOException
	 *             error de conexion moodle
	 */
	public static List<Course> getUserCourses(int userid) throws IOException {
		WebService ws = new CoreEnrolGetUsersCourses(userid);
		String response = ws.getResponse();
		JSONArray jsonArray = new JSONArray(response);
		return createCourses(jsonArray);

	}

	/**
	 * Crea un usuario moodle del que se loguea en la aplicación
	 * 
	 * @param username
	 *            nombre de usuario
	 * @return el usuario moodle
	 * @throws IOException
	 *             si no ha podido conectarse al servidor moodle
	 */
	public static MoodleUser createMoodleUser(String username) throws IOException {
		WebService ws = new CoreUserGetUsersByField(Field.USERNAME, username);
		String response = ws.getResponse();

		JSONObject jsonObject = new JSONArray(response).getJSONObject(0);

		MoodleUser moodleUser = new MoodleUser();
		moodleUser.setId(jsonObject.getInt("id"));

		moodleUser.setUserName(jsonObject.optString("username"));

		moodleUser.setFullName(jsonObject.optString(FULLNAME));

		moodleUser.setEmail(jsonObject.optString("email"));

		moodleUser.setFirstAccess(Instant.ofEpochSecond(jsonObject.optLong("firstaccess")));

		moodleUser.setLastAccess(Instant.ofEpochSecond(jsonObject.optLong("lastaccess")));

		byte[] imageBytes = downloadImage(jsonObject.optString("profileimageurlsmall", null));

		moodleUser.setUserPhoto(new Image(new ByteArrayInputStream(imageBytes)));

		moodleUser.setLang(jsonObject.optString("lang"));

		moodleUser.setTimezone(jsonObject.optString("timezone"));

		List<Course> courses = getUserCourses(moodleUser.getId());
		moodleUser.setCourses(courses);

		Set<Integer> ids = courses.stream()
				.mapToInt(c -> c.getCourseCategory().getId()) // cogemos los ids de cada curso
				.boxed() // convertimos a Integer
				.collect(Collectors.toSet());

		createCourseCategories(ids);

		return moodleUser;
	}

	/**
	 * Descarga una imagen de moodle, necesario usar los cookies en versiones
	 * posteriores al 3.5
	 * 
	 * @param url
	 *            url de la image
	 * @return array de bytes de la imagen o un array de byte vacío si la url es
	 *         null
	 * @throws IOException
	 *             si hay algun problema al descargar la imagen
	 */
	private static byte[] downloadImage(String url) throws IOException {
		if (url == null) {
			return new byte[0];
		}

		return Jsoup.connect(url)
				.ignoreContentType(true)
				.cookies(CONTROLLER.getCookies())
				.execute()
				.bodyAsBytes();

	}

	/**
	 * Crea las categorias de curso.
	 * 
	 * @param ids
	 *            ids de las categorias
	 * @throws IOException
	 *             si hay algun problema conectarse a moodle
	 */
	public static void createCourseCategories(Set<Integer> ids) throws IOException {

		WebService ws = new CoreCourseGetCategories(ids);
		String response = ws.getResponse();
		JSONArray jsonArray = new JSONArray(response);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			int id = jsonObject.getInt("id");
			CourseCategory courseCategory = CONTROLLER.getDataBase().getCourseCategories().getById(id);
			courseCategory.setName(jsonObject.getString("name"));
			courseCategory.setDescription(jsonObject.getString(DESCRIPTION));
			courseCategory.setDescriptionFormat(DescriptionFormat.get(jsonObject.getInt(DESCRIPTIONFORMAT)));
			courseCategory.setCoursecount(jsonObject.getInt("coursecount"));
			courseCategory.setDepth(jsonObject.getInt("depth"));
			courseCategory.setPath(jsonObject.getString("path"));

		}

	}

	/**
	 * Crea e inicializa los usuarios matriculados de un curso.
	 * 
	 * @param courseid
	 *            id del curso
	 * @return lista de usuarios matriculados en el curso
	 * @throws IOException
	 *             si no ha podido conectarse
	 */
	public static List<EnrolledUser> createEnrolledUsers(int courseid) throws IOException {

		WebService ws = CoreEnrolGetEnrolledUsers.newBuilder(courseid).build();

		String response = ws.getResponse();

		JSONArray users = new JSONArray(response);

		List<EnrolledUser> enrolledUsers = new ArrayList<>();

		for (int i = 0; i < users.length(); i++) {
			JSONObject user = users.getJSONObject(i);
			enrolledUsers.add(createEnrolledUser(user));
		}
		return enrolledUsers;

	}

	/**
	 * Crea el usuario matriculado a partir del json parcial de la respuesta de
	 * moodle
	 * 
	 * @param user
	 *            json parcial del usuario
	 *            {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS}
	 * @return usuario matriculado
	 * @throws IOException
	 *             si hay un problema de conexion con moodle
	 */
	public static EnrolledUser createEnrolledUser(JSONObject user) throws IOException {

		int id = user.getInt("id");

		EnrolledUser enrolledUser = CONTROLLER.getDataBase().getUsers().getById(id);

		enrolledUser.setFirstname(user.optString("firstname"));
		enrolledUser.setLastname(user.optString("lastname"));
		enrolledUser.setFullName(user.optString(FULLNAME));
		enrolledUser.setFirstaccess(Instant.ofEpochSecond(user.optLong("firstaccess", -1))); // -1 si no esta disponible
		enrolledUser.setLastaccess(Instant.ofEpochSecond(user.optLong("lastaccess", -1)));// -1 si no esta disponible
		enrolledUser.setLastcourseaccess(Instant.ofEpochSecond(user.optLong("lastcourseaccess", -1)));// disponible en
																										// moodle 3.7
		enrolledUser.setDescription(user.optString(DESCRIPTION));
		enrolledUser.setDescriptionformat(DescriptionFormat.get(user.optInt(DESCRIPTIONFORMAT)));
		enrolledUser.setCity(user.optString("city"));
		enrolledUser.setCountry(user.optString("country"));
		enrolledUser.setProfileimageurl(user.optString("profileimageurl"));
		enrolledUser.setEmail(user.optString("email"));

		String imageUrl = user.optString("profileimageurl", null);
		enrolledUser.setProfileimageurlsmall(imageUrl);

		if (imageUrl != null) {
			LOGGER.info("Descargando foto de usuario: {} con la URL: {}", enrolledUser, imageUrl);
			byte[] imageBytes = downloadImage(imageUrl);

			enrolledUser.setImageBytes(imageBytes);
		}

		List<Course> courses = createCourses(user.optJSONArray("enrolledcourses"));
		courses.forEach(course -> course.addEnrolledUser(enrolledUser));

		List<Role> roles = createRoles(user.optJSONArray("roles"));
		roles.forEach(role-> role.addEnrolledUser(enrolledUser));
		

		List<Group> groups = createGroups(user.getJSONArray("groups"));
		groups.forEach(group->group.addEnrolledUser(enrolledUser));

		return enrolledUser;

	}

	/**
	 * Crea los cursos a partir del json parcial de la función moodle de los
	 * usuarios matriculados del curso y de la funcion cursos matriculados de un
	 * usuario.
	 * 
	 * @param jsonArray
	 *            json parcial
	 *            {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS} o
	 *            {@link webservice.WSFunctions#CORE_ENROL_GET_USERS_COURSES}
	 * @return lista de cursos
	 */
	public static List<Course> createCourses(JSONArray jsonArray) {
		if (jsonArray == null)
			return Collections.emptyList();

		List<Course> courses = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			courses.add(createCourse(jsonObject));
		}
		return courses;

	}

	/**
	 * Crea un curso e inicializa sus atributos
	 * 
	 * @param jsonObject
	 *            json parcial
	 *            {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS} o
	 *            {@link webservice.WSFunctions#CORE_ENROL_GET_USERS_COURSES}
	 * @return curso creado
	 */
	public static Course createCourse(JSONObject jsonObject) {
		if (jsonObject == null)
			return null;

		int id = jsonObject.getInt("id");
		Course course = CONTROLLER.getDataBase().getCourses().getById(id);

		String shortName = jsonObject.getString("shortname");
		String fullName = jsonObject.getString(FULLNAME);

		String idNumber = jsonObject.optString("idnumber");
		String summary = jsonObject.optString("summary");
		DescriptionFormat summaryFormat = DescriptionFormat.get(jsonObject.optInt("summaryformat"));
		Instant startDate = Instant.ofEpochSecond(jsonObject.optInt("startdate"));
		Instant endDate = Instant.ofEpochSecond(jsonObject.optInt("enddate"));

		int categoryId = jsonObject.optInt("category");
		if (categoryId != 0) {
			CourseCategory courseCategory = CONTROLLER.getDataBase().getCourseCategories().getById(categoryId);
			course.setCourseCategory(courseCategory);
		}

		course.setShortName(shortName);
		course.setFullName(fullName);
		course.setIdNumber(idNumber);
		course.setSummary(summary);
		course.setSummaryformat(summaryFormat);
		course.setStartDate(startDate);
		course.setEndDate(endDate);

		return course;

	}

	/**
	 * Crea los roles que tiene el usuario dentro del curso.
	 * 
	 * @param jsonArray
	 *            json parcial
	 *            {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS}
	 * @return lista de roles
	 */
	public static List<Role> createRoles(JSONArray jsonArray) {

		if (jsonArray == null)
			return Collections.emptyList();

		List<Role> roles = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			roles.add(createRole(jsonObject));
		}
		return roles;
	}

	/**
	 * Crea un rol
	 * 
	 * @param jsonObject
	 *            json parcial de la funcion
	 *            {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS}
	 * @return el rol creado
	 */
	public static Role createRole(JSONObject jsonObject) {

		if (jsonObject == null)
			return null;

		int roleid = jsonObject.getInt("roleid");

		Role role = CONTROLLER.getDataBase().getRoles().getById(roleid);

		String name = jsonObject.getString("name");
		String shortName = jsonObject.getString("shortname");

		role.setRoleName(name);
		role.setRoleShortName(shortName);

		CONTROLLER.getDataBase().getActualCourse().addRole(role);

		return role;

	}

	/**
	 * Crea los grupos del curso
	 * 
	 * @param jsonArray
	 *            json parcial de
	 *            {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS}
	 * @return listado de grupos
	 */
	public static List<Group> createGroups(JSONArray jsonArray) {

		if (jsonArray == null)
			return Collections.emptyList();

		List<Group> groups = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			groups.add(createGroup(jsonObject));
		}
		return groups;
	}

	/**
	 * Crea un grupo a partir del json
	 * 
	 * @param jsonObject
	 *            {@link webservice.WSFunctions#CORE_ENROL_GET_ENROLLED_USERS}
	 * @return el grupo
	 */
	public static Group createGroup(JSONObject jsonObject) {

		if (jsonObject == null)
			return null;

		int groupid = jsonObject.getInt("id");
		Group group = CONTROLLER.getDataBase().getGroups().getById(groupid);

		String name = jsonObject.getString("name");
		String description = jsonObject.getString(DESCRIPTION);
		DescriptionFormat descriptionFormat = DescriptionFormat.get(jsonObject.getInt(DESCRIPTIONFORMAT));

		group.setGroupName(name);
		group.setDescription(description);
		group.setDescriptionFormat(descriptionFormat);

		CONTROLLER.getDataBase().getActualCourse().addGroup(group);

		return group;

	}

	/**
	 * Crea los modulos del curso a partir de la funcion de moodle
	 * {@link webservice.WSFunctions#CORE_COURSE_GET_CONTENTS}
	 * 
	 * @param courseid
	 *            id del curso
	 * @return lista de modulos del curso
	 * @throws IOException
	 *             error de conexion con moodle
	 */
	public static List<CourseModule> createSectionsAndModules(int courseid) throws IOException {

		WebService ws = CoreCourseGetContents.newBuilder(courseid)
				.setExcludecontents(true) // ignoramos el contenido
				.build();
		String response = ws.getResponse();

		JSONArray jsonArray = new JSONArray(response);
		List<CourseModule> modulesList = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject sectionJson = jsonArray.getJSONObject(i);

			if (sectionJson == null)
				throw new IllegalStateException("Nulo en la secciones del curso");

			Section section = createSection(sectionJson);
			CONTROLLER.getActualCourse().addSection(section);

			JSONArray modules = sectionJson.getJSONArray("modules");

			for (int j = 0; j < modules.length(); j++) {
				JSONObject mJson = modules.getJSONObject(j);
				CourseModule courseModule = createModule(mJson);
				if (courseModule != null) {
					courseModule.setSection(section);
					modulesList.add(courseModule);
					
				}

			}
		}

		return modulesList;

	}

	private static Section createSection(JSONObject sectionJson) {

		int id = sectionJson.getInt("id");

		Section section = CONTROLLER.getDataBase().getSections().getById(id);
		section.setName(sectionJson.optString("name"));
		section.setVisible(sectionJson.optInt("visible") == 1);
		section.setSummary(sectionJson.getString("summary"));
		section.setSummaryformat(DescriptionFormat.get(sectionJson.optInt("summaryformat")));
		section.setSectionNumber(sectionJson.optInt("section", -1));
		section.setHiddenbynumsections(sectionJson.optInt("hiddenbynumsections"));
	

		return section;
	}

	/**
	 * Crea los modulos del curso a partir del json de
	 * {@link webservice.WSFunctions#CORE_COURSE_GET_CONTENTS}
	 * 
	 * @param jsonObject
	 *            de {@link webservice.WSFunctions#CORE_COURSE_GET_CONTENTS}
	 * @return modulo del curso
	 */
	public static CourseModule createModule(JSONObject jsonObject) {

		if (jsonObject == null)
			return null;

		int cmid = jsonObject.getInt("id");

		ModuleType moduleType = ModuleType.get(jsonObject.getString("modname"));

		CourseModule module = CONTROLLER.getDataBase().getModules().getById(cmid);

		module.setCmid(jsonObject.getInt("id"));
		module.setUrl(jsonObject.optString("url"));
		module.setModuleName(jsonObject.optString("name"));
		module.setInstance(jsonObject.optInt("instance"));
		module.setDescription(jsonObject.optString(DESCRIPTION));
		module.setVisible(jsonObject.optInt("visible"));
		module.setUservisible(jsonObject.optBoolean("uservisible"));
		module.setVisibleoncoursepage(jsonObject.optInt("visibleoncoursepage"));
		module.setModicon(jsonObject.optString("modicon"));
		module.setModuleType(moduleType);
		module.setIndent(jsonObject.optInt("indent"));

		CONTROLLER.getDataBase().getActualCourse().addModule(module);

		return module;

	}

	/**
	 * Crea los grade item y su jerarquia de niveles
	 * {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADES_TABLE} y
	 * {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADE_ITEMS}
	 * 
	 * @param courseid
	 *            id del curso
	 * @return lista de grade item
	 * @throws IOException
	 *             si no se ha conectado con moodle
	 */
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
		updateToOriginalGradeItem(gradeItems);

		return gradeItems;
	}

	/**
	 * Actualiza los grade item
	 * 
	 * @param gradeItems
	 *            las de grade item
	 */
	private static void updateToOriginalGradeItem(List<GradeItem> gradeItems) {
		for (GradeItem gradeItem : gradeItems) {
			GradeItem original = CONTROLLER.getDataBase().getGradeItems().getById(gradeItem.getId());
			CONTROLLER.getDataBase().getActualCourse().addGradeItem(original);

			if (gradeItem.getFather() != null) {
				GradeItem originalFather = CONTROLLER.getDataBase().getGradeItems()
						.getById(gradeItem.getFather().getId());
				original.setFather(originalFather);
			}
			List<GradeItem> originalChildren = new ArrayList<>();
			for (GradeItem child : gradeItem.getChildren()) {
				originalChildren.add(CONTROLLER.getDataBase().getGradeItems().getById(child.getId()));
			}
			original.setChildren(originalChildren);

			original.setItemname(gradeItem.getItemname());
			original.setLevel(gradeItem.getLevel());
			original.setWeightraw(gradeItem.getWeightraw());
			original.setGraderaw(gradeItem.getGraderaw());
			original.setGrademax(gradeItem.getGrademax());
			original.setGrademin(gradeItem.getGrademin());

		}
	}

	/**
	 * Crea la jearquia de padres e hijos de los grade item
	 * 
	 * @param jsonObject
	 *            {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADES_TABLE}
	 * @return lista de grade item
	 */
	private static List<GradeItem> createHierarchyGradeItems(JSONObject jsonObject) {

		JSONObject table = jsonObject.getJSONArray("tables").getJSONObject(0);

		int maxDepth = table.getInt("maxdepth") + 1;

		GradeItem[] categories = new GradeItem[maxDepth];

		JSONArray tabledata = table.getJSONArray("tabledata");

		List<GradeItem> gradeItems = new ArrayList<>();
		for (int i = 0; i < tabledata.length(); i++) {

			JSONObject item = tabledata.optJSONObject(i); // linea del gradereport

			if (item == null) // grade item no visible
				continue;

			JSONObject itemname = item.getJSONObject("itemname");
			int nivel = getNivel(itemname.getString("class"));

			Document content = Jsoup.parseBodyFragment(itemname.getString("content"));

			GradeItem gradeItem = new GradeItem();

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

	/**
	 * Inicializa los atributos basicos del grade item
	 * 
	 * @param gradeItems
	 *            lista de grade item
	 * @param jsonObject
	 *            {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADE_ITEMS}
	 */
	private static void setBasicAttributes(List<GradeItem> gradeItems, JSONObject jsonObject) {

		JSONObject usergrade = jsonObject.getJSONArray("usergrades").getJSONObject(0);

		JSONArray gradeitems = usergrade.getJSONArray("gradeitems");

		if (gradeitems.length() != gradeItems.size()) {
			LOGGER.error(
					"El tamaño de las lineas del calificador no son iguales: de la funcion gradereport_user_get_grade_items es de tamaño {} "
							+ "y de la funcion gradereport_user_get_grades_table se obtiene:{} ",
					gradeitems.length(),
					gradeItems.size());
			throw new IllegalStateException(
					"El tamaño de las lineas del calificador no son iguales: de la funcion gradereport_user_get_grade_items es de tamaño"
							+ gradeitems.length() + "y de la funcion gradereport_user_get_grades_table se obtiene: "
							+ gradeItems.size());
		}

		for (int i = 0; i < gradeitems.length(); i++) {
			JSONObject gradeitem = gradeitems.getJSONObject(i);
			GradeItem gradeItem = gradeItems.get(i);

			gradeItem.setId(gradeitem.getInt("id"));

			CONTROLLER.getDataBase().getGradeItems().putIfAbsent(gradeItem.getId(), gradeItem);

			String itemtype = gradeitem.getString("itemtype");
			ModuleType moduleType;

			if ("mod".equals(itemtype)) {
				CourseModule module = CONTROLLER.getDataBase().getModules().getById(gradeitem.getInt("cmid"));
				gradeItem.setModule(module);
				moduleType = ModuleType.get(gradeitem.getString("itemmodule"));

			} else if ("course".equals(itemtype)) {
				moduleType = ModuleType.CATEGORY;

			} else {
				moduleType = ModuleType.get(itemtype);
			}
			gradeItem.setItemModule(moduleType);
			gradeItem.setWeightraw(gradeitem.optDouble("weightraw"));

			gradeItem.setGrademin(gradeitem.optDouble("grademin"));
			gradeItem.setGrademax(gradeitem.optDouble("grademax"));

		}

	}

	/**
	 * Añade las calificaciones a los usuarios.
	 * 
	 * @param gradeItems
	 *            gradeitems
	 * @param jsonObject
	 *            {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADE_ITEMS}
	 */
	private static void setEnrolledUserGrades(List<GradeItem> gradeItems, JSONObject jsonObject) {
		JSONArray usergrades = jsonObject.getJSONArray("usergrades");

		for (int i = 0; i < usergrades.length(); i++) {

			JSONObject usergrade = usergrades.getJSONObject(i);

			EnrolledUser enrolledUser = CONTROLLER.getDataBase().getUsers().getById(usergrade.getInt("userid"));

			JSONArray gradeitems = usergrade.getJSONArray("gradeitems");
			for (int j = 0; j < gradeitems.length(); j++) {
				JSONObject gradeitem = gradeitems.getJSONObject(j);
				GradeItem gradeItem = gradeItems.get(j);

				gradeItem.addUserGrade(enrolledUser, gradeitem.optDouble("graderaw"));
			}
		}

	}

	/**
	 * Crea la jerarquia de padre e hijo
	 * 
	 * @param categories
	 *            grade item de tipo categoria
	 * @param nivel
	 *            nivel de jerarquia
	 * @param gradeItem
	 *            grade item
	 */
	protected static void setFatherAndChildren(GradeItem[] categories, int nivel, GradeItem gradeItem) {
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
	protected static int getNivel(String stringClass) {
		Matcher matcher = NIVEL.matcher(stringClass);
		if (matcher.find()) {
			return Integer.valueOf(matcher.group(1));
		}
		throw new IllegalStateException("No se encuentra el nivel en " + stringClass
				+ ", probablemente haya cambiado el formato de las tablas.");
	}

	private CreatorUBUGradesController() {
		throw new UnsupportedOperationException();
	}

}
