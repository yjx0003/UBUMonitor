package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class BoxplotLogTime extends PlotlyLog {

	public BoxplotLogTime(MainController mainController) {
		this(mainController, ChartType.BOXPLOT_LOG_TIME);
	}

	public BoxplotLogTime(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
		useGroupButton = true;
		useRangeDate = true;
		useLegend = true;
		useGroupBy = true;
	}

	@Override
	public <E> JSArray createData(List<E> typeLogs, DataSet<E> dataSet, List<EnrolledUser> selectedUsers,
			LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy) {

		boolean groupActive = getGroupButtonActive();
		boolean horizontalMode = getConfigValue("horizontalMode");
		boolean standardDeviation = getConfigValue("standardDeviation");
		boolean notched = getConfigValue("notched");
		JSArray data = new JSArray();
		List<String> range = groupBy.getRangeString(dateStart, dateEnd);
		if (!selectedUsers.isEmpty()) {
			Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, selectedUsers,
					typeLogs, dateStart, dateEnd);
			data.add(createTrace(selectedUsers, userCounts, range, I18n.get("text.selectedUsers"), true, horizontalMode,
					notched, standardDeviation));
		}

		for (Group group : getSelectedGroups()) {

			List<EnrolledUser> users = getUserWithRole(group.getEnrolledUsers(), getSelectedRoles());
			Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, users, typeLogs,
					dateStart, dateEnd);

			data.add(createTrace(users, userCounts, range, group.getGroupName(), groupActive, horizontalMode, notched,
					standardDeviation));

		}

		return data;
	}

	@Override
	public <E> JSObject createLayout(List<E> typeLogs, DataSet<E> dataSet, LocalDate dateStart, LocalDate dateEnd,
			GroupByAbstract<?> groupBy) {
		JSObject layout = new JSObject();

		boolean horizontalMode = getConfigValue("horizontalMode");
		long max = getSuggestedMax(textFieldMax.getText());

		JSObject xaxis = new JSObject();
		JSObject yaxis = new JSObject();
		if (horizontalMode) {
			yaxis.put("type", "'category'");
		} else {
			xaxis.put("type", "'category'");
		}

		Plotly.horizontalMode(layout, xaxis, yaxis, null, horizontalMode, getXAxisTitle(), getYAxisTitle(),
				max == 0 ? "" : "[0," + max + "]");

		layout.put("boxmode", "'group'");
		layout.put("hovermode", "'closest'");
		return layout;
	}

	private <E> JSObject createTrace(List<EnrolledUser> users, Map<EnrolledUser, Map<E, List<Integer>>> userCounts,
			List<String> rangeDates, String name, boolean visible, boolean horizontalMode, boolean notched,
			boolean standardDeviation) {

		JSObject trace = new JSObject();
		JSArray logValues = new JSArray();
		JSArray logValuesIndex = new JSArray();
		JSArray userNames = new JSArray();
		JSArray userids = new JSArray();

		Map<EnrolledUser, List<Integer>> logCounts = transform(userCounts, rangeDates.size());

		for (EnrolledUser user : users) {
			List<Integer> map = logCounts.get(user);
			for (int i = 0; i < rangeDates.size(); ++i) {

				logValues.add(map.get(i));
				logValuesIndex.addWithQuote(rangeDates.get(i));
				userNames.addWithQuote(user.getFullName());
				userids.add(user.getId());
			}
		}

		Plotly.createAxisValuesHorizontal(horizontalMode, trace, logValuesIndex, logValues);

		trace.put("type", "'box'");
		trace.put("boxpoints", "'all'");
		trace.put("pointpos", 0);
		trace.put("jitter", 1);
		trace.putWithQuote("name", name);
		trace.put("userids", userids);
		trace.put("text", userNames);
		trace.put("hovertemplate", "'<b>%{" + (horizontalMode ? "y" : "x") + "}<br>%{text}: </b>%{"
				+ (horizontalMode ? "x" : "y") + "}<extra></extra>'");
		JSObject marker = new JSObject();
		marker.put("color", rgb(name));
		trace.put("marker", marker);
		if (!visible) {
			trace.put("visible", "'legendonly'");
		}

		trace.put("notched", notched);
		trace.put("boxmean", standardDeviation ? "'sd'" : "true");

		return trace;

	}

	private <E> Map<EnrolledUser, List<Integer>> transform(Map<EnrolledUser, Map<E, List<Integer>>> userCounts,
			int rangeDateSize) {
		Map<EnrolledUser, List<Integer>> map = new HashMap<>();
		for (Map.Entry<EnrolledUser, Map<E, List<Integer>>> entry : userCounts.entrySet()) {
			List<Integer> list = new ArrayList<>();
			map.put(entry.getKey(), list);
			for (int j = 0; j < rangeDateSize; ++j) {
				int sum = 0;
				for (List<Integer> values : entry.getValue()
						.values()) {
					sum += values.get(j);
				}
				list.add(sum);
			}
		}
		return map;
	}

	private List<DescriptiveStatistics> getDescriptiveStatistics(Map<EnrolledUser, List<Integer>> logCounts,
			List<EnrolledUser> users, int size) {
		List<DescriptiveStatistics> descriptiveStatistics = Stream.generate(DescriptiveStatistics::new)
				.limit(size)
				.collect(Collectors.toList());
		for (EnrolledUser user : users) {
			List<Integer> counts = logCounts.get(user);
			for (int i = 0; i < size; ++i) {
				descriptiveStatistics.get(i)
						.addValue(counts.get(i));
			}
		}
		return descriptiveStatistics;

	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, selectedUsers, typeLogs,
				dateStart, dateEnd);
		List<String> rangeDatesString = groupBy.getRangeString(dateStart, dateEnd);
		Map<EnrolledUser, List<Integer>> logCounts = transform(userCounts, rangeDatesString.size());
		List<DescriptiveStatistics> descriptiveStatistics = getDescriptiveStatistics(logCounts, selectedUsers,
				rangeDatesString.size());
		for (int i = 0; i < descriptiveStatistics.size(); i++) {
			DescriptiveStatistics stats = descriptiveStatistics.get(i);
			printer.print(rangeDatesString.get(i));
			printer.print(stats.getN());
			printer.print(stats.getMin());
			printer.print(stats.getPercentile(25));
			printer.print(stats.getPercentile(50));
			printer.print(stats.getPercentile(75));
			printer.print(stats.getMax());
			printer.println();

		}

	}

	@Override
	protected String[] getCSVHeader() {
		return new String[] { "time", "n", "min", "q1", "median", "q3", "max" };
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, selectedUsers, typeLogs,
				dateStart, dateEnd);
		List<String> rangeDatesString = groupBy.getRangeString(dateStart, dateEnd);
		Map<EnrolledUser, List<Integer>> logCounts = transform(userCounts, rangeDatesString.size());
		for (Map.Entry<EnrolledUser, List<Integer>> entry : logCounts.entrySet()) {
			printer.print(entry.getKey()
					.getId());
			printer.print(entry.getKey()
					.getFullName());
			printer.printRecord(entry.getValue());
		}

	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();
		List<String> range = groupBy.getRangeString(dateStart, dateEnd);
		range.add(0, "userid");
		range.add(1, "fullname");
		return range.toArray(new String[0]);
	}

	@Override
	public String getXAxisTitle() {
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"), I18n.get(choiceBoxDate.getValue()
				.getTypeTime()));
	}

}
