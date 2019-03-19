package webservice.mod;

import java.util.Set;

import webservice.ParametersCoursesids;
import webservice.WSFunctions;

public class ModQuizGetQuizzesByCourses extends ParametersCoursesids {

	public ModQuizGetQuizzesByCourses() {
		super();

	}

	public ModQuizGetQuizzesByCourses(int courseid) {
		super(courseid);
		
	}

	public ModQuizGetQuizzesByCourses(Set<Integer> coursesIds) {
		super(coursesIds);
		
	}

	@Override
	public WSFunctions getWSFunction() {
	
		return WSFunctions.MOD_QUIZ_GET_QUIZZES_BY_COURSES;
	}


}
