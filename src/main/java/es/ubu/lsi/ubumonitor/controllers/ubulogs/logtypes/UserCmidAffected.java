package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * The user with id '' updated the completion state for the course module with id '' for the user with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserCmidAffected extends ReferencesLog {

	/**
	 * Instacia única de la clase UserCmidAffected.
	 */
	private static UserCmidAffected instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserCmidAffected() {
	}

	/**
	 * Devuelve la instancia única de UserCmidAffected.
	 * @return instancia singleton
	 */
	public static UserCmidAffected getInstance() {
		if (instance == null) {
			instance = new UserCmidAffected();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		setCourseModuleById(log, ids.get(1));
		setAffectedUserById(log, ids.get(2));
	}

}
