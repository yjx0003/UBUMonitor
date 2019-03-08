package controllers.ubulogs.logcreator;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import controllers.ubulogs.logcreator.logtypes.ReferencesLog;
import model.Course;
import model.Log;

public class LogCreator {

	
	
	private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");

	
	public static final String TIME = "Time";
	public static final String USER_FULL_NAME = "User full name";
	public static final String AFFECTED_USER = "Affected user";
	public static final String EVENT_CONTEXT = "Event context";
	public static final String COMPONENT = "Component";
	public static final String EVENT_NAME = "Event name";
	public static final String DESCRIPTION = "Description";
	public static final String ORIGIN = "Origin";
	public static final String IP_ADRESS = "IP address";

	

	private LogCreator(Course course, ZoneId zoneId) {

		
		ReferencesLog.setCourse(course);
		ReferencesLog.setZoneId(zoneId);

	}
	


	public void setCourse(Course course) {

		ReferencesLog.setCourse(course);
	}
	
	public void setZoneId(ZoneId zoneId) {
	
		ReferencesLog.setZoneId(zoneId);
	}
	
	
	public List<Log> createLogs(List<Map<String, String>> allLogs) {
		List<Log> logs=new ArrayList<Log>();
		for (Map<String,String> log:allLogs) {
			logs.add(createLog(log));
		}
		return logs;
	}

	public Log createLog(Map<String, String> mapLog) {
		

		
		//cogemos el texto de la descripcion y buscamos todos los numeros que hay en la descripcion
		String description = mapLog.get(DESCRIPTION);
		List<Integer> ids=getIdsInDescription(description);
		
		Log log=ReferencesLog.createLogWithBasicAttributes(mapLog);
		
		ReferencesLog referencesLog=ReferencesLog.getReferenceLog(log.getComponent(), log.getEventName());
		referencesLog.setLogReferencesAttributes(log, ids);
		
		return log;
	}
	
	


	
	private List<Integer> getIdsInDescription(String description){
		Matcher m=INTEGER_PATTERN.matcher(description);
		List<Integer> list=new ArrayList<Integer>();
		while(m.find()) {
			list.add(Integer.parseInt(m.group(0)));
		}
		return list;
	}
	
	
}
