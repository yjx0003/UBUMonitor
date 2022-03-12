package es.ubu.lsi.ubumonitor.view.chart.sigma;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.sigma.controller.EnrolledUserStudentMapping;
import es.ubu.lsi.ubumonitor.sigma.model.Student;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class SigmaBar extends Plotly {

	private EnrolledUserStudentMapping enrolledUserStudentMapping;

	public SigmaBar(MainController mainController, EnrolledUserStudentMapping enrolledUserStudentMapping) {
		super(mainController, ChartType.SIGMA_BAR);
		this.enrolledUserStudentMapping = enrolledUserStudentMapping;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> selectedusers = getSelectedEnrolledUser();
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("userid", "fullname",
				"yearsConsumed", "enrollments", "yearAccess", "yearsOld"))) {
			for (EnrolledUser user : selectedusers) {
				Student student = this.enrolledUserStudentMapping.getStudent(user);
				if (student != null) {
					printer.printRecord(user.getId(), user.getFullName(), student.getYearsConsumed(),
							student.getNumberOfEnrols(), student.getYearAccess(), student.getYearsOld());
				}
			}
		}

	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> selectedusers = getSelectedEnrolledUser();
		List<Student> students = this.enrolledUserStudentMapping.getStudents(selectedusers);

		Map<Integer, List<Student>> yearsConsumed = students.stream()
				.collect(Collectors.groupingBy(Student::getYearsConsumed, TreeMap::new, Collectors.toList()));
		Map<Integer, List<Student>> numberOfEnrols = students.stream()
				.collect(Collectors.groupingBy(Student::getNumberOfEnrols, TreeMap::new, Collectors.toList()));
		Map<Integer, List<Student>> yearsAccess = students.stream()
				.collect(Collectors.groupingBy(Student::getYearAccess, TreeMap::new, Collectors.toList()));
		Map<Integer, List<Student>> yearsOld = students.stream()
				.collect(Collectors.groupingBy(Student::getYearsOld, TreeMap::new, Collectors.toList()));

		data.add(createData(yearsConsumed, students.size(), I18n.get("sigma.yearsConsumed"), "x1", "x1"));
		data.add(createData(numberOfEnrols, students.size(), I18n.get("sigma.numberOfEnrols"), "x2", "y2"));
		data.add(createData(yearsAccess, students.size(), I18n.get("sigma.yearsAccess"), "x3", "y3"));
		data.add(createData(yearsOld, students.size(), I18n.get("sigma.yearsOld"), "x4", "y4"));
	}

	private <T> JSObject createData(Map<T, List<Student>> counter, int totalStudents, String name, String xaxis,
			String yaxis) {
		JSObject jsObject = new JSObject();

		jsObject.putWithQuote("name", name);
		jsObject.putWithQuote("xaxis", xaxis);
		jsObject.putWithQuote("yaxis", yaxis);
		jsObject.put("type", "'bar'");
		jsObject.put("hovertemplate", "'<b>%{data.name}: </b>%{label}<br>%{text}<br>%{customdata}<extra></extra>'");
		JSArray y = new JSArray();
		JSArray x = new JSArray();
		jsObject.put("y", y);
		jsObject.put("x", x);
		JSArray customdata = new JSArray();
		jsObject.put("customdata", customdata);
		JSArray text = new JSArray();
		jsObject.put("text", text);
		JSArray userids = new JSArray();
		jsObject.put("userids", userids);

		for (Map.Entry<T, List<Student>> entry : counter.entrySet()) {
			x.addWithQuote(entry.getKey());
			y.addWithQuote(entry.getValue()
					.size());
			StringBuilder studentsNames = new StringBuilder();
			text.add("'<b>'+toPercentage(" + entry.getValue()
					.size() + "," + totalStudents + ")+'</b>'");
			JSArray ids = new JSArray();
			userids.add(ids);
			for (Student student : entry.getValue()) {
				EnrolledUser user = this.enrolledUserStudentMapping.getEnrolledUser(student);
				ids.add(user.getId());
				studentsNames.append("<br>• ");
				studentsNames.append(user.getFullName());

			}
			customdata.addWithQuote(studentsNames);
		}
		return jsObject;
	}

	@Override
	public void createLayout(JSObject layout) {
		layout.put("grid", "{rows:2,columns:2,pattern: 'independent'}");
		layout.put("xaxis", "{type:'category', title:'" + I18n.get("sigma.yearsConsumed") + "'}");
		layout.put("xaxis2", "{type:'category', title:'" + I18n.get("sigma.numberOfEnrols") + "'}");
		layout.put("xaxis3", "{type:'category', title:'" + I18n.get("sigma.yearsAccess") + "'}");
		layout.put("xaxis4", "{type:'category', title:'" + I18n.get("sigma.yearsOld") + "'}");
	}

}
