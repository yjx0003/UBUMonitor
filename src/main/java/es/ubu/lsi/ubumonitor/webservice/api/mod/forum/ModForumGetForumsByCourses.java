package es.ubu.lsi.ubumonitor.webservice.api.mod.forum;

import java.util.Collection;
import java.util.Collections;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * Returns a list of forum instances in a provided set of courses, if no courses
 * are provided then all the forum instances the user has access to will be
 * returned.
 * 
 * @author Yi Peng Ji
 *
 */
public class ModForumGetForumsByCourses extends WSFunctionAbstract {

	public ModForumGetForumsByCourses(int courseid) {
		this(WSFunctionEnum.MOD_FORUM_GET_FORUMS_BY_COURSES, courseid);

	}

	public ModForumGetForumsByCourses(Collection<Integer> courseids) {
		this(WSFunctionEnum.MOD_FORUM_GET_FORUMS_BY_COURSES, courseids);

	}

	protected ModForumGetForumsByCourses(WSFunctionEnum wsFunction, int courseid) {
		super(wsFunction);
		setCourseid(courseid);
	}

	protected ModForumGetForumsByCourses(WSFunctionEnum wsFunction, Collection<Integer> courseids) {
		super(wsFunction);
		setCourseids(courseids);
	}

	/**
	 * Course id.
	 * @param courseid course id
	 */
	public void setCourseid(int courseid) {
		setCourseids(Collections.singleton(courseid));
	}
	
	/**
	 * Course ids.
	 * @param courseids course ids
	 */
	public void setCourseids(Collection<Integer> courseids) {
		parameters.put("courseids", courseids);

	}

}
