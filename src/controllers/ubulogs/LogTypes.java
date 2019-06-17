package controllers.ubulogs;

import java.util.HashMap;
import java.util.Map;

import controllers.ubulogs.logtypes.AttemptCmidUser;
import controllers.ubulogs.logtypes.Course;
import controllers.ubulogs.logtypes.Default;
import controllers.ubulogs.logtypes.Ignore;
import controllers.ubulogs.logtypes.Item;
import controllers.ubulogs.logtypes.ReferencesLog;
import controllers.ubulogs.logtypes.SystemCourseRestored;
import controllers.ubulogs.logtypes.SystemCourseViewed;
import controllers.ubulogs.logtypes.User;
import controllers.ubulogs.logtypes.UserAffected;
import controllers.ubulogs.logtypes.UserAffectedCmid;
import controllers.ubulogs.logtypes.UserAffectedCourse;
import controllers.ubulogs.logtypes.UserAffectedDiscussionCmid;
import controllers.ubulogs.logtypes.UserAffectedGroup;
import controllers.ubulogs.logtypes.UserAttemptAffectedCmid;
import controllers.ubulogs.logtypes.UserAttemptCmid;
import controllers.ubulogs.logtypes.UserAttemptCmidAffected;
import controllers.ubulogs.logtypes.UserCalendar;
import controllers.ubulogs.logtypes.UserCategoryCmid;
import controllers.ubulogs.logtypes.UserChapterCmid;
import controllers.ubulogs.logtypes.UserChoiceCmid;
import controllers.ubulogs.logtypes.UserCmid;
import controllers.ubulogs.logtypes.UserCmidAffected;
import controllers.ubulogs.logtypes.UserCommentCmid;
import controllers.ubulogs.logtypes.UserCommentPageCmid;
import controllers.ubulogs.logtypes.UserCommentSubmissionCmid;
import controllers.ubulogs.logtypes.UserCompetencyCourse;
import controllers.ubulogs.logtypes.UserCourse;
import controllers.ubulogs.logtypes.UserCourseAffected;
import controllers.ubulogs.logtypes.UserDiscussionCmid;
import controllers.ubulogs.logtypes.UserEvidence;
import controllers.ubulogs.logtypes.UserFieldCmid;
import controllers.ubulogs.logtypes.UserFilesCmid;
import controllers.ubulogs.logtypes.UserGlossaryCmid;
import controllers.ubulogs.logtypes.UserGradeAffectedGradeitem;
import controllers.ubulogs.logtypes.UserGroup;
import controllers.ubulogs.logtypes.UserGroupGrouping;
import controllers.ubulogs.logtypes.UserGrouping;
import controllers.ubulogs.logtypes.UserNoteAffected;
import controllers.ubulogs.logtypes.UserOptionAffectedCmid;
import controllers.ubulogs.logtypes.UserOverrideCmidAffected;
import controllers.ubulogs.logtypes.UserOverrideCmidGroup;
import controllers.ubulogs.logtypes.UserPageCmid;
import controllers.ubulogs.logtypes.UserPagePagePageCmid;
import controllers.ubulogs.logtypes.UserPostDiscussionCmid;
import controllers.ubulogs.logtypes.UserQuestionAttemptCmid;
import controllers.ubulogs.logtypes.UserQuestioncategory;
import controllers.ubulogs.logtypes.UserRecordCmid;
import controllers.ubulogs.logtypes.UserRole;
import controllers.ubulogs.logtypes.UserRoleAffected;
import controllers.ubulogs.logtypes.UserRule;
import controllers.ubulogs.logtypes.UserScaleCourse;
import controllers.ubulogs.logtypes.UserScoCmid;
import controllers.ubulogs.logtypes.UserSectionCourse;
import controllers.ubulogs.logtypes.UserSubmissionAffectedCmid;
import controllers.ubulogs.logtypes.UserSubmissionCmid;
import controllers.ubulogs.logtypes.UserSubscription;
import controllers.ubulogs.logtypes.UserTour;
import controllers.ubulogs.logtypes.UserWordsCmid;
import model.ComponentEvent;


import static model.Component.*;
import static model.Event.*;
import static model.ComponentEvent.get;

public class LogTypes {
	
	private static final Map<ComponentEvent, ReferencesLog> LOG_TYPES = new HashMap<>();

	static{


		LOG_TYPES.put(get(ACTIVITY_REPORT, ACTIVITY_REPORT_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(ACTIVITY_REPORT, OUTLINE_REPORT_VIEWED), UserAffectedCourse.getInstance());

		LOG_TYPES.put(get(ASSIGNMENT, A_SUBMISSION_HAS_BEEN_SUBMITTED), UserSubmissionCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, ALL_THE_SUBMISSIONS_ARE_BEING_DOWNLOADED), UserCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, AN_EXTENSION_HAS_BEEN_GRANTED), UserAffectedCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, ASSIGNMENT_OVERRIDE_CREATED), UserOverrideCmidAffected.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, ASSIGNMENT_OVERRIDE_DELETED), UserOverrideCmidAffected.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, ASSIGNMENT_OVERRIDE_UPDATED), UserOverrideCmidAffected.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, GRADING_FORM_VIEWED), UserAffectedCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, GRADING_TABLE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, SUBMISSION_CONFIRMATION_FORM_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, SUBMISSION_FORM_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, SUBMISSION_VIEWED), UserAffectedCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, THE_STATE_OF_THE_WORKFLOW_HAS_BEEN_UPDATED), UserAffectedCmid.getInstance()); 
		LOG_TYPES.put(get(ASSIGNMENT, THE_STATUS_OF_THE_SUBMISSION_HAS_BEEN_UPDATED), UserSubmissionCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, THE_STATUS_OF_THE_SUBMISSION_HAS_BEEN_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, THE_SUBMISSION_HAS_BEEN_GRADED), UserSubmissionAffectedCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, THE_SUBMISSIONS_HAVE_BEEN_LOCKED_FOR_A_USER), UserAffectedCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, THE_SUBMISSIONS_HAVE_BEEN_UNLOCKED_FOR_A_USER),UserAffectedCmid.getInstance());
		LOG_TYPES.put(get(ASSIGNMENT, THE_USER_HAS_ACCEPTED_THE_STATEMENT_OF_THE_SUBMISSION), UserSubmissionCmid.getInstance());

		LOG_TYPES.put(get(BOOK, CHAPTER_CREATED), UserChapterCmid.getInstance());
		LOG_TYPES.put(get(BOOK, CHAPTER_UPDATED), UserChapterCmid.getInstance());
		LOG_TYPES.put(get(BOOK, CHAPTER_VIEWED), UserChapterCmid.getInstance());
		LOG_TYPES.put(get(BOOK, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		LOG_TYPES.put(get(BOOK_PRINTING, BOOK_PRINTED), UserCmid.getInstance());

		LOG_TYPES.put(get(CHAT, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(CHAT, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(CHAT, MESSAGE_SENT),  UserCmid.getInstance());
		LOG_TYPES.put(get(CHAT, SESSIONS_VIEWED), UserCmid.getInstance());

		LOG_TYPES.put(get(CHOICE, CHOICE_ANSWER_ADDED), UserOptionAffectedCmid.getInstance());
		LOG_TYPES.put(get(CHOICE, CHOICE_MADE), UserChoiceCmid.getInstance());
		LOG_TYPES.put(get(CHOICE, CHOICE_REPORT_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(CHOICE, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(CHOICE, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		LOG_TYPES.put(get(COMMENTS, COMMENT_CREATED), UserCommentCmid.getInstance());
		LOG_TYPES.put(get(COMMENTS, COMMENT_DELETED), UserCommentCmid.getInstance());

		LOG_TYPES.put(get(COURSE_COMPLETION, COMPLETION_REPORT_VIEWED), UserCourse.getInstance());

		LOG_TYPES.put(get(COURSE_PARTICIPATION, PARTICIPATION_REPORT_VIEWED), UserCourse.getInstance());

		LOG_TYPES.put(get(DATABASE, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(DATABASE, FIELD_CREATED), UserFieldCmid.getInstance());
		LOG_TYPES.put(get(DATABASE, FIELD_UPDATED), UserFieldCmid.getInstance());
		LOG_TYPES.put(get(DATABASE, RECORD_CREATED), UserRecordCmid.getInstance());
		LOG_TYPES.put(get(DATABASE, RECORD_DELETED), UserRecordCmid.getInstance());
		LOG_TYPES.put(get(DATABASE, RECORD_UPDATED), UserRecordCmid.getInstance());
		LOG_TYPES.put(get(DATABASE, TEMPLATE_UPDATED), UserCmid.getInstance());
		LOG_TYPES.put(get(DATABASE, TEMPLATES_VIEWED), UserCmid.getInstance());

		LOG_TYPES.put(get(EVENT_MONITOR, RULE_CREATED), UserRule.getInstance());
		LOG_TYPES.put(get(EVENT_MONITOR, SUBSCRIPTION_CREATED), UserSubscription.getInstance());

		LOG_TYPES.put(get(EXCEL_SPREADSHEET, XLS_GRADE_EXPORTED), User.getInstance());

		LOG_TYPES.put(get(EXTERNAL_TOOL, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		LOG_TYPES.put(get(FEEDBACK, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(FEEDBACK, COURSE_MODULE_VIEWED), Default.getInstance());
		LOG_TYPES.put(get(FEEDBACK, RESPONSE_SUBMITTED), UserCmid.getInstance()); 

		LOG_TYPES.put(get(FILE, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		LOG_TYPES.put(get(FILE_SUBMISSIONS, A_FILE_HAS_BEEN_UPLOADED), UserSubmissionCmid.getInstance());
		LOG_TYPES.put(get(FILE_SUBMISSIONS, SUBMISSION_CREATED), UserFilesCmid.getInstance());
		LOG_TYPES.put(get(FILE_SUBMISSIONS, SUBMISSION_UPDATED), UserFilesCmid.getInstance());

		LOG_TYPES.put(get(FOLDER, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(FOLDER, FOLDER_UPDATED), UserCmid.getInstance());
		LOG_TYPES.put(get(FOLDER, ZIP_ARCHIVE_OF_FOLDER_DOWNLOADED), UserCmid.getInstance());

		LOG_TYPES.put(get(FORUM, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(FORUM, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(FORUM, COURSE_SEARCHED), UserCourse.getInstance());
		LOG_TYPES.put(get(FORUM, DISCUSSION_CREATED), UserDiscussionCmid.getInstance());
		LOG_TYPES.put(get(FORUM, DISCUSSION_DELETED), UserDiscussionCmid.getInstance());
		LOG_TYPES.put(get(FORUM, DISCUSSION_MOVED), UserDiscussionCmid.getInstance()); 
		LOG_TYPES.put(get(FORUM, DISCUSSION_PINNED), Default.getInstance()); //TODO
		LOG_TYPES.put(get(FORUM, DISCUSSION_SUBSCRIPTION_CREATED), UserAffectedDiscussionCmid.getInstance());
		LOG_TYPES.put(get(FORUM, DISCUSSION_SUBSCRIPTION_DELETED), UserAffectedDiscussionCmid.getInstance()); 
		LOG_TYPES.put(get(FORUM, DISCUSSION_VIEWED), UserDiscussionCmid.getInstance());
		LOG_TYPES.put(get(FORUM, POST_CREATED), UserPostDiscussionCmid.getInstance());
		LOG_TYPES.put(get(FORUM, POST_DELETED), UserPostDiscussionCmid.getInstance()); 
		LOG_TYPES.put(get(FORUM, POST_UPDATED), UserPostDiscussionCmid.getInstance());
		LOG_TYPES.put(get(FORUM, SOME_CONTENT_HAS_BEEN_POSTED), UserPostDiscussionCmid.getInstance());
		LOG_TYPES.put(get(FORUM, SUBSCRIBERS_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(FORUM, SUBSCRIPTION_CREATED), UserAffectedCmid.getInstance());
		LOG_TYPES.put(get(FORUM, SUBSCRIPTION_DELETED), UserAffectedCmid.getInstance());
		LOG_TYPES.put(get(FORUM, USER_REPORT_VIEWED), UserAffectedCourse.getInstance());

		LOG_TYPES.put(get(GLOSSARY, CATEGORY_HAS_BEEN_CREATED), UserCategoryCmid.getInstance());
		LOG_TYPES.put(get(GLOSSARY, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(GLOSSARY, ENTRY_HAS_BEEN_CREATED), UserGlossaryCmid.getInstance());
		LOG_TYPES.put(get(GLOSSARY, ENTRY_HAS_BEEN_UPDATED), UserGlossaryCmid.getInstance());
		LOG_TYPES.put(get(GLOSSARY, ENTRY_HAS_BEEN_VIEWED), UserGlossaryCmid.getInstance());

		LOG_TYPES.put(get(GRADER_REPORT, GRADER_REPORT_VIEWED), User.getInstance());

		LOG_TYPES.put(get(GUIA_DOCENTE, EVALUATION_SYSTEM_CREATED), Ignore.getInstance());
		LOG_TYPES.put(get(GUIA_DOCENTE, EVALUATION_SYSTEM_MODIFIED), Ignore.getInstance()); 
		LOG_TYPES.put(get(GUIA_DOCENTE, OUTSTANDING_RATINGS_MODIFIED), Ignore.getInstance()); 
		LOG_TYPES.put(get(GUIA_DOCENTE, SUBJECT_MODIFIED), Ignore.getInstance()); 
		LOG_TYPES.put(get(GUIA_DOCENTE, UNIT_MODIFIED), Ignore.getInstance()); 

		LOG_TYPES.put(get(HOTPOT_MODULE, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(HOTPOT_MODULE, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(HOTPOT_MODULE, HOTPOT_ATTEMPT_STARTED), UserCmid.getInstance());
		LOG_TYPES.put(get(HOTPOT_MODULE, HOTPOT_ATTEMPT_SUBMITTED), UserCmid.getInstance());

		LOG_TYPES.put(get(IMS_CONTENT_PACKAGE, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		LOG_TYPES.put(get(JOURNAL, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(JOURNAL, JOURNAL_ENTRIES_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(JOURNAL, JOURNAL_ENTRY_CREATED), UserCmid.getInstance());
		LOG_TYPES.put(get(JOURNAL, JOURNAL_ENTRY_UPDATED), UserCmid.getInstance());

		LOG_TYPES.put(get(LESSON, CONTENT_PAGE_VIEWED), UserPageCmid.getInstance());
		LOG_TYPES.put(get(LESSON, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(LESSON, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(LESSON, LESSON_ENDED), UserCmid.getInstance());
		LOG_TYPES.put(get(LESSON, LESSON_STARTED), UserCmid.getInstance()); 
		LOG_TYPES.put(get(LESSON, PAGE_CREATED), UserPageCmid.getInstance());
		LOG_TYPES.put(get(LESSON, PAGE_MOVED), UserPagePagePageCmid.getInstance());
		LOG_TYPES.put(get(LESSON, PAGE_UPDATED), UserPageCmid.getInstance());
		LOG_TYPES.put(get(LESSON, QUESTION_ANSWERED), UserPageCmid.getInstance());
		LOG_TYPES.put(get(LESSON, QUESTION_VIEWED), UserPageCmid.getInstance());

		LOG_TYPES.put(get(LIVE_LOGS, LIVE_LOG_REPORT_VIEWED), UserCourse.getInstance());

		LOG_TYPES.put(get(LOGS, LOG_REPORT_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(LOGS, USER_LOG_REPORT_VIEWED), UserAffected.getInstance());

		LOG_TYPES.put(get(ONLINE_TEXT_SUBMISSIONS, AN_ONLINE_TEXT_HAS_BEEN_UPLOADED), UserSubmissionCmid.getInstance());
		LOG_TYPES.put(get(ONLINE_TEXT_SUBMISSIONS, SUBMISSION_CREATED), UserWordsCmid.getInstance());
		LOG_TYPES.put(get(ONLINE_TEXT_SUBMISSIONS, SUBMISSION_UPDATED), UserWordsCmid.getInstance());

		LOG_TYPES.put(get(OPENDOCUMENT_SPREADSHEET, OPENDOCUMENT_GRADE_EXPORTED), User.getInstance());

		LOG_TYPES.put(get(OUTCOMES_REPORT, GRADE_OUTCOMES_REPORT_VIEWED), User.getInstance());

		LOG_TYPES.put(get(OVERVIEW_REPORT, GRADE_OVERVIEW_REPORT_VIEWED), User.getInstance());

		LOG_TYPES.put(get(PAGE, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		LOG_TYPES.put(get(PLAIN_TEXT_FILE, TXT_GRADE_EXPORTED), User.getInstance());

		LOG_TYPES.put(get(QUIZ, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(QUIZ, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(QUIZ, QUESTION_MANUALLY_GRADED), UserQuestionAttemptCmid.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_ATTEMPT_ABANDONED), UserAttemptCmid.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_ATTEMPT_DELETED), UserAttemptCmidAffected.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_ATTEMPT_PREVIEW_STARTED), UserAttemptAffectedCmid.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_ATTEMPT_REVIEWED), UserAttemptAffectedCmid.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_ATTEMPT_STARTED), UserAttemptCmid.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_ATTEMPT_SUBMITTED), UserAttemptCmid.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_ATTEMPT_SUMMARY_VIEWED), UserAttemptAffectedCmid.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_ATTEMPT_TIME_LIMIT_EXCEEDED), AttemptCmidUser.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_ATTEMPT_VIEWED), UserAttemptAffectedCmid.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_EDIT_PAGE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_OVERRIDE_CREATED), UserOverrideCmidGroup.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_OVERRIDE_DELETED), UserOverrideCmidGroup.getInstance());
		LOG_TYPES.put(get(QUIZ, QUIZ_REPORT_VIEWED), UserCmid.getInstance());

		LOG_TYPES.put(get(RECYCLE_BIN, ITEM_CREATED), Item.getInstance());
		LOG_TYPES.put(get(RECYCLE_BIN, ITEM_DELETED), Item.getInstance());
		LOG_TYPES.put(get(RECYCLE_BIN, ITEM_RESTORED), Item.getInstance());

		LOG_TYPES.put(get(SCORM_PACKAGE, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(SCORM_PACKAGE, SCO_LAUNCHED), UserScoCmid.getInstance());
		LOG_TYPES.put(get(SCORM_PACKAGE, SUBMITTED_SCORM_RAW_SCORE), UserAttemptCmid.getInstance());
		LOG_TYPES.put(get(SCORM_PACKAGE, SUBMITTED_SCORM_STATUS), UserAttemptCmid.getInstance());

		LOG_TYPES.put(get(SINGLE_VIEW, GRADE_SINGLE_VIEW_REPORT_VIEWED), User.getInstance());

		LOG_TYPES.put(get(STATISTICS, USER_STATISTICS_REPORT_VIEWED), UserAffected.getInstance());

		LOG_TYPES.put(get(SUBMISSION_COMMENTS, COMMENT_CREATED), UserCommentSubmissionCmid.getInstance());
		LOG_TYPES.put(get(SUBMISSION_COMMENTS, COMMENT_DELETED), UserCommentSubmissionCmid.getInstance());

		LOG_TYPES.put(get(SURVEY, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(SURVEY, SURVEY_REPORT_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(SURVEY, SURVEY_RESPONSE_SUBMITTED), UserCmid.getInstance());

		LOG_TYPES.put(get(SYSTEM, BADGE_LISTING_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, CALENDAR_EVENT_CREATED), UserCalendar.getInstance());
		LOG_TYPES.put(get(SYSTEM, CALENDAR_EVENT_DELETED), UserCalendar.getInstance());
		LOG_TYPES.put(get(SYSTEM, CALENDAR_EVENT_UPDATED), UserCalendar.getInstance());
		LOG_TYPES.put(get(SYSTEM, CALENDAR_SUBSCRIPTION_UPDATED), UserCalendar.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_ACTIVITY_COMPLETION_UPDATED), UserCmidAffected.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_BACKUP_CREATED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_COMPLETED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_COMPLETION_UPDATED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_CONTENT_DELETED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_CREATED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_MODULE_CREATED), UserCmid.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_MODULE_DELETED), UserCmid.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_MODULE_UPDATED), UserCmid.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_RESET_ENDED), Course.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_RESET_STARTED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_RESTORED), SystemCourseRestored.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_SECTION_CREATED), UserSectionCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_SECTION_DELETED), UserSectionCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_SECTION_UPDATED), UserSectionCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_SUMMARY_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_UPDATED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_USER_REPORT_VIEWED), UserCourseAffected.getInstance());
		LOG_TYPES.put(get(SYSTEM, COURSE_VIEWED), SystemCourseViewed.getInstance());
		LOG_TYPES.put(get(SYSTEM, ENROLMENT_INSTANCE_CREATED), UserAffected.getInstance());
		LOG_TYPES.put(get(SYSTEM, ENROLMENT_INSTANCE_UPDATED), UserAffected.getInstance());
		LOG_TYPES.put(get(SYSTEM, EVIDENCE_CREATED), UserEvidence.getInstance());
		LOG_TYPES.put(get(SYSTEM, GRADE_DELETED), UserGradeAffectedGradeitem.getInstance());
		LOG_TYPES.put(get(SYSTEM, GROUP_ASSIGNED_TO_GROUPING), UserGroupGrouping.getInstance());
		LOG_TYPES.put(get(SYSTEM, GROUP_CREATED), UserGroup.getInstance());
		LOG_TYPES.put(get(SYSTEM, GROUP_DELETED), UserGroup.getInstance());
		LOG_TYPES.put(get(SYSTEM, GROUP_MEMBER_ADDED), UserAffectedGroup.getInstance());
		LOG_TYPES.put(get(SYSTEM, GROUP_MEMBER_REMOVED), UserAffectedGroup.getInstance());
		LOG_TYPES.put(get(SYSTEM, GROUP_UPDATED), UserGroup.getInstance());
		LOG_TYPES.put(get(SYSTEM, GROUPING_CREATED), UserGrouping.getInstance());
		LOG_TYPES.put(get(SYSTEM, GROUPING_DELETED), UserGrouping.getInstance());
		LOG_TYPES.put(get(SYSTEM, NOTE_CREATED), UserNoteAffected.getInstance());
		LOG_TYPES.put(get(SYSTEM, NOTES_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, QUESTION_CATEGORY_CREATED), UserQuestioncategory.getInstance());
		LOG_TYPES.put(get(SYSTEM, RECENT_ACTIVITY_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, ROLE_ASSIGNED), UserRoleAffected.getInstance());
		LOG_TYPES.put(get(SYSTEM, ROLE_CAPABILITIES_UPDATED), UserRole.getInstance());
		LOG_TYPES.put(get(SYSTEM, ROLE_UNASSIGNED), UserRoleAffected.getInstance());
		LOG_TYPES.put(get(SYSTEM, SCALE_CREATED), UserScaleCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, TAG_ADDED_TO_AN_ITEM), Default.getInstance()); //TODO
		LOG_TYPES.put(get(SYSTEM, USER_COMPETENCY_RATED_IN_COURSE), Default.getInstance()); //TODO
		LOG_TYPES.put(get(SYSTEM, USER_COMPETENCY_VIEWED_IN_A_COURSE), UserCompetencyCourse.getInstance()); //TODO
		LOG_TYPES.put(get(SYSTEM, USER_ENROLLED_IN_COURSE), UserAffectedCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, USER_GRADED), UserGradeAffectedGradeitem.getInstance());
		LOG_TYPES.put(get(SYSTEM, USER_LIST_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, USER_PROFILE_VIEWED), UserAffectedCourse.getInstance());
		LOG_TYPES.put(get(SYSTEM, USER_UNENROLLED_FROM_COURSE), UserAffectedCourse.getInstance());

		LOG_TYPES.put(get(TURNITIN_ASSIGNMENT_2, ADD_SUBMISSION), Ignore.getInstance());
		LOG_TYPES.put(get(TURNITIN_ASSIGNMENT_2, LIST_SUBMISSIONS), Course.getInstance());

		LOG_TYPES.put(get(URL, COURSE_MODULE_VIEWED), UserCmid.getInstance());

		LOG_TYPES.put(get(USER_REPORT, GRADE_USER_REPORT_VIEWED), User.getInstance());

		LOG_TYPES.put(get(USER_TOURS, STEP_SHOWN), UserTour.getInstance()); 
		LOG_TYPES.put(get(USER_TOURS, TOUR_ENDED), UserTour.getInstance());
		LOG_TYPES.put(get(USER_TOURS, TOUR_STARTED), UserTour.getInstance());

		LOG_TYPES.put(get(WIKI, COMMENT_CREATED), UserCommentPageCmid.getInstance());
		LOG_TYPES.put(get(WIKI, COMMENTS_VIEWED), UserPageCmid.getInstance());
		LOG_TYPES.put(get(WIKI, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(WIKI, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(WIKI, WIKI_HISTORY_VIEWED), UserPageCmid.getInstance());
		LOG_TYPES.put(get(WIKI, WIKI_PAGE_CREATED), UserPageCmid.getInstance());
		LOG_TYPES.put(get(WIKI, WIKI_PAGE_LOCKS_DELETED), UserPageCmid.getInstance());
		LOG_TYPES.put(get(WIKI, WIKI_PAGE_MAP_VIEWED), UserPageCmid.getInstance());
		LOG_TYPES.put(get(WIKI, WIKI_PAGE_UPDATED), UserPageCmid.getInstance());
		LOG_TYPES.put(get(WIKI, WIKI_PAGE_VIEWED), UserPageCmid.getInstance());

		LOG_TYPES.put(get(WORKSHOP, A_SUBMISSION_HAS_BEEN_UPLOADED), UserSubmissionCmid.getInstance());
		LOG_TYPES.put(get(WORKSHOP, ASSESSMENT_EVALUATED), UserCmid.getInstance());
		LOG_TYPES.put(get(WORKSHOP, COURSE_MODULE_INSTANCE_LIST_VIEWED), UserCourse.getInstance());
		LOG_TYPES.put(get(WORKSHOP, COURSE_MODULE_VIEWED), UserCmid.getInstance());
		LOG_TYPES.put(get(WORKSHOP, PHASE_SWITCHED), UserCmid.getInstance());
		LOG_TYPES.put(get(WORKSHOP, SUBMISSION_ASSESSED), UserSubmissionAffectedCmid.getInstance());
		LOG_TYPES.put(get(WORKSHOP, SUBMISSION_CREATED), UserSubmissionCmid.getInstance());
		LOG_TYPES.put(get(WORKSHOP, SUBMISSION_DELETED), UserSubmissionCmid.getInstance());
		LOG_TYPES.put(get(WORKSHOP, SUBMISSION_REASSESSED), UserSubmissionAffectedCmid.getInstance());
		LOG_TYPES.put(get(WORKSHOP, SUBMISSION_UPDATED), UserSubmissionCmid.getInstance());
		LOG_TYPES.put(get(WORKSHOP, SUBMISSION_VIEWED), UserSubmissionCmid.getInstance());

		LOG_TYPES.put(get(XML_FILE, XML_GRADE_EXPORTED), User.getInstance());

	}

	public static Map<ComponentEvent, ReferencesLog> getLogTypes() {
		return LOG_TYPES;
	}
	
	
	private LogTypes() {
		throw new UnsupportedOperationException();
	}
}
