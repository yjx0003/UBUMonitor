package es.ubu.lsi.ubumonitor.webservice.api.mod.quiz;

import java.util.Collection;

import es.ubu.lsi.ubumonitor.webservice.api.mod.forum.ModForumGetForumsByCourses;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * Returns a list of quizzes in a provided list of courses, if no list is
 * provided all quizzes that the user can view will be returned.
 * 
 * @author Yi Peng Ji
 *
 */
public class ModQuizGetQuizzesByCourses extends ModForumGetForumsByCourses {

	public ModQuizGetQuizzesByCourses(Collection<Integer> courseids) {
		super(WSFunctionEnum.MOD_QUIZ_GET_QUIZZES_BY_COURSES, courseids);

	}

	public ModQuizGetQuizzesByCourses(int courseid) {
		super(WSFunctionEnum.MOD_QUIZ_GET_QUIZZES_BY_COURSES, courseid);

	}

}
