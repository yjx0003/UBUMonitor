package es.ubu.lsi.ubumonitor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Style {
	private static final Map<String, String> STYLES = new LinkedHashMap<>();
	static {
		STYLES.put("Modena", null);
		STYLES.put("Caspian", "/com/sun/javafx/scene/control/skin/caspian/caspian.css");
		STYLES.put("Bootstrap 3", Style.class.getResource("/css/bootstrap3.css").toExternalForm());
	}

	private Style() {

	}
	
	public static Map<String, String> getStyles(){
		return STYLES;
	}

	public static void addStyle(String style, List<String> styles) {
		String path = STYLES.get(style);
		if(path != null) {
			styles.add(path);
		}
	}
}
