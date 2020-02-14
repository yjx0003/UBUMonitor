package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * The user with id '' added a comment with id '' on the page with id '' for the wiki with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserCommentPageCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserCommentPageCmid.
	 */
	private static UserCommentPageCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserCommentPageCmid() {
	}

	/**
	 * Devuelve la instancia única de UserCommentPageCmid.
	 * @return instancia singleton
	 */
	public static UserCommentPageCmid getInstance() {
		if (instance == null) {
			instance = new UserCommentPageCmid();
		}
		return instance;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//comment id
		//page id
		setCourseModuleById(log, ids.get(3));

	}

}
