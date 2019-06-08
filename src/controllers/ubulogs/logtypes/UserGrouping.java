package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;
/**
 * The user with id '' created the grouping with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserGrouping extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserGrouping.
	 */
	private static UserGrouping instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserGrouping() {
	}

	/**
	 * Devuelve la instancia única de UserGrouping.
	 * @return instancia singleton
	 */
	public static UserGrouping getInstance() {
		if (instance == null) {
			instance = new UserGrouping();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//grouping id

	}

}
