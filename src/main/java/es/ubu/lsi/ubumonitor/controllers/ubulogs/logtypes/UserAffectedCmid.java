package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * 
 * The user with id '' viewed the grading form for the user with id '' for the assignment with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserAffectedCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserAffectedCmid.
	 */
	private static UserAffectedCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserAffectedCmid() {
	}

	/**
	 * Devuelve la instancia única de UserAffectedCmid.
	 * @return instancia singleton
	 */
	public static UserAffectedCmid getInstance() {
		if (instance == null) {
			instance = new UserAffectedCmid();
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
		setCourseModuleById(log, ids.get(2));
	}

}
