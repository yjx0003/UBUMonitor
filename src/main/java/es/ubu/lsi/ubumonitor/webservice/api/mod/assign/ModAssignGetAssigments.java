package es.ubu.lsi.ubumonitor.webservice.api.mod.assign;

import java.util.Collection;
import java.util.Collections;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * Returns the courses and assignments for the users capability.
 * 
 * @author Yi Peng Ji
 *
 */
public class ModAssignGetAssigments extends WSFunctionAbstract {

	public ModAssignGetAssigments() {
		super(WSFunctionEnum.MOD_ASSIGN_GET_ASSIGNMENTS);

	}

	/**
	 * Only one course id
	 * 
	 * @param courseid course id
	 */
	public void setCourseid(int courseid) {
		setCourseids(Collections.singleton(courseid));
	}

	/**
	 * Course id, empty for retrieving all the courses where the user is enroled in.
	 * 
	 * @param courseids 0 or more course ids
	 */
	public void setCourseids(Collection<Integer> courseids) {
		parameters.put("courseids", courseids);
	}

	/**
	 * List of capabilities used to filter courses
	 * 
	 * @param capabilities capability collection
	 */
	public void setCapabilities(Collection<String> capabilities) {
		parameters.put("capabilities", capabilities);
	}

	/**
	 * Whether to return courses that the user can see even if is not enroled in.
	 * This requires the parameter courseids to not be empty.
	 * 
	 * @param includenotenrolledcourses true if include courses is not enroled in
	 */
	public void setIncludenotenrolledcourses(boolean includenotenrolledcourses) {
		parameters.put("includenotenrolledcourses", includenotenrolledcourses ? 1 : 0);
	}

}
