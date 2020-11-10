package es.ubu.lsi.ubumonitor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;

public class DateController {

	@FXML
	private GridPane dateGridPane;
	
	@FXML
	private DatePicker datePickerStart;
	
	@FXML
	private DatePicker datePickerEnd;

	/**
	 * @return the datePickerStart
	 */
	public DatePicker getDatePickerStart() {
		return datePickerStart;
	}

	/**
	 * @return the datePickerEnd
	 */
	public DatePicker getDatePickerEnd() {
		return datePickerEnd;
	}
	
	/**
	 * @return the dateGridPane
	 */
	public GridPane getDateGridPane() {
		return dateGridPane;
	}
	
	public void resetDates() {
		datePickerStart.setValue(Controller.getInstance().getActualCourse().getStart());
		datePickerEnd.setValue(Controller.getInstance().getActualCourse().getEnd());
	}
	
	
}
