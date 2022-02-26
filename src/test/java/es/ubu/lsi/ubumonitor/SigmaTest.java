package es.ubu.lsi.ubumonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import es.ubu.lsi.ubumonitor.sigma.parser.SigmaParser;
import es.ubu.lsi.ubumonitor.sigma.parser.model.Student;

public class SigmaTest {
	public static final String SAMPLE_XLSX_FILE_PATH = "D:\\Users\\34651\\Downloads\\ANONIMIZADO_COMO_CSV_RECORTADO_1000287_CDS10_ListadoFichaAlumnos_22.02.2022.17.48.16.104.xls";

	private static final int NUMBER_STUDENTS = 17;

	@Test
	@Ignore
	public void readLines() throws IOException {
		try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(SAMPLE_XLSX_FILE_PATH),
				StandardCharsets.ISO_8859_1)) {

			String line = bufferedReader.readLine();
			while (line != null) {
				System.out.println(Arrays.asList(line.trim()
						.split("\t")));
				line = bufferedReader.readLine();
			}
		}
	}

	@Test
	public List<Student> SigmaParserTest() throws IOException {
		SigmaParser sigmaParser = new SigmaParser(new File(SAMPLE_XLSX_FILE_PATH));
		List<Student> students = sigmaParser.parse();
		System.out.println(students.get(0));
		assertEquals(NUMBER_STUDENTS, students.size());
		return students;
	}
	
	@Test 
	public void toJSON() throws IOException {
		List<Student> students = SigmaParserTest();
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

}
