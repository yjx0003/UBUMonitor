package controllers.ubulogs.logcreator;

import java.io.Reader;
import java.io.StringReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
import model.Component;
import model.Event;
import model.LogLine;
import model.Logs;
import model.Origin;

public class LogCreator {
	static final Logger logger = LoggerFactory.getLogger(LogCreator.class);

	private final static Controller CONTROLLER = Controller.getInstance();

	public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/MM/yy, kk:mm");

	private static final Set<String> NOT_AVAIBLE_COMPONENTS = new TreeSet<>();
	private static final Set<String> NOT_AVAIBLE_EVENTS = new TreeSet<>();

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

	public static void setZoneId(ZoneId zoneId) {
		dateTimeFormatter = dateTimeFormatter.withZone(zoneId);
	}

	/**
	 * Actualiza el log del curso descargando diariamente.
	 * @param logs
	 * @throws Exception
	 */
	public static void updateCourseLog(Logs logs) throws Exception {

		DownloadLogController download = new DownloadLogController(CONTROLLER.getHost(), CONTROLLER.getUsername(),
				CONTROLLER.getPassword(), CONTROLLER.getActualCourse().getId(), CONTROLLER.getUser().getTimezone(),
				CONTROLLER.getCookies());

		setZoneId(download.getUserTimeZone());

		ZonedDateTime lastDateTime = logs.getLastDatetime();

		List<String> dailyLogs = download.downloadLog(lastDateTime,
				ZonedDateTime.now().withZoneSameInstant(lastDateTime.getZone()));

		for (String dailyLog : dailyLogs) {
			List<Map<String, String>> parsedLog = LogCreator.parse(new StringReader(dailyLog));
			List<LogLine> logList = LogCreator.createLogs(parsedLog);
			Collections.reverse(logList);
			logs.addAll(logList);

		}

	}

	/**
	 * Crea el log del curso con zona horaria del servidor
	 * @return el log del server
	 * @throws Exception si ha habido un problema al crearlo
	 */
	public static Logs createCourseLog() throws Exception {
		DownloadLogController download = new DownloadLogController(CONTROLLER.getHost(), CONTROLLER.getUsername(),
				CONTROLLER.getPassword(), CONTROLLER.getActualCourse().getId(), CONTROLLER.getUser().getTimezone(),
				CONTROLLER.getCookies());

		setZoneId(download.getUserTimeZone());

		List<Map<String, String>> parsedLog = LogCreator.parse(new StringReader(download.downloadLog()));
		List<LogLine> logList = LogCreator.createLogs(parsedLog);
		Collections.reverse(logList);
		Logs logs = new Logs(download.getServerTimeZone(), logList); // lo guardamos con la zona horaria del servidor.

		return logs;
	}

	/**
	 * Parsea el contenido del CSV y convirtiendolo en una lista de mapas
	 * @param reader reader con el csv 
	 * @return lista de mapas con clave de mapa los nombres de las columnas
	 * @throws Exception si ha habido un problema al parsear el csv
	 */
	public static List<Map<String, String>> parse(Reader reader) throws Exception {

		try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

			List<Map<String, String>> logs = new ArrayList<Map<String, String>>();
			Set<String> headers = csvParser.getHeaderMap().keySet();

			logger.info("Las columnas del csv son: " + headers);

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
			throw e;
		}
	}

	/**
	 * Crea todos los logs  la lista
	 * @param allLogs los logs listados en mapas con clave la columna del logline
	 * @return
	 */
	public static List<LogLine> createLogs(List<Map<String, String>> allLogs) {
		List<LogLine> logs = new ArrayList<LogLine>();
		for (Map<String, String> log : allLogs) {
			logs.add(createLog(log));
		}
		if (!NOT_AVAIBLE_COMPONENTS.isEmpty()) {
			logger.warn("No disponible el componenente en " + Component.class.getName() + ": "
					+ NOT_AVAIBLE_COMPONENTS);
		}
		if (!NOT_AVAIBLE_EVENTS.isEmpty()) {
			logger.warn("No disponible los siguientes eventos en " + Event.class.getName() + ": "
					+ NOT_AVAIBLE_EVENTS);
		}

		return logs;
	}

	/**
	 * Crea un log y añade los atributos de las columnas y a que van asociado (usuario, course module etc)
	 * @param mapLog mapa con clave las columnas de la linea de log
	 * @return logline con atributos
	 */
	public static LogLine createLog(Map<String, String> mapLog) {

		String description = mapLog.get(DESCRIPTION);
		List<Integer> ids = getIdsInDescription(description);

		LogLine log = new LogLine();
		String time = mapLog.get(LogCreator.TIME);
		ZonedDateTime zdt = ZonedDateTime.parse(time, dateTimeFormatter);
		log.setTime(zdt);

		Component component = Component.get(mapLog.get(LogCreator.COMPONENT));
		if (component == Component.COMPONENT_NOT_AVAILABLE) {
			NOT_AVAIBLE_COMPONENTS.add(mapLog.get(LogCreator.COMPONENT));

		}

		Event event = Event.get(mapLog.get(LogCreator.EVENT_NAME));
		if (event == Event.EVENT_NOT_AVAILABLE) {
			NOT_AVAIBLE_EVENTS.add(mapLog.get(LogCreator.EVENT_NAME));

		}

		log.setComponent(component);
		log.setEventName(event);
		log.setOrigin(Origin.get(mapLog.get(LogCreator.ORIGIN)));
		log.setIPAdress(mapLog.get(LogCreator.IP_ADRESS));

		ReferencesLog referencesLog = ReferencesLog.getReferenceLog(component, event);

		referencesLog.setLogReferencesAttributes(log, ids);

		return log;
	}

	/**
	 * Devuelve los componentes que no existen en el enum {@link model.Component} al
	 * parsear logs
	 * 
	 * @return los componentes que no existen en el enum {@link model.Component}
	 */
	public static Set<String> getNotAvaibleComponents() {
		return NOT_AVAIBLE_COMPONENTS;
	}

	/**
	 * Devuelve los eventos que no existen en el enum {@link model.Event} al parsear
	 * los logs
	 * 
	 * @return eventos que no existen en el enum {@link model.Event}
	 */
	public static Set<String> getNotAvaibleEvents() {
		return NOT_AVAIBLE_EVENTS;
	}

	/**
	 * Busca los integer de la Descripción de una linea de log
	 * 
	 * @param description
	 *            de la columna decripción
	 * @return lista de integer encontrado en la descripción
	 */
	private static List<Integer> getIdsInDescription(String description) {
		Matcher m = INTEGER_PATTERN.matcher(description);
		List<Integer> list = new ArrayList<Integer>();
		while (m.find()) {
			list.add(Integer.parseInt(m.group(0)));
		}
		return list;
	}

}
