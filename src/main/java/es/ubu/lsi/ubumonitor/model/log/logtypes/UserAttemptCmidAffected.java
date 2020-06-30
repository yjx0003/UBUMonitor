package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * The user with id '' deleted the attempt with id '' belonging to the quiz with course module id '' for the user with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserAttemptCmidAffected extends ReferencesLog {

	/**
	 * Instacia única de la clase UserAttemptCmidAffected.
	 */
	private static UserAttemptCmidAffected instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserAttemptCmidAffected() {
	}

	/**
	 * Devuelve la instancia única de UserAttemptCmidAffected.
	 * @return instancia singleton
	 */
	public static UserAttemptCmidAffected getInstance() {
		if (instance == null) {
			instance = new UserAttemptCmidAffected();
		}
		return instance;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//attempt id
		setCourseModuleById(log, ids.get(2));
		setAffectedUserById(log, ids.get(3));
	}

}
