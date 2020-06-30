package es.ubu.lsi.ubumonitor.export.builder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.export.CSVBuilderAbstract;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * Builds the log file.
 * 
 * @author Raúl Marticorena
 * @since 2.4.0.0
 */
public class CSVLog extends CSVBuilderAbstract {

	/** Logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVLog.class);

	/** Header. */
	private static final String[] HEADER = new String[] { "Time", "Component", "EventName", "UserId", "AffectedUserId",
			"CourseModule", "Origin", "IP", "Hour", "AmPm", "DayOfWeek", "DayOfWeekName", "DayOfMonth", "DayOfYear", "WeekOfYear",
			"Month", "MonthName", "YearQuarter", "YearMonth", "YearWeek", "Year" };

	/**
	 * Constructor.
	 * 
	 * @param name name
	 * @param dataBase dataBase
	 */
	public CSVLog(String name, DataBase dataBase) {
		super(name, dataBase, HEADER);
	}

	@Override
	public void buildBody() {
		List<LogLine> logs = Controller.getInstance().getActualCourse().getLogs().getList();
		// Load data
		String userId;
		String affectedUserId;
		String courseModule;
		String ip;
		
		for (LogLine log : logs) {
			// Preprocess data
			userId = log.getUser() != null ? Integer.toString(log.getUser().getId()) : "";
			affectedUserId = log.getAffectedUser() != null ? Integer.toString(log.getAffectedUser().getId()) : "";
			courseModule = log.getCourseModule() != null ? Integer.toString(log.getCourseModule().getCmid()) : "";
			ip = log.getIPAdress() != null ? log.getIPAdress() : "";
			// Add data
			
			LOGGER.debug("Data line: {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}",
					log.getTime().toLocalDateTime(),
					log.getComponent().getName(),
					log.getEventName().getName(), 
					userId, affectedUserId, courseModule,
					log.getOrigin(),
					ip,
					log.getHour(),
					log.getAmPm(), 
					log.getDayOfWeek().getValue(),
					log.getDayOfWeek(),
					log.getDayOfMonth(),
					log.getDayOfYear(),
					log.getWeekOfYear(),
					log.getMonth().getValue(),
					log.getMonth(),
					log.getYearQuarter(),
					log.getYearMonth(),
					log.getYearWeek(),
					log.getYear());

			getData().add(new String[] { log.getTime().toLocalDateTime().toString(),
					log.getComponent().getName(),
					log.getEventName().getName(), 
					userId,
					affectedUserId,
					courseModule,
					log.getOrigin().toString(),
					ip,
					Integer.toString(log.getHour()),
					log.getAmPm().toString(),
					Integer.toString(log.getDayOfWeek().getValue()),
					log.getDayOfWeek().toString(),
					Integer.toString(log.getDayOfMonth()),
					Integer.toString(log.getDayOfYear()),
					Integer.toString(log.getWeekOfYear()),
					Integer.toString(log.getMonth().getValue()),
					log.getMonth().toString(), 
					log.getYearQuarter().toString(),
					log.getYearMonth().toString(),
					log.getYearWeek().toString(), 
					log.getYear().toString() });
		}
	}

}
