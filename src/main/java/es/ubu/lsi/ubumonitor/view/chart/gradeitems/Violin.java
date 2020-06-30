package es.ubu.lsi.ubumonitor.view.chart.gradeitems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;

public class Violin extends ChartjsGradeItem {

	public Violin(MainController mainController) {
		super(mainController, ChartType.VIOLIN);
	}

	@Override
	public String createDataset(List<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems) {
		JSObject data = new JSObject();
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		data.put("labels", "[" + UtilMethods.joinWithQuotes(selectedGradeItems) + "]");
		JSArray datasets = new JSArray();

		if (!selectedUser.isEmpty()) {
			datasets.add(createData(selectedUser, selectedGradeItems, I18n.get("text.selectedUsers"), false));

		}

		datasets.add(createData(Controller.getInstance().getActualCourse().getEnrolledUsers(), selectedGradeItems,
				I18n.get("text.all"),
				!(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive")));

		for (Group group : slcGroup.getCheckModel().getCheckedItems()) {
			if (group != null) {
				datasets.add(createData(group.getEnrolledUsers(), selectedGradeItems, group.getGroupName(),
						!(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive")));
			}

		}

		data.put("datasets", datasets);

		return data.toString();
	}

	private JSObject createData(Collection<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems, String text,
			boolean hidden) {
		JSObject dataset = new JSObject();
		dataset.putWithQuote("label", text);
		dataset.put("borderColor", hex(text));
		dataset.put("backgroundColor", rgba(text, OPACITY));

		dataset.put("padding", 10);
		dataset.put("itemRadius", 2);
		dataset.putWithQuote("itemStyle", "circle");
		dataset.put("itemBackgroundColor", hex(text));
		dataset.put("outlierColor", hex(text));
		dataset.put("borderWidth", 1);
		dataset.put("outlierRadius", 5);
		dataset.put("hidden", hidden);
		JSArray data = new JSArray();

		for (GradeItem gradeItem : selectedGradeItems) {
			JSArray dataArray = new JSArray();
			boolean hasNonNaN = false;
			for (EnrolledUser user : selectedUser) {
				double grade = gradeItem.getEnrolledUserPercentage(user);
				if (!Double.isNaN(grade)) {
					dataArray.add(adjustTo10(grade));
					hasNonNaN = true;
				}
			}
			if (!hasNonNaN) {
				dataArray.add(-100000);
			}
			data.add(dataArray);
		}
		dataset.put("data", data);
		return dataset;
	}

	@Override
	public int onClick(int index) {
		return -1; // do nothing at the moment
	}

	@Override
	public String getOptions(JSObject jsObject) {
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		boolean useHorizontal = mainConfiguration.getValue(getChartType(), "horizontalMode");
	
		jsObject.putWithQuote("typeGraph", useHorizontal ? "horizontalViolin" : "violin");
		jsObject.put("tooltipDecimals", 2);

		String xLabel = useHorizontal ? getYScaleLabel() : getXScaleLabel();
		String yLabel = useHorizontal ? getXScaleLabel() : getYScaleLabel();

		jsObject.put("scales", "{yAxes:[{" + yLabel + ",ticks:{min:0}}],xAxes:[{" + xLabel + ",ticks:{min:0}}]}");
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

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader(header.toArray(new String[0])))) {
			List<EnrolledUser> enrolledUser = getSelectedEnrolledUser();
			if (enrolledUser != null && !enrolledUser.isEmpty()) {
				exportCSV(printer, enrolledUser, gradeItems, "selected users");

			}
			MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
			if((boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive")){
				exportCSV(printer, controller.getActualCourse().getEnrolledUsers(), gradeItems, "all");
			}
			
			if((boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive")){
				for (Group group : slcGroup.getCheckModel().getCheckedItems()) {
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
