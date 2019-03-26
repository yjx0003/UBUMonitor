package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;


/**
 * The user with id '' viewed the course with id ''.
 * The user with id '' viewed the section number '' of the course with id ''.

 * @author Yi Peng Ji
 *
 */
public class SystemCourseViewed extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static SystemCourseViewed instance;

	/**
	 * Private constructor for singleton.
	 */
	private SystemCourseViewed() {
	}

	/**
	 * Return a singleton instance of SystemCourseViewed.
	 */
	public static SystemCourseViewed getInstance() {
		if (instance == null) {
			instance = new SystemCourseViewed();
		}
		return instance;
	}
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		ReferencesLog referencesLog = ids.size()==2? UserCourse.getInstance():UserSectionCourse.getInstance();
		referencesLog.setLogReferencesAttributes(log, ids);

	}

}
