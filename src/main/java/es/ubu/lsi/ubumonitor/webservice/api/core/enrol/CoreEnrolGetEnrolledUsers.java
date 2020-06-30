package es.ubu.lsi.ubumonitor.webservice.api.core.enrol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreEnrolGetEnrolledUsers extends WSFunctionAbstract {

	/**
	 * return only users with this capability. This option requires
	 * 'moodle/role:review' on the course context.
	 * https://docs.moodle.org/37/en/Category:Capabilities
	 */
	private String withcapability;

	/**
	 * return only users in this group id. If the course has groups enabled and this
	 * param isn't defined, returns all the viewable users. This option requires
	 * 'moodle/site:accessallgroups' on the course context if the user doesn't
	 * belong to the group.
	 */
	private Integer groupid;

	/**
	 * return only users with active enrolments and matching time restrictions. This
	 * option requires 'moodle/course:enrolreview' on the course context.
	 */
	private Integer onlyactive;

	/**
	 * return only the values of these user fields.
	 */
	private Set<String> userfields;

	/**
	 * sql limit from.
	 */
	private Integer limitfrom;

	/**
	 * maximum number of returned users.
	 */
	private Integer limitnumber;

	/**
	 * sort by id, firstname or lastname. For ordering like the site does, use
	 * siteorder.
	 */
	private String sortby;

	/**
	 * ASC or DESC
	 */
	private String sortdirection;

	private Integer courseid;

	public CoreEnrolGetEnrolledUsers(Integer courseid) {
		super(WSFunctionEnum.CORE_ENROL_GET_ENROLLED_USERS);
		this.courseid = courseid;
	}

	/**
	 * 
	 * Constructor privado que recibe un builder con parametros adicionales de la
	 * función de Moodle.
	 * 
	 * @param builder
	 *            builder
	 */
	private CoreEnrolGetEnrolledUsers(Builder builder) {
		this(builder.courseid);
		withcapability = builder.withcapability;
		groupid = builder.groupid;
		onlyactive = builder.onlyactive;
		userfields = builder.userfields;
		limitfrom = builder.limitfrom;
		limitnumber = builder.limitnumber;
		sortby = builder.sortby;
		sortdirection = builder.sortdirection;

	}

	@Override
	public void addToMapParemeters() {

		Map<String, Object> map = new HashMap<>();

		parameters.put("courseid", Integer.toString(courseid));
		map.put("withcapability", withcapability);
		map.put("groupid", groupid);
		map.put("onlyactive", onlyactive);
		map.put("userfields", userfields);
		map.put("limitfrom", limitfrom);
		map.put("limitnumber", limitnumber);
		map.put("sortby", sortby);
		map.put("sortdirection", sortdirection);

		Util.putIfNotNull(parameters, "options", "name", "value", map);
		
	}

	/**
	 * Devuelve un Builder para añadir parámetros adicionales a la función de
	 * Moodle.
	 * 
	 * @param courseid
	 *            id del curso
	 * @return Builder
	 */
	public static Builder newBuilder(int courseid) {
		return new Builder(courseid);
	}

	/**
	 * 
	 * Constructor builder para añadir parámetros adicionales a la función de
	 * Moodle.
	 * 
	 * @author Yi Peng Ji
	 *
	 */
	public static class Builder {

		private Integer courseid;
		private String withcapability;
		private Integer groupid;
		private Integer onlyactive;
		private Set<String> userfields;
		private Integer limitfrom;
		private Integer limitnumber;
		private String sortby;
		private String sortdirection;

		public Builder(int courseid) {
			this.courseid = courseid;
		}

		public Builder setGroupid(int groupid) {
			this.groupid = groupid;
			return this;
		}

		public Builder setWithcapability(String withcapability) {
			this.withcapability = withcapability;
			return this;
		}

		public Builder setOnlyactive(int onlyactive) {

			this.onlyactive = onlyactive;
			return this;
		}

		public Builder setUserfields(Set<String> userfields) {
			this.userfields = userfields;
			return this;
		}

		public Builder addUserfield(String userfield) {
			if (userfield == null)
				userfields = new HashSet<>();
			userfields.add(userfield);
			return this;
		}

		public Builder setLimitfrom(int limitfrom) {
			this.limitfrom = limitfrom;
			return this;
		}

		public Builder setLimitnumber(int limitnumber) {
			this.limitnumber = limitnumber;
			return this;
		}

		public Builder setSortbyId() {
			this.sortby = "id";
			return this;
		}

		public Builder setSortbyFirstname() {
			this.sortby = "firstname";
			return this;
		}

		public Builder setSortbyLastname() {
			this.sortby = "lastname";
			return this;
		}

		public Builder setSortbySiteorder() {
			this.sortby = "siteorder";
			return this;
		}

		public Builder setSortdirectionASC() {
			this.sortdirection = "ASC";
			return this;
		}

		public Builder setSortdirectionDESC() {
			this.sortdirection = "DESC";
			return this;
		}

		public CoreEnrolGetEnrolledUsers build() {

			return new CoreEnrolGetEnrolledUsers(this);

		}

	}
}
