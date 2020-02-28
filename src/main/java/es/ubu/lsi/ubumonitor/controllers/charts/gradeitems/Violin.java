package es.ubu.lsi.ubumonitor.controllers.charts.gradeitems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.charts.ChartType;
import es.ubu.lsi.ubumonitor.controllers.charts.ChartjsGradeItem;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;

public class Violin extends ChartjsGradeItem {

	public Violin(MainController mainController) {
		super(mainController, ChartType.VIOLIN);
	}

	@Override
	public String createDataset(List<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems) {
		StringBuilder stringBuilder = new StringBuilder();
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		stringBuilder.append("{labels:[");
		stringBuilder.append(UtilMethods.joinWithQuotes(selectedGradeItems));
		stringBuilder.append("],datasets:[");
		if (!selectedUser.isEmpty()) {
			createData(selectedUser, selectedGradeItems, stringBuilder, I18n.get("text.selectedUsers"), false);

		}
		if (useGeneralButton) {
			createData(Controller.getInstance().getActualCourse().getEnrolledUsers(), selectedGradeItems, stringBuilder,
					I18n.get("text.all"),
					!(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive"));
		}
		if (useGroupButton) {
			for (Group group : slcGroup.getCheckModel().getCheckedItems()) {

				createData(group.getEnrolledUsers(), selectedGradeItems, stringBuilder, group.getGroupName(),
						!(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive"));

			}

		}

		stringBuilder.append("]}");

		return stringBuilder.toString();
	}

	private void createData(Collection<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems,
			StringBuilder stringBuilder, String text, boolean hidden) {
		stringBuilder.append("{label:'" + text + "',");
		stringBuilder.append("borderColor:" + rgba(text, 0.7) + ",");
		stringBuilder.append("backgroundColor:" + rgba(text, OPACITY) + ",");

		stringBuilder.append("padding: 10,");
		stringBuilder.append("itemRadius: 2,");
		stringBuilder.append("itemStyle: 'circle',");
		stringBuilder.append("itemBackgroundColor:" + hex(text) + ",");
		stringBuilder.append("outlierColor:" + hex(text) + ",");
		stringBuilder.append("borderWidth: 1,");
		stringBuilder.append("outlierRadius : 5,");
		stringBuilder.append("hidden:" + hidden + ",");
		stringBuilder.append("data:[");

		for (GradeItem gradeItem : selectedGradeItems) {
			stringBuilder.append("[");
			boolean hasNonNaN = false;
			for (EnrolledUser user : selectedUser) {
				double grade = gradeItem.getEnrolledUserPercentage(user);
				if (!Double.isNaN(grade)) {
					stringBuilder.append(adjustTo10(grade) + ",");
					hasNonNaN = true;
				}
			}

			if (!hasNonNaN) {
				stringBuilder.append(-100000);
			}
			stringBuilder.append("],");
		}
		stringBuilder.append("]},");
	}

	@Override
	public int onClick(int index) {
		return -1; // do nothing at the moment
	}

	@Override
	public String getOptions() {
		JSObject jsObject = getDefaultOptions();
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		boolean useHorizontal = mainConfiguration.getValue(getChartType(), "horizontalMode");
		int tooltipDecimals = mainConfiguration.getValue(getChartType(), "tooltipDecimals");
		jsObject.putWithQuote("typeGraph", useHorizontal ? "horizontalViolin" : "violin");
		jsObject.put("tooltipDecimals", tooltipDecimals);
		
		String xLabel = useHorizontal ? getYScaleLabel() :getXScaleLabel();
		String yLabel = useHorizontal ? getXScaleLabel() : getYScaleLabel();
		
		jsObject.put("scales",
				"{yAxes:[{" + yLabel + ",ticks:{min:0}}],xAxes:[{" + xLabel + ",ticks:{min:0}}]}");
		return jsObject.toString();
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<String> header = new ArrayList<>();
		header.add("violin");
		header.add("stats");
		List<GradeItem> gradeItems = getSelectedGradeItems();
		for (GradeItem gradeItem : gradeItems) {
			header.add(gradeItem.getItemname());
		}

		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader(header.toArray(new String[0])))) {
			List<EnrolledUser> enrolledUser = getSelectedEnrolledUser();
			if (enrolledUser != null && !enrolledUser.isEmpty()) {
				exportCSV(printer, enrolledUser, gradeItems,"selected users");

			}
			exportCSV(printer, controller.getActualCourse().getEnrolledUsers(), gradeItems, "all");
			for (Group group : slcGroup.getCheckModel().getCheckedItems()) {
				exportCSV(printer, group.getEnrolledUsers(), gradeItems, group.getGroupName());
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
