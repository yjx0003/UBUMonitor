package es.ubu.lsi.ubumonitor.controllers;

public interface MainAction {

	public void onWebViewTabChange();

	public default void updateTreeViewGradeItem() {}

	public void updateListViewEnrolledUser();

	public void updatePredicadeEnrolledList();

	public default void updateListViewActivity() {} 

	public default void onSetTabLogs() {}

	public default void onSetTabGrades() {}

	public default void onSetTabActivityCompletion() {}

	public default void onSetSubTabLogs() {}

	public default void updateListViewComponents() {}

	public default void updateListViewEvents() {}

	public default void updateListViewSection() {}

	public default void updateListViewCourseModule() {}
	
	public default void updateListViewForum() {}

	public void saveImage();

	public void applyConfiguration();

}
