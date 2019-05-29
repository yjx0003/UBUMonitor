package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import model.mod.Module;
import model.mod.ModuleType;

public class BBDD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Integer, Role> roles;
	private Map<Integer, Group> groups;
	private Map<Integer, EnrolledUser> users;
	private Map<Integer, Module> modules;
	private Map<Integer, Course> courses;
	private Map<Integer, GradeItem> gradeItems;
	private Map<Integer, CourseCategory> courseCategories;

	/**
	 * Curso actual del usuario
	 */
	private Course actualCourse;

	public BBDD() {
		roles = new HashMap<Integer, Role>();
		groups = new HashMap<Integer, Group>();
		users = new HashMap<Integer, EnrolledUser>();
		modules = new HashMap<Integer, Module>();
		courses = new HashMap<Integer, Course>();
		gradeItems = new HashMap<Integer, GradeItem>();
		courseCategories = new HashMap<Integer, CourseCategory>();
	}

	public BBDD(BBDD BBDD) {
		roles = BBDD.roles;
		groups = BBDD.groups;
		users = BBDD.users;
		modules = BBDD.modules;
		courses = BBDD.courses;
		gradeItems = BBDD.gradeItems;

		actualCourse = BBDD.actualCourse;
	}

	/**
	 * Devuelve el curso actual.
	 * 
	 * @return curso actual.
	 */
	public Course getActualCourse() {
		return actualCourse;
	}

	public void setActualCourse(Course course) {
		actualCourse = course;
	}

	public Map<Integer, Role> getRoles() {
		return roles;
	}

	public Role getRoleById(int id) {
		return roles.computeIfAbsent(id, key -> new Role(id));
	}

	public boolean containsRole(int id) {
		return roles.containsKey(id);
	}

	public Role putRole(Role role) {
		return roles.putIfAbsent(role.getId(), role);
	}

	// #################################################################################
	public Map<Integer, Group> getGroups() {
		return groups;
	}

	public Group getGroupById(int id) {
		return groups.computeIfAbsent(id, key -> new Group(id));
	}

	public boolean containsGroup(int id) {
		return groups.containsKey(id);
	}

	public Group putGroup(Group group) {
		return groups.putIfAbsent(group.getId(), group);
	}

	// #################################################################################

	public Map<Integer, EnrolledUser> getUsers() {
		return users;
	}

	public EnrolledUser getEnrolledUserById(int id) {
		return users.computeIfAbsent(id, key -> new EnrolledUser(id));
	}

	public boolean containsEnrolledUser(int id) {
		return users.containsKey(id);
	}

	public EnrolledUser putEnrolledUser(EnrolledUser user) {
		return users.putIfAbsent(user.getId(), user);
	}

	// #################################################################################
	public Map<Integer, Module> getModules() {
		return modules;
	}

	public Module getCourseModuleById(int id) {
		return modules.computeIfAbsent(id, key -> new Module(id));
	}

	public Module getCourseModuleByIdOrCreate(int id, ModuleType moduleType) {
		return modules.computeIfAbsent(id, key -> moduleType.createInstance(id));
	}

	public boolean containsModule(int id) {
		return modules.containsKey(id);
	}

	public Module putModule(Module module) {
		return modules.putIfAbsent(module.getId(), module);
	}

	// #################################################################################
	public Map<Integer, Course> getCourses() {
		return courses;
	}

	public boolean containsCourse(int id) {
		return courses.containsKey(id);
	}

	public Course getCourseById(int id) {
		return courses.computeIfAbsent(id, key -> new Course(id));
	}

	public Course putCourse(Course course) {
		return courses.putIfAbsent(course.getId(), course);
	}

	// #################################################################################
	public Map<Integer, GradeItem> getGradeItems() {
		return gradeItems;
	}

	public boolean containsGradeItem(int id) {
		return gradeItems.containsKey(id);
	}

	public GradeItem getGradeItemById(int id) {
		return gradeItems.computeIfAbsent(id, key -> new GradeItem(id));
	}

	/**
	 * Añade el grade item a la base de datos si no existia anteriormente.
	 * 
	 * @param gradeItem el gradeItem que se quiere añadir
	 * @return el valor anterior asociado al id del grade item, null si no contenia.
	 */
	public GradeItem putGradeItem(GradeItem gradeItem) {
		return gradeItems.putIfAbsent(gradeItem.getId(), gradeItem);
	}

	public void clearGradeItems() {
		gradeItems.clear();
	}

	// #################################################################################

	public Map<Integer, CourseCategory> getCourseCategories() {
		return courseCategories;
	}

	public boolean containsCourseCategory(int id) {
		return courseCategories.containsKey(id);
	}

	public CourseCategory getCourseCategoryById(int id) {
		return courseCategories.computeIfAbsent(id, key -> new CourseCategory(id));
	}

	public CourseCategory putCourseCategory(CourseCategory courseCategory) {
		return courseCategories.putIfAbsent(courseCategory.getId(), courseCategory);
	}

	@Override
	public String toString() {
		return "BBDD [\nroles=" + roles + ",\n groups=" + groups + ",\n users=" + users + ",\n modules=" + modules
				+ ",\n courses=" + courses + ",\n gradeItems=" + gradeItems + ",\n " + "actualCourse=" + actualCourse
				+ "]";
	}
}
