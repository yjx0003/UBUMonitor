package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * "The user with id '' viewed the user log report for the user with id ''."
 * @author Yi Peng Ji
 *
 */
public class UserAffected extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserAffected instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserAffected() {
	}

	/**
	 * Return a singleton instance of UserAffected.
	 */
	public static UserAffected getInstance() {
		if (instance == null) {
			instance = new UserAffected();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		setAffectedUserById(log, ids.get(1));
	}

}
