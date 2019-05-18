package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * The user with id '' created the question category with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserQuestioncategory extends ReferencesLog {

	
	/**
	 * static Singleton instance.
	 */
	private static UserQuestioncategory instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserQuestioncategory() {
	}

	/**
	 * Return a singleton instance of UserQuestioncategory.
	 */
	public static UserQuestioncategory getInstance() {
		if (instance == null) {
			instance = new UserQuestioncategory();
		}
		return instance;
	}
	
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//question category id
		
	}

}
