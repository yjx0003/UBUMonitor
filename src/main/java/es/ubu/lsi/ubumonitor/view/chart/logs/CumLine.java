package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.FirstGroupBy;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.LogAction;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class CumLine extends PlotlyLog {

	public CumLine(MainController mainController) {
		super(mainController, ChartType.CUM_LINE);
		useGeneralButton = true;
		useLegend = true;
		useGroupBy = true;
	}

	@Override
	public <E> JSArray createData(List<E> typeLogs, DataSet<E> dataSet, List<EnrolledUser> selectedUsers,
			LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy) {
		JSArray data = new JSArray();
		List<EnrolledUser> enrolledUsers = getUsers();

		List<String> rangeDates = groupBy.getRangeString(dateStart, dateEnd);
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);

		Map<E, List<Double>> means = dataSet.getMeans(groupBy, enrolledUsers, typeLogs, dateStart, dateEnd);

		createUsersTraces(data, selectedUsers, typeLogs, userCounts, rangeDates);
		createMeanTrace(data, typeLogs, means, rangeDates, I18n.get("chartlabel.generalMean"));

		return data;
	}

	@Override
	public <E> JSObject createLayout(List<E> typeLogs, DataSet<E> dataSet, LocalDate dateStart, LocalDate dateEnd,
			GroupByAbstract<?> groupBy) {
		JSObject layout = new JSObject();

		JSArray ticktext = new JSArray();

		List<String> rangeDate = groupBy.getRangeString(dateStart, dateEnd);
		for (String date : rangeDate) {
			ticktext.addWithQuote(date);
		}


		JSObject xaxis = new JSObject();
		Plotly.defaultAxisValues(xaxis, getXAxisTitle(), null);
		xaxis.put("nticks", 20);
		
		
		JSObject yaxis = new JSObject();
		Plotly.defaultAxisValues(yaxis, getYAxisTitle(), "");
		long max = getSuggestedMax(textFieldMax.getText());
		yaxis.put("range", max == 0 ? null : "[0," + (max * 1.1) + "]");

		layout.put("xaxis", xaxis);
		layout.put("yaxis", yaxis);
		layout.put("hovermode", "'closest'");
		return layout;

	}

	private JSObject createTrace(String name, JSArray x, JSArray y, boolean visible, String dash) {
		JSObject trace = new JSObject();
		JSObject line = new JSObject();
		JSObject marker = new JSObject();
		trace.putWithQuote("name", name);
		trace.put("type", "'scatter'");
		trace.put("x", x);
		trace.put("y", y);
		trace.put("line", line);
		trace.put("marker", marker);
		trace.put("hovertemplate", "'<b>%{x}<br>%{data.name}: </b>%{y}<extra></extra>'");
		if (!visible) {
			trace.put("visible", "'legendonly'");
		}

		line.putWithQuote("dash", dash);

		marker.put("color", rgb(name));
		marker.put("size", 6);

		return trace;
	}

	private <E> void createUsersTraces(JSArray data, List<EnrolledUser> selectedUsers, List<E> typeLogs,
			Map<EnrolledUser, Map<E, List<Integer>>> userCounts, List<String> rangeDates) {

		for (EnrolledUser selectedUser : selectedUsers) {
			JSArray x = new JSArray();
			JSArray y = new JSArray();

			Map<E, List<Integer>> types = userCounts.get(selectedUser);

			int result = 0;
			for (int j = 0; j < rangeDates.size(); j++) {

				for (E typeLog : typeLogs) {
					List<Integer> times = types.get(typeLog);
					result += times.get(j);
				}

				y.add(result);
				x.addWithQuote(rangeDates.get(j));
			}
			JSObject trace = createTrace(selectedUser.getFullName(), x, y, true, "solid");
			trace.put("userids", selectedUser.getId());
			data.add(trace);

		}

	}

	private <E> void createMeanTrace(JSArray data, List<E> typeLogs, Map<E, List<Double>> means,
			List<String> rangeDates, String name) {

		JSArray x = new JSArray();
		JSArray y = new JSArray();
		double cumResult = 0;
		for (int j = 0; j < rangeDates.size(); j++) {
			double result = 0;
			for (E typeLog : typeLogs) {
				List<Double> times = means.get(typeLog);
				result += times.get(j);
			}
			cumResult += result;
			x.addWithQuote(rangeDates.get(j));
			y.add(Math.round(cumResult * 100) / 100.0);
		}
		data.add(createTrace(name, x, y, getGeneralButtonlActive(), "dash"));

	}

	@Override
	public String calculateMax() {

		long maxYAxis = selectionController.typeLogsAction(new LogAction<Long>() {

			@Override
			public <E extends Serializable, T extends Serializable> Long action(List<E> logType, DataSet<E> dataSet,
					Function<GroupByAbstract<?>, FirstGroupBy<E, T>> function) {

				return function.apply(choiceBoxDate.getValue())
						.getCumulativeMax(getUsers(), logType, datePickerStart.getValue(), datePickerEnd.getValue());
			}
		});

		return Long.toString(maxYAxis);
	}

	@Override
	public String getXAxisTitle() {
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"), I18n.get(choiceBoxDate.getValue()
				.getTypeTime()));
	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<?> rangeDates = groupBy.getRange(dateStart, dateEnd);
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);
			List<Long> results = new ArrayList<>();
			long result = 0;
			for (int j = 0; j < rangeDates.size(); j++) {

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
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<String> range = groupBy.getRangeString(dateStart, dateEnd);
		range.add(0, "userid");
		range.add(1, "fullname");
		return range.toArray(new String[0]);
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, typeLogs,
				dateStart, dateEnd);
		boolean hasId = hasId();
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);

			for (E type : typeLogs) {
				List<Integer> times = types.get(type);
				printer.print(selectedUser.getId());
				printer.print(selectedUser.getFullName());

				if (hasId) {
					printer.print(type.hashCode());
				}
				printer.print(type);
				long sum = 0;
				for (long result : times) {
					sum += result;
					printer.print(sum);
				}
				printer.println();
			}

		}

	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
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
