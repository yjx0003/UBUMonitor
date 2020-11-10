package es.ubu.lsi.ubumonitor.controllers.load;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.api.gradereport.GradereportUserGetGradeItems;
import es.ubu.lsi.ubumonitor.webservice.api.gradereport.GradereportUserGetGradesTable;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;

public class PopulateGradeItem {

	private static final Logger LOGGER = LoggerFactory.getLogger(PopulateGradeItem.class);
	/**
	 * Icono de carpeta, indica que el grade item es de categoria.
	 */
	private static final String FOLDER_ICON = "icon fa fa-folder fa-fw icon itemicon";

	/**
	 * Nivel de jearquia del grade Item
	 */
	private static final Pattern NIVEL = Pattern.compile("level(\\d+)");

	private DataBase dataBase;

	private WebService webService;

	public PopulateGradeItem(DataBase dataBase, WebService webService) {
		this.dataBase = dataBase;
		this.webService = webService;
	}

	/**
	 * Crea los grade item y su jerarquia de niveles, not using at the moment
	 * {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADES_TABLE} y
	 * {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADE_ITEMS}
	 * 
	 * @param courseid id del curso
	 * @return lista de grade item
	 * @throws IOException si no se ha conectado con moodle
	 */
	public List<GradeItem> createGradeItems(int courseid, int userid) throws IOException {
		try {
			GradereportUserGetGradesTable gradereportUserGetGradesTable = new GradereportUserGetGradesTable(courseid);
			gradereportUserGetGradesTable.setCourseid(courseid);
			gradereportUserGetGradesTable.setUserid(userid);
			JSONObject jsonObject = UtilMethods.getJSONObjectResponse(webService, gradereportUserGetGradesTable);

			return tryGetGradeItems(courseid, jsonObject);

		} catch (Exception e) {
			LOGGER.error("Error al descargar los items de calificaciones", e);
			return Collections.emptyList();
		}

	}

	public List<GradeItem> tryGetGradeItems(int courseid, JSONObject jsonObject) throws IOException {
		try {
			List<GradeItem> gradeItems = createHierarchyGradeItems(jsonObject);
			getGradereportUserGetGradeItems(courseid, gradeItems, jsonObject);
			return gradeItems;
		} catch (Exception e) {
			PopulateGradeItemTable creatorGradeItems = new PopulateGradeItemTable(dataBase, webService);
			return creatorGradeItems.createGradeItems(courseid, jsonObject);
		}
	}

	public JSONObject getGradereportUserGetGradeItems(int courseid, List<GradeItem> gradeItems,
			JSONObject gradeItemsTable) throws IOException {
		JSONObject jsonGradeItems;
		try {
			jsonGradeItems = UtilMethods.getJSONObjectResponse(webService, new GradereportUserGetGradeItems(courseid));
			LOGGER.info("GradereportUserGetGradeItems works");
		} catch (Exception e) {

			jsonGradeItems = gradeItemsTable;
		}
		setBasicAttributes(gradeItems, jsonGradeItems);
		setEnrolledUserGrades(gradeItems, jsonGradeItems);
		updateToOriginalGradeItem(gradeItems);
		return jsonGradeItems;
	}

	/**
	 * Actualiza los grade item
	 * 
	 * @param gradeItems las de grade item
	 */
	private void updateToOriginalGradeItem(List<GradeItem> gradeItems) {
		for (GradeItem gradeItem : gradeItems) {
			GradeItem original = dataBase.getGradeItems()
					.getById(gradeItem.getId());
			dataBase.getActualCourse()
					.addGradeItem(original);

			if (gradeItem.getFather() != null) {
				GradeItem originalFather = dataBase.getGradeItems()
						.getById(gradeItem.getFather()
								.getId());
				original.setFather(originalFather);
			}
			List<GradeItem> originalChildren = new ArrayList<>();
			for (GradeItem child : gradeItem.getChildren()) {
				originalChildren.add(dataBase.getGradeItems()
						.getById(child.getId()));
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
	 * @param jsonObject {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADES_TABLE}
	 * @return lista de grade item
	 */
	private List<GradeItem> createHierarchyGradeItems(JSONObject jsonObject) {

		JSONObject table = jsonObject.getJSONArray(Constants.TABLES)
				.getJSONObject(0);

		int maxDepth = table.getInt(Constants.MAXDEPTH) + 1;

		GradeItem[] categories = new GradeItem[maxDepth];

		JSONArray tabledata = table.getJSONArray(Constants.TABLEDATA);

		List<GradeItem> gradeItems = new ArrayList<>();
		for (int i = 0; i < tabledata.length(); i++) {

			JSONObject item = tabledata.optJSONObject(i); // linea del gradereport

			if (item == null) // grade item no visible
				continue;

			JSONObject itemname = item.getJSONObject(Constants.ITEMNAME);
			int nivel = getNivel(itemname.getString(Constants.CLASS));

			Document content = Jsoup.parseBodyFragment(itemname.getString(Constants.CONTENT));

			GradeItem gradeItem = new GradeItem();

			gradeItem.setLevel(nivel);
			gradeItem.setItemname(content.text());
			Element element;
			if ((element = content.selectFirst("i")) != null && element.className()
					.equals(FOLDER_ICON)) {
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
	 * @param gradeItems lista de grade item
	 * @param jsonObject {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADE_ITEMS}
	 */
	private void setBasicAttributes(List<GradeItem> gradeItems, JSONObject jsonObject) {

		JSONObject usergrade = jsonObject.getJSONArray(Constants.USERGRADES)
				.getJSONObject(0);

		JSONArray gradeitems = usergrade.getJSONArray(Constants.GRADEITEMS);

		if (gradeitems.length() != gradeItems.size()) {
			LOGGER.error(
					"El tamaño de las lineas del calificador no son iguales: de la funcion gradereport_user_get_grade_items es de tamaño {} "
							+ "y de la funcion gradereport_user_get_grades_table se obtiene:{} ",
					gradeitems.length(), gradeItems.size());
			throw new IllegalStateException(
					"El tamaño de las lineas del calificador no son iguales: de la funcion gradereport_user_get_grade_items es de tamaño"
							+ gradeitems.length() + "y de la funcion gradereport_user_get_grades_table se obtiene: "
							+ gradeItems.size());
		}

		for (int i = 0; i < gradeitems.length(); i++) {
			JSONObject gradeitem = gradeitems.getJSONObject(i);
			GradeItem gradeItem = gradeItems.get(i);

			gradeItem.setId(gradeitem.getInt(Constants.ID));

			dataBase.getGradeItems()
					.putIfAbsent(gradeItem.getId(), gradeItem);

			String itemtype = gradeitem.getString(Constants.ITEMTYPE);
			ModuleType moduleType;

			if (Constants.MOD.equals(itemtype)) {
				CourseModule module = dataBase.getModules()
						.getById(gradeitem.getInt(Constants.CMID));
				gradeItem.setModule(module);
				moduleType = ModuleType.get(gradeitem.getString(Constants.ITEMMODULE));

			} else if (Constants.COURSE.equals(itemtype)) {
				moduleType = ModuleType.CATEGORY;

			} else {
				moduleType = ModuleType.get(itemtype);
			}
			gradeItem.setItemModule(moduleType);
			gradeItem.setWeightraw(gradeitem.optDouble(Constants.WEIGHTRAW));

			gradeItem.setGrademin(gradeitem.optDouble(Constants.GRADEMIN));
			gradeItem.setGrademax(gradeitem.optDouble(Constants.GRADEMAX));

		}

	}

	/**
	 * Añade las calificaciones a los usuarios.
	 * 
	 * @param gradeItems gradeitems
	 * @param jsonObject {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADE_ITEMS}
	 */
	private void setEnrolledUserGrades(List<GradeItem> gradeItems, JSONObject jsonObject) {
		JSONArray usergrades = jsonObject.getJSONArray(Constants.USERGRADES);
		DecimalFormat decimalFormat = new DecimalFormat("###.#####");
		for (int i = 0; i < usergrades.length(); i++) {

			JSONObject usergrade = usergrades.getJSONObject(i);

			EnrolledUser enrolledUser = dataBase.getUsers()
					.getById(usergrade.getInt(Constants.USERID));

			JSONArray gradeitems = usergrade.getJSONArray(Constants.GRADEITEMS);
			for (int j = 0; j < gradeitems.length(); j++) {
				JSONObject gradeitem = gradeitems.getJSONObject(j);
				GradeItem gradeItem = gradeItems.get(j);

				gradeItem.addUserGrade(enrolledUser, gradeitem.optDouble(Constants.GRADERAW));
				double grade = Double.NaN;
				String percentage = gradeitem.optString(Constants.PERCENTAGEFORMATTED, "-");
				if (!"-".equals(percentage)) {

					try {
						grade = decimalFormat.parse(percentage)
								.doubleValue();
					} catch (Exception e) {
						LOGGER.warn("Cannot format percentage {}", percentage);
					}
				}
				gradeItem.addUserPercentage(enrolledUser, grade);
			}
		}

	}

	/**
	 * Crea la jerarquia de padre e hijo
	 * 
	 * @param categories grade item de tipo categoria
	 * @param nivel      nivel de jerarquia
	 * @param gradeItem  grade item
	 */
	public static void setFatherAndChildren(GradeItem[] categories, int nivel, GradeItem gradeItem) {
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
	 * @param stringClass el string del key "class" de "itemname"
	 * @return el nivel
	 */
	public static int getNivel(String stringClass) {
		Matcher matcher = NIVEL.matcher(stringClass);
		if (matcher.find()) {
			return Integer.valueOf(matcher.group(1));
		}
		throw new IllegalStateException("No se encuentra el nivel en " + stringClass
				+ ", probablemente haya cambiado el formato de las tablas.");
	}

}
