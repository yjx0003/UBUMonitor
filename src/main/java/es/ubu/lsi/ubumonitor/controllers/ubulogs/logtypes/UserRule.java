package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * The user with id '' created the event monitor rule with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserRule extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserRule.
	 */
	private static UserRule instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserRule() {
	}

	/**
	 * Devuelve la instancia única de UserRule.
	 * @return instancia singleton
	 */
	public static UserRule getInstance() {
		if (instance == null) {
			instance = new UserRule();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//event monitor rule id
	}

}
