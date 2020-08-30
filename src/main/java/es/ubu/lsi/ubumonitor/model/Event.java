package es.ubu.lsi.ubumonitor.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa la columna de Nombre de evento en la tabla de logs.
 * Se enumeran todos los eventos posibles.
 * @author Yi Peng Ji
 *
 */
public enum Event {
	
	
	
	
	ACTIVITY_REPORT_VIEWED("Activity report viewed"),
	ADD_SUBMISSION("Add Submission"),
	ALL_THE_SUBMISSIONS_ARE_BEING_DOWNLOADED("All the submissions are being downloaded."),
	AN_EXTENSION_HAS_BEEN_GRANTED("An extension has been granted."),
	AN_ONLINE_TEXT_HAS_BEEN_UPLOADED("An online text has been uploaded."),
	ASSESSMENT_EVALUATED("Assessment evaluated"),
	ASSIGNMENT_DETAILS_VIEWED("Assignment details viewed"),
	ASSIGNMENT_SUBMITTED("Assignment submitted"),
	ASSIGNMENT_OVERRIDE_CREATED("Assignment override created"),
	ASSIGNMENT_OVERRIDE_DELETED("Assignment override deleted"),
	ASSIGNMENT_OVERRIDE_UPDATED("Assignment override updated"),
	A_FILE_HAS_BEEN_UPLOADED("A file has been uploaded."),
	A_SUBMISSION_HAS_BEEN_SUBMITTED("A submission has been submitted."),
	A_SUBMISSION_HAS_BEEN_UPLOADED("A submission has been uploaded."),
	BADGE_LISTING_VIEWED("Badge listing viewed"),
	BIBLIOGRAPHY_CREATED("Bibliography created"),
	BIBLIOGRAPHY_DELETED("Bibliography deleted"),
	BIBLIOGRAPHY_MODIFIED("Bibliography modified"),
	BOOK_PRINTED("Book printed"),
	CALENDAR_EVENT_CREATED("Calendar event created"),
	CALENDAR_EVENT_DELETED("Calendar event deleted"),
	CALENDAR_EVENT_UPDATED("Calendar event updated"),
	CALENDAR_SUBSCRIPTION_UPDATED("Calendar subscription updated"),
	CATEGORY_HAS_BEEN_CREATED("Category has been created"),
	CHAPTER_CREATED("Chapter created"),
	CHAPTER_UPDATED("Chapter updated"),
	CHAPTER_VIEWED("Chapter viewed"),
	CHOICE_ANSWER_ADDED("Choice answer added"),
	CHOICE_MADE("Choice made"),
	CHOICE_REPORT_VIEWED("Choice report viewed"),
	COMMENTS_VIEWED("Comments viewed"),
	COMMENT_CREATED("Comment created"),
	COMMENT_DELETED("Comment deleted"),
	COMPLETION_REPORT_VIEWED("Completion report viewed"),
	CONTENT_PAGE_VIEWED("Content page viewed"),
	COURSE_ACTIVITY_COMPLETION_UPDATED("Course activity completion updated"),
	COURSE_BACKUP_CREATED("Course backup created"),
	COURSE_COMPLETED("Course completed"),
	COURSE_COMPLETION_UPDATED("Course completion updated"),
	COURSE_CONTENT_DELETED("Course content deleted"),
	COURSE_CREATED("Course created"),
	COURSE_MODULE_CREATED("Course module created"),
	COURSE_MODULE_DELETED("Course module deleted"),
	COURSE_MODULE_INSTANCE_LIST_VIEWED("Course module instance list viewed"),
	COURSE_MODULE_UPDATED("Course module updated"),
	COURSE_MODULE_VIEWED("Course module viewed"),
	COURSE_RESET_ENDED("Course reset ended"),
	COURSE_RESET_STARTED("Course reset started"),
	COURSE_RESTORED("Course restored"),
	COURSE_SEARCHED("Course searched"),
	COURSE_SECTION_CREATED("Course section created"),
	COURSE_SECTION_DELETED("Course section deleted"),
	COURSE_SECTION_UPDATED("Course section updated"),
	COURSE_SUMMARY_VIEWED("Course summary viewed"),
	COURSE_UPDATED("Course updated"),
	COURSE_USER_REPORT_VIEWED("Course user report viewed"),
	COURSE_VIEWED("Course viewed"),
	DISCUSSION_CREATED("Discussion created"),
	DISCUSSION_DELETED("Discussion deleted"),
	DISCUSSION_MOVED("Discussion moved"),
	DISCUSSION_PINNED("Discussion pinned"),
	DISCUSSION_SUBSCRIPTION_CREATED("Discussion subscription created"),
	DISCUSSION_SUBSCRIPTION_DELETED("Discussion subscription deleted"),
	DISCUSSION_VIEWED("Discussion viewed"),
	ENROLMENT_INSTANCE_CREATED("Enrolment instance created"),
	ENROLMENT_INSTANCE_UPDATED("Enrolment instance updated"),
	ENTRY_HAS_BEEN_CREATED("Entry has been created"),
	ENTRY_HAS_BEEN_UPDATED("Entry has been updated"),
	ENTRY_HAS_BEEN_VIEWED("Entry has been viewed"),
	EVALUATION_SYSTEM_CREATED("Evaluation system created"),
	EVALUATION_SYSTEM_MODIFIED("Evaluation system modified"),
	EVENT_CREATED_BLOCK_INSTANCE("Event created block instance"),
	EVENT_DELETED_BLOCK_INSTANCE("Event deleted block instance"),
	EVIDENCE_CREATED("Evidence created."),
	FEDBACK_VIEWED("Feedback viewed"),
	FIELD_CREATED("Field created"),
	FIELD_UPDATED("Field updated"),
	FOLDER_UPDATED("Folder updated"),
	GRADER_REPORT_VIEWED("Grader report viewed"),
	GRADE_DELETED("Grade deleted"),
	GRADE_HISTORY_REPORT_VIEWED("Grade history report viewed"),
	GRADE_OUTCOMES_REPORT_VIEWED("Grade outcomes report viewed"),
	GRADE_OVERVIEW_REPORT_VIEWED("Grade overview report viewed"),
	GRADE_SINGLE_VIEW_REPORT_VIEWED("Grade single view report viewed."),
	GRADE_SUBMISSIONS_PAGE_VIEWED("Grade submissions page viewed"),
	GRADE_USER_REPORT_VIEWED("Grade user report viewed"),
	GRADING_FORM_VIEWED("Grading form viewed"),
	GRADING_TABLE_VIEWED("Grading table viewed"),
	GROUPING_CREATED("Grouping created"),
	GROUPING_DELETED("Grouping deleted"),
	GROUP_ASSIGNED_TO_GROUPING("Group assigned to grouping"),
	GROUP_CREATED("Group created"),
	GROUP_DELETED("Group deleted"),
	GROUP_MEMBER_ADDED("Group member added"),
	GROUP_MEMBER_REMOVED("Group member removed"),
	GROUP_UPDATED("Group updated"),
	H5P_CONTENT_VIEWED("H5P content viewed"),
	HOTPOT_ATTEMPT_STARTED("HotPot attempt started"),
	HOTPOT_ATTEMPT_SUBMITTED("HotPot attempt submitted"),
	ITEM_CREATED("Item created"),
	ITEM_DELETED("Item deleted"),
	ITEM_RESTORED("Item restored"),
	JOURNAL_ENTRIES_VIEWED("Journal entries viewed"),
	JOURNAL_ENTRY_CREATED("Journal entry created"),
	JOURNAL_ENTRY_UPDATED("Journal entry updated"),
	LESSON_ENDED("Lesson ended"),
	LESSON_STARTED("Lesson started"),
	LESSON_RESTARTED("Lesson restarted"),
	LESSON_RESUMED("Lesson resumed"),
	LIST_SUBMISSIONS("List Submissions"),
	LIVE_LOG_REPORT_VIEWED("Live log report viewed"),
	LOG_REPORT_VIEWED("Log report viewed"),
	MESSAGE_SENT("Message sent"),
	NOTES_VIEWED("Notes viewed"),
	NOTE_CREATED("Note created"),
	OBJECTIVE_CREATED("Objective created"),
	OPENDOCUMENT_GRADE_EXPORTED("OpenDocument grade exported"),
	OUTLINE_REPORT_VIEWED("Outline report viewed"),
	OUTSTANDING_RATINGS_MODIFIED("Outstanding ratings modified"),
	PAGE_CREATED("Page created"),
	PAGE_MOVED("Page moved"),
	PAGE_UPDATED("Page updated"),
	PARTICIPATION_REPORT_VIEWED("Participation report viewed"),
	PHASE_SWITCHED("Phase switched"),
	POST_CREATED("Post created"),
	POST_DELETED("Post deleted"),
	POST_UPDATED("Post updated"),
	QUESTION_ANSWERED("Question answered"),
	QUESTION_CATEGORY_CREATED("Question category created"),
	QUESTION_CATEGORY_VIEWED("Question category viewed"),
	QUESTION_CREATED("Question created"),
	QUESTION_MANUALLY_GRADED("Question manually graded"),
	QUESTION_MOVED("Question moved"),
	QUESTION_UPDATED("Question updated"),
	QUESTION_VIEWED("Question viewed"),
	QUESTIONS_IMPORTED("Questions imported"),
	QUIZ_ATTEMPT_ABANDONED("Quiz attempt abandoned"),
	QUIZ_ATTEMPT_DELETED("Quiz attempt deleted"),
	QUIZ_ATTEMPT_PREVIEW_STARTED("Quiz attempt preview started"),
	QUIZ_ATTEMPT_REVIEWED("Quiz attempt reviewed"),
	QUIZ_ATTEMPT_STARTED("Quiz attempt started"),
	QUIZ_ATTEMPT_SUBMITTED("Quiz attempt submitted"),
	QUIZ_ATTEMPT_SUMMARY_VIEWED("Quiz attempt summary viewed"),
	QUIZ_ATTEMPT_TIME_LIMIT_EXCEEDED("Quiz attempt time limit exceeded"),
	QUIZ_ATTEMPT_VIEWED("Quiz attempt viewed"),
	QUIZ_EDIT_PAGE_VIEWED("Quiz edit page viewed"),
	QUIZ_OVERRIDE_CREATED("Quiz override created"),
	QUIZ_OVERRIDE_DELETED("Quiz override deleted"),
	QUIZ_OVERRIDE_UPDATED("Quiz override updated"),
	QUIZ_REPORT_VIEWED("Quiz report viewed"),
	RECENT_ACTIVITY_VIEWED("Recent activity viewed"),
	RECORD_CREATED("Record created"),
	RECORD_DELETED("Record deleted"),
	RECORD_UPDATED("Record updated"),
	REMOVE_SUBMISSION_CONFIRMATION_VIEWED("Remove submission confirmation viewed."),
	RESPONSE_SUBMITTED("Response submitted"),
	ROLE_ASSIGNED("Role assigned"),
	ROLE_CAPABILITIES_UPDATED("Role capabilities updated"),
	ROLE_UNASSIGNED("Role unassigned"),
	RULE_CREATED("Rule created"),
	SCALE_CREATED("Scale created"),
	SCO_LAUNCHED("Sco launched"),
	SESSIONS_VIEWED("Sessions viewed"),
	SINGLE_SUBMISSION_PAGE_VIEWED("Single submission page viewed"),
	SOME_CONTENT_HAS_BEEN_POSTED("Some content has been posted."),
	STEP_SHOWN("Step shown"),
	SUBJECT_DELETED("Subject deleted"),
	SUBJECT_MODIFIED("Subject modified"),
	SUBMISSION_ASSESSED("Submission assessed"),
	SUBMISSION_CONFIRMATION_FORM_VIEWED("Submission confirmation form viewed."),
	SUBMISSION_CREATED("Submission created"),
	SUBMISSION_CREATED2("Submission created."),
	SUBMISSION_DELETED("Submission deleted"),
	SUBMISSION_FORM_VIEWED("Submission form viewed."),
	SUBMISSION_REASSESSED("Submission re-assessed"),
	SUBMISSION_UPDATED("Submission updated"),
	SUBMISSION_UPDATED2("Submission updated."),
	SUBMISSION_VIEWED("Submission viewed"),
	SUBMITTED_SCORM_RAW_SCORE("Submitted SCORM raw score"),
	SUBMITTED_SCORM_STATUS("Submitted SCORM status"),
	SUBSCRIBERS_VIEWED("Subscribers viewed"),
	SUBSCRIPTION_CREATED("Subscription created"),
	SUBSCRIPTION_DELETED("Subscription deleted"),
	SURVEY_REPORT_VIEWED("Survey report viewed"),
	SURVEY_RESPONSE_SUBMITTED("Survey response submitted"),
	TAG_ADDED_TO_AN_ITEM("Tag added to an item"),
	TEMPLATES_VIEWED("Templates viewed"),
	TEMPLATE_UPDATED("Template updated"),
	THE_STATE_OF_THE_WORKFLOW_HAS_BEEN_UPDATED("The state of the workflow has been updated."),
	THE_STATUS_OF_THE_SUBMISSION_HAS_BEEN_UPDATED("The status of the submission has been updated."),
	THE_STATUS_OF_THE_SUBMISSION_HAS_BEEN_VIEWED("The status of the submission has been viewed."),
	THE_SUBMISSIONS_HAVE_BEEN_LOCKED_FOR_A_USER("The submissions have been locked for a user."),
	THE_SUBMISSIONS_HAVE_BEEN_UNLOCKED_FOR_A_USER("The submissions have been unlocked for a user."),
	THE_SUBMISSION_HAS_BEEN_GRADED("The submission has been graded."),
	THE_USER_HAS_ACCEPTED_THE_STATEMENT_OF_THE_SUBMISSION("The user has accepted the statement of the submission."),
	TOUR_ENDED("Tour ended"),
	TOUR_STARTED("Tour started"),
	TXT_GRADE_EXPORTED("TXT grade exported"),
	UNIT_MODIFIED("Unit modified"),
	USER_COMPETENCY_RATED_IN_COURSE("User competency rated in course."),
	USER_COMPETENCY_VIEWED_IN_A_COURSE("User competency viewed in a course."),
	USER_ENROLLED_IN_COURSE("User enrolled in course"),
	USER_GRADED("User graded"),
	USER_LIST_VIEWED("User list viewed"),
	USER_LOG_REPORT_VIEWED("User log report viewed"),
	USER_PROFILE_VIEWED("User profile viewed"),
	USER_REPORT_VIEWED("User report viewed"),
	USER_STATISTICS_REPORT_VIEWED("User statistics report viewed"),
	USER_UNENROLLED_FROM_COURSE("User unenrolled from course"),
	VIDEO_RESOURCE_VIEWED("Video resource viewed"),
	WIKI_HISTORY_VIEWED("Wiki history viewed"),
	WIKI_PAGE_CREATED("Wiki page created"),
	WIKI_PAGE_LOCKS_DELETED("Wiki page locks deleted"),
	WIKI_PAGE_MAP_VIEWED("Wiki page map viewed"),
	WIKI_PAGE_UPDATED("Wiki page updated"),
	WIKI_PAGE_VIEWED("Wiki page viewed"),
	XLS_GRADE_EXPORTED("XLS grade exported"),
	XML_GRADE_EXPORTED("XML grade exported"),
	ZIP_ARCHIVE_OF_FOLDER_DOWNLOADED("Zip archive of folder downloaded"),
	EVENT_NOT_AVAILABLE("Event not avaible");


	
	
	
	private String name;
	private static Map<String, Event> map;

	Event(String name) {
		this.name = name;
	}

	/**
	 * Creamos una mapa inverso, la key es el texto en String y el value el elemento
	 * de la enumeración
	 */
	static {
		map = new HashMap<>();
		for (Event eventName : Event.values()) {
			map.put(eventName.name, eventName);
		}
	}

	/**
	 * Devuelve el elemento Event de enum o {@link Event#EVENT_NOT_AVAILABLE} si no
	 * existe
	 * 
	 * @param name
	 *            nombre del evento
	 * @return elemento Event de enum o {@link Event#EVENT_NOT_AVAILABLE} si no
	 *         existe
	 */
	public static Event get(String name) {
		return map.getOrDefault(name, EVENT_NOT_AVAILABLE);
	}
	
	/**
	 * Devuelve el texto del evento tal y como esta en la tabla de logs.
	 * @return el texto del evento tal y como esta en la tabla de logs
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}


}
