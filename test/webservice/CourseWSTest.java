package webservice;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.junit.Test;

import model.Course;
import model.EnrolledUser;
import model.GradeReportLine;
import model.Role;

public class CourseWSTest {

	private final String host = "http://localhost";
	private final String token = "1";
	private final int userId = 1;
	private final int courseId = 1;
	private Course course = new Course();

	@Test
	public void setEnrolledUserTest() {
		
		course.setId(1);
		
		try {
			CourseWS.setEnrolledUsers(host, token, course);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(2, course.getEnrolledUsersCount());
		List<EnrolledUser> users = course.getEnrolledUsers();
		EnrolledUser user = users.get(0);
		assertEquals("Luis", user.getFirstName());
		assertEquals("Garcia", user.getLastName());
		assertEquals("Luis Garcia", user.getFullName());
		List<Role> roles = user.getRoles();
		assertEquals(3, roles.size());
		assertEquals("Manager", roles.get(0).getName());
		assertEquals("Teacher", roles.get(1).getName());
		assertEquals("Student", roles.get(2).getName());
		
		user = users.get(1);
		assertEquals("Pepe", user.getFirstName());
		assertEquals("Garcia", user.getLastName());
		assertEquals("Pepe Garcia", user.getFullName());
		roles = user.getRoles();
		assertEquals(1, roles.size());
		assertEquals("Student", roles.get(0).getName());
		
		List<String> groups = course.getGroups();
		assertEquals(2,groups.size());
		assertEquals("0002", groups.get(0));
		assertEquals("0001", groups.get(1));	
	}
	
	@Test
	public void setGradeReportLinesTest() {
		try {
			CourseWS.setGradeReportLines(host, token, userId, course);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<GradeReportLine> grlList = course.getGradeReportLines();
		assertEquals(20, grlList.size());
		
		GradeReportLine grl = grlList.get(0);
		assertEquals("Programación", grl.getName());
		assertEquals("Category", grl.getNameType());
		assertEquals(1, grl.getLevel());
		assertEquals("5,00", grl.getGrade());
		assertEquals(0.0, grl.getPercentage(), 0);
		assertEquals(Float.NaN, grl.getWeight(), 0);
		assertEquals("10", grl.getRangeMax());
		assertEquals("0", grl.getRangeMin());

		List<GradeReportLine> children = grl.getChildren();
		assertEquals(11, children.size());
		String childrenList = "[Practicas, Teoría, Tipo texto, Rango 10-20, Tipo escala, Mas escalas,"
				+ " Taller 1 (envío), Taller 1 (evaluación), Leccion Tema 1, Tarea con escala, Tarea con escala 2]";
		assertEquals(childrenList, children.toString());
		
		grl = grlList.get(12);
		assertEquals("Rango 10-20", grl.getName());
		assertEquals("ManualItem", grl.getNameType());
		assertEquals(2, grl.getLevel());
		assertEquals("15,60", grl.getGrade());
		assertEquals(Float.NaN, grl.getPercentage(), 0);
		assertEquals(Float.NaN, grl.getWeight(), 0);
		assertEquals("20", grl.getRangeMax());
		assertEquals("10", grl.getRangeMin());
		assertEquals("5.6000004", grl.getGradeAdjustedTo10());

		children = grl.getChildren();
		assertEquals(0, children.size());	
		
		grl = grlList.get(18);
		assertEquals("Tarea con escala", grl.getName());
		assertEquals("Assignment", grl.getNameType());
		assertEquals(2, grl.getLevel());
		assertEquals("Yes", grl.getGrade());
		assertEquals(33.33, grl.getPercentage(), 0.10);
		assertEquals(Float.NaN, grl.getWeight(), 0);
		assertEquals("Yes", grl.getRangeMax());
		assertEquals("No", grl.getRangeMin());
		assertEquals("10.0", grl.getGradeWithScale(course));	
		
		children = grl.getChildren();
		assertEquals(0, children.size());	
	}
	
	@Test
	public void getUserGradeReportLinesTest() {
		List<GradeReportLine> grlList = null;
		try {
			grlList = CourseWS.getUserGradeReportLines(host, token, userId, courseId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(20, grlList.size());
		
		GradeReportLine grl = grlList.get(19);
		assertEquals("Programación", grl.getName());
		assertEquals("Category", grl.getNameType());
		assertEquals(1, grl.getLevel());
		assertEquals("5,00", grl.getGrade());
		assertEquals(50.0, grl.getPercentage(), 0);
		assertEquals(Float.NaN, grl.getWeight(), 0);
		assertEquals("10", grl.getRangeMax());
		assertEquals("0", grl.getRangeMin());
		
		grl = grlList.get(11);
		assertEquals("Rango 10-20", grl.getName());
		assertEquals("ManualItem", grl.getNameType());
		assertEquals(2, grl.getLevel());
		assertEquals("15,60", grl.getGrade());
		assertEquals(Float.NaN, grl.getPercentage(), 0);
		assertEquals(Float.NaN, grl.getWeight(), 0);
		assertEquals("20", grl.getRangeMax());
		assertEquals("10", grl.getRangeMin());
		assertEquals("5.6000004", grl.getGradeAdjustedTo10());	
	}

}
