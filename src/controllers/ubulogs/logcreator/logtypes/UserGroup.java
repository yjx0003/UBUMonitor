package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * The user with id '' created the group with id ''.
 * The user with id '' deleted the group with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserGroup extends  ReferencesLog{

	/**
	 * Instacia única de la clase UserGroup.
	 */
	private static UserGroup instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserGroup() {
	}

	/**
	 * Devuelve la instancia única de UserGroup.
	 * @return instancia singleton
	 */
	public static UserGroup getInstance() {
		if (instance == null) {
			instance = new UserGroup();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//group id
		
	}

}
