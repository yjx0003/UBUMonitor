package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * The user with id '' has viewed the content page with id '' in the lesson activity with course module id ''.
 * The user with id '' has created a Matching page with the id '' in the lesson activity with course module id ''."
 * The user with id '' has created a Content page with the id '' in the lesson activity with course module id ''.
 * The user with id '' has updated the Content page with the id '' in the lesson activity with course module id ''.
 * The user with id '' has updated the True/false page with the id '' in the lesson activity with course module id ''.
 * The user with id '' has answered the Matching question with id '' in the lesson activity with course module id ''.
 * The user with id '' has viewed the Multichoice question with id '' in the lesson activity with course module id ''.
 * The user with id '' has viewed the Matching question with id '' in the lesson activity with course module id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserPageCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserPageCmid.
	 */
	private static UserPageCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserPageCmid() {
	}

	/**
	 * Devuelve la instancia única de UserPageCmid.
	 * @return instancia singleton
	 */
	public static UserPageCmid getInstance() {
		if (instance == null) {
			instance = new UserPageCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//content page id
		setCourseModuleById(log, ids.get(2));
	}

}
