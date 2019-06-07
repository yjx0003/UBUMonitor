package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * The user with id '' viewed the instance list for the module 'assign' in the course with id ''.
 * The user with id '' viewed the instance list for the module 'choice' in the course with id ''.
 * The user with id '' viewed the instance list for the module 'feedback' in the course with id ''.
 * The user with id '' viewed the instance list for the module 'forum' in the course with id ''.
 * The user with id '' viewed the live log report for the course with id ''.
 * The user with id '' viewed the log report for the course with id ''.
 * The user with id '' viewed the instance list for the module 'quiz' in the course with id ''.
 * The user with id '' has viewed the list of available badges for the course with the id ''.
 * The user with id '' viewed the list of resources in the course with id ''.
 * The user with id '' started the reset of the course with id ''.
 * The user with id '' viewed the course information for the course with id ''.
 * The user with id '' updated the course with id ''.
 * The user with id '' viewed the course with id ''.
 * The user with id '' viewed the recent activity report in the course with id ''.
 * The user with id '' viewed the list of users in the course with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserCourse extends ReferencesLog {

	/**
	 * Instacia única de la clase UserCourse.
	 */
	private static UserCourse instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserCourse() {
	}

	/**
	 * Devuelve la instancia única de UserCourse.
	 * @return instancia singleton
	 */
	public static UserCourse getInstance() {
		if (instance == null) {
			instance = new UserCourse();
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
