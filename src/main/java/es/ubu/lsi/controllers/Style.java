package es.ubu.lsi.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Style {
	public static final Map<String, String> STYLES = new LinkedHashMap<>();
	static {
		STYLES.put("Modena", null);
		STYLES.put("Caspian", "/com/sun/javafx/scene/control/skin/caspian/caspian.css");
		STYLES.put("Bootstrap 3", Style.class.getResource("/css/bootstrap3.css").toExternalForm());
	}

	private Style() {

	}

	public static void addStyle(String style, List<String> styles) {
		String path = STYLES.get(style);
		if(path != null) {
			styles.add(path);
		}
	}
}
