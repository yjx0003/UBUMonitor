package controllers.UBULogs.logCreator.logsTypes;

import java.util.List;

import model.Log;
/**
 * The user with id '' created the group with id ''.
 * The user with id '' deleted the group with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserGroup extends  ReferencesLog{

	/**
	 * static Singleton instance.
	 */
	private static UserGroup instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserGroup() {
	}

	/**
	 * Return a singleton instance of UserGroup.
	 */
	public static UserGroup getInstance() {
		if (instance == null) {
			instance = new UserGroup();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		// TODO Auto-generated method stub
		
	}

}
