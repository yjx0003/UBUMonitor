package controllers.UBULogs.logCreator;

import java.util.HashMap;
import java.util.Map;

public enum Component {
	
	ASSIGNMENT("Assignment"),
	CHOICE("Choice"),
	EXCEL_SPREADSHEET("Excel spreadsheet"),
	FEEDBACK("Feedback"),
	FILE("File"),
	FILE_SUBMISSIONS("File submissions"),
	FOLDER("Folder"),
	FORUM("Forum"),
	GRADE_REPORT("Grade report"),
	LIVE_LOGS("Live logs"),
	LOGS("Logs"),
	OPENDOCUMENT_SPREADSHEET("OpenDocument spreadsheet"),
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
	XML_FILE("XML file");
	
	private String name;
	private static Map<String,Component> map;
	
	Component(String name){
		this.name=name;
	}
	
	
	static {
		map=new HashMap<String,Component>();
		for(Component component:Component.values()) {
			map.put(component.toString(), component);
		}
	}
	public Component get(String name) {
		return map.get(name);
	}
	
	
	@Override
	public String toString() {
		return name;
	}
	
}
