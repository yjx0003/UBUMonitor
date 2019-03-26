package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;


/**
 * The user with id '' created a file submission and uploaded '' file/s in the assignment with course module id ''.
 * The user with id '' updated a file submission and uploaded '' file/s in the assignment with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserFilesCmid extends ReferencesLog {
	
	/**
	 * static Singleton instance.
	 */
	private static UserFilesCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserFilesCmid() {
	}

	/**
	 * Return a singleton instance of UserFilesCmid.
	 */
	public static UserFilesCmid getInstance() {
		if (instance == null) {
			instance = new UserFilesCmid();
		}
		return instance;
	}

	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//number of files ids.get(1);
		setCourseModuleById(log, ids.get(2));

	}

}
