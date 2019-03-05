package controllers.UBULogs.logCreator.logsTypes;

import java.util.List;

import model.Log;
/**
 * The user with id '' deleted the grade with id '' for the user with id '' for the grade item with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserGradeAffectedGradeitem extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserGradeAffectedGradeitem instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserGradeAffectedGradeitem() {
	}

	/**
	 * Return a singleton instance of UserGradeAffectedGradeitem.
	 */
	public static UserGradeAffectedGradeitem getInstance() {
		if (instance == null) {
			instance = new UserGradeAffectedGradeitem();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		// TODO Auto-generated method stub
		
	}

}
