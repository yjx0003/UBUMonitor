package es.ubu.lsi.ubumonitor.controllers.load;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.model.CourseCategory;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.DescriptionFormat;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetCategories;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;

public class PopulateCourseCategories {

	private DataBase dataBase;
	private WebService webService;

	public PopulateCourseCategories(DataBase dataBase, WebService webService) {
		this.dataBase = dataBase;
		this.webService = webService;
	}

	public void populateCourseCategories(Collection<Integer> categoryids) {

		try {
			CoreCourseGetCategories coreCourseGetCategories = new CoreCourseGetCategories();
			coreCourseGetCategories.appendIds(categoryids);
			coreCourseGetCategories.setAddsubcategories(false);
			JSONArray jsonArray = UtilMethods.getJSONArrayResponse(webService, coreCourseGetCategories);
			populateCourseCategories(jsonArray);
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * Populate attributes in every course category from
	 * {@link WSFunctionEnum#CORE_COURSE_GET_CATEGORIES}
	 * 
	 * @param dataBase
	 * @param jsonArray
	 */
	public void populateCourseCategories(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			int id = jsonObject.getInt(Constants.ID);
			CourseCategory courseCategory = dataBase.getCourseCategories()
					.getById(id);
			courseCategory.setName(jsonObject.getString(Constants.NAME));
			courseCategory.setDescription(jsonObject.getString(Constants.DESCRIPTION));
			courseCategory.setDescriptionFormat(DescriptionFormat.get(jsonObject.getInt(Constants.DESCRIPTIONFORMAT)));
			courseCategory.setCoursecount(jsonObject.getInt(Constants.COURSECOUNT));
			courseCategory.setDepth(jsonObject.getInt(Constants.DEPTH));
			courseCategory.setPath(jsonObject.getString(Constants.PATH));

		}
	}

}
