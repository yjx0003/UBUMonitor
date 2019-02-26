package controllers.UBULogs.logCreator.logsTypes;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import controllers.UBULogs.logCreator.LogCreator;
import model.Course;
import model.Log;

public abstract class ReferencesLog {
	protected static Course course;
	protected static DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yy, kk:mm");

	public static void setCourse(Course course) {
		ReferencesLog.course = course;
	}

	public static void setZoneId(ZoneId zoneId) {
		ReferencesLog.dateTimeFormatter = dateTimeFormatter.withZone(zoneId);
	}

	public Log createLogWithAttributes(Map<String, String> mapLog, List<Integer> ids) {

		Log log = new Log();
		if (mapLog.containsKey(LogCreator.TIME)) {
			log.setTime(ZonedDateTime.parse(mapLog.get(LogCreator.TIME), dateTimeFormatter));
		}

		log.setUserFullName(mapLog.get(LogCreator.USER_FULL_NAME));

		log.setAffectedUser(mapLog.get(LogCreator.AFFECTED_USER));

		log.setComponent(mapLog.get(LogCreator.COMPONENT));
		log.setEventName(mapLog.get(LogCreator.EVENT_NAME));
		log.setEventContext(mapLog.get(LogCreator.EVENT_CONTEXT));
		log.setDescription(mapLog.get(LogCreator.DESCRIPTION));
		log.setOrigin(mapLog.get(LogCreator.ORIGIN));
		log.setIPAdress(mapLog.get(LogCreator.IP_ADRESS));

		setLogReferencesAttributes(log, ids);
		return log;
	}

	protected abstract void setLogReferencesAttributes(Log log, List<Integer> ids);
}
