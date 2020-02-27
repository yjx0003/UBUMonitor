package es.ubu.lsi.ubumonitor.controllers.charts;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSet;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.TypeTimes;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;

public class Scatter extends ChartjsLog {
	
	private DateTimeFormatter dateFormatter;
	private DateTimeFormatter timeFormatter;
	private String datePattern;
	private String timePattern;

	public Scatter(MainController mainController) {
		super(mainController, ChartType.SCATTER);
		useLegend = true;
		useRangeDate = true;
		datePattern = DateTimeFormatterBuilder
				.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, Locale.getDefault())
				.toUpperCase();
		timePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, FormatStyle.SHORT,
				IsoChronology.INSTANCE, Locale.getDefault());

		dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withZone(ZoneId.systemDefault());
		timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault());
	}

	@Override
	public void exportCSV(String path) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getOptions() {
		JSObject jsObject = getDefaultOptions();
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		jsObject.put("typeGraph", "'scatter'");
		jsObject.put("onClick",
				"function(t,a){let e=myChart.getElementAtEvent(t)[0];e&&javaConnector.dataPointSelection(myChart.data.datasets[e._datasetIndex].data[e._index].y)}");
		jsObject.put("scales",
				"{yAxes:[{type:'category'}],xAxes:[{type:'time',ticks:{min:'" + dateFormatter.format(dateStart)
						+ "',max:'" + dateFormatter.format(dateEnd) + "',maxTicksLimit:10},time:{minUnit:'day',parser:'"
						+ datePattern + " " + timePattern + "'}}]}");

		jsObject.put("tooltips",
				"{callbacks:{label:function(l,a){return a.datasets[l.datasetIndex].label+': '+ l.xLabel}}}");
		return jsObject.toString();
	}



	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = Controller.getInstance().getActualCourse().getLogStats().getByType(TypeTimes.DAY);
		Map<EnrolledUser, Map<E, List<LogLine>>> map = dataSet.getUserLogs(groupBy, selectedUsers, typeLogs, dateStart,
				dateEnd);
		Map<E, JSArray> dataMap = new HashMap<>();
		JSObject data = new JSObject();
		JSArray jsArray = new JSArray();
		JSArray datasets = new JSArray();
		jsArray.addAllWithQuote(selectedUsers);
		data.put("labels", jsArray);

		for (int i = 0; i < selectedUsers.size(); i++) {
			Map<E, List<LogLine>> mapLogTypes = map.get(selectedUsers.get(i));
			for (E logTye : typeLogs) {
				jsArray = dataMap.computeIfAbsent(logTye, k -> new JSArray());
				List<LogLine> logLines = mapLogTypes.get(logTye);
				for (LogLine logLine : logLines) {
					JSObject point = new JSObject();
					point.putWithQuote("x",
							dateFormatter.format(logLine.getTime()) + " " + timeFormatter.format(logLine.getTime()));
					point.put("y", i);
					jsArray.add(point);
				}

			}
		}

		for (E logType : typeLogs) {
			JSObject dataset = new JSObject();
			dataset.putWithQuote("label", dataSet.translate(logType));
			dataset.put("backgroundColor", hex(logType));
			dataset.put("data", dataMap.get(logType));
			datasets.add(dataset);
		}

		data.put("datasets", datasets);
		return data.toString();
	}

}
