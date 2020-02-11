package controllers;

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
	public void updateTreeViewGradeItem() {
		// do nothing

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
	public void updateListViewActivity() {
		// do nothing

	}

	@Override
	public void onSetTabLogs() {
		// do nothing

	}

	@Override
	public void onSetTabGrades() {
		// do nothing

	}

	@Override
	public void onSetTabActivityCompletion() {
		// do nothing

	}

	@Override
	public void onSetSubTabLogs() {
		// do nothing

	}

	@Override
	public void updateListViewComponents() {
		// do nothing

	}

	@Override
	public void updateListViewEvents() {
		// do nothing

	}

	@Override
	public void updateListViewSection() {
		// do nothing

	}

	@Override
	public void updateListViewCourseModule() {
		// do nothing

	}

	@Override
	public void save() {
		// do nothing

	}

	@Override
	public void applyConfiguration() {
		// do nothing

	}

}
