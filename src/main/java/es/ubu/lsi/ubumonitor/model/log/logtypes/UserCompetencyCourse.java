package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * The user with id '' viewed the user course competency with id '' in course with id ''
 * @author Yi Peng Ji
 *
 */
public class UserCompetencyCourse extends ReferencesLog {

	/**
	 * Instacia única de la clase UserCompetencyCourse.
	 */
	private static UserCompetencyCourse instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserCompetencyCourse() {
	}

	/**
	 * Devuelve la instancia única de UserCompetencyCourse.
	 * @return instancia singleton
	 */
	public static UserCompetencyCourse getInstance() {
		if (instance == null) {
			instance = new UserCompetencyCourse();
		}
		return instance;
	}
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//competency id

	}

}
