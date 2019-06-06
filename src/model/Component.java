package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Elementos de la columna de logs Componente.
 * @author Yi Peng Ji
 *
 */
public enum Component {
	
	
	ACTIVITY_REPORT("Activity report"),
	ASSIGNMENT("Assignment"),
	CHAT("Chat"),
	CHOICE("Choice"),
	COURSE_PARTICIPATION("Course participation"),
	EXCEL_SPREADSHEET("Excel spreadsheet"),
	FEEDBACK("Feedback"),
	FILE("File"),
	FILE_SUBMISSIONS("File submissions"),
	FOLDER("Folder"),
	FORUM("Forum"),
	GLOSSARY("Glossary"),
	GRADER_REPORT("Grader report"),
	GUIA_DOCENTE("Guía Docente"),
	HOTPOT_MODULE("HotPot module"),
	LIVE_LOGS("Live logs"),
	LOGS("Logs"),
	ONLINE_TEXT_SUBMISSIONS("Online text submissions"),
	OPENDOCUMENT_SPREADSHEET("OpenDocument spreadsheet"),
	OUTCOMES_REPORT("Outcomes report"),
	OVERVIEW_REPORT("Overview report"),
	PAGE("Page"),
	PLAIN_TEXT_FILE("Plain text file"),
	QUIZ("Quiz"),
	RECYCLE_BIN("Recycle bin"),
	SINGLE_VIEW("Single view"),
	SUBMISSION_COMMENTS("Submission comments"),
	SYSTEM("System"),
	TURNITIN_ASSIGNMENT_2("Turnitin Assignment 2"),
	URL("URL"),
	USER_REPORT("User report"),
	USER_TOURS("User tours"),
	WIKI("Wiki"),
	WORKSHOP("Workshop"),
	XML_FILE("XML file"),
	COMPONENT_NOT_AVAILABLE("Component not avaible");


	
	private String name;
	private static Map<String, Component> map;

	private Component(String name) {
		this.name = name;
	}

	static {
		map = new HashMap<String, Component>();
		for (Component component : Component.values()) {
			map.put(component.name, component);
		}
	}

	/**
	 * Devuelve el componente a partir del String, si no existe devuelve {@link Component#COMPONENT_NOT_AVAILABLE}
	 * @param name string del componente
	 * @return el componente a partir del String, si no existe {@link Component#COMPONENT_NOT_AVAILABLE}
	 */
	public static Component get(String name) {
		return map.getOrDefault(name, Component.COMPONENT_NOT_AVAILABLE);
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
}
