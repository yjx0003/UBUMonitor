package controllers.ubulogs.logcreator.logtypes;

import java.util.List;

import model.LogLine;
/**
 * 
 * The user with id '' has viewed the tour with id '' at step index '' (id '') on the page with URL ''.
 * The user with id '' has ended the tour with id '' at step index '' (id '') on the page with URL ''.
 * The user with id '' has started the tour with id '' on the page with URL ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserTour extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserTour instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserTour() {
	}

	/**
	 * Return a singleton instance of UserTour.
	 */
	public static UserTour getInstance() {
		if (instance == null) {
			instance = new UserTour();
		}
		return instance;
	}
	
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//tour id
	}

}
