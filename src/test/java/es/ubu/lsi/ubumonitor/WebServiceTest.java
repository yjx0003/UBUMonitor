package es.ubu.lsi.ubumonitor;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import es.ubu.lsi.ubumonitor.controllers.load.CreatorGradeItems;
import es.ubu.lsi.ubumonitor.controllers.load.CreatorUBUGradesController;
import es.ubu.lsi.ubumonitor.controllers.load.DownloadLogController;
import es.ubu.lsi.ubumonitor.controllers.load.LogCreator;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Logs;
import es.ubu.lsi.ubumonitor.model.MoodleUser;
import es.ubu.lsi.ubumonitor.webservice.api.gradereport.GradereportUserGetGradesTable;
import es.ubu.lsi.ubumonitor.webservice.api.mod.quiz.ModQuizGetQuizzesByCourses;
import es.ubu.lsi.ubumonitor.webservice.api.mod.quiz.ModQuizGetUserAttempts;
import es.ubu.lsi.ubumonitor.webservice.api.tool.mobile.ToolMobileCallExternalFunctions;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;
import okhttp3.Response;

@TestMethodOrder(OrderAnnotation.class)
public class WebServiceTest {

	private static final String USERNAME = "teacher";
	private static final String PASSWORD = "moodle";
	private static final String HOST = "https://school.moodledemo.net";
	private static final int COURSE_ID = 62;
	private static WebService webService;

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
		CONTROLLER.setActualCourse(CONTROLLER.getDataBase().getCourses().getById(COURSE_ID));
		webService = CONTROLLER.getWebService();
	}

	@Test
	@Order(2)
	public void createMoodleUserTest() throws IOException {
		MoodleUser moodleUser = CreatorUBUGradesController.createMoodleUser(CONTROLLER.getUsername());
		assertEquals(CONTROLLER.getUsername(), moodleUser.getUserName());
		CONTROLLER.setUser(moodleUser);
	}

	@Test
	@Order(3)
	public void getUserCourses() throws IOException {
		List<Course> courses = CreatorUBUGradesController.getUserCourses(CONTROLLER.getUser().getId());
		assertFalse(courses.isEmpty());
		assertTrue(courses.stream().anyMatch(c -> c.getId() == COURSE_ID), "User not enrolled in the course id: " + COURSE_ID);
		
	}

	@Test
	@Order(4)
	public List<EnrolledUser> getEnrolledUsers() throws IOException {
		List<EnrolledUser> enrolledUsers = CreatorUBUGradesController.createEnrolledUsers(COURSE_ID);
		assertFalse(enrolledUsers.isEmpty());
		return enrolledUsers;
	}
	
	@Test
	@Order(5)
	public void getCourseModules() throws IOException {
		List<CourseModule> courseModules = CreatorUBUGradesController.createSectionsAndModules(COURSE_ID);
		assertFalse(courseModules.isEmpty());
	}
	
	@Test
	@Order(6)
	public void getGradeItems() throws IOException {
		JSONObject jsonObject = CreatorUBUGradesController.getJSONObjectResponse(new GradereportUserGetGradesTable(COURSE_ID, CONTROLLER.getUser().getId()));
		CreatorGradeItems creatorGradeItems = new CreatorGradeItems();
		List<GradeItem> gradeItems = creatorGradeItems.createGradeItems(COURSE_ID, jsonObject);
		assertFalse(gradeItems.isEmpty());
	}
	@Test
	@Order(6)
	public void getActivityCompletion() throws IOException {
		System.out.println(CONTROLLER.getActualCourse().getEnrolledUsers());
		CreatorUBUGradesController.createActivitiesCompletionStatus(COURSE_ID, CONTROLLER.getActualCourse().getEnrolledUsers());
	}
	
	@Test
	@Order(7)
	public void getLogs() throws IOException {
		DownloadLogController downloadLogController = LogCreator.download();

		Response response = downloadLogController.downloadLog(true);
	
		Logs logs = new Logs(downloadLogController.getServerTimeZone());
		LogCreator.parserResponse(logs, response);
		CONTROLLER.getActualCourse().setLogs(logs);
	}

	@Test
	@Order(9)
	public void modQuizGetUserAttempts() throws IOException {
		
		
		
		List<Integer> userids = getEnrolledUsers().stream().map(EnrolledUser::getId).collect(Collectors.toList());
		
		
		List<Integer> quizzesids = new ArrayList<>(); 
		ModQuizGetQuizzesByCourses modQuizGetQuizzesByCourses = new ModQuizGetQuizzesByCourses(COURSE_ID);
		
		JSONArray jsonArray = new JSONObject(webService.getResponse(modQuizGetQuizzesByCourses).body().string()).getJSONArray("quizzes");
		for(int i = 0; i< jsonArray.length();++i) {
			quizzesids.add(jsonArray.getJSONObject(i).getInt("id"));
		}
		
		ModQuizGetUserAttempts modQuizGetUserAttempts = new ModQuizGetUserAttempts();
		for(Integer userid: userids) {
			for(Integer quizid : quizzesids) {
				
				modQuizGetUserAttempts.setQuizid(quizid);
				modQuizGetUserAttempts.setUserid(userid);
				Response response = webService.getResponse(modQuizGetUserAttempts);
				String value  = response.body().string();
				//System.out.println(response.request().url());
				//System.out.println(value);
			}
		}
		
	}
	@Test
	@Order(10)
	public void toolMobileCallExternalFunctions() throws IOException {
		ToolMobileCallExternalFunctions toolMobileCallExternalFunctions = new ToolMobileCallExternalFunctions();
		
		
		List<Integer> userids = getEnrolledUsers().stream().map(EnrolledUser::getId).collect(Collectors.toList());
		
		
		List<Integer> quizzesids = new ArrayList<>(); 
		ModQuizGetQuizzesByCourses modQuizGetQuizzesByCourses = new ModQuizGetQuizzesByCourses(COURSE_ID);
		
		JSONArray jsonArray = new JSONObject(webService.getResponse(modQuizGetQuizzesByCourses).body().string()).getJSONArray("quizzes");
		for(int i = 0; i< jsonArray.length();++i) {
			quizzesids.add(jsonArray.getJSONObject(i).getInt("id"));
		}
		
		for(Integer quizid: quizzesids) {
			for(Integer userid : userids) {
				ModQuizGetUserAttempts modQuizGetUserAttempts = new ModQuizGetUserAttempts();
				modQuizGetUserAttempts.setQuizid(quizid);
				modQuizGetUserAttempts.setUserid(userid);
				toolMobileCallExternalFunctions.addFunction(modQuizGetUserAttempts);
			}
		}
		Response response = webService.getResponse(toolMobileCallExternalFunctions);
		String value  = response.body().string();
//		System.out.println(response.request().url());
//		System.out.println(value);
	}
	

}
