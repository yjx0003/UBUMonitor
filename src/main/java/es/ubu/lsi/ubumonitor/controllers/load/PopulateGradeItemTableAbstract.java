package es.ubu.lsi.ubumonitor.controllers.load;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.api.gradereport.GradereportUserGetGradesTable;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;

/**
 * Metodo alternativo de busqueda grade item al no funcionar la funcion
 * {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADE_ITEMS} con el
 * feedback desactivado.
 * 
 * @author Yi Peng Ji
 * @author Raúl Marticorena
 * @since 2.10.4
 *
 */
public abstract class PopulateGradeItemTableAbstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(PopulateGradeItemTableAbstract.class);

	protected static final String CONTENT = "content";

	/**
	 * Patrin que busca el id del course module
	 */
	protected static final Pattern MODULE_ID_PATTERN = Pattern.compile("id=(\\d+)");

	protected final DecimalFormat decimalFormat;

	protected DataBase dataBase;
	protected WebService webService;

	/**
	 * Constructor a partir del locale
	 * 
	 * @param locale locale
	 */
	public PopulateGradeItemTableAbstract(DataBase dataBase, WebService webService) {
		decimalFormat = new DecimalFormat("###.#####");
		this.dataBase = dataBase;
		this.webService = webService;
	}

	/**
	 * Creamos los gradeItem a partir de la funcion
	 * {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADES_TABLE}
	 * 
	 * @param courseid id del curso
	 * @return lista de grade item
	 * @throws JSONException error en el json
	 * @throws IOException   error de conexion con moodle
	 */
	public List<GradeItem> createGradeItems(int courseid, JSONObject userTable) throws IOException {

		JSONObject jsonObject;
		try {
			jsonObject = UtilMethods.getJSONObjectResponse(webService, new GradereportUserGetGradesTable(courseid));
			if (!jsonObject.has(Constants.TABLES)) {

				jsonObject = userTable;
			}
		} catch (Exception e) {
			LOGGER.error("Error al parsear las calificaciones.", e);
			jsonObject = userTable;
		}

		List<GradeItem> gradeItems = createHierarchyGradeItems(jsonObject);
		setEnrolledUserGrades(jsonObject, gradeItems);

		return gradeItems;
	}

	/**
	 * Crea la jerarquia de padres e hijos en los grade item
	 * 
	 * @param jsonObject json
	 * @return lista de grade item
	 */
	abstract List<GradeItem> createHierarchyGradeItems(JSONObject jsonObject); 

	/**
	 * Asigna el grade item al modulo del curso si existe
	 * 
	 * @param gradeItem     grade item
	 * @param contentString string con la posibilidad que contenga el id del modulo
	 *                      del curso
	 */
	void setCourseModule(GradeItem gradeItem, String contentString) {

		Matcher matcher = MODULE_ID_PATTERN.matcher(contentString);
		if (matcher.find()) {

			int cmid = Integer.parseInt(matcher.group(1));
			CourseModule module = dataBase.getModules()
					.getById(cmid);
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
	void setAtrributes(GradeItem gradeItem, JSONObject tabledataJsonObject) {

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
	boolean isEmpty(String content) {
		return "-".equals(content);
	}

	/**
	 * Convierte el rango de en calificacioens minimas y maximas posibles. Los que
	 * son escala, las notas minima es 0 y maxima 100
	 * 
	 * @param gradeItem           grade item
	 * @param tabledataJsonObject json
	 */
	void setGradeMinMax(GradeItem gradeItem, JSONObject tabledataJsonObject) {
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
	 * @param gradeItem           grade item
	 * @param tabledataJsonObject json
	 */
	void setWeight(GradeItem gradeItem, JSONObject tabledataJsonObject) {

		if (!tabledataJsonObject.has(Constants.WEIGHT)) {
			return;
		}

		JSONObject weight = tabledataJsonObject.getJSONObject(Constants.WEIGHT);
		String content = weight.getString(CONTENT);

		if (isEmpty(content)) {
			gradeItem.setWeightraw(Double.NaN);
		} else {
			try {
				double weightraw = decimalFormat.parse(content)
						.doubleValue() / 100;
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
	 void setEnrolledUserGrades(JSONObject jsonObject, List<GradeItem> gradeItems) {
		JSONArray jsonArray = jsonObject.optJSONArray(Constants.TABLES);
		if (jsonArray == null) {
			return;
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject table = jsonArray.optJSONObject(i);
			if (table == null) {
				continue;
			}
			int userid = table.getInt(Constants.USERID);
			EnrolledUser enrolledUser = dataBase.getUsers()
					.getById(userid);
			JSONArray tabledata = table.getJSONArray(Constants.TABLEDATA);
			int gradeItemCount = 0;
			for (int j = 0; j < tabledata.length(); j++) {
				JSONObject tabledataObject = tabledata.optJSONObject(j);

				if (tabledataObject != null && tabledataObject.has(Constants.PERCENTAGE)) {

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
	 * @param gradeItem       grade item
	 * @param enrolledUser    usuario
	 */
	abstract void setGrade(JSONObject tabledataObject, GradeItem gradeItem, EnrolledUser enrolledUser);

	/**
	 * Asigna la columna del porcentaje.
	 * 
	 * @param tabledataObject json
	 * @param gradeItem       gradeitem actual
	 * @param enrolledUser    usuario
	 */
	 void setPercentage(JSONObject tabledataObject, GradeItem gradeItem, EnrolledUser enrolledUser) {

		JSONObject percentageJson = tabledataObject.optJSONObject(Constants.PERCENTAGE);
		double percentage = Double.NaN;
		if (percentageJson != null) {
			String content = percentageJson.optString(CONTENT);
			if (!"-".equals(content)) {
				try {
					percentage = decimalFormat.parse(content)
							.doubleValue();
				} catch (ParseException e) {
					LOGGER.warn("No se puede parsear {} a decimal", content);
				}
			}

		}
		gradeItem.addUserPercentage(enrolledUser, percentage);
	}

}

