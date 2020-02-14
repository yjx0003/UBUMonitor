package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

/**
 * 
 * The user with id '' has graded the submission '' for the user with id '' for the assignment with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserSubmissionAffectedCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserSubmissionAffectedCmid.
	 */
	private static UserSubmissionAffectedCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserSubmissionAffectedCmid() {
	}

	/**
	 * Devuelve la instancia única de UserSubmissionAffectedCmid.
	 * @return instancia singleton
	 */
	public static UserSubmissionAffectedCmid getInstance() {
		if (instance == null) {
			instance = new UserSubmissionAffectedCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//submission
		setAffectedUserById(log, ids.get(2));
		setCourseModuleById(log, ids.get(3));
		
	}

}
