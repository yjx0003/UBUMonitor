package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.mod.Module;
import model.mod.ModuleType;

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

	private List<EnrolledUser> enrolledUsers;
	private List<Role> roles; // roles que hay en el curso
	private List<Group> groups; // grupos que hay en el curso
	private List<Module> modules;
	private List<GradeItem> gradeItems;
	private List<LogLine> logs;

	static final Logger logger = LoggerFactory.getLogger(Course.class);

	public Course() {
		this.enrolledUsers = new ArrayList<>();
		this.roles = new ArrayList<>();
		this.groups = new ArrayList<>();
		this.gradeItems=new ArrayList<>();
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

	public GradeItem getRootGradeItem() {
		return gradeItems.stream()
				.filter(g -> g.getFather() == null)
				.findFirst()
				.get();
	}

	public List<GradeItem> getGradeItems() {
		return gradeItems;
	}

	public void setGradeItems(List<GradeItem> gradeItems) {
		this.gradeItems = gradeItems;
	}

	public List<LogLine> getLogs() {
		return logs;
	}

	public void setLogs(List<LogLine> logs) {
		this.logs = logs;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Module> getModules() {
		return modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	public void setEnrolledUsers(List<EnrolledUser> enrolledUsers) {
		this.enrolledUsers = enrolledUsers;
	}

	public void addEnrolledUser(EnrolledUser user) {
		enrolledUsers.add(user);
	}

	public void addLog(LogLine log) {
		logs.add(log);

	}

	public List<EnrolledUser> getEnrolledUsers() {
		return enrolledUsers;
	}

	public void addRole(Role role) {
		roles.add(role);

	}

	public List<ModuleType> getUniqueModuleTypes() {
		List<ModuleType> uniqueModulesTypes = new ArrayList<>();
		for (GradeItem gradeItem : gradeItems) {
			ModuleType moduleType = gradeItem.getItemModule();
			if (moduleType != null && uniqueModulesTypes.contains(moduleType)) {
				uniqueModulesTypes.add(moduleType);
			}
		}
		return uniqueModulesTypes;
	}

	@Override
	public String toString() {
		return this.fullName;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Course other = (Course) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
