package webservice;

import java.util.HashSet;
import java.util.Set;

public abstract class ParametersCoursesids extends WebService {

	private Set<Integer> coursesids;

	public ParametersCoursesids(Set<Integer> coursesIds) {
		this.coursesids = coursesIds;

	}

	public ParametersCoursesids() {
		coursesids = new HashSet<Integer>();

	}

	@Override
	public void appendToUrlParameters() {

		appendToUrlCoursesids(coursesids);

	}

	public ParametersCoursesids(int courseid) {
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
