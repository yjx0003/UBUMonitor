package es.ubu.lsi.ubumonitor.view.chart.sigma;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.sigma.controller.EnrolledUserStudentMapping;
import es.ubu.lsi.ubumonitor.sigma.model.Student;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.ParallelCategory;
import javafx.scene.control.TreeView;

public class SigmaViolin extends Plotly {

	private TreeView<GradeItem> treeViewGradeItem;
	private EnrolledUserStudentMapping enrolledUserStudentMapping;

	public SigmaViolin(MainController mainController, EnrolledUserStudentMapping enrolledUserStudentMapping) {
		super(mainController, ChartType.SIGMA_VIOLIN);
		this.treeViewGradeItem = mainController.getSelectionController()
				.getTvwGradeReport();
		this.enrolledUserStudentMapping = enrolledUserStudentMapping;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		List<Student> selectedStudents = enrolledUserStudentMapping.getStudents(selectedUsers);
		boolean noGradeAsZero = getGeneralConfigValue("noGrade");
		Map<EnrolledUser, DescriptiveStatistics> usersGrades = ParallelCategory.getUsersGrades(selectedUsers,
				gradeItems, noGradeAsZero);
		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader("userid", "username", "routeAccess", "gradeMean"))) {
			for (Student student : selectedStudents) {
				EnrolledUser user = enrolledUserStudentMapping.getEnrolledUser(student);
				printer.print(user.getId());
				printer.print(user.getFullName());
				printer.print(student.getRouteAccess());
				printer.print(usersGrades.get(user)
						.getMean());
				printer.println();
			}
		}
	}

	@Override
	public void createData(JSArray data) {
		int limit = getConfigValue("limit");
		boolean useHorizontal = getConfigValue("horizontalMode");
		boolean boxVisible = getConfigValue("boxVisible");
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		boolean noGrade = getGeneralConfigValue("noGrade");
		List<Student> students = enrolledUserStudentMapping.getStudents(users);
		List<String> routeAccess = SigmaBoxplot.getUniqueRouteAccess(students, limit);
		Map<EnrolledUser, DescriptiveStatistics> userGrades = ParallelCategory.getUsersGrades(users, gradeItems,
				noGrade);
		data.add(createTrace(I18n.get("text.selectedUsers"), students, routeAccess, userGrades, useHorizontal,
				boxVisible));
	}

	public JSObject createTrace(String name, List<Student> students, List<String> routeAccess,
			Map<EnrolledUser, DescriptiveStatistics> userGrades, boolean horizontalMode, boolean boxVisible) {
		JSObject trace = new JSObject();

		JSArray userNames = new JSArray();
		JSArray userids = new JSArray();
		JSArray x = new JSArray();
		JSArray y = new JSArray();

		for (Student student : students) {
			EnrolledUser user = enrolledUserStudentMapping.getEnrolledUser(student);
			int index = routeAccess.indexOf(student.getRouteAccess());
			if (index < 0) {
				continue;
			}
			x.add(index);
			y.add(userGrades.get(user)
					.getMean());
			userNames.addWithQuote(user.getFullName());
			userids.addWithQuote(user.getId());
		}
		createAxisValuesHorizontal(horizontalMode, trace, x, y);

		trace.put("jitter", 1);
		trace.put("type", "'violin'");
		trace.putWithQuote("name", name);
		trace.put("userids", userids);
		trace.put("text", userNames);
		trace.put("hovertemplate", "'<b>%{" + (horizontalMode ? "y" : "x") + "}<br>%{text}: </b>%{"
				+ (horizontalMode ? "x" : "y") + ":.2~f}<extra></extra>'");
		trace.put("box", "{visible:" + boxVisible + "}");
		trace.put("meanline", "{visible:true}");
		return trace;

	}

	@Override
	public void createLayout(JSObject layout) {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		boolean horizontalMode = false;
		int limit = getConfigValue("limit");
		List<Student> students = enrolledUserStudentMapping.getStudents(users);
		List<String> routeAccess = SigmaBoxplot.getUniqueRouteAccess(students, limit);
		JSArray ticktext = new JSArray();
		routeAccess.forEach(ticktext::addWithQuote);
		horizontalMode(layout, ticktext, horizontalMode, getXAxisTitle(), getYAxisTitle(), "[-0.5,10.5]");
		layout.put("violinmode", "'group'");
		layout.put("hovermode", "'closest'");

	}

	@Override
	public String getXAxisTitle() {
		boolean noGrade = getGeneralConfigValue("noGrade");
		return super.getXAxisTitle() + " (" + I18n.get("noGrade." + noGrade) + ")";
	}

}
