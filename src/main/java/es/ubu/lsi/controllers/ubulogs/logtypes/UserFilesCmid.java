package es.ubu.lsi.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.model.LogLine;


/**
 * The user with id '' created a file submission and uploaded '' file/s in the assignment with course module id ''.
 * The user with id '' updated a file submission and uploaded '' file/s in the assignment with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserFilesCmid extends ReferencesLog {
	
	/**
	 * Instacia única de la clase UserFilesCmid.
	 */
	private static UserFilesCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserFilesCmid() {
	}

	/**
	 * Devuelve la instancia única de UserFilesCmid.
	 * @return instancia singleton
	 */
	public static UserFilesCmid getInstance() {
		if (instance == null) {
			instance = new UserFilesCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//number of files
		setCourseModuleById(log, ids.get(2));

	}

}
