package controllers.UBULogs.logCreator.logsTypes;

import java.util.List;

import model.Log;
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
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		// TODO Auto-generated method stub
		
	}

}
