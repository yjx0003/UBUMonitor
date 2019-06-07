package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * The user with id '' created the override with id '' for the quiz with course module id '' for the group with id ''.
 * The user with id '' deleted the override with id '' for the quiz with course module id '' for the group with id ''
 * 
 * @author Yi Peng Ji
 *
 */
public class UserOverrideCmidGroup extends ReferencesLog {

	/**
	 * Instacia única de la clase UserOverrideCmidGroup.
	 */
	private static UserOverrideCmidGroup instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserOverrideCmidGroup() {
	}

	/**
	 * Devuelve la instancia única de UserOverrideCmidGroup.
	 * @return instancia singleton
	 */
	public static UserOverrideCmidGroup getInstance() {
		if (instance == null) {
			instance = new UserOverrideCmidGroup();
		}
		return instance;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//override id
		setCourseModuleById(log, ids.get(2));
		//group id
	}

}
