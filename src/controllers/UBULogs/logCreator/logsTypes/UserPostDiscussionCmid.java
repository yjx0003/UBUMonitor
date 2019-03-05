package controllers.UBULogs.logCreator.logsTypes;

import java.util.List;

import model.Log;
/**
 * 
 * The user with id '' has created the post with id '' in the discussion with id '' in the forum with course module id ''.
 * The user with id '' has updated the post with id '' in the discussion with id '' in the forum with course module id ''.
 * The user with id '' has posted content in the forum post with id '' in the discussion '' located in the forum with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserPostDiscussionCmid extends ReferencesLog {

	
	/**
	 * static Singleton instance.
	 */
	private static UserPostDiscussionCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserPostDiscussionCmid() {
	}

	/**
	 * Return a singleton instance of UserPostDiscussionCmid.
	 */
	public static UserPostDiscussionCmid getInstance() {
		if (instance == null) {
			instance = new UserPostDiscussionCmid();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		// TODO Auto-generated method stub

	}

}
