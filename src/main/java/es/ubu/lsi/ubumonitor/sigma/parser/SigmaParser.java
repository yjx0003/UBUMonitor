package es.ubu.lsi.ubumonitor.sigma.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.sigma.controller.SubjectFactory;
import es.ubu.lsi.ubumonitor.sigma.parser.model.Student;
import es.ubu.lsi.ubumonitor.sigma.parser.model.Subject;

public class SigmaParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(SigmaParser.class);
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");
	private File file;
	private Student actualStudent;
	private List<Student> students;
	private String[] splitedLine;
	BufferedReader bufferedReader;

	Map<String, Runnable> map = new HashMap<>();

	{
		map.put("Ficha Completa", this::fullRecord);
		map.put("Apellidos, nombre", this::fullName);
		map.put("Dirección electrónica", this::email);
		map.put("Fecha nacimiento", this::dateOfBirth);
		map.put("DNI / Pasaporte", this::dniGender);
		map.put("Dirección habitual", this::usualAddress);
		map.put("Teléfono habitual", this::usualPhone);
		map.put("Dirección durante el curso", this::courseAddress);
		map.put("Teléfono durante el curso", this::coursePhone);
		map.put("Centro", this::center);
		map.put("Asignatura", this::subject);
		map.put("Créditos de la asignatura", this::subjectCreditTypeOfTeaching);
		map.put("Grupo", this::groupPeriod);
		map.put("Año académico", this::courseYear);
		map.put("Tipo de asignatura", this::typeOfSubjectCallsConsumed);
		map.put("Número de veces matriculado", this::numberOfEnrols);
		map.put("Tipo de acceso a titulación", this::typeOfAccessYear);
		map.put("Vía de acceso", this::routeAccessInternationalProgram);
		map.put("Especialidad", this::speciality);
		map.put("Plan de estudios", this::studyPlan);
		map.put("Número de créditos superados hasta el curso anterior", this::numberOfCreditsPassed);
		map.put("Número créditos matriculados en este curso", this::courseNumberCreditsEnrolled);
		map.put("Número de créditos de formación básica y obligatorios matriculados en este curso", this::basicAndObligatoryCredits);
		map.put("Número de créditos del plan de estudios", this::creditsStudyPlan);
		map.put("Asignaturas optativas superadas", this::optionalSubjects);
		map.put("Asignaturas matriculadas en este curso", this::enrolledSubjects);
		map.put("Observaciones del alumno/a", this::observations);

	}

	public SigmaParser(File file) {
		this.file = file;
		this.students = new ArrayList<>();
	}

	public List<Student> parse() throws IOException {
		bufferedReader = Files.newBufferedReader(file.toPath(), StandardCharsets.ISO_8859_1);
		while (nextLine()) {
			Runnable runnable = map.get(splitedLine[0]);
			if (runnable != null) {
				try {

					runnable.run();
				} catch (Exception e) {
					LOGGER.warn("Error al parsear la linea {}", Arrays.asList(splitedLine), e);
				}
			}
		}

		bufferedReader.close();
		return students;
	}

	private void fullRecord() {

		actualStudent = new Student();
		students.add(actualStudent);

	}

	private boolean nextLine() {

		try {
			String line = bufferedReader.readLine();
			if (line == null) {
				return false;
			}

			splitedLine = line.trim()
					.split("\t");
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private void fullName() {
		this.actualStudent.setFullName(splitedLine[1]);
	}

	private void email() {
		this.actualStudent.setEmail(splitedLine[1]);
	}

	private void dateOfBirth() {
		LocalDate birthOfDate = LocalDate.parse(splitedLine[1], DATE_FORMATTER);
		if (birthOfDate.isAfter(LocalDate.now())) {
			birthOfDate = birthOfDate.minusYears(100);
		}
		this.actualStudent.setDateOfBirth(birthOfDate);
	}

	private void dniGender() {
		this.actualStudent.setDni(splitedLine[1]);
		this.actualStudent.setGender(splitedLine[3]);
	}

	private void usualAddress() {
		this.actualStudent.setUsualAddress(splitedLine[1]);
	}

	private void usualPhone() {
		this.actualStudent.setUsualPhone(splitedLine[1]);
	}

	private void courseAddress() {
		this.actualStudent.setCourseAddress(splitedLine[1]);
	}

	private void coursePhone() {
		this.actualStudent.setCoursePhone(splitedLine[1]);
	}

	private void center() {
		this.actualStudent.setCenter(splitedLine[1]);
	}

	private void subject() {
		this.actualStudent.setSubject(splitedLine[1]);
	}

	private void subjectCreditTypeOfTeaching() {
		this.actualStudent.setSubjectCredits(Double.parseDouble(splitedLine[1]));
		this.actualStudent.setTypeOfTeaching(splitedLine[3]);
	}

	private void groupPeriod() {
		this.actualStudent.setGroup(splitedLine[1]);
		this.actualStudent.setPeriod(splitedLine[3]);
	}

	private void courseYear() {
		this.actualStudent.setCourseYear(splitedLine[1]);
	}

	private void typeOfSubjectCallsConsumed() {
		this.actualStudent.setTypeOfSubject(splitedLine[1]);
		this.actualStudent.setYearsConsumed(Integer.parseInt(splitedLine[3]));
	}

	private void numberOfEnrols() {
		this.actualStudent.setNumberOfEnrols(Integer.parseInt(splitedLine[1]));
	}

	private void typeOfAccessYear() {
		this.actualStudent.setTypeAccessGrade(splitedLine[1]);
		this.actualStudent.setYearAccess(Integer.parseInt(splitedLine[3]));
	}

	private void routeAccessInternationalProgram() {
		this.actualStudent.setRouteAccess(splitedLine[1]);
		this.actualStudent.setInternationalProgram("-".equals(splitedLine[3]) ? null : splitedLine[3]);
	}
	
	private void speciality() {
		this.actualStudent.setSpeciality("-".equals(splitedLine[1]) ? null : splitedLine[1]);
	}
	
	private void studyPlan() {
		this.actualStudent.setStudyPlan(splitedLine[1]);
	}
	
	private void numberOfCreditsPassed() {
		this.actualStudent.setNumberCreditsPassed(Integer.parseInt(splitedLine[1]));
	}
	
	private void courseNumberCreditsEnrolled() {
		this.actualStudent.setCourseNumberCreditsEnrolled(Integer.parseInt(splitedLine[1]));
	}
	private void basicAndObligatoryCredits() {
		this.actualStudent.setBasicAndObligatoryCredits(Integer.parseInt(splitedLine[1]));
	}
	
	private void creditsStudyPlan() {
		this.actualStudent.setStudyPlanCredits(Integer.parseInt(splitedLine[1]));
	}

	private void optionalSubjects() {
		nextLine(); // ignore header (Codigo, descripcion, clase)
		while (nextLine()) {
			if ("Asignaturas matriculadas en este curso".equals(splitedLine[0])) {
				enrolledSubjects();
				return;
			} else if (!"XXXX".equals(splitedLine[0])) {
				Subject subject = SubjectFactory.getInstance()
						.getSubject(splitedLine[0], splitedLine[1], splitedLine[2]);
				this.actualStudent.addEnrolledSubject(subject);
			}
		}
	}

	private void enrolledSubjects() {
		nextLine(); // ignore header (Codigo, descripcion, clase)
		while (nextLine()) {
			if ("Observaciones del alumno/a".equals(splitedLine[0])) {
				observations();
				return;

			} else {
				String type = splitedLine.length == 2 ? "Básica" : splitedLine[2];
				Subject subject = SubjectFactory.getInstance()
						.getSubject(splitedLine[0], splitedLine[1], type);
				this.actualStudent.addEnrolledSubject(subject);
			}
		}
	}

	private void observations() {
		while (nextLine()) {
			if ("Ficha Completa".equals(splitedLine[0])) {
				fullRecord();
				return;
			} else {
				this.actualStudent.addObservation(splitedLine[0]);
			}

		}

	}
}
