package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;
/**
 * The user with id '' created the category with id '' for the glossary activity with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserCategoryCmid extends ReferencesLog {

	/**
	 * Instacia única de la clase UserCategoryCmid.
	 */
	private static UserCategoryCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserCategoryCmid() {
	}

	/**
	 * Devuelve la instancia única de UserCategoryCmid.
	 * @return instancia singleton
	 */
	public static UserCategoryCmid getInstance() {
		if (instance == null) {
			instance = new UserCategoryCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//category id
		setCourseModuleById(log, ids.get(2));
		

	}

}
