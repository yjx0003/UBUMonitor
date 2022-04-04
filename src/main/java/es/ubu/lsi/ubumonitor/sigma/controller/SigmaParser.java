package es.ubu.lsi.ubumonitor.sigma.controller;

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

import es.ubu.lsi.ubumonitor.sigma.model.Student;
import es.ubu.lsi.ubumonitor.sigma.model.Subject;

public class SigmaParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(SigmaParser.class);
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");
	private static final String DEFAULT_STRING = "-";

	private Student actualStudent;
	private ArrayList<Student> students;
	private String[] splitedLine;
	private BufferedReader bufferedReader;

	Map<String, Runnable> map = new HashMap<>();

	public SigmaParser() {
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
		map.put("Tipo de asignatura", this::typeOfSubjectYearsConsumed);
		map.put("Número de veces matriculado", this::numberOfEnrols);
		map.put("Tipo de acceso a titulación", this::typeOfAccessYear);
		map.put("Vía de acceso", this::routeAccessInternationalProgram);
		map.put("Especialidad", this::speciality);
		map.put("Plan de estudios", this::studyPlan);
		map.put("Número de créditos superados hasta el curso anterior", this::numberOfCreditsPassed);
		map.put("Número créditos matriculados en este curso", this::courseNumberCreditsEnrolled);
		map.put("Número de créditos de formación básica y obligatorios matriculados en este curso",
				this::basicAndObligatoryCredits);
		map.put("Número de créditos del plan de estudios", this::creditsStudyPlan);
		map.put("Asignaturas optativas superadas", this::optionalSubjects);
		map.put("Asignaturas matriculadas en este curso", this::enrolledSubjects);
		map.put("Observaciones del alumno/a", this::observations);
		this.students = new ArrayList<>();
	}

	public List<Student> parse(File file) throws IOException {
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

	private String getField(int index, String defaultValue) {
		if (index > splitedLine.length || splitedLine[index].trim()
				.isEmpty()) {
			return defaultValue;
		}

		return splitedLine[index];
	}

	private int getField(int index, int defaultValue) {
		try {
			return Integer.parseInt(getField(index));
		} catch (Exception e) {
			return defaultValue;
		}

	}

	private double getField(int index, double defaultValue) {
		try {
			return Double.parseDouble(getField(index));
		} catch (Exception e) {
			return defaultValue;
		}

	}

	private String getField(int index) {
		return getField(index, DEFAULT_STRING);
	}

	private void fullName() {
		this.actualStudent.setFullName(getField(1));
	}

	private void email() {
		this.actualStudent.setEmail(getField(1));
	}

	private void dateOfBirth() {
		LocalDate birthOfDate = LocalDate.parse(getField(1), DATE_FORMATTER);
		if (birthOfDate.isAfter(LocalDate.now())) {
			birthOfDate = birthOfDate.minusYears(100);
		}
		this.actualStudent.setDateOfBirth(birthOfDate);
	}

	private void dniGender() {
		this.actualStudent.setDni(getField(1));
		this.actualStudent.setGender(getField(3));
	}

	private void usualAddress() {
		this.actualStudent.setUsualAddress(getField(1));
	}

	private void usualPhone() {
		this.actualStudent.setUsualPhone(getField(1));
	}

	private void courseAddress() {
		this.actualStudent.setCourseAddress(getField(1));
	}

	private void coursePhone() {
		this.actualStudent.setCoursePhone(getField(1));
	}

	private void center() {
		this.actualStudent.setCenter(getField(1));
	}

	private void subject() {
		this.actualStudent.setSubject(getField(1));
	}

	private void subjectCreditTypeOfTeaching() {
		this.actualStudent.setSubjectCredits(getField(1, 0.0));
		this.actualStudent.setTypeOfTeaching(getField(3));
	}

	private void groupPeriod() {
		this.actualStudent.setGroup(getField(1));
		this.actualStudent.setPeriod(getField(3));
	}

	private void courseYear() {
		this.actualStudent.setCourseYear(getField(1));
	}

	private void typeOfSubjectYearsConsumed() {
		this.actualStudent.setTypeOfSubject(getField(1));
		this.actualStudent.setYearsConsumed(getField(3, 0));
	}

	private void numberOfEnrols() {
		this.actualStudent.setNumberOfEnrols(getField(1, 0));
	}

	private void typeOfAccessYear() {
		this.actualStudent.setTypeAccessGrade(getField(1));
		this.actualStudent.setYearAccess(getField(3, 0));
	}

	private void routeAccessInternationalProgram() {
		this.actualStudent.setRouteAccess(getField(1));
		this.actualStudent.setInternationalProgram(getField(3));
	}

	private void speciality() {
		this.actualStudent.setSpeciality(getField(1));
	}

	private void studyPlan() {
		this.actualStudent.setStudyPlan(getField(1));
	}

	private void numberOfCreditsPassed() {
		this.actualStudent.setNumberCreditsPassed(getField(1, 0));
	}

	private void courseNumberCreditsEnrolled() {
		this.actualStudent.setCourseNumberCreditsEnrolled(getField(1, 0));
	}

	private void basicAndObligatoryCredits() {
		this.actualStudent.setBasicAndObligatoryCreditsEnrolled(getField(1, 0));
	}

	private void creditsStudyPlan() {
		this.actualStudent.setStudyPlanCredits(getField(1, 0));
	}

	private void optionalSubjects() {
		nextLine(); // ignore header (Codigo, descripcion, clase)
		while (nextLine()) {
			if ("Asignaturas matriculadas en este curso".equals(getField(0))) {
				enrolledSubjects();
				return;
			} else if (!"XXXX ".equals(getField(0))) {
				Subject subject = SubjectFactory.getInstance()
						.getSubject(getField(0), getField(1), getField(2));
				this.actualStudent.getOptionalSubjects()
						.add(subject);
			}
		}
	}

	private void enrolledSubjects() {
		nextLine(); // ignore header (Codigo, descripcion, clase)
		while (nextLine()) {
			if ("Observaciones del alumno/a".equals(getField(0))) {
				observations();
				return;

			} else if (!"XXXX ".equals(getField(0))) {
				String type = splitedLine.length == 2 ? "Básica" : getField(2);
				Subject subject = SubjectFactory.getInstance()
						.getSubject(getField(0), getField(1), type);
				this.actualStudent.getEnrolledSubjects()
						.add(subject);
			}
		}
	}

	private void observations() {
		while (nextLine()) {
			String firstSplited = getField(0);
			if ("Ficha Completa".equals(firstSplited)) {
				fullRecord();
				return;
			} else if (!"No hay observaciones definidas para este alumno".equals(firstSplited)
					&& !firstSplited.isEmpty()) {
				this.actualStudent.getObservations()
						.add(getField(0));
			}

		}

	}
}
