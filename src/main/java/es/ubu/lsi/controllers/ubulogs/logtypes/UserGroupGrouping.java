package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;

/**
 * The user with id '' assigned the group with id '' to the grouping with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserGroupGrouping extends ReferencesLog {

	/**
	 * Instacia única de la clase UserGroupGrouping.
	 */
	private static UserGroupGrouping instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserGroupGrouping() {
	}

	/**
	 * Devuelve la instancia única de UserGroupGrouping.
	 * @return instancia singleton
	 */
	public static UserGroupGrouping getInstance() {
		if (instance == null) {
			instance = new UserGroupGrouping();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//group id
		//grouping id

	}

}
