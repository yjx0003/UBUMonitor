package controllers.UBULogs.logCreator;

import java.util.HashMap;
import java.util.Map;

import controllers.UBULogs.logCreator.logsTypes.CourseViewedLog;
import controllers.UBULogs.logCreator.logsTypes.ReferencesLog;

public class LogTypes {

	public static final String COURSE_VIEWED = "The user with id '' viewed the log report for the course with id ''.";

	private static Map<String, ReferencesLog> descriptionLog;
	static {
		descriptionLog = new HashMap<String, ReferencesLog>();
		descriptionLog.put(COURSE_VIEWED, new CourseViewedLog());
		
	}

	private LogTypes() {

	}

	public static ReferencesLog getReferenceLog(String key) {
		return descriptionLog.get(key);
	}

}
