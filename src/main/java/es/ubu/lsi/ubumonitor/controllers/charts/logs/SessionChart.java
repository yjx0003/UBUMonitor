package es.ubu.lsi.ubumonitor.controllers.charts.logs;

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
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.charts.ChartType;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSet;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.model.Session;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;

public class SessionChart extends ChartjsLog {

	private static final List<LogLine> EMPTY_LOG_LINES = Collections.emptyList();

	public SessionChart(MainController mainController) {
		super(mainController, ChartType.SESSION);
		useLegend = true;
		useGroupBy = true;
	}

	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		MainConfiguration mainConfiguration = Controller.getInstance()
				.getMainConfiguration();
		int timeInterval = mainConfiguration.getValue(this.chartType, "timeInterval");
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		return createSession(typeLogs, dataSet, selectedUsers, dateStart, dateEnd, groupBy, timeInterval);

	}

	private <E, T extends Serializable> String createSession(List<E> typeLogs, DataSet<E> dataSet,
			List<EnrolledUser> selectedUsers, LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<T> groupBy,
			int timeInterval) {
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

		Map<EnrolledUser, Map<T, List<Session>>> sessionMap = createSession(selectedUsers, dateStart, dateEnd, groupBy,
				timeInterval, groupByTime);

		List<String> rangeDatesString = groupBy.getRangeString(dateStart, dateEnd);
		List<T> rangeDates = groupBy.getRange(dateStart, dateEnd);
		JSObject data = new JSObject();

		data.put("labels", createLabels(rangeDatesString));

		JSArray datasets = createDatasets(sessionMap, selectedUsers, rangeDates);

		data.put("datasets", datasets);
		return data.toString();

	}

	private <T> JSArray createDatasets(Map<EnrolledUser, Map<T, List<Session>>> sessionsMap,
			List<EnrolledUser> selectedUsers, List<T> rangeDates) {

		JSArray datasets = new JSArray();

		for (EnrolledUser enrolledUser : selectedUsers) {
			JSArray data = new JSArray();
			JSArray n = new JSArray();
			JSObject dataset = new JSObject();
			dataset.putWithQuote("label", enrolledUser.getFullName());
			dataset.put("backgroundColor", hex(enrolledUser.getId()));
			for (T element : rangeDates) {
				List<Session> session = sessionsMap.get(enrolledUser)
						.get(element);

				data.add(session.stream()
						.map(Session::getDiffMinutes)
						.collect(Collectors.summingLong(Long::longValue)));
				n.add(session.size());
			}
			dataset.put("data", data);
			dataset.put("n", n);
			datasets.add(dataset);
		}

		return datasets;
	}

	public <T extends Serializable> Map<EnrolledUser, Map<T, List<Session>>> createSession(
			List<EnrolledUser> selectedUsers, LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<T> groupBy,
			int timeInterval, Map<EnrolledUser, Map<T, List<LogLine>>> groupByTime) {
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
		// TODO Auto-generated method stub

	}

	@Override
	protected String[] getCSVHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOptions() {

		JSObject jsObject = getDefaultOptions();
		jsObject.putWithQuote("typeGraph", "bar");
		jsObject.put("scales", "{yAxes:[{" + getYScaleLabel() + ",stacked: true,ticks:{suggestedMax:" + getSuggestedMax()
				+ ",stepSize:0}}],xAxes:[{" + getXScaleLabel() + ",stacked: true}]}");
		jsObject.put("tooltips",
				"{callbacks:{label:function(a,e){return e.datasets[a.datasetIndex].label+': '+a.yLabel+' (avg: '+Math.round(a.yLabel/e.datasets[a.datasetIndex].n[a.index]*100)/100+')'},afterLabel:function(a,e){return'"
						+ I18n.get("text.session") + "'+e.datasets[a.datasetIndex].n[a.index]}}}");
		return jsObject.toString();
	}

	@Override
	public String getXAxisTitle() {
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"), I18n.get(choiceBoxDate.getValue()
				.getTypeTime()));
	}
	
	@Override
	public String getYAxisTitle() {
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		
		return MessageFormat.format(I18n.get(getChartType() + ".yAxisTitle"), (int)mainConfiguration.getValue(chartType, "timeInterval"));
	}

}
