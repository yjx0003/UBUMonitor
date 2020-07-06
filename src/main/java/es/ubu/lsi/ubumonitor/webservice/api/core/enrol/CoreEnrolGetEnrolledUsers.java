package es.ubu.lsi.ubumonitor.webservice.api.core.enrol;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * Get enrolled users by course id.
 * 
 * @author Yi Peng Ji
 *
 */
public class CoreEnrolGetEnrolledUsers extends WSFunctionAbstract {

	private JSONArray options;

	public CoreEnrolGetEnrolledUsers(int courseid) {
		super(WSFunctionEnum.CORE_ENROL_GET_ENROLLED_USERS);
		setCourseid(courseid);
		options = new JSONArray();
	}

	/**
	 * Course id (required).
	 * 
	 * @param courseid course id
	 */
	public void setCourseid(int courseid) {
		parameters.put("courseid", courseid);

	}

	private void append(String name, Object value) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", name);
		jsonObject.put("value", value);
		options.put(jsonObject);
	}

	/**
	 * Return only users with this capability. This option requires
	 * 'moodle/role:review' on the course context.
	 * 
	 * @param withcapability capability
	 */
	public void appendWithcapability(String withcapability) {
		append("withcapability", withcapability);

	}

	/**
	 * Return only users in this group id. If the course has groups enabled and this
	 * param isn't defined, returns all the viewable users. This option requires
	 * 'moodle/site:accessallgroups' on the course context if the user doesn't
	 * belong to the group.
	 * 
	 * @param groupid group id
	 */
	public void appendGroupid(int groupid) {
		append("groupid", groupid);
	}

	/**
	 * Return only users with active enrolments and matching time restrictions. This
	 * option requires 'moodle/course:enrolreview' on the course context.
	 * 
	 * @param onlyactive only active
	 */
	public void appendOnlyactive(boolean onlyactive) {
		append("onlyactive", onlyactive ? 1 : 0);
	}

	/**
	 * Return only the values of these user fields.
	 * 
	 * @param userfields user fields
	 */
	public void appendUserfields(Collection<String> userfields) {
		if (userfields != null && !userfields.isEmpty()) {
			append("userfields", userfields);
		}

	}

	/**
	 * SQL limit from.
	 * 
	 * @param limitfrom SQL limit from
	 */
	public void appendLimitfrom(int limitfrom) {
		append("limitfrom", limitfrom);
	}

	/**
	 * Maximum number of returned users.
	 * 
	 * @param limitnumber Maximum number of returned users
	 */
	public void appendLimitnumber(int limitnumber) {
		append("limitnumber", limitnumber);
	}

	/**
	 * Sort by id, firstname or lastname. For ordering like the site does, use
	 * siteorder.
	 * 
	 * @param sortby id, firstname, lastname or siteorder
	 */
	public void appendSortby(String sortby) {
		append("sortby", sortby);
	}

	/**
	 * Sort direction (ASC or DESC)
	 * 
	 * @param sortdirection ASC or DESC
	 */
	public void appendSortdirection(String sortdirection) {
		append("sortdirection", sortdirection);
	}

}
