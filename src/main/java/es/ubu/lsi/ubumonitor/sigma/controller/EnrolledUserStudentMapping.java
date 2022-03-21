package es.ubu.lsi.ubumonitor.sigma.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.sigma.model.Student;

/**
 * Class in charge of mapping enrolled users to students via email.
 * 
 * @author Yi Peng Ji
 *
 */
public class EnrolledUserStudentMapping {
	private static EnrolledUserStudentMapping instance;

	private Map<EnrolledUser, Student> enrolledUserStudentMap;
	private Map<Student, EnrolledUser> studentEnrolledUserMap;
	private EnrolledUserStudentMapping() {
		// private constructor singleton
	}

	public static EnrolledUserStudentMapping getInstance() {
		if (instance == null)
			instance = new EnrolledUserStudentMapping();
		return instance;
	}

	public void map(Collection<EnrolledUser> enrolledUsers, Collection<Student> students) {
		enrolledUserStudentMap = new HashMap<>();
		studentEnrolledUserMap = new HashMap<>();
		for (EnrolledUser enrolledUser : enrolledUsers) {
			students.stream()
					.filter(s -> s.getEmail()
							.equals(enrolledUser.getEmail()))
					.findAny()
					.ifPresent(s -> {
						enrolledUserStudentMap.put(enrolledUser, s);
						studentEnrolledUserMap.put(s, enrolledUser);
						});

		}
	}

	public Student getStudent(EnrolledUser user) {
		return enrolledUserStudentMap.get(user);
	}

	public List<Student> getStudents(Collection<EnrolledUser> enrolledUsers) {

		List<Student> students = new ArrayList<>();
		for (EnrolledUser enrolledUser : enrolledUsers) {
			Student student = enrolledUserStudentMap.get(enrolledUser);
			if (student != null) {
				students.add(student);
			}
		}

		return students;

	}
	
	public EnrolledUser getEnrolledUser(Student student) {
		return studentEnrolledUserMap.get(student);
	}

	public List<EnrolledUser> getEnrolledUsers(Collection<Student> students) {

		List<EnrolledUser> enrolledUsers = new ArrayList<>();
		for (Student student : students) {
			EnrolledUser enrolledUser = studentEnrolledUserMap.get(student);
			if (enrolledUser != null) {
				enrolledUsers.add(enrolledUser);
			}
		}

		return enrolledUsers;

	}
}
