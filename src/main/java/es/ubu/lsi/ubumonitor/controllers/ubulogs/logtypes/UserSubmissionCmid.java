package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;


/**
 * 
 * 
 * The user with id '' has submitted the submission with id '' for the assignment with course module id ''.
 * The user with id '' has updated the status of the submission with id '' for the assignment with course module id '' to the status 'draft'.
 * The user with id '' has accepted the statement of the submission with id '' for the assignment with course module id ''.
 * The user with id '' has uploaded a file to the submission with id '' in the assignment activity with course module id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserSubmissionCmid extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserSubmissionCmid.
	 */
	private static UserSubmissionCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserSubmissionCmid() {
	}

	/**
	 * Devuelve la instancia única de UserSubmissionCmid.
	 * @return instancia singleton
	 */
	public static UserSubmissionCmid getInstance() {
		if (instance == null) {
			instance = new UserSubmissionCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//submission id
		setCourseModuleById(log, ids.get(2));

	}

}
