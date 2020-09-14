package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.DateTimeWrapper;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;

public class ScatterUser extends ChartjsLog {

	private DateTimeWrapper dateTimeWrapper;

	public ScatterUser(MainController mainController) {
		super(mainController, ChartType.SCATTER_USER);
		useLegend = true;
		useRangeDate = true;
		dateTimeWrapper = new DateTimeWrapper();
	}

	@Override
	public JSObject getOptions(JSObject jsObject) {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		jsObject.put("typeGraph", "'scatter'");
		jsObject.put("onClick",
				"function(t,a){let e=myChart.getElementAtEvent(t)[0];e&&javaConnector.dataPointSelection(e._datasetIndex)}");
		jsObject.put("scales",
				"{yAxes:[{type:'category'}],xAxes:[{type:'time',ticks:{min:'" + dateTimeWrapper.formatDate(dateStart)
						+ "',max:'" + dateTimeWrapper.formatDate(dateEnd.plusDays(1))
						+ "',maxTicksLimit:10},time:{minUnit:'day',parser:'" + dateTimeWrapper.getPattern() + "'}}]}");
		jsObject.put("tooltips",
				"{callbacks:{label:function(l,a){return a.datasets[l.datasetIndex].label+': '+ l.xLabel}}}");
		return jsObject;
	}

	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);
		Map<EnrolledUser, Map<E, List<LogLine>>> map = dataSet.getUserLogs(groupBy, selectedUsers, typeLogs, dateStart,
				dateEnd);
		JSObject data = new JSObject();
		JSArray datasets = new JSArray();

		JSArray labels = new JSArray();
		for (E typeLog : typeLogs) {
			labels.addWithQuote(dataSet.translate(typeLog));
		}

		data.put("labels", labels);

		for (EnrolledUser user : selectedUsers) {
			JSObject dataset = new JSObject();
			dataset.putWithQuote("label", user.getFullName());
			dataset.put("backgroundColor", hex(user.getId()));
			JSArray dataArray = new JSArray();
			for (int i = 0; i < typeLogs.size(); i++) {
				List<LogLine> logLines = map.get(user)
						.get(typeLogs.get(i));
				for (LogLine logLine : logLines) {
					JSObject point = new JSObject();
					point.putWithQuote("x", dateTimeWrapper.format(logLine.getTime()));
					point.putWithQuote("y", i);
					dataArray.add(point);
				}

			}
			dataset.put("data", dataArray);
			datasets.add(dataset);
		}

		data.put("datasets", datasets);
		return data.toString();
	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);
		List<?> rangeDates = groupBy.getRange(dateStart, dateEnd);
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);
			List<Integer> results = new ArrayList<>();
			for (int j = 0; j < rangeDates.size(); j++) {
				int result = 0;
				for (E type : typeLogs) {
					List<Integer> times = types.get(type);
					result += times.get(j);
				}
				results.add(result);

			}
			printer.print(selectedUser.getId());
			printer.print(selectedUser.getFullName());
			printer.printRecord(results);
		}

	}

	@Override
	protected String[] getCSVHeader() {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);
		List<String> range = groupBy.getRangeString(dateStart, dateEnd);
		range.add(0, "userid");
		range.add(1, "fullname");
		return range.toArray(new String[0]);
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);

			for (E type : typeLogs) {
				List<Integer> times = types.get(type);
				printer.print(selectedUser.getId());
				printer.print(selectedUser.getFullName());
				if (hasId()) {
					printer.print(type.hashCode());
				}
				printer.print(type);
				printer.printRecord(times);
			}

		}

	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);
		List<String> list = new ArrayList<>();
		list.add("userid");
		list.add("fullname");
		String selectedTab = tabPaneSelection.getSelectionModel()
				.getSelectedItem()
				.getText();
		if (hasId()) {
			list.add(selectedTab + "_id");
		}
		list.add(selectedTab);
		list.addAll(groupBy.getRangeString(dateStart, dateEnd));
		return list.toArray(new String[0]);
	}

}
