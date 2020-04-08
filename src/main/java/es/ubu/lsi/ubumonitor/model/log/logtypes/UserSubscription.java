package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * The user with id '' created the event monitor subscription with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserSubscription extends ReferencesLog {

	/**
	 * Instacia única de la clase UserSubscription.
	 */
	private static UserSubscription instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserSubscription() {
	}

	/**
	 * Devuelve la instancia única de UserSubscription.
	 * @return instancia singleton
	 */
	public static UserSubscription getInstance() {
		if (instance == null) {
			instance = new UserSubscription();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//event monitor subscription id
	}

}
