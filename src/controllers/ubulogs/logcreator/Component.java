package controllers.ubulogs.logcreator;

import java.util.HashMap;
import java.util.Map;

public enum Component {
	
	ACTIVITY_REPORT("Activity report"),
	ASSIGNMENT("Assignment"),
	CHOICE("Choice"),
	EXCEL_SPREADSHEET("Excel spreadsheet"),
	FEEDBACK("Feedback"),
	FILE("File"),
	FILE_SUBMISSIONS("File submissions"),
	FOLDER("Folder"),
	FORUM("Forum"),
	GRADER_REPORT("Grader report"),
	LIVE_LOGS("Live logs"),
	LOGS("Logs"),
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
	USER_REPORT("User report"),
	USER_TOURS("User tours"),
	WORKSHOP("Workshop"),
	XML_FILE("XML file");
	
	private String name;
	private static Map<String,Component> map;
	
	Component(String name){
		this.name=name;
	}
	
	
	static {
		map=new HashMap<String,Component>();
		for(Component component:Component.values()) {
			map.put(component.name, component);
		}
	}
	public static Component get(String name) {
		return map.get(name);
	}
	
	
}
