package es.ubu.lsi.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.model.LogLine;

/**
 * Log con dos tipos de descripciones diferentes. "The user with id '' restored
 * the course with id ''.", "The user with id '' restored old course with id ''
 * to a new course with id ''."
 * 
 * @author Yi Peng Ji
 *
 */
public class SystemCourseRestored extends ReferencesLog {

	/**
	 * Instacia única de la clase SystemCourseRestored.
	 */
	private static SystemCourseRestored instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private SystemCourseRestored() {
	}

	/**
	 * Devuelve la instancia única de SystemCourseRestored.
	 * 
	 * @return instancia singleton
	 */
	public static SystemCourseRestored getInstance() {
		if (instance == null) {
			instance = new SystemCourseRestored();
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		if (ids.size() == 2) {
			UserCourse.getInstance().setLogReferencesAttributes(log, ids);
		} else if (ids.size() == 3) {
			UserCourseCourse.getInstance().setLogReferencesAttributes(log, ids);
		}

	}

}
