package es.ubu.lsi.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.model.LogLine;

/**
 * 
 * @author Yi Peng Ji
 *
 */
public class UserNoteAffected extends ReferencesLog {

	/**
	 * Instacia única de la clase UserNoteAffected.
	 */
	private static UserNoteAffected instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserNoteAffected() {
	}

	/**
	 * Devuelve la instancia única de UserNoteAffected.
	 * @return instancia singleton
	 */
	public static UserNoteAffected getInstance() {
		if (instance == null) {
			instance = new UserNoteAffected();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//note id
		setAffectedUserById(log, ids.get(2));
	}

}
