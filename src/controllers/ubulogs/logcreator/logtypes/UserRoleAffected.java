package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;


/**
 * 
 * The user with id '' assigned the role with id '' to the user with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserRoleAffected extends ReferencesLog {

	
	/**
	 * static Singleton instance.
	 */
	private static UserRoleAffected instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserRoleAffected() {
	}

	/**
	 * Return a singleton instance of UserRoleAffected.
	 */
	public static UserRoleAffected getInstance() {
		if (instance == null) {
			instance = new UserRoleAffected();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//TODO role id
		setAffectedUserById(log, ids.get(2));
	}

}
