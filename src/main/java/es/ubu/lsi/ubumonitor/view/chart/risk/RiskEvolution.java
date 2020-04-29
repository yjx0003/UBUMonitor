package es.ubu.lsi.ubumonitor.view.chart.risk;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.ComponentEvent;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LastActivity;
import es.ubu.lsi.ubumonitor.model.LastActivityFactory;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;

public class RiskEvolution extends RiskBarTemporal {

	public RiskEvolution(MainController mainController, DatePicker datePickerStart, DatePicker datePickerEnd,
			ChoiceBox<GroupByAbstract<?>> choiceBoxDate) {
		super(mainController, ChartType.RISK_EVOLUTION);
		this.datePickerStart = datePickerStart;
		this.datePickerEnd = datePickerEnd;
		this.choiceBoxDate = choiceBoxDate;
		useRangeDate = true;
		useGroupBy = true;
		useOptions = true;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> selectedEnrolledUser = getSelectedEnrolledUser();
		LocalDate start = datePickerStart.getValue();
		LocalDate end = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<LocalDateTime> dateTimes = groupBy.getRangeLocalDateTime(start, end);
		Map<LastActivity, Map<LocalDateTime, List<EnrolledUser>>> map = classify(selectedEnrolledUser, dateTimes);

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("userid", "fullname", "date", "group"))) {
			for(Map.Entry<LastActivity, Map<LocalDateTime,List<EnrolledUser>>> entry: map.entrySet()) {
				LastActivity lastActivity = entry.getKey();
				for(Map.Entry<LocalDateTime, List<EnrolledUser>> entryUser: entry.getValue().entrySet()) {
					for(EnrolledUser user: entryUser.getValue()) {
						printer.print(user.getId());
						printer.print(user.getFullName());
						printer.print(entryUser.getKey().format(Controller.DATE_FORMATTER));
						printer.print(lastActivity);
						printer.println();
					}
				}
			}
		}

	}

	@Override
	protected String createDataset(List<EnrolledUser> selectedEnrolledUser) {
		LocalDate start = datePickerStart.getValue();
		LocalDate end = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<LocalDateTime> dateTimes = groupBy.getRangeLocalDateTime(start, end);
		Map<LastActivity, Map<LocalDateTime, List<EnrolledUser>>> map = classify(selectedEnrolledUser, dateTimes);

		JSObject data = new JSObject();
		JSArray labels = new JSArray();
		labels.addAllWithQuote(dateTimes.stream()
				.map(l -> l.format(Controller.DATE_FORMATTER))
				.collect(Collectors.toList()));
		data.put("labels", labels);
		JSArray datasets = new JSArray();

		for (int i = 0; i < LastActivityFactory.getAllLastActivity()
				.size(); i++) {
			LastActivity lastActivity = LastActivityFactory.getActivity(i);
			JSObject dataset = new JSObject();
			dataset.putWithQuote("label", lastActivity);
			dataset.put("borderColor", colorToRGB(lastActivity.getColor()));
			dataset.put("backgroundColor", colorToRGB(lastActivity.getColor(), 0.2));
			dataset.put("fill", i == 0 ? "'origin'" : "'-1'");
			JSArray dataArray = new JSArray();
			Map<LocalDateTime, List<EnrolledUser>> mapData = map.computeIfAbsent(lastActivity, k -> new HashMap<>());
			for (LocalDateTime dateTime : dateTimes) {
				List<EnrolledUser> users = mapData.computeIfAbsent(dateTime, k -> new ArrayList<>());
				dataArray.add(users.size());
			}
			dataset.put("data", dataArray);
			datasets.add(dataset);

		}
		data.put("datasets", datasets);
		return data.toString();
	}

	@Override
	public String getOptions() {
		JSObject jsObject = getDefaultOptions();
		jsObject.putWithQuote("typeGraph", "line");

		JSObject scales = new JSObject();

		scales.put("yAxes", "[{" + getYScaleLabel() + ",stacked:!0}]");
		scales.put("xAxes", "[{" + getXScaleLabel() + "}]");
		jsObject.put("scales", scales);

		return jsObject.toString();

	}

	public Map<LastActivity, Map<LocalDateTime, List<EnrolledUser>>> classify(List<EnrolledUser> selectedEnrolledUser,
			List<LocalDateTime> dateTimes) {

		Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs = getUserLogs(selectedEnrolledUser);

		Map<LastActivity, Map<LocalDateTime, List<EnrolledUser>>> map = new HashMap<>();

		for (LocalDateTime localDateTime : dateTimes) {
			ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
			for (EnrolledUser user : selectedEnrolledUser) {
				ZonedDateTime lastDateTime = getLastLog(l -> l.getTime()
						.isBefore(zonedDateTime), logs, user);
				map.computeIfAbsent(LastActivityFactory.getActivity(lastDateTime, zonedDateTime), k -> new HashMap<>())
						.computeIfAbsent(localDateTime, k -> new ArrayList<>())
						.add(user);
			}
		}
		return map;
	}

}
