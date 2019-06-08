package controllers.ubulogs.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * The user with id '' created the question category with id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserQuestioncategory extends ReferencesLog {

	
	/**
	 * Instacia única de la clase UserQuestioncategory.
	 */
	private static UserQuestioncategory instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserQuestioncategory() {
	}

	/**
	 * Devuelve la instancia única de UserQuestioncategory.
	 * @return instancia singleton
	 */
	public static UserQuestioncategory getInstance() {
		if (instance == null) {
			instance = new UserQuestioncategory();
		}
		return instance;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//question category id
		
	}

}
