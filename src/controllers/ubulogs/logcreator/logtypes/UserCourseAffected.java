package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.Log;
/**
 * The user with id '' viewed the user report for the course with id '' for user with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserCourseAffected extends ReferencesLog {
	/**
	 * static Singleton instance.
	 */
	private static UserCourseAffected instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserCourseAffected() {
	}

	/**
	 * Return a singleton instance of UserCourseAffected.
	 */
	public static UserCourseAffected getInstance() {
		if (instance == null) {
			instance = new UserCourseAffected();
		}
		return instance;
	}

	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		// TODO Auto-generated method stub
		
	}

}
