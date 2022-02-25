package es.ubu.lsi.ubumonitor.view.chart.gradeitems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

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
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;

public class Radar extends Plotly {

	private TreeView<GradeItem> treeViewGradeItem;
	private boolean noGrade;

	public Radar(MainController mainController, TreeView<GradeItem> treeViewGradeItem) {
		super(mainController, ChartType.RADAR);
		this.treeViewGradeItem = treeViewGradeItem;
		useGeneralButton = true;
		useLegend = true;
		useGroupButton = true;
	}

	@Override
	public void createData(JSArray data) {
		noGrade = getGeneralConfigValue("noGrade");
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);

		createUserTraces(data, selectedUsers, gradeItems);

		createCutGradeTrace(data, gradeItems, getGeneralConfigValue("cutGrade"), getConfigValue("cutGradeColor"));

		boolean generalActive = getGeneralButtonlActive();

		createMeanTrace(data, selectedUsers, gradeItems, generalActive);

		boolean groupActive = getGroupButtonActive();
		createGroupTraces(data, getSelectedGroups(), gradeItems, groupActive);

	}

	private void createUserTraces(JSArray data, List<EnrolledUser> users, List<GradeItem> gradeItems) {
		JSArray theta = createTheta(gradeItems);
		for (EnrolledUser user : users) {

			JSArray r = createRadio(gradeItems, gradeItem -> gradeItem.getEnrolledUserPercentage(user) / 10);

			JSObject trace = createTrace(user.getFullName(), theta, r, true, "solid");
			trace.put("userids", user.getId());
			data.add(trace);
		}

	}

	private void createMeanTrace(JSArray data, List<EnrolledUser> users, List<GradeItem> gradeItems, boolean visible) {
		Map<GradeItem, DescriptiveStatistics> map = new HashMap<>();
		for (GradeItem gradeItem : gradeItems) {
			DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
			for (EnrolledUser user : users) {
				double grade = gradeItem.getEnrolledUserPercentage(user) / 10;

				UtilMethods.noGradeValues(grade, descriptiveStatistics, noGrade);
			}

			map.put(gradeItem, descriptiveStatistics);

		}

		JSArray theta = createTheta(gradeItems);
		JSArray r = createRadio(gradeItems, gradeItem -> map.getOrDefault(gradeItem, EMPTY_DESCRIPTIVE_STATISTICS)
				.getMean());
		data.add(createTrace(I18n.get("text.meanselectedusers"), theta, r, visible, "dash"));
	}

	private void createGroupTraces(JSArray data, List<Group> groups, List<GradeItem> gradeItems, boolean visible) {
		JSArray theta = createTheta(gradeItems);
		for (Group group : groups) {
			Map<GradeItem, DescriptiveStatistics> map = new HashMap<>();
			for (GradeItem gradeItem : gradeItems) {
				DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
				
				for (EnrolledUser user : group.getEnrolledUsers()) {
					double grade = gradeItem.getEnrolledUserPercentage(user) / 10;
					UtilMethods.noGradeValues(grade, descriptiveStatistics, visible);
					map.put(gradeItem, descriptiveStatistics);
				}
			}
			JSArray r = createRadio(gradeItems,
					gradeItem -> map.getOrDefault(gradeItem, EMPTY_DESCRIPTIVE_STATISTICS)
							.getMean());

			data.add(createTrace(group.getGroupName(), theta, r, visible, "dot"));
		}
		
		
	}

	private void createCutGradeTrace(JSArray data, List<GradeItem> gradeItems, double cutGrade, Color color) {

		JSArray theta = createTheta(gradeItems);
		JSArray r = createRadio(gradeItems, gradeItem -> cutGrade);
		JSObject trace = createTrace(I18n.get("cutGrade") + " (" + cutGrade + ")", theta, r, true, "solid");
		trace.remove("fill");
		JSObject line = (JSObject) trace.get("line");
		line.put("color", colorToRGB(color));
		line.put("shape", "'spline'");
		trace.put("mode", "'lines'");
		trace.put("hoverinfo", "'none'");
		trace.remove("hovertemplate");
		data.add(trace);

	}

	private JSArray createRadio(List<GradeItem> gradeItems, ToDoubleFunction<GradeItem> function) {
		JSArray r = new JSArray();
		for (GradeItem gradeItem : gradeItems) {

			UtilMethods.noGradeValues(function.applyAsDouble(gradeItem), r, noGrade);

		}
		if (!r.isEmpty()) {
			r.add(r.get(0));
		}
		return r;
	}

	private JSObject createTrace(String name, JSArray theta, JSArray r, boolean visible, String dash) {
		JSObject trace = new JSObject();
		JSObject line = new JSObject();

		trace.putWithQuote("name", name);
		trace.put("type", "'scatterpolar'");
		trace.put("theta", theta);
		trace.put("r", r);
		trace.put("fill", "'toself'");
		trace.put("hoveron", "'points'");
		line.put("color", rgb(name));
		line.putWithQuote("dash", dash);
		trace.put("line", line);

		trace.put("hovertemplate", "'<b>%{data.name}<br>%{theta}: </b>%{r:.2f}<extra></extra>'");
		if (!visible) {
			trace.put("visible", "'legendonly'");
		}

		return trace;
	}

	private JSArray createTheta(List<GradeItem> gradeItems) {
		JSArray theta = new JSArray();
		for (GradeItem gradeItem : gradeItems) {

			theta.add(gradeItem.getId());
		}
		// add first element to the final
		if (!theta.isEmpty()) {

			theta.add(theta.get(0));
		}
		return theta;
	}

	@Override
	public void createLayout(JSObject layout) {
		JSObject polar = new JSObject();
		layout.put("polar", polar);
		JSObject angularaxis = new JSObject();
		JSObject radialaxis = new JSObject();
		polar.put("angularaxis", angularaxis);
		polar.put("radialaxis", radialaxis);

		JSArray tickvals = new JSArray();
		JSArray ticktext = new JSArray();

		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		for (GradeItem gradeItem : gradeItems) {
			ticktext.addWithQuote(gradeItem.getItemname());
			tickvals.add(gradeItem.getId());
		}

		radialaxis.put("angle", 90);
		radialaxis.put("tickangle", 90);
		radialaxis.put("range", "[-0.5,10.5]");

		createCategoryAxis(angularaxis, tickvals, ticktext);

	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<String> header = new ArrayList<>();
		header.add("userid");
		header.add("fullname");
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		for (GradeItem gradeItem : gradeItems) {
			header.add(gradeItem.getItemname());
		}

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader(header.toArray(new String[0])))) {
			for (EnrolledUser enrolledUser : getSelectedEnrolledUser()) {
				printer.print(enrolledUser.getId());
				printer.print(enrolledUser.getFullName());
				for (GradeItem gradeItem : gradeItems) {
					printer.print(adjustTo10(gradeItem.getEnrolledUserPercentage(enrolledUser)));
				}
				printer.println();
			}
		}
	}
}
