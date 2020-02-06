package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * The user with id '' viewed the user report for the course with id '' for user with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserCourseAffected extends ReferencesLog {

	/**
	 * Instacia única de la clase UserCourseAffected.
	 */
	private static UserCourseAffected instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserCourseAffected() {
	}

	/**
	 * Devuelve la instancia única de UserCourseAffected.
	 * @return instancia singleton
	 */
	public static UserCourseAffected getInstance() {
		if (instance == null) {
			instance = new UserCourseAffected();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		// El curso ya se enlaza al crear el log en createLogWithBasicAttributes de la clase ReferencesLog
		setAffectedUserById(log, ids.get(2));
		
	}

}
