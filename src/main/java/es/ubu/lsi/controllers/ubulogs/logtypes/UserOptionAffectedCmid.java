package es.ubu.lsi.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.model.LogLine;


/**
 * 
 * The user with id '' has added the option with id '' for the\n            user with id '' from the choice activity with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserOptionAffectedCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserOptionAffectedCmid.
	 */
	private static UserOptionAffectedCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserOptionAffectedCmid() {
	}

	/**
	 * Devuelve la instancia única de UserOptionAffectedCmid.
	 * @return instancia singleton
	 */
	public static UserOptionAffectedCmid getInstance() {
		if (instance == null) {
			instance = new UserOptionAffectedCmid();
		}
		return instance;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//option id
		setAffectedUserById(log, ids.get(2));
		setCourseModuleById(log, ids.get(3));

	}

}
