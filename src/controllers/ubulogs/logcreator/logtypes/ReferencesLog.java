package controllers.ubulogs.logcreator.logtypes;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import controllers.ubulogs.logcreator.CompEventKey;
import controllers.ubulogs.logcreator.Component;
import controllers.ubulogs.logcreator.Event;
import controllers.ubulogs.logcreator.LogCreator;
import model.EnrolledUser;
import model.LogLine;
import model.mod.Module;

public abstract class ReferencesLog {

	static final Logger logger = LoggerFactory.getLogger(ReferencesLog.class);

	private static final Controller CONTROLLER=Controller.getInstance();
	
	private static final Set<String> NOT_AVAIBLE_COMPONENTS = new TreeSet<>();
	private static final Set<String> NOT_AVAIBLE_EVENTS = new TreeSet<>();
	public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/MM/yy, kk:mm");

	private static Map<CompEventKey, ReferencesLog> logTypes = new HashMap<CompEventKey, ReferencesLog>();
	static {
		logTypes.put(new CompEventKey(Component.ACTIVITY_REPORT, Event.ACTIVITY_REPORT_VIEWED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.A_SUBMISSION_HAS_BEEN_SUBMITTED), UserSubmissionCmid.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.ALL_THE_SUBMISSIONS_ARE_BEING_DOWNLOADED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.ASSIGNMENT_OVERRIDE_CREATED), UserOverrideCmidAffected.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.ASSIGNMENT_OVERRIDE_DELETED), UserOverrideCmidAffected.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.GRADING_FORM_VIEWED), UserAffectedCmid.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.GRADING_TABLE_VIEWED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.SUBMISSION_CONFIRMATION_FORM_VIEWED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.SUBMISSION_FORM_VIEWED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.THE_STATUS_OF_THE_SUBMISSION_HAS_BEEN_UPDATED), UserSubmissionCmid.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.THE_STATUS_OF_THE_SUBMISSION_HAS_BEEN_VIEWED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.THE_SUBMISSION_HAS_BEEN_GRADED), UserSubmissionAffectedCmid.getInstance());
		logTypes.put(new CompEventKey(Component.ASSIGNMENT, Event.THE_USER_HAS_ACCEPTED_THE_STATEMENT_OF_THE_SUBMISSION), UserSubmissionCmid.getInstance());

		logTypes.put(new CompEventKey(Component.CHOICE, Event.CHOICE_ANSWER_ADDED), UserOptionAffectedCmid.getInstance());
		logTypes.put(new CompEventKey(Component.CHOICE, Event.CHOICE_REPORT_VIEWED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.CHOICE, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.CHOICE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

		logTypes.put(new CompEventKey(Component.EXCEL_SPREADSHEET, Event.XLS_GRADE_EXPORTED), User.getInstance());

		logTypes.put(new CompEventKey(Component.FEEDBACK, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());

		logTypes.put(new CompEventKey(Component.FILE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

		logTypes.put(new CompEventKey(Component.FILE_SUBMISSIONS, Event.A_FILE_HAS_BEEN_UPLOADED), UserSubmissionCmid.getInstance());
		logTypes.put(new CompEventKey(Component.FILE_SUBMISSIONS, Event.SUBMISSION_CREATED), UserFilesCmid.getInstance());
		logTypes.put(new CompEventKey(Component.FILE_SUBMISSIONS, Event.SUBMISSION_UPDATED), UserFilesCmid.getInstance());

		logTypes.put(new CompEventKey(Component.FOLDER, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.FOLDER, Event.FOLDER_UPDATED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.FOLDER, Event.ZIP_ARCHIVE_OF_FOLDER_DOWNLOADED), UserCmid.getInstance());

		logTypes.put(new CompEventKey(Component.FORUM, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.FORUM, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.FORUM, Event.DISCUSSION_CREATED), UserDiscussionCmid.getInstance());
		logTypes.put(new CompEventKey(Component.FORUM, Event.DISCUSSION_SUBSCRIPTION_CREATED), UserDiscussionCmid.getInstance());
		logTypes.put(new CompEventKey(Component.FORUM, Event.DISCUSSION_VIEWED), UserDiscussionCmid.getInstance());
		logTypes.put(new CompEventKey(Component.FORUM, Event.POST_CREATED), UserPostDiscussionCmid.getInstance());
		logTypes.put(new CompEventKey(Component.FORUM, Event.POST_UPDATED), UserPostDiscussionCmid.getInstance());
		logTypes.put(new CompEventKey(Component.FORUM, Event.SOME_CONTENT_HAS_BEEN_POSTED), UserPostDiscussionCmid.getInstance());

		logTypes.put(new CompEventKey(Component.GRADER_REPORT, Event.GRADER_REPORT_VIEWED), User.getInstance());

		logTypes.put(new CompEventKey(Component.LIVE_LOGS, Event.LIVE_LOG_REPORT_VIEWED), UserCourse.getInstance());

		logTypes.put(new CompEventKey(Component.LOGS, Event.LOG_REPORT_VIEWED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.LOGS, Event.USER_LOG_REPORT_VIEWED), UserAffected.getInstance());
		
		logTypes.put(new CompEventKey(Component.OPENDOCUMENT_SPREADSHEET, Event.OPENDOCUMENT_GRADE_EXPORTED), User.getInstance());

		logTypes.put(new CompEventKey(Component.OUTCOMES_REPORT, Event.GRADE_OUTCOMES_REPORT_VIEWED), User.getInstance());
		
		logTypes.put(new CompEventKey(Component.OVERVIEW_REPORT, Event.GRADE_OVERVIEW_REPORT_VIEWED), User.getInstance());

		logTypes.put(new CompEventKey(Component.PAGE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

		logTypes.put(new CompEventKey(Component.PLAIN_TEXT_FILE, Event.TXT_GRADE_EXPORTED), User.getInstance());

		logTypes.put(new CompEventKey(Component.QUIZ, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.QUIZ, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.QUIZ, Event.QUIZ_ATTEMPT_ABANDONED), UserAttemptCmid.getInstance());
		logTypes.put(new CompEventKey(Component.QUIZ, Event.QUIZ_ATTEMPT_REVIEWED), UserAttemptAffectedCmid.getInstance());
		logTypes.put(new CompEventKey(Component.QUIZ, Event.QUIZ_ATTEMPT_STARTED), UserAttemptCmid.getInstance());
		logTypes.put(new CompEventKey(Component.QUIZ, Event.QUIZ_ATTEMPT_SUBMITTED), UserAttemptCmid.getInstance());
		logTypes.put(new CompEventKey(Component.QUIZ, Event.QUIZ_ATTEMPT_SUMMARY_VIEWED), UserAttemptAffectedCmid.getInstance());
		logTypes.put(new CompEventKey(Component.QUIZ, Event.QUIZ_ATTEMPT_VIEWED), UserAttemptAffectedCmid.getInstance());
		logTypes.put(new CompEventKey(Component.QUIZ, Event.QUIZ_EDIT_PAGE_VIEWED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.QUIZ, Event.QUIZ_REPORT_VIEWED), UserCmid.getInstance());

		logTypes.put(new CompEventKey(Component.RECYCLE_BIN, Event.ITEM_CREATED), Item.getInstance());
		logTypes.put(new CompEventKey(Component.RECYCLE_BIN, Event.ITEM_DELETED), Item.getInstance());

		logTypes.put(new CompEventKey(Component.SINGLE_VIEW, Event.GRADE_SINGLE_VIEW_REPORT_VIEWED), User.getInstance());

		logTypes.put(new CompEventKey(Component.SUBMISSION_COMMENTS, Event.COMMENT_CREATED), UserCommentSubmissionCmid.getInstance());
		logTypes.put(new CompEventKey(Component.SUBMISSION_COMMENTS, Event.COMMENT_DELETED), UserCommentSubmissionCmid.getInstance());

		logTypes.put(new CompEventKey(Component.SYSTEM, Event.BADGE_LISTING_VIEWED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.CALENDAR_EVENT_CREATED), UserCalendar.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.CALENDAR_EVENT_DELETED), UserCalendar.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.CALENDAR_EVENT_UPDATED), UserCalendar.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_ACTIVITY_COMPLETION_UPDATED), UserCmidAffected.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_CREATED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_MODULE_CREATED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_MODULE_DELETED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_MODULE_UPDATED), UserCmid.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_RESET_ENDED), controllers.ubulogs.logcreator.logtypes.Course.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_RESET_STARTED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_SECTION_CREATED), UserSectionCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_SECTION_UPDATED), UserSectionCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_SUMMARY_VIEWED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_UPDATED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_USER_REPORT_VIEWED), UserCourseAffected.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.COURSE_VIEWED), SystemCourseViewed.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.ENROLMENT_INSTANCE_CREATED), UserAffected.getInstance()); // TODO comprobar
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.GRADE_DELETED), UserGradeAffectedGradeitem.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.GROUP_CREATED), UserGroup.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.GROUP_DELETED), UserGroup.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.GROUP_MEMBER_ADDED), UserAffectedGroup.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.GROUP_MEMBER_REMOVED), UserAffectedGroup.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.GROUPING_CREATED), UserGrouping.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.RECENT_ACTIVITY_VIEWED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.ROLE_ASSIGNED), UserRoleAffected.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.ROLE_UNASSIGNED), UserRoleAffected.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.SCALE_CREATED), UserScaleCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.USER_ENROLLED_IN_COURSE), UserAffectedCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.USER_GRADED), UserGradeAffectedGradeitem.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.USER_LIST_VIEWED), UserCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.USER_PROFILE_VIEWED), UserAffectedCourse.getInstance());
		logTypes.put(new CompEventKey(Component.SYSTEM, Event.USER_UNENROLLED_FROM_COURSE), UserAffectedCourse.getInstance());

		logTypes.put(new CompEventKey(Component.USER_REPORT, Event.GRADE_USER_REPORT_VIEWED), User.getInstance());

		logTypes.put(new CompEventKey(Component.WORKSHOP, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
		
		logTypes.put(new CompEventKey(Component.XML_FILE, Event.XML_GRADE_EXPORTED), User.getInstance());
	}

	public static Set<String> getNotAvaibleComponents() {
		return NOT_AVAIBLE_COMPONENTS;
	}

	public static Set<String> getNotAvaibleEvents() {
		return NOT_AVAIBLE_EVENTS;
	}

	public static ReferencesLog getReferenceLog(Component component, Event eventName) {
		return logTypes.getOrDefault(new CompEventKey(component, eventName), Default.getInstance());
	}


	public static void setZoneId(ZoneId zoneId) {
		ReferencesLog.dateTimeFormatter = dateTimeFormatter.withZone(zoneId);
	}

	public static LogLine createLogWithBasicAttributes(Map<String, String> mapLog) {

		LogLine log = new LogLine();
		String time = mapLog.get(LogCreator.TIME);
		ZonedDateTime zdt = ZonedDateTime.parse(time, dateTimeFormatter);
		log.setTime(zdt);

		Component component = Component.get(mapLog.get(LogCreator.COMPONENT));
		if (component == null) {
			NOT_AVAIBLE_COMPONENTS.add(mapLog.get(LogCreator.COMPONENT));

		}

		Event event = Event.get(mapLog.get(LogCreator.EVENT_NAME));
		if (event == null) {
			NOT_AVAIBLE_EVENTS.add(mapLog.get(LogCreator.EVENT_NAME));

		}

		log.setComponent(component);
		log.setEventName(event);
		log.setEventContext(mapLog.get(LogCreator.EVENT_CONTEXT));
		log.setDescription(mapLog.get(LogCreator.DESCRIPTION));
		log.setOrigin(mapLog.get(LogCreator.ORIGIN));
		log.setIPAdress(mapLog.get(LogCreator.IP_ADRESS));

		return log;
	}

	public static void setUserById(LogLine log, int id) {
		EnrolledUser user = CONTROLLER.getBBDD().getEnrolledUserById(id);
		if (user != null) {
			log.setUser(user);
		}
	}

	public static void setAffectedUserById(LogLine log, int id) {
		EnrolledUser affectedUser = CONTROLLER.getBBDD().getEnrolledUserById(id);
		if (affectedUser != null) {

			log.setAffectedUser(affectedUser);
		}

	}

	public static void setCourseModuleById(LogLine log, int id) {
		Module courseModule = CONTROLLER.getBBDD().getCourseModuleById(id);
		if (courseModule != null) {

			log.setCourseModule(courseModule);
		}

	}

	public abstract void setLogReferencesAttributes(LogLine log, List<Integer> ids);
}
