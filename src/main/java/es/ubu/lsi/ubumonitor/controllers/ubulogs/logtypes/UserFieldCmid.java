package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;


/**
 * The user with id '' created the field with id '' for the data activity with course module id ''.
 * The user with id '' updated the field with id '' in the data activity with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserFieldCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserFieldCmid.
	 */
	private static UserFieldCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserFieldCmid() {
	}

	/**
	 * Devuelve la instancia única de UserFieldCmid.
	 * @return instancia singleton
	 */
	public static UserFieldCmid getInstance() {
		if (instance == null) {
			instance = new UserFieldCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//field id
		setCourseModuleById(log, ids.get(2));

	}

}
