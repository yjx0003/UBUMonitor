package webservice.mod;

import java.util.Set;

import webservice.ParametersCoursesids;
import webservice.WSFunctions;


/**
 * Función de Moodle que devuelve los cuestionarios de uno, varios cursos o
 * todos los cursos del usuario.
 * 
 * @author Yi Peng Ji
 *
 */
public class ModQuizGetQuizzesByCourses extends ParametersCoursesids {

	/**
	 * Constructor inicializado sin ningún curso definido.
	 * @see ParametersCoursesids#ParametersCoursesids()
	 */
	public ModQuizGetQuizzesByCourses() {
		super();

	}

	/**
	 * @see ParametersCoursesids#ParametersCoursesids(int)
	 * @param courseid
	 *            id del curso
	 */
	public ModQuizGetQuizzesByCourses(int courseid) {
		super(courseid);

	}

	/**
	 * Constructor que recibe un set de los ids de cursos.
	 * @see ParametersCoursesids#ParametersCoursesids(Set)
	 * @param coursesIds
	 *            conjunto de ids de cursos
	 */
	public ModQuizGetQuizzesByCourses(Set<Integer> coursesIds) {
		super(coursesIds);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WSFunctions getWSFunction() {

		return WSFunctions.MOD_QUIZ_GET_QUIZZES_BY_COURSES;
	}

}
