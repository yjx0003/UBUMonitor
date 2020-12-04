package es.ubu.lsi.ubumonitor.view.chart.gradeitems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.Stats;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.scene.control.TreeView;

public class Radar extends Plotly {

	private TreeView<GradeItem> treeViewGradeItem;

	public Radar(MainController mainController, TreeView<GradeItem> treeViewGradeItem) {
		super(mainController, ChartType.RADAR);
		this.treeViewGradeItem = treeViewGradeItem;
		useGeneralButton = true;
		useLegend = true;
		useGroupButton = true;
	}

	@Override
	public void createData(JSArray data) {

		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);

		createUserTraces(data, users, gradeItems);

		boolean generalActive = getGeneralButtonlActive();
		createMeanTrace(data, stats.getGeneralStats(), gradeItems, generalActive);

		boolean groupActive = getGroupButtonActive();
		createGroupTraces(data, getSelectedGroups(), gradeItems, stats, groupActive);
	}

	private void createUserTraces(JSArray data, List<EnrolledUser> users, List<GradeItem> gradeItems) {

		for (EnrolledUser user : users) {
			JSArray theta = new JSArray();
			JSArray r = new JSArray();

			for (GradeItem gradeItem : gradeItems) {
				r.add(gradeItem.getEnrolledUserPercentage(user) / 10);
				theta.add(gradeItem.getId());
			}
			if (!gradeItems.isEmpty()) {
				r.add(r.get(0));
				theta.add(theta.get(0));
			}
			JSObject trace = createTrace(user.getFullName(), theta, r, true, "solid");
			trace.put("userids", user.getId());
			data.add(trace);
		}

	}

	private void createMeanTrace(JSArray data, Map<GradeItem, DescriptiveStatistics> descriptiveStats,
			List<GradeItem> gradeItems, boolean visible) {

		JSArray theta = new JSArray();
		JSArray r = new JSArray();
		for (GradeItem gradeItem : gradeItems) {
			r.add(descriptiveStats.getOrDefault(gradeItem, EMPTY_DESCRIPTIVE_STATISTICS)
					.getMean() / 10);
			theta.add(gradeItem.getId());
		}
		if (!gradeItems.isEmpty()) {
			r.add(r.get(0));
			theta.add(theta.get(0));
		}
		data.add(createTrace(I18n.get("chartlabel.generalMean"), theta, r, visible, "dash"));
	}

	private void createGroupTraces(JSArray data, List<Group> groups, List<GradeItem> gradeItems, Stats stats,
			boolean visible) {
		for (Group group : groups) {
			JSArray theta = new JSArray();
			JSArray r = new JSArray();
			Map<GradeItem, DescriptiveStatistics> descriptiveStats = stats.getGroupStats(group);
			for (GradeItem gradeItem : gradeItems) {
				r.add(descriptiveStats.getOrDefault(gradeItem, EMPTY_DESCRIPTIVE_STATISTICS)
						.getMean() / 10);
				theta.add(gradeItem.getId());
			}
			if (!gradeItems.isEmpty()) {
				r.add(r.get(0));
				theta.add(theta.get(0));
			}
			data.add(createTrace(group.getGroupName(), theta, r, visible, "dot"));
		}
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
