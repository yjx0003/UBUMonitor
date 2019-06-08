package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;

/**
 * The user with id '' updated the capabilities for the role with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserRole extends ReferencesLog {

	/**
	 * Instacia única de la clase UserRole.
	 */
	private static UserRole instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserRole() {
	}

	/**
	 * Devuelve la instancia única de UserRole.
	 * @return instancia singleton
	 */
	public static UserRole getInstance() {
		if (instance == null) {
			instance = new UserRole();
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

	}

}
