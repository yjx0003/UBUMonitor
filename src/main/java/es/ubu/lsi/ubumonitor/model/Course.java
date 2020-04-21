package es.ubu.lsi.ubumonitor.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase curso (asignatura). Guarda información de los roles, usuarios
 * matriculados y grade item actuales. Guarda también toda los logs del curso.
 * En esta clase solo se almacena info que devuelve moodle.
 * 
 * @author Claudia Martínez Herrero
 * @since 1.0
 * @version 2.4.1.0
 */
public class Course implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(Course.class);
	private int id;
	private String shortName;
	private String fullName;
	private String idNumber;
	private String summary;
	private DescriptionFormat summaryformat;
	private Instant startDate;
	private Instant endDate;
	private boolean isFavorite;

	private CourseCategory courseCategory;

	private Set<EnrolledUser> enrolledUsers;
	private Set<Role> roles; // roles que hay en el curso
	private Set<Group> groups; // grupos que hay en el curso
	private Set<CourseModule> modules;
	private Set<GradeItem> gradeItems;
	private Set<Section> sections;
	private Logs logs;
	private Stats stats;
	private LogStats logStats;
	private byte[] userPhoto;
	private String userFullName;
	private ZonedDateTime updatedCourse;

	public Course() {
		this.enrolledUsers = new HashSet<>();
		this.roles = new HashSet<>();
		this.groups = new HashSet<>();
		this.gradeItems = new HashSet<>();
		this.modules = new LinkedHashSet<>();
		this.sections = new LinkedHashSet<>();
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
	 * @param id El id.
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
	 * @param shortName El nombre corto.
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
	 * @param fullName El nombre completo-
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
	 * @param idNumber El id del curso.
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
	 * @param summary El resumen.
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

	/**
	 * Busca el gradeItem que tenga
	 * 
	 * @return el grade item raiz
	 */
	public GradeItem getRootGradeItem() {
		for (GradeItem gradeItem : gradeItems) {
			if (gradeItem.getFather() == null)
				return gradeItem;
		}
		throw new IllegalStateException(
				"No existe el nodo raíz, no hay un nodo que tenga como atributo 'father' en null");
	}

	/**
	 * Devuelve los grade item del curso.
	 * 
	 * @return los grade item del curso
	 */
	public Set<GradeItem> getGradeItems() {
		return gradeItems;
	}

	/**
	 * Modifica los grade item
	 * 
	 * @param gradeItems grade items
	 */
	public void setGradeItems(Set<GradeItem> gradeItems) {
		this.gradeItems = gradeItems;
	}

	/**
	 * Devuelve el instante de tiempo que empezó el curso
	 * 
	 * @return instante de tiempo que empezó el curso
	 */
	public Instant getStartDate() {
		return startDate;
	}

	/**
	 * Modifica el tiempo
	 * 
	 * @param startDate fecha de inicio
	 */
	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	/**
	 * Devuelve el instante de tiempo de finalización del curso.
	 * 
	 * @return instante de tiempo de finalización del curso
	 */
	public Instant getEndDate() {
		return endDate;
	}

	/**
	 * Modifica la finalización del curso
	 * 
	 * @param endDate nuevo instante de finalización
	 */
	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	/**
	 * Devuelve las estadísticas de calificaciones del curso.
	 * 
	 * @return estadísticas de calificaciónes del curso
	 */
	public Stats getStats() {
		return stats;
	}

	/**
	 * Modifiaca las estadísticas de curso.
	 * 
	 * @param stats nuevas estadísticas del curso
	 */
	public void setStats(Stats stats) {
		this.stats = stats;
	}

	/**
	 * Devuelve los logs completos del curso.
	 * 
	 * @return los logs completos del curso
	 */
	public Logs getLogs() {
		return logs;
	}

	/**
	 * Modifica los logs del curso.
	 * 
	 * @param logs logs completos
	 */
	public void setLogs(Logs logs) {
		this.logs = logs;
	}

	/**
	 * Devuelve los roles actuales en el curso.
	 * 
	 * @return los roles actuales del curso
	 */
	public Set<Role> getRoles() {
		return roles;
	}

	/**
	 * Modifica los roles del curso.
	 * 
	 * @param roles nuevos roles
	 */
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	/**
	 * Devuelve los grupos actuales del curso.
	 * 
	 * @return los grupos actuales del curso
	 */
	public Set<Group> getGroups() {
		return groups;
	}

	/**
	 * Modificar los grupos del curso.
	 * 
	 * @param groups nuevos grupos del curso
	 */
	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	/**
	 * Devuelve los modulos que hay en el curso.
	 * 
	 * @return los modulos del curso
	 */
	public Set<CourseModule> getModules() {
		return modules;
	}

	/**
	 * Modifica los módulos del curso.
	 * 
	 * @param modules modulos del curso
	 */
	public void setModules(Set<CourseModule> modules) {
		this.modules = modules;
	}

	/**
	 * Añade un modulo al curso.
	 * 
	 * @param module nuevo modulo
	 */
	public void addModule(CourseModule module) {
		modules.add(module);

	}

	/**
	 * Devuelve los usuarios del curso.
	 * 
	 * @return los usuarios matriculados en el curso
	 */
	public Set<EnrolledUser> getEnrolledUsers() {
		return enrolledUsers;
	}

	/**
	 * Modifica los usuarios del curso.
	 * 
	 * @param enrolledUsers nuevos usuarios del curso
	 */
	public void setEnrolledUsers(Set<EnrolledUser> enrolledUsers) {
		this.enrolledUsers = enrolledUsers;
	}

	public Set<Section> getSections() {
		return sections;
	}

	public void setSections(Set<Section> sections) {
		this.sections = sections;
	}

	/**
	 * Añade un usuario al curso.
	 * 
	 * @param user nuevo usuario
	 */
	public void addEnrolledUser(EnrolledUser user) {
		enrolledUsers.add(user);
	}

	/**
	 * Añade un rol nuevo rol al curso.
	 * 
	 * @param role nuevo rol
	 */
	public void addRole(Role role) {
		roles.add(role);
	}

	/**
	 * Añade un grupo al curso.
	 * 
	 * @param group nuevo grupo
	 */
	public void addGroup(Group group) {
		groups.add(group);

	}

	/**
	 * Add section to this course.
	 * 
	 * @param section section
	 */
	public void addSection(Section section) {
		sections.add(section);
	}

	/**
	 * Añade un item de calificacion nuevo.
	 * 
	 * @param gradeItem nuevo item de calificación
	 */
	public void addGradeItem(GradeItem gradeItem) {
		gradeItems.add(gradeItem);
	}

	/**
	 * Elimina todos los elementos del curso excepto los logs.
	 */
	public void clear() {
		this.enrolledUsers.clear();
		this.roles.forEach(Role::clear); // eliminamos los usuarios de ese rol
		this.roles.clear();
		this.groups.forEach(Group::clear); // eliminamos los usuarios de ese grupo
		this.groups.clear();
		this.modules.clear();
		this.gradeItems.clear();
		this.sections.clear();
	}

	public LogStats getLogStats() {
		return logStats;
	}

	public void setLogStats(LogStats logStats) {
		this.logStats = logStats;
	}

	public CourseCategory getCourseCategory() {
		return courseCategory;
	}

	public void setCourseCategory(CourseCategory courseCategory) {
		this.courseCategory = courseCategory;
	}

	/**
	 * Devuelve los tipos de modulos del curso actual sin repetir y por orden
	 * natural
	 * 
	 * @return los tipos de modulo sin repetición
	 */
	public Set<ModuleType> getUniqueGradeModuleTypes() {
		return gradeItems.stream().map(GradeItem::getItemModule).filter(Objects::nonNull)
				.collect(Collectors.toCollection(TreeSet::new));

	}

	/**
	 * Devuelve los componentes de logs realizados por usuario actuales del curso.
	 * 
	 * @return componentes de logs realizados por usuario actuales del curso
	 */
	public List<Component> getUniqueComponents() {
		return logs.getList().stream()
				// cogemos las lineas de log de los usuarios que pertenecen al curso actual
				.filter(logLine -> logLine.getUser() != null && enrolledUsers.contains(logLine.getUser()))
				.map(LogLine::getComponent).distinct().collect(Collectors.toList());
	}

	/**
	 * Devuelve los pares componente-evento unicos de los logs de usuarios actuales
	 * del curso
	 * 
	 * @return componente-evento unicos de los logs de usuarios actuales del curso
	 */
	public List<ComponentEvent> getUniqueComponentsEvents() {
		return logs.getList().stream()
				// cogemos las lineas de log de los usuarios que pertenecen al curso actual
				.filter(logLine -> logLine.getUser() != null && enrolledUsers.contains(logLine.getUser()))
				.map(l -> ComponentEvent.get(l.getComponent(), l.getEventName())).distinct()
				.collect(Collectors.toList());
	}

	public List<ModuleType> getUniqueCourseModulesTypes() {
		return modules.stream().map(CourseModule::getModuleType).distinct().collect(Collectors.toList());
	}

	public LocalDate getStart() {
		LOGGER.debug("Fecha de inicio del curso por el servidor: {}", startDate);
		if (startDate.getEpochSecond() == 0) {
			return getEnd();
		}

		return startDate.isBefore(Instant.now()) ? LocalDateTime.ofInstant(startDate, logs.getZoneId()).toLocalDate()
				: LocalDate.now();
	}

	public LocalDate getEnd() {
		LOGGER.debug("Fecha de fin del curso por el servidor: {}", endDate);
		if (endDate.getEpochSecond() == 0) {
			return LocalDate.now();
		}

		return endDate.isBefore(Instant.now()) ? LocalDateTime.ofInstant(endDate, logs.getZoneId()).toLocalDate()
				: LocalDate.now();

	}

	/**
	 * Devuelve el rol de nombre corto "estudiante" con mayor número usuarios
	 * matriculados o null en caso de que no haya ninguno.
	 * 
	 * @return student role or null
	 */
	public Role getStudentRole() {
		return roles.stream().filter(r -> "student".equals(r.getRoleShortName()))
				.max(Comparator.comparingInt(r -> r.getEnrolledUsers().size())).orElse(null);

	}

	@Override
	public String toString() {
		if (this.courseCategory == null) {
			return this.fullName;
		}
		return this.fullName + " (" + this.courseCategory + ")";
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Course))
			return false;
		Course other = (Course) obj;
		return id == other.id;

	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public boolean contains(EnrolledUser enrolledUser) {
		return enrolledUsers.contains(enrolledUser);
	}

	/**
	 * @return the userPhoto
	 */
	public byte[] getUserPhoto() {
		return userPhoto;
	}

	/**
	 * @param userPhoto the userPhoto to set
	 */
	public void setUserPhoto(byte[] userPhoto) {
		this.userPhoto = userPhoto;
	}

	/**
	 * @return the userFullName
	 */
	public String getUserFullName() {
		return userFullName;
	}

	/**
	 * @param userFullName the userFullName to set
	 */
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	/**
	 * @return the updatedCourse
	 */
	public ZonedDateTime getUpdatedCourse() {
		return updatedCourse;
	}

	/**
	 * @param updatedCourse the updatedCourse to set
	 */
	public void setUpdatedCourse(ZonedDateTime updatedCourse) {
		this.updatedCourse = updatedCourse;
	}

}
