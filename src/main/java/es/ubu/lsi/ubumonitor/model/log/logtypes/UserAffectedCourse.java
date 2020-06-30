package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * The user with id '' viewed the profile for the user with id '' in the course with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserAffectedCourse extends ReferencesLog{


	/**
	 * Instacia única de la clase UserAffectedCourse.
	 */
	private static UserAffectedCourse instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserAffectedCourse() {
	}

	/**
	 * Devuelve la instancia única de UserAffectedCourse.
	 * @return instancia singleton
	 */
	public static UserAffectedCourse getInstance() {
		if (instance == null) {
			instance = new UserAffectedCourse();
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
		// La id del curso se ignora de momento

	}

}
