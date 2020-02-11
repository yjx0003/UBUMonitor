package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;

/**
 * The user with id '' restored old course with id '' to a new course with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserCourseCourse extends ReferencesLog {

	/**
	 * Instacia única de la clase UserCourseCourse.
	 */
	private static UserCourseCourse instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserCourseCourse() {
	}

	/**
	 * Devuelve la instancia única de UserCourseCourse.
	 * @return instancia singleton
	 */
	public static UserCourseCourse getInstance() {
		if (instance == null) {
			instance = new UserCourseCourse();
		}
		return instance;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//old course id
		//new course id

	}

}
