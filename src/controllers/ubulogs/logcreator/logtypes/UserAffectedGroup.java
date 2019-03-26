package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * The user with id '' added the user with id '' to the group with id ''.
 * The user with id '' removed the user with id '' to the group with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserAffectedGroup extends ReferencesLog {
	
	
	/**
	 * static Singleton instance.
	 */
	private static UserAffectedGroup instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserAffectedGroup() {
	}

	/**
	 * Return a singleton instance of UserAffectedGroup.
	 */
	public static UserAffectedGroup getInstance() {
		if (instance == null) {
			instance = new UserAffectedGroup();
		}
		return instance;
	}

	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		setAffectedUserById(log, ids.get(1));
		
	}

}
