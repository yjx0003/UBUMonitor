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
import controllers.ubulogs.logcreator.ComponentEvent;
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

	private static Map<ComponentEvent, ReferencesLog> logTypes = new HashMap<ComponentEvent, ReferencesLog>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{

			put(ComponentEvent.getInstance(Component.ACTIVITY_REPORT, Event.ACTIVITY_REPORT_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.ACTIVITY_REPORT, Event.OUTLINE_REPORT_VIEWED), UserAffectedCourse.getInstance());

			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.A_SUBMISSION_HAS_BEEN_SUBMITTED), UserSubmissionCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.ALL_THE_SUBMISSIONS_ARE_BEING_DOWNLOADED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.ASSIGNMENT_OVERRIDE_CREATED), UserOverrideCmidAffected.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.ASSIGNMENT_OVERRIDE_DELETED), UserOverrideCmidAffected.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.ASSIGNMENT_OVERRIDE_UPDATED), UserOverrideCmidAffected.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.GRADING_FORM_VIEWED), UserAffectedCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.GRADING_TABLE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.SUBMISSION_CONFIRMATION_FORM_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.SUBMISSION_FORM_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.THE_STATUS_OF_THE_SUBMISSION_HAS_BEEN_UPDATED), UserSubmissionCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.THE_STATUS_OF_THE_SUBMISSION_HAS_BEEN_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.THE_SUBMISSION_HAS_BEEN_GRADED), UserSubmissionAffectedCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.THE_SUBMISSIONS_HAVE_BEEN_LOCKED_FOR_A_USER), UserAffectedCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.THE_SUBMISSIONS_HAVE_BEEN_UNLOCKED_FOR_A_USER),UserAffectedCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ASSIGNMENT, Event.THE_USER_HAS_ACCEPTED_THE_STATEMENT_OF_THE_SUBMISSION), UserSubmissionCmid.getInstance());

			put(ComponentEvent.getInstance(Component.CHAT, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.CHAT, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.CHAT, Event.MESSAGE_SENT),  UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.CHAT, Event.SESSIONS_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.getInstance(Component.CHOICE, Event.CHOICE_ANSWER_ADDED), UserOptionAffectedCmid.getInstance());
			put(ComponentEvent.getInstance(Component.CHOICE, Event.CHOICE_REPORT_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.CHOICE, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.CHOICE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.getInstance(Component.COURSE_PARTICIPATION, Event.PARTICIPATION_REPORT_VIEWED), UserCourse.getInstance());

			put(ComponentEvent.getInstance(Component.EXCEL_SPREADSHEET, Event.XLS_GRADE_EXPORTED), User.getInstance());

			put(ComponentEvent.getInstance(Component.FEEDBACK, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.FEEDBACK, Event.COURSE_MODULE_VIEWED), Default.getInstance());
			put(ComponentEvent.getInstance(Component.FEEDBACK, Event.RESPONSE_SUBMITTED), UserCmid.getInstance()); 

			put(ComponentEvent.getInstance(Component.FILE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.getInstance(Component.FILE_SUBMISSIONS, Event.A_FILE_HAS_BEEN_UPLOADED), UserSubmissionCmid.getInstance());
			put(ComponentEvent.getInstance(Component.FILE_SUBMISSIONS, Event.SUBMISSION_CREATED), UserFilesCmid.getInstance());
			put(ComponentEvent.getInstance(Component.FILE_SUBMISSIONS, Event.SUBMISSION_UPDATED), UserFilesCmid.getInstance());

			put(ComponentEvent.getInstance(Component.FOLDER, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.FOLDER, Event.FOLDER_UPDATED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.FOLDER, Event.ZIP_ARCHIVE_OF_FOLDER_DOWNLOADED), UserCmid.getInstance());

			put(ComponentEvent.getInstance(Component.FORUM, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.FORUM, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.FORUM, Event.COURSE_SEARCHED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.FORUM, Event.DISCUSSION_CREATED), UserDiscussionCmid.getInstance());
			put(ComponentEvent.getInstance(Component.FORUM, Event.DISCUSSION_DELETED), UserDiscussionCmid.getInstance());
			put(ComponentEvent.getInstance(Component.FORUM, Event.DISCUSSION_MOVED), UserDiscussionCmid.getInstance()); 
			put(ComponentEvent.getInstance(Component.FORUM, Event.DISCUSSION_SUBSCRIPTION_CREATED), UserDiscussionCmid.getInstance());
			put(ComponentEvent.getInstance(Component.FORUM, Event.DISCUSSION_VIEWED), UserDiscussionCmid.getInstance());
			put(ComponentEvent.getInstance(Component.FORUM, Event.POST_CREATED), UserPostDiscussionCmid.getInstance());
			put(ComponentEvent.getInstance(Component.FORUM, Event.POST_DELETED), UserPostDiscussionCmid.getInstance()); 
			put(ComponentEvent.getInstance(Component.FORUM, Event.POST_UPDATED), UserPostDiscussionCmid.getInstance());
			put(ComponentEvent.getInstance(Component.FORUM, Event.SOME_CONTENT_HAS_BEEN_POSTED), UserPostDiscussionCmid.getInstance());
			put(ComponentEvent.getInstance(Component.FORUM, Event.USER_REPORT_VIEWED), UserAffectedCourse.getInstance());

			put(ComponentEvent.getInstance(Component.GLOSSARY, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.GLOSSARY, Event.ENTRY_HAS_BEEN_CREATED), UserGlossaryCmid.getInstance());
			put(ComponentEvent.getInstance(Component.GLOSSARY, Event.ENTRY_HAS_BEEN_UPDATED), UserGlossaryCmid.getInstance());
			put(ComponentEvent.getInstance(Component.GLOSSARY, Event.ENTRY_HAS_BEEN_VIEWED), UserGlossaryCmid.getInstance());

			put(ComponentEvent.getInstance(Component.GRADER_REPORT, Event.GRADER_REPORT_VIEWED), User.getInstance());

			put(ComponentEvent.getInstance(Component.GUIA_DOCENTE, Event.EVALUATION_SYSTEM_CREATED), Ignore.getInstance());
			put(ComponentEvent.getInstance(Component.GUIA_DOCENTE, Event.EVALUATION_SYSTEM_MODIFIED), Ignore.getInstance()); 
			put(ComponentEvent.getInstance(Component.GUIA_DOCENTE, Event.OUTSTANDING_RATINGS_MODIFIED), Ignore.getInstance()); 
			put(ComponentEvent.getInstance(Component.GUIA_DOCENTE, Event.SUBJECT_MODIFIED), Ignore.getInstance()); 
			put(ComponentEvent.getInstance(Component.GUIA_DOCENTE, Event.UNIT_MODIFIED), Ignore.getInstance()); 

			put(ComponentEvent.getInstance(Component.HOTPOT_MODULE, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.HOTPOT_MODULE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.HOTPOT_MODULE, Event.HOTPOT_ATTEMPT_STARTED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.HOTPOT_MODULE, Event.HOTPOT_ATTEMPT_SUBMITTED), UserCmid.getInstance());

			put(ComponentEvent.getInstance(Component.LIVE_LOGS, Event.LIVE_LOG_REPORT_VIEWED), UserCourse.getInstance());

			put(ComponentEvent.getInstance(Component.LOGS, Event.LOG_REPORT_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.LOGS, Event.USER_LOG_REPORT_VIEWED), UserAffected.getInstance());

			put(ComponentEvent.getInstance(Component.ONLINE_TEXT_SUBMISSIONS, Event.AN_ONLINE_TEXT_HAS_BEEN_UPLOADED), UserSubmissionCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ONLINE_TEXT_SUBMISSIONS, Event.SUBMISSION_CREATED), UserWordsCmid.getInstance());
			put(ComponentEvent.getInstance(Component.ONLINE_TEXT_SUBMISSIONS, Event.SUBMISSION_UPDATED), Default.getInstance());

			put(ComponentEvent.getInstance(Component.OPENDOCUMENT_SPREADSHEET, Event.OPENDOCUMENT_GRADE_EXPORTED), User.getInstance());

			put(ComponentEvent.getInstance(Component.OUTCOMES_REPORT, Event.GRADE_OUTCOMES_REPORT_VIEWED), User.getInstance());

			put(ComponentEvent.getInstance(Component.OVERVIEW_REPORT, Event.GRADE_OVERVIEW_REPORT_VIEWED), User.getInstance());

			put(ComponentEvent.getInstance(Component.PAGE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.getInstance(Component.PLAIN_TEXT_FILE, Event.TXT_GRADE_EXPORTED), User.getInstance());

			put(ComponentEvent.getInstance(Component.QUIZ, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUESTION_MANUALLY_GRADED), UserQuestionAttemptCmid.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUIZ_ATTEMPT_ABANDONED), UserAttemptCmid.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUIZ_ATTEMPT_PREVIEW_STARTED), Default.getInstance()); //TODO
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUIZ_ATTEMPT_REVIEWED), UserAttemptAffectedCmid.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUIZ_ATTEMPT_STARTED), UserAttemptCmid.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUIZ_ATTEMPT_SUBMITTED), UserAttemptCmid.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUIZ_ATTEMPT_SUMMARY_VIEWED), UserAttemptAffectedCmid.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUIZ_ATTEMPT_TIME_LIMIT_EXCEEDED), AttemptCmidUser.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUIZ_ATTEMPT_VIEWED), UserAttemptAffectedCmid.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUIZ_EDIT_PAGE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUIZ_OVERRIDE_CREATED), UserOverrideCmidGroup.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUIZ_OVERRIDE_DELETED), UserOverrideCmidGroup.getInstance());
			put(ComponentEvent.getInstance(Component.QUIZ, Event.QUIZ_REPORT_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.getInstance(Component.RECYCLE_BIN, Event.ITEM_CREATED), Item.getInstance());
			put(ComponentEvent.getInstance(Component.RECYCLE_BIN, Event.ITEM_DELETED), Item.getInstance());

			put(ComponentEvent.getInstance(Component.SINGLE_VIEW, Event.GRADE_SINGLE_VIEW_REPORT_VIEWED), User.getInstance());

			put(ComponentEvent.getInstance(Component.SUBMISSION_COMMENTS, Event.COMMENT_CREATED), UserCommentSubmissionCmid.getInstance());
			put(ComponentEvent.getInstance(Component.SUBMISSION_COMMENTS, Event.COMMENT_DELETED), UserCommentSubmissionCmid.getInstance());

			put(ComponentEvent.getInstance(Component.SYSTEM, Event.BADGE_LISTING_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.CALENDAR_EVENT_CREATED), UserCalendar.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.CALENDAR_EVENT_DELETED), UserCalendar.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.CALENDAR_EVENT_UPDATED), UserCalendar.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_ACTIVITY_COMPLETION_UPDATED), UserCmidAffected.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_BACKUP_CREATED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_CREATED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_MODULE_CREATED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_MODULE_DELETED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_MODULE_UPDATED), UserCmid.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_RESET_ENDED), Course.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_RESET_STARTED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_SECTION_CREATED), UserSectionCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_SECTION_UPDATED), UserSectionCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_SUMMARY_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_UPDATED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_USER_REPORT_VIEWED), UserCourseAffected.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.COURSE_VIEWED), SystemCourseViewed.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.ENROLMENT_INSTANCE_CREATED), UserAffected.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.GRADE_DELETED), UserGradeAffectedGradeitem.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.GROUP_CREATED), UserGroup.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.GROUP_DELETED), UserGroup.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.GROUP_MEMBER_ADDED), UserAffectedGroup.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.GROUP_MEMBER_REMOVED), UserAffectedGroup.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.GROUPING_CREATED), UserGrouping.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.QUESTION_CATEGORY_CREATED), UserQuestioncategory.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.RECENT_ACTIVITY_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.ROLE_ASSIGNED), UserRoleAffected.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.ROLE_UNASSIGNED), UserRoleAffected.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.SCALE_CREATED), UserScaleCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.USER_ENROLLED_IN_COURSE), UserAffectedCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.USER_GRADED), UserGradeAffectedGradeitem.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.USER_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.USER_PROFILE_VIEWED), UserAffectedCourse.getInstance());
			put(ComponentEvent.getInstance(Component.SYSTEM, Event.USER_UNENROLLED_FROM_COURSE), UserAffectedCourse.getInstance());

			put(ComponentEvent.getInstance(Component.TURNITIN_ASSIGNMENT_2, Event.ADD_SUBMISSION), Default.getInstance()); //TODO
			put(ComponentEvent.getInstance(Component.TURNITIN_ASSIGNMENT_2, Event.LIST_SUBMISSIONS), Default.getInstance()); //TODO

			put(ComponentEvent.getInstance(Component.URL, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.getInstance(Component.USER_REPORT, Event.GRADE_USER_REPORT_VIEWED), User.getInstance());

			put(ComponentEvent.getInstance(Component.USER_TOURS, Event.STEP_SHOWN), UserTour.getInstance()); 
			put(ComponentEvent.getInstance(Component.USER_TOURS, Event.TOUR_ENDED), UserTour.getInstance());
			put(ComponentEvent.getInstance(Component.USER_TOURS, Event.TOUR_STARTED), UserTour.getInstance());

			put(ComponentEvent.getInstance(Component.WIKI, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());

			put(ComponentEvent.getInstance(Component.WORKSHOP, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.getInstance(Component.XML_FILE, Event.XML_GRADE_EXPORTED), User.getInstance());

		}
	};


	public static Set<String> getNotAvaibleComponents() {
		return NOT_AVAIBLE_COMPONENTS;
	}

	public static Set<String> getNotAvaibleEvents() {
		return NOT_AVAIBLE_EVENTS;
	}

	public static ReferencesLog getReferenceLog(Component component, Event eventName) {
		return logTypes.getOrDefault(ComponentEvent.getInstance(component, eventName), Default.getInstance());
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
		if (component==Component.COMPONENT_NOT_AVAILABLE) {
			NOT_AVAIBLE_COMPONENTS.add(mapLog.get(LogCreator.COMPONENT));

		}

		Event event = Event.get(mapLog.get(LogCreator.EVENT_NAME));
		if (event==Event.EVENT_NOT_AVAILABLE) {
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
