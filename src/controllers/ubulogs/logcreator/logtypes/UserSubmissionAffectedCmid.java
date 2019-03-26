package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;

/**
 * 
 * The user with id '' has graded the submission '' for the user with id '' for the assignment with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserSubmissionAffectedCmid extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserSubmissionAffectedCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserSubmissionAffectedCmid() {
	}

	/**
	 * Return a singleton instance of UserSubmissionAffectedCmid.
	 */
	public static UserSubmissionAffectedCmid getInstance() {
		if (instance == null) {
			instance = new UserSubmissionAffectedCmid();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//submission
		setAffectedUserById(log, ids.get(2));
		setCourseModuleById(log, ids.get(3));
		
	}

}
