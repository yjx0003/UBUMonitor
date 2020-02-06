package es.ubu.lsi.ubumonitor.controllers.ubugrades;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.webservice.WebService;
import es.ubu.lsi.ubumonitor.webservice.gradereport.GradereportUserGetGradesTable;

/**
 * Metodo alternativo de busqueda grade item al no funcionar la funcion
 * {@link es.ubu.lsi.ubumonitor.webservice.WSFunctions#GRADEREPORT_USER_GET_GRADE_ITEMS} con el
 * feedback desactivado.
 * 
 * @author Yi Peng Ji
 *
 */
public class CreatorGradeItems {

	private static final String CONTENT = "content";

	private static final Logger LOGGER = LoggerFactory.getLogger(CreatorGradeItems.class);

	/**
	 * Icono de tipo categoria
	 */
	private static final String FOLDER_ICON = "icon fa fa-folder fa-fw icon itemicon";

	/**
	 * Icono de items manuales
	 */
	private static final String MANUAL_ITEM_ICON = "icon fa fa-square-o fa-fw icon itemicon";

	/**
	 * Patrin que busca el id del course module
	 */
	private static final Pattern MODULE_ID_PATTERN = Pattern.compile("id=(\\d+)");

	private static final Controller CONTROLLER = Controller.getInstance();
	private final DecimalFormat decimalFormat;

	/**
	 * Constructor a partir del locale
	 * 
	 * @param locale locale
	 */
	public CreatorGradeItems(Locale locale) {
		decimalFormat = new DecimalFormat("###.#####", new DecimalFormatSymbols(locale));
	}

	/**
	 * Creamos los gradeItem a partir de la funcion
	 * {@link es.ubu.lsi.ubumonitor.webservice.WSFunctions#GRADEREPORT_USER_GET_GRADES_TABLE}
	 * 
	 * @param courseid id del curso
	 * @return lista de grade item
	 * @throws JSONException error en el json
	 * @throws IOException error de conexion con moodle
	 */
	public List<GradeItem> createGradeItems(int courseid) throws IOException {
		WebService ws = new GradereportUserGetGradesTable(courseid);
		String response = ws.getResponse();

		JSONObject jsonObject = new JSONObject(response);

		List<GradeItem> gradeItems = createHierarchyGradeItems(jsonObject);

		setEnrolledUserGrades(jsonObject, gradeItems);
		CONTROLLER.getActualCourse().setGradeItems(new HashSet<>(gradeItems));
		return gradeItems;
	}

	/**
	 * Crea la jerarquia de padres e hijos en los grade item
	 * 
	 * @param jsonObject json
	 * @return lista de grade item
	 */
	private List<GradeItem> createHierarchyGradeItems(JSONObject jsonObject) {

		JSONObject table = jsonObject.getJSONArray("tables").optJSONObject(0);
		if (table == null) { // if there is no element in the gradereport
			return Collections.emptyList();
		}

		int maxDepth = table.getInt("maxdepth") + 1;

		GradeItem[] categories = new GradeItem[maxDepth];

		JSONArray tabledata = table.getJSONArray("tabledata");

		List<GradeItem> gradeItems = new ArrayList<>();
		for (int i = 0; i < tabledata.length(); i++) {

			JSONObject tabledataJsonObject = tabledata.optJSONObject(i); // linea del gradereport

			if (tabledataJsonObject == null) // grade item no visible
				continue;

			JSONObject itemname = tabledataJsonObject.getJSONObject("itemname");
			int nivel = CreatorUBUGradesController.getNivel(itemname.getString("class"));
			String contentString = itemname.getString(CONTENT);
			Document content = Jsoup.parseBodyFragment(contentString);

			GradeItem gradeItem = CONTROLLER.getDataBase().getGradeItems().getById(i);
			gradeItem.setItemname(content.text());
			gradeItem.clearChildren();

			gradeItem.setLevel(nivel);

			// Buscamos la etiqueta HTML "i" dentro del JSONObject de content.
			Element element = content.selectFirst("i");

			String icon = element == null ? "" : element.className();

			// Si hay icono de carpeta es la cabecera de la categoria con su nombre de
			// carpeta
			if (icon.equals(FOLDER_ICON)) {
				gradeItem.setItemModule(ModuleType.CATEGORY);
				categories[nivel] = gradeItem;
				CreatorUBUGradesController.setFatherAndChildren(categories, nivel, gradeItem);

				// Comprobamos si es nota de la categoria, si existe un elemento en ese nivel es
				// que es de la nota de la categoria
			} else if (categories[nivel] != null) {
				gradeItem = categories[nivel];
				categories[nivel] = null;
				gradeItems.add(gradeItem);
				setAtrributes(gradeItem, tabledataJsonObject);
			} else if (icon.equals(MANUAL_ITEM_ICON)) {
				gradeItem.setItemModule(ModuleType.MANUAL_ITEM);
				gradeItems.add(gradeItem);
				setAtrributes(gradeItem, tabledataJsonObject);
				CreatorUBUGradesController.setFatherAndChildren(categories, nivel, gradeItem);
			} else { // todos los demas modulos calificables
				gradeItems.add(gradeItem);
				setAtrributes(gradeItem, tabledataJsonObject);
				CreatorUBUGradesController.setFatherAndChildren(categories, nivel, gradeItem);

				setCourseModule(gradeItem, contentString);

			}

		}

		return gradeItems;
	}

	/**
	 * Asigna el grade item al modulo del curso si existe
	 * 
	 * @param gradeItem grade item
	 * @param contentString string con la posibilidad que contenga el id del modulo
	 * del curso
	 */
	private void setCourseModule(GradeItem gradeItem, String contentString) {

		Matcher matcher = MODULE_ID_PATTERN.matcher(contentString);
		if (matcher.find()) {

			int cmid = Integer.parseInt(matcher.group(1));
			CourseModule module = CONTROLLER.getDataBase().getModules().getById(cmid);
			gradeItem.setModule(module);
			gradeItem.setItemModule(module.getModuleType());
		}

	}

	/**
	 * Asigna atributos de peso y calificaciones minimas y maximas posibles
	 * 
	 * @param gradeItem
	 * @param tabledataJsonObject
	 */
	private void setAtrributes(GradeItem gradeItem, JSONObject tabledataJsonObject) {

		setWeight(gradeItem, tabledataJsonObject);
		setGradeMinMax(gradeItem, tabledataJsonObject);

	}

	/**
	 * Comprueba si el contenido de una celda de la tabla de calificacion esta vacia
	 * (-)
	 * 
	 * @param content
	 * @return
	 */
	private boolean isEmpty(String content) {
		return "-".equals(content);
	}

	/**
	 * Convierte el rango de en calificacioens minimas y maximas posibles. Los que
	 * son escala, las notas minima es 0 y maxima 100
	 * 
	 * @param gradeItem grade item
	 * @param tabledataJsonObject json
	 */
	private void setGradeMinMax(GradeItem gradeItem, JSONObject tabledataJsonObject) {
		if (!tabledataJsonObject.has("range")) {
			return;
		}

		JSONObject range = tabledataJsonObject.getJSONObject("range");
		String content = range.getString(CONTENT);

		double minGrade;
		double maxGrade;
		try {
			String[] minMax = content.split("&ndash;");
			minGrade = Double.parseDouble(minMax[0]);
			maxGrade = Double.parseDouble(minMax[1]);
		} catch (NumberFormatException e) {
			// si al parsar no es un numero es una escala, asignamos 0 y 100 a como nota de
			// la escala
			minGrade = 0.0;
			maxGrade = 100.0;
		} catch (RuntimeException e) {
			minGrade = 0.0;
			maxGrade = 1.0;
		}

		gradeItem.setGrademin(minGrade);
		gradeItem.setGrademax(maxGrade);
	}

	/**
	 * Asigna el peso del grade item
	 * 
	 * @param gradeItem grade item
	 * @param tabledataJsonObject json
	 */
	private void setWeight(GradeItem gradeItem, JSONObject tabledataJsonObject) {

		if (!tabledataJsonObject.has("weight")) {
			return;
		}

		JSONObject weight = tabledataJsonObject.getJSONObject("weight");
		String content = weight.getString(CONTENT);

		if (isEmpty(content)) {
			gradeItem.setWeightraw(Double.NaN);
		} else {
			try {
				double weightraw = decimalFormat.parse(content).doubleValue() / 100;
				gradeItem.setWeightraw(weightraw);
			} catch (ParseException e) {
				LOGGER.error("Error al parsear la nota: " + content, e);
				gradeItem.setWeightraw(Double.NaN);
			}
		}

	}

	/**
	 * Asigna las calificaciones a los usuarios.
	 * 
	 * @param jsonObject json
	 * @param gradeItems lista de grade items
	 */
	private void setEnrolledUserGrades(JSONObject jsonObject, List<GradeItem> gradeItems) {
		JSONArray jsonArray = jsonObject.getJSONArray("tables");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject table = jsonArray.optJSONObject(i);
			if (table == null) {
				continue;
			}
			int userid = table.getInt("userid");
			EnrolledUser enrolledUser = CONTROLLER.getDataBase().getUsers().getById(userid);
			JSONArray tabledata = table.getJSONArray("tabledata");
			int gradeItemCount = 0;
			for (int j = 0; j < tabledata.length(); j++) {
				JSONObject tabledataObject = tabledata.optJSONObject(j);

				if (tabledataObject != null && tabledataObject.has("percentage")) {

					setGrade(tabledataObject, gradeItems.get(gradeItemCount), enrolledUser);
					setPercentage(tabledataObject, gradeItems.get(gradeItemCount), enrolledUser);
			
					gradeItemCount++;
				}
				

			}
		}
	}

	/**
	 * Asigna la calificacion de un usario a un grade item
	 * 
	 * @param tabledataObject json
	 * @param gradeItem grade item
	 * @param enrolledUser usuario
	 */
	private void setGrade(JSONObject tabledataObject, GradeItem gradeItem, EnrolledUser enrolledUser) {
		if(!tabledataObject.has("grade")) {
			gradeItem.addUserGrade(enrolledUser, Double.NaN);
			return;
		}
		
		String content = tabledataObject.getJSONObject("grade").getString(CONTENT);
		double grade = Double.NaN;

		if (!"-".equals(content)) {

			try {
				grade = decimalFormat.parse(content).doubleValue();
			} catch (ParseException e) {
				LOGGER.info("No se puede parsear: {}, lo intentamos buscando el porcentaje", content);

				try {
					JSONObject percentage = tabledataObject.optJSONObject("percentage");
					if (percentage != null) {
						content = percentage.optString(CONTENT);
						grade = decimalFormat.parse(content).doubleValue();
					}

				} catch (ParseException e1) {
					LOGGER.error("No se puede parsear la nota de: " + tabledataObject.toString(2), e1);
				}
			}
		}

		gradeItem.addUserGrade(enrolledUser, grade);

	}

	/**
	 * Asigna la columna del porcentaje.
	 * 
	 * @param tabledataObject json
	 * @param gradeItem gradeitem actual
	 * @param enrolledUser usuario
	 */
	private void setPercentage(JSONObject tabledataObject, GradeItem gradeItem, EnrolledUser enrolledUser) {

		JSONObject percentageJson = tabledataObject.optJSONObject("percentage");
		double percentage = Double.NaN;
		if (percentageJson != null) {
			String content = percentageJson.optString(CONTENT);
			if (!"-".equals(content)) {
				try {
					percentage = decimalFormat.parse(content).doubleValue();
				} catch (ParseException e) {
					LOGGER.warn("No se puede parsear {} a decimal", content);
				}
			}
			

		}
		gradeItem.addUserPercentage(enrolledUser, percentage);
	}

}
