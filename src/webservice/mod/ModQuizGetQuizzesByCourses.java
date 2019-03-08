package webservice.mod;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import webservice.MoodleOptions;
import webservice.WebService;

public class ModQuizGetQuizzesByCourses extends WebService {

	private Set<Integer> coursesids;

	public ModQuizGetQuizzesByCourses(Set<Integer> coursesIds) {
		super(MoodleOptions.OBTENER_QUIZZES);
		this.coursesids=coursesIds;
	
	}

	public ModQuizGetQuizzesByCourses() {
		super(MoodleOptions.OBTENER_QUIZZES);
		coursesids = new HashSet<Integer>();
		
	}

	public ModQuizGetQuizzesByCourses(int courseid) {
		this();
		coursesids.add(courseid);
		
	}

	public void addCourseId(int courseId) {
		coursesids.add(courseId);
	}

	public void removeCourseId(int courseId) {
		coursesids.remove(courseId);
	}

	/**
	 * Devuele todos los cuestionarios (mod/Quiz) de uno o varios cursos. Si
	 * coursesIds está vacio devuelve los cuestionarios de todos los cursos
	 * matriculados del usuario.
	 * 
	 */
	@Override
	public String getResponse() throws IOException {
		String url = this.url;
		
		
		

		return getContentWithJsoup(url);
	}


}
