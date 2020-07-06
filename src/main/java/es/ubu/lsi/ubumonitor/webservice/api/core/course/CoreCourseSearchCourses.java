package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCourseSearchCourses extends WSFunctionAbstract {

	public CoreCourseSearchCourses() {

		super(WSFunctionEnum.CORE_COURSE_SEARCH_COURSES);
	}

	private void setBy(String criterianame, Object criteriavalue) {
		parameters.put("criterianame", criterianame);
		parameters.put("criteriavalue", criteriavalue);
	}

	/**
	 * Search
	 * 
	 * @param value value to search
	 */
	public void setBySearch(String value) {
		setBy("search", value);
	}

	/**
	 * Modulelist (only admins)
	 * 
	 * @param value modulelist
	 */
	public void setByModuleList(String value) {
		setBy("modulelist", value);
	}

	/**
	 * Blocklist (only admins)
	 * 
	 * @param value
	 */
	public void setByBlocklist(String value) {
		setBy("blocklist", value);
	}

	/**
	 * Tag id.
	 * 
	 * @param tagid tag id
	 */
	public void setByTagid(int tagid) {
		setBy("tagid", tagid);
	}

	@Override
	public void addToMapParemeters() {
		if (!parameters.has("criterianame") || !parameters.has("criteriavalue")) {
			throw new IllegalArgumentException("Missing criterianame and/or criteriavalue parameters");
		}
	}
}
