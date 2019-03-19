package controllers;

import model.UBUGrades;

public class Controller {

	private static UBUGrades ubuGrade = UBUGrades.getInstance();
	
	/**
	 * static Singleton instance.
	 */
	private static Controller instance;

	/**
	 * Private constructor for singleton.
	 */
	private Controller() {
	}

	/**
	 * Return a singleton instance of Controller.
	 */
	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}
	
	
	
}
