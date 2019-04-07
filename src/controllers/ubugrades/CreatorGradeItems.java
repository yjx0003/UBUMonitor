package controllers.ubugrades;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
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

import controllers.Controller;
import model.EnrolledUser;
import model.GradeItem;
import model.mod.Module;
import model.mod.ModuleType;
import webservice.WebService;
import webservice.gradereport.GradereportUserGetGradesTable;

public class CreatorGradeItems {

	static final Logger logger = LoggerFactory.getLogger(CreatorGradeItems.class);

	private static final String FOLDER_ICON = "icon fa fa-folder fa-fw icon itemicon";
	private static final String MANUAL_ITEM_ICON = "icon fa fa-square-o fa-fw icon itemicon";

	private static final Pattern MODULE_ID_PATTERN = Pattern.compile("id=(\\d+)");

	private static final Controller CONTROLLLER = Controller.getInstance();
	private final DecimalFormat decimalFormat;

	public CreatorGradeItems(Locale locale) {
		decimalFormat = new DecimalFormat("0.0", new DecimalFormatSymbols(locale));
	}

	public List<GradeItem> createGradeItems(int courseid) throws JSONException, IOException {
		WebService ws = new GradereportUserGetGradesTable(courseid);
		String response = ws.getResponse();
		JSONObject jsonObject = new JSONObject(response);
		List<GradeItem> gradeItems = createHierarchyGradeItems(jsonObject);

		setEnrolledUserGrades(jsonObject, gradeItems);

		return gradeItems;
	}

	private List<GradeItem> createHierarchyGradeItems(JSONObject jsonObject) {

		JSONObject table = jsonObject.getJSONArray("tables").getJSONObject(0);

		int maxDepth = table.getInt("maxdepth") + 1;

		GradeItem[] categories = new GradeItem[maxDepth];

		JSONArray tabledata = table.getJSONArray("tabledata");

		List<GradeItem> gradeItems = new ArrayList<GradeItem>();
		for (int i = 0; i < tabledata.length(); i++) {

			JSONObject tabledataJsonObject = tabledata.optJSONObject(i); // linea del gradereport

			if (tabledataJsonObject == null) // grade item no visible
				continue;

			JSONObject itemname = tabledataJsonObject.getJSONObject("itemname");
			int nivel = CreatorUBUGradesController.getNivel(itemname.getString("class"));
			String contentString = itemname.getString("content");
			Document content = Jsoup.parseBodyFragment(contentString);

			GradeItem gradeItem = CONTROLLLER.getBBDD().getGradeItemByString(content.text());
			gradeItem.clearChildren();
			CONTROLLLER.getActualCourse().addGradeItem(gradeItem);
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
			} else {
				gradeItems.add(gradeItem);
				setAtrributes(gradeItem, tabledataJsonObject);
				CreatorUBUGradesController.setFatherAndChildren(categories, nivel, gradeItem);

				setCourseModule(gradeItem, contentString);

			}

		}

		return gradeItems;
	}

	private void setCourseModule(GradeItem gradeItem, String contentString) {
	
		Matcher matcher = MODULE_ID_PATTERN.matcher(contentString);
		if (matcher.find()) {
			
			int cmid = Integer.parseInt(matcher.group(1));
			Module module=CONTROLLLER.getBBDD().getCourseModuleById(cmid);
			gradeItem.setModule(module);
			gradeItem.setItemModule(module.getModuleType());
		}

	}

	private void setAtrributes(GradeItem gradeItem, JSONObject tabledataJsonObject) {

		setWeight(gradeItem, tabledataJsonObject);
		setGradeMinMax(gradeItem, tabledataJsonObject);

	}

	private boolean isEmpty(String content) {
		return content.equals("-");
	}

	private void setGradeMinMax(GradeItem gradeItem, JSONObject tabledataJsonObject) {
		if (!tabledataJsonObject.has("range")) {
			return;
		}

		JSONObject range = tabledataJsonObject.getJSONObject("range");
		String content = range.getString("content");

		String[] minMax = content.split("&ndash;");
		double minGrade;
		double maxGrade;
		try {
			minGrade = Double.parseDouble(minMax[0]);
			maxGrade = Double.parseDouble(minMax[1]);
		} catch (NumberFormatException e) {
			// si al parsar no es un numero es una escala, asignamos 0 y 100 a como nota de
			// la escala
			minGrade = 0.0;
			maxGrade = 100.0;
		}

		gradeItem.setGrademin(minGrade);
		gradeItem.setGrademax(maxGrade);
	}

	private void setWeight(GradeItem gradeItem, JSONObject tabledataJsonObject) {

		if (!tabledataJsonObject.has("weight")) {
			return;
		}

		JSONObject weight = tabledataJsonObject.getJSONObject("weight");
		String content = weight.getString("content");

		if (isEmpty(content)) {
			gradeItem.setWeightraw(Double.NaN);
		} else {
			try {
				double weightraw = decimalFormat.parse(content).doubleValue() / 100;
				gradeItem.setWeightraw(weightraw);
			} catch (ParseException e) {
				logger.error("Error al parsear la nota: " + content, e);
				gradeItem.setWeightraw(Double.NaN);
			}
		}

	}

	private void setEnrolledUserGrades(JSONObject jsonObject, List<GradeItem> gradeItems) {
		JSONArray jsonArray = jsonObject.getJSONArray("tables");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject table = jsonArray.optJSONObject(i);
			if (table == null) {
				continue;
			}
			int userid = table.getInt("userid");
			EnrolledUser enrolledUser = CONTROLLLER.getBBDD().getEnrolledUserById(userid);
			JSONArray tabledata = table.getJSONArray("tabledata");

			for (int j = 0, gradeItemCount = 0; j < tabledata.length(); j++) {
				JSONObject tabledataObject = tabledata.optJSONObject(j);
				if(tabledataObject==null) {
					continue;
				}
				if (tabledataObject.has("grade")) {
					setGrade(tabledataObject, gradeItems.get(gradeItemCount), enrolledUser);
					gradeItemCount++;
				}
			}
		}
	}

	private void setGrade(JSONObject tabledataObject, GradeItem gradeItem, EnrolledUser enrolledUser) {

		String content = tabledataObject.getJSONObject("grade").getString("content");
		double grade = Double.NaN;

		if (!content.equals("-")) {

			try {
				grade = decimalFormat.parse("content").doubleValue();
			} catch (ParseException e) {
				logger.info("No se puede parsear: "+content+", lo intentamos buscando el porcentaje");
				content = tabledataObject.getJSONObject("percentage").getString("content");
				try {
					grade = decimalFormat.parse(content).doubleValue();
				} catch (ParseException e1) {
					logger.error("No se puede parsear la nota de: " + tabledataObject.toString(2), e1);
				}
			}
		}

		gradeItem.addUserGrade(enrolledUser, grade);

	}

}
