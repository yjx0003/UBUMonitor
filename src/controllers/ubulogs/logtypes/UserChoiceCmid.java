package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;

/**
 * The user with id '' has added the option with id '' for the\n            user with id '' from the choice activity with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserChoiceCmid extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserChoiceCmid.
	 */
	private static UserChoiceCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserChoiceCmid() {
	}

	/**
	 * Devuelve la instancia única de UserChoiceCmid.
	 * @return instancia singleton
	 */
	public static UserChoiceCmid getInstance() {
		if (instance == null) {
			instance = new UserChoiceCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//choice id
		setCourseModuleById(log, ids.get(2));

	}

}
