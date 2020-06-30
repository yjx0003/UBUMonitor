package es.ubu.lsi.ubumonitor.webservice.api.mod.assign;

import java.util.HashSet;
import java.util.Set;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class ModAssignGetAssigments extends WSFunctionAbstract {

	private Set<Integer> courseids;
	private Set<String> capabilities;
	private Boolean includenotenrolledcourses;

	public ModAssignGetAssigments() {
		this(new HashSet<>());
		
	}

	public ModAssignGetAssigments(Set<Integer> courseids) {
		super(WSFunctionEnum.MOD_ASSIGN_GET_ASSIGNMENTS);
		this.courseids = courseids;
		this.capabilities = new HashSet<>();
	}

	public Set<Integer> getCourseids() {
		return courseids;
	}

	public void setCourseids(Set<Integer> courseids) {
		this.courseids = courseids;
	}

	public Set<String> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Set<String> capabilities) {
		this.capabilities = capabilities;
	}

	public Boolean getIncludenotenrolledcourses() {
		return includenotenrolledcourses;
	}

	public void setIncludenotenrolledcourses(Boolean includenotenrolledcourses) {
		this.includenotenrolledcourses = includenotenrolledcourses;
	}

	@Override
	public void addToMapParemeters() {
		Util.putIfNotNull(parameters, "courseids", courseids);
		Util.putIfNotNull(parameters, "capabilities", capabilities);
		Util.putIfNotNull(parameters, "includenotenrolledcourses", Util.booleanToBinary(includenotenrolledcourses));
	}

}
