package es.ubu.lsi.ubumonitor.controllers.charts.logs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.charts.Chart;
import es.ubu.lsi.ubumonitor.controllers.charts.ChartType;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSet;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.TypeTimes;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;

public class TotalBar extends ChartjsLog {

	public TotalBar(MainController mainController) {
		super(mainController, ChartType.TOTAL_BAR);
		useRangeDate = true;
		useLegend = true;
		useGeneralButton = true;
		useGroupButton = true;
	}

	@Override
	public String getOptions() {
		JSObject jsObject = getDefaultOptions();

		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		boolean useHorizontal = mainConfiguration.getValue(getChartType(), "horizontalMode");
		jsObject.putWithQuote("typeGraph", useHorizontal ? "horizontalBar" : "bar");
		String xLabel = useHorizontal ? getYScaleLabel() : getXScaleLabel();
		String yLabel = useHorizontal ? getXScaleLabel() : getYScaleLabel();
		jsObject.put("scales", "{yAxes:[{" + yLabel + ",ticks:{stepSize:0}}],xAxes:[{" + xLabel + "}]}");
		jsObject.put("onClick", null);
		jsObject.put("tooltips", "{mode:'index'}");
		return jsObject.toString();
	}

	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {

		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = Controller.getInstance().getActualCourse().getLogStats().getByType(TypeTimes.DAY);
		Map<EnrolledUser, Map<E, List<Integer>>> map;

		JSObject data = new JSObject();
		data.put("labels", createLabels(typeLogs, dataSet));

		JSArray datasets = new JSArray();
		if (!selectedUsers.isEmpty()) {
			map = dataSet.getUserCounts(groupBy, selectedUsers, typeLogs, dateStart, dateEnd);
			datasets.add(createDataset(I18n.get("text.selectedUsers"), typeLogs, map, false));
		}

		map = dataSet.getUserCounts(groupBy, listParticipants.getItems(), typeLogs, dateStart, dateEnd);
		datasets.add(createDataset(I18n.get("text.filteredusers"), typeLogs, map,
				!(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive")));

		for (Group group : slcGroup.getCheckModel().getCheckedItems()) {
			if (group != null) {
				map = dataSet.getUserCounts(groupBy, new ArrayList<>(group.getEnrolledUsers()), typeLogs, dateStart,
						dateEnd);
				datasets.add(createDataset(group.getGroupName(), typeLogs, map,
						!(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive")));
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

	public <E> JSObject createDataset(String label, List<E> typeLogs, Map<EnrolledUser, Map<E, List<Integer>>> map,
			boolean hidden) {
		List<DescriptiveStatistics> result = getTypeLogsStats(typeLogs, map);

		JSObject dataset = new JSObject();
		dataset.putWithQuote("label", label);
		dataset.put("backgroundColor", rgba(label, Chart.OPACITY));
		dataset.put("borderColor", hex(label));
		dataset.put("borderWidth", 1);
		dataset.put("hidden", hidden);
		JSArray dataArray = new JSArray();

		for (int i = 0; i < typeLogs.size(); i++) {
			dataArray.add(result.get(i).getSum());
		}
		dataset.put("data", dataArray);

		return dataset;
	}

	public <E> List<DescriptiveStatistics> getTypeLogsStats(List<E> typeLogs,
			Map<EnrolledUser, Map<E, List<Integer>>> map) {
		List<DescriptiveStatistics> result = Stream.generate(DescriptiveStatistics::new).limit(typeLogs.size())
				.collect(Collectors.toList());
		for (Map<E, List<Integer>> values : map.values()) {
			for (int i = 0; i < typeLogs.size(); i++) {
				List<Integer> counts = values.get(typeLogs.get(i));
				DescriptiveStatistics descriptiveStatistics = result.get(i);
				int sum = counts.stream().mapToInt(Integer::intValue).sum(); // sum all logs between days
				descriptiveStatistics.addValue(sum);
			}
		}
		return result;
	}

	@Override
	public String getXAxisTitle() {
		return mainController.getTabPaneUbuLogs().getSelectionModel().getSelectedItem().getText();

	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		boolean generalActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive");
		boolean groupActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive");
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = Controller.getInstance().getActualCourse().getLogStats().getByType(TypeTimes.DAY);
		Map<EnrolledUser, Map<E, List<Integer>>> map = dataSet.getUserCounts(groupBy, selectedUsers, typeLogs,
				dateStart, dateEnd);

		List<DescriptiveStatistics> selectedUsersStats = getTypeLogsStats(typeLogs, map);
		List<DescriptiveStatistics> filteredUsersStats = null;
		if (generalActive) {
			map = dataSet.getUserCounts(groupBy, listParticipants.getItems(), typeLogs, dateStart, dateEnd);
			filteredUsersStats = getTypeLogsStats(typeLogs, map);
		}
		List<List<DescriptiveStatistics>> groupStats = null;
		if (groupActive) {
			groupStats = new ArrayList<>();
			for (Group group : slcGroup.getCheckModel().getCheckedItems()) {
				map = dataSet.getUserCounts(groupBy, new ArrayList<>(group.getEnrolledUsers()), typeLogs, dateStart,
						dateEnd);
				groupStats.add(getTypeLogsStats(typeLogs, map));
			}
		}

		boolean hasId = hasId();
		for (int i = 0; i < typeLogs.size(); i++) {
			E typeLog = typeLogs.get(i);
			if (hasId) {
				printer.print(typeLog.hashCode());
			}
			printer.print(typeLog);
			printer.print(selectedUsersStats.get(i).getSum());
			if (generalActive) {
				printer.print(filteredUsersStats.get(i).getSum());
			}
			if (groupActive) {
				for (List<DescriptiveStatistics> groupStat : groupStats) {
					printer.print(groupStat.get(i).getSum());
				}
			}

			printer.println();
		}

	}

	@Override
	protected String[] getCSVHeader() {
		String selectedTab = mainController.getTabPaneUbuLogs().getSelectionModel().getSelectedItem().getText();
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		boolean generalActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive");
		boolean groupActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive");

		List<String> list = new ArrayList<>();
		if (hasId()) {
			list.add(selectedTab + "_id");
		}
		list.add(selectedTab);
		list.add(I18n.get("text.selectedUsers"));
		if (generalActive) {
			list.add(I18n.get("text.filteredusers"));
		}
		if (groupActive) {
			for (Group group : slcGroup.getCheckModel().getCheckedItems()) {
				list.add(group.getGroupName());
			}
		}

		return list.toArray(new String[0]);
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<EnrolledUser> filteredUsers = listParticipants.getItems();
		List<EnrolledUser> users = Controller.getInstance().getActualCourse().getEnrolledUsers()
				.stream()
				.sorted(EnrolledUser.NAME_COMPARATOR)
				.collect(Collectors.toList());
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		boolean generalActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive");
		boolean groupActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive");
		
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = Controller.getInstance().getActualCourse().getLogStats().getByType(TypeTimes.DAY);
		Map<EnrolledUser, Map<E, List<Integer>>> map = dataSet.getUserCounts(groupBy, users, typeLogs,
				dateStart, dateEnd);
		boolean hasId = hasId();
		for (EnrolledUser enrolledUser : users) {
			for (E typeLog : typeLogs) {
				List<Integer> list = map.get(enrolledUser).get(typeLog);
				int sum = list.stream().mapToInt(Integer::intValue).sum();
				printer.print(enrolledUser.getId());
				printer.print(enrolledUser.getFullName());
				if (hasId) {
					printer.print(typeLog.hashCode());
				}
				printer.print(typeLog);
				printer.print(sum);
				printer.print(selectedUsers.contains(enrolledUser));
				if(generalActive) {
					printer.print(filteredUsers.contains(enrolledUser));
				}
				if(groupActive) {
					for(Group group: slcGroup.getCheckModel().getCheckedItems()) {
						printer.print(group.contains(enrolledUser));
					}
				}
				printer.println();
			}
		}
	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		String selectedTab = mainController.getTabPaneUbuLogs().getSelectionModel().getSelectedItem().getText();
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		boolean generalActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive");
		boolean groupActive = mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive");

		List<String> list = new ArrayList<>();
		list.add("userid");
		list.add("fullname");
		if (hasId()) {
			list.add(selectedTab + "_id");
		}

		list.add(selectedTab);
		list.add("logs");
		list.add(I18n.get("text.selectedUsers"));
		if (generalActive) {
			list.add(I18n.get("text.filteredusers"));
		}
		if (groupActive) {
			for (Group group : slcGroup.getCheckModel().getCheckedItems()) {
				list.add(group.getGroupName());
			}
		}

		return list.toArray(new String[0]);
	}

}
