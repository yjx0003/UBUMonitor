package controllers.UBULogs.logCreator.logsTypes;

import java.util.List;

import model.Log;


/**
 * 
 * 
 * The user with id '' has submitted the submission with id '' for the assignment with course module id ''.
 * The user with id '' has updated the status of the submission with id '' for the assignment with course module id '' to the status 'draft'.
 * The user with id '' has accepted the statement of the submission with id '' for the assignment with course module id ''.
 * The user with id '' has uploaded a file to the submission with id '' in the assignment activity with course module id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserSubmissionCmid extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserSubmissionCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserSubmissionCmid() {
	}

	/**
	 * Return a singleton instance of UserSubmissionCmid.
	 */
	public static UserSubmissionCmid getInstance() {
		if (instance == null) {
			instance = new UserSubmissionCmid();
		}
		return instance;
	}
	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		// TODO Auto-generated method stub

	}

}
