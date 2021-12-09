package es.ubu.lsi.ubumonitor.controllers;

public class NullMainAction implements MainAction {

	/**
	 * static Singleton instance.
	 */
	private static NullMainAction instance;

	/**
	 * Private constructor for singleton.
	 */
	private NullMainAction() {
	}

	/**
	 * Return a singleton instance of NullMainAction.
	 */
	public static NullMainAction getInstance() {

		if (instance == null) {
			instance = new NullMainAction();

		}
		return instance;
	}

	@Override
	public void updateListViewEnrolledUser() {
		// do nothing

	}

	@Override
	public void updatePredicadeEnrolledList() {
		// do nothing

	}

	@Override
	public void saveImage() {
		// do nothing

	}

	@Override
	public void applyConfiguration() {
		// do nothing

	}

	@Override
	public void onWebViewTabChange() {
		// do nothing
		
	}

	@Override
	public void copyImage() {
		//do nothing
	}

}
