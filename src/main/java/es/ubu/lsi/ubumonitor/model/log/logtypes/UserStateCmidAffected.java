package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * The user with id '' updated the completion state for the course module with
 * id '' for the user with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserStateCmidAffected extends ReferencesLog {

	/**
	 * Instacia única de la clase UserCmidAffected.
	 */
	private static UserStateCmidAffected instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserStateCmidAffected() {
	}

	/**
	 * Devuelve la instancia única de UserCmidAffected.
	 * 
	 * @return instancia singleton
	 */
	public static UserStateCmidAffected getInstance() {
		if (instance == null) {
			instance = new UserStateCmidAffected();
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		setCourseModuleById(log, ids.get(2));
		setAffectedUserById(log, ids.get(3));
	}
}