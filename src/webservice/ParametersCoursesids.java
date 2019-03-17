package webservice;

import java.util.HashSet;
import java.util.Set;

public abstract class Coursesids extends WebService {

	private Set<Integer> coursesids;

	public Coursesids(Set<Integer> coursesIds) {
		this.coursesids = coursesIds;

	}

	public Coursesids() {
		coursesids = new HashSet<Integer>();

	}

	@Override
	public void appendToUrlParameters() {

		appendToUrlCoursesids(coursesids);

	}

	public Coursesids(int courseid) {
		this();
		coursesids.add(courseid);

	}

	public void addCourseId(int courseId) {
		coursesids.add(courseId);
	}

	public void removeCourseId(int courseId) {
		coursesids.remove(courseId);
	}

}
