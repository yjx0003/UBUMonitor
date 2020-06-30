package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * The user with id '' has had their attempt with id '' marked as abandoned for the quiz with course module id ''.
 * The user with id '' has started the attempt with id '' for the quiz with course module id ''.
 * The user with id '' has submitted the attempt with id '' for the quiz with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserAttemptCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserAttemptCmid.
	 */
	private static UserAttemptCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserAttemptCmid() {
	}

	/**
	 * Devuelve la instancia única de UserAttemptCmid.
	 * @return instancia singleton
	 */
	public static UserAttemptCmid getInstance() {
		if (instance == null) {
			instance = new UserAttemptCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//atttempt id mod_quiz_get_user_attempts 
		setCourseModuleById(log, ids.get(2));

	}

}
