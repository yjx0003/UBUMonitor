package es.ubu.lsi.ubumonitor.controllers.ubulogs;

import java.io.IOException;
import java.io.StringReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes.LogTypes;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes.ReferencesLog;
import es.ubu.lsi.ubumonitor.model.Component;
import es.ubu.lsi.ubumonitor.model.Event;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.model.Logs;
import es.ubu.lsi.ubumonitor.model.Origin;

/**
 * Clase encargada de los logs, con metodos encargados de descargar los logs y
 * parsearlo.
 * 
 * @author Yi Peng Ji
 *
 */
public class LogCreator {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogCreator.class);

	private static final Controller CONTROLLER = Controller.getInstance();

	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/MM/yy, kk:mm");

	private static final Set<String> NOT_AVAIBLE_COMPONENTS = new TreeSet<>();
	private static final Set<String> NOT_AVAIBLE_EVENTS = new TreeSet<>();

	// Sin escapar: (['"])(?<idQuote>\d+)\1|[^'"](?<idNoQuote>\d+)[^'"]
	private static final Pattern INTEGER_PATTERN = Pattern
			.compile("(['\"])(?<idQuote>-?\\d+)\\1|[^'\"](?<idNoQuote>\\d+)[^'\"]");

	public static final String TIME = "Time";
	public static final String USER_FULL_NAME = "User full name";
	public static final String AFFECTED_USER = "Affected user";
	public static final String EVENT_CONTEXT = "Event context";
	public static final String COMPONENT = "Component";
	public static final String EVENT_NAME = "Event name";
	public static final String DESCRIPTION = "Description";
	public static final String ORIGIN = "Origin";
	public static final String IP_ADRESS = "IP address";

	/**
	 * Cambia la zona horia del formateador de tiempo
	 * 
	 * @param zoneId zona horaria
	 */
	public static void setDateTimeFormatter(ZoneId zoneId) {
		dateTimeFormatter = dateTimeFormatter.withZone(zoneId);
	}



	public static void parseMultipledays(Logs logs, List<String> dailyLogs) throws IOException {
		for (String dailyLog : dailyLogs) {
			try(CSVParser csvParser = new CSVParser(new StringReader(dailyLog),
					CSVFormat.DEFAULT.withFirstRecordAsHeader())){
				List<LogLine> logList = LogCreator.createLogs(csvParser);
				logs.addAll(logList);
			} 
			

		}
	}

	public static List<String> downloadMultipleDays(Logs logs) {
		ZoneId userZoneDateTime = "99".equals(CONTROLLER.getUser().getTimezone()) ? logs.getZoneId()
				: ZoneId.of(CONTROLLER.getUser().getTimezone());
		LOGGER.info("Zona horaria del usuario: {}", userZoneDateTime);

		DownloadLogController download = new DownloadLogController(CONTROLLER.getUrlHost().toString(),
				CONTROLLER.getActualCourse().getId(), userZoneDateTime);

		setDateTimeFormatter(download.getUserTimeZone());

		ZonedDateTime lastDateTime = logs.getLastDatetime();

		LOGGER.info("La fecha del ultimo log antes de actualizar es {}", lastDateTime);

		return download.downloadLog(lastDateTime,
				ZonedDateTime.now().withZoneSameInstant(lastDateTime.getZone()));

	}



	public static Logs parseLog(String log, ZoneId serverTimeZone) throws IOException {
		try (CSVParser csvParser = new CSVParser(new StringReader(log),
				CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
			List<LogLine> logList = LogCreator.createLogs(csvParser);
			return new Logs(serverTimeZone, logList); // lo guardamos con la zona horaria del servidor.
		}
	}



	public static DownloadLogController download() throws IOException {
		DownloadLogController download = new DownloadLogController(CONTROLLER.getUrlHost().toString(),
				CONTROLLER.getActualCourse().getId(), CONTROLLER.getUser().getTimezone());

		setDateTimeFormatter(download.getUserTimeZone());
		return download;
	}

	/**
	 * Crea todos los logs la lista
	 * 
	 * @param allLogs los logs listados en mapas con clave la columna del logline
	 * @return los logs creados
	 */
	public static List<LogLine> createLogs(CSVParser csvParser) {
		LinkedList<LogLine> logs = new LinkedList<>();
		Set<String> headers = csvParser.getHeaderMap().keySet();
		LOGGER.info("Los nombres de las columnas del csv son: {}", headers);
		for (CSVRecord csvRecord : csvParser) {
			logs.addFirst(createLog(headers, csvRecord));
		}

		return logs;
	}

	/**
	 * Crea un log y añade los atributos de las columnas y a que van asociado
	 * (usuario, course module etc)
	 * 
	 * @param mapLog mapa con clave las columnas de la linea de log
	 * @return logline con atributos
	 */
	public static LogLine createLog(Set<String> headers, CSVRecord csvRecord) {

		LogLine log = new LogLine();
		Component component;
		if (headers.contains(LogCreator.COMPONENT)) {
			component = Component.get(csvRecord.get(LogCreator.COMPONENT));
		} else {
			component = Component.COMPONENT_NOT_AVAILABLE;
		}
		log.setComponent(component);

		Event event;
		if (headers.contains(LogCreator.EVENT_NAME)) {
			event = Event.get(csvRecord.get(LogCreator.EVENT_NAME));
		} else {
			event = Event.EVENT_NOT_AVAILABLE;
		}
		log.setEventName(event);

		if (headers.contains(LogCreator.DESCRIPTION)) {
			String description = csvRecord.get(LogCreator.DESCRIPTION);
			List<Integer> ids = getIdsInDescription(description);
			try {
				ReferencesLog referencesLog = LogTypes.getReferenceLog(component, event);
				referencesLog.setLogReferencesAttributes(log, ids);
			} catch (Exception e) {
				LOGGER.error("Problema en linea de log: " + csvRecord + " usando el gestor: con los ids:" + ids, e);
			}
		}

		if (headers.contains(LogCreator.TIME)) {
			String time = csvRecord.get(LogCreator.TIME);
			ZonedDateTime zdt = ZonedDateTime.parse(time, dateTimeFormatter);
			log.setTime(zdt);
		}

		if (headers.contains(LogCreator.ORIGIN)) {
			log.setOrigin(Origin.get(csvRecord.get(LogCreator.ORIGIN)));

		}

		if (headers.contains(LogCreator.IP_ADRESS)) {
			log.setIPAdress(csvRecord.get(LogCreator.IP_ADRESS));
		}

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
	 * @param description de la columna decripción
	 * @return lista de integer encontrado en la descripción
	 */
	private static List<Integer> getIdsInDescription(String description) {
		Matcher m = INTEGER_PATTERN.matcher(description);
		List<Integer> list = new ArrayList<>();
		while (m.find()) {
			String integer = null;
			if (m.group("idQuote") != null) { // si el id esta entre comillas simples o dobles
				integer = m.group("idQuote");
			} else if (m.group("idNoQuote") != null) { // si el id no esta entre comillas
				integer = m.group("idNoQuote");
			}

			if (integer != null) {
				list.add(Integer.parseInt(integer));
			}
		}
		return list;
	}

	private LogCreator() {
		throw new UnsupportedOperationException();
	}

}
