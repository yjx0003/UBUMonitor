package es.ubu.lsi.ubumonitor.controllers.load;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;

/**
 * Metodo alternativo de busqueda grade item al no funcionar la funcion
 * {@link webservice.WSFunctions#GRADEREPORT_USER_GET_GRADE_ITEMS} con el
 * feedback desactivado.
 * 
 * @author Raúl Marticorena
 * @since 2.10.4
 */
public class PopulateGradeItemMoodle4Table extends PopulateGradeItemTableAbstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(PopulateGradeItemMoodle4Table.class);

	/**
	 * Chevron down icon.
	 */
	private static final String FOLDER_ICON = "icon fa fa-chevron-down fa-fw"; // 4.x expand

	/**
	 * Manual icon.
	 */
	private static final String MANUAL_ITEM_ICON = "icon fa fa-pencil-square-o fa-fw icon itemicon"; // 4.x

	/**
	 * Constructor a partir del locale
	 * 
	 * @param locale locale
	 */
	public PopulateGradeItemMoodle4Table(DataBase dataBase, WebService webService) {
		super(dataBase, webService);
	}

	/**
	 * Crea la jerarquia de padres e hijos en los grade item
	 * 
	 * @param jsonObject json
	 * @return lista de grade item
	 */
	@Override
	List<GradeItem> createHierarchyGradeItems(JSONObject jsonObject) {

		JSONObject table = Optional.ofNullable(jsonObject).map(a -> a.optJSONArray(Constants.TABLES))
				.map(o -> o.optJSONObject(0)).orElse(null);
		if (table == null) { // if there is no element in the gradereport
			return Collections.emptyList();
		}

		int maxDepth = table.getInt(Constants.MAXDEPTH) + 1;

		GradeItem[] categories = new GradeItem[maxDepth];

		JSONArray tabledata = table.getJSONArray(Constants.TABLEDATA);

		List<GradeItem> gradeItems = new ArrayList<>();
		for (int i = 0; i < tabledata.length(); i++) { 

			JSONObject tabledataJsonObject = tabledata.optJSONObject(i); // linea del gradereport

			if (tabledataJsonObject == null) // grade item no visible
				continue;

			// jump if contains leader
			if (tabledataJsonObject.optJSONObject("leader") != null) {
				continue; // jump this element!!!!
			}

			JSONObject itemname = tabledataJsonObject.getJSONObject(Constants.ITEMNAME);

			int nivel = PopulateGradeItem.getNivel(itemname.getString(Constants.CLASS));
			String contentString = itemname.getString(CONTENT);
			Document content = Jsoup.parseBodyFragment(contentString);

			GradeItem gradeItem = dataBase.getGradeItems().getById(i);

			gradeItem.clearChildren();

			gradeItem.setLevel(nivel);

			// Buscamos la etiqueta HTML "i" dentro del JSONObject de content.
			Element element = content.selectFirst("i");

			String icon = element == null ? "" : element.className();

			if (icon.equals(FOLDER_ICON)) { // TODO
				LOGGER.debug("adding text category in gradebook: {}", content.text());
				gradeItem.setItemname(content.text());
			} else if (icon.equals(MANUAL_ITEM_ICON)) {
				LOGGER.debug("adding text manual item in gradebook: {}",
						content.getElementsByClass("gradeitemheader").text());
				gradeItem.setItemname(content.getElementsByClass("gradeitemheader").text());
			} else {
				LOGGER.debug("adding text item in gradebook: {}", content.getElementsByTag("a").text());
				gradeItem.setItemname(content.getElementsByTag("a").text());
			}

			// Si hay icono de carpeta es la cabecera de la categoria con su nombre de
			// carpeta
			if (icon.equals(FOLDER_ICON)) {
				gradeItem.setItemModule(ModuleType.CATEGORY);
				categories[nivel] = gradeItem;
				PopulateGradeItem.setFatherAndChildren(categories, nivel, gradeItem);

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
				PopulateGradeItem.setFatherAndChildren(categories, nivel, gradeItem);
			} else { // todos los demas modulos calificables
				gradeItems.add(gradeItem);
				setAtrributes(gradeItem, tabledataJsonObject);
				PopulateGradeItem.setFatherAndChildren(categories, nivel, gradeItem);

				setCourseModule(gradeItem, contentString);

			}

		}

		return gradeItems;
	}

	/**
	 * Asigna la calificacion de un usario a un grade item
	 * 
	 * @param tabledataObject json
	 * @param gradeItem       grade item
	 * @param enrolledUser    usuario
	 */
	@Override
	void setGrade(JSONObject tabledataObject, GradeItem gradeItem, EnrolledUser enrolledUser) {
		if (!tabledataObject.has("grade")) {
			gradeItem.addUserGrade(enrolledUser, Double.NaN);
			return;
		}

		String content = tabledataObject.getJSONObject(Constants.GRADE).getString(CONTENT);
		double grade = Double.NaN;

		if (!"-".equals(content)) {

			try {
				// check if the content grade comes with any icon
				if (content.contains("icon fa")) {
					int last = content.lastIndexOf('>'); // e.g. aria-label=\"Pass\"><\/i>6.00"
					grade = decimalFormat.parse(content.substring(last + 1)).doubleValue();
				}
				else {
					grade = decimalFormat.parse(content) // classic grade without icons
							.doubleValue();
				}
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
}