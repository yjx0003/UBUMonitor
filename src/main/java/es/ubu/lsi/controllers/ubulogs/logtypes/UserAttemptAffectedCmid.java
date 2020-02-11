package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;
/**
 * The user with id '' has had their attempt with id '' reviewed by the user with id '' for the quiz with course module id ''.
 * The user with id '' has viewed the summary for the attempt with id '' belonging to the user with id '' for the quiz with course module id ''.
 * The user with id '' has viewed the attempt with id '' belonging to the user with id '' for the quiz with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserAttemptAffectedCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserAttemptAffectedCmid.
	 */
	private static UserAttemptAffectedCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserAttemptAffectedCmid() {
	}

	/**
	 * Devuelve la instancia única de UserAttemptAffectedCmid.
	 * @return instancia singleton
	 */
	public static UserAttemptAffectedCmid getInstance() {
		if (instance == null) {
			instance = new UserAttemptAffectedCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//attempt id mod_quiz_get_user_attempts
		setAffectedUserById(log, ids.get(2));
		setCourseModuleById(log, ids.get(3));

	}

}
