package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * The quiz attempt with id '' belonging to the quiz with course module id '' for the user with id '' became overdue.
 * @author Yi Peng Ji
 *
 */
public class AttemptCmidUser extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static AttemptCmidUser instance;

	/**
	 * Private constructor for singleton.
	 */
	private AttemptCmidUser() {
	}

	/**
	 * Return a singleton instance of AttemptCmidUser.
	 */
	public static AttemptCmidUser getInstance() {
		if (instance == null) {
			instance = new AttemptCmidUser();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		//attempt id
		setCourseModuleById(log, ids.get(1));
		setUserById(log, ids.get(2));
	}

}
