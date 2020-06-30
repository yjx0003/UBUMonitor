package es.ubu.lsi.ubumonitor.webservice.api.core.course;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCourseGetRecentCourses extends WSFunctionAbstract {
	/**
	 * id of the user, default to current user
	 */
	private Integer userid;

	/**
	 * result set limit
	 */
	private Integer limit;

	/**
	 * Result set offset
	 */
	private Integer offset;

	/**
	 * Sort string
	 */
	private String sort;

	public CoreCourseGetRecentCourses() {
		super(WSFunctionEnum.CORE_COURSE_GET_RECENT_COURSES);
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	@Override
	public void addToMapParemeters() {
		Util.putIfNotNull(parameters, "userid", userid);
		Util.putIfNotNull(parameters, "limit", limit);
		Util.putIfNotNull(parameters, "offset", offset);
		Util.putIfNotNull(parameters, "sort", sort);
	}

}
