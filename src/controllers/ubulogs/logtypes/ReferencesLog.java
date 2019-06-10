package controllers.ubulogs.logtypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.Controller;
import model.Component;
import model.ComponentEvent;
import model.EnrolledUser;
import model.Event;
import model.LogLine;
import model.mod.Module;

public abstract class ReferencesLog {


	private static final Controller CONTROLLER = Controller.getInstance();

	// @formatter:off desabilitamos el auto indentación de eclipse en esta sección
	private static final Map<ComponentEvent, ReferencesLog> LOG_TYPES = new HashMap<ComponentEvent, ReferencesLog>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{


			put(ComponentEvent.get(Component.ACTIVITY_REPORT, Event.ACTIVITY_REPORT_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.ACTIVITY_REPORT, Event.OUTLINE_REPORT_VIEWED), UserAffectedCourse.getInstance());

			put(ComponentEvent.get(Component.ASSIGNMENT, Event.A_SUBMISSION_HAS_BEEN_SUBMITTED), UserSubmissionCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.ALL_THE_SUBMISSIONS_ARE_BEING_DOWNLOADED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.AN_EXTENSION_HAS_BEEN_GRANTED), UserAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.ASSIGNMENT_OVERRIDE_CREATED), UserOverrideCmidAffected.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.ASSIGNMENT_OVERRIDE_DELETED), UserOverrideCmidAffected.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.ASSIGNMENT_OVERRIDE_UPDATED), UserOverrideCmidAffected.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.GRADING_FORM_VIEWED), UserAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.GRADING_TABLE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.SUBMISSION_CONFIRMATION_FORM_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.SUBMISSION_FORM_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.SUBMISSION_VIEWED), UserAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.THE_STATE_OF_THE_WORKFLOW_HAS_BEEN_UPDATED), UserAffectedCmid.getInstance()); 
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.THE_STATUS_OF_THE_SUBMISSION_HAS_BEEN_UPDATED), UserSubmissionCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.THE_STATUS_OF_THE_SUBMISSION_HAS_BEEN_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.THE_SUBMISSION_HAS_BEEN_GRADED), UserSubmissionAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.THE_SUBMISSIONS_HAVE_BEEN_LOCKED_FOR_A_USER), UserAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.THE_SUBMISSIONS_HAVE_BEEN_UNLOCKED_FOR_A_USER),UserAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.ASSIGNMENT, Event.THE_USER_HAS_ACCEPTED_THE_STATEMENT_OF_THE_SUBMISSION), UserSubmissionCmid.getInstance());

			put(ComponentEvent.get(Component.BOOK, Event.CHAPTER_CREATED), UserChapterCmid.getInstance());
			put(ComponentEvent.get(Component.BOOK, Event.CHAPTER_UPDATED), UserChapterCmid.getInstance());
			put(ComponentEvent.get(Component.BOOK, Event.CHAPTER_VIEWED), UserChapterCmid.getInstance());
			put(ComponentEvent.get(Component.BOOK, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.BOOK_PRINTING, Event.BOOK_PRINTED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.CHAT, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.CHAT, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.CHAT, Event.MESSAGE_SENT),  UserCmid.getInstance());
			put(ComponentEvent.get(Component.CHAT, Event.SESSIONS_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.CHOICE, Event.CHOICE_ANSWER_ADDED), UserOptionAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.CHOICE, Event.CHOICE_MADE), UserChoiceCmid.getInstance());
			put(ComponentEvent.get(Component.CHOICE, Event.CHOICE_REPORT_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.CHOICE, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.CHOICE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.COMMENTS, Event.COMMENT_CREATED), UserCommentCmid.getInstance()); //TODO
			put(ComponentEvent.get(Component.COMMENTS, Event.COMMENT_DELETED), UserCommentCmid.getInstance()); //TODO

			put(ComponentEvent.get(Component.COURSE_COMPLETION, Event.COMPLETION_REPORT_VIEWED), UserCourse.getInstance());

			put(ComponentEvent.get(Component.COURSE_PARTICIPATION, Event.PARTICIPATION_REPORT_VIEWED), UserCourse.getInstance());

			put(ComponentEvent.get(Component.DATABASE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.DATABASE, Event.FIELD_CREATED), UserFieldCmid.getInstance());
			put(ComponentEvent.get(Component.DATABASE, Event.FIELD_UPDATED), UserFieldCmid.getInstance());
			put(ComponentEvent.get(Component.DATABASE, Event.RECORD_CREATED), UserRecordCmid.getInstance());
			put(ComponentEvent.get(Component.DATABASE, Event.RECORD_DELETED), UserRecordCmid.getInstance());
			put(ComponentEvent.get(Component.DATABASE, Event.RECORD_UPDATED), UserRecordCmid.getInstance());
			put(ComponentEvent.get(Component.DATABASE, Event.TEMPLATE_UPDATED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.DATABASE, Event.TEMPLATES_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.EVENT_MONITOR, Event.RULE_CREATED), UserRule.getInstance());
			put(ComponentEvent.get(Component.EVENT_MONITOR, Event.SUBSCRIPTION_CREATED), UserSubscription.getInstance());

			put(ComponentEvent.get(Component.EXCEL_SPREADSHEET, Event.XLS_GRADE_EXPORTED), User.getInstance());

			put(ComponentEvent.get(Component.EXTERNAL_TOOL, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.FEEDBACK, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.FEEDBACK, Event.COURSE_MODULE_VIEWED), Default.getInstance());
			put(ComponentEvent.get(Component.FEEDBACK, Event.RESPONSE_SUBMITTED), UserCmid.getInstance()); 

			put(ComponentEvent.get(Component.FILE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.FILE_SUBMISSIONS, Event.A_FILE_HAS_BEEN_UPLOADED), UserSubmissionCmid.getInstance());
			put(ComponentEvent.get(Component.FILE_SUBMISSIONS, Event.SUBMISSION_CREATED), UserFilesCmid.getInstance());
			put(ComponentEvent.get(Component.FILE_SUBMISSIONS, Event.SUBMISSION_UPDATED), UserFilesCmid.getInstance());

			put(ComponentEvent.get(Component.FOLDER, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.FOLDER, Event.FOLDER_UPDATED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.FOLDER, Event.ZIP_ARCHIVE_OF_FOLDER_DOWNLOADED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.FORUM, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.COURSE_SEARCHED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.DISCUSSION_CREATED), UserDiscussionCmid.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.DISCUSSION_DELETED), UserDiscussionCmid.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.DISCUSSION_MOVED), UserDiscussionCmid.getInstance()); 
			put(ComponentEvent.get(Component.FORUM, Event.DISCUSSION_PINNED), Default.getInstance()); //TODO
			put(ComponentEvent.get(Component.FORUM, Event.DISCUSSION_SUBSCRIPTION_CREATED), UserAffectedDiscussionCmid.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.DISCUSSION_SUBSCRIPTION_DELETED), UserAffectedDiscussionCmid.getInstance()); 
			put(ComponentEvent.get(Component.FORUM, Event.DISCUSSION_VIEWED), UserDiscussionCmid.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.POST_CREATED), UserPostDiscussionCmid.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.POST_DELETED), UserPostDiscussionCmid.getInstance()); 
			put(ComponentEvent.get(Component.FORUM, Event.POST_UPDATED), UserPostDiscussionCmid.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.SOME_CONTENT_HAS_BEEN_POSTED), UserPostDiscussionCmid.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.SUBSCRIBERS_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.SUBSCRIPTION_CREATED), UserAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.SUBSCRIPTION_DELETED), UserAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.FORUM, Event.USER_REPORT_VIEWED), UserAffectedCourse.getInstance());

			put(ComponentEvent.get(Component.GLOSSARY, Event.CATEGORY_HAS_BEEN_CREATED), UserCategoryCmid.getInstance());
			put(ComponentEvent.get(Component.GLOSSARY, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.GLOSSARY, Event.ENTRY_HAS_BEEN_CREATED), UserGlossaryCmid.getInstance());
			put(ComponentEvent.get(Component.GLOSSARY, Event.ENTRY_HAS_BEEN_UPDATED), UserGlossaryCmid.getInstance());
			put(ComponentEvent.get(Component.GLOSSARY, Event.ENTRY_HAS_BEEN_VIEWED), UserGlossaryCmid.getInstance());

			put(ComponentEvent.get(Component.GRADER_REPORT, Event.GRADER_REPORT_VIEWED), User.getInstance());

			put(ComponentEvent.get(Component.GUIA_DOCENTE, Event.EVALUATION_SYSTEM_CREATED), Ignore.getInstance());
			put(ComponentEvent.get(Component.GUIA_DOCENTE, Event.EVALUATION_SYSTEM_MODIFIED), Ignore.getInstance()); 
			put(ComponentEvent.get(Component.GUIA_DOCENTE, Event.OUTSTANDING_RATINGS_MODIFIED), Ignore.getInstance()); 
			put(ComponentEvent.get(Component.GUIA_DOCENTE, Event.SUBJECT_MODIFIED), Ignore.getInstance()); 
			put(ComponentEvent.get(Component.GUIA_DOCENTE, Event.UNIT_MODIFIED), Ignore.getInstance()); 

			put(ComponentEvent.get(Component.HOTPOT_MODULE, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.HOTPOT_MODULE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			//put(ComponentEvent.get(Component.HOTPOT_MODULE, Event.HOTPOT_ATTEMPT_STARTED), UserCmid.getInstance());
			//put(ComponentEvent.get(Component.HOTPOT_MODULE, Event.HOTPOT_ATTEMPT_SUBMITTED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.IMS_CONTENT_PACKAGE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.JOURNAL, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.JOURNAL, Event.JOURNAL_ENTRIES_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.JOURNAL, Event.JOURNAL_ENTRY_CREATED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.JOURNAL, Event.JOURNAL_ENTRY_UPDATED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.LESSON, Event.CONTENT_PAGE_VIEWED), UserPageCmid.getInstance());
			put(ComponentEvent.get(Component.LESSON, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.LESSON, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.LESSON, Event.LESSON_ENDED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.LESSON, Event.LESSON_STARTED), UserCmid.getInstance()); 
			put(ComponentEvent.get(Component.LESSON, Event.PAGE_CREATED), UserPageCmid.getInstance());
			put(ComponentEvent.get(Component.LESSON, Event.PAGE_MOVED), UserPagePagePageCmid.getInstance());
			put(ComponentEvent.get(Component.LESSON, Event.PAGE_UPDATED), UserPageCmid.getInstance());
			put(ComponentEvent.get(Component.LESSON, Event.QUESTION_ANSWERED), UserPageCmid.getInstance());
			put(ComponentEvent.get(Component.LESSON, Event.QUESTION_VIEWED), UserPageCmid.getInstance());

			put(ComponentEvent.get(Component.LIVE_LOGS, Event.LIVE_LOG_REPORT_VIEWED), UserCourse.getInstance());

			put(ComponentEvent.get(Component.LOGS, Event.LOG_REPORT_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.LOGS, Event.USER_LOG_REPORT_VIEWED), UserAffected.getInstance());

			put(ComponentEvent.get(Component.ONLINE_TEXT_SUBMISSIONS, Event.AN_ONLINE_TEXT_HAS_BEEN_UPLOADED), UserSubmissionCmid.getInstance());
			put(ComponentEvent.get(Component.ONLINE_TEXT_SUBMISSIONS, Event.SUBMISSION_CREATED), UserWordsCmid.getInstance());
			put(ComponentEvent.get(Component.ONLINE_TEXT_SUBMISSIONS, Event.SUBMISSION_UPDATED), UserWordsCmid.getInstance());

			put(ComponentEvent.get(Component.OPENDOCUMENT_SPREADSHEET, Event.OPENDOCUMENT_GRADE_EXPORTED), User.getInstance());

			put(ComponentEvent.get(Component.OUTCOMES_REPORT, Event.GRADE_OUTCOMES_REPORT_VIEWED), User.getInstance());

			put(ComponentEvent.get(Component.OVERVIEW_REPORT, Event.GRADE_OVERVIEW_REPORT_VIEWED), User.getInstance());

			put(ComponentEvent.get(Component.PAGE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.PLAIN_TEXT_FILE, Event.TXT_GRADE_EXPORTED), User.getInstance());

			put(ComponentEvent.get(Component.QUIZ, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUESTION_MANUALLY_GRADED), UserQuestionAttemptCmid.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_ATTEMPT_ABANDONED), UserAttemptCmid.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_ATTEMPT_DELETED), UserAttemptCmidAffected.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_ATTEMPT_PREVIEW_STARTED), UserAttemptAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_ATTEMPT_REVIEWED), UserAttemptAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_ATTEMPT_STARTED), UserAttemptCmid.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_ATTEMPT_SUBMITTED), UserAttemptCmid.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_ATTEMPT_SUMMARY_VIEWED), UserAttemptAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_ATTEMPT_TIME_LIMIT_EXCEEDED), AttemptCmidUser.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_ATTEMPT_VIEWED), UserAttemptAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_EDIT_PAGE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_OVERRIDE_CREATED), UserOverrideCmidGroup.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_OVERRIDE_DELETED), UserOverrideCmidGroup.getInstance());
			put(ComponentEvent.get(Component.QUIZ, Event.QUIZ_REPORT_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.RECYCLE_BIN, Event.ITEM_CREATED), Item.getInstance());
			put(ComponentEvent.get(Component.RECYCLE_BIN, Event.ITEM_DELETED), Item.getInstance());
			put(ComponentEvent.get(Component.RECYCLE_BIN, Event.ITEM_RESTORED), Item.getInstance());

			put(ComponentEvent.get(Component.SCORM_PACKAGE, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.SCORM_PACKAGE, Event.SCO_LAUNCHED), UserScoCmid.getInstance());
			put(ComponentEvent.get(Component.SCORM_PACKAGE, Event.SUBMITTED_SCORM_RAW_SCORE), UserAttemptCmid.getInstance());
			put(ComponentEvent.get(Component.SCORM_PACKAGE, Event.SUBMITTED_SCORM_STATUS), UserAttemptCmid.getInstance());

			put(ComponentEvent.get(Component.SINGLE_VIEW, Event.GRADE_SINGLE_VIEW_REPORT_VIEWED), User.getInstance());

			put(ComponentEvent.get(Component.STATISTICS, Event.USER_STATISTICS_REPORT_VIEWED), UserAffected.getInstance());

			put(ComponentEvent.get(Component.SUBMISSION_COMMENTS, Event.COMMENT_CREATED), UserCommentSubmissionCmid.getInstance());
			put(ComponentEvent.get(Component.SUBMISSION_COMMENTS, Event.COMMENT_DELETED), UserCommentSubmissionCmid.getInstance());

			put(ComponentEvent.get(Component.SURVEY, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.SURVEY, Event.SURVEY_REPORT_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.SURVEY, Event.SURVEY_RESPONSE_SUBMITTED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.SYSTEM, Event.BADGE_LISTING_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.CALENDAR_EVENT_CREATED), UserCalendar.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.CALENDAR_EVENT_DELETED), UserCalendar.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.CALENDAR_EVENT_UPDATED), UserCalendar.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.CALENDAR_SUBSCRIPTION_UPDATED), UserCalendar.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_ACTIVITY_COMPLETION_UPDATED), UserCmidAffected.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_BACKUP_CREATED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_COMPLETED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_COMPLETION_UPDATED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_CONTENT_DELETED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_CREATED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_MODULE_CREATED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_MODULE_DELETED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_MODULE_UPDATED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_RESET_ENDED), Course.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_RESET_STARTED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_RESTORED), SystemCourseRestored.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_SECTION_CREATED), UserSectionCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_SECTION_DELETED), UserSectionCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_SECTION_UPDATED), UserSectionCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_SUMMARY_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_UPDATED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_USER_REPORT_VIEWED), UserCourseAffected.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.COURSE_VIEWED), SystemCourseViewed.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.ENROLMENT_INSTANCE_CREATED), UserAffected.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.ENROLMENT_INSTANCE_UPDATED), UserAffected.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.EVIDENCE_CREATED), UserEvidence.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.GRADE_DELETED), UserGradeAffectedGradeitem.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.GROUP_ASSIGNED_TO_GROUPING), UserGroupGrouping.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.GROUP_CREATED), UserGroup.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.GROUP_DELETED), UserGroup.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.GROUP_MEMBER_ADDED), UserAffectedGroup.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.GROUP_MEMBER_REMOVED), UserAffectedGroup.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.GROUP_UPDATED), UserGroup.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.GROUPING_CREATED), UserGrouping.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.GROUPING_DELETED), UserGrouping.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.NOTE_CREATED), UserNoteAffected.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.NOTES_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.QUESTION_CATEGORY_CREATED), UserQuestioncategory.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.RECENT_ACTIVITY_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.ROLE_ASSIGNED), UserRoleAffected.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.ROLE_CAPABILITIES_UPDATED), UserRole.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.ROLE_UNASSIGNED), UserRoleAffected.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.SCALE_CREATED), UserScaleCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.TAG_ADDED_TO_AN_ITEM), Default.getInstance()); //TODO
			put(ComponentEvent.get(Component.SYSTEM, Event.USER_COMPETENCY_RATED_IN_COURSE), Default.getInstance()); //TODO
			put(ComponentEvent.get(Component.SYSTEM, Event.USER_COMPETENCY_VIEWED_IN_A_COURSE), UserCompetencyCourse.getInstance()); //TODO
			put(ComponentEvent.get(Component.SYSTEM, Event.USER_ENROLLED_IN_COURSE), UserAffectedCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.USER_GRADED), UserGradeAffectedGradeitem.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.USER_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.USER_PROFILE_VIEWED), UserAffectedCourse.getInstance());
			put(ComponentEvent.get(Component.SYSTEM, Event.USER_UNENROLLED_FROM_COURSE), UserAffectedCourse.getInstance());

			put(ComponentEvent.get(Component.TURNITIN_ASSIGNMENT_2, Event.ADD_SUBMISSION), Ignore.getInstance());
			put(ComponentEvent.get(Component.TURNITIN_ASSIGNMENT_2, Event.LIST_SUBMISSIONS), Course.getInstance());

			put(ComponentEvent.get(Component.URL, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());

			put(ComponentEvent.get(Component.USER_REPORT, Event.GRADE_USER_REPORT_VIEWED), User.getInstance());

			put(ComponentEvent.get(Component.USER_TOURS, Event.STEP_SHOWN), UserTour.getInstance()); 
			put(ComponentEvent.get(Component.USER_TOURS, Event.TOUR_ENDED), UserTour.getInstance());
			put(ComponentEvent.get(Component.USER_TOURS, Event.TOUR_STARTED), UserTour.getInstance());

			put(ComponentEvent.get(Component.WIKI, Event.COMMENT_CREATED), UserCommentPageCmid.getInstance());
			put(ComponentEvent.get(Component.WIKI, Event.COMMENTS_VIEWED), UserPageCmid.getInstance());
			put(ComponentEvent.get(Component.WIKI, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.WIKI, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.WIKI, Event.WIKI_HISTORY_VIEWED), UserPageCmid.getInstance());
			put(ComponentEvent.get(Component.WIKI, Event.WIKI_PAGE_CREATED), UserPageCmid.getInstance());
			put(ComponentEvent.get(Component.WIKI, Event.WIKI_PAGE_LOCKS_DELETED), UserPageCmid.getInstance());
			put(ComponentEvent.get(Component.WIKI, Event.WIKI_PAGE_MAP_VIEWED), UserPageCmid.getInstance());
			put(ComponentEvent.get(Component.WIKI, Event.WIKI_PAGE_UPDATED), UserPageCmid.getInstance());
			put(ComponentEvent.get(Component.WIKI, Event.WIKI_PAGE_VIEWED), UserPageCmid.getInstance());

			put(ComponentEvent.get(Component.WORKSHOP, Event.A_SUBMISSION_HAS_BEEN_UPLOADED), UserSubmissionCmid.getInstance());
			put(ComponentEvent.get(Component.WORKSHOP, Event.ASSESSMENT_EVALUATED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.WORKSHOP, Event.COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
			put(ComponentEvent.get(Component.WORKSHOP, Event.COURSE_MODULE_VIEWED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.WORKSHOP, Event.PHASE_SWITCHED), UserCmid.getInstance());
			put(ComponentEvent.get(Component.WORKSHOP, Event.SUBMISSION_ASSESSED), UserSubmissionAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.WORKSHOP, Event.SUBMISSION_CREATED), UserSubmissionCmid.getInstance());
			put(ComponentEvent.get(Component.WORKSHOP, Event.SUBMISSION_DELETED), UserSubmissionCmid.getInstance());
			put(ComponentEvent.get(Component.WORKSHOP, Event.SUBMISSION_REASSESSED), UserSubmissionAffectedCmid.getInstance());
			put(ComponentEvent.get(Component.WORKSHOP, Event.SUBMISSION_UPDATED), UserSubmissionCmid.getInstance());
			put(ComponentEvent.get(Component.WORKSHOP, Event.SUBMISSION_VIEWED), UserSubmissionCmid.getInstance());

			put(ComponentEvent.get(Component.XML_FILE, Event.XML_GRADE_EXPORTED), User.getInstance());


		}
	};
	
	// @formatter:on volvemos a activar el auto indentación

	/**
	 * Devuelve la clase encargada de gestionar los numeros a partir del componente
	 * y evento, si no existe esa combinacion de componente y event devuelve uno por
	 * defecto que no hace nada.
	 * 
	 * @param component el componente del log
	 * @param eventName evento del log
	 * @return clase encargada de gestionar
	 */
	public static ReferencesLog getReferenceLog(Component component, Event eventName) {
		return LOG_TYPES.getOrDefault(ComponentEvent.get(component, eventName), Default.getInstance());
	}

	/**
	 * Modifica el usuario que realiza la acción del log a partir del id usuario.
	 * @param log log
	 * @param id id del usuario
	 */
	protected static void setUserById(LogLine log, int id) {
		EnrolledUser user = CONTROLLER.getBBDD().getEnrolledUserById(id);

		log.setUser(user);

	}

	/**
	 * Modifica el usuario afectado del log a partir del id de usuario.
	 * @param log log 
	 * @param id  del usuario
	 */
	protected static void setAffectedUserById(LogLine log, int id) {
		EnrolledUser affectedUser = CONTROLLER.getBBDD().getEnrolledUserById(id);

		log.setAffectedUser(affectedUser);

	}

	/**
	 * Modifica el modulo del curso asociado al log a partir del id del modulo del curso
	 * @param log log
	 * @param id del modulo (cmid)
	 */
	protected static void setCourseModuleById(LogLine log, int id) {
		Module courseModule = CONTROLLER.getBBDD().getCourseModuleById(id);

		log.setCourseModule(courseModule);

	}

	/**
	 * Asigna las referencias del log a otros objetos en funcion de que id sea
	 * @param log log 
	 * @param ids diferentes ids
	 */
	public abstract void setLogReferencesAttributes(LogLine log, List<Integer> ids);
	
	@Override
	public String toString() {
		return getClass().getName();
	}
}
