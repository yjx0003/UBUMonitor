package controllers.ubulogs.logtypes;

import java.util.List;

import controllers.Controller;
import controllers.ubulogs.LogTypes;
import model.Component;
import model.ComponentEvent;
import model.EnrolledUser;
import model.Event;
import model.LogLine;
import model.mod.Module;

public abstract class ReferencesLog {

	private static final Controller CONTROLLER = Controller.getInstance();

	/**
	 * Devuelve la clase encargada de gestionar los numeros a partir del componente
	 * y evento, si no existe esa combinacion de componente y event devuelve uno por
	 * defecto que no hace nada.
	 * 
	 * @param component
	 *            el componente del log
	 * @param eventName
	 *            evento del log
	 * @return clase encargada de gestionar
	 */
	public static ReferencesLog getReferenceLog(Component component, Event eventName) {
		return LogTypes.getLogTypes().getOrDefault(ComponentEvent.get(component, eventName), Default.getInstance());
	}

	/**
	 * Modifica el usuario que realiza la acción del log a partir del id usuario.
	 * 
	 * @param log
	 *            log
	 * @param id
	 *            id del usuario
	 */
	protected static void setUserById(LogLine log, int id) {
		EnrolledUser user = CONTROLLER.getDataBase().getEnrolledUserById(id);

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
		EnrolledUser affectedUser = CONTROLLER.getDataBase().getEnrolledUserById(id);

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
		Module courseModule = CONTROLLER.getDataBase().getCourseModuleById(id);

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

	@Override
	public String toString() {
		return getClass().getName();
	}
}
