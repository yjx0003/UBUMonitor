package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;


/**
 * 
 * The user with id '' has added the option with id '' for the\n            user with id '' from the choice activity with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserOptionAffectedCmid extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserOptionAffectedCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserOptionAffectedCmid() {
	}

	/**
	 * Return a singleton instance of UserOptionAffectedCmid.
	 */
	public static UserOptionAffectedCmid getInstance() {
		if (instance == null) {
			instance = new UserOptionAffectedCmid();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//TODO option id
		setAffectedUserById(log, ids.get(2));
		setCourseModuleById(log, ids.get(3));

	}

}
