package es.ubu.lsi.ubumonitor.clustering.util;

import javafx.beans.value.ChangeListener;
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

	private JavaFXUtils() {
		throw new UnsupportedOperationException();
	}

}
