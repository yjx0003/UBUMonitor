package controllers.ubugrades;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;
import model.Course;
import model.EnrolledUser;
import model.GradeItem;
import model.MoodleUser;
import model.mod.Module;
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

	public static List<EnrolledUser> createEnrolledUsers(int courseid) throws IOException {

		WebService ws = CoreEnrolGetEnrolledUsers.newBuilder(courseid).build();

		String response = ws.getResponse();

		JSONArray users = new JSONArray(response);

		List<EnrolledUser> enrolledUsers = new ArrayList<EnrolledUser>();

		for (int i = 0; i < users.length(); i++) {
			JSONObject user = users.getJSONObject(i);
			enrolledUsers.add(UbuGradesFactory.createEnrolledUser(user));
		}
		return enrolledUsers;

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
		return UbuGradesFactory.createCourses(jsonArray);

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

				modulesList.add(UbuGradesFactory.createModule(mJson));

			}
		}

		return modulesList;

	}
	
	public static List<GradeItem> createGradeItems(int courseid) throws IOException {

		WebService ws = new GradereportUserGetGradesTable(courseid);

		String response = ws.getResponse();

		JSONObject jsonObject = new JSONObject(response);
		
		List<GradeItem> gradeItems = GradeTableController.createHierarchyGradeItems(jsonObject);
		ws = new GradereportUserGetGradeItems(courseid);
		response = ws.getResponse();
		jsonObject = new JSONObject(response);
		GradeTableController.setBasicAttributes(gradeItems, jsonObject);
		GradeTableController.setEnrolledUserGrades(gradeItems, jsonObject);
		
		
		return gradeItems;
	}

}
