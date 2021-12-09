package es.ubu.lsi.ubumonitor.view.chart.gradeitems;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;

public class CalificationBar extends Plotly {

	private TreeView<GradeItem> treeViewGradeItem;

	public CalificationBar(MainController mainController, TreeView<GradeItem> treeViewGradeItem) {
		super(mainController, ChartType.CALIFICATION_BAR);
		this.treeViewGradeItem = treeViewGradeItem;
		useLegend = true;
	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		boolean horizontalMode = getConfigValue("horizontalMode");
		double cutGrade = getGeneralConfigValue("cutGrade");

		List<List<EnrolledUser>> listNaN = new ArrayList<>();
		List<List<EnrolledUser>> listFail = new ArrayList<>();
		List<List<EnrolledUser>> listPass = new ArrayList<>();

		for (GradeItem gradeItem : gradeItems) {
			List<EnrolledUser> countNaN = new ArrayList<>();
			listNaN.add(countNaN);
			List<EnrolledUser> countLessCut = new ArrayList<>();
			listFail.add(countLessCut);
			List<EnrolledUser> countGreaterCut = new ArrayList<>();
			listPass.add(countGreaterCut);

			for (EnrolledUser user : users) {
				double grade = gradeItem.getEnrolledUserPercentage(user) / 10;
				if (Double.isNaN(grade)) {

					countNaN.add(user);
				} else if (grade < cutGrade) {
					countLessCut.add(user);
				} else {
					countGreaterCut.add(user);
				}
			}

		}

		data.add(createTrace(I18n.get("text.empty"), listNaN, gradeItems, getConfigValue("emptyGradeColor"),
				users.size(), horizontalMode));
		data.add(createTrace(I18n.get("text.fail") + "( <" + cutGrade + ")", listFail, gradeItems, getConfigValue("failGradeColor"),
				users.size(), horizontalMode));
		data.add(createTrace(I18n.get("text.pass") + "( >=" + cutGrade + ")", listPass, gradeItems, getConfigValue("passGradeColor"),
				users.size(), horizontalMode));

	}

	public JSObject createTrace(String name, List<List<EnrolledUser>> data, List<GradeItem> gradeItems, Color color,
			int nUsers, boolean horizontalMode) {
		JSObject trace = new JSObject();
		DecimalFormat df = new DecimalFormat("#.##");
		JSArray index = new JSArray();
		JSArray text = new JSArray();
		JSArray customdata = new JSArray();
		for (int i = 0; i < data.size(); i++) {
			List<EnrolledUser> users = data.get(i);
			index.add(i);
			text.add("'<b>'+toPercentage(" + users.size() + "," + nUsers + ")+'</b>'");
			StringBuilder usernames = new StringBuilder();
			
			GradeItem gradeItem = gradeItems.get(i);
			for (EnrolledUser user : users) {
				double grade = gradeItem.getEnrolledUserPercentage(user)/10;
				String value = Double.isNaN(grade) ? " (-)" : " (" + df.format(grade) + ")";
				usernames.append("<br>• ");
				usernames.append(user.getFullName());
				usernames.append(value);

			}
			customdata.addWithQuote(usernames);
		}

		createAxisValuesHorizontal(horizontalMode, trace, index, data.stream()
				.map(List::size)
				.collect(Collectors.toList()));

		trace.put("type", "'bar'");
		trace.putWithQuote("name", name);
		trace.put("text", text);
		trace.put("textposition", "'auto'");
		trace.put("customdata", customdata);
		trace.put("hovertemplate", "'<b>%{data.name}: </b>%{text}<br>%{customdata}<extra></extra>'");
		JSObject marker = new JSObject();
		marker.put("color", colorToRGB(color));
		trace.put("marker", marker);
		return trace;
	}

	@Override
	public void createLayout(JSObject layout) {
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);

		JSArray ticktext = new JSArray();
		for (GradeItem gradeItem : gradeItems) {
			ticktext.addWithQuote(gradeItem.getItemname());
		}

		horizontalMode(layout, ticktext, getConfigValue("horizontalMode"), getXAxisTitle(), getYAxisTitle(), null);
		layout.put("barmode", "'stack'");

	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<String> header = new ArrayList<>();
		header.add("stats");
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		for (GradeItem gradeItem : gradeItems) {
			header.add(gradeItem.getItemname());
		}

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader(header.toArray(new String[0])))) {
			double cutGrade = getGeneralConfigValue("cutGrade");
			List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
			List<Integer> countNaN = new ArrayList<>(gradeItems.size());
			List<Integer> countLessCut = new ArrayList<>(gradeItems.size());
			List<Integer> countGreaterCut = new ArrayList<>(gradeItems.size());
			List<Double> percentageNaN = new ArrayList<>(gradeItems.size());
			List<Double> percentageLess = new ArrayList<>(gradeItems.size());
			List<Double> percentageGreater = new ArrayList<>(gradeItems.size());

			for (GradeItem gradeItem : gradeItems) {
				int nan = 0;
				int less = 0;
				int greater = 0;
				for (EnrolledUser user : enrolledUsers) {
					double grade = adjustTo10(gradeItem.getEnrolledUserPercentage(user));
					if (Double.isNaN(grade)) {
						++nan;
					} else if (grade < cutGrade) {
						++less;
					} else {
						++greater;
					}
				}
				countNaN.add(nan);
				countLessCut.add(less);
				countGreaterCut.add(greater);
				percentageNaN.add(nan / (double) enrolledUsers.size());
				percentageLess.add(less / (double) enrolledUsers.size());
				percentageGreater.add(greater / (double) enrolledUsers.size());
			}
			printer.print("empty");
			printer.printRecord(countNaN);
			printer.print("fail");
			printer.printRecord(countLessCut);
			printer.print("pass");
			printer.printRecord(countGreaterCut);
			printer.print("empty %");
			printer.printRecord(percentageNaN);
			printer.print("fail %");
			printer.printRecord(percentageLess);
			printer.print("pass %");
			printer.printRecord(percentageGreater);

		}

	}

}
