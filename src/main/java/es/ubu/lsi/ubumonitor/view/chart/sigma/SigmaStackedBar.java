package es.ubu.lsi.ubumonitor.view.chart.sigma;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
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
import es.ubu.lsi.ubumonitor.util.ManageDuplicate;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public class SigmaStackedBar extends Plotly {

	private EnrolledUserStudentMapping enrolledUserStudentMapping;
	private static final Comparator<Student> STUDENT_COMPARATOR = Comparator
			.comparing(Student::getNumberCreditsPassed, Comparator.reverseOrder())
			.thenComparing(Student::getBasicAndObligatoryCreditsEnrolled, Comparator.reverseOrder())
			.thenComparing(Student::getOptionalCreditsEnrolled, Comparator.reverseOrder());

	public SigmaStackedBar(MainController mainController, EnrolledUserStudentMapping enrolledUserStudentMapping) {
		super(mainController, ChartType.SIGMA_STACKED_BAR);
		this.useLegend = true;
		this.enrolledUserStudentMapping = enrolledUserStudentMapping;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> selectedusers = getSelectedEnrolledUser();
		List<Student> students = this.enrolledUserStudentMapping.getStudents(selectedusers);
		students.sort(STUDENT_COMPARATOR);
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader("userid", "fullname",
				"creditsPassed", "basicAndObligatoryCredits", "optionalCredits"))) {
			for (Student student : students) {
				EnrolledUser user = this.enrolledUserStudentMapping.getEnrolledUser(student);

				printer.printRecord(user.getId(), user.getFullName(), student.getNumberCreditsPassed(),
						student.getBasicAndObligatoryCreditsEnrolled(), student.getOptionalCreditsEnrolled());

			}
		}

	}

	@Override
	public void createData(JSArray data) {
		List<EnrolledUser> selectedusers = getSelectedEnrolledUser();
		List<Student> students = this.enrolledUserStudentMapping.getStudents(selectedusers);

		students.sort(STUDENT_COMPARATOR);

		List<Integer> creditsPassed = students.stream()
				.map(Student::getNumberCreditsPassed)
				.collect(Collectors.toList());
		List<Integer> basicAndObligatory = students.stream()
				.map(Student::getBasicAndObligatoryCreditsEnrolled)
				.collect(Collectors.toList());
		List<Integer> optionalCredits = students.stream()
				.map(Student::getOptionalCreditsEnrolled)
				.collect(Collectors.toList());

		data.add(createData(students, creditsPassed, I18n.get("sigma.creditsPassed")));
		data.add(createData(students, basicAndObligatory, I18n.get("sigma.basicAndObligatory")));
		data.add(createData(students, optionalCredits, I18n.get("sigma.optionalCredits")));
	}

	private JSObject createData(List<Student> students, List<Integer> y, String name) {
		JSObject jsObject = new JSObject();

		jsObject.putWithQuote("name", name);
		jsObject.put("type", "'bar'");

		jsObject.put("hovertemplate", "'<b>%{x}</b><br>%{data.name}: %{y}<extra></extra>'");
		JSArray x = new JSArray();
		jsObject.put("x", x);
		jsObject.put("y", y);

		JSArray customdata = new JSArray();
		jsObject.put("customdata", customdata);
		JSArray text = new JSArray();
		text.addAll(y);
		jsObject.put("text", text);
		JSArray userids = new JSArray();
		jsObject.put("userids", userids);
		ManageDuplicate manageDuplicate = new ManageDuplicate();
		for (Student student : students) {
			EnrolledUser user = this.enrolledUserStudentMapping.getEnrolledUser(student);
			x.addWithQuote(manageDuplicate.getValue(user.getFullName()));
			userids.add(user.getId());
		}

		return jsObject;
	}

	@Override
	public void createLayout(JSObject layout) {
		layout.put("barmode", "'stack'");

	}

}
