package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * 
 * The user with id '' has created the post with id '' in the discussion with id '' in the forum with course module id ''.
 * The user with id '' has updated the post with id '' in the discussion with id '' in the forum with course module id ''.
 * The user with id '' has posted content in the forum post with id '' in the discussion '' located in the forum with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserPostDiscussionCmid extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserPostDiscussionCmid.
	 */
	private static UserPostDiscussionCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserPostDiscussionCmid() {
	}

	/**
	 * Devuelve la instancia única de UserPostDiscussionCmid.
	 * @return instancia singleton
	 */
	public static UserPostDiscussionCmid getInstance() {
		if (instance == null) {
			instance = new UserPostDiscussionCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//post id
		//discussion id
		setCourseModuleById(log, ids.get(3));

	}

}
