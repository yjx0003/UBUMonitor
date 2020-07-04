package es.ubu.lsi.ubumonitor.clustering.util;

import java.time.LocalDate;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;

/**
 * Clase de utilidad.
 * 
 * @author Xing Long Ji
 *
 */
public class JavaFXUtils {

	/**
	 * Devuelve un listener que limita los números hasta cuatro dígitos.
	 * 
	 * @param spinner componente spinner de JavaFX
	 * @return el listener
	 */
	public static ChangeListener<String> getSpinnerListener(Spinner<Integer> spinner) {
		return (obs, oldValue, newValue) -> {
			if (!newValue.matches("^[1-9]\\d{0,4}")) {
				spinner.getEditor().setText(oldValue);
			} else {
				spinner.getValueFactory().setValue(Integer.valueOf(newValue));
			}
		};
	}

	public static void initDatePickers(DatePicker datePickerStart, DatePicker datePickerEnd) {
		Controller controller = Controller.getInstance();
		datePickerStart.setValue(controller.getActualCourse().getStart());
		datePickerEnd.setValue(controller.getActualCourse().getEnd());

		datePickerStart.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.isAfter(datePickerEnd.getValue()));
			}
		});

		datePickerEnd.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.isBefore(datePickerStart.getValue()) || date.isAfter(LocalDate.now()));
			}
		});
	}

	private JavaFXUtils() {
		throw new UnsupportedOperationException();
	}

}
