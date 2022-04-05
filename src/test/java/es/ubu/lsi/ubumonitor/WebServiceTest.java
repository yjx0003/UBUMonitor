package es.ubu.lsi.ubumonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.load.DownloadLogController;
import es.ubu.lsi.ubumonitor.controllers.load.LogCreator;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateActivityCompletion;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateCourse;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateCourseCategories;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateCourseContent;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateEnrolledUsersCourse;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateGradeItem;
import es.ubu.lsi.ubumonitor.controllers.load.PopulateMoodleUser;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.CourseCategory;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Logs;
import es.ubu.lsi.ubumonitor.model.MoodleUser;
import es.ubu.lsi.ubumonitor.model.Section;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.api.core.webservice.CoreWebserviceGetSiteInfo;
import es.ubu.lsi.ubumonitor.webservice.api.mod.quiz.ModQuizGetQuizzesByCourses;
import es.ubu.lsi.ubumonitor.webservice.api.mod.quiz.ModQuizGetUserAttempts;
import es.ubu.lsi.ubumonitor.webservice.api.tool.mobile.ToolMobileCallExternalFunctions;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;
import javafx.util.Pair;
import okhttp3.Response;

@TestMethodOrder(OrderAnnotation.class)
public class WebServiceTest {

	private static final String USERNAME = "teacher";
	private static final String PASSWORD = "moodle";
	private static final String HOST = "https://school.moodledemo.net";
	private static final int COURSE_ID = 62;
	private static WebService webService;
	private static DataBase dataBase;

	private static Controller CONTROLLER;

	@BeforeAll
	public static void loginTest() throws IOException {
		System.setProperty("logfile.name", "./log/" + AppInfo.APPLICATION_NAME_WITH_VERSION + "-test.log");
		CONTROLLER = Controller.getInstance();
		CONTROLLER.tryLogin(HOST, USERNAME, PASSWORD);
		CONTROLLER.setDataBase(new DataBase());
		CONTROLLER.setUsername(USERNAME);
		CONTROLLER.setURLHost(new URL(HOST));
		CONTROLLER.setPassword(PASSWORD);
		CONTROLLER.setActualCourse(CONTROLLER.getDataBase()
				.getCourses()
				.getById(COURSE_ID));
		webService = CONTROLLER.getWebService();
		dataBase = CONTROLLER.getDataBase();
	}

	@Test
	@Order(2)
	public void createMoodleUserTest() throws IOException {
		PopulateMoodleUser populateMoodleUser = new PopulateMoodleUser(webService);
		MoodleUser moodleUser = populateMoodleUser.populateMoodleUser(CONTROLLER.getUsername(), HOST);
		assertEquals(CONTROLLER.getUsername(), moodleUser.getUserName());
		CONTROLLER.setUser(moodleUser);
	}

	@Test
	@Order(3)
	public void getUserCourses() throws IOException {
		PopulateCourse populateCourse = new PopulateCourse(dataBase, webService);

		List<Course> courses = populateCourse.createCourses(CONTROLLER.getUser()
				.getId());
		assertFalse(courses.isEmpty());
		assertTrue(courses.stream()
				.anyMatch(c -> c.getId() == COURSE_ID), "User not enrolled in the course id: " + COURSE_ID);

	}

	@Test
	@Order(4)
	public List<EnrolledUser> getEnrolledUsers() throws IOException {
		PopulateEnrolledUsersCourse populateEnrolledUsersCourse = new PopulateEnrolledUsersCourse(dataBase, webService);
		List<EnrolledUser> enrolledUsers = populateEnrolledUsersCourse.createEnrolledUsers(COURSE_ID);
		assertFalse(enrolledUsers.isEmpty());
		return enrolledUsers;
	}

	@Test
	@Order(5)
	public Pair<List<Section>, List<CourseModule>> getCourseModules() throws IOException {
		PopulateCourseContent populateCourseContent = new PopulateCourseContent(webService, dataBase);
		Pair<List<Section>, List<CourseModule>> pair = populateCourseContent.populateCourseContent(COURSE_ID);
		assertFalse(pair.getKey()
				.isEmpty());
		assertFalse(pair.getValue()
				.isEmpty());
		return pair;
	}

	@Test
	@Order(6)
	public void getGradeItems() throws IOException {
		PopulateGradeItem populateGradeItem = new PopulateGradeItem(dataBase, webService);

		List<GradeItem> gradeItems = populateGradeItem.createGradeItems(COURSE_ID, CONTROLLER.getUser()
				.getId());
		assertFalse(gradeItems.isEmpty());
	}

	@Test
	@Order(6)
	public void getActivityCompletion() throws IOException {
		PopulateActivityCompletion populateActivityCompletion = new PopulateActivityCompletion(dataBase, webService);
		populateActivityCompletion.creeateActivitiesCompletionStatus(COURSE_ID, getEnrolledUsers());
		System.out.println();
		assertTrue(getCourseModules()
				.getValue()
				.stream()
				.map(CourseModule::getActivitiesCompletion)
				.anyMatch(m -> !m.isEmpty()));
	}

	@Test
	@Order(7)
	public void getLogs() throws IOException {
		JSONObject jsonObject = UtilMethods.getJSONObjectResponse(webService, new CoreWebserviceGetSiteInfo());
		LogCreator.setDateTimeFormatter(jsonObject.getString("release"));
		DownloadLogController downloadLogController = LogCreator.download();

		Response response = downloadLogController.downloadLog(true);

		Logs logs = new Logs(downloadLogController.getServerTimeZone());
		LogCreator.parserResponse(logs, response.body()
				.charStream());
		CONTROLLER.getActualCourse()
				.setLogs(logs);
		assertFalse(logs.getList()
				.isEmpty());
	}

	@Test
	@Order(9)
	public void modQuizGetUserAttempts() throws IOException {

		List<Integer> userids = getEnrolledUsers().stream()
				.map(EnrolledUser::getId)
				.collect(Collectors.toList());

		List<Integer> quizzesids = new ArrayList<>();
		ModQuizGetQuizzesByCourses modQuizGetQuizzesByCourses = new ModQuizGetQuizzesByCourses(COURSE_ID);

		JSONArray jsonArray = new JSONObject(webService.getResponse(modQuizGetQuizzesByCourses)
				.body()
				.string()).getJSONArray("quizzes");
		for (int i = 0; i < jsonArray.length(); ++i) {
			quizzesids.add(jsonArray.getJSONObject(i)
					.getInt("id"));
		}

		ModQuizGetUserAttempts modQuizGetUserAttempts = new ModQuizGetUserAttempts();
		for (Integer userid : userids) {
			for (Integer quizid : quizzesids) {

				modQuizGetUserAttempts.setQuizid(quizid);
				modQuizGetUserAttempts.setUserid(userid);
				// Response response = webService.getResponse(modQuizGetUserAttempts);
				// String value = response.body().string();
				// System.out.println(response.request().url());
				// System.out.println(value);
			}
		}

	}

	@Test
	@Order(10)
	public void toolMobileCallExternalFunctions() throws IOException {
		ToolMobileCallExternalFunctions toolMobileCallExternalFunctions = new ToolMobileCallExternalFunctions();

		List<Integer> userids = getEnrolledUsers().stream()
				.map(EnrolledUser::getId)
				.collect(Collectors.toList());

		List<Integer> quizzesids = new ArrayList<>();
		ModQuizGetQuizzesByCourses modQuizGetQuizzesByCourses = new ModQuizGetQuizzesByCourses(COURSE_ID);

		JSONArray jsonArray = new JSONObject(webService.getResponse(modQuizGetQuizzesByCourses)
				.body()
				.string()).getJSONArray("quizzes");

		for (int i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.getJSONObject(i)
					.getInt("visible") == 1) {
				quizzesids.add(jsonArray.getJSONObject(i)
						.getInt("id"));
			}

		}

		for (Integer quizid : quizzesids) {
			for (Integer userid : userids) {
				ModQuizGetUserAttempts modQuizGetUserAttempts = new ModQuizGetUserAttempts(quizid, userid);

				toolMobileCallExternalFunctions.addExternalFunction(modQuizGetUserAttempts);
			}
		}

		Response response = webService.getResponse(toolMobileCallExternalFunctions);
		JSONObject value = new JSONObject(response.body()
				.string());

		assertEquals(quizzesids.size() * userids.size(), value.getJSONArray("responses")
				.length());
	}

	@Test
	@Order(10)
	public void CoreCourseSearchCourses() {
		PopulateCourse populateCourse = new PopulateCourse(dataBase, webService);

		Pair<Integer, List<Course>> pair = populateCourse.searchCourse("a");

		assertNotEquals(0, pair.getKey());
		assertFalse(pair.getValue()
				.isEmpty());

	}

	@Test
	@Order(11)
	public void coreCourseGetCategories() {

		PopulateCourseCategories populateCourseCategories = new PopulateCourseCategories(dataBase, webService);
		List<Integer> categoryIds = CONTROLLER.getDataBase()
				.getCourses()
				.getMap()
				.values()
				.stream()
				.map(Course::getCourseCategory)
				.map(CourseCategory::getId)
				.distinct()
				.collect(Collectors.toList());
		List<CourseCategory> courseCategories = populateCourseCategories.populateCourseCategories(categoryIds);
		assertFalse(courseCategories.isEmpty());

	}

}
