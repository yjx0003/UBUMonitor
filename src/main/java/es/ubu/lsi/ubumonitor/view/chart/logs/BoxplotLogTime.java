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

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.Role;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.BoxPlot;

public class BoxplotLogTime extends ChartjsLog {

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
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		boolean groupActive = controller.getMainConfiguration()
				.getValue(MainConfiguration.GENERAL, "groupActive");

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = choiceBoxDate.getValue();

		List<String> rangeDates = groupBy.getRangeString(dateStart, dateEnd);

		JSObject data = new JSObject();

		data.put("labels", createLabels(rangeDates));

		JSArray datasets = new JSArray();

		if (!selectedUsers.isEmpty()) {
			Map<EnrolledUser, Map<E, List<Integer>>> userCounts = dataSet.getUserCounts(groupBy, selectedUsers,
					typeLogs, dateStart, dateEnd);
			datasets.add(
					createDataset(selectedUsers, userCounts, rangeDates.size(), I18n.get("text.selectedUsers"), false));
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

				datasets.add(createDataset(users, userCounts, rangeDates.size(), group.getGroupName(), !groupActive));
			}

		}

		data.put("datasets", datasets);
		return data.toString();
	}

	private <E> JSObject createDataset(List<EnrolledUser> users, Map<EnrolledUser, Map<E, List<Integer>>> userCounts,
			int rangeDateSize, String text, boolean hidden) {

		Map<EnrolledUser, List<Integer>> logCounts = transform(userCounts, rangeDateSize);

		JSObject dataset = BoxPlot.getDefaulDatasetProperties(text, hidden);
		dataset.put("outlierRadius", 5);
		JSArray usersArray = new JSArray();
		users.forEach(u -> usersArray.addWithQuote(u.getFullName()));
		dataset.put("users", usersArray);

		JSArray data = new JSArray();

		for (int i = 0; i < rangeDateSize; ++i) {
			JSArray dataArray = new JSArray();
			for (EnrolledUser user : users) {
				dataArray.add(logCounts.get(user)
						.get(i));
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
	public String getOptions(JSObject jsObject) {

		MainConfiguration mainConfiguration = Controller.getInstance()
				.getMainConfiguration();
		boolean useHorizontal = mainConfiguration.getValue(getChartType(), "horizontalMode");
		jsObject.putWithQuote("typeGraph", useHorizontal ? "horizontalBoxplot" : "boxplot");
		jsObject.put("tooltipDecimals", 0);
		String xLabel = useHorizontal ? getYScaleLabel() : getXScaleLabel();
		String yLabel = useHorizontal ? getXScaleLabel() : getYScaleLabel();
		jsObject.put("scales", "{yAxes:[{" + yLabel + ",ticks:{suggestedMax:" + getSuggestedMax(textFieldMax.getText())
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
		return MessageFormat.format(I18n.get(getChartType() + ".xAxisTitle"), I18n.get(choiceBoxDate.getValue()
				.getTypeTime()));
	}
}
