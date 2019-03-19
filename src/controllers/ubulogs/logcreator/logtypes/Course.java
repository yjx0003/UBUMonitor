package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.Log;
/**
 * The reset of the course with id '' has ended.
 * 
 * @author Yi Peng Ji
 *
 */
public class Course extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static Course instance;

	/**
	 * Private constructor for singleton.
	 */
	private Course() {
	}

	/**
	 * Return a singleton instance of Course.
	 */
	public static Course getInstance() {
		if (instance == null) {
			instance = new Course();
		}
		return instance;
	}
	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		// El curso ya se enlaza al crear el log en createLogWithBasicAttributes de la clase ReferencesLog

	}

}
