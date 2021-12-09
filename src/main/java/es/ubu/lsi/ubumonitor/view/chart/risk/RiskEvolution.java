package es.ubu.lsi.ubumonitor.view.chart.risk;

import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.ComponentEvent;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LastActivity;
import es.ubu.lsi.ubumonitor.model.LastActivityFactory;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;

public class RiskEvolution extends Plotly {

	private DatePicker datePickerStart;
	private DatePicker datePickerEnd;
	private ChoiceBox<GroupByAbstract<?>> choiceBoxDate;

	public RiskEvolution(MainController mainController, DatePicker datePickerStart, DatePicker datePickerEnd,
			ChoiceBox<GroupByAbstract<?>> choiceBoxDate) {
		super(mainController, ChartType.RISK_EVOLUTION);
		this.datePickerStart = datePickerStart;
		this.datePickerEnd = datePickerEnd;
		this.choiceBoxDate = choiceBoxDate;
		useLegend = true;
		useRangeDate = true;
		useGroupBy = true;
		useOptions = true;
		useLogs = true;
	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> selectedEnrolledUser = getSelectedEnrolledUser();
		LocalDate start = datePickerStart.getValue();
		LocalDate end = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();

		JSArray x = new JSArray();
		x.addAllWithQuote(groupBy.getRangeString(start, end));

		createTraces(data, selectedEnrolledUser, x, start, end, groupBy);

	}

	@Override
	public void createLayout(JSObject layout) {
		JSObject xaxis = new JSObject();
		defaultAxisValues(xaxis, getXAxisTitle(), null);
		xaxis.put("type", "'category'");

		JSObject yaxis = new JSObject();
		defaultAxisValues(yaxis, getYAxisTitle(), "");
		layout.put("xaxis", xaxis);
		layout.put("yaxis", yaxis);
		layout.put("hovermode", "'x unified'");
	}

	public <T> Map<LastActivity, Map<T, DescriptiveStatistics>> classify(List<EnrolledUser> selectedEnrolledUser,
			Map<T, List<LocalDateTime>> mapTimes) {

		Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs = RiskBarTemporal.getUserLogs(selectedEnrolledUser,
				actualCourse);

		Map<LastActivity, Map<T, DescriptiveStatistics>> map = new HashMap<>();

		for (Map.Entry<T, List<LocalDateTime>> entry : mapTimes.entrySet()) {
			for (LocalDateTime localDateTime : entry.getValue()) {
				ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
						.plusDays(1);
				Map<LastActivity, List<EnrolledUser>> users = new HashMap<>();
				for (EnrolledUser user : selectedEnrolledUser) {
					ZonedDateTime lastDateTime = RiskBarTemporal.getLastLog(l -> l.getTime()
							.isBefore(zonedDateTime), logs, user);
					users.computeIfAbsent(LastActivityFactory.DEFAULT.getActivity(lastDateTime, zonedDateTime),
							k -> new ArrayList<>())
							.add(user);

				}
				for (LastActivity lastActivity : LastActivityFactory.DEFAULT.getAllLastActivity()) {
					map.computeIfAbsent(lastActivity, k -> new HashMap<>())
							.computeIfAbsent(entry.getKey(), k -> new DescriptiveStatistics())
							.addValue(users.computeIfAbsent(lastActivity, k -> new ArrayList<>())
									.size());
				}

			}

		}

		return map;
	}

	private <T extends Serializable> void createTraces(JSArray data, List<EnrolledUser> users, JSArray x,
			LocalDate start, LocalDate end, GroupByAbstract<T> groupBy) {

		Map<LastActivity, Map<T, DescriptiveStatistics>> map = classify(users,
				groupBy.getRangeLocalDateTime(start, end));

		for (int i = 0; i < LastActivityFactory.DEFAULT.getAllLastActivity()
				.size(); i++) {
			LastActivity lastActivity = LastActivityFactory.DEFAULT.getActivity(i);

			JSArray y = new JSArray();
			Map<T, DescriptiveStatistics> mapData = map.computeIfAbsent(lastActivity, k -> new HashMap<>());
			for (T time : groupBy.getRange(start, end)) {
				DescriptiveStatistics descriptiveStatistics = mapData.computeIfAbsent(time,
						k -> new DescriptiveStatistics());
				y.add(Double.isNaN(descriptiveStatistics.getMean()) ? 0 : descriptiveStatistics.getMean());
			}

			data.add(createTrace(x, y, lastActivity));

		}

	}

	public JSObject createTrace(JSArray x, JSArray y, LastActivity lastActivity) {
		JSObject trace = new JSObject();
		trace.putWithQuote("name", lastActivity);
		JSObject marker = new JSObject();
		marker.put("color", colorToRGB(lastActivity.getColor()));
		trace.put("fillcolor", colorToRGB(lastActivity.getColor(), 0.2));
		trace.put("marker", marker);
		trace.put("mode", "'lines+markers'");
		trace.put("hovertemplate", "'%{data.name}: %{y:.2~f}<extra></extra>'");
		trace.put("stackgroup", "'one'");
		trace.put("y", y);
		trace.put("x", x);

		return trace;
	}

	@Override
	public String getXAxisTitle() {
		String start = datePickerStart.getValue()
				.atStartOfDay()
				.format(Controller.DATE_TIME_FORMATTER);
		String end = datePickerEnd.getValue()
				.plusDays(1)
				.atStartOfDay()
				.format(Controller.DATE_TIME_FORMATTER);
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"), I18n.get(choiceBoxDate.getValue()
				.getTypeTime()), start, end);
	}

	@Override
	public void exportCSV(String path) throws IOException {

		LocalDate start = datePickerStart.getValue();
		LocalDate end = datePickerEnd.getValue();
		List<EnrolledUser> users = getSelectedEnrolledUser();
		GroupByAbstract<?> groupBy = actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);
		List<String> range = groupBy.getRangeString(start, end);
		List<String> header = Stream.of("userid", "fullname")
				.collect(Collectors.toList());
		header.addAll(range);

		List<LocalDateTime> dateTimes = groupBy.getRangeLocalDateTime(start, end)
				.values()
				.stream()
				.flatMap(List::stream)
				.sorted()
				.collect(Collectors.toList());

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader(header.toArray(new String[0])))) {
			Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs = RiskBarTemporal.getUserLogs(users,
					actualCourse);

			for (EnrolledUser user : users) {
				printer.print(user.getId());
				printer.print(user.getFullName());
				for (LocalDateTime dateTime : dateTimes) {
					ZonedDateTime zonedDateTime = dateTime.plusDays(1)
							.atZone(ZoneId.systemDefault());
					ZonedDateTime lastDateTime = RiskBarTemporal.getLastLog(l -> l.getTime()
							.isBefore(zonedDateTime), logs, user);
					if (lastDateTime == null) {
						printer.print(null);
					} else {
						printer.print(ChronoUnit.DAYS.between(lastDateTime, zonedDateTime));
					}
				}
				printer.println();
			}

		}

	}
}
