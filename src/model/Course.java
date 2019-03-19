package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.mod.Module;

/**
 * Clase curso (asignatura). Cada curso tiene un calificador (compuesto por
 * líneas de calificación o GradeItems); y varios grupos, roles de
 * participantes, y tipos de actividades.
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class Course implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String shortName;
	private String fullName;
	private String idNumber;
	private String summary;
	private DescriptionFormat summaryformat;

	private Map<Integer, EnrolledUser> enrolledUsers;
	private Map<Integer, Role> roles; // roles que hay en el curso
	private Map<Integer, Group> groups; // grupos que hay en el curso
	private Map<Integer,Module> modules;
	private GradeItem rootGradeItem;
	private List<Log> logs;

	static final Logger logger = LoggerFactory.getLogger(Course.class);

	public Course() {
		this.enrolledUsers = new HashMap<>();
		this.roles= new HashMap<>();
		this.groups= new HashMap<>();

		this.logs = new ArrayList<>();
	}

	public Course(int id, String shortName, String fullName, String idNumber, String summary,
			DescriptionFormat summaryformat) {
		this();
		this.id = id;
		this.shortName = shortName;
		this.fullName = fullName;
		this.idNumber = idNumber;
		this.summary = summary;
		this.summaryformat = summaryformat;
	}

	/**
	 * Devuelve el id del curso.
	 * 
	 * @return id del curso
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Modifica el id del curso.
	 * 
	 * @param id
	 *            El id.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el nombre corto del curso.
	 * 
	 * @return shortName
	 */
	public String getShortName() {
		return this.shortName;
	}

	/**
	 * Modifica el nombre corto del curso.
	 * 
	 * @param shortName
	 *            El nombre corto.
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * Devuelve el nombre del curso.
	 * 
	 * @return fullName
	 */
	public String getFullName() {
		return this.fullName;
	}

	/**
	 * Modifica el nombre del curso.
	 * 
	 * @param fullName
	 *            El nombre completo-
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Devuelve el nº de usuarios del curso.
	 * 
	 * @return enrolledUsersCount
	 */
	public int getEnrolledUsersCount() {
		return this.enrolledUsers.size();
	}

	/**
	 * Devuelve el idNumber del curso
	 * 
	 * @return idNumber
	 */
	public String getIdNumber() {
		return this.idNumber;
	}

	/**
	 * Modifica el idNumber del curso.
	 * 
	 * @param idNumber
	 *            El id del curso.
	 */
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	/**
	 * Devuelve el resumen del curso.
	 * 
	 * @return summary
	 */
	public String getSummary() {
		return this.summary;
	}

	/**
	 * Modifica el resumen del curso.
	 * 
	 * @param summary
	 *            El resumen.
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	

	public DescriptionFormat getSummaryformat() {
		return summaryformat;
	}

	public void setSummaryformat(DescriptionFormat summaryformat) {
		this.summaryformat = summaryformat;
	}

	public Map<Integer, Group> getGroups() {
		return groups;
	}

	public void setGroups(Map<Integer, Group> groups) {
		this.groups = groups;
	}

	public Map<Integer, Module> getModules() {
		return modules;
	}

	public void setModules(Map<Integer, Module> modules) {
		this.modules = modules;
	}

	public GradeItem getRootGradeItem() {
		return rootGradeItem;
	}

	public void setRootGradeItem(GradeItem rootGradeItem) {
		this.rootGradeItem = rootGradeItem;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

	public void setEnrolledUsers(Map<Integer, EnrolledUser> enrolledUsers) {
		this.enrolledUsers = enrolledUsers;
	}

	public void setRoles(Map<Integer, Role> roles) {
		this.roles = roles;
	}



	public void addEnrolledUser(int id,EnrolledUser user) {
		enrolledUsers.put(id,user);
	}

	  

	public void addLog(Log log) {
		logs.add(log);

	}

	@Override
	public String toString() {
		return this.fullName;
	}

}
