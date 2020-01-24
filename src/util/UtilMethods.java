package util;

import java.util.List;
import java.util.stream.Collectors;

import controllers.AppInfo;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UtilMethods {
	/**
	 * Escapa las comillas simples de un texto añadiendo un \
	 * 
	 * @param input texto
	 * @return texto escapado
	 */
	public static String escapeJavaScriptText(String input) {
		return input.replaceAll("'", "\\\\'");
	}

	/**
	 * Convierte una lista de elementos en string separados por comas
	 * 
	 * @param datasets
	 * @return
	 */
	public static <E> String join(List<E> datasets) {
		return datasets.stream().map(E::toString).collect(Collectors.joining(", "));
	}

	/**
	 * Convierte una lista en string con los elementos entre comillas y separado por
	 * comas.
	 * 
	 * @param list
	 * @return
	 */
	public static <T> String joinWithQuotes(List<T> list) {
		// https://stackoverflow.com/a/18229122
		return list.stream().map(s -> "'" + escapeJavaScriptText(s.toString()) + "'").collect(Collectors.joining(", "));
	}

	/**
	 * Removes reserved character from Windows filename.
	 * 
	 * @param stringToRemove need to remove reserved character
	 * @return without reserved character
	 */
	public static String removeReservedChar(String stringToRemove) {
		return stringToRemove.replaceAll(":|\\|/|\\?|\\*|\"|\\|", "");
	}

	/**
	 * Muestra una ventana de error.
	 * 
	 * @param mensaje El mensaje que se quiere mostrar.
	 * @return 
	 */
	public static ButtonType errorWindow(String contentText) {
		return dialogWindow(AlertType.ERROR, "Information", contentText);
	}

	/**
	 * Muestra una ventana de información.
	 * 
	 * @param message El mensaje que se quiere mostrar.
	 * @param exit Indica si se quiere mostar el boton de salir o no.
	 * @return 
	 */
	public static ButtonType infoWindow(String contentText) {
		
		return dialogWindow(AlertType.INFORMATION, "Information", contentText);
	}

	public static ButtonType confirmationWindow(String contentText) {
		return dialogWindow(AlertType.CONFIRMATION, "Confirmation", contentText);
	}

	private static ButtonType dialogWindow(AlertType alertType, String headerText, String contentText) {
		Alert alert = new Alert(alertType);
		alert.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);
		alert.initModality(Modality.APPLICATION_MODAL);
		Stage stageAlert = (Stage) alert.getDialogPane().getScene().getWindow();
		stageAlert.getIcons().add(new Image("/img/logo_min.png"));
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
		return alert.getResult();
	}

}
