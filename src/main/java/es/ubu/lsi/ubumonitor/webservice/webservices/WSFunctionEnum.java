package es.ubu.lsi.ubumonitor.webservice.webservices;

import java.util.HashMap;
import java.util.Map;

public enum WSFunctionEnum {

	/**
	 * Fetch the day view data for a calendar
	 */
	CORE_CALENDAR_GET_CALENDAR_DAY_VIEW("core_calendar_get_calendar_day_view", 3.4),

	/**
	 * Get calendar events.
	 */
	CORE_CALENDAR_GET_CALENDAR_EVENTS("core_calendar_get_calendar_events", 2.5),

	/**
	 * Fetch the monthly view data for a calendar
	 */
	CORE_CALENDAR_GET_CALENDAR_MONTHLY_VIEW("core_calendar_get_calendar_monthly_view", 3.4),

	/**
	 * Fetch the upcoming view data for a calendar
	 */
	CORE_CALENDAR_GET_CALENDAR_UPCOMING_VIEW("core_calendar_get_calendar_upcoming_view", 3.4),

	/**
	 * Return the activities completion status for a user in a course.
	 */
	CORE_COMPLETION_GET_ACTIVITIES_COMPLETION_STATUS("core_completion_get_activities_completion_status", 2.9),

	/**
	 * Get categories.
	 */
	CORE_COURSE_GET_CATEGORIES("core_course_get_categories", 2.3),

	/**
	 * Get course content (modules + web service file urls).
	 */
	CORE_COURSE_GET_CONTENTS("core_course_get_contents", 2.2),

	/**
	 * Get courses matching a specific field (id/s, shortname, idnumber, category)
	 */
	CORE_COURSE_GET_COURSES_BY_FIELD("core_course_get_courses_by_field", 3.2),

	/**
	 * Get enrolled users by course id.
	 */
	CORE_ENROL_GET_ENROLLED_USERS("core_enrol_get_enrolled_users", 2.1),

	/**
	 * List of enrolled courses for the given timeline classification (past,
	 * inprogress, or future).
	 */
	CORE_COURSE_GET_ENROLLED_COURSES_BY_TIMELINE_CLASSIFICATION(
			"core_course_get_enrolled_courses_by_timeline_classification", 3.6),

	/**
	 * List of courses a user has accessed most recently.
	 */
	CORE_COURSE_GET_RECENT_COURSES("core_course_get_recent_courses", 3.6),

	/**
	 * Return a list of administration options in a set of courses that are
	 * avaialable or not for the current user.
	 */
	CORE_COURSE_GET_USER_ADMINISTRATION_OPTIONS("core_course_get_user_administration_options", 3.2),

	/**
	 * Return a list of navigation options in a set of courses that are avaialable
	 * or not for the current user.
	 */
	CORE_COURSE_GET_USER_NAVIGATION_OPTIONS("core_course_get_user_navigation_options", 3.2),

	/**
	 * Search courses by (name, module, block, tag).
	 */
	CORE_COURSE_SEARCH_COURSES("core_course_search_courses", 3.0),

	/**
	 * Get list of course ids that a user is enrolled in (if you are allowed to see
	 * that).
	 */
	CORE_ENROL_GET_USERS_COURSES("core_enrol_get_users_courses", 2.0),

	/**
	 * Retrieve users information for a specified unique field - If you want to do a
	 * user search, use core_user_get_users().
	 */
	CORE_USER_GET_USERS_BY_FIELD("core_user_get_users_by_field", 2.5),

	/**
	 * Return some site info / user info / list web service functions
	 */
	CORE_WEBSERVICE_GET_SITE_INFO("core_webservice_get_site_info", 2.1),

	/**
	 * Returns the complete list of grade items for users in a course.
	 */
	GRADEREPORT_USER_GET_GRADE_ITEMS("gradereport_user_get_grade_items", 3.2),

	/**
	 * Get the user/s report grades table for a course.
	 */
	GRADEREPORT_USER_GET_GRADES_TABLE("gradereport_user_get_grades_table", 2.9),

	/**
	 * Returns the courses and assignments for the users capability.
	 */
	MOD_ASSIGN_GET_ASSIGNMENTS("mod_assign_get_assignments", 2.4),

	/**
	 * Return information of a given lesson.
	 */
	MOD_LESSON_GET_LESSONS_BY_COURSES("mod_lesson_get_lessons_by_courses", 3.3),

	/**
	 * Returns a list of quizzes in a provided list of courses, if no list is
	 * provided all quizzes that the user can view will be returned.
	 */
	MOD_QUIZ_GET_QUIZZES_BY_COURSES("mod_quiz_get_quizzes_by_courses", 3.1),

	/**
	 * Creates an auto-login key for the current user. Is created only in https
	 * sites and is restricted by time and ip address.
	 */
	TOOL_MOBILE_GET_AUTOLOGIN_KEY("tool_mobile_get_autologin_key", 3.2),
	/**
	 * Returns a list of the site public settings, those not requiring
	 * authentication.
	 */
	TOOL_MOBILE_GET_PUBLIC_CONFIG("tool_mobile_get_public_config", 3.2),

	/**
	 * Return the user token.
	 */
	MOODLE_MOBILE_APP("moodle_mobile_app", 2.0);

	private static Map<String, WSFunctionEnum> map = new HashMap<>();

	static {
		for (WSFunctionEnum value : WSFunctionEnum.values()) {
			map.put(value.name, value);
		}
	}

	private String name;
	private double since;

	private WSFunctionEnum(String name, double since) {
		this.name = name;
		this.since = since;
	}

	public WSFunctionEnum get(String name) {
		if (map.containsKey(name))
			return map.get(name);
		throw new IllegalArgumentException(name + " web service function not avaible.");
	}

	public double getSince() {
		return since;
	}

	@Override
	public String toString() {
		return name;
	}
}
