package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.BoxPlot;

public class ViolinLog extends PlotlyLog {

	public ViolinLog(MainController mainController) {
		super(mainController, ChartType.VIOLIN_LOG);
		useGroupButton = true;
		useRangeDate = true;
		useLegend = true;
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
		JSObject plot = new JSObject();
		plot.put("data", createData(typeLogs, dataSet, groupActive, selectedUsers, dateStart, dateEnd, groupBy));
		plot.put("layout", createLayout(typeLogs, dataSet));
		return plot.toString();
	}

	private <E> JSObject createLayout(List<E> typeLogs, DataSet<E> dataSet) {
		JSObject layout = new JSObject();

		JSArray ticktext = new JSArray();

		for (E typeLog : typeLogs) {
			ticktext.addWithQuote(dataSet.translate(typeLog));
		}

		Plotly.horizontalMode(layout, ticktext, getConfigValue("horizontalMode"), getXAxisTitle(), getYAxisTitle(),
				null);
		layout.put("violinmode", "'group'");
		layout.put("hovermode", "'closest'");
		return layout;

	}

	public <E> JSArray createData(List<E> typeLogs, DataSet<E> dataSet, boolean groupActive,
			List<EnrolledUser> selectedUsers, LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy) {
		boolean horizontalMode = getConfigValue("horizontalMode");
		boolean boxVisible = getConfigValue("boxVisible");
		JSArray data = new JSArray();

		if (!selectedUsers.isEmpty()) {
			Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, selectedUsers,
					typeLogs, dateStart, dateEnd);
			data.add(createTrace(selectedUsers, userCounts, typeLogs, I18n.get("text.selectedUsers"), true,
					horizontalMode, boxVisible));
		}

		Set<EnrolledUser> userWithRole = getUsersInRoles(selectionUserController.getCheckComboBoxRole()
				.getCheckModel()
				.getCheckedItems());
		for (Group group : getSelectedGroups()) {

			List<EnrolledUser> users = getUserWithRole(group.getEnrolledUsers(), userWithRole);
			Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, users, typeLogs,
					dateStart, dateEnd);

			data.add(createTrace(users, userCounts, typeLogs, group.getGroupName(), groupActive, horizontalMode,
					boxVisible));

		}

		return data;
	}

	private <E> JSObject createTrace(List<EnrolledUser> users, Map<EnrolledUser, Map<E, List<Integer>>> userCounts,
			List<E> typeLogs, String name, boolean visible, boolean horizontalMode, boolean boxVisible) {

		JSObject trace = new JSObject();
		JSArray logValues = new JSArray();
		JSArray logValuesIndex = new JSArray();
		JSArray userNames = new JSArray();
		JSArray userids = new JSArray();

		Map<EnrolledUser, Map<E, Integer>> logCounts = transform(userCounts, typeLogs);

		for (EnrolledUser user : users) {
			Map<E, Integer> map = logCounts.get(user);
			for (int i = 0; i < typeLogs.size(); ++i) {

				logValues.add(map.get(typeLogs.get(i)));
				logValuesIndex.add(i);
				userNames.addWithQuote(user.getFullName());
				userids.add(user.getId());
			}
		}

		Plotly.createAxisValuesHorizontal(horizontalMode, trace, logValuesIndex, logValues);

		trace.put("type", "'violin'");
		trace.putWithQuote("name", name);
		trace.put("userids", userids);
		trace.put("text", userNames);
		trace.put("hovertemplate", BoxPlot.getHorizontalModeHoverTemplate(horizontalMode));
		JSObject line = new JSObject();
		line.put("color", rgb(name));
		trace.put("line", line);
		if (!visible) {
			trace.put("visible", "'legendonly'");
		}

		trace.put("box", "{visible:" + boxVisible + "}");
		trace.put("meanline", "{visible:true}");

		return trace;

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

	private <E> Map<EnrolledUser, Map<E, Integer>> transform(Map<EnrolledUser, Map<E, List<Integer>>> userCounts,
			List<E> typeLogs) {
		Map<EnrolledUser, Map<E, Integer>> map = new HashMap<>();
		for (Map.Entry<EnrolledUser, Map<E, List<Integer>>> entry : userCounts.entrySet()) {
			for (E typeLog : typeLogs) {
				Map<E, Integer> logsMap = map.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
				List<Integer> everyDayLogs = entry.getValue()
						.get(typeLog);
				int sum = everyDayLogs.stream()
						.mapToInt(Integer::intValue)
						.sum();
				logsMap.put(typeLog, sum);

				map.put(entry.getKey(), logsMap);
			}
		}
		return map;
	}

	private <E> Map<E, DescriptiveStatistics> getDescriptiveStatistics(Map<EnrolledUser, Map<E, Integer>> logCounts,
			List<EnrolledUser> selectedUsers, List<E> typeLogs) {
		Map<E, DescriptiveStatistics> descriptiveStatistics = new HashMap<>();

		for (EnrolledUser user : selectedUsers) {
			Map<E, Integer> map = logCounts.get(user);
			for (E typeLog : typeLogs) {
				descriptiveStatistics.computeIfAbsent(typeLog, k -> new DescriptiveStatistics())
						.addValue(map.getOrDefault(typeLog, 0));
			}
		}

		return descriptiveStatistics;
	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);

		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, selectedUsers, typeLogs,
				dateStart, dateEnd);

		Map<EnrolledUser, Map<E, Integer>> logCounts = transform(userCounts, typeLogs);
		Map<E, DescriptiveStatistics> descriptiveStatistics = getDescriptiveStatistics(logCounts, selectedUsers,
				typeLogs);
		for (E typeLog : typeLogs) {
			DescriptiveStatistics stats = descriptiveStatistics.get(typeLog);
			printer.print(dataSet.translate(typeLog));
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
		return new String[] { "item", "n", "min", "q1", "median", "q3", "max" };
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);

		Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, selectedUsers, typeLogs,
				dateStart, dateEnd);
		Map<EnrolledUser, Map<E, Integer>> logCounts = transform(userCounts, typeLogs);
		for (Map.Entry<EnrolledUser, Map<E, Integer>> entry : logCounts.entrySet()) {

			for (Map.Entry<E, Integer> entry2 : entry.getValue()
					.entrySet()) {
				printer.print(entry.getKey()
						.getId());
				printer.print(entry.getKey()
						.getFullName());
				printer.print(dataSet.translate(entry2.getKey()));
				printer.print(entry2.getValue());
				printer.println();
			}

		}

	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		return new String[] { "userid", "fullname", "item", "value" };
	}

	@Override
	public String getXAxisTitle() {
		return "<b>" + tabPaneSelection.getSelectionModel()
				.getSelectedItem()
				.getText() + "</b>";

	}

}
