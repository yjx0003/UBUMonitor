package es.ubu.lsi.webservice;

/**
 * Enumeración de las funciones de moodle en upper case.
 * 
 * @author Yi Peng Ji
 *
 */
public enum WSFunctions {
	
	/**
	 * Return the activities completion status for a user in a course.
	 */
	CORE_COMPLETION_GET_ACTIVITIES_COMPLETION_STATUS,
	
	/**
	 * Devuelve las categorías de los cursos.
	 * 
	 */
	CORE_COURSE_GET_CATEGORIES,
	
	/**
	 * Devuelve los modulos de un curso como tareas, cuestionarios, lecciones, etc.
	 */
	CORE_COURSE_GET_CONTENTS,
	
	/**
	 * Devuelve los usuarios matriculados en un curso.
	 */
	CORE_ENROL_GET_ENROLLED_USERS,
	
	/**
	 * Devuelve los cursos en los que está matriculado el usuario
	 */
	CORE_ENROL_GET_USERS_COURSES,
	
	/**
	 * Devuelve los cursos accedidos recientemente.
	 */
	CORE_COURSE_GET_RECENT_COURSES,
	
	/**
	 * List of enrolled courses for the given timeline classification (past, inprogress, or future).
	 */
 	CORE_COURSE_GET_ENROLLED_COURSES_BY_TIMELINE_CLASSIFICATION, 
	/**
	 * Devuelve información relativa al usuario
	 */
	CORE_USER_GET_USERS_BY_FIELD,
	
	/**
	 * Devuelve las notas de todos los elementos del calificador (Moodel 3.3)
	 */
	GRADEREPORT_USER_GET_GRADE_ITEMS,
	
	/**
	 * Devuelve todas las categorías y elementos evaluables de un curso.
	 * 
	 */
	GRADEREPORT_USER_GET_GRADES_TABLE,
	
	/**
	 * Devuelve información sobre los assignments (tareas) que hay en un curso
	 */
	MOD_ASSIGN_GET_ASSIGNMENTS,
	
	/**
	 * Devuelve información sobre una lección (Moodel 3.3)
	 */
	MOD_LESSON_GET_LESSON,
	
	/**
	 * Devuelve información sobre los quizs (cuestionarios) que hay en un curso
	 */
	MOD_QUIZ_GET_QUIZZES_BY_COURSES,
	
	/**
	 * Devuelve token de usuario
	 */
	MOODLE_MOBILE_APP;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
