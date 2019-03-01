package controllers.UBULogs.logCreator.logsTypes;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.UBULogs.logCreator.Component;
import controllers.UBULogs.logCreator.ComponentEventKey;
import controllers.UBULogs.logCreator.EventName;
import controllers.UBULogs.logCreator.LogCreator;
import model.Course;
import model.Log;

public abstract class ReferencesLog {
	protected static Course course;
	protected static DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yy, kk:mm");
	
	
	private static Map<ComponentEventKey, ReferencesLog> logTypes;
	
	static {
		logTypes = new HashMap<ComponentEventKey, ReferencesLog>();
		logTypes.put(new ComponentEventKey(Component.SYSTEM, EventName.COURSE_VIEWED), new UserCourse());
		logTypes.put(new ComponentEventKey(Component.SYSTEM, EventName.COURSE_MODULE_VIEWED), new UserCmid());
	}

	public static ReferencesLog getReferenceLog(Component component,EventName eventName) {
		return logTypes.get(new ComponentEventKey(component,eventName));
	}

	public static void setCourse(Course course) {
		ReferencesLog.course = course;
	}

	public static void setZoneId(ZoneId zoneId) {
		ReferencesLog.dateTimeFormatter = dateTimeFormatter.withZone(zoneId);
	}

	public static Log createLogWithAttributes(Map<String, String> mapLog) {

		Log log = new Log();
		if (mapLog.containsKey(LogCreator.TIME)) {
			log.setTime(ZonedDateTime.parse(mapLog.get(LogCreator.TIME), dateTimeFormatter));
		}

		log.setComponent(Component.get(mapLog.get(LogCreator.COMPONENT)));
		log.setEventName(EventName.get(mapLog.get(LogCreator.EVENT_NAME)));
		log.setEventContext(mapLog.get(LogCreator.EVENT_CONTEXT));
		log.setDescription(mapLog.get(LogCreator.DESCRIPTION));
		log.setOrigin(mapLog.get(LogCreator.ORIGIN));
		log.setIPAdress(mapLog.get(LogCreator.IP_ADRESS));
		
		log.setCourse(course);

		
		return log;
	}
public abstract void setLogReferencesAttributes(Log log, List<Integer> ids);
}
