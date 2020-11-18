package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.Role;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
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
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {
		boolean groupActive = controller.getMainConfiguration()
				.getValue(MainConfiguration.GENERAL, "groupActive");

		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);
		List<String> range = groupBy.getRangeString(dateStart, dateEnd);
		JSObject plot = new JSObject();
		plot.put("data",
				createData(typeLogs, dataSet, groupActive, selectedUsers, dateStart, dateEnd, groupBy, range.size()));
		plot.put("layout", createLayout(range));
		return plot.toString();
	}

	public <E> JSArray createData(List<E> typeLogs, DataSet<E> dataSet, boolean groupActive,
			List<EnrolledUser> selectedUsers, LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy,
			int size) {
		boolean horizontalMode = getConfigValue("horizontalMode");
		boolean standardDeviation = getConfigValue("standardDeviation");
		boolean notched = getConfigValue("notched");
		JSArray data = new JSArray();

		if (!selectedUsers.isEmpty()) {
			Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, selectedUsers,
					typeLogs, dateStart, dateEnd);
			data.add(createTrace(selectedUsers, userCounts, size, I18n.get("text.selectedUsers"), true, horizontalMode,
					notched, standardDeviation));
		}

		Set<EnrolledUser> userWithRole = getUsersInRoles(selectionUserController.getCheckComboBoxRole()
				.getCheckModel()
				.getCheckedItems());
		for (Group group : slcGroup.getCheckModel()
				.getCheckedItems()) {
			if (group != null) {
				List<EnrolledUser> users = getUserWithRole(group.getEnrolledUsers(), userWithRole);
				Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, users, typeLogs,
						dateStart, dateEnd);

				data.add(createTrace(users, userCounts, size, group.getGroupName(), groupActive, horizontalMode,
						notched, standardDeviation));
			}

		}

		return data;
	}

	private <E> JSObject createTrace(List<EnrolledUser> users, Map<EnrolledUser, Map<E, List<Integer>>> userCounts,
			int rangeDateSize, String name, boolean visible, boolean horizontalMode, boolean notched,
			boolean standardDeviation) {

		JSObject trace = new JSObject();
		JSArray logValues = new JSArray();
		JSArray logValuesIndex = new JSArray();
		JSArray userNames = new JSArray();
		JSArray userids = new JSArray();

		Map<EnrolledUser, List<Integer>> logCounts = transform(userCounts, rangeDateSize);

		for (EnrolledUser user : users) {
			List<Integer> map = logCounts.get(user);
			for (int i = 0; i < rangeDateSize; ++i) {

				logValues.add(map.get(i));
				logValuesIndex.add(i);
				userNames.addWithQuote(user.getFullName());
				userids.add(user.getId());
			}
		}

		if (horizontalMode) {
			trace.put("y", logValuesIndex);
			trace.put("x", logValues);
			trace.put("orientation", "'h'");
		} else {
			trace.put("x", logValuesIndex);
			trace.put("y", logValues);
		}

		trace.put("type", "'box'");
		trace.put("boxpoints", "'all'");
		trace.put("pointpos", 0);
		trace.put("jitter", 1);
		trace.putWithQuote("name", name);
		trace.put("userids", userids);
		trace.put("text", userNames);
		trace.put("hovertemplate", "'<b>%{" + (horizontalMode ? "x" : "y") + "}<br>%{text}: </b>%{"
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

	private JSObject createLayout(List<String> rangeDate) {
		JSObject layout = new JSObject();

		JSArray ticktext = new JSArray();

		for (String date : rangeDate) {
			ticktext.addWithQuote(date);
		}

		long max = getSuggestedMax(textFieldMax.getText());
		Plotly.horizontalMode(layout, ticktext, getConfigValue("horizontalMode"), getXAxisTitle(), getYAxisTitle(),
				max == 0 ? null : "[0," + max + "]");
		layout.put("boxmode", "'group'");
		layout.put("hovermode", "'closest'");
		return layout;

	}

	private Set<EnrolledUser> getUsersInRoles(Collection<Role> roles) {
		return roles.stream()
				.map(Role::getEnrolledUsers)
				.flatMap(Set::stream)
				.distinct()
				.collect(Collectors.toSet());

	}

	private List<EnrolledUser> getUserWithRole(Collection<EnrolledUser> groupUsers,
			Collection<EnrolledUser> usersInRoles) {

		return groupUsers.stream()
				.filter(usersInRoles::contains)
				.collect(Collectors.toList());

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
