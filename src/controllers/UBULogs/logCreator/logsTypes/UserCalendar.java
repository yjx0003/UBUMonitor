package controllers.UBULogs.logCreator.logsTypes;

import java.util.List;

import model.Log;
/**
 * The user with id '' created the event '' with id ''.
 * The user with id '' deleted the event '' with id ''.
 * The user with id '' updated the event '' with id ''.
 * @author Yi Peng Ji
 *
 */
public class UserCalendar extends ReferencesLog {

	/**
	 * static Singleton instance.
	 */
	private static UserCalendar instance;

	/**
	 * Private constructor for singleton.
	 */
	private UserCalendar() {
	}

	/**
	 * Return a singleton instance of UserCalendar.
	 */
	public static UserCalendar getInstance() {
		if (instance == null) {
			instance = new UserCalendar();
		}
		return instance;
	}
	@Override
	public void setLogReferencesAttributes(Log log, List<Integer> ids) {
		// TODO Auto-generated method stub

	}

}
