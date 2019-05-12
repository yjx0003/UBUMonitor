package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * The user with id '' created the grouping with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserGrouping extends ReferencesLog {

	
	/**
	 * static Singleton instance.
	 */
	private static UserGrouping instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserGrouping() {
	}

	/**
	 * Return a singleton instance of UserGrouping.
	 */
	public static UserGrouping getInstance() {
		if (instance == null) {
			instance = new UserGrouping();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//TODO grouping id

	}

}
