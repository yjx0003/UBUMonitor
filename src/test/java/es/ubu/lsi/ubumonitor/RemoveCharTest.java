package es.ubu.lsi.ubumonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import es.ubu.lsi.ubumonitor.util.UtilMethods;

public class RemoveCharTest {

	private static Map<String, String> map;

	private static void fillMap(String actual, String expected) {
		map.put(UtilMethods.removeReservedChar(actual), expected);
	}

	@BeforeAll
	public static void testUp() {
		map = new LinkedHashMap<>();
		fillMap("Inglés EPS VENA-curso 2019/20", "Inglés EPS VENA-curso 201920");
		fillMap("Inglés EPS VENA-curso 2019*20", "Inglés EPS VENA-curso 201920");
		fillMap("Inglés EPS VENA-curso 2019\\20", "Inglés EPS VENA-curso 201920");
		fillMap("Inglés EPS VENA-curso 2019?20", "Inglés EPS VENA-curso 201920");
		fillMap("Inglés EPS VENA-curso 2019:20", "Inglés EPS VENA-curso 201920");
		fillMap("Inglés EPS\\ VENA-curso 2019:20", "Inglés EPS VENA-curso 201920");
		fillMap("Inglés EPS ?VENA-curso 2019:20", "Inglés EPS VENA-curso 201920");
		

	}

	@Test
	public void removeBackSlashTest() {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			assertEquals(entry.getValue(), entry.getKey());
		}

	}
}
