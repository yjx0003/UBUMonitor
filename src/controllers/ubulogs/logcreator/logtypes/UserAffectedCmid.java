package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.Log;
/**
 * 
 * The user with id '' viewed the grading form for the user with id '' for the assignment with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserAffectedCmid extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserAffectedCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserAffectedCmid() {
	}

	/**
	 * Return a singleton instance of UserAffectedCmid.
	 */
	public static UserAffectedCmid getInstance() {
		if (instance == null) {
			instance = new UserAffectedCmid();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		setAffectedUserById(log, ids.get(1));
		setCourseModuleById(log, ids.get(2));
	}

}
