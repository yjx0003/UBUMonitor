package es.ubu.lsi.ubumonitor.controllers;

import es.ubu.lsi.ubumonitor.model.Course;
import javafx.fxml.FXML;

public class SelectionMainController {
	
	@FXML
	private SelectionController selectionController;
	
	@FXML
	private SelectionForumController selectionForumController;
	
	@FXML
	private SelectionEmptyController selectionEmptyController;
	
	@FXML
	private SelectionCourseModuleController selectionCourseModuleController;

	public void init(MainController mainController, Course actualCourse) {
		selectionController.init(mainController, actualCourse);
		selectionForumController.init(mainController, actualCourse);
		selectionEmptyController.init(mainController, actualCourse);
		selectionCourseModuleController.init(mainController, actualCourse);
		
	}
	
	public SelectionController getSelectionController() {
		return selectionController;
	}
	
	public SelectionForumController getSelectionForumController() {
		return selectionForumController;
	}
	
	public SelectionCourseModuleController getSelectionCourseModuleController() {
		return selectionCourseModuleController;
	}
	
}
