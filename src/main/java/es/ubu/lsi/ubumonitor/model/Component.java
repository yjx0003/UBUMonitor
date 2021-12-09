package es.ubu.lsi.ubumonitor.model;

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
	BIG_BLUE_BUTTON("BigBlueButton"),
	BOOK("Book"),
	BOOK_PRINTING("Book printing"),
	CHAT("Chat"),
	CHOICE("Choice"),
	COMMENTS("Comments"),
	COURSE_COMPLETION("Course completion"),
	COURSE_PARTICIPATION("Course participation"),
	DATABASE("Database"),
	EVENT_MONITOR("Event monitor"),
	EXCEL_SPREADSHEET("Excel spreadsheet"),
	EXTERNAL_TOOL("External tool"),
	FEEDBACK("Feedback"),
	FILE("File"),
	FILE_SUBMISSIONS("File submissions"),
	FOLDER("Folder"),
	FORUM("Forum"),
	GLOSSARY("Glossary"),
	GRADE_HISTORY("Grade history"),
	GRADER_REPORT("Grader report"),
	GUIA_DOCENTE("Guía Docente"),
	H5P("H5P"),
	H5P_PACKAGE("H5P Package"),
	HOTPOT_MODULE("HotPot module"),
	IMS_CONTENT_PACKAGE("IMS content package"),
	JOURNAL("Journal"),
	KALTURA_MEDIA_ASSIGNMENT("Kaltura Media Assignment"),
	KALTURA_VIDEO_RESOURCE("Kaltura Video Resource"),
	LESSON("Lesson"),
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
	SCORM_PACKAGE("SCORM package"),
	SINGLE_VIEW("Single view"),
	STATISTICS("Statistics"),
	SUBMISSION_COMMENTS("Submission comments"),
	SURVEY("Survey"),
	SMOWL("SMOWL"),
	SYSTEM("System"),
	TAB_DISPLAY("Tab display"),
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
		map = new HashMap<>();
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
	
	/**
	 * Devuelve el texto del componente tal y como esta en la tabla de logs.
	 * @return el texto del componente tal y como esta en la tabla de logs
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
	
}
