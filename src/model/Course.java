package model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

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
	private Instant startDate;
	private Instant endDate;

	private Set<EnrolledUser> enrolledUsers;
	private Set<Role> roles; // roles que hay en el curso
	private Set<Group> groups; // grupos que hay en el curso
	private Set<Module> modules;
	private Set<GradeItem> gradeItems;
	private Logs logs;
	private Stats stats;

	static final Logger logger = LoggerFactory.getLogger(Course.class);

	public Course() {
		this.enrolledUsers = new TreeSet<>(
				Comparator.comparing((Function<EnrolledUser, String> & Serializable) EnrolledUser::getLastname,
							String.CASE_INSENSITIVE_ORDER)
					.thenComparing(
							(Function<EnrolledUser, String> & Serializable) EnrolledUser::getFirstname,
							String.CASE_INSENSITIVE_ORDER));
		this.roles = new HashSet<>();
		this.groups = new HashSet<>();
		this.gradeItems = new HashSet<>();
		this.modules = new HashSet<>();
	}

	public Course(int id, String shortName, String fullName, String idNumber, String summary,
			DescriptionFormat summaryformat, Instant startDate, Instant endDate) {
		this();
		this.id = id;
		this.shortName = shortName;
		this.fullName = fullName;
		this.idNumber = idNumber;
		this.summary = summary;
		this.summaryformat = summaryformat;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Course(int id) {
		this();
		this.id = id;
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
		for (GradeItem gradeItem : gradeItems) {
			if (gradeItem.getFather() == null)
				return gradeItem;
		}
		throw new IllegalStateException(
				"No existe el nodo raíz, no hay un nodo que tenga como atributo 'father' en null");
	}

	public Set<GradeItem> getGradeItems() {
		return gradeItems;
	}

	public void setGradeItems(Set<GradeItem> gradeItems) {
		this.gradeItems = gradeItems;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public Stats getStats() {
		return stats;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	public Logs getLogs() {
		return logs;
	}

	public void setLogs(Logs logs) {
		this.logs = logs;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public Set<Module> getModules() {
		return modules;
	}

	public void setModules(Set<Module> modules) {
		this.modules = modules;
	}

	public void setEnrolledUsers(Set<EnrolledUser> enrolledUsers) {
		this.enrolledUsers = enrolledUsers;
	}

	public void addEnrolledUser(EnrolledUser user) {
		enrolledUsers.add(user);
	}

	public Set<EnrolledUser> getEnrolledUsers() {
		return enrolledUsers;
	}

	public void addRole(Role role) {
		roles.add(role);
		role.setCourse(this);

	}

	public void addGroup(Group group) {
		groups.add(group);
		group.setCourse(this);

	}

	public void addGradeItem(GradeItem gradeItem) {
		gradeItems.add(gradeItem);
		gradeItem.setCourse(this);

	}

	public void clear() {
		this.enrolledUsers.clear();
		this.roles.clear();
		this.groups.clear();
		this.modules.clear();
	}

	public Set<ModuleType> getUniqueModuleTypes() {
		Set<ModuleType> uniqueModulesTypes = gradeItems.stream()
				.map(GradeItem::getItemModule)
				.filter(Objects::nonNull)
				.collect(Collectors.toCollection(TreeSet::new));
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

	public void addModule(Module module) {
		modules.add(module);
		module.setCourse(this);

	}

}
