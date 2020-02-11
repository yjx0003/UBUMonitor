package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * The user with id '' has created the discussion with id '' in the forum with course module id ''.
 * The user with id '' has viewed the discussion with id '' in the forum with course module id ''.
 * 
 * 
 * @author Yi Peng Ji
 *
 */
public class UserDiscussionCmid extends ReferencesLog{

	/**
	 * Instacia única de la clase UserDiscussionCmid.
	 */
	private static UserDiscussionCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserDiscussionCmid() {
	}

	/**
	 * Devuelve la instancia única de UserDiscussionCmid.
	 * @return instancia singleton
	 */
	public static UserDiscussionCmid getInstance() {
		if (instance == null) {
			instance = new UserDiscussionCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//discussion id
		setCourseModuleById(log, ids.get(2));
		
	}

}
