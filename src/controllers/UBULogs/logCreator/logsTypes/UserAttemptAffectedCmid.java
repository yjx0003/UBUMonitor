package controllers.UBULogs.logCreator.logsTypes;

import java.util.List;

import model.Log;
/**
 * The user with id '' has had their attempt with id '' reviewed by the user with id '' for the quiz with course module id ''.
 * The user with id '' has viewed the summary for the attempt with id '' belonging to the user with id '' for the quiz with course module id ''.
 * The user with id '' has viewed the attempt with id '' belonging to the user with id '43999' for the quiz with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserAttemptAffectedCmid extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserAttemptAffectedCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserAttemptAffectedCmid() {
	}

	/**
	 * Return a singleton instance of UserAttemptAffectedCmid.
	 */
	public static UserAttemptAffectedCmid getInstance() {
		if (instance == null) {
			instance = new UserAttemptAffectedCmid();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		// TODO Auto-generated method stub

	}

}
