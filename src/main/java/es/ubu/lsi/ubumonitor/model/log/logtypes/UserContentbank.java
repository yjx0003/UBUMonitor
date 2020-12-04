package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

public class UserContentbank extends ReferencesLog{
	/**
	 * Instacia única de la clase User.
	 */
	private static UserContentbank instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserContentbank() {
	}

	/**
	 * Devuelve la instancia única de User.
	 * @return instancia singleton
	 */
	public static UserContentbank getInstance() {
		if (instance == null) {
			instance = new UserContentbank();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		// ignore content bank at the moment
	}
}
