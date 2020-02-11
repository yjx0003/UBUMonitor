package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;

/**
 * The user with id '' added the comment with id '' to the '' with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserCommentCmid extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserCommentCmid.
	 */
	private static UserCommentCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserCommentCmid() {
	}

	/**
	 * Devuelve la instancia única de UserCommentCmid.
	 * @return instancia singleton
	 */
	public static UserCommentCmid getInstance() {
		if (instance == null) {
			instance = new UserCommentCmid();
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
		setCourseModuleById(log, ids.get(2));

	}

}
