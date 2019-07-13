package export.builder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import export.CSVBuilderAbstract;
import model.DataBase;
import model.LogLine;

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
		String userId, affectedUserId, courseModule, IP;
		for (LogLine log : logs) {
			// Preprocess data
			userId = log.getUser() != null ? Integer.toString(log.getUser().getId()) : "";
			affectedUserId = log.getAffectedUser() != null ? Integer.toString(log.getAffectedUser().getId()) : "";
			courseModule = log.getCourseModule() != null ? Integer.toString(log.getCourseModule().getCmid()) : "";
			IP = log.getIPAdress() != null ? log.getIPAdress().toString() : "";
			// Add data
			LOGGER.debug("Data line: {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}",
					log.getTime().toLocalDateTime().toString(), log.getComponent().getName(),
					log.getEventName().getName(), userId, affectedUserId, courseModule, log.getOrigin().toString(), IP,
					Integer.toString(log.getHour()), log.getAmPm().toString(), log.getDayOfWeek().getValue(), log.getDayOfWeek().toString(),
					Integer.toString(log.getDayOfMonth()), Integer.toString(log.getDayOfYear()),
					Integer.toString(log.getWeekOfYear()), log.getMonth().getValue(), log.getMonth().toString(), log.getYearQuarter().toString(),
					log.getYearMonth().toString(), log.getYearWeek().toString(), log.getYear().toString());

			getData().add(new String[] { log.getTime().toLocalDateTime().toString(), log.getComponent().getName(),
					log.getEventName().getName(), userId, affectedUserId, courseModule, log.getOrigin().toString(), IP,
					Integer.toString(log.getHour()), log.getAmPm().toString(), Integer.toString(log.getDayOfWeek().getValue()), log.getDayOfWeek().toString(),
					Integer.toString(log.getDayOfMonth()), Integer.toString(log.getDayOfYear()),
					Integer.toString(log.getWeekOfYear()), Integer.toString(log.getMonth().getValue()), log.getMonth().toString(), log.getYearQuarter().toString(),
					log.getYearMonth().toString(), log.getYearWeek().toString(), log.getYear().toString() });
		}
	}

}
