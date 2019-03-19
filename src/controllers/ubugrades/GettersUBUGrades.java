package controllers.ubugrades;

import java.util.HashMap;
import java.util.Map;

import model.Course;
import model.EnrolledUser;
import model.Group;
import model.Role;
import model.mod.Module;

public class GettersUBUGrades {
	
	protected static Map<Integer, Role> roles = new HashMap<Integer, Role>();
	protected static Map<Integer, Group> groups = new HashMap<Integer, Group>();
	protected static Map<Integer, EnrolledUser> users = new HashMap<Integer, EnrolledUser>();
	protected static Map<Integer, Module> modules = new HashMap<Integer, Module>();
	protected static Map<Integer, Course> courses = new HashMap<Integer, Course>();
	
	


	public static Map<Integer, Role> getRoles() {
		return roles;
	}

	public static Map<Integer, Group> getGroups() {
		return groups;
	}

	public static Map<Integer, EnrolledUser> getUsers() {
		return users;
	}

	public static Map<Integer, Module> getModules() {
		return modules;
	}

	public static Map<Integer, Course> getCourses() {
		return courses;
	}
	

	public static Role getRoleById(int roleid) {
		return roles.get(roleid);
	}
	
	public static Group getGroupById(int id) {
		return groups.get(id);
	}
	
	public static EnrolledUser getEnrolledUserById(int id) {
		return users.get(id);
	}
	
	public static Course getCourseById(int id) {
		return courses.get(id);
	}
	
	public static Module getCourseModuleById(int id) {
		return modules.get(id);
	}
}
