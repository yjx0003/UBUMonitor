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

	}

	@Override
	protected String createDataset(List<EnrolledUser> selectedEnrolledUser) {

		LocalDate start = datePickerStart.getValue();
		LocalDate end = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();

		Map<EnrolledUser, Map<ComponentEvent, List<LogLine>>> logs = getUserLogs(selectedEnrolledUser);
		List<LocalDateTime> dateTimes = groupBy.getRangeLocalDateTime(start, end);
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

		JSObject data = new JSObject();
		JSArray labels = new JSArray();
		labels.addAllWithQuote(dateTimes.stream()
				.map(l -> l.format(Controller.DATE_TIME_FORMATTER))
				.collect(Collectors.toList()));
		data.put("labels", labels);
		JSArray datasets = new JSArray();

		for (LastActivity lastActivity : LastActivityFactory.getAllLastActivity()) {

			JSObject dataset = new JSObject();
			dataset.putWithQuote("label", lastActivity);
			dataset.put("borderColor", colorToRGB(lastActivity.getColor()));
			dataset.put("backgroundColor", colorToRGB(lastActivity.getColor(), 0.5));
			dataset.put("fill", true);
			JSArray dataArray = new JSArray();
			Map<LocalDateTime, List<EnrolledUser>> mapData = map.computeIfAbsent(lastActivity, k-> new HashMap<>());
			for(LocalDateTime dateTime: dateTimes) {
				List<EnrolledUser> users = mapData.computeIfAbsent(dateTime, k->new ArrayList<>());
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

		//jsObject.put("onClick",
			//	"function(t,e){let n=myChart.getElementsAtEventForMode(t,'nearest',{intersect:!0});if(n.length>0){let t=n[0],e=t._chart.config.data.datasets[t._datasetIndex].usersId[t._index];javaConnector.dataPointSelection(e[counter%e.length]),counter++}}");
		return jsObject.toString();

	}

}
