package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * The user with id '' created an online text submission with '' words in the assignment with course module id ''.
 * The user with id '' updated an online text submission with '' words in the assignment with course module id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserWordsCmid extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserWordsCmid.
	 */
	private static UserWordsCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserWordsCmid() {
	}

	/**
	 * Devuelve la instancia única de UserWordsCmid.
	 * @return instancia singleton
	 */
	public static UserWordsCmid getInstance() {
		if (instance == null) {
			instance = new UserWordsCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//number of words
		setCourseModuleById(log, ids.get(2));
		
	}

}
