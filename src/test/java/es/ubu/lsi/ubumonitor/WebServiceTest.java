package es.ubu.lsi.ubumonitor;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import es.ubu.lsi.ubumonitor.controllers.AppInfo;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.ubugrades.CreatorUBUGradesController;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.MoodleUser;

@TestMethodOrder(OrderAnnotation.class)
public class WebServiceTest {

	private static final String USERNAME = "teacher";
	private static final String PASSWORD = "moodle";
	private static final String HOST = "https://school.moodledemo.net";
	private static final Controller CONTROLLER = Controller.getInstance();

	@Test
	@Order(1)
	public void loginTest() throws IOException {
		System.setProperty("logfile.name", ".log/"+AppInfo.APPLICATION_NAME_WITH_VERSION+"_Test.log");
		CONTROLLER.tryLogin(HOST, USERNAME, PASSWORD);
		CONTROLLER.setDataBase(new DataBase());
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
		assertTrue(!courses.isEmpty());
	}

}
