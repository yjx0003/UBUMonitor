package es.ubu.lsi.ubumonitor.sigma.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Student implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static final String NA = "N/A";
	
	private String fullName = NA;
	private String email = NA;
	private LocalDate dateOfBirth = LocalDate.now();
	private String dni = NA;
	private String gender = NA;
	private String usualAddress = NA;
	private String usualPhone = NA;
	private String courseAddress = NA;
	private String coursePhone = NA;
	private String center = NA;
	private String subject = NA;
	private double subjectCredits = 0.0;
	private String typeOfTeaching = NA;
	private String group = NA;
	private String period = NA;
	private String courseYear = NA;
	private String typeOfSubject = NA;
	private int yearsConsumed = -1;
	private int numberOfEnrols = -1;
	private String typeAccessGrade  = NA;
	private int yearAccess  = -1;
	private String routeAccess = NA;
	private String internationalProgram = NA;
	private String speciality = NA;
	private String studyPlan = NA;
	private int numberCreditsPassed = -1;
	private int courseNumberCreditsEnrolled = -1;
	private int basicAndObligatoryCreditsEnrolled = -1;
	private int studyPlanCredits = -1;
	private List<Subject> optionalSubjects = new ArrayList<>();
	private List<Subject> enrolledSubjects = new ArrayList<>();
	private List<String> observations = new ArrayList<>();


	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}



	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}



	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}



	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}



	/**
	 * @return the dateOfBirth
	 */
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}



	/**
	 * @param dateOfBirth the dateOfBirth to set
	 */
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}



	/**
	 * @return the dni
	 */
	public String getDni() {
		return dni;
	}



	/**
	 * @param dni the dni to set
	 */
	public void setDni(String dni) {
		this.dni = dni;
	}



	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}



	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}



	/**
	 * @return the usualAddress
	 */
	public String getUsualAddress() {
		return usualAddress;
	}



	/**
	 * @param usualAddress the usualAddress to set
	 */
	public void setUsualAddress(String usualAddress) {
		this.usualAddress = usualAddress;
	}



	/**
	 * @return the usualPhone
	 */
	public String getUsualPhone() {
		return usualPhone;
	}



	/**
	 * @param usualPhone the usualPhone to set
	 */
	public void setUsualPhone(String usualPhone) {
		this.usualPhone = usualPhone;
	}



	/**
	 * @return the courseAddress
	 */
	public String getCourseAddress() {
		return courseAddress;
	}



	/**
	 * @param courseAddress the courseAddress to set
	 */
	public void setCourseAddress(String courseAddress) {
		this.courseAddress = courseAddress;
	}



	/**
	 * @return the coursePhone
	 */
	public String getCoursePhone() {
		return coursePhone;
	}



	/**
	 * @param coursePhone the coursePhone to set
	 */
	public void setCoursePhone(String coursePhone) {
		this.coursePhone = coursePhone;
	}



	/**
	 * @return the center
	 */
	public String getCenter() {
		return center;
	}



	/**
	 * @param center the center to set
	 */
	public void setCenter(String center) {
		this.center = center;
	}



	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}



	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}



	/**
	 * @return the subjectCredits
	 */
	public double getSubjectCredits() {
		return subjectCredits;
	}



	/**
	 * @param subjectCredits the subjectCredits to set
	 */
	public void setSubjectCredits(double subjectCredits) {
		this.subjectCredits = subjectCredits;
	}



	/**
	 * @return the typeOfTeaching
	 */
	public String getTypeOfTeaching() {
		return typeOfTeaching;
	}



	/**
	 * @param typeOfTeaching the typeOfTeaching to set
	 */
	public void setTypeOfTeaching(String typeOfTeaching) {
		this.typeOfTeaching = typeOfTeaching;
	}



	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}



	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}



	/**
	 * @return the period
	 */
	public String getPeriod() {
		return period;
	}



	/**
	 * @param period the period to set
	 */
	public void setPeriod(String period) {
		this.period = period;
	}



	/**
	 * @return the courseYear
	 */
	public String getCourseYear() {
		return courseYear;
	}



	/**
	 * @param courseYear the courseYear to set
	 */
	public void setCourseYear(String courseYear) {
		this.courseYear = courseYear;
	}



	/**
	 * @return the typeOfSubject
	 */
	public String getTypeOfSubject() {
		return typeOfSubject;
	}



	/**
	 * @param typeOfSubject the typeOfSubject to set
	 */
	public void setTypeOfSubject(String typeOfSubject) {
		this.typeOfSubject = typeOfSubject;
	}



	/**
	 * @return the yearsConsumed
	 */
	public int getYearsConsumed() {
		return yearsConsumed;
	}



	/**
	 * @param yearsConsumed the yearsConsumed to set
	 */
	public void setYearsConsumed(int yearsConsumed) {
		this.yearsConsumed = yearsConsumed;
	}



	/**
	 * @return the numberOfEnrols
	 */
	public int getNumberOfEnrols() {
		return numberOfEnrols;
	}



	/**
	 * @param numberOfEnrols the numberOfEnrols to set
	 */
	public void setNumberOfEnrols(int numberOfEnrols) {
		this.numberOfEnrols = numberOfEnrols;
	}



	/**
	 * @return the typeAccessGrade
	 */
	public String getTypeAccessGrade() {
		return typeAccessGrade;
	}



	/**
	 * @param typeAccessGrade the typeAccessGrade to set
	 */
	public void setTypeAccessGrade(String typeAccessGrade) {
		this.typeAccessGrade = typeAccessGrade;
	}



	/**
	 * @return the yearAccess
	 */
	public int getYearAccess() {
		return yearAccess;
	}



	/**
	 * @param yearAccess the yearAccess to set
	 */
	public void setYearAccess(int yearAccess) {
		this.yearAccess = yearAccess;
	}



	/**
	 * @return the routeAccess
	 */
	public String getRouteAccess() {
		return routeAccess;
	}



	/**
	 * @param routeAccess the routeAccess to set
	 */
	public void setRouteAccess(String routeAccess) {
		this.routeAccess = routeAccess;
	}



	/**
	 * @return the internationalProgram
	 */
	public String getInternationalProgram() {
		return internationalProgram;
	}



	/**
	 * @param internationalProgram the internationalProgram to set
	 */
	public void setInternationalProgram(String internationalProgram) {
		this.internationalProgram = internationalProgram;
	}



	/**
	 * @return the speciality
	 */
	public String getSpeciality() {
		return speciality;
	}



	/**
	 * @param speciality the speciality to set
	 */
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}



	/**
	 * @return the studyPlan
	 */
	public String getStudyPlan() {
		return studyPlan;
	}



	/**
	 * @param studyPlan the studyPlan to set
	 */
	public void setStudyPlan(String studyPlan) {
		this.studyPlan = studyPlan;
	}



	/**
	 * @return the numberCreditsPassed
	 */
	public int getNumberCreditsPassed() {
		return numberCreditsPassed;
	}



	/**
	 * @param numberCreditsPassed the numberCreditsPassed to set
	 */
	public void setNumberCreditsPassed(int numberCreditsPassed) {
		this.numberCreditsPassed = numberCreditsPassed;
	}



	/**
	 * @return the courseNumberCreditsEnrolled
	 */
	public int getCourseNumberCreditsEnrolled() {
		return courseNumberCreditsEnrolled;
	}



	/**
	 * @param courseNumberCreditsEnrolled the courseNumberCreditsEnrolled to set
	 */
	public void setCourseNumberCreditsEnrolled(int courseNumberCreditsEnrolled) {
		this.courseNumberCreditsEnrolled = courseNumberCreditsEnrolled;
	}



	/**
	 * @return the basicAndObligatoryCreditsEnrolled
	 */
	public int getBasicAndObligatoryCreditsEnrolled() {
		return basicAndObligatoryCreditsEnrolled;
	}



	/**
	 * @param basicAndObligatoryCreditsEnrolled the basicAndObligatoryCreditsEnrolled to set
	 */
	public void setBasicAndObligatoryCreditsEnrolled(int basicAndObligatoryCreditsEnrolled) {
		this.basicAndObligatoryCreditsEnrolled = basicAndObligatoryCreditsEnrolled;
	}



	/**
	 * @return the studyPlanCredits
	 */
	public int getStudyPlanCredits() {
		return studyPlanCredits;
	}



	/**
	 * @param studyPlanCredits the studyPlanCredits to set
	 */
	public void setStudyPlanCredits(int studyPlanCredits) {
		this.studyPlanCredits = studyPlanCredits;
	}



	/**
	 * @return the optionalSubjects
	 */
	public List<Subject> getOptionalSubjects() {
		return optionalSubjects;
	}



	/**
	 * @param optionalSubjects the optionalSubjects to set
	 */
	public void setOptionalSubjects(List<Subject> optionalSubjects) {
		this.optionalSubjects = optionalSubjects;
	}



	/**
	 * @return the enrolledSubjects
	 */
	public List<Subject> getEnrolledSubjects() {
		return enrolledSubjects;
	}



	/**
	 * @param enrolledSubjects the enrolledSubjects to set
	 */
	public void setEnrolledSubjects(List<Subject> enrolledSubjects) {
		this.enrolledSubjects = enrolledSubjects;
	}



	/**
	 * @return the observations
	 */
	public List<String> getObservations() {
		return observations;
	}



	/**
	 * @param observations the observations to set
	 */
	public void setObservations(List<String> observations) {
		this.observations = observations;
	}

	public int getYearsOld() {
		return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
	}

	public int getOptionalCreditsEnrolled() {
		return this.courseNumberCreditsEnrolled - basicAndObligatoryCreditsEnrolled;
	}

	@Override
	public String toString() {
		return "Student [fullName=" + fullName + ", email=" + email + ", dateOfBirth=" + dateOfBirth + ", dni=" + dni
				+ ", gender=" + gender + ", usualAddress=" + usualAddress + ", usualPhone=" + usualPhone
				+ ", courseAddress=" + courseAddress + ", coursePhone=" + coursePhone + ", center=" + center
				+ ", subject=" + subject + ", subjectCredits=" + subjectCredits + ", typeOfTeaching=" + typeOfTeaching
				+ ", group=" + group + ", period=" + period + ", courseYear=" + courseYear + ", typeOfSubject="
				+ typeOfSubject + ", yearsConsumed=" + yearsConsumed + ", numberOfEnrols=" + numberOfEnrols
				+ ", typeAccessGrade=" + typeAccessGrade + ", yearAccess=" + yearAccess + ", routeAccess=" + routeAccess
				+ ", internationalProgram=" + internationalProgram + ", speciality=" + speciality + ", studyPlan="
				+ studyPlan + ", numberCreditsPassed=" + numberCreditsPassed + ", courseNumberCreditsEnrolled="
				+ courseNumberCreditsEnrolled + ", basicAndObligatoryCredits=" + basicAndObligatoryCreditsEnrolled
				+ ", studyPlanCredits=" + studyPlanCredits + ", optionalSubjects=" + optionalSubjects
				+ ", enrolledSubjects=" + enrolledSubjects + ", observations=" + observations + "]";
	}

	

}
