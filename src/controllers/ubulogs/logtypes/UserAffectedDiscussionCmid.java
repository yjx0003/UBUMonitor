package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;

public class UserAffectedDiscussionCmid extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserAffectedDiscussionCmid.
	 */
	private static UserAffectedDiscussionCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserAffectedDiscussionCmid() {
	}

	/**
	 * Devuelve la instancia única de UserAffectedDiscussionCmid.
	 * @return instancia singleton
	 */
	public static UserAffectedDiscussionCmid getInstance() {
		if (instance == null) {
			instance = new UserAffectedDiscussionCmid();
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
		//discussion id
		setCourseModuleById(log, ids.get(0));
	}

}
