package es.ubu.lsi.ubumonitor.model.log.logtypes;


import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;

public class UserQuestionQuestioncategory extends ReferencesLog{
	/**
	 * Instacia única de la clase .
	 */
	private static UserQuestionQuestioncategory instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserQuestionQuestioncategory() {
	}

	/**O
	 * Devuelve la instancia única.
	 * @return instancia singleton
	 */
	public static UserQuestionQuestioncategory getInstance() {
		if (instance == null) {
			instance = new UserQuestionQuestioncategory();
		}
		return instance;
	}

	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//question id
		//question category id
	}

	
}
