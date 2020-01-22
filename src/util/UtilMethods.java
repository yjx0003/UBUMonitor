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
	 */
	public static void errorWindow(Stage stageOwner, String mensaje) {
		Alert alert = new Alert(AlertType.ERROR);

		alert.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);
		alert.setHeaderText("Error");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(stageOwner);
		alert.getDialogPane().setContentText(mensaje);
		Stage stageAlert = (Stage) alert.getDialogPane().getScene().getWindow();
		stageAlert.getIcons().add(new Image("/img/logo_min.png"));
		alert.getButtonTypes().setAll(ButtonType.CLOSE);

		alert.showAndWait();
	}

	/**
	 * Muestra una ventana de información.
	 * 
	 * @param mensaje El mensaje que se quiere mostrar.
	 * @param exit Indica si se quiere mostar el boton de salir o no.
	 */
	public static void infoWindow(Stage stageOwner, String mensaje) {
		Alert alert = new Alert(AlertType.INFORMATION);

		alert.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);
		alert.setHeaderText("Information");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(stageOwner);
		alert.getDialogPane().setContentText(mensaje);
		alert.getButtonTypes().setAll(ButtonType.OK);
		alert.showAndWait();

	}
}
