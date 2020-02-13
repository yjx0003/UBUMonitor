package es.ubu.lsi.webservice.gradereport;

import es.ubu.lsi.webservice.ParametersCourseidUseridGroupid;
import es.ubu.lsi.webservice.WSFunctions;


/**
 * Función de Moodle que devuelve el calificador de un usuario, grupo o de todos los usuarios del curso.
 * NO contiene codigo HTML la función de Moodle. 
 * Actualmente tiene un bug cuando la retroalimentación del calificador esta desactivada.
 * @author Yi Peng Ji
 *
 */
public class GradereportUserGetGradeItems extends ParametersCourseidUseridGroupid {

	/**
	 * Constructor con el id del usuario o del grupo
	 * @param courseid id del curso
	 * @param optionalParameter selección del id es del usuario o del grupo
	 * @param userOrGroupid id del usuario o del grupo
	 */
	public GradereportUserGetGradeItems(int courseid, OptionalParameter optionalParameter, int userOrGroupid) {
		super(courseid, optionalParameter, userOrGroupid);
	}

	/**
	 * Constructor con el id del curso
	 * @param courseid id del curso
	 */
	public GradereportUserGetGradeItems( int courseid) {
		super(courseid);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WSFunctions getWSFunction() {
		return  WSFunctions.GRADEREPORT_USER_GET_GRADE_ITEMS;
	}

}