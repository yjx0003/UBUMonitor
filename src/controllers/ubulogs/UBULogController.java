package controllers.ubulogs;

import java.io.StringReader;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import controllers.ubulogs.logcreator.LogCreator;
import controllers.ubulogs.logcreator.logtypes.ReferencesLog;
import model.LogLine;
import model.Logs;

public class UBULogController {
	static final Logger logger = LoggerFactory.getLogger(UBULogController.class);
	private final static Controller CONTROLLER = Controller.getInstance();

	/**
	 * static Singleton instance.
	 */
	private static UBULogController instance;

	/**
	 * Private constructor for singleton.
	 */
	private UBULogController() {
	}

	/**
	 * Return a singleton instance of UBULogController.
	 */
	public static UBULogController getInstance() {
		if (instance == null) {
			instance = new UBULogController();
		}
		return instance;
	}

	public void updateCourseLog(Logs logs) {
	 

		DownloadLogController download = new DownloadLogController(CONTROLLER.getHost(), CONTROLLER.getUsername(),
				CONTROLLER.getPassword(), CONTROLLER.getActualCourse().getId(), CONTROLLER.getUser().getTimezone());
		
		ReferencesLog.setZoneId(download.getTimezone());
		List<String> dailyLogs = download.downloadLog(logs.getLastDatetime(), ZonedDateTime.now());
		for (String dailyLog : dailyLogs) {
			List<Map<String, String>> parsedLog = UBULogParser.parse(new StringReader(dailyLog));
			List<LogLine> logList = LogCreator.createLogs(parsedLog);
			Collections.reverse(logList);
			logs.addAll(logList);

		}
	

	}

	public Logs createCourseLog() {
		DownloadLogController download = new DownloadLogController(CONTROLLER.getHost(), CONTROLLER.getUsername(),
				CONTROLLER.getPassword(), CONTROLLER.getActualCourse().getId(), CONTROLLER.getUser().getTimezone());
		ReferencesLog.setZoneId(download.getTimezone());
		List<Map<String, String>> parsedLog = UBULogParser.parse(new StringReader(download.downloadLog()));
		List<LogLine> logList = LogCreator.createLogs(parsedLog);
		Collections.reverse(logList);
		Logs logs = new Logs(CONTROLLER.getUser().getTimezone(), download.getTimezone(), logList);
		return logs;
	}

}
