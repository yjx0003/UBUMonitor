package es.ubu.lsi.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.model.LogLine;
/**
 * The user with id '' added the user with id '' to the group with id ''.
 * The user with id '' removed the user with id '' to the group with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserAffectedGroup extends ReferencesLog {
	
	
	/**
	 * Instacia única de la clase UserAffectedGroup.
	 */
	private static UserAffectedGroup instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserAffectedGroup() {
	}

	/**
	 * Devuelve la instancia única de UserAffectedGroup.
	 * @return instancia singleton
	 */
	public static UserAffectedGroup getInstance() {
		if (instance == null) {
			instance = new UserAffectedGroup();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		setAffectedUserById(log, ids.get(1));
		
	}

}
