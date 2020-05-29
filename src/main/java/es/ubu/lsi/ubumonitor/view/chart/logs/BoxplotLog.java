package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.Controller;
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
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.BoxPlot;

public class BoxplotLog extends ChartjsLog {

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
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {

		boolean groupActive = controller.getMainConfiguration()
				.getValue(MainConfiguration.GENERAL, "groupActive");

		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = Controller.getInstance()
				.getActualCourse()
				.getLogStats()
				.getByType(TypeTimes.DAY);

		return createData(typeLogs, dataSet, groupActive, selectedUsers, dateStart, dateEnd, groupBy);
	}

	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet, boolean groupActive,
			List<EnrolledUser> selectedUsers, LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy) {
		JSObject data = new JSObject();
		data.put("labels", createLabels(typeLogs, dataSet));

		JSArray datasets = new JSArray();

		if (!selectedUsers.isEmpty()) {
			Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, selectedUsers,
					typeLogs, dateStart, dateEnd);
			datasets.add(createDataset(selectedUsers, userCounts, typeLogs, I18n.get("text.selectedUsers"), false));
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

				datasets.add(createDataset(users, userCounts, typeLogs, group.getGroupName(), !groupActive));
			}

		}

		data.put("datasets", datasets);
		return data.toString();
	}

	public <E> JSArray createLabels(List<E> typeLogs, DataSet<E> dataSet) {
		JSArray labels = new JSArray();
		for (E typeLog : typeLogs) {
			labels.addWithQuote(dataSet.translate(typeLog));
		}
		return labels;
	}

	private <E> JSObject createDataset(List<EnrolledUser> users, Map<EnrolledUser, Map<E, List<Integer>>> userCounts,
			List<E> typeLogs, String text, boolean hidden) {

		Map<EnrolledUser, Map<E, Integer>> logCounts = transform(userCounts, typeLogs);
		JSObject dataset = BoxPlot.getDefaulDatasetProperties(text, hidden);

		JSArray usersArray = new JSArray();
		users.forEach(u -> usersArray.addWithQuote(u.getFullName()));
		dataset.put("users", usersArray);

		JSArray data = new JSArray();

		for (E typeLog : typeLogs) {
			JSArray dataArray = new JSArray();
			for (EnrolledUser user : users) {
				dataArray.add(logCounts.get(user)
						.get(typeLog));
			}
			data.add(dataArray);

		}
		dataset.put("data", data);

		return dataset;

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
		GroupByAbstract<?> groupBy = Controller.getInstance()
				.getActualCourse()
				.getLogStats()
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
		GroupByAbstract<?> groupBy = Controller.getInstance()
				.getActualCourse()
				.getLogStats()
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
	public String getOptions(JSObject jsObject) {

		MainConfiguration mainConfiguration = Controller.getInstance()
				.getMainConfiguration();
		boolean useHorizontal = mainConfiguration.getValue(getChartType(), "horizontalMode");
		jsObject.putWithQuote("typeGraph", useHorizontal ? "horizontalBoxplot" : "boxplot");
		jsObject.put("tooltipDecimals", 0);
		String xLabel = useHorizontal ? getYScaleLabel() : getXScaleLabel();
		String yLabel = useHorizontal ? getXScaleLabel() : getYScaleLabel();
		jsObject.put("scales", "{yAxes:[{" + yLabel + ",ticks:{suggestedMax:" + getSuggestedMax()
				+ ",stepSize:0}}],xAxes:[{" + xLabel + "}]}");
		JSObject callbacks = new JSObject();
		callbacks.put("afterTitle", "function(t,e){return e.datasets[t[0].datasetIndex].label}");
		callbacks.put("boxplotLabel", "boxplotLabel");
		jsObject.put("tooltips", "{callbacks:" + callbacks + "}");

		return jsObject.toString();
	}

	@Override
	public int onClick(int index) {
		return -1; // do nothing at the moment
	}

	@Override
	public String getXAxisTitle() {
		return tabPaneUbuLogs.getSelectionModel()
				.getSelectedItem()
				.getText();

	}
}
