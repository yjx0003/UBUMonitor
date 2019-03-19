package controllers.ubulogs.logcreator.logtypes;

import java.util.List;
import model.Log;



/**
 * 
 * The user with id '' has downloaded all the submissions for the assignment with course module id ''.
 * The user with id '' viewed the grading table for the assignment with course module id ''.
 * The user with id '' viewed the submission confirmation form for the assignment with course module id ''.
 * The user with id '' viewed their submission for the assignment with course module id ''.
 * The user with id '' has viewed the submission status page for the assignment with course module id ''.
 * The user with id '' has viewed the report for the choice activity with course module id ''.
 * The user with id '' viewed the '' activity with course module id ''.
 * The user with id '' updated the folder activity with course module id ''.
 * The user with id '' downloaded a zip archive containing all the files from the folder activity with course module id ''.
 * The user with id '' viewed the 'forum' activity with course module id ''.
 * The user with id '' viewed the 'page' activity with course module id ''.
 * The user with id '' viewed the 'quiz' activity with course module id ''.
 * The user with id '' viewed the edit page for the quiz with course module id ''.
 * The user with id '' viewed the report 'overview' for the quiz with course module id ''.
 * The user with id '' created the '' activity with course module id ''.
 * The user with id '' deleted the '' activity with course module id ''.
 * The user with id '' updated the '' activity with course module id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserCmid extends ReferencesLog {
	
	/**
	 * static Singleton instance.
	 */
	private static UserCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserCmid() {
	}

	/**
	 * Return a singleton instance of UserCmid.
	 */
	public static UserCmid getInstance() {
		if (instance == null) {
			instance = new UserCmid();
		}
		return instance;
	}

	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		setCourseModuleById(log, ids.get(1));
	}

}
