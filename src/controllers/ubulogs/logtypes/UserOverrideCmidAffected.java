package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;


/**
 * 
 * The user with id '' created the override with id '' for the assign with course module id '' for the user with id ''.
 * The user with id '' deleted the override with id '' for the assign with course module id '' for the user with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserOverrideCmidAffected extends ReferencesLog {

	/**
	 * Instacia única de la clase UserOverrideCmidAffected.
	 */
	private static UserOverrideCmidAffected instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserOverrideCmidAffected() {
	}

	/**
	 * Devuelve la instancia única de UserOverrideCmidAffected.
	 * @return instancia singleton
	 */
	public static UserOverrideCmidAffected getInstance() {
		if (instance == null) {
			instance = new UserOverrideCmidAffected();
		}
		return instance;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//override id
		setCourseModuleById(log, ids.get(1));
		setAffectedUserById(log, ids.get(2));

	}

}
