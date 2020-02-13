package es.ubu.lsi.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.model.LogLine;
/**
 * 
 * The user with id '' manually graded the question with id '' for the attempt with id '' for the quiz with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserQuestionAttemptCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserQuestionAttemptCmid.
	 */
	private static UserQuestionAttemptCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserQuestionAttemptCmid() {
	}

	/**
	 * Devuelve la instancia única de UserQuestionAttemptCmid.
	 * @return instancia singleton
	 */
	public static UserQuestionAttemptCmid getInstance() {
		if (instance == null) {
			instance = new UserQuestionAttemptCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//question id
		//attempt id
		setCourseModuleById(log, ids.get(3));

	}

}
