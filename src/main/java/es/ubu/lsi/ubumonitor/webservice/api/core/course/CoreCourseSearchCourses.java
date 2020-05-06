package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCourseSearchCourses extends WSFunctionAbstract {

	private String value;
	private int page;
	private int perpage;

	public CoreCourseSearchCourses(String value) {

		super(WSFunctionEnum.CORE_COURSE_SEARCH_COURSES);
		this.value = value;
	}

	public CoreCourseSearchCourses(String value, int page, int perpage) {

		this(value);
		this.page = page;
		this.perpage = perpage;
	}

	@Override
	public void addToMapParemeters() {
		parameters.put("criterianame", "search");
		parameters.put("criteriavalue", value);
		parameters.put("page", Integer.toString(page));
		parameters.put("perpage", Integer.toString(perpage));

	}

}
