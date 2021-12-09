package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.model.Session;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class SessionChart extends PlotlyLog {

	private static final List<LogLine> EMPTY_LOG_LINES = Collections.emptyList();

	public SessionChart(MainController mainController) {
		super(mainController, ChartType.SESSION);
		useLegend = true;
		useGroupBy = true;
	}

	@Override
	public <E> JSArray createData(List<E> typeLogs, DataSet<E> dataSet, List<EnrolledUser> selectedUsers,
			LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy) {

		return createSessionData(typeLogs, dataSet, selectedUsers, dateStart, dateEnd, groupBy,
				getConfigValue("timeInterval"));
	}

	@Override
	public <E> JSObject createLayout(List<E> typeLogs, DataSet<E> dataSet, LocalDate dateStart, LocalDate dateEnd,
			GroupByAbstract<?> groupBy) {
		JSObject layout = new JSObject();
		JSObject xaxis = new JSObject();
		xaxis.put("type", "'category'");
		Plotly.defaultAxisValues(xaxis, getXAxisTitle(), null);
		JSObject yaxis = new JSObject();
	
		String maxText = textFieldMax.getText();
		if(maxText==null ||  maxText.isEmpty()) {
			Plotly.defaultAxisValues(yaxis, getYAxisTitle(), null);
		}else {
			long max =  getSuggestedMax(maxText);
			Plotly.defaultAxisValues(yaxis, getYAxisTitle(), "[0," + max + "]");
			
		}
		
		
		layout.put("xaxis", xaxis);
		layout.put("yaxis", yaxis);
		layout.put("barmode", "'stack'");
		layout.put("hovermode", "'closest'");
		return layout;
	}

	private <E, T extends Serializable> JSArray createSessionData(List<E> typeLogs, DataSet<E> dataSet,
			List<EnrolledUser> selectedUsers, LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<T> groupBy,
			int timeInterval) {
		JSArray data = new JSArray();

		List<T> rangeDates = groupBy.getRange(dateStart, dateEnd);

		Map<EnrolledUser, Map<T, List<Session>>> sessionMap = createSession(typeLogs, dataSet, selectedUsers, dateStart,
				dateEnd, groupBy, timeInterval);

		List<String> rangeDatesString = groupBy.getRangeString(rangeDates);

		Map<EnrolledUser, Color> colors = UtilMethods.getRandomColors(selectedUsers);
		for (EnrolledUser enrolledUser : selectedUsers) {
			JSArray x = new JSArray();
			JSArray y = new JSArray();
			JSArray userids = new JSArray();
			JSArray nsession = new JSArray();
			JSArray avgSession = new JSArray();
			for (int i = 0; i < rangeDates.size(); i++) {
				List<Session> session = sessionMap.get(enrolledUser)
						.get(rangeDates.get(i));
				long value = session.stream()
						.map(Session::getDiffMinutes)
						.collect(Collectors.summingLong(Long::longValue));
				y.add(value);
				x.addWithQuote(rangeDatesString.get(i));
				userids.add(enrolledUser.getId());
				avgSession.add(value/(double)session.size());
				nsession.add(session.size());
			}
			JSObject trace = createTrace(enrolledUser.getFullName(), x, y, colors.get(enrolledUser));
			trace.put("userids", userids);
			trace.put("text", nsession);
			trace.put("customdata", avgSession);
			data.add(trace);

		}

		return data;

	}

	private JSObject createTrace(String name, JSArray x, JSArray y, Color color) {
		JSObject trace = new JSObject();

		trace.put("type", "'bar'");
		trace.putWithQuote("name", name);
		trace.put("x", x);
		trace.put("y", y);
		trace.put("textposition", "'none'");
		trace.put("hovertemplate", "'<b>%{x}<br>%{data.name}: </b>%{y} (avg: %{customdata:.2~f})<br><b>"+I18n.get("text.session")+"</b>%{text}<extra></extra>'");
		JSObject marker = new JSObject();
		marker.put("color", awtColorToRGB(color, OPACITY));
		trace.put("marker", marker);

		return trace;

	}

	private <E, T extends Serializable> Map<EnrolledUser, Map<T, List<Session>>> createSession(List<E> typeLogs,
			DataSet<E> dataSet, List<EnrolledUser> selectedUsers, LocalDate dateStart, LocalDate dateEnd,
			GroupByAbstract<T> groupBy, int timeInterval) {

		Map<EnrolledUser, Map<E, List<LogLine>>> map = dataSet.getUserLogs(groupBy, selectedUsers, typeLogs, dateStart,
				dateEnd);
		Map<EnrolledUser, Map<T, List<LogLine>>> groupByTime = new HashMap<>();
		for (EnrolledUser enrolledUser : selectedUsers) {
			Map<T, List<LogLine>> a = map.get(enrolledUser)
					.values()
					.stream()
					.flatMap(List::stream)
					.sorted(Comparator.comparing(LogLine::getTime))
					.collect(Collectors.groupingBy(groupBy.getGroupByFunction(), Collectors.toList()));
			groupByTime.put(enrolledUser, a);
		}

		Map<EnrolledUser, Map<T, List<Session>>> userSessions = new HashMap<>();

		for (EnrolledUser enrolledUser : selectedUsers) {
			Map<T, List<LogLine>> logLinesGrouped = groupByTime.get(enrolledUser);
			Map<T, List<Session>> sessionsGrouped = new HashMap<>();
			userSessions.put(enrolledUser, sessionsGrouped);

			for (T timeType : groupBy.getRange(dateStart, dateEnd)) {
				List<Session> sessions = new ArrayList<>();
				sessionsGrouped.put(timeType, sessions);
				List<LogLine> logLines = logLinesGrouped.computeIfAbsent(timeType, k -> EMPTY_LOG_LINES);
				ZonedDateTime actualTime = null;
				Session session = null;
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
			}
		}
		return userSessions;
	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {

		exportCSV(printer, dataSet, typeLogs, choiceBoxDate.getValue());

	}

	private <E, T extends Serializable> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs,
			GroupByAbstract<T> groupBy) throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		int timeInterval = getConfigValue("timeInterval");

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		Map<EnrolledUser, Map<T, List<Session>>> sessionMap = createSession(typeLogs, dataSet, selectedUsers, dateStart,
				dateEnd, groupBy, timeInterval);

		for (EnrolledUser enrolledUser : selectedUsers) {
			printer.print(enrolledUser.getId());
			printer.print(enrolledUser.getFullName());
			for (T time : groupBy.getRange(dateStart, dateEnd)) {
				List<Session> session = sessionMap.get(enrolledUser)
						.get(time);
				printer.print(session.stream()
						.map(Session::getDiffMinutes)
						.collect(Collectors.summingLong(Long::longValue)));

			}
			printer.println();

		}
	}

	@Override
	protected String[] getCSVHeader() {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<String> range = groupBy.getRangeString(dateStart, dateEnd);
		range.add(0, "userid");
		range.add(1, "fullname");
		return range.toArray(new String[0]);
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		exportCSVDesglosed(printer, dataSet, typeLogs, choiceBoxDate.getValue());

	}

	private <E, T extends Serializable> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet,
			List<E> typeLogs, GroupByAbstract<T> groupBy) throws IOException {

		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		int timeInterval = getConfigValue("timeInterval");

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		Map<EnrolledUser, Map<T, List<Session>>> sessionMap = createSession(typeLogs, dataSet, selectedUsers, dateStart,
				dateEnd, groupBy, timeInterval);

		for (EnrolledUser enrolledUser : selectedUsers) {

			for (T time : groupBy.getRange(dateStart, dateEnd)) {

				for (Session session : sessionMap.get(enrolledUser)
						.get(time)) {
					printer.print(enrolledUser.getId());
					printer.print(enrolledUser.getFullName());
					printer.print(Controller.DATE_TIME_FORMATTER.format(session.getFirstLogTime()));
					printer.print(Controller.DATE_TIME_FORMATTER.format(session.getLastLogTime()));
					printer.println();
				}

			}

		}

	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		return new String[] { "userid", "fullname", "start", "end" };
	}

	@Override
	public String getXAxisTitle() {
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"), I18n.get(choiceBoxDate.getValue()
				.getTypeTime()));
	}

	@Override
	public String getYAxisTitle() {

		return MessageFormat.format(I18n.get(getChartType() + ".yAxisTitle"), (int) getConfigValue("timeInterval"));
	}

}
