package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * The user with id '' created an online text submission with '' words in the assignment with course module id ''.
 * The user with id '' updated an online text submission with '' words in the assignment with course module id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserWordsCmid extends ReferencesLog {

	
	/**
	 * static Singleton instance.
	 */
	private static UserWordsCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserWordsCmid() {
	}

	/**
	 * Return a singleton instance of UserWordsCmid.
	 */
	public static UserWordsCmid getInstance() {
		if (instance == null) {
			instance = new UserWordsCmid();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//number of words
		setCourseModuleById(log, ids.get(2));
		
	}

}
