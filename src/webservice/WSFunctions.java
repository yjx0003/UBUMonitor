package webservice;

public enum WSFunctions {
	
	/**
	 * moodle_mobile_app<br>
	 * Devuelve token de usuario
	 */
	SERVICIO_WEB_MOODLE("moodle_mobile_app"),
	
	/**
	 * core_user_get_users_by_field<br>
	 * Devuelve información relativa al usuario
	 */
	OBTENER_INFO_USUARIO("core_user_get_users_by_field"),
	
	/**
	 * Devuelve los cursos en los que está matriculado el usuario
	 */
	OBTENER_CURSOS("core_enrol_get_users_courses"),
	
	/**
	 * core_enrol_get_enrolled_users<br>
	 * Devuelve los usuarios matriculados en un curso.
	 */
	OBTENER_USUARIOS_MATRICULADOS("core_enrol_get_enrolled_users"),
	
	/**
	 * gradereport_user_get_grades_table<br>
	 * Devuelve todas las categorías y elementos evaluables de un curso.
	 * 
	 */
	OBTENER_TABLA_NOTAS("gradereport_user_get_grades_table"),
	
	/**
	 * mod_assign_get_assignments<br>
	 * Devuelve información sobre los assignments (tareas) que hay en un curso
	 */
	OBTENER_ASSIGNMENTS("mod_assign_get_assignments"),
	
	/**
	 * mod_quiz_get_quizzes_by_courses<br>
	 * Devuelve información sobre los quizs (cuestionarios) que hay en un curso
	 */
	OBTENER_QUIZZES("mod_quiz_get_quizzes_by_courses"),
	
	/**
	 * gradereport_user_get_grade_items<br>
	 * Devuelve las notas de todos los elementos del calificador (Moodel 3.3)
	 */
	OBTENER_NOTAS_ALUMNO("gradereport_user_get_grade_items"),
	
	/**
	 * mod_lesson_get_lesson<br>
	 * Devuelve información sobre una lección (Moodel 3.3)
	 */
	OBTENER_INFO_LECCION("mod_lesson_get_lesson");
	
	
	
	private String functionName;
	
	WSFunctions(String functionName){
		this.functionName=functionName;
	}
	
	
	@Override
	public String toString() {
		return functionName;
	}
}
