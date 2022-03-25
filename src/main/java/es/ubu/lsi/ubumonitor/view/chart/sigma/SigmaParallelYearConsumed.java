package es.ubu.lsi.ubumonitor.view.chart.sigma;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
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
import javafx.scene.paint.Color;

public class SigmaParallelYearConsumed extends Plotly {

	private EnrolledUserStudentMapping enrolledUserStudentMapping;
	private TreeView<GradeItem> treeViewGradeItem;
	private List<String> gradeTypes;

	public SigmaParallelYearConsumed(MainController mainController,
			EnrolledUserStudentMapping enrolledUserStudentMapping) {
		super(mainController, ChartType.SIGMA_PARALLEL_YEAR_CONSUMED);
		this.treeViewGradeItem = mainController.getSelectionController()
				.getTvwGradeReport();
		this.enrolledUserStudentMapping = enrolledUserStudentMapping;
		gradeTypes = Arrays.asList(I18n.get("text.empty"), I18n.get("text.fail"), I18n.get("text.pass"));
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		List<Student> selectedStudents = enrolledUserStudentMapping.getStudents(selectedUsers);
		boolean noGradeAsZero = getGeneralConfigValue("noGrade");
		double cutGrade = getGeneralConfigValue("cutGrade");
		Map<EnrolledUser, DescriptiveStatistics> usersGrades = ParallelCategory.getUsersGrades(selectedUsers,
				gradeItems, noGradeAsZero);
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("userid", "username",
				  "yearAccess", "consumed","gradeMean", "gradeType"))) {
			for (Student student: selectedStudents) {
				EnrolledUser user = enrolledUserStudentMapping.getEnrolledUser(student);
				printer.print(user.getId());
				printer.print(user.getFullName());
				printer.print(student.getYearAccess());
				printer.print(student.getYearsConsumed());
				printer.print(usersGrades.get(user).getMean());
				int typeGrade = ParallelCategory.getCategoryGrade(usersGrades.get(user).getMean(), cutGrade);
				printer.print(gradeTypes.get(typeGrade));
				printer.println();
			}
		}
		
	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		List<Student> selectedStudents = enrolledUserStudentMapping.getStudents(selectedUsers);
		selectedStudents.sort(Comparator.comparing(Student::getYearAccess).thenComparing(Student::getYearsConsumed));
		selectedUsers = enrolledUserStudentMapping.getEnrolledUsers(selectedStudents);
		boolean noGradeAsZero = getGeneralConfigValue("noGrade");
		double cutGrade = getGeneralConfigValue("cutGrade");
		Map<EnrolledUser, DescriptiveStatistics> usersGrades = ParallelCategory.getUsersGrades(selectedUsers,
				gradeItems, noGradeAsZero);

		data.add(createTrace(usersGrades, cutGrade));

	}

	private JSObject createTrace(Map<EnrolledUser, DescriptiveStatistics> usersGrades, double cutGrade) {
		JSObject trace = new JSObject();
		trace.put("type", "'parcats'");
		trace.put("hoveron", "'color'");
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
		colorscale.add("[0," + colorToRGB(Color.web("#d3d3d3", 0.3)) + "]");
		colorscale.add("[0.5," + colorToRGB(Color.web("#dc143c", 0.3)) + "]");
		colorscale.add("[1," + colorToRGB(Color.web("#2dc214", 0.3)) + "]");
		
		JSObject yearAccessDimension = new JSObject();
		JSArray yearAccessValues = new JSArray();
		ParallelCategory.createDimension(I18n.get("sigma.yearAccess"), dimensions, yearAccessDimension, yearAccessValues);
		
		

		JSObject gradeDimension = new JSObject();
		JSArray gradeValues = new JSArray();
		ParallelCategory.createDimension(I18n.get("cutGrade") + ": " + cutGrade, dimensions, gradeDimension,
				gradeValues);
		
		JSObject consumedDimension = new JSObject();
		JSArray consumedValues = new JSArray();
		ParallelCategory.createDimension(I18n.get("sigma.yearsConsumed"), dimensions, consumedDimension, consumedValues);

		
		

		

		for (Map.Entry<EnrolledUser, DescriptiveStatistics> entry : usersGrades.entrySet()) {
			EnrolledUser user = entry.getKey();
			Student student = enrolledUserStudentMapping.getStudent(user);
			DescriptiveStatistics descriptiveStatistics = entry.getValue();
			int typeGrade = ParallelCategory.getCategoryGrade(descriptiveStatistics.getMean(), cutGrade);
			color.add(typeGrade);
			consumedValues.addWithQuote(student.getYearsConsumed());
			gradeValues.addWithQuote(gradeTypes.get(typeGrade));
			yearAccessValues.addWithQuote(student.getYearAccess());
		}
		return trace;
	}

	@Override
	public void createLayout(JSObject layout) {
		// do nothing

	}

}
