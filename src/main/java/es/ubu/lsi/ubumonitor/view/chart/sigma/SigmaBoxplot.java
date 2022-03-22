package es.ubu.lsi.ubumonitor.view.chart.sigma;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

public class SigmaBoxplot extends Plotly {

	private TreeView<GradeItem> treeViewGradeItem;
	private EnrolledUserStudentMapping enrolledUserStudentMapping;

	public SigmaBoxplot(MainController mainController, EnrolledUserStudentMapping enrolledUserStudentMapping) {
		super(mainController, ChartType.SIGMA_BOXPLOT);
		this.treeViewGradeItem = mainController.getSelectionController()
				.getTvwGradeReport();
		this.enrolledUserStudentMapping = enrolledUserStudentMapping;
	}

	@Override
	public void exportCSV(String path) throws IOException {

	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		boolean noGrade = getGeneralConfigValue("noGrade");
		List<Student> students = enrolledUserStudentMapping.getStudents(users);
		List<String> routeAccess = getUniqueRouteAccess(students);
		Map<EnrolledUser, DescriptiveStatistics> userGrades = ParallelCategory.getUsersGrades(users, gradeItems, noGrade);
		data.add(createTrace(I18n.get("selectedUser"), students, routeAccess, userGrades));
	}

	public JSObject createTrace(String name, List<Student> students, List<String> routeAccess, Map<EnrolledUser, DescriptiveStatistics> userGrades) {
		JSObject trace = new JSObject();
		
		JSArray userNames = new JSArray();
		JSArray userids = new JSArray();
		JSArray x = new JSArray();
		JSArray y = new JSArray();

		for(Student student: students) {
			EnrolledUser user = enrolledUserStudentMapping.getEnrolledUser(student);
			x.add(routeAccess.indexOf(student.getRouteAccess()));
			y.add(userGrades.get(user).getMean());
			userNames.addWithQuote(user.getFullName());
			userids.addWithQuote(user.getId());
		}
		
		trace.put("x", x);
		trace.put("y", y);
		trace.put("type", "'box'");
		trace.put("boxpoints", "'all'");
		trace.put("pointpos", 0);
		trace.put("jitter", 1);
		trace.putWithQuote("name", name);
		trace.put("userids", userids);
		trace.put("text", userNames);
		trace.put("hovertemplate", "'<b>%{x}<br>%{text}: </b>%{y:.2~f}<extra></extra>'");
		return trace;

	}

	@Override
	public void createLayout(JSObject layout) {
		List<EnrolledUser> users = getSelectedEnrolledUser();
		boolean horizontalMode = false;
		List<Student> students = enrolledUserStudentMapping.getStudents(users);
		List<String> routeAccess = getUniqueRouteAccess(students);
		JSArray ticktext = new JSArray();
		routeAccess.forEach(ticktext::addWithQuote);
		horizontalMode(layout, ticktext, horizontalMode, getXAxisTitle(), getYAxisTitle(), "[-0.5,10.5]");
		layout.put("boxmode", "'group'");
		layout.put("hovermode", "'closest'");

	}

	private List<String> getUniqueRouteAccess(List<Student> students) {
		return students.stream()
				.map(Student::getRouteAccess)
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}

}
