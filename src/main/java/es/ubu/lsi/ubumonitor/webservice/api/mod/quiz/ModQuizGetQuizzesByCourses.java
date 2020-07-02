package es.ubu.lsi.ubumonitor.webservice.api.mod.quiz;

import java.util.HashSet;
import java.util.Set;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class ModQuizGetQuizzesByCourses extends WSFunctionAbstract{

	private Set<Integer> courseids;

	public ModQuizGetQuizzesByCourses(Set<Integer> courseids) {
		super(WSFunctionEnum.MOD_QUIZ_GET_QUIZZES_BY_COURSES);
		this.courseids = courseids;
	}
	
	public ModQuizGetQuizzesByCourses(Integer courseid) {
		super(WSFunctionEnum.MOD_QUIZ_GET_QUIZZES_BY_COURSES);
		this.courseids = new HashSet<>();
		courseids.add(courseid);
	}
	
	@Override
	public void addToMapParemeters() {
		Util.putIfNotNull(parameters, "courseids", courseids);
	}
	

	
	
}
