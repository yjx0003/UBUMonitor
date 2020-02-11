package controllers.ubulogs.logtypes;

import java.util.List;

import controllers.Controller;
import model.EnrolledUser;
import model.LogLine;
import model.CourseModule;

public abstract class ReferencesLog {

	private static final Controller CONTROLLER = Controller.getInstance();


	/**
	 * Modifica el usuario que realiza la acción del log a partir del id usuario.
	 * 
	 * @param log
	 *            log
	 * @param id
	 *            id del usuario
	 */
	protected static void setUserById(LogLine log, int id) {
		EnrolledUser user = CONTROLLER.getDataBase().getUsers().getById(id);

		log.setUser(user);

	}

	/**
	 * Modifica el usuario afectado del log a partir del id de usuario.
	 * 
	 * @param log
	 *            log
	 * @param id
	 *            del usuario
	 */
	protected static void setAffectedUserById(LogLine log, int id) {
		EnrolledUser affectedUser = CONTROLLER.getDataBase().getUsers().getById(id);

		log.setAffectedUser(affectedUser);

	}

	/**
	 * Modifica el modulo del curso asociado al log a partir del id del modulo del
	 * curso
	 * 
	 * @param log
	 *            log
	 * @param id
	 *            del modulo (cmid)
	 */
	protected static void setCourseModuleById(LogLine log, int id) {
		CourseModule courseModule = CONTROLLER.getDataBase().getModules().getById(id);

		log.setCourseModule(courseModule);

	}

	/**
	 * Asigna las referencias del log a otros objetos en funcion de que id sea
	 * 
	 * @param log
	 *            log
	 * @param ids
	 *            diferentes ids
	 */
	public abstract void setLogReferencesAttributes(LogLine log, List<Integer> ids);


}
