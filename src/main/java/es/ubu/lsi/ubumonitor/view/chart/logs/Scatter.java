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
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.ManageDuplicate;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class Scatter extends PlotlyLog {

	public Scatter(MainController mainController) {
		super(mainController, ChartType.SCATTER);
		useLegend = true;
		useRangeDate = true;
	}

	@Override
	public GroupByAbstract<?> getGroupBy() {
		return actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);
	}

	@Override
	public <E> JSArray createData(List<E> typeLogs, DataSet<E> dataSet, List<EnrolledUser> selectedUsers,
			LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy) {
		JSArray data = new JSArray();
		Map<EnrolledUser, Map<E, List<LogLine>>> map = dataSet.getUserLogs(groupBy, selectedUsers, typeLogs, dateStart,
				dateEnd);

		for (E typeLog : typeLogs) {
			JSArray x = new JSArray();
			JSArray y = new JSArray();
			JSArray userids = new JSArray();
			ManageDuplicate manageDuplicate = new ManageDuplicate();
			for (EnrolledUser user : selectedUsers) {
				String userFullName = manageDuplicate.getValue(user.getFullName());
				List<LogLine> logLines = map.get(user)
						.get(typeLog);
				for (LogLine logLine : logLines) {
					y.addWithQuote(userFullName);
					x.addWithQuote(logLine.getTime()
							.toLocalDateTime());
					userids.add(user.getId());
				}
			}
			
			JSObject trace = createTrace(dataSet.translate(typeLog), x, y);
			trace.put("userids", userids);
			data.add(trace);
		}

		return data;
	}


	@Override
	public <E> JSObject createLayout(List<E> typeLogs, DataSet<E> dataSet, LocalDate dateStart, LocalDate dateEnd,
			GroupByAbstract<?> groupBy) {
		JSObject layout = new JSObject();

		JSObject xaxis = new JSObject();
		JSArray range = new JSArray();
		range.addWithQuote(dateStart);
		range.addWithQuote(dateEnd);
		Plotly.defaultAxisValues(xaxis, null, null);
		xaxis.put("type", "'date'");
		xaxis.put("range", range);

		JSObject yaxis = new JSObject();
		Plotly.defaultAxisValues(yaxis, null, null);
		yaxis.put("autorange", "'reversed'");
		yaxis.put("type", "'category'");

		layout.put("xaxis", xaxis);
		layout.put("yaxis", yaxis);

		layout.put("hovermode", "'closest'");
		return layout;
	}

	private JSObject createTrace(String name, JSArray x, JSArray y) {
		JSObject trace = new JSObject();

		JSObject marker = new JSObject();
		trace.putWithQuote("name", name);
		trace.put("type", "'scatter'");
		trace.put("mode", "'markers'");
		trace.put("x", x);
		trace.put("y", y);
		trace.put("marker", marker);
		trace.put("hovertemplate", "'<b>%{data.name}<br>%{y}: </b>%{x}<extra></extra>'");

		marker.put("color", rgb(name));

		return trace;
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
