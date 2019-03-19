package controllers.ubugrades;

import java.io.IOException;
import java.util.ArrayList;
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

import model.Course;
import model.EnrolledUser;
import model.GradeItem;
import model.ItemType;
import model.mod.Module;
import model.mod.ModuleType;

public class GradeTableController {

	static final Logger logger = LoggerFactory.getLogger(GradeTableController.class);
	private static final String FOLDER_ICON = "icon fa fa-folder fa-fw icon itemicon";
	private static final Pattern NIVEL = Pattern.compile("level(\\d+)");

	

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

		Course course = GettersUBUGrades.getCourseById(usergrade.getInt("courseid"));

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
			
			String itemtype=gradeitem.getString("itemtype");
			
			ItemType itemType = ItemType.get(itemtype);
			gradeItem.setItemtype(itemType);

			
			if (itemType == ItemType.MOD) {
				Module module = GettersUBUGrades.getCourseModuleById(gradeitem.optInt("cmid"));
				gradeItem.setModule(module);
				ModuleType moduleType=ModuleType.get(gradeitem.getString("itemmodule"));
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

			EnrolledUser enrolledUser = GettersUBUGrades.getEnrolledUserById(usergrade.getInt("userid"));

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
