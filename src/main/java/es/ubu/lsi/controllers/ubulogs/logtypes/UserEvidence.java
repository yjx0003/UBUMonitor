package es.ubu.lsi.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.model.LogLine;

/**
 * The user with id '' created an evidence with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserEvidence extends ReferencesLog {

	/**
	 * Instacia única de la clase UserEvidence.
	 */
	private static UserEvidence instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserEvidence() {
	}

	/**
	 * Devuelve la instancia única de UserEvidence.
	 * @return instancia singleton
	 */
	public static UserEvidence getInstance() {
		if (instance == null) {
			instance = new UserEvidence();
		}
		return instance;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//evidence id

	}

}
