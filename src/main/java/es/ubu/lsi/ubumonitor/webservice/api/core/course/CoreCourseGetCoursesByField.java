package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import java.util.Collection;
import java.util.stream.Collectors;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCourseGetCoursesByField extends WSFunctionAbstract {

	public CoreCourseGetCoursesByField() {
		super(WSFunctionEnum.CORE_COURSE_GET_COURSES_BY_FIELD);
	}

	private void put(String field, Object value) {
		parameters.put("field", field);
		parameters.put("value", value);
	}

	/**
	 * Course id.
	 * 
	 * @param id Course id
	 */
	public void setId(int id) {
		put("id", id);
	}

	/**
	 * Comma separated course ids
	 * 
	 * @param ids course ids
	 */
	public void setIds(Collection<Integer> ids) {
		put("ids", ids.stream()
				.map(Object::toString)
				.collect(Collectors.joining(",")));
	}

	/**
	 * Course short name.
	 * 
	 * @param shortname Course short name
	 */
	public void setShortname(String shortname) {
		put("shortname", shortname);
	}

	/**
	 * Course id number.
	 * 
	 * @param idnumber Course id number
	 */
	public void setIdnumber(int idnumber) {
		put("idnumber", idnumber);
	}

	/**
	 * Category id the course belongs to
	 * 
	 * @param categoryid Category id the course belongs to
	 */
	public void setCategoryid(int categoryid) {
		put("categoryid", categoryid);
	}

	@Override
	public void addToMapParemeters() {
		if (parameters.length() != 2) {
			throw new IllegalArgumentException("Should be only one field and value");
		}
	}
}
