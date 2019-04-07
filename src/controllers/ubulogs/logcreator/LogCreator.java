package controllers.ubulogs.logcreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.ubulogs.logcreator.logtypes.ReferencesLog;
import model.LogLine;

public class LogCreator {
	static final Logger logger = LoggerFactory.getLogger(LogCreator.class);

	private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");

	public static final String TIME = "﻿Time";
	public static final String USER_FULL_NAME = "User full name";
	public static final String AFFECTED_USER = "Affected user";
	public static final String EVENT_CONTEXT = "Event context";
	public static final String COMPONENT = "Component";
	public static final String EVENT_NAME = "Event name";
	public static final String DESCRIPTION = "Description";
	public static final String ORIGIN = "Origin";
	public static final String IP_ADRESS = "IP address";

	public static List<LogLine> createLogs(List<Map<String, String>> allLogs) {
		List<LogLine> logs = new ArrayList<LogLine>();
		for (Map<String, String> log : allLogs) {
			logs.add(createLog(log));
		}
		if (!ReferencesLog.getNotAvaibleComponents().isEmpty()) {
			logger.warn("No disponible el componenente en "+Component.class.getName()+": "
					+ ReferencesLog.getNotAvaibleComponents());
		}
		if (!ReferencesLog.getNotAvaibleEvents().isEmpty()) {
			logger.warn("No disponible los siguientes eventos en "+Event.class.getName()+": "
					+ ReferencesLog.getNotAvaibleEvents());
		}

		return logs;
	}

	public static LogLine createLog(Map<String, String> mapLog) {

		// cogemos el texto de la descripcion y buscamos todos los numeros que hay en la
		// descripcion
		String description = mapLog.get(DESCRIPTION);
		List<Integer> ids = getIdsInDescription(description);

		LogLine log = ReferencesLog.createLogWithBasicAttributes(mapLog);

		ReferencesLog referencesLog = ReferencesLog.getReferenceLog(log.getComponent(), log.getEventName());

		referencesLog.setLogReferencesAttributes(log, ids);

		return log;
	}

	private static List<Integer> getIdsInDescription(String description) {
		Matcher m = INTEGER_PATTERN.matcher(description);
		List<Integer> list = new ArrayList<Integer>();
		while (m.find()) {
			list.add(Integer.parseInt(m.group(0)));
		}
		return list;
	}

}
