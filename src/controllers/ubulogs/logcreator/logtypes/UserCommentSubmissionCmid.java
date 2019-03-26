package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * The user with id '' added the comment with id '' to the submission with id '' for the assignment with course module id ''.
 * The user with id '' deleted the comment with id '' from the submission with id '' for the assignment with course module id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserCommentSubmissionCmid extends ReferencesLog{
	
	/**
	 * static Singleton instance.
	 */
	private static UserCommentSubmissionCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserCommentSubmissionCmid() {
	}

	/**
	 * Return a singleton instance of UserCommentSubmissionCmid.
	 */
	public static UserCommentSubmissionCmid getInstance() {
		if (instance == null) {
			instance = new UserCommentSubmissionCmid();
		}
		return instance;
	}

	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//TODO core_comment_get_comments
		//TODO mod_assign_get_submissions
		setCourseModuleById(log, ids.get(3));
		
	}

}
