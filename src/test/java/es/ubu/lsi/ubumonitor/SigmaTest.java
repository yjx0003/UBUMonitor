package es.ubu.lsi.ubumonitor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import es.ubu.lsi.ubumonitor.sigma.controller.SigmaParser;
import es.ubu.lsi.ubumonitor.sigma.model.Province;
import es.ubu.lsi.ubumonitor.sigma.model.Student;
import es.ubu.lsi.ubumonitor.view.chart.sigma.SigmaBoxplot;
import es.ubu.lsi.ubumonitor.view.chart.sigma.SigmaUsualAddressMap;

public class SigmaTest {
	public static final String SAMPLE_XLSX_FILE_PATH = "D:\\Users\\34651\\Downloads\\ANONIMIZADO_COMO_CSV_RECORTADO_1000287_CDS10_ListadoFichaAlumnos_22.02.2022.17.48.16.104.xls";

	private static final int NUMBER_STUDENTS = 17;
	private static List<Student> students;
	
	@BeforeAll
	public static void beforeAll() throws IOException {
		SigmaParser sigmaParser = new SigmaParser();
		students = sigmaParser.parse(new File(SAMPLE_XLSX_FILE_PATH));
	}

	@Test
	public void sizeTest() throws IOException {
		
		
		assertEquals(NUMBER_STUDENTS, students.size());
		
	}
	
	@Test 
	public void toJSON() throws IOException {
		JSONArray studentsJSON = new JSONArray(students);
		 //Write JSON file
        try (FileWriter file = new FileWriter("./test/students.json")) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(studentsJSON.toString(4)); 
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	@Test
	public void sortTest() {
		
		List<Student> students = new ArrayList<>(); 
		generateStudent(students, "c");
		generateStudent(students, "b");
		generateStudent(students, "a");
		generateStudent(students, "d");
		generateStudent(students, "e");
		generateStudent(students, "e");
		generateStudent(students, "b");
		generateStudent(students, "b");
		
		List<String> routes = SigmaBoxplot.getUniqueRouteAccess(students, 2);
		assertEquals(2, routes.size());
		assertEquals("b", routes.get(0));
		assertEquals("e", routes.get(1));
	}
	
	private void generateStudent(List<Student> students, String routeAccess) {
		Student student = new Student();
		student.setRouteAccess(routeAccess);
		students.add(student);
	}
	
	@Test
	public void usualZipCodeTest() {
		
		
		for(Student student: students) {
			String zipCode = SigmaUsualAddressMap.getProviceZipCode(student.getUsualAddress());
			int intValue = Integer.valueOf(zipCode);
			assertTrue( intValue >= 0 && intValue<= 52,"Error fuera de rango: "+intValue +"\nEstudiante: " + student.getFullName());
		}
		
	}
	
	@Test
	public void courseZipCodeTest() {
		
		
		for(Student student: students) {
			String zipCode = SigmaUsualAddressMap.getProviceZipCode(student.getCourseAddress());
			int intValue = Integer.valueOf(zipCode);
			assertTrue( intValue >= 0 && intValue<= 52, "Error fuera de rango: "+intValue +"\nEstudiante: " + student.getFullName());
		}
		
	}
	
	@Test
	public void mapZipCodeTest() {
		
		Map<String, Province> map = Province.getMapZipCode();
		assertEquals(53, map.size());
		
	}

}
