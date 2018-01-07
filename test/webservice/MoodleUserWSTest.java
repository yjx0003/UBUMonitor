package webservice;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.junit.Test;

import javafx.embed.swing.JFXPanel;
import model.Course;
import model.MoodleUser;

public class MoodleUserWSTest {

	private final String host = "http://localhost";
	private final String token = "1";
	private final String eMail = "prueba@prueba.com";
	private MoodleUser mUser = new MoodleUser();

	@Test
	public void setMoodleUserTest() {
		try {
			// Inicializamos JavaFX
			@SuppressWarnings("unused")
			JFXPanel jfxPanel = new JFXPanel();
			
			MoodleUserWS.setMoodleUser(host, token, eMail, mUser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals("pega", mUser.getUserName());
		assertEquals("Pepe Garcia", mUser.getFullName());
		assertEquals("prueba@prueba.com", mUser.getEmail());
		assertEquals(1507054556, mUser.getFirstAccess().getTime());
		assertEquals(1515151537, mUser.getLastAccess().getTime());
	}
	
	@Test
	public void setCoursesTest() {
		try {
			// Inicializamos JavaFX
			@SuppressWarnings("unused")
			JFXPanel jfxPanel = new JFXPanel();
			
			MoodleUserWS.setCourses(host, token, mUser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Course> courses = mUser.getCourses();
		assertEquals(2, courses.size());
		
		Course course = courses.get(0);
		assertEquals("SisOp" , course.getShortName());
		assertEquals("Sistemas Operativos" , course.getFullName());
		assertEquals(7 , course.getEnrolledUsersCount());
		
		course = courses.get(1);
		assertEquals("Prog" , course.getShortName());
		assertEquals("Programacion" , course.getFullName());
		assertEquals(7 , course.getEnrolledUsersCount());
	}

}
