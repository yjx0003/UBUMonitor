package es.ubu.lsi.ubumonitor.model.log.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * The user with id '' viewed the profile for the user with id '' in the course with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserActivityCourseAffected extends ReferencesLog{


	/**
	 * Instacia única de la clase UserActivityCourseAffected.
	 */
	private static UserActivityCourseAffected instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserActivityCourseAffected() {
	}

	/**
	 * Devuelve la instancia única de UserActivityCourseAffected.
	 * @return instancia singleton
	 */
	public static UserActivityCourseAffected getInstance() {
		if (instance == null) {
			instance = new UserActivityCourseAffected();
		}
		return instance;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		// La id del curso se ignora de momento

	}

}
