package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;



import static es.ubu.lsi.ubumonitor.model.Component.*;
import static es.ubu.lsi.ubumonitor.model.ComponentEvent.get;
import static es.ubu.lsi.ubumonitor.model.Event.*;

import java.util.HashMap;
import java.util.Map;

import es.ubu.lsi.ubumonitor.model.Component;
import es.ubu.lsi.ubumonitor.model.ComponentEvent;
import es.ubu.lsi.ubumonitor.model.Event;

public class LogTypes {
	
	private static final Map<ComponentEvent, ReferencesLog> TYPES = new HashMap<>();

	static{


		TYPES.put(get(ACTIVITY_REPORT, ACTIVITY_REPORT_VIEWED), UserCourse.getInstance());
		TYPES.put(get(ACTIVITY_REPORT, OUTLINE_REPORT_VIEWED), UserAffectedCourse.getInstance());

		TYPES.put(get(ASSIGNMENT, A_SUBMISSION_HAS_BEEN_SUBMITTED), UserSubmissionCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, ALL_THE_SUBMISSIONS_ARE_BEING_DOWNLOADED), UserCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, AN_EXTENSION_HAS_BEEN_GRANTED), UserAffectedCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, ASSIGNMENT_OVERRIDE_CREATED), UserOverrideCmidAffected.getInstance());
		TYPES.put(get(ASSIGNMENT, ASSIGNMENT_OVERRIDE_DELETED), UserOverrideCmidAffected.getInstance());
		TYPES.put(get(ASSIGNMENT, ASSIGNMENT_OVERRIDE_UPDATED), UserOverrideCmidAffected.getInstance());
		TYPES.put(get(ASSIGNMENT, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		TYPES.put(get(ASSIGNMENT, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, FEDBACK_VIEWED), UserAffectedCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, GRADING_FORM_VIEWED), UserAffectedCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, GRADING_TABLE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, REMOVE_SUBMISSION_CONFIRMATION_VIEWED), UserCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, SUBMISSION_CONFIRMATION_FORM_VIEWED), UserCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, SUBMISSION_FORM_VIEWED), UserCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, SUBMISSION_VIEWED), UserAffectedCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, THE_STATE_OF_THE_WORKFLOW_HAS_BEEN_UPDATED), UserAffectedCmid.getInstance()); 
		TYPES.put(get(ASSIGNMENT, THE_STATUS_OF_THE_SUBMISSION_HAS_BEEN_UPDATED), UserSubmissionCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, THE_STATUS_OF_THE_SUBMISSION_HAS_BEEN_VIEWED), UserCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, THE_SUBMISSION_HAS_BEEN_GRADED), UserSubmissionAffectedCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, THE_SUBMISSIONS_HAVE_BEEN_LOCKED_FOR_A_USER), UserAffectedCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, THE_SUBMISSIONS_HAVE_BEEN_UNLOCKED_FOR_A_USER),UserAffectedCmid.getInstance());
		TYPES.put(get(ASSIGNMENT, THE_USER_HAS_ACCEPTED_THE_STATEMENT_OF_THE_SUBMISSION), UserSubmissionCmid.getInstance());

		TYPES.put(get(BOOK, CHAPTER_CREATED), UserChapterCmid.getInstance());
		TYPES.put(get(BOOK, CHAPTER_UPDATED), UserChapterCmid.getInstance());
		TYPES.put(get(BOOK, CHAPTER_VIEWED), UserChapterCmid.getInstance());
		TYPES.put(get(BOOK, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		TYPES.put(get(BOOK_PRINTING, BOOK_PRINTED), UserCmid.getInstance());

		TYPES.put(get(CHAT, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		TYPES.put(get(CHAT, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(CHAT, MESSAGE_SENT),  UserCmid.getInstance());
		TYPES.put(get(CHAT, SESSIONS_VIEWED), UserCmid.getInstance());

		TYPES.put(get(CHOICE, CHOICE_ANSWER_ADDED), UserOptionAffectedCmid.getInstance());
		TYPES.put(get(CHOICE, CHOICE_MADE), UserChoiceCmid.getInstance());
		TYPES.put(get(CHOICE, CHOICE_REPORT_VIEWED), UserCmid.getInstance());
		TYPES.put(get(CHOICE, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		TYPES.put(get(CHOICE, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		TYPES.put(get(COMMENTS, COMMENT_CREATED), UserCommentCmid.getInstance());
		TYPES.put(get(COMMENTS, COMMENT_DELETED), UserCommentCmid.getInstance());

		TYPES.put(get(COURSE_COMPLETION, COMPLETION_REPORT_VIEWED), UserCourse.getInstance());

		TYPES.put(get(COURSE_PARTICIPATION, PARTICIPATION_REPORT_VIEWED), UserCourse.getInstance());

		TYPES.put(get(DATABASE, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(DATABASE, FIELD_CREATED), UserFieldCmid.getInstance());
		TYPES.put(get(DATABASE, FIELD_UPDATED), UserFieldCmid.getInstance());
		TYPES.put(get(DATABASE, RECORD_CREATED), UserRecordCmid.getInstance());
		TYPES.put(get(DATABASE, RECORD_DELETED), UserRecordCmid.getInstance());
		TYPES.put(get(DATABASE, RECORD_UPDATED), UserRecordCmid.getInstance());
		TYPES.put(get(DATABASE, TEMPLATE_UPDATED), UserCmid.getInstance());
		TYPES.put(get(DATABASE, TEMPLATES_VIEWED), UserCmid.getInstance());

		TYPES.put(get(EVENT_MONITOR, RULE_CREATED), UserRule.getInstance());
		TYPES.put(get(EVENT_MONITOR, SUBSCRIPTION_CREATED), UserSubscription.getInstance());

		TYPES.put(get(EXCEL_SPREADSHEET, XLS_GRADE_EXPORTED), User.getInstance());

		TYPES.put(get(EXTERNAL_TOOL, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		TYPES.put(get(FEEDBACK, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		TYPES.put(get(FEEDBACK, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(FEEDBACK, RESPONSE_SUBMITTED), UserCmid.getInstance()); 

		TYPES.put(get(FILE, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		TYPES.put(get(FILE_SUBMISSIONS, A_FILE_HAS_BEEN_UPLOADED), UserSubmissionCmid.getInstance());
		TYPES.put(get(FILE_SUBMISSIONS, SUBMISSION_CREATED), UserFilesCmid.getInstance());
		TYPES.put(get(FILE_SUBMISSIONS, SUBMISSION_CREATED_), UserFilesCmid.getInstance());
		TYPES.put(get(FILE_SUBMISSIONS, SUBMISSION_UPDATED), UserFilesCmid.getInstance());
		TYPES.put(get(FILE_SUBMISSIONS, SUBMISSION_UPDATED2), UserFilesCmid.getInstance());

		TYPES.put(get(FOLDER, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(FOLDER, FOLDER_UPDATED), UserCmid.getInstance());
		TYPES.put(get(FOLDER, ZIP_ARCHIVE_OF_FOLDER_DOWNLOADED), UserCmid.getInstance());

		TYPES.put(get(FORUM, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		TYPES.put(get(FORUM, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(FORUM, COURSE_SEARCHED), UserCourse.getInstance());
		TYPES.put(get(FORUM, DISCUSSION_CREATED), UserDiscussionCmid.getInstance());
		TYPES.put(get(FORUM, DISCUSSION_DELETED), UserDiscussionCmid.getInstance());
		TYPES.put(get(FORUM, DISCUSSION_MOVED), UserDiscussionCmid.getInstance()); 
		TYPES.put(get(FORUM, DISCUSSION_PINNED), UserDiscussionCmid.getInstance());
		TYPES.put(get(FORUM, DISCUSSION_SUBSCRIPTION_CREATED), UserAffectedDiscussionCmid.getInstance());
		TYPES.put(get(FORUM, DISCUSSION_SUBSCRIPTION_DELETED), UserAffectedDiscussionCmid.getInstance()); 
		TYPES.put(get(FORUM, DISCUSSION_VIEWED), UserDiscussionCmid.getInstance());
		TYPES.put(get(FORUM, POST_CREATED), UserPostDiscussionCmid.getInstance());
		TYPES.put(get(FORUM, POST_DELETED), UserPostDiscussionCmid.getInstance()); 
		TYPES.put(get(FORUM, POST_UPDATED), UserPostDiscussionCmid.getInstance());
		TYPES.put(get(FORUM, SOME_CONTENT_HAS_BEEN_POSTED), UserPostDiscussionCmid.getInstance());
		TYPES.put(get(FORUM, SUBSCRIBERS_VIEWED), UserCmid.getInstance());
		TYPES.put(get(FORUM, SUBSCRIPTION_CREATED), UserAffectedCmid.getInstance());
		TYPES.put(get(FORUM, SUBSCRIPTION_DELETED), UserAffectedCmid.getInstance());
		TYPES.put(get(FORUM, USER_REPORT_VIEWED), UserAffectedCourse.getInstance());

		TYPES.put(get(GLOSSARY, CATEGORY_HAS_BEEN_CREATED), UserCategoryCmid.getInstance());
		TYPES.put(get(GLOSSARY, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(GLOSSARY, ENTRY_HAS_BEEN_CREATED), UserGlossaryCmid.getInstance());
		TYPES.put(get(GLOSSARY, ENTRY_HAS_BEEN_UPDATED), UserGlossaryCmid.getInstance());
		TYPES.put(get(GLOSSARY, ENTRY_HAS_BEEN_VIEWED), UserGlossaryCmid.getInstance());

		TYPES.put(get(GRADER_REPORT, GRADER_REPORT_VIEWED), User.getInstance());

		TYPES.put(get(GUIA_DOCENTE, EVALUATION_SYSTEM_CREATED), Ignore.getInstance());
		TYPES.put(get(GUIA_DOCENTE, EVALUATION_SYSTEM_MODIFIED), Ignore.getInstance()); 
		TYPES.put(get(GUIA_DOCENTE, OUTSTANDING_RATINGS_MODIFIED), Ignore.getInstance()); 
		TYPES.put(get(GUIA_DOCENTE, SUBJECT_MODIFIED), Ignore.getInstance()); 
		TYPES.put(get(GUIA_DOCENTE, UNIT_MODIFIED), Ignore.getInstance()); 

		TYPES.put(get(HOTPOT_MODULE, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		TYPES.put(get(HOTPOT_MODULE, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(HOTPOT_MODULE, HOTPOT_ATTEMPT_STARTED), UserCmid.getInstance());
		TYPES.put(get(HOTPOT_MODULE, HOTPOT_ATTEMPT_SUBMITTED), UserCmid.getInstance());

		TYPES.put(get(IMS_CONTENT_PACKAGE, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		TYPES.put(get(JOURNAL, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(JOURNAL, JOURNAL_ENTRIES_VIEWED), UserCmid.getInstance());
		TYPES.put(get(JOURNAL, JOURNAL_ENTRY_CREATED), UserCmid.getInstance());
		TYPES.put(get(JOURNAL, JOURNAL_ENTRY_UPDATED), UserCmid.getInstance());

		TYPES.put(get(LESSON, CONTENT_PAGE_VIEWED), UserPageCmid.getInstance());
		TYPES.put(get(LESSON, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		TYPES.put(get(LESSON, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(LESSON, LESSON_ENDED), UserCmid.getInstance());
		TYPES.put(get(LESSON, LESSON_STARTED), UserCmid.getInstance()); 
		TYPES.put(get(LESSON, PAGE_CREATED), UserPageCmid.getInstance());
		TYPES.put(get(LESSON, PAGE_MOVED), UserPagePagePageCmid.getInstance());
		TYPES.put(get(LESSON, PAGE_UPDATED), UserPageCmid.getInstance());
		TYPES.put(get(LESSON, LESSON_RESTARTED), UserCmid.getInstance());
		TYPES.put(get(LESSON, LESSON_RESUMED), UserCmid.getInstance());
		TYPES.put(get(LESSON, QUESTION_ANSWERED), UserPageCmid.getInstance());
		TYPES.put(get(LESSON, QUESTION_VIEWED), UserPageCmid.getInstance());

		TYPES.put(get(LIVE_LOGS, LIVE_LOG_REPORT_VIEWED), UserCourse.getInstance());

		TYPES.put(get(LOGS, LOG_REPORT_VIEWED), UserCourse.getInstance());
		TYPES.put(get(LOGS, USER_LOG_REPORT_VIEWED), UserAffected.getInstance());

		TYPES.put(get(ONLINE_TEXT_SUBMISSIONS, AN_ONLINE_TEXT_HAS_BEEN_UPLOADED), UserSubmissionCmid.getInstance());
		TYPES.put(get(ONLINE_TEXT_SUBMISSIONS, SUBMISSION_CREATED), UserWordsCmid.getInstance());
		TYPES.put(get(ONLINE_TEXT_SUBMISSIONS, SUBMISSION_UPDATED), UserWordsCmid.getInstance());
		TYPES.put(get(ONLINE_TEXT_SUBMISSIONS, SUBMISSION_CREATED_), UserWordsCmid.getInstance());
		TYPES.put(get(ONLINE_TEXT_SUBMISSIONS, SUBMISSION_UPDATED2), UserWordsCmid.getInstance());

		TYPES.put(get(OPENDOCUMENT_SPREADSHEET, OPENDOCUMENT_GRADE_EXPORTED), User.getInstance());

		TYPES.put(get(OUTCOMES_REPORT, GRADE_OUTCOMES_REPORT_VIEWED), User.getInstance());

		TYPES.put(get(OVERVIEW_REPORT, GRADE_OVERVIEW_REPORT_VIEWED), User.getInstance());

		TYPES.put(get(PAGE, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		TYPES.put(get(PLAIN_TEXT_FILE, TXT_GRADE_EXPORTED), User.getInstance());

		TYPES.put(get(QUIZ, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		TYPES.put(get(QUIZ, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(QUIZ, QUESTION_MANUALLY_GRADED), UserQuestionAttemptCmid.getInstance());
		TYPES.put(get(QUIZ, QUIZ_ATTEMPT_ABANDONED), UserAttemptCmid.getInstance());
		TYPES.put(get(QUIZ, QUIZ_ATTEMPT_DELETED), UserAttemptCmidAffected.getInstance());
		TYPES.put(get(QUIZ, QUIZ_ATTEMPT_PREVIEW_STARTED), UserAttemptAffectedCmid.getInstance());
		TYPES.put(get(QUIZ, QUIZ_ATTEMPT_REVIEWED), UserAttemptAffectedCmid.getInstance());
		TYPES.put(get(QUIZ, QUIZ_ATTEMPT_STARTED), UserAttemptCmid.getInstance());
		TYPES.put(get(QUIZ, QUIZ_ATTEMPT_SUBMITTED), UserAttemptCmid.getInstance());
		TYPES.put(get(QUIZ, QUIZ_ATTEMPT_SUMMARY_VIEWED), UserAttemptAffectedCmid.getInstance());
		TYPES.put(get(QUIZ, QUIZ_ATTEMPT_TIME_LIMIT_EXCEEDED), AttemptCmidUser.getInstance());
		TYPES.put(get(QUIZ, QUIZ_ATTEMPT_VIEWED), UserAttemptAffectedCmid.getInstance());
		TYPES.put(get(QUIZ, QUIZ_EDIT_PAGE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(QUIZ, QUIZ_OVERRIDE_CREATED), UserOverrideCmidGroup.getInstance());
		TYPES.put(get(QUIZ, QUIZ_OVERRIDE_DELETED), UserOverrideCmidGroup.getInstance());
		TYPES.put(get(QUIZ, QUIZ_OVERRIDE_UPDATED), UserOverrideCmidAffected.getInstance());
		TYPES.put(get(QUIZ, QUIZ_REPORT_VIEWED), UserCmid.getInstance());

		TYPES.put(get(RECYCLE_BIN, ITEM_CREATED), Item.getInstance());
		TYPES.put(get(RECYCLE_BIN, ITEM_DELETED), Item.getInstance());
		TYPES.put(get(RECYCLE_BIN, ITEM_RESTORED), Item.getInstance());

		TYPES.put(get(SCORM_PACKAGE, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(SCORM_PACKAGE, SCO_LAUNCHED), UserScoCmid.getInstance());
		TYPES.put(get(SCORM_PACKAGE, SUBMITTED_SCORM_RAW_SCORE), UserAttemptCmid.getInstance());
		TYPES.put(get(SCORM_PACKAGE, SUBMITTED_SCORM_STATUS), UserAttemptCmid.getInstance());

		TYPES.put(get(SINGLE_VIEW, GRADE_SINGLE_VIEW_REPORT_VIEWED), User.getInstance());

		TYPES.put(get(STATISTICS, USER_STATISTICS_REPORT_VIEWED), UserAffected.getInstance());

		TYPES.put(get(SUBMISSION_COMMENTS, COMMENT_CREATED), UserCommentSubmissionCmid.getInstance());
		TYPES.put(get(SUBMISSION_COMMENTS, COMMENT_DELETED), UserCommentSubmissionCmid.getInstance());

		TYPES.put(get(SURVEY, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(SURVEY, SURVEY_REPORT_VIEWED), UserCmid.getInstance());
		TYPES.put(get(SURVEY, SURVEY_RESPONSE_SUBMITTED), UserCmid.getInstance());

		TYPES.put(get(SYSTEM, BADGE_LISTING_VIEWED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, CALENDAR_EVENT_CREATED), UserCalendar.getInstance());
		TYPES.put(get(SYSTEM, CALENDAR_EVENT_DELETED), UserCalendar.getInstance());
		TYPES.put(get(SYSTEM, CALENDAR_EVENT_UPDATED), UserCalendar.getInstance());
		TYPES.put(get(SYSTEM, CALENDAR_SUBSCRIPTION_UPDATED), UserCalendar.getInstance());
		TYPES.put(get(SYSTEM, COURSE_ACTIVITY_COMPLETION_UPDATED), UserCmidAffected.getInstance());
		TYPES.put(get(SYSTEM, COURSE_BACKUP_CREATED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, COURSE_COMPLETED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, COURSE_COMPLETION_UPDATED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, COURSE_CONTENT_DELETED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, COURSE_CREATED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, COURSE_MODULE_CREATED), UserCmid.getInstance());
		TYPES.put(get(SYSTEM, COURSE_MODULE_DELETED), UserCmid.getInstance());
		TYPES.put(get(SYSTEM, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, COURSE_MODULE_UPDATED), UserCmid.getInstance());
		TYPES.put(get(SYSTEM, COURSE_RESET_ENDED), Course.getInstance());
		TYPES.put(get(SYSTEM, COURSE_RESET_STARTED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, COURSE_RESTORED), SystemCourseRestored.getInstance());
		TYPES.put(get(SYSTEM, COURSE_SECTION_CREATED), UserSectionCourse.getInstance());
		TYPES.put(get(SYSTEM, COURSE_SECTION_DELETED), UserSectionCourse.getInstance());
		TYPES.put(get(SYSTEM, COURSE_SECTION_UPDATED), UserSectionCourse.getInstance());
		TYPES.put(get(SYSTEM, COURSE_SUMMARY_VIEWED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, COURSE_UPDATED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, COURSE_USER_REPORT_VIEWED), UserCourseAffected.getInstance());
		TYPES.put(get(SYSTEM, COURSE_VIEWED), SystemCourseViewed.getInstance());
		TYPES.put(get(SYSTEM, ENROLMENT_INSTANCE_CREATED), UserAffected.getInstance());
		TYPES.put(get(SYSTEM, ENROLMENT_INSTANCE_UPDATED), UserAffected.getInstance());
		TYPES.put(get(SYSTEM, EVIDENCE_CREATED), UserEvidence.getInstance());
		TYPES.put(get(SYSTEM, GRADE_DELETED), UserGradeAffectedGradeitem.getInstance());
		TYPES.put(get(SYSTEM, GROUP_ASSIGNED_TO_GROUPING), UserGroupGrouping.getInstance());
		TYPES.put(get(SYSTEM, GROUP_CREATED), UserGroup.getInstance());
		TYPES.put(get(SYSTEM, GROUP_DELETED), UserGroup.getInstance());
		TYPES.put(get(SYSTEM, GROUP_MEMBER_ADDED), UserAffectedGroup.getInstance());
		TYPES.put(get(SYSTEM, GROUP_MEMBER_REMOVED), UserAffectedGroup.getInstance());
		TYPES.put(get(SYSTEM, GROUP_UPDATED), UserGroup.getInstance());
		TYPES.put(get(SYSTEM, GROUPING_CREATED), UserGrouping.getInstance());
		TYPES.put(get(SYSTEM, GROUPING_DELETED), UserGrouping.getInstance());
		TYPES.put(get(SYSTEM, NOTE_CREATED), UserNoteAffected.getInstance());
		TYPES.put(get(SYSTEM, NOTES_VIEWED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, QUESTION_CATEGORY_CREATED), UserQuestioncategory.getInstance());
		TYPES.put(get(SYSTEM, QUESTION_CATEGORY_VIEWED), UserQuestioncategory.getInstance());
		TYPES.put(get(SYSTEM, QUESTION_UPDATED), UserQuestion.getInstance());
		TYPES.put(get(SYSTEM, QUESTION_VIEWED), UserQuestion.getInstance());
		TYPES.put(get(SYSTEM, RECENT_ACTIVITY_VIEWED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, ROLE_ASSIGNED), UserRoleAffected.getInstance());
		TYPES.put(get(SYSTEM, ROLE_CAPABILITIES_UPDATED), UserRole.getInstance());
		TYPES.put(get(SYSTEM, ROLE_UNASSIGNED), UserRoleAffected.getInstance());
		TYPES.put(get(SYSTEM, SCALE_CREATED), UserScaleCourse.getInstance());
		TYPES.put(get(SYSTEM, TAG_ADDED_TO_AN_ITEM), Default.getInstance());
		TYPES.put(get(SYSTEM, USER_COMPETENCY_RATED_IN_COURSE), Default.getInstance());
		TYPES.put(get(SYSTEM, USER_COMPETENCY_VIEWED_IN_A_COURSE), UserCompetencyCourse.getInstance());
		TYPES.put(get(SYSTEM, USER_ENROLLED_IN_COURSE), UserAffectedCourse.getInstance());
		TYPES.put(get(SYSTEM, USER_GRADED), UserGradeAffectedGradeitem.getInstance());
		TYPES.put(get(SYSTEM, USER_LIST_VIEWED), UserCourse.getInstance());
		TYPES.put(get(SYSTEM, USER_PROFILE_VIEWED), UserAffectedCourse.getInstance());
		TYPES.put(get(SYSTEM, USER_UNENROLLED_FROM_COURSE), UserAffectedCourse.getInstance());

		TYPES.put(get(TAB_DISPLAY, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		
		TYPES.put(get(TURNITIN_ASSIGNMENT_2, ADD_SUBMISSION), Ignore.getInstance());
		TYPES.put(get(TURNITIN_ASSIGNMENT_2, LIST_SUBMISSIONS), Course.getInstance());

		TYPES.put(get(URL, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		TYPES.put(get(USER_REPORT, GRADE_USER_REPORT_VIEWED), User.getInstance());

		TYPES.put(get(USER_TOURS, STEP_SHOWN), UserTour.getInstance()); 
		TYPES.put(get(USER_TOURS, TOUR_ENDED), UserTour.getInstance());
		TYPES.put(get(USER_TOURS, TOUR_STARTED), UserTour.getInstance());

		TYPES.put(get(WIKI, COMMENT_CREATED), UserCommentPageCmid.getInstance());
		TYPES.put(get(WIKI, COMMENTS_VIEWED), UserPageCmid.getInstance());
		TYPES.put(get(WIKI, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		TYPES.put(get(WIKI, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(WIKI, WIKI_HISTORY_VIEWED), UserPageCmid.getInstance());
		TYPES.put(get(WIKI, WIKI_PAGE_CREATED), UserPageCmid.getInstance());
		TYPES.put(get(WIKI, WIKI_PAGE_LOCKS_DELETED), UserPageCmid.getInstance());
		TYPES.put(get(WIKI, WIKI_PAGE_MAP_VIEWED), UserPageCmid.getInstance());
		TYPES.put(get(WIKI, WIKI_PAGE_UPDATED), UserPageCmid.getInstance());
		TYPES.put(get(WIKI, WIKI_PAGE_VIEWED), UserPageCmid.getInstance());
		
		TYPES.put(get(KALTURA_MEDIA_ASSIGNMENT, ASSIGNMENT_DETAILS_VIEWED), UserCmid.getInstance());
		TYPES.put(get(KALTURA_VIDEO_RESOURCE, VIDEO_RESOURCE_VIEWED), UserCmid.getInstance());
		
		TYPES.put(get(WORKSHOP, A_SUBMISSION_HAS_BEEN_UPLOADED), UserSubmissionCmid.getInstance());
		TYPES.put(get(WORKSHOP, ASSESSMENT_EVALUATED), UserCmid.getInstance());
		TYPES.put(get(WORKSHOP, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		TYPES.put(get(WORKSHOP, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		TYPES.put(get(WORKSHOP, PHASE_SWITCHED), UserCmid.getInstance());
		TYPES.put(get(WORKSHOP, SUBMISSION_ASSESSED), UserSubmissionAffectedCmid.getInstance());
		TYPES.put(get(WORKSHOP, SUBMISSION_CREATED), UserSubmissionCmid.getInstance());
		TYPES.put(get(WORKSHOP, SUBMISSION_DELETED), UserSubmissionCmid.getInstance());
		TYPES.put(get(WORKSHOP, SUBMISSION_REASSESSED), UserSubmissionAffectedCmid.getInstance());
		TYPES.put(get(WORKSHOP, SUBMISSION_UPDATED), UserSubmissionCmid.getInstance());
		TYPES.put(get(WORKSHOP, SUBMISSION_VIEWED), UserSubmissionCmid.getInstance());

		TYPES.put(get(XML_FILE, XML_GRADE_EXPORTED), User.getInstance());

	}

	/**
	 * Devuelve la clase encargada de gestionar los numeros a partir del componente
	 * y evento, si no existe esa combinacion de componente y event devuelve uno por
	 * defecto que no hace nada.
	 * 
	 * @param component
	 *            el componente del log
	 * @param eventName
	 *            evento del log
	 * @return clase encargada de gestionar
	 */
	public static ReferencesLog getReferenceLog(Component component, Event eventName) {
		return TYPES.getOrDefault(get(component, eventName), Default.getInstance());
	}

	
	
	private LogTypes() {
		throw new UnsupportedOperationException();
	}
}
