package webservice.mod;

import java.util.Set;

import webservice.ParametersCoursesids;
import webservice.WSFunctions;
/**
 * Función de Moodle que devuelve los cuestionarios de uno o varios cursos.
 * 
 * @author Yi Peng Ji
 *
 */
public class ModQuizGetQuizzesByCourses extends ParametersCoursesids {

	/**
	 * @see ParametersCoursesids#ParametersCoursesids()
	 */
	public ModQuizGetQuizzesByCourses() {
		super();

	}

	/**
	 * @see ParametersCoursesids#ParametersCoursesids(int)
	 */
	public ModQuizGetQuizzesByCourses(int courseid) {
		super(courseid);
		
	}

	/**
	 * @see ParametersCoursesids#ParametersCoursesids(Set)
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
