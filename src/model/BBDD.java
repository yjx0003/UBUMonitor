package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import model.mod.Module;

public class BBDD implements Serializable{

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

	public BBDD() {
		roles = new HashMap<Integer, Role>();
		groups = new HashMap<Integer, Group>();
		users = new HashMap<Integer, EnrolledUser>();
		modules = new HashMap<Integer, Module>();
		courses = new HashMap<Integer, Course>();
		gradeItems=new HashMap<Integer, GradeItem>();
	}

	private Course actualCourse;

	public Course getActualCourse() {
		return actualCourse;
	}

	public void setActualCourse(Course course) {
		actualCourse = course;
	}

	public Map<Integer, Role> getRoles() {
		return roles;
	}

	public Role getRoleById(int roleid) {
		return roles.get(roleid);
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
		return groups.get(id);
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
		return users.get(id);
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
		return modules.get(id);
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
		return courses.get(id);
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
		return gradeItems.get(id);
	}
	/**
	 * Añade el grade item a la base de datos si no existia anteriormente.
	 * 
	 * @param gradeItem
	 * @return el valor anterior asociado al id del grade item, null si no contenia.
	 */
	public GradeItem putGradeItem(GradeItem gradeItem) {
		return gradeItems.putIfAbsent(gradeItem.getId(), gradeItem);
	}
}
