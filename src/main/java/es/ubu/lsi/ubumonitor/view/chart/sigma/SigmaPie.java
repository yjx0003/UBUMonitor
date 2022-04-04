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

public class SigmaPie extends Plotly {

	private EnrolledUserStudentMapping enrolledUserStudentMapping;

	public SigmaPie(MainController mainController, EnrolledUserStudentMapping enrolledUserStudentMapping) {
		super(mainController, ChartType.SIGMA_PIE);
		this.enrolledUserStudentMapping = enrolledUserStudentMapping;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> selectedusers = getSelectedEnrolledUser();
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("userid", "fullname",
				"gender", "accessRoute"))) {
			for(EnrolledUser user: selectedusers) {
				Student student = this.enrolledUserStudentMapping.getStudent(user);
				if(student != null) {
					printer.printRecord(user.getId(), user.getFullName(), student.getGender(), student.getRouteAccess());
				}
			}
		}
		

	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> selectedusers = getSelectedEnrolledUser();
		List<Student> students = this.enrolledUserStudentMapping.getStudents(selectedusers);

		Map<String, List<Student>> genders = students.stream()
				.collect(Collectors.groupingBy(Student::getGender, TreeMap::new, Collectors.toList()));
		Map<String, List<Student>> routeAccess = students.stream()
				.collect(Collectors.groupingBy(Student::getRouteAccess, TreeMap::new, Collectors.toList()));

		data.add(createData(genders, I18n.get("sigma.gender"), 0, 0));
		data.add(createData(routeAccess, I18n.get("sigma.routeAccess"),0, 1));
	}

	private JSObject createData(Map<String, List<Student>> counter, String name, int row, int column) {
		JSObject jsObject = new JSObject();

		jsObject.putWithQuote("name", name);
		jsObject.put("domain", "{row:" + row + ",column:" + column + "}");
		jsObject.put("type", "'pie'");
		jsObject.put("textinfo", "'label+percent+name'");
		jsObject.put("hovertemplate", "'<b>%{data.name}: </b>%{label}<br>%{percent} (%{value})<br>%{customdata}<extra></extra>'");
		JSArray values = new JSArray();
		JSArray labels = new JSArray();
		jsObject.put("values", values);
		jsObject.put("labels", labels);
		JSArray customdata = new JSArray();
		jsObject.put("customdata", customdata);
		
		for (Map.Entry<String, List<Student>> entry : counter.entrySet()) {
			labels.addWithQuote(entry.getKey());
			values.add(entry.getValue().size());
			StringBuilder studentsNames = new StringBuilder();
			for(Student student:entry.getValue()) {
				EnrolledUser user = this.enrolledUserStudentMapping.getEnrolledUser(student);
				studentsNames.append("<br>• ");
				studentsNames.append(user.getFullName());
				
				
				
				
			}
			customdata.addWithQuote(studentsNames);
		}
		return jsObject;
	}

	@Override
	public void createLayout(JSObject layout) {
		layout.put("grid", "{rows:1,columns:2}");
	}

}
