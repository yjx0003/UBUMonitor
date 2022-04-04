package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.ManageDuplicate;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class TotalBar extends PlotlyLog {

	public TotalBar(MainController mainController) {
		super(mainController, ChartType.TOTAL_BAR);
		useRangeDate = true;
		useLegend = true;
		useGeneralButton = true;
		useGroupButton = true;
	}


	@Override
	public GroupByAbstract<?> getGroupBy() {
		return actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);
	}

	@Override
	public <E> JSArray createData(List<E> typeLogs, DataSet<E> dataSet, List<EnrolledUser> selectedUsers,
			LocalDate dateStart, LocalDate dateEnd, GroupByAbstract<?> groupBy) {

		boolean horizontalMode = getConfigValue("horizontalMode");
		Map<E, Integer> logCounts;
		JSArray data = new JSArray();
		if (!selectedUsers.isEmpty()) {
			logCounts = getTypeLogsSum(typeLogs,
					dataSet.getUserLogsGroupedByLogElement(groupBy, selectedUsers, typeLogs, dateStart, dateEnd));
			data.add(createTrace(I18n.get("text.selectedUsers"), typeLogs, dataSet, logCounts, horizontalMode, true));
		}

		// filtered users
		logCounts = getTypeLogsSum(typeLogs,
				dataSet.getUserLogsGroupedByLogElement(groupBy, getFilteredUsers(), typeLogs, dateStart, dateEnd));
		data.add(createTrace(I18n.get("text.filteredusers"), typeLogs, dataSet, logCounts, horizontalMode,
				getGeneralButtonlActive()));

		// group users with the selected roles

		for (Group group : getSelectedGroups()) {
			logCounts = getTypeLogsSum(typeLogs,
					dataSet.getUserLogsGroupedByLogElement(groupBy,
							getUserWithRole(group.getEnrolledUsers(), getSelectedRoles()),
							typeLogs, dateStart, dateEnd));
			data.add(createTrace(group.getGroupName(), typeLogs, dataSet, logCounts, horizontalMode,
					getGroupButtonActive()));
		}
		return data;
	}

	private <E> JSObject createTrace(String name, List<E> typeLogs, DataSet<E> dataSet, Map<E, Integer> logCounts,
			boolean horizontalMode, boolean visible) {
		JSObject trace = new JSObject();
		JSArray x = new JSArray();
		JSArray y = new JSArray();
		ManageDuplicate manageDuplicate = new ManageDuplicate();
		for (E typeLog : typeLogs) {
			x.addWithQuote(manageDuplicate.getValue(dataSet.translate(typeLog)));
			y.add(logCounts.get(typeLog));
		}

		Plotly.createAxisValuesHorizontal(horizontalMode, trace, x, y);

		trace.put("type", "'bar'");
		trace.putWithQuote("name", name);
		JSObject marker = new JSObject();
		marker.put("color", rgba(name, 1.0));
		trace.put("marker", marker);
		trace.put("hovertemplate", "'<b>%{data.name}: </b>%{" + (horizontalMode ? "x" : "y") + "}<extra></extra>'");
		if (!visible) {
			trace.put("visible", "'legendonly'");
		}

		return trace;
	}

	@Override
	public <E> JSObject createLayout(List<E> typeLogs, DataSet<E> dataSet, LocalDate dateStart, LocalDate dateEnd,
			GroupByAbstract<?> groupBy) {

		boolean horizontalMode = getConfigValue("horizontalMode");
		JSObject layout = new JSObject();

		JSObject axis = new JSObject();
		axis.put("showgrid", true);
		axis.put("tickmode", "'array'");
		axis.put("type", "'category'");
		axis.put("tickson", "'boundaries'");
	
		JSObject xaxis = horizontalMode ? new JSObject() : axis;

		JSObject yaxis = horizontalMode ? axis : new JSObject();

		if (horizontalMode) {
			Plotly.defaultAxisValues(xaxis, getYAxisTitle(), "");
			Plotly.defaultAxisValues(yaxis, getXAxisTitle(), null);
			yaxis.putAll(axis);
		
			layout.put("xaxis", yaxis);
			layout.put("yaxis", xaxis);
		} else {
			Plotly.defaultAxisValues(xaxis, getXAxisTitle(), null);
			Plotly.defaultAxisValues(yaxis, getYAxisTitle(), "");
			xaxis.putAll(axis);
			
			layout.put("xaxis", xaxis);
			layout.put("yaxis", yaxis);
		}
		

		layout.put("barmode", "'group'");
		layout.put("hovermode", "'x unified'");
		return layout;
	}

	private <E> Map<E, Integer> getTypeLogsSum(List<E> typeLogs, Map<EnrolledUser, Map<E, Integer>> map) {
		Map<E, Integer> typeLogsCount = new HashMap<>();
		for (E typeLog : typeLogs) {
			int count = 0;
			for (Map<E, Integer> values : map.values()) {
				count += values.getOrDefault(typeLog, 0);
			}
			typeLogsCount.put(typeLog, count);
		}
		return typeLogsCount;
	}

	

	@Override
	public String getXAxisTitle() {
		return "<b>" + tabPaneSelection.getSelectionModel()
				.getSelectedItem()
				.getText() + "</b>";

	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		boolean generalActive = getGeneralButtonlActive();
		boolean groupActive = getGroupButtonActive();
		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);
		Map<EnrolledUser, Map<E, Integer>> map = dataSet.getUserLogsGroupedByLogElement(groupBy, selectedUsers,
				typeLogs, dateStart, dateEnd);

		Map<E, Integer> selectedUsersSum = getTypeLogsSum(typeLogs, map);
		Map<E, Integer> filteredUsersSum = null;
		if (generalActive) {
			map = dataSet.getUserLogsGroupedByLogElement(groupBy, getFilteredUsers(), typeLogs, dateStart, dateEnd);
			filteredUsersSum = getTypeLogsSum(typeLogs, map);
		}
		List<Map<E, Integer>> groupStats = null;
		if (groupActive) {

			groupStats = new ArrayList<>();
			for (Group group : getSelectedGroups()) {
				map = dataSet.getUserLogsGroupedByLogElement(groupBy,
						getUserWithRole(group.getEnrolledUsers(), getSelectedRoles()), typeLogs, dateStart, dateEnd);
				groupStats.add(getTypeLogsSum(typeLogs, map));
			}
		}

		boolean hasId = hasId();
		for (E typeLog : typeLogs) {

			if (hasId) {
				printer.print(typeLog.hashCode());
			}
			printer.print(typeLog);
			printer.print(selectedUsersSum.get(typeLog));
			if (generalActive) {
				printer.print(filteredUsersSum.get(typeLog));
			}
			if (groupActive) {
				for (Map<E, Integer> groupStat : groupStats) {
					printer.print(groupStat.get(typeLog));
				}
			}

			printer.println();
		}

	}

	@Override
	protected String[] getCSVHeader() {
		String selectedTab = tabPaneSelection.getSelectionModel()
				.getSelectedItem()
				.getText();

		boolean groupActive = getGroupButtonActive();
		boolean generalActive = getGeneralButtonlActive();

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

			for (Group group : getSelectedGroups()) {
				list.add(group.getGroupName());
			}
		}

		return list.toArray(new String[0]);
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<EnrolledUser> filteredUsers = getFilteredUsers();

		boolean groupActive = getGroupButtonActive();

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = actualCourse.getLogStats()
				.getByType(TypeTimes.DAY);
		Map<EnrolledUser, Map<E, Integer>> map = dataSet.getUserLogsGroupedByLogElement(groupBy, filteredUsers,
				typeLogs, dateStart, dateEnd);
		boolean hasId = hasId();
		for (EnrolledUser enrolledUser : filteredUsers) {
			for (E typeLog : typeLogs) {
				int sum = map.get(enrolledUser)
						.get(typeLog);

				printer.print(enrolledUser.getId());
				printer.print(enrolledUser.getFullName());
				if (hasId) {
					printer.print(typeLog.hashCode());
				}
				printer.print(typeLog);
				printer.print(sum);
				printer.print(selectedUsers.contains(enrolledUser) ? 1 : 0);

				if (groupActive) {
					Set<EnrolledUser> usersInRoles = getUserWithRole(getSelectedRoles());
					for (Group group : getSelectedGroups()) {
						printer.print(group.contains(enrolledUser) && usersInRoles.contains(enrolledUser) ? 1 : 0);
					}
				}
				printer.println();
			}
		}
	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		String selectedTab = tabPaneSelection.getSelectionModel()
				.getSelectedItem()
				.getText();

		boolean groupActive = getGroupButtonActive();

		List<String> list = new ArrayList<>();
		list.add("userid");
		list.add("fullname");
		if (hasId()) {
			list.add(selectedTab + "_id");
		}

		list.add(selectedTab);
		list.add("logs");
		list.add(I18n.get("text.selectedUsers"));
		if (groupActive) {
			for (Group group : getSelectedGroups()) {
				list.add(group.getGroupName());
			}
		}

		return list.toArray(new String[0]);
	}

}
