package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;


/**
 * 
 * The user with id '' created the custom scale with id '' from the course with the id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserScaleCourse extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserScaleCourse instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserScaleCourse() {
	}

	/**
	 * Return a singleton instance of UserScaleCourse.
	 */
	public static UserScaleCourse getInstance() {
		if (instance == null) {
			instance = new UserScaleCourse();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//TODO scale id
		// La id del curso se ignora de momento

	}

}
