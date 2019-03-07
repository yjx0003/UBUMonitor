package controllers.UBULogs.logCreator.logsTypes;

import java.util.List;

import model.Log;
/**
 * The user with id '' updated the completion state for the course module with id '' for the user with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserCmidAffected extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserCmidAffected instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserCmidAffected() {
	}

	/**
	 * Return a singleton instance of UserCmidAffected.
	 */
	public static UserCmidAffected getInstance() {
		if (instance == null) {
			instance = new UserCmidAffected();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		// TODO Auto-generated method stub
		
	}

}