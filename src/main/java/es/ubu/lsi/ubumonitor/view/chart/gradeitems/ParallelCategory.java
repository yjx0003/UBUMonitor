package es.ubu.lsi.ubumonitor.view.chart.gradeitems;

import java.io.IOException;
import java.text.Collator;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.ManageDuplicate;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import es.ubu.lsi.ubumonitor.view.chart.enrollment.EnrollmentBar;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;

public class ParallelCategory extends Plotly {

	private TreeView<GradeItem> treeViewGradeItem;
	private List<String> gradeTypes;

	public ParallelCategory(MainController mainController, TreeView<GradeItem> treeViewGradeItem) {
		super(mainController, ChartType.PARALLEL_CATEGORY);
		this.treeViewGradeItem = treeViewGradeItem;
		useGroupButton = true;
		gradeTypes = Arrays.asList(I18n.get("text.empty"), I18n.get("text.fail"), I18n.get("text.pass"));
	}

	@Override
	public void exportCSV(String path) throws IOException {

		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		double cutGrade = getGeneralConfigValue("cutGrade");
		boolean noGradeAsZero = getConfigValue("noGradeAsZero");
		Month startMonth = getConfigValue("startMonth");
		Month endMonth = getConfigValue("endMonth");
		ObservableList<Group> observableGroups = getConfigValue("groups");
		Set<Group> groups = new HashSet<>(observableGroups);

		Map<Integer, List<EnrolledUser>> yearCount = countYearWithUsers(selectedUsers, startMonth.getValue(),
				endMonth.getValue());

		Map<EnrolledUser, Group> usersGroup = getUserUniqueGroup(selectedUsers, groups);

		Map<EnrolledUser, DescriptiveStatistics> usersGrades = getUsersGrades(selectedUsers, gradeItems, noGradeAsZero);

		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("userid", "username",
				"year", "groupid", "groupName", "gradeMean", "gradeType"))) {
			for (Map.Entry<Integer, List<EnrolledUser>> entry : yearCount.entrySet()) {
				Integer year = entry.getKey();
				List<EnrolledUser> users = entry.getValue();
				for (EnrolledUser user : users) {
					printer.print(user.getId());
					printer.print(user.getFullName());

					printer.print(getYear(startMonth, endMonth, year));

					Group group = usersGroup.get(user);
					printer.print(group == null ? null : group.getGroupId());
					printer.print(group == null ? null : group.getGroupName());
					double gradeMean = usersGrades.get(user)
							.getMean();
					printer.print(gradeMean);

					int typeGrade = getCategoryGrade(usersGrades.get(user)
							.getMean(), cutGrade);
					printer.print(gradeTypes.get(typeGrade));
					printer.println();

				}
			}
		}
	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		boolean noGradeAsZero = getConfigValue("noGradeAsZero");
		Month startMonth = getConfigValue("startMonth");
		Month endMonth = getConfigValue("endMonth");
		ObservableList<Group> observableGroups = getConfigValue("groups");
		Set<Group> groups = getGroupButtonActive() ? new HashSet<>(observableGroups) : Collections.emptySet();

		Map<Integer, List<EnrolledUser>> yearCount = countYearWithUsers(users, startMonth.getValue(),
				endMonth.getValue());

		Map<EnrolledUser, Group> usersGroup = getUserUniqueGroup(users, groups);

		Map<EnrolledUser, DescriptiveStatistics> usersGrades = getUsersGrades(users, gradeItems, noGradeAsZero);

		data.add(createTrace(startMonth, endMonth, yearCount, usersGroup, usersGrades,
				getGeneralConfigValue("cutGrade"), getConfigValue("emptyGradeColor"), getConfigValue("failGradeColor"),
				getConfigValue("passGradeColor"), getConfigValue("moreInfoProbability")));
	}

	private JSObject createTrace(Month startMonth, Month endMonth, Map<Integer, List<EnrolledUser>> yearCount,
			Map<EnrolledUser, Group> usersGroup, Map<EnrolledUser, DescriptiveStatistics> usersGrades, double cutGrade,
			Color emptyColor, Color failColor, Color passColor, boolean moreInfoProbability) {

		JSObject trace = new JSObject();
		trace.put("type", "'parcats'");
		trace.put("hoveron", moreInfoProbability ? "'color'" : "'dimension'");
		trace.put("hoverinfo", "'all'");
		trace.put("arrangement", "'freeform'");
		trace.put("labelfont", "{size:16}");
		JSArray dimensions = new JSArray();
		trace.put("dimensions", dimensions);

		JSObject line = new JSObject();
		trace.put("line", line);
		line.put("cmax", 2);
		line.put("cmin", 0);
		JSArray color = new JSArray();
		line.put("color", color);
		JSArray colorscale = new JSArray();
		line.put("colorscale", colorscale);
		colorscale.add("[0," + colorToRGB(emptyColor) + "]");
		colorscale.add("[0.5," + colorToRGB(failColor) + "]");
		colorscale.add("[1," + colorToRGB(passColor) + "]");

		JSObject yearDimension = new JSObject();
		JSArray yearValues = new JSArray();
		createDimension(null, dimensions, yearDimension, yearValues);

		JSObject groupDimension = new JSObject();
		JSArray groupValues = new JSArray();
		createDimension(null, dimensions, groupDimension, groupValues);
		Set<Group> groups = new TreeSet<>(
				Comparator.nullsLast(Comparator.comparing(Group::getGroupName, Collator.getInstance())));
		groups.addAll(usersGroup.values());
		JSArray tickText = new JSArray();
		JSArray categoryarray = new JSArray();
		groupDimension.put("ticktext", tickText);
		groupDimension.put("categoryarray", categoryarray);
		for (Group group : groups) {
			if (group == null) {
				categoryarray.add(null);
				tickText.addWithQuote(I18n.get("noGroup"));
			} else {
				categoryarray.add(group.getGroupId());
				tickText.addWithQuote(group.getGroupName());
			}
		}

		JSObject gradeDimension = new JSObject();

		JSArray gradeValues = new JSArray();
		createDimension(I18n.get("cutGrade") + ": " + cutGrade, dimensions, gradeDimension, gradeValues);

		boolean hasGroup = usersGroup.values()
				.stream()
				.anyMatch(Objects::nonNull);
		for (Map.Entry<Integer, List<EnrolledUser>> entry : yearCount.entrySet()) {
			Integer year = entry.getKey();
			List<EnrolledUser> users = entry.getValue();
			for (EnrolledUser user : users) {
				yearValues.addWithQuote(getYear(startMonth, endMonth, year));
				int typeGrade = getCategoryGrade(usersGrades.get(user)
						.getMean(), cutGrade);
				gradeValues.addWithQuote(gradeTypes.get(typeGrade));
				color.add(typeGrade);
				if (hasGroup) {
					Group group = usersGroup.get(user);
					groupValues.add(group == null ? null : group.getGroupId());
				}
			}
		}

		return trace;

	}

	public String getYear(Month startMonth, Month endMonth, Integer year) {
		String yearString;
		if (year == null) {
			yearString = I18n.get("unknown");
		} else if (startMonth.getValue() > endMonth.getValue()) {
			yearString = year + "-" + (year + 1);
		} else {
			yearString = year.toString();
		}
		return yearString;
	}

	private int getCategoryGrade(double mean, double cutGrade) {
		if (Double.isNaN(mean)) {
			return 0;
		} else if (mean < cutGrade) {
			return 1;
		}
		return 2;
	}

	private void createDimension(String label, JSArray dimensions, JSObject dimension, JSArray values) {
		dimensions.add(dimension);
		dimension.putWithQuote("label", label);
		dimension.put("values", values);

	}

	private Map<EnrolledUser, DescriptiveStatistics> getUsersGrades(List<EnrolledUser> users,
			List<GradeItem> gradeItems, boolean noGradeAsZero) {
		Map<EnrolledUser, DescriptiveStatistics> map = new HashMap<>();
		for (EnrolledUser user : users) {
			DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
			map.put(user, descriptiveStatistics);
			for (GradeItem gradeItem : gradeItems) {
				double percentage = gradeItem.getEnrolledUserPercentage(user);
				if (!Double.isNaN(percentage)) {
					descriptiveStatistics.addValue(percentage / 10);

				} else if(noGradeAsZero) {
					descriptiveStatistics.addValue(0);
				}
			}
		}
		return map;
	}

	public Map<Group, String> groupLabel(Set<Group> groups) {
		Map<Group, String> groupsLabels = new HashMap<>();
		ManageDuplicate manageDuplicate = new ManageDuplicate();
		groups.forEach(g -> groupsLabels.put(g, manageDuplicate.getValue(g.getGroupName())));
		return groupsLabels;
	}

	/**
	 * Get user one of possible group if enrolled in multiple groups. If the user
	 * has two or more, selectes the biggest group. if not enrolled in none group
	 * the value is null
	 * 
	 * @param users  the users
	 * @param groups groups
	 */
	private Map<EnrolledUser, Group> getUserUniqueGroup(List<EnrolledUser> users, Set<Group> groups) {
		Map<EnrolledUser, Group> userGroup = new HashMap<>();
		for (EnrolledUser user : users) {
			Group group = groups.stream()
					.filter(g -> g.getEnrolledUsers()
							.contains(user))
					.max((a, b) -> Integer.compare(a.getEnrolledUsers()
							.size(),
							b.getEnrolledUsers()
									.size()))
					.orElse(null);
			userGroup.put(user, group);
		}
		return userGroup;
	}

	private Map<Integer, List<EnrolledUser>> countYearWithUsers(List<EnrolledUser> users, int startMonth,
			int endMonth) {
		Map<Integer, List<EnrolledUser>> map = new TreeMap<>(Comparator.nullsLast(Comparator.reverseOrder()));
		for (EnrolledUser user : users) {
			Integer year = EnrollmentBar.getYear(startMonth, endMonth, user.getFirstaccess());
			map.computeIfAbsent(year, k -> new ArrayList<>())
					.add(user);
		}
		return map;
	}

	@Override
	public void createLayout(JSObject layout) {
		// do nothing

	}

}
