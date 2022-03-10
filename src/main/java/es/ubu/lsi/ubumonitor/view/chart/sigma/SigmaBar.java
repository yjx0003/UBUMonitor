package es.ubu.lsi.ubumonitor.view.chart.sigma;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.sigma.controller.EnrolledUserStudentMapping;
import es.ubu.lsi.ubumonitor.sigma.model.Student;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class SigmaBar extends Plotly{
	
	private EnrolledUserStudentMapping enrolledUserStudentMapping;
	public SigmaBar(MainController mainController, EnrolledUserStudentMapping enrolledUserStudentMapping) {
		super(mainController, ChartType.SIGMA_BAR);
		this.enrolledUserStudentMapping = enrolledUserStudentMapping;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> selectedusers = getSelectedEnrolledUser();
		List<Student> students = this.enrolledUserStudentMapping.getStudents(selectedusers);
		
		Map<Integer, Long> yearsConsumed = students.stream().collect(Collectors.groupingBy(Student::getYearsConsumed, TreeMap::new, Collectors.counting()));
		Map<Integer, Long> numberOfEnrols = students.stream().collect(Collectors.groupingBy(Student::getNumberOfEnrols, TreeMap::new, Collectors.counting()));
		Map<Integer, Long> yearsAccess = students.stream().collect(Collectors.groupingBy(Student::getYearAccess, TreeMap::new, Collectors.counting()));
		Map<Integer, Long> yearsOld = students.stream().collect(Collectors.groupingBy(Student::getYearsOld, TreeMap::new, Collectors.counting()));

		
		data.add(createData(yearsConsumed, I18n.get("sigma.yearsConsumed"),"x1", "x1"));
		data.add(createData(numberOfEnrols, I18n.get("sigma.numberOfEnrols"),"x2", "y2"));
		data.add(createData(yearsAccess, I18n.get("sigma.yearsAccess"),"x3", "y3"));
		data.add(createData(yearsOld, I18n.get("sigma.yearsOld"),"x4", "y4"));
	}

	private <T> JSObject  createData(Map<T, Long> counter, String name, String xaxis, String yaxis) {
		JSObject jsObject = new JSObject();
		
		jsObject.putWithQuote("name", name);
		jsObject.putWithQuote("xaxis", xaxis);
		jsObject.putWithQuote("yaxis", yaxis);
		jsObject.put("type", "'bar'");
		jsObject.put("textinfo", "'label+percent+name'");
		JSArray y = new JSArray();
		JSArray x = new JSArray();
		jsObject.put("y", y);
		jsObject.put("x", x);
		for (Map.Entry<T, Long> entry : counter.entrySet()) {
			x.addWithQuote(entry.getKey());
			y.addWithQuote(entry.getValue());
		}
		return jsObject;
	}

	@Override
	public void createLayout(JSObject layout) {
		layout.put("grid", "{rows:2,columns:2,pattern: 'independent'}");
		layout.put("xaxis", "{type:'category', title:'"+I18n.get("sigma.yearsConsumed")+"'}");
		layout.put("xaxis2", "{type:'category', title:'"+I18n.get("sigma.numberOfEnrols")+"'}");
		layout.put("xaxis3", "{type:'category', title:'"+I18n.get("sigma.yearsAccess")+"'}");
		layout.put("xaxis4", "{type:'category', title:'"+I18n.get("sigma.yearsOld")+"'}");
	}
	
}
