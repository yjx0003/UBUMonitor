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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSet;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.controllers.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.TypeTimes;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;

public class Scatter extends Chartjs {
	private static final Logger LOGGER = LoggerFactory.getLogger(Scatter.class);
	private DateTimeFormatter dateFormatter;
	private DateTimeFormatter timeFormatter;
	private String datePattern;
	private String timePattern;

	public Scatter(MainController mainController) {
		super(mainController, ChartType.SCATTER, Tabs.LOGS);
		useLegend = true;

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
		jsObject.put("onClick", "function(t,a){let e=myChart.getElementAtEvent(t)[0];e&&javaConnector.dataPointSelection(myChart.data.datasets[e._datasetIndex].data[e._index].y)}");
		jsObject.put("scales",
				"{yAxes:[{type:'category'}],xAxes:[{type:'time',ticks:{maxTicksLimit:10},time:{min:'"
						+ dateFormatter.format(dateStart) + "',max:'" + dateFormatter.format(dateEnd) + "',format:'"
						+ datePattern + " " + timePattern + "'}}]}");
		
		jsObject.put("tooltips", "{callbacks:{label:function(l,a){return a.datasets[l.datasetIndex].label+': '+ l.xLabel}}}");
		return jsObject.toString();
	}

	@Override
	public void update() {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> group = Controller.getInstance().getActualCourse().getLogStats().getByType(TypeTimes.DAY);
		String dataset = null;

		if (tabUbuLogsComponent.isSelected()) {

			dataset = createData(selectedUsers, listViewComponents.getSelectionModel().getSelectedItems(), dateStart,
					dateEnd, DataSetComponent.getInstance(), group);
		} else if (tabUbuLogsEvent.isSelected()) {

			dataset = createData(selectedUsers, listViewEvents.getSelectionModel().getSelectedItems(), dateStart,
					dateEnd, DataSetComponentEvent.getInstance(), group);
		} else if (tabUbuLogsSection.isSelected()) {

			dataset = createData(selectedUsers, listViewSection.getSelectionModel().getSelectedItems(), dateStart,
					dateEnd, DataSetSection.getInstance(), group);
		} else if (tabUbuLogsCourseModule.isSelected()) {

			dataset = createData(selectedUsers, listViewCourseModule.getSelectionModel().getSelectedItems(), dateStart,
					dateEnd, DatasSetCourseModule.getInstance(), group);
		}
		String options = getOptions();
		LOGGER.info("Dataset en JS: {}", dataset);
		LOGGER.info("Opciones para el stacked bar en JS: {}", options);
		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", dataset, options));
	}

	private <E> String createData(List<EnrolledUser> selectedUsers, List<E> logTypes, LocalDate dateStart,
			LocalDate dateEnd, DataSet<E> dataSet, GroupByAbstract<?> groupBy) {

		Map<EnrolledUser, Map<E, List<LogLine>>> map = dataSet.getUserLogs(groupBy, selectedUsers, logTypes, dateStart,
				dateEnd);
		Map<E, JSArray> dataMap = new HashMap<>();
		JSObject data = new JSObject();
		JSArray jsArray = new JSArray();
		JSArray datasets = new JSArray();
		jsArray.addAllWithQuote(selectedUsers);
		data.put("labels", jsArray);

		for (int i = 0; i < selectedUsers.size(); i++) {
			Map<E, List<LogLine>> mapLogTypes = map.get(selectedUsers.get(i));
			for (E logTye : logTypes) {
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
		
		for(E logType : logTypes ) {
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
