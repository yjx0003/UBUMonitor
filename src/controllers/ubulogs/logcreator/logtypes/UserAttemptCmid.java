package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;

/**
 * The user with id '' has had their attempt with id '' marked as abandoned for the quiz with course module id ''.
 * The user with id '' has started the attempt with id '' for the quiz with course module id ''.
 * The user with id '' has submitted the attempt with id '' for the quiz with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserAttemptCmid extends ReferencesLog {
	/**
	 * static Singleton instance.
	 */
	private static UserAttemptCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserAttemptCmid() {
	}

	/**
	 * Return a singleton instance of UserAttemptCmid.
	 */
	public static UserAttemptCmid getInstance() {
		if (instance == null) {
			instance = new UserAttemptCmid();
		}
		return instance;
	}

	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//TODO Habria que usar la funcion mod_quiz_get_user_attempts de moodle para recoger las ids de los intentos de quiz
		setCourseModuleById(log, ids.get(2));

	}

}
