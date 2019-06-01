package webservice.core;

import webservice.ParametersUserid;
import webservice.WSFunctions;

/**
 * Función de Moodle que devuelve la información de los cursos matriculados del usuario.
 * @author Yi Peng Ji
 *
 */
public class CoreEnrolGetUsersCourses extends ParametersUserid {

	
	/**
	 * Constructor con el id de usuario.
	 * @param userid id de usuario
	 */
	public CoreEnrolGetUsersCourses(int userid) {
		super(userid);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WSFunctions getWSFunction() {
	
		return WSFunctions.CORE_ENROL_GET_USERS_COURSES;
	}

	
	
	


	

}
