package es.ubu.lsi.ubumonitor.view.chart.gradeitems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.control.TreeView;

public class BoxPlot extends Plotly {

	private TreeView<GradeItem> treeViewGradeItem;

	public BoxPlot(MainController mainController, TreeView<GradeItem> treeViewGradeItem) {
		super(mainController, ChartType.BOXPLOT);
		this.treeViewGradeItem = treeViewGradeItem;
		useLegend = true;
		useGroupButton = true;
		useGeneralButton = true;
	}

	@Override
	public void createData(JSArray data) {

		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		boolean useHorizontal = getConfigValue("horizontalMode");
		boolean standardDeviation = getConfigValue("standardDeviation");
		boolean notched = getConfigValue("notched");

		data.add(createTrace(users, gradeItems, I18n.get("text.selectedUsers"), useHorizontal, true, standardDeviation,
				notched));
		data.add(createTrace(actualCourse.getEnrolledUsers(), gradeItems, I18n.get("text.all"), useHorizontal,
				getGeneralConfigValue("generalActive"), standardDeviation, notched));
		for (Group group : slcGroup.getCheckModel()
				.getCheckedItems()) {
			boolean groupActive = getGeneralConfigValue("groupActive");
			if (group != null) {
				data.add(createTrace(group.getEnrolledUsers(), gradeItems, group.getGroupName(), useHorizontal,
						groupActive, standardDeviation, notched));
			}
		}

	}

	public JSObject createTrace(Collection<EnrolledUser> users, List<GradeItem> gradeItems, String name,
			boolean useHorizontal, boolean visible, boolean standardDeviation, boolean notched) {
		JSObject trace = new JSObject();
		JSArray grades = new JSArray();
		JSArray gradeItemIndex = new JSArray();
		JSArray userNames = new JSArray();
		JSArray userids = new JSArray();

		for (int i = 0; i < gradeItems.size(); ++i) {
			GradeItem gradeItem = gradeItems.get(i);

			for (EnrolledUser user : users) {

				double grade = gradeItem.getEnrolledUserPercentage(user);
				if (!Double.isNaN(grade)) {
					grades.add(gradeItem.getEnrolledUserPercentage(user) / 10);
					gradeItemIndex.add(i);
					userids.add(user.getId());
					userNames.addWithQuote(user.getFullName());
				}

			}
		}

		if (useHorizontal) {
			trace.put("y", gradeItemIndex);
			trace.put("x", grades);
			trace.put("orientation", "'h'");
		} else {
			trace.put("x", gradeItemIndex);
			trace.put("y", grades);
		}

		trace.put("type", "'box'");
		trace.put("boxpoints", "'all'");
		trace.put("pointpos", 0);
		trace.put("jitter", 1);
		trace.putWithQuote("name", name);
		trace.put("userids", userids);
		trace.put("text", userNames);
		trace.put("hovertemplate", "'<b>%{" + (useHorizontal ? "x" : "y") + "}<br>%{text}: </b>%{"
				+ (useHorizontal ? "x" : "y") + ":.2f}<extra></extra>'");
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

	@Override
	public void createLayout(JSObject layout) {
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);

		JSArray ticktext = new JSArray();
		for (GradeItem gradeItem : gradeItems) {
			ticktext.addWithQuote(gradeItem.getItemname());
		}

		horizontalMode(layout, ticktext, getConfigValue("horizontalMode"), getXAxisTitle(), getYAxisTitle(),
				"[-0.5,10.5]");
		layout.put("boxmode", "'group'");
		layout.put("hovermode", "'closest'");

	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<String> header = new ArrayList<>();
		header.add("boxplot");
		header.add("stats");
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		for (GradeItem gradeItem : gradeItems) {
			header.add(gradeItem.getItemname());
		}

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader(header.toArray(new String[0])))) {
			List<EnrolledUser> enrolledUser = getSelectedEnrolledUser();
			if (enrolledUser != null && !enrolledUser.isEmpty()) {
				exportCSV(printer, enrolledUser, gradeItems, "selected users");

			}
			if ((boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive")) {
				exportCSV(printer, controller.getActualCourse()
						.getEnrolledUsers(), gradeItems, "all");
			}

			if ((boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive")) {
				for (Group group : slcGroup.getCheckModel()
						.getCheckedItems()) {
					exportCSV(printer, group.getEnrolledUsers(), gradeItems, group.getGroupName());
				}
			}

		}

	}

	public void exportCSV(CSVPrinter printer, Collection<EnrolledUser> enrolledUsers, List<GradeItem> gradeItems,
			String boxplot) throws IOException {
		List<Long> count = new ArrayList<>(gradeItems.size());
		List<Integer> sc = new ArrayList<>(gradeItems.size());
		List<Double> avg = new ArrayList<>(gradeItems.size());
		List<Double> min = new ArrayList<>(gradeItems.size());
		List<Double> q1 = new ArrayList<>(gradeItems.size());
		List<Double> q2 = new ArrayList<>(gradeItems.size());
		List<Double> q3 = new ArrayList<>(gradeItems.size());
		List<Double> max = new ArrayList<>(gradeItems.size());
		for (GradeItem gradeItem : gradeItems) {
			DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
			int scCount = 0;
			for (EnrolledUser enrolledUser : enrolledUsers) {
				double grade = gradeItem.getEnrolledUserPercentage(enrolledUser);
				if (Double.isNaN(grade)) {
					scCount++;
				} else {

					descriptiveStatistics.addValue(grade / 10);
				}
			}
			count.add(descriptiveStatistics.getN());
			sc.add(scCount);
			min.add(descriptiveStatistics.getMin());
			q1.add(descriptiveStatistics.getPercentile(25));
			q2.add(descriptiveStatistics.getPercentile(50));
			q3.add(descriptiveStatistics.getPercentile(75));
			max.add(descriptiveStatistics.getMax());
			avg.add(descriptiveStatistics.getMean());
		}
		exportCSV(printer, boxplot, "graded", count);
		exportCSV(printer, boxplot, "not graded", sc);
		exportCSV(printer, boxplot, "mean", avg);
		exportCSV(printer, boxplot, "min", min);
		exportCSV(printer, boxplot, "q1", q1);
		exportCSV(printer, boxplot, "q2", q2);
		exportCSV(printer, boxplot, "q3", q3);
		exportCSV(printer, boxplot, "max", max);

	}

	public <T> void exportCSV(CSVPrinter printer, String boxplot, String stat, List<T> count) throws IOException {
		printer.print(boxplot);
		printer.print(stat);
		printer.printRecord(count);
	}

}
