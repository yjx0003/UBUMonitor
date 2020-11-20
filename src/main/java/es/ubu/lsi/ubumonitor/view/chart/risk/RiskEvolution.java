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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;

public class RiskEvolution extends RiskBarTemporal {

	private DatePicker datePickerStart;
	private DatePicker datePickerEnd;
	private ChoiceBox<GroupByAbstract<?>> choiceBoxDate;

	public RiskEvolution(MainController mainController, DatePicker datePickerStart, DatePicker datePickerEnd,
			ChoiceBox<GroupByAbstract<?>> choiceBoxDate) {
		super(mainController, ChartType.RISK_EVOLUTION);
		this.datePickerStart = datePickerStart;
		this.datePickerEnd = datePickerEnd;
		this.choiceBoxDate = choiceBoxDate;
		useRangeDate = true;
		useGroupBy = true;
		useOptions = true;
		useLogs = true;
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
			Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs = getUserLogs(users);

			for (EnrolledUser user : users) {
				printer.print(user.getId());
				printer.print(user.getFullName());
				for (LocalDateTime dateTime : dateTimes) {
					ZonedDateTime zonedDateTime = dateTime.plusDays(1)
							.atZone(ZoneId.systemDefault());
					ZonedDateTime lastDateTime = getLastLog(l -> l.getTime()
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

	@Override
	protected String createDataset(List<EnrolledUser> selectedEnrolledUser) {

		LocalDate start = datePickerStart.getValue();
		LocalDate end = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();

		JSObject data = new JSObject();
		JSArray labels = new JSArray();
		labels.addAllWithQuote(groupBy.getRangeString(start, end));
		data.put("labels", labels);

		data.put("datasets", createDatasets(selectedEnrolledUser, start, end, groupBy));
		return data.toString();
	}

	@Override
	public void fillOptions(JSObject jsObject) {
		jsObject.putWithQuote("typeGraph", "line");

		JSObject scales = new JSObject();

		JSObject callbacks = new JSObject();

		callbacks.put("label",
				"function(e,t){return t.datasets[e.datasetIndex].label +': '+e.yLabel.toLocaleString(locale,{maximumFractionDigits:2})}");
		jsObject.put("tooltips", "{callbacks:" + callbacks + "}");
		scales.put("yAxes", "[{" + getYScaleLabel() + ",stacked:!0}]");
		scales.put("xAxes", "[{" + getXScaleLabel() + "}]");
		jsObject.put("scales", scales);

	}

	public <T> Map<LastActivity, Map<T, DescriptiveStatistics>> classify(List<EnrolledUser> selectedEnrolledUser,
			Map<T, List<LocalDateTime>> mapTimes) {

		Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs = getUserLogs(selectedEnrolledUser);

		Map<LastActivity, Map<T, DescriptiveStatistics>> map = new HashMap<>();

		for (Map.Entry<T, List<LocalDateTime>> entry : mapTimes.entrySet()) {
			for (LocalDateTime localDateTime : entry.getValue()) {
				ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
						.plusDays(1);
				Map<LastActivity, List<EnrolledUser>> users = new HashMap<>();
				for (EnrolledUser user : selectedEnrolledUser) {
					ZonedDateTime lastDateTime = getLastLog(l -> l.getTime()
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

	private <T extends Serializable> JSArray createDatasets(List<EnrolledUser> users, LocalDate start, LocalDate end,
			GroupByAbstract<T> groupBy) {

		Map<LastActivity, Map<T, DescriptiveStatistics>> map = classify(users,
				groupBy.getRangeLocalDateTime(start, end));

		JSArray datasets = new JSArray();

		for (int i = 0; i < LastActivityFactory.DEFAULT.getAllLastActivity()
				.size(); i++) {
			LastActivity lastActivity = LastActivityFactory.DEFAULT.getActivity(i);
			JSObject dataset = new JSObject();
			dataset.putWithQuote("label", lastActivity);
			dataset.put("borderColor", colorToRGB(lastActivity.getColor()));
			dataset.put("backgroundColor", colorToRGB(lastActivity.getColor(), 0.2));
			dataset.put("fill", i == 0 ? "'origin'" : "'-1'");
			JSArray dataArray = new JSArray();
			Map<T, DescriptiveStatistics> mapData = map.computeIfAbsent(lastActivity, k -> new HashMap<>());
			for (T time : groupBy.getRange(start, end)) {
				DescriptiveStatistics descriptiveStatistics = mapData.computeIfAbsent(time,
						k -> new DescriptiveStatistics());
				dataArray.add(Double.isNaN(descriptiveStatistics.getMean()) ? 0 : descriptiveStatistics.getMean());
			}
			dataset.put("data", dataArray);
			datasets.add(dataset);

		}
		return datasets;
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
}
