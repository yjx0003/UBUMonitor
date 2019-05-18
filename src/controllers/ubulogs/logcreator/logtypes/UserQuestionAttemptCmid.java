package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * The user with id '' manually graded the question with id '' for the attempt with id '' for the quiz with course module id ''.
 * @author Yi Peng Ji
 *
 */
public class UserQuestionAttemptCmid extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserQuestionAttemptCmid instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserQuestionAttemptCmid() {
	}

	/**
	 * Return a singleton instance of UserQuestionAttemptCmid.
	 */
	public static UserQuestionAttemptCmid getInstance() {
		if (instance == null) {
			instance = new UserQuestionAttemptCmid();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		// TODO Auto-generated method stub

	}

}
