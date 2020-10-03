package es.ubu.lsi.ubumonitor.controllers.load;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.CourseCategory;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.DescriptionFormat;
import es.ubu.lsi.ubumonitor.model.SubDataBase;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetCoursesByField;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetEnrolledCoursesByTimelineClassification;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetEnrolledCoursesByTimelineClassification.Classification;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetRecentCourses;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetUserAdministrationOptions;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseGetUserNavigationOptions;
import es.ubu.lsi.ubumonitor.webservice.api.core.course.CoreCourseSearchCourses;
import es.ubu.lsi.ubumonitor.webservice.api.core.enrol.CoreEnrolGetUsersCourses;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;
import okhttp3.Response;

public class PopulateCourse {
	private static final Logger LOGGER = LoggerFactory.getLogger(PopulateCourse.class);
	private static final List<Course> EMPTY_LIST_COURSE = Collections.emptyList();
	private DataBase dataBase;
	private WebService webService;

	public PopulateCourse(DataBase dataBase, WebService webService) {
		this.dataBase = dataBase;
		this.webService = webService;
	}

	public List<Course> createCourses(int userid) {
		try {
			JSONArray jsonArray = UtilMethods.getJSONArrayResponse(webService, new CoreEnrolGetUsersCourses(userid));
			return userCourses(jsonArray);
		} catch (Exception e) {
			return EMPTY_LIST_COURSE;
		}

	}

	public List<Course> userCourses(JSONArray jsonArray) {
		if (jsonArray == null)
			return Collections.emptyList();

		List<Course> courses = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if (jsonObject != null) {
				courses.add(userCourses(jsonObject));
			}

		}
		return courses;

	}

	/**
	 * Convert to Course object JSONObject from
	 * {@link WSFunctionEnum#CORE_ENROL_GET_USERS_COURSES}
	 * 
	 * @param dataBase
	 * @param jsonObject
	 * @return the course created
	 * 
	 */
	public Course userCourses(JSONObject jsonObject) {
		Course course = dataBase.getCourses()
				.getById(jsonObject.getInt(Constants.ID));

		course.setShortName(jsonObject.getString(Constants.SHORTNAME));
		course.setFullName(jsonObject.getString(Constants.FULLNAME));

		course.setHasActivityCompletion(jsonObject.optBoolean(Constants.ENABLECOMPLETION));
		int categoryId = jsonObject.optInt(Constants.CATEGORY);
		if (categoryId != 0) {
			CourseCategory courseCategory = dataBase.getCourseCategories()
					.getById(categoryId);
			course.setCourseCategory(courseCategory);
		}

		course.setIdNumber(jsonObject.optString(Constants.IDNUMBER));
		course.setSummary(jsonObject.optString(Constants.SUMMARY));
		course.setSummaryformat(DescriptionFormat.get(jsonObject.optInt(Constants.SUMMARYFORMAT)));
		course.setStartDate(Instant.ofEpochSecond(jsonObject.optLong(Constants.STARTDATE)));
		course.setEndDate(Instant.ofEpochSecond(jsonObject.optLong(Constants.ENDDATE)));
		course.setFavorite(jsonObject.optBoolean(Constants.ISFAVOURITE));

		return course;

	}

	public List<Course> searchCourse(String value) {
	
		try {
			CoreCourseSearchCourses coreCourseSearchCourses = new CoreCourseSearchCourses();
			coreCourseSearchCourses.setBySearch(value);
			JSONObject jsonObject = UtilMethods.getJSONObjectResponse(webService, coreCourseSearchCourses);
			
			return searchCourse(jsonObject);
		} catch (Exception e) {
			LOGGER.error("Error in searching course", e);
			return EMPTY_LIST_COURSE;
		}
	}

	public List<Course> searchCourse(JSONObject jsonObject) {

		JSONArray coursesArray = jsonObject.getJSONArray(Constants.COURSES);

		List<Course> courses = new ArrayList<>();
		for (int i = 0; i < coursesArray.length(); ++i) {
			JSONObject jsObject = coursesArray.getJSONObject(i);
			Course course = dataBase.getCourses()
					.getById(jsObject.getInt(Constants.ID));
			course.setFullName(jsObject.getString(Constants.FULLNAME));
			course.setShortName(jsObject.optString(Constants.SHORTNAME));
			course.setSummary(jsObject.optString(Constants.SUMMARY));
			course.setSummaryformat(DescriptionFormat.get(jsObject.optInt(Constants.SUMMARYFORMAT)));

			CourseCategory category = dataBase.getCourseCategories()
					.getById(jsObject.getInt(Constants.CATEGORYID));
			category.setName(jsObject.getString(Constants.CATEGORYNAME));
			course.setCourseCategory(category);
			courses.add(course);
		}
		return courses;

	}

	public List<Course> coursesByTimelineClassification(Classification classification) {
		try {
			JSONObject jsonObject = UtilMethods.getJSONObjectResponse(webService,
					new CoreCourseGetEnrolledCoursesByTimelineClassification(classification));
			return coursesByTimelineClassification(jsonObject.getJSONArray(Constants.COURSES));
		} catch (Exception e) {
			LOGGER.error("Cannot get timeline courses:", e);
			return EMPTY_LIST_COURSE;
		}
	}

	/**
	 * 
	 * @param jsonObject
	 * @return
	 */
	public List<Course> coursesByTimelineClassification(JSONArray jsonArray) {

		List<Course> courses = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			Course course = dataBase.getCourses()
					.getById(jsonArray.getJSONObject(i)
							.getInt(Constants.ID));
			courses.add(course);
		}
		return courses;
	}

	public List<Course> recentCourses() {
		try {

			Response response = webService.getAjaxResponse(new CoreCourseGetRecentCourses());
			JSONArray jsonArray = new JSONArray(response.body()
					.string());

			return coursesByTimelineClassification(jsonArray.getJSONObject(0)
					.getJSONArray(Constants.DATA));
		} catch (Exception e) {
			LOGGER.error("Cannot get recent courses:", e);
			return EMPTY_LIST_COURSE;
		}
	}

	public void createCourseAdministrationOptions(Collection<Integer> courseids) {

		if (courseids == null || courseids.isEmpty()) {
			return;
		}
		SubDataBase<Course> dataBaseCourse = dataBase.getCourses();

		try {
			JSONArray jsonArray;
			jsonArray = UtilMethods
					.getJSONObjectResponse(webService, new CoreCourseGetUserAdministrationOptions(courseids))
					.getJSONArray(Constants.COURSES);
			findPermission(jsonArray, dataBaseCourse, "reports", Course::setReportAccess);

			jsonArray = UtilMethods.getJSONObjectResponse(webService, new CoreCourseGetUserNavigationOptions(courseids))
					.getJSONArray(Constants.COURSES);
			findPermission(jsonArray, dataBaseCourse, "grades", Course::setGradeItemAccess);
			CoreCourseGetCoursesByField coreCourseGetCoursesByField = new CoreCourseGetCoursesByField();
			coreCourseGetCoursesByField.setIds(courseids);
			jsonArray = UtilMethods.getJSONObjectResponse(webService, coreCourseGetCoursesByField)
					.getJSONArray(Constants.COURSES);

			for (int i = 0; i < jsonArray.length(); ++i) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				Course course = dataBaseCourse.getById(jsonObject.getInt(Constants.ID));
				if (jsonObject.has(Constants.ENABLECOMPLETION)) {
					course.setHasActivityCompletion(jsonObject.optInt(Constants.ENABLECOMPLETION) == 1);
				}

			}
		} catch (Exception e) {
			LOGGER.warn("Error in course options: ", e);
		}

	}

	public void findPermission(JSONArray jsonArray, SubDataBase<Course> dataBaseCourse, String permission,
			BiConsumer<Course, Boolean> consumer) {
		for (int i = 0; i < jsonArray.length(); ++i) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Course course = dataBaseCourse.getById(jsonObject.getInt(Constants.ID));
			course.setCourseAccess(true);
			JSONArray options = jsonObject.getJSONArray(Constants.OPTIONS);
			for (int j = 0; j < options.length(); ++j) {
				JSONObject option = options.getJSONObject(j);

				if (permission.equals(option.getString(Constants.NAME))) {
					consumer.accept(course, option.getBoolean(Constants.AVAILABLE));
				}
			}
		}

	}

}
