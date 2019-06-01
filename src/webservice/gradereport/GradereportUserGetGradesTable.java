package webservice.gradereport;

import webservice.ParametersCourseidUseridGroupid;
import webservice.WSFunctions;

/**
 * Función de Moodle que devuelve el calificador de un usuario, grupo o de todos los usuarios del curso.
 * Contiene codigo HTML la función de Moodle.
 * @author Yi Peng Ji
 *
 */
public class GradereportUserGetGradesTable extends ParametersCourseidUseridGroupid {


	/**
	 * Constructor con el id del usuario o del grupo
	 * @param courseid id del curso
	 * @param optionalParameter selección del id es del usuario o del grupo
	 * @param userOrGroupid id del usuario o del grupo
	 */
	public GradereportUserGetGradesTable(int courseid, OptionalParameter optionalParameter, int userOrGroupid) {
		super( courseid, optionalParameter, userOrGroupid);
	}

	/**
	 * Constructor con el id del curso
	 * @param courseid id del curso
	 */
	public GradereportUserGetGradesTable(int courseid) {
		super(courseid);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WSFunctions getWSFunction() {
		return WSFunctions.GRADEREPORT_USER_GET_GRADES_TABLE;
	}

}
