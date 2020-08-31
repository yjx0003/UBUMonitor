package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * List of enrolled courses for the given timeline classification.
 * 
 * @author Yi Peng Ji
 *
 */
public class CoreCourseGetEnrolledCoursesByTimelineClassification extends WSFunctionAbstract {

	public enum Classification {
		FUTURE("future"), IN_PROGRESS("inprogress"), PAST("past"), FAVOURITES("favourites"), HIDDEN("hidden"), ALL("all"); // all
																														// option
																														// excludes
		// hidden courses

		private String classificationName;

		private Classification(String classificationName) {
			this.classificationName = classificationName;
		}

		public String getClassificationName() {
			return classificationName;
		}

		@Override
		public String toString() {
			return classificationName;
		}
	}

	public CoreCourseGetEnrolledCoursesByTimelineClassification(Classification classification) {
		super(WSFunctionEnum.CORE_COURSE_GET_ENROLLED_COURSES_BY_TIMELINE_CLASSIFICATION);
		setClassification(classification);
	}

	/**
	 * Classification (required).
	 * 
	 * @param classification future, inprogress, past, hidden or all
	 */
	public void setClassification(Classification classification) {
		if (classification != null)
			parameters.put("classification", classification.getClassificationName());
	}

	/**
	 * Result set limit (default: 0).
	 * 
	 * @param limit Result set limit
	 */
	public void setLimit(int limit) {
		parameters.put("limit", limit);
	}

	/**
	 * Result set offset (default: 0).
	 * 
	 * @param offset Result set offset.
	 */
	public void setOffset(int offset) {
		parameters.put("offset", offset);
	}

	/**
	 * Sort string column name (default: null)
	 * 
	 * @param sort column name
	 */
	public void setSort(String sort) {
		parameters.put("sort", sort);
	}

	@Override
	public void addToMapParemeters() {
		if (!parameters.has("classification")) {
			throw new IllegalArgumentException("Required classification argument");
		}
	}
}
