package es.ubu.lsi.ubumonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.enrollment.EnrollmentBar;

public class UnitTest {

	@Test
	public void enrolledYear() {
		enrolledYear(9, 7, null, null);
		enrolledYear(9, 7, ZonedDateTime.of(2021, 10, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2021); // 03/10/2021
		enrolledYear(9, 7, ZonedDateTime.of(2021, 8, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2021); // 03/08/2021
		enrolledYear(9, 7, ZonedDateTime.of(2021, 7, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2020); // 03/07/2021
		enrolledYear(9, 7, ZonedDateTime.of(2020, 9, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2020); // 03/09/2020
		
		
		enrolledYear(3, 9, ZonedDateTime.of(2021, 1, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2021); // 03/01/2021
		enrolledYear(3, 9, ZonedDateTime.of(2021, 3, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2021); // 03/03/2021
		enrolledYear(3, 9, ZonedDateTime.of(2021, 8, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2021); // 03/08/2021
		enrolledYear(3, 9, ZonedDateTime.of(2021, 9, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2021); // 03/09/2021
		enrolledYear(3, 9, ZonedDateTime.of(2021, 10, 3, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant(), 2022); // 03/10/2021
		
	}
	
	private void enrolledYear(int startMonth, int endMonth, Instant firstAccess, Integer expectedYear) {
		Integer year = EnrollmentBar.getYear(startMonth, endMonth, firstAccess);
		assertEquals(expectedYear, year);
	}
	

	@Test
	public void removeBackSlashTest() {
		Map<String,String> map = new LinkedHashMap<>();
		fillMap(map, "Inglés EPS VENA-curso 2019/20", "Inglés EPS VENA-curso 201920");
		fillMap(map, "Inglés EPS VENA-curso 2019*20", "Inglés EPS VENA-curso 201920");
		fillMap(map, "Inglés EPS VENA-curso 2019\\20", "Inglés EPS VENA-curso 201920");
		fillMap(map, "Inglés EPS VENA-curso 2019?20", "Inglés EPS VENA-curso 201920");
		fillMap(map, "Inglés EPS VENA-curso 2019:20", "Inglés EPS VENA-curso 201920");
		fillMap(map, "Inglés EPS\\ VENA-curso 2019:20", "Inglés EPS VENA-curso 201920");
		fillMap(map, "Inglés EPS ?VENA-curso 2019:20", "Inglés EPS VENA-curso 201920");
		fillMap(map, "Inglés EPS \"VENA-curso 2019:20\"", "Inglés EPS VENA-curso 201920");
		fillMap(map, "Inglés EPS <VENA-curso 2019:20>", "Inglés EPS VENA-curso 201920");
		fillMap(map, "Inglés EPS |VENA-curso 2019:20|", "Inglés EPS VENA-curso 201920");
		for (Map.Entry<String, String> entry : map.entrySet()) {
			assertEquals(entry.getValue(), entry.getKey());
		}

	}
	
	private static void fillMap(Map<String,String> map, String actual, String expected) {
		map.put(UtilMethods.removeReservedChar(actual), expected);
	}
	
	@Test
	public void collatorSpaces() throws UnsupportedEncodingException, FileNotFoundException, IOException, ParseException {

		
		String usuario1 = "de la Fuente, Juan";
		String usuario2 = "Death, Jack";
		List<String> list = Arrays.asList(usuario1,usuario2);
		Locale.setDefault(Locale.forLanguageTag("es-ES"));
		
		
		list.sort(Collator.getInstance());
		assertEquals(list.get(0), usuario2);
		
		
		String rules = ((RuleBasedCollator) Collator.getInstance()).getRules();
		RuleBasedCollator correctedCollator 
		    = new RuleBasedCollator(rules.replaceAll("<'\\u005f'", "<' '<'\\u005f'")); // añadimos el espacio antes del underscore
		list.sort(correctedCollator);
		assertEquals(list.get(0), usuario1);
		
	
	}
}

	

