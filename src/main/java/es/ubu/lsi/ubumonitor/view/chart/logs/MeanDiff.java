package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class MeanDiff extends PlotlyLog {

	private int max;

	public MeanDiff(MainController mainController) {
		super(mainController, ChartType.MEAN_DIFF);
		useLegend = true;
		useNegativeValues = true;
		useGroupBy = true;
		max = 0;
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
		List<Double> listMeans = createMeanList(typeLogs, means, rangeDates);
		createUsersTraces(data, selectedUsers, typeLogs, userCounts, listMeans, rangeDates);

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

		List<Integer> tickvals = IntStream.range(0, ticktext.size())
				.boxed()
				.collect(Collectors.toList());

		JSObject xaxis = new JSObject();
		Plotly.defaultAxisValues(xaxis, getXAxisTitle(), "");
		Plotly.createCategoryAxis(xaxis, tickvals, ticktext);

		JSObject yaxis = new JSObject();
		Plotly.defaultAxisValues(yaxis, getYAxisTitle(), "");
		long maxText = getSuggestedMax(textFieldMax.getText());
		yaxis.put("range", maxText == 0 ? "[" + (-max * 1.1) + "," + (max * 1.1) + "]"
				: "[" + (-maxText * 1.1) + "," + (maxText * 1.1) + "]");

		yaxis.put("zeroline", true);
		yaxis.put("zerolinecolor", colorToRGB(getConfigValue("zeroLineColor")));
		yaxis.put("zerolinewidth", getConfigValue("zeroLineWidth"));
		layout.put("xaxis", xaxis);
		layout.put("yaxis", yaxis);

		layout.put("hovermode", "'closest'");
		return layout;
	}

	private <E> void createUsersTraces(JSArray data, List<EnrolledUser> selectedUsers, List<E> typeLogs,
			Map<EnrolledUser, Map<E, List<Integer>>> userCounts, List<Double> listMeans, List<String> rangeDates) {
		List<Integer> maxValues = new ArrayList<>();
		for (EnrolledUser selectedUser : selectedUsers) {
			JSArray x = new JSArray();
			JSArray y = new JSArray();

			Map<E, List<Integer>> types = userCounts.get(selectedUser);

			long cum = 0;
			for (int j = 0; j < rangeDates.size(); j++) {
				long result = 0;
				for (E typeLog : typeLogs) {
					List<Integer> times = types.get(typeLog);
					result += times.get(j);
				}
				cum += result;
				x.add(j);
				double value = cum - listMeans.get(j);
				maxValues.add((int) Math.abs(value));
				y.add(value);
			}

			JSObject trace = createTrace(selectedUser.getFullName(), x, y, true, "solid");
			trace.put("userids", selectedUser.getId());
			data.add(trace);

		}
		max = (int) UtilMethods.getMax(maxValues);

	}

	private <T> List<Double> createMeanList(List<T> typeLogs, Map<T, List<Double>> means, List<String> rangeDates) {
		List<Double> results = new ArrayList<>();
		double cum = 0;
		for (int j = 0; j < rangeDates.size(); j++) {
			double result = 0;
			for (T typeLog : typeLogs) {
				List<Double> times = means.get(typeLog);
				result += times.get(j);
			}
			cum += result;
			results.add(cum);
		}
		return results;

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
		trace.put("hovertemplate", "'<b>%{x}<br>%{data.name}: </b>%{y:.2~f}<extra></extra>'");
		if (!visible) {
			trace.put("visible", "'legendonly'");
		}

		line.putWithQuote("dash", dash);

		marker.put("color", rgb(name));
		marker.put("size", 6);

		return trace;
	}

	@Override
	public String calculateMax() {

//		long maxYAxis = selectionController.typeLogsAction(new LogAction<Long>() {
//
//			@Override
//			public <E extends Serializable, T extends Serializable> Long action(List<E> logType, DataSet<E> dataSet,
//					Function<GroupByAbstract<?>, FirstGroupBy<E, T>> function) {
//
//				return function.apply(choiceBoxDate.getValue())
//						.getMeanDifferenceMax(getUsers(), logType, datePickerStart.getValue(),
//								datePickerEnd.getValue());
//			}
//		});

		return Integer.toString(max);
	}

	@Override
	public String getXAxisTitle() {
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"), I18n.get(choiceBoxDate.getValue()
				.getTypeTime()));
	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> selecteds) throws IOException {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<String> rangeDates = groupBy.getRangeString(dateStart, dateEnd);
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<E, List<Double>> means = dataSet.getMeans(groupBy, getUsers(), selecteds, dateStart, dateEnd);

		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
				dateStart, dateEnd);

		List<Double> listMeans = createMeanList(selecteds, means, rangeDates);
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);
			List<Double> results = new ArrayList<>();
			long result = 0;
			for (int j = 0; j < rangeDates.size(); j++) {

				for (E type : selecteds) {
					List<Integer> times = types.get(type);
					result += times.get(j);
				}
				results.add(result - listMeans.get(j));

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
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> selecteds)
			throws IOException {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
				dateStart, dateEnd);
		Map<E, List<Double>> means = dataSet.getMeans(groupBy, getUsers(), selecteds, dateStart, dateEnd);
		boolean hasId = hasId();
		for (EnrolledUser selectedUser : enrolledUsers) {
			Map<E, List<Integer>> types = userCounts.get(selectedUser);

			for (E type : selecteds) {
				List<Integer> times = types.get(type);
				List<Double> meanTimes = means.get(type);
				printer.print(selectedUser.getId());
				printer.print(selectedUser.getFullName());
				if (hasId) {
					printer.print(type.hashCode());
				}
				printer.print(type);
				long sum = 0;
				double meanSum = 0;
				for (int i = 0; i < times.size(); i++) {
					sum += times.get(i);
					meanSum += meanTimes.get(i);
					printer.print(sum - meanSum);
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
