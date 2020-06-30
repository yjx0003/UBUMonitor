package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * The user with id 'INTEGER' made the choice with id 'INTEGER' in the choice activity\n            with course module id 'INTEGER'.
 * @author Yi Peng Ji
 *
 */
public class UserChoiceCmid extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserChoiceCmid.
	 */
	private static UserChoiceCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserChoiceCmid() {
	}

	/**
	 * Devuelve la instancia única de UserChoiceCmid.
	 * @return instancia singleton
	 */
	public static UserChoiceCmid getInstance() {
		if (instance == null) {
			instance = new UserChoiceCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//choice id
		setCourseModuleById(log, ids.get(2));

	}

}
