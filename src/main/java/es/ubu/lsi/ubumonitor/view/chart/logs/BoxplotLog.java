package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class BoxplotLog extends PlotlyLog {

	public BoxplotLog(MainController mainController) {
		this(mainController, ChartType.BOXPLOT_LOG);
	}

	public BoxplotLog(MainController mainController, ChartType chartType) {
		super(mainController, chartType);

		useGroupButton = true;
		useRangeDate = true;
		useLegend = true;
	}

	@Override
	public GroupByAbstract<?> getGroupBy() {
		return actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);
	}

	@Override
	public <E> JSArray createData(List<E> typeLogs, DataSet<E> dataSet, List<EnrolledUser> selectedUsers,
			LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy) {
		boolean groupActive = getGroupButtonActive();
		boolean horizontalMode = getConfigValue("horizontalMode");
		boolean standardDeviation = getConfigValue("standardDeviation");
		boolean notched = getConfigValue("notched");
		JSArray data = new JSArray();

		if (!selectedUsers.isEmpty()) {
			Map<EnrolledUser, Map<E, Integer>> userCounts = dataSet.getUserLogsGroupedByLogElement(groupBy,
					selectedUsers, typeLogs, dateStart, dateEnd);
			data.add(createTrace(selectedUsers, userCounts, typeLogs, I18n.get("text.selectedUsers"), true,
					horizontalMode, notched, standardDeviation));
		}

		for (Group group : getSelectedGroups()) {

			List<EnrolledUser> users = getUserWithRole(group.getEnrolledUsers(), getSelectedRoles());
			Map<EnrolledUser, Map<E, Integer>> userCounts = dataSet.getUserLogsGroupedByLogElement(groupBy, users,
					typeLogs, dateStart, dateEnd);

			data.add(createTrace(users, userCounts, typeLogs, group.getGroupName(), groupActive, horizontalMode,
					notched, standardDeviation));

		}

		return data;
	}

	@Override
	public <E> JSObject createLayout(List<E> typeLogs, DataSet<E> dataSet, LocalDate dateStart, LocalDate dateEnd,
			GroupByAbstract<?> groupBy) {
		JSObject layout = new JSObject();

		JSArray ticktext = new JSArray();

		for (E typeLog : typeLogs) {
			ticktext.addWithQuote(dataSet.translate(typeLog));
		}

		Plotly.horizontalMode(layout, ticktext, getConfigValue("horizontalMode"), getXAxisTitle(), getYAxisTitle(),
				null);
		layout.put("boxmode", "'group'");
		layout.put("hovermode", "'closest'");
		return layout;

	}

	private <E> JSObject createTrace(List<EnrolledUser> users, Map<EnrolledUser, Map<E, Integer>> userCounts,
			List<E> typeLogs, String name, boolean visible, boolean horizontalMode, boolean notched,
			boolean standardDeviation) {

		JSObject trace = new JSObject();
		JSArray logValues = new JSArray();
		JSArray logValuesIndex = new JSArray();
		JSArray userNames = new JSArray();
		JSArray userids = new JSArray();

		for (EnrolledUser user : users) {
			Map<E, Integer> map = userCounts.get(user);
			for (int i = 0; i < typeLogs.size(); ++i) {

				logValues.add(map.get(typeLogs.get(i)));
				logValuesIndex.add(i);
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

		Map<EnrolledUser, Map<E, Integer>> userCounts = dataSet.getUserLogsGroupedByLogElement(groupBy, selectedUsers,
				typeLogs, dateStart, dateEnd);

		Map<E, DescriptiveStatistics> descriptiveStatistics = getDescriptiveStatistics(userCounts, selectedUsers,
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

		Map<EnrolledUser, Map<E, Integer>> userCounts = dataSet.getUserLogsGroupedByLogElement(groupBy, selectedUsers,
				typeLogs, dateStart, dateEnd);

		for (Map.Entry<EnrolledUser, Map<E, Integer>> entry : userCounts.entrySet()) {

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
