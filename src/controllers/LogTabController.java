package controllers;

public class LogTabController {

	/**
	 * static Singleton instance.
	 */
	private static LogTabController instance;

	/**
	 * Private constructor for singleton.
	 */
	private LogTabController() {
	}

	/**
	 * Return a singleton instance of LogTabController.
	 */
	public static LogTabController getInstance() {
		if (instance == null) {
			instance = new LogTabController();
		}
		return instance;
	}
	
	
	
}
