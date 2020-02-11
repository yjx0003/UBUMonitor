package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;

public class UserQuestion extends ReferencesLog{
	/**
	 * Instacia única de la clase .
	 */
	private static UserQuestion instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserQuestion() {
	}

	/**
	 * Devuelve la instancia única.
	 * @return instancia singleton
	 */
	public static UserQuestion getInstance() {
		if (instance == null) {
			instance = new UserQuestion();
		}
		return instance;
	}

	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//question id
	}

	
}
