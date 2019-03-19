package webservice.core;

import java.util.HashSet;
import java.util.Set;

import webservice.ParametersCourseid;
import webservice.WSFunctions;

public class CoreEnrolGetEnrolledUsers extends ParametersCourseid {

	/**
	 * return only users with this capability. This option requires
	 * 'moodle/role:review' on the course context.
	 */
	private String withcapability;

	/**
	 * return only users in this group id. If the course has groups enabled and this
	 * param isn't defined, returns all the viewable users. This option requires
	 * 'moodle/site:accessallgroups' on the course context if the user doesn't
	 * belong to the group.
	 */
	private int groupid;

	/**
	 * return only users with active enrolments and matching time restrictions. This
	 * option requires 'moodle/course:enrolreview' on the course context.
	 */
	private int onlyactive;

	/**
	 * return only the values of these user fields.
	 */
	private Set<String> userfields;

	/**
	 * sql limit from.
	 */
	private int limitfrom;

	/**
	 * maximum number of returned users.
	 */
	private int limitnumber;

	/**
	 * sort by id, firstname or lastname. For ordering like the site does, use
	 * siteorder.
	 */
	private String sortby;

	/**
	 * ASC or DESC
	 */
	private String sortdirection;

	/**
	 * contador de indice de las opciones
	 */
	private int index;

	public CoreEnrolGetEnrolledUsers(int courseid) {
		super(courseid);
	}

	private CoreEnrolGetEnrolledUsers(Builder builder) {
		super(builder.courseid);
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
	public void appendToUrlParameters() {
		super.appendToUrlParameters();
		appendIfNotNull("withcapability", withcapability);
		appendIfNotZero("groupid", groupid);
		appendIfNotZero("onlyactive", onlyactive);
		appendIfNotNull("userfields", userfields);
		appendIfNotZero("limitfrom", limitfrom);
		appendIfNotZero("limitnumber", limitnumber);
		appendIfNotNull("sortby", sortby);
		appendIfNotNull("sortdirection", sortdirection);

	}

	private void appendIfNotNull(String name, String value) {
		if (value != null)
			appendToUrlOptions(index++, name, value);
	}

	private void appendIfNotZero(String name, int value) {
		if (value != 0)
			appendToUrlOptions(index++, name, value);
	}

	private void appendIfNotNull(String name, Set<String> value) {
		if (value != null)
			appendToUrlOptions(index++, name, String.join(",", value));
	}

	@Override
	public WSFunctions getWSFunction() {
		return WSFunctions.CORE_ENROL_GET_ENROLLED_USERS;
	}

	public String getWithcapability() {
		return withcapability;
	}

	public void setWithcapability(String withcapability) {
		this.withcapability = withcapability;
	}

	public int getGroupid() {
		return groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	public int getOnlyactive() {
		return onlyactive;
	}

	public void setOnlyactive(int onlyactive) {
		this.onlyactive = onlyactive;
	}

	public Set<String> getUserfields() {
		return userfields;
	}

	public void setUserfields(Set<String> userfields) {
		this.userfields = userfields;
	}

	public int getLimitfrom() {
		return limitfrom;
	}

	public void setLimitfrom(int limitfrom) {
		this.limitfrom = limitfrom;
	}

	public int getLimitnumber() {
		return limitnumber;
	}

	public void setLimitnumber(int limitnumber) {
		this.limitnumber = limitnumber;
	}

	public String getSortby() {
		return sortby;
	}

	public void setSortby(String sortby) {
		this.sortby = sortby;
	}

	public String getSortdirection() {
		return sortdirection;
	}

	public void setSortdirection(String sortdirection) {
		this.sortdirection = sortdirection;
	}

	public static Builder newBuilder(int courseid) {
		return new Builder(courseid);
	}

	public static class Builder {

		private int courseid;
		private String withcapability;
		private int groupid;
		private int onlyactive;
		private Set<String> userfields;
		private int limitfrom;
		private int limitnumber;
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
				userfields = new HashSet<String>();
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
