package es.ubu.lsi.ubumonitor.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase para el usuario logeado en la aplicación
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class MoodleUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String userName;
	private String fullName;
	private String email;
	private Instant firstAccess;
	private Instant lastAccess;
	private String city;
	private String country;
	private String lang;
	private byte[] userPhoto;
	private List<Course> courses;
	private List<Course> recentCourses;
	private List<Course> inProgressCourses;
	private List<Course> futureCourses;
	private List<Course> pastCourses;
	private ZoneId timezone;
	private ZoneId serverTimezone;

	/**
	 * Constructor de MoodleUser sin parámetros
	 */
	public MoodleUser() {
		this.courses = new ArrayList<>();
		this.setRecentCourses(new ArrayList<>());
		this.setFutureCourses(new ArrayList<>());
		this.setInProgressCourses(new ArrayList<>());
		this.setPastCourses(new ArrayList<>());
	}

	/**
	 * Devuelve el id del usuario
	 * 
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Modifica el id del usuario.
	 * 
	 * @param id
	 *            El id.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el nombre de usuario
	 * 
	 * @return userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * Modifica el nombre del usuario.
	 * 
	 * @param userName
	 *            El nombre de usuario.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Devuelve el nombre completo del usuario
	 * 
	 * @return fullName
	 */
	public String getFullName() {
		return this.fullName;
	}

	/**
	 * Modifica el nombre completo del usuario.
	 * 
	 * @param fullName
	 *            El nombre completo.
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Devuelve el email del usuario
	 * 
	 * @return email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Modifica el email del usuario.
	 * 
	 * @param email
	 *            EL email.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Devuelve la fecha de primer acceso
	 * 
	 * @return firstAccess
	 */
	public Instant getFirstAccess() {
		return this.firstAccess;
	}

	/**
	 * Modifica la fecha de primer acceso.
	 * 
	 * @param firstAccess
	 *            La fecha de primer acceso.
	 */
	public void setFirstAccess(Instant firstAccess) {
		this.firstAccess = firstAccess;
	}

	/**
	 * Devuelve la fecha de último acceso
	 * 
	 * @return lastAccess
	 */
	public Instant getLastAccess() {
		return this.lastAccess;
	}

	/**
	 * Modifica la fecha de último acceso.
	 * 
	 * @param lastAccess
	 *            La fecha de último acceso.
	 */
	public void setLastAccess(Instant lastAccess) {
		this.lastAccess = lastAccess;
	}

	/**
	 * Devuelve la ciudad del usuario
	 * 
	 * @return city
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * Modifica la ciudad del usuario.
	 * 
	 * @param city
	 *            La ciudad.
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Devuelve el país
	 * 
	 * @return country
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * Modifica el país del usuario.
	 * 
	 * @param country
	 *            El país.
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the userPhoto
	 */
	public byte[] getUserPhoto() {
		return userPhoto;
	}

	/**
	 * @param userPhoto
	 *            the userPhoto to set
	 */
	public void setUserPhoto(byte[] userPhoto) {
		this.userPhoto = userPhoto;
	}

	/**
	 * Devuelve la lista de cursos que en los que está atriculado el usuario
	 * 
	 * @return lista de cursos
	 */
	public List<Course> getCourses() {
		return this.courses;
	}
	
	
	public Course getCourseById(int id) {
		for(Course course: courses) {
			if (course.getId() == id)
				return course;
		}
		return null;
	}

	/**
	 * Modifica la lista de cursos en los que está matriculado el usuario.
	 * 
	 * @param courses
	 *            La lista de cursos.
	 */
	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	public void setTimezone(ZoneId zoneId) {
		this.timezone = zoneId;

	}

	public ZoneId getTimezone() {
		return this.timezone;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public List<Course> getFavoriteCourses() {
		return courses
				.stream()
				.filter(Course::isFavorite)
				.collect(Collectors.toList());
	}

	public List<Course> getRecentCourses() {
		return recentCourses;
	}

	public void setRecentCourses(List<Course> recentCourses) {
		this.recentCourses = recentCourses;
	}

	public List<Course> getInProgressCourses() {
		return inProgressCourses;
	}

	public void setInProgressCourses(List<Course> inProgressCourses) {
		this.inProgressCourses = inProgressCourses;
	}

	public List<Course> getFutureCourses() {
		return futureCourses;
	}

	public void setFutureCourses(List<Course> futureCourses) {
		this.futureCourses = futureCourses;
	}

	public List<Course> getPastCourses() {
		return pastCourses;
	}

	public void setPastCourses(List<Course> pastCourses) {
		this.pastCourses = pastCourses;
	}

	/**
	 * @return the serverTimezone
	 */
	public ZoneId getServerTimezone() {
		return serverTimezone;
	}

	/**
	 * @param serverTimezone the serverTimezone to set
	 */
	public void setServerTimezone(ZoneId serverTimezone) {
		this.serverTimezone = serverTimezone;
	}
}
