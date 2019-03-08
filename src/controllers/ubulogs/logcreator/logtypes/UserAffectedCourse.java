package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.Log;
/**
 * The user with id '' viewed the profile for the user with id '' in the course with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserAffectedCourse extends ReferencesLog{

	/**
	 * static Singleton instance.
	 */
	private static UserAffectedCourse instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserAffectedCourse() {
	}

	/**
	 * Return a singleton instance of UserAffectedCourse.
	 */
	public static UserAffectedCourse getInstance() {
		if (instance == null) {
			instance = new UserAffectedCourse();
		}
		return instance;
	}
	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		// TODO Auto-generated method stub
		
	}

}
