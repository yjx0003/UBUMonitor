package controllers.ubulogs.logcreator;

import java.util.HashMap;
import java.util.Map;

public enum Event {
	
	EVENT_NOT_AVAILABLE("Event not avaible"),
	
	
	ACTIVITY_REPORT_VIEWED("Activity report viewed"),
	ADD_SUBMISSION("Add Submission"),
	ALL_THE_SUBMISSIONS_ARE_BEING_DOWNLOADED("All the submissions are being downloaded."),
	AN_ONLINE_TEXT_HAS_BEEN_UPLOADED("An online text has been uploaded."),
	ASSIGNMENT_OVERRIDE_CREATED("Assignment override created"),
	ASSIGNMENT_OVERRIDE_DELETED("Assignment override deleted"),
	ASSIGNMENT_OVERRIDE_UPDATED("Assignment override updated"),
	A_FILE_HAS_BEEN_UPLOADED("A file has been uploaded."),
	A_SUBMISSION_HAS_BEEN_SUBMITTED("A submission has been submitted."),
	BADGE_LISTING_VIEWED("Badge listing viewed"),
	CALENDAR_EVENT_CREATED("Calendar event created"),
	CALENDAR_EVENT_DELETED("Calendar event deleted"),
	CALENDAR_EVENT_UPDATED("Calendar event updated"),
	CHOICE_ANSWER_ADDED("Choice answer added"),
	CHOICE_REPORT_VIEWED("Choice report viewed"),
	COMMENT_CREATED("Comment created"),
	COMMENT_DELETED("Comment deleted"),
	COURSE_ACTIVITY_COMPLETION_UPDATED("Course activity completion updated"),
	COURSE_BACKUP_CREATED("Course backup created"),
	COURSE_CREATED("Course created"),
	COURSE_MODULE_CREATED("Course module created"),
	COURSE_MODULE_DELETED("Course module deleted"),
	COURSE_MODULE_INSTANCE_LIST_VIEWED("Course module instance list viewed"),
	COURSE_MODULE_UPDATED("Course module updated"),
	COURSE_MODULE_VIEWED("Course module viewed"),
	COURSE_RESET_ENDED("Course reset ended"),
	COURSE_RESET_STARTED("Course reset started"),
	COURSE_SEARCHED("Course searched"),
	COURSE_SECTION_CREATED("Course section created"),
	COURSE_SECTION_UPDATED("Course section updated"),
	COURSE_SUMMARY_VIEWED("Course summary viewed"),
	COURSE_UPDATED("Course updated"),
	COURSE_USER_REPORT_VIEWED("Course user report viewed"),
	COURSE_VIEWED("Course viewed"),
	DISCUSSION_CREATED("Discussion created"),
	DISCUSSION_DELETED("Discussion deleted"),
	DISCUSSION_MOVED("Discussion moved"),
	DISCUSSION_SUBSCRIPTION_CREATED("Discussion subscription created"),
	DISCUSSION_VIEWED("Discussion viewed"),
	ENROLMENT_INSTANCE_CREATED("Enrolment instance created"),
	ENTRY_HAS_BEEN_CREATED("Entry has been created"),
	ENTRY_HAS_BEEN_UPDATED("Entry has been updated"),
	ENTRY_HAS_BEEN_VIEWED("Entry has been viewed"),
	EVALUATION_SYSTEM_CREATED("Evaluation system created"),
	EVALUATION_SYSTEM_MODIFIED("Evaluation system modified"),
	FOLDER_UPDATED("Folder updated"),
	GRADER_REPORT_VIEWED("Grader report viewed"),
	GRADE_DELETED("Grade deleted"),
	GRADE_OUTCOMES_REPORT_VIEWED("Grade outcomes report viewed"),
	GRADE_OVERVIEW_REPORT_VIEWED("Grade overview report viewed"),
	GRADE_SINGLE_VIEW_REPORT_VIEWED("Grade single view report viewed."),
	GRADE_USER_REPORT_VIEWED("Grade user report viewed"),
	GRADING_FORM_VIEWED("Grading form viewed"),
	GRADING_TABLE_VIEWED("Grading table viewed"),
	GROUPING_CREATED("Grouping created"),
	GROUP_CREATED("Group created"),
	GROUP_DELETED("Group deleted"),
	GROUP_MEMBER_ADDED("Group member added"),
	GROUP_MEMBER_REMOVED("Group member removed"),
	HOTPOT_ATTEMPT_STARTED("HotPot attempt started"),
	HOTPOT_ATTEMPT_SUBMITTED("HotPot attempt submitted"),
	ITEM_CREATED("Item created"),
	ITEM_DELETED("Item deleted"),
	LIST_SUBMISSIONS("List Submissions"),
	LIVE_LOG_REPORT_VIEWED("Live log report viewed"),
	LOG_REPORT_VIEWED("Log report viewed"),
	MESSAGE_SENT("Message sent"),
	OPENDOCUMENT_GRADE_EXPORTED("OpenDocument grade exported"),
	OUTLINE_REPORT_VIEWED("Outline report viewed"),
	OUTSTANDING_RATINGS_MODIFIED("Outstanding ratings modified"),
	PARTICIPATION_REPORT_VIEWED("Participation report viewed"),
	POST_CREATED("Post created"),
	POST_DELETED("Post deleted"),
	POST_UPDATED("Post updated"),
	QUESTION_CATEGORY_CREATED("Question category created"),
	QUESTION_MANUALLY_GRADED("Question manually graded"),
	QUIZ_ATTEMPT_ABANDONED("Quiz attempt abandoned"),
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
	QUIZ_REPORT_VIEWED("Quiz report viewed"),
	RECENT_ACTIVITY_VIEWED("Recent activity viewed"),
	RESPONSE_SUBMITTED("Response submitted"),
	ROLE_ASSIGNED("Role assigned"),
	ROLE_UNASSIGNED("Role unassigned"),
	SCALE_CREATED("Scale created"),
	SESSIONS_VIEWED("Sessions viewed"),
	SOME_CONTENT_HAS_BEEN_POSTED("Some content has been posted."),
	STEP_SHOWN("Step shown"),
	SUBJECT_MODIFIED("Subject modified"),
	SUBMISSION_CONFIRMATION_FORM_VIEWED("Submission confirmation form viewed."),
	SUBMISSION_CREATED("Submission created."),
	SUBMISSION_FORM_VIEWED("Submission form viewed."),
	SUBMISSION_UPDATED("Submission updated."),
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
	USER_ENROLLED_IN_COURSE("User enrolled in course"),
	USER_GRADED("User graded"),
	USER_LIST_VIEWED("User list viewed"),
	USER_LOG_REPORT_VIEWED("User log report viewed"),
	USER_PROFILE_VIEWED("User profile viewed"),
	USER_REPORT_VIEWED("User report viewed"),
	USER_UNENROLLED_FROM_COURSE("User unenrolled from course"),
	XLS_GRADE_EXPORTED("XLS grade exported"),
	XML_GRADE_EXPORTED("XML grade exported"),
	ZIP_ARCHIVE_OF_FOLDER_DOWNLOADED("Zip archive of folder downloaded");

	
	private String name;
	private static Map<String,Event> map;
	
	Event(String name){
		this.name=name;
	}
	
	static {
		map=new HashMap<String,Event>();
		for(Event eventName:Event.values()) {
			map.put(eventName.name, eventName);
		}
	}
	
	public static Event get(String name) {
		return map.getOrDefault(name,EVENT_NOT_AVAILABLE);
	}
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
