package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;

/**
 * The user with id '' exported grades using the xls export in the gradebook.
 * The user with id '' viewed the grader report in the gradebook.
 * The user with id '' exported grades using the ods export in the gradebook.
 * The user with id '' viewed the overview report in the gradebook.
 * The user with id '' exported grades using the txt export in the gradebook.
 * The user with id '' viewed the singleview report in the gradebook.
 * The user with id '' viewed the user report in the gradebook.
 * 
 * @author Yi Peng Ji
 *
 */
public class User extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static User instance;

	/**
	 * Private constructor for singleton.
	 */
	private User() {
	}

	/**
	 * Return a singleton instance of User.
	 */
	public static User getInstance() {
		if (instance == null) {
			instance = new User();
		}
		return instance;
	}
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));

	}

}
