package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVPrinter;

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

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected String[] getCSVHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		// TODO Auto-generated method stub
		return null;
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
}
