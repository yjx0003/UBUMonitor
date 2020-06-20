package es.ubu.lsi.ubumonitor.export.dashboard;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.model.Session;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;

public class FillSheetSession extends FillSheetData {

	public FillSheetSession() {
		super("Session");
	}

	@Override
	protected void fillTable(XSSFSheet sheet, CellStyle dateStyle) {
		TemporalField firstDayWeek = WeekFields.of(Locale.getDefault())
				.dayOfWeek();
		int timeInterval = Controller.getInstance()
				.getMainConfiguration()
				.getValue(ChartType.SESSION, "timeInterval");

		List<LogLine> logLines = Controller.getInstance()
				.getActualCourse()
				.getLogs()
				.getList();
		Map<EnrolledUser, List<LogLine>> map = logLines.stream()
				.filter(l -> l.getUser() != null)
				.collect(Collectors.groupingBy(LogLine::getUser, Collectors.toList()));
		int rowIndex = 0;
		for (Map.Entry<EnrolledUser, List<LogLine>> entry : map.entrySet()) {
			List<Session> sessions = createSessions(timeInterval, entry.getValue());
			for (Session session : sessions) {
				int columnIndex = -1;
				++rowIndex;
				setCellValue(sheet, rowIndex, ++columnIndex, entry.getKey()
						.getId());
				setCellValue(sheet, rowIndex, ++columnIndex, session.getFirstLogTime()
						.toLocalDateTime(), dateStyle);
				setCellValue(sheet, rowIndex, ++columnIndex, session.getFirstLogTime()
						.toLocalDate()
						.with(firstDayWeek, 1), dateStyle);
				setCellValue(sheet, rowIndex, ++columnIndex, session.getLastLogTime()
						.toLocalDateTime(), dateStyle);
				setCellValue(sheet, rowIndex, ++columnIndex, session.getDiffMinutes());
			}
		}

	}

	public List<Session> createSessions(int timeInterval, List<LogLine> logLines) {
		ZonedDateTime actualTime = null;
		Session session = null;
		List<Session> sessions = new ArrayList<>();
		for (LogLine logLine : logLines) {
			ZonedDateTime time = logLine.getTime();
			if (actualTime == null || actualTime.until(time, ChronoUnit.MINUTES) > timeInterval) {
				session = new Session(logLine);
				sessions.add(session);
				actualTime = time;
			} else {

				session.add(logLine);
			}

		}
		return sessions;
	}

}
