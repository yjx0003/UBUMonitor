package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import java.util.Collection;
import java.util.stream.Collectors;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCourseGetCoursesByField extends WSFunctionAbstract {

	public static final String ID = "id";
	public static final String IDS = "ids";
	public static final String ID_NUMBER = "idnumber";
	public static final String ID_CATEGORY = "category";
	private String value;
	private String field;

	public CoreCourseGetCoursesByField(Collection<Integer> ids) {

		this(IDS, ids.stream()
				.map(Object::toString)
				.collect(Collectors.joining(",")));
	}

	public CoreCourseGetCoursesByField(String field, String value) {
		super(WSFunctionEnum.CORE_COURSE_GET_COURSES_BY_FIELD);
		this.field = field;
		this.value = value;
	}

	@Override
	public void addToMapParemeters() {
		parameters.put("field", field);
		parameters.put("value", value);
	}

}
