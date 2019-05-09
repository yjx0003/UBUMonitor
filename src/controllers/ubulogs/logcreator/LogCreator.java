package controllers.ubulogs.logcreator;

import java.io.Reader;
import java.io.StringReader;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import controllers.ubulogs.DownloadLogController;
import controllers.ubulogs.logcreator.logtypes.ReferencesLog;
import model.LogLine;
import model.Logs;

public class LogCreator {
	static final Logger logger = LoggerFactory.getLogger(LogCreator.class);
	
	private final static Controller CONTROLLER = Controller.getInstance();

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

	
	public static void updateCourseLog(Logs logs) throws Exception {
		 

		DownloadLogController download = new DownloadLogController(CONTROLLER.getHost(), CONTROLLER.getUsername(),
				CONTROLLER.getPassword(), CONTROLLER.getActualCourse().getId(), CONTROLLER.getUser().getTimezone());
		
		ReferencesLog.setZoneId(download.getTimezone());
		ZonedDateTime lastDateTime=logs.getLastDatetime();
		List<String> dailyLogs = download.downloadLog(lastDateTime, ZonedDateTime.now());
		for (String dailyLog : dailyLogs) {
			List<Map<String, String>> parsedLog = LogCreator.parse(new StringReader(dailyLog));
			List<LogLine> logList = LogCreator.createLogs(parsedLog);
			Collections.reverse(logList);
			logs.addAll(logList);

		}
	

	}

	public static Logs createCourseLog() throws Exception {
		DownloadLogController download = new DownloadLogController(CONTROLLER.getHost(), CONTROLLER.getUsername(),
				CONTROLLER.getPassword(), CONTROLLER.getActualCourse().getId(), CONTROLLER.getUser().getTimezone());
		
		ReferencesLog.setZoneId(download.getTimezone());
		
		List<Map<String, String>> parsedLog = LogCreator.parse(new StringReader(download.downloadLog()));
		List<LogLine> logList = LogCreator.createLogs(parsedLog);
		Collections.reverse(logList);
		Logs logs = new Logs(CONTROLLER.getUser().getTimezone(), download.getTimezone(), logList);
		
		return logs;
	}
	
	
	
	public static List<Map<String, String>> parse(Reader reader) {

		try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

			List<Map<String, String>> logs = new ArrayList<Map<String, String>>();
			Set<String> headers = csvParser.getHeaderMap().keySet();

			logger.info("Las columnas el parser del csv son: " + headers);

			for (CSVRecord csvRecord : csvParser) {

				Map<String, String> log = new HashMap<String, String>();
				logs.add(log);

				for (String header : headers) {
					log.put(header, csvRecord.get(header));
				}

			}
			return logs;

		} catch (Exception e) {
			logger.error("No se ha podido parsear el contenido", e);
		}
		return null;
	}
	
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
