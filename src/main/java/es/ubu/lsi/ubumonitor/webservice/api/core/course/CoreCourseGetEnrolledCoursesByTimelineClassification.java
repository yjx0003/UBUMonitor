package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCourseGetEnrolledCoursesByTimelineClassification extends WSFunctionAbstract {
	
	
	public static final String PAST = "past";
	public static final String IN_PROGRESS = "inprogress";
	public static final String FUTURE = "future";
	private String classification;

	public CoreCourseGetEnrolledCoursesByTimelineClassification(String classification) {
		super(WSFunctionEnum.CORE_COURSE_GET_ENROLLED_COURSES_BY_TIMELINE_CLASSIFICATION);
		this.setClassification(classification);
	}

	/**
	 * @return the classification
	 */
	public String getClassification() {
		return classification;
	}

	/**
	 * @param classification the classification to set
	 */
	public void setClassification(String classification) {
		this.classification = classification;
	}

	@Override
	public void addToMapParemeters() {
		parameters.put("classification", classification);

	}
}
