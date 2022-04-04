package es.ubu.lsi.ubumonitor.view.chart.gradeitems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

public class Line extends Plotly {

	private TreeView<GradeItem> treeViewGradeItem;

	public Line(MainController mainController, TreeView<GradeItem> treeViewGradeItem) {
		super(mainController, ChartType.LINE);
		this.treeViewGradeItem = treeViewGradeItem;
		useGeneralButton = true;
		useLegend = true;
		useGroupButton = true;
	}

	@Override
	public void createData(JSArray data) {

		boolean noGrade = getGeneralConfigValue("noGrade");
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<EnrolledUser> filteredUsers = getFilteredUsers();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		
		createUserTraces(data, selectedUsers, gradeItems, noGrade);

		boolean generalActive = getGeneralButtonlActive();
		createMeanTrace(data, selectedUsers, gradeItems, generalActive, noGrade, I18n.get("text.meanselectedusers"));
		createMeanTrace(data, filteredUsers, gradeItems, generalActive, noGrade, I18n.get("text.meanfilteredusers"));
		
		boolean groupActive = getGroupButtonActive();
		createGroupTraces(data, getSelectedGroups(), gradeItems, groupActive, noGrade);
	}

	private void createUserTraces(JSArray data, List<EnrolledUser> users, List<GradeItem> gradeItems, boolean noGrade) {

		for (EnrolledUser user : users) {
			JSArray x = new JSArray();
			JSArray y = new JSArray();

			for (GradeItem gradeItem : gradeItems) {
				double grade = gradeItem.getEnrolledUserPercentage(user) / 10;
				
				UtilMethods.noGradeValues(grade, y, noGrade);
				
				x.add(gradeItem.getId());
			}

			data.add(createTrace(user.getFullName(), x, y, true, "solid"));
		}

	}

	private void createMeanTrace(JSArray data, List<EnrolledUser> users, 
			List<GradeItem> gradeItems, boolean visible, boolean noGrade, String traceName) {

		JSArray x = new JSArray();
		JSArray y = new JSArray();
		for (GradeItem gradeItem : gradeItems) {
			DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
			for(EnrolledUser user: users) {
				double grade = gradeItem.getEnrolledUserPercentage(user) / 10;
				
				UtilMethods.noGradeValues(grade, descriptiveStatistics, noGrade);
			}
				
			
			y.add(descriptiveStatistics.getMean());
			
			x.add(gradeItem.getId());
		}

		data.add(createTrace(traceName, x, y, visible, "dash"));
	}

	private void createGroupTraces(JSArray data, List<Group> groups, List<GradeItem> gradeItems,
			boolean visible, boolean noGrade) {
		for (Group group : groups) {
			JSArray x = new JSArray();
			JSArray y = new JSArray();
			Set<EnrolledUser> users = group.getEnrolledUsers();
			for (GradeItem gradeItem : gradeItems) {
				DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
				for(EnrolledUser user: users) {
					double grade = gradeItem.getEnrolledUserPercentage(user) / 10;
					
					UtilMethods.noGradeValues(grade, descriptiveStatistics, noGrade);
				}
					
				
				y.add(descriptiveStatistics.getMean());
				
				x.add(gradeItem.getId());
			}
			data.add(createTrace(group.getGroupName(), x, y, visible, "dot"));
		}
	}

	private JSObject createTrace(String name, JSArray x, JSArray y, boolean visible, String dash) {
		JSObject trace = new JSObject();
		JSObject line = new JSObject();
		JSObject marker = new JSObject();
		trace.putWithQuote("name", name);
		trace.put("type", "'scatter'");
		trace.put("x", x);
		trace.put("y", y);
		trace.put("line", line);
		trace.put("marker", marker);
		trace.put("hovertemplate", "'<b>%{data.name}: </b>%{y:.2~f}<extra></extra>'");
		if (!visible) {
			trace.put("visible", "'legendonly'");
		}

		line.putWithQuote("dash", dash);

		marker.put("color", rgb(name));
		marker.put("size", 6);

		return trace;
	}

	@Override
	public void createLayout(JSObject layout) {
		JSObject xaxis = new JSObject();
		defaultAxisValues(xaxis, getXAxisTitle(), "");

		JSArray tickvals = new JSArray();
		JSArray ticktext = new JSArray();

		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		for (GradeItem gradeItem : gradeItems) {
			ticktext.addWithQuote(gradeItem.getItemname());
			tickvals.add(gradeItem.getId());
		}

		JSObject yaxis = new JSObject();
		defaultAxisValues(yaxis, getYAxisTitle(), "[-0.5,10.5]");

		createCategoryAxis(xaxis, tickvals, ticktext);
		layout.put("xaxis", xaxis);
		layout.put("yaxis", yaxis);
		layout.put("hovermode", "'x unified'");

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
	
	@Override
	public String getXAxisTitle() {
		boolean noGrade = getGeneralConfigValue("noGrade");
		return super.getXAxisTitle() + " (" + I18n.get("noGrade." + noGrade) + ")";
	}


}
