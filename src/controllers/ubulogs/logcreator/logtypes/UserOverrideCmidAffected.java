package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;


/**
 * 
 * The user with id '' created the override with id '' for the assign with course module id '' for the user with id ''.
 * The user with id '' deleted the override with id '' for the assign with course module id '' for the user with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserOverrideCmidAffected extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserOverrideCmidAffected instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserOverrideCmidAffected() {
	}

	/**
	 * Return a singleton instance of UserOverrideCmidAffected.
	 */
	public static UserOverrideCmidAffected getInstance() {
		if (instance == null) {
			instance = new UserOverrideCmidAffected();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//TODO override id
		setCourseModuleById(log, ids.get(1));
		setAffectedUserById(log, ids.get(2));

	}

}
