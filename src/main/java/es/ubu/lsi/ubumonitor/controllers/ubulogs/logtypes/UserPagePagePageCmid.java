package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * The user with id '' has moved a Content page with the id '' to the slot after the page with the id '' and before the page with the id '' in the lesson activity with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserPagePagePageCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserPagePagePageCmid.
	 */
	private static UserPagePagePageCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserPagePagePageCmid() {
	}

	/**
	 * Devuelve la instancia única de UserPagePagePageCmid.
	 * @return instancia singleton
	 */
	public static UserPagePagePageCmid getInstance() {
		if (instance == null) {
			instance = new UserPagePagePageCmid();
		}
		return instance;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//page id
		//page id
		//page id
		setCourseModuleById(log, ids.get(3));

	}

}
