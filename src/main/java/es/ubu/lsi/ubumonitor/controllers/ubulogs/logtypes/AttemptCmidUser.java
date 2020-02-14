package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * 
 * The quiz attempt with id '' belonging to the quiz with course module id '' for the user with id '' became overdue.
 * @author Yi Peng Ji
 *
 */
public class AttemptCmidUser extends ReferencesLog {

	/**
	 * Instacia única de la clase AttemptCmidUser.
	 */
	private static AttemptCmidUser instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private AttemptCmidUser() {
	}

	/**
	 * Devuelve la instancia única de AttemptCmidUser.
	 * @return instancia singleton
	 */
	public static AttemptCmidUser getInstance() {
		if (instance == null) {
			instance = new AttemptCmidUser();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		//attempt id
		setCourseModuleById(log, ids.get(1));
		setUserById(log, ids.get(2));
	}

}
