package es.ubu.lsi.ubumonitor.view.chart.sigma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.sigma.model.Student;
import es.ubu.lsi.ubumonitor.util.DateTimeWrapper;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabulator;

public class SigmaTableNotEnrolled extends Tabulator {
	private static final Logger LOGGER = LoggerFactory.getLogger(SigmaTableNotEnrolled.class);

	public static final String FULLNAME = "fullname";
	public static final String EMAIL = "email";
	public static final String DATE_OF_BIRTH = "dateOfBirth";
	public static final String DNI = "DNI";
	public static final String GENDER = "gender";
	public static final String USUAL_ADDRESS = "usualAddress";
	public static final String USUAL_PHONE = "usualPhone";
	public static final String COURSE_ADDRESS = "courseAddress";
	public static final String COURSE_PHONE = "coursePhone";
	public static final String CENTER = "center";
	public static final String SUBJECT = "subject";
	public static final String SUBJECT_CREDITS = "subjectCredits";
	public static final String TYPE_OF_TEACHING = "typeOfTeaching";
	public static final String GROUP = "group";
	public static final String PERIOD = "period";
	public static final String COURSE_YEAR = "courseYear";
	public static final String TYPE_OF_SUBJECT = "typeOfSubject";
	public static final String CALLS_CONSUMED = "callsConsumed";
	public static final String NUMBER_OF_ENROLS = "numberOfEnrols";
	public static final String TYPE_ACCESS_GRADE = "typeAccessGrade";
	public static final String YEAR_ACCESS = "yearAccess";
	public static final String ACCESS_ROUTE = "accessRoute";
	public static final String INTERNATIONAL_PROGRAM = "internationalProgram";
	public static final String SPECIALITY = "speciality";
	public static final String STUDY_PLAN = "studyPlan";
	public static final String NUMBER_CREDITS_PASSED = "numberCreditsPassed";
	public static final String COURSE_NUMBER_CREDITS_ENROLLED = "courseNumberCreditsEnrolled";
	public static final String BASIC_AND_OBLIGATORY_CREDITS_ENROLLED = "basicAndObligatoryCreditsEnrolled";
	public static final String STUDY_PLAN_CREDITS = "studyPlanCredits";
	public static final String OPTIONAL_SUBJECTS = "optionalSubjects";
	public static final String ENROLLED_SUBJECTS = "enrolledSubjects";
	public static final String OBSERVATIONS = "observations";

	private DateTimeWrapper dateTimeWrapper;
	private List<Student> notEnrolledStudents;
	public SigmaTableNotEnrolled(MainController mainController, Course actualCourse, List<Student> students) {
		super(mainController, ChartType.SIGMA_TABLE_NOT_ENROLLED);
		
		dateTimeWrapper = new DateTimeWrapper();
		notEnrolledStudents = new ArrayList<>();
		this.actualCourse = actualCourse;
		for(Student student:students) {
			boolean noneMatch = actualCourse.getEnrolledUsers().stream().noneMatch(u-> u.getEmail().equals(student.getEmail()));
			if(noneMatch) {
				notEnrolledStudents.add(student);
			}
		}
	}

	@Override
	public void exportCSV(String path) throws IOException {
		
		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader( FULLNAME, EMAIL, DATE_OF_BIRTH, DNI, GENDER, USUAL_ADDRESS,
						USUAL_PHONE, COURSE_ADDRESS, COURSE_PHONE, CENTER, SUBJECT, SUBJECT_CREDITS, TYPE_OF_TEACHING,
						GROUP, PERIOD, COURSE_YEAR, TYPE_OF_SUBJECT, CALLS_CONSUMED, NUMBER_OF_ENROLS,
						TYPE_ACCESS_GRADE, YEAR_ACCESS, ACCESS_ROUTE, INTERNATIONAL_PROGRAM, SPECIALITY, STUDY_PLAN,
						NUMBER_CREDITS_PASSED, COURSE_NUMBER_CREDITS_ENROLLED, BASIC_AND_OBLIGATORY_CREDITS_ENROLLED,
						STUDY_PLAN_CREDITS, OPTIONAL_SUBJECTS, ENROLLED_SUBJECTS, OBSERVATIONS))) {
			for (Student student : notEnrolledStudents) {
			
				printer.print(student.getFullName());
				printer.print(student.getEmail());
				printer.print(student.getDateOfBirth());
				printer.print(student.getDni());
				printer.print(student.getGender());
				printer.print(student.getUsualAddress());
				printer.print(student.getUsualPhone());
				printer.print(student.getCourseAddress());
				printer.print(student.getCoursePhone());
				printer.print(student.getCenter());
				printer.print(student.getSubject());
				printer.print(student.getSubjectCredits());
				printer.print(student.getTypeOfTeaching());
				printer.print(student.getGroup());
				printer.print(student.getPeriod());
				printer.print(student.getCourseYear());
				printer.print(student.getTypeOfSubject());
				printer.print(student.getYearsConsumed());
				printer.print(student.getNumberOfEnrols());
				printer.print(student.getTypeAccessGrade());
				printer.print(student.getYearAccess());
				printer.print(student.getRouteAccess());
				printer.print(student.getInternationalProgram());
				printer.print(student.getSpeciality());
				printer.print(student.getStudyPlan());
				printer.print(student.getNumberCreditsPassed());
				printer.print(student.getCourseNumberCreditsEnrolled());
				printer.print(student.getBasicAndObligatoryCreditsEnrolled());
				printer.print(student.getStudyPlanCredits());
				printer.print(student.getOptionalSubjects());
				printer.print(student.getEnrolledSubjects());
				printer.print(student.getObservations());
				printer.println();
				
			}
		}

	}

	@Override
	public void fillOptions(JSObject jsObject) {
		jsObject.put("invalidOptionWarnings", false);
		jsObject.put("height", "height");
		jsObject.put("tooltipsHeader", true);
		jsObject.put("virtualDom", true);
		jsObject.putWithQuote("layout", "fitDataFill");
		jsObject.put("responsiveLayout", "'collapse'");
		jsObject.put("responsiveLayoutCollapseStartOpen", false);
		jsObject.putWithQuote("headerFilterPlaceholder", I18n.get("label.filter"));
	}

	@Override
	public void update() {

		JSArray columns = createColumns();
		JSArray tableData = createData(notEnrolledStudents);
		JSObject data = new JSObject();
		data.put("columns", columns);
		data.put("tabledata", tableData);
		LOGGER.debug("Columnas:{}", columns);
		LOGGER.debug("Datos de tabla:{}", data);
		webViewChartsEngine.executeScript(String.format("updateTabulator(%s, %s)", data, getOptions()));

	}

	private JSArray createData(List<Student> students) {
		JSArray jsArray = new JSArray();
		for (Student student : students) {
			
			JSObject jsObject = new JSObject();
			jsArray.add(jsObject);
			jsObject.putWithQuote(FULLNAME, student.getFullName());
			jsObject.putWithQuote(EMAIL, student.getEmail());
			jsObject.putWithQuote(DATE_OF_BIRTH, student.getDateOfBirth()
					.format(dateTimeWrapper.getDateFormatter()));
			jsObject.putWithQuote(DNI, student.getDni());
			jsObject.putWithQuote(GENDER, student.getGender());
			jsObject.putWithQuote(USUAL_ADDRESS, student.getUsualAddress());
			jsObject.putWithQuote(USUAL_PHONE, student.getUsualPhone());
			jsObject.putWithQuote(COURSE_ADDRESS, student.getCourseAddress());
			jsObject.putWithQuote(COURSE_PHONE, student.getCoursePhone());
			jsObject.putWithQuote(CALLS_CONSUMED, student.getYearsConsumed());
			jsObject.putWithQuote(NUMBER_OF_ENROLS, student.getNumberOfEnrols());
			jsObject.putWithQuote(YEAR_ACCESS, student.getYearAccess());
			jsObject.putWithQuote(ACCESS_ROUTE, student.getRouteAccess());
			jsObject.putWithQuote(INTERNATIONAL_PROGRAM, student.getInternationalProgram());
			jsObject.putWithQuote(SPECIALITY, student.getSpeciality());
			jsObject.putWithQuote(BASIC_AND_OBLIGATORY_CREDITS_ENROLLED,
					student.getBasicAndObligatoryCreditsEnrolled());
			jsObject.putWithQuote(NUMBER_CREDITS_PASSED, student.getNumberCreditsPassed());
			jsObject.putWithQuote(COURSE_NUMBER_CREDITS_ENROLLED, student.getCourseNumberCreditsEnrolled());
			jsObject.putWithQuote(OPTIONAL_SUBJECTS, student.getOptionalSubjects());
			jsObject.putWithQuote(ENROLLED_SUBJECTS, student.getEnrolledSubjects());
			jsObject.putWithQuote(OBSERVATIONS, student.getObservations());
		}
		return jsArray;
	}

	private JSArray createColumns() {
		JSArray columns = new JSArray();
		columns.add(createResponsiveLayoutColumn());
		columns.add(createColumn(FULLNAME));
		columns.add(createColumn(EMAIL));
		columns.add(createColumn(DNI));
		columns.add(createColumn(COURSE_ADDRESS));
		columns.add(createColumn(COURSE_PHONE));
		columns.add(createColumn(YEAR_ACCESS));
		columns.add(createColumn(ACCESS_ROUTE));
		columns.add(createColumn(CALLS_CONSUMED));
		columns.add(createColumn(NUMBER_OF_ENROLS));
		columns.add(createColumn(NUMBER_CREDITS_PASSED));
		columns.add(createColumn(COURSE_NUMBER_CREDITS_ENROLLED));
		columns.add(createColumn(BASIC_AND_OBLIGATORY_CREDITS_ENROLLED));
		JSObject dateOfBirth = createColumn(DATE_OF_BIRTH, "'date'");
		dateOfBirth.put("sorterParams", "{format: '" + dateTimeWrapper.getDatePattern() + "'}");
		columns.add(dateOfBirth);
		columns.add(createColumn(GENDER));
		columns.add(createColumn(USUAL_ADDRESS));
		columns.add(createColumn(USUAL_PHONE));
		columns.add(createColumn(INTERNATIONAL_PROGRAM));
		columns.add(createColumn(SPECIALITY));
		columns.add(createColumn(OPTIONAL_SUBJECTS));
		columns.add(createColumn(ENROLLED_SUBJECTS));
		columns.add(createColumn(OBSERVATIONS));

		return columns;

	}

	private JSObject createResponsiveLayoutColumn() {
		JSObject responsiveLayout = new JSObject();
		responsiveLayout.put("formatter", "'responsiveCollapse'");
		responsiveLayout.put("width", 30);
		responsiveLayout.put("minWidth", 30);
		responsiveLayout.put("hozAlign", "'center'");
		responsiveLayout.put("resizable", false);
		responsiveLayout.put("headerSort", false);
		
		return responsiveLayout;

	}

	private JSObject createColumn(String title, String sorter) {
		JSObject column = new JSObject();
		column.put("hozAlign", "'center'");
		column.putWithQuote("title", I18n.get("sigma." + title));
		column.putWithQuote("field", title);
		column.put("sorter", sorter);
		column.put("headerFilter", true);
		return column;
	}

	private JSObject createColumn(String title) {
		return createColumn(title, "'string'");
	}

}
