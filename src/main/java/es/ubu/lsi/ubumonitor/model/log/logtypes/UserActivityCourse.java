package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * The user with id '' viewed the profile for the user with id '' in the course with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserActivityCourse extends ReferencesLog{


	/**
	 * Instacia única de la clase UserAffectedCourse.
	 */
	private static UserActivityCourse instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserActivityCourse() {
	}

	/**
	 * Devuelve la instancia única de UserAffectedCourse.
	 * @return instancia singleton
	 */
	public static UserActivityCourse getInstance() {
		if (instance == null) {
			instance = new UserActivityCourse();
		}
		return instance;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//ignorado
		// La id del curso se ignora de momento
		setAffectedUserById(log, ids.get(3));
	}

}
