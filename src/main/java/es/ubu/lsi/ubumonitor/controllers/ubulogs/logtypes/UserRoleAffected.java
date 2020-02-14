package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;


/**
 * 
 * The user with id '' assigned the role with id '' to the user with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserRoleAffected extends ReferencesLog {

	/**
	 * Instacia única de la clase UserRoleAffected.
	 */
	private static UserRoleAffected instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserRoleAffected() {
	}

	/**
	 * Devuelve la instancia única de UserRoleAffected.
	 * @return instancia singleton
	 */
	public static UserRoleAffected getInstance() {
		if (instance == null) {
			instance = new UserRoleAffected();
		}
		return instance;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//role id
		setAffectedUserById(log, ids.get(2));
	}

}
