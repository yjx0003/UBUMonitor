package es.ubu.lsi.ubumonitor.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import es.ubu.lsi.ubumonitor.controllers.AppInfo;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UtilMethods {
	private UtilMethods() {
	}

	/**
	 * Escapa las comillas simples de un texto añadiendo un \
	 * 
	 * @param input texto
	 * @return texto escapado
	 */
	public static String escapeJavaScriptText(String input) {
		return input.replace("'", "\\\'");
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

	public static ButtonType errorWindow(String contentText, Throwable ex) {

		Alert alert = createAlert(AlertType.ERROR, "Error", contentText);

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(sw.toString());
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		alert.showAndWait();
		return alert.getResult();
	}

	private static ButtonType dialogWindow(AlertType alertType, String headerText, String contentText) {
		Alert alert = createAlert(alertType, headerText, contentText);
		alert.showAndWait();
		return alert.getResult();
	}

	private static Alert createAlert(AlertType alertType, String headerText, String contentText) {
		Alert alert = new Alert(alertType);
		alert.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);
		alert.initModality(Modality.APPLICATION_MODAL);
		Stage stageAlert = (Stage) alert.getDialogPane().getScene().getWindow();
		stageAlert.getIcons().add(new Image("/img/logo_min.png"));
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		return alert;
	}

	public static void changeScene(URL url, Stage stage) {
		changeScene(url, stage, null, true);
	}

	public static void changeScene(URL url, Stage stage, Object fxmlController) {
		changeScene(url, stage, fxmlController, true);
	}

	public static void changeScene(URL url, Stage stage, boolean showStage) {
		changeScene(url, stage, null, showStage);
	}

	/**
	 * Permite cambiar la ventana actual.
	 * 
	 * @param sceneFXML La ventanan a la que se quiere cambiar.
	 *
	 * @throws IOException
	 */
	public static void changeScene(URL sceneFXML, Stage stage, Object fxmlController, boolean showStage) {

		// Accedemos a la siguiente ventana
		FXMLLoader loader = new FXMLLoader(sceneFXML, I18n.getResourceBundle());
		if (fxmlController != null) {
			loader.setController(fxmlController);
		}

		try {

			Parent root = loader.load();

			if (stage.getScene() == null) {
				stage.setScene(new Scene(root));
			} else {
				stage.getScene().setRoot(root);

			}
			stage.close();
			if (showStage) {
				stage.show();
			}
		} catch (IOException e) {
			errorWindow("error loading fxml: " + sceneFXML, e);
			throw new IllegalArgumentException("Invalid fxml");
		}

	}

	public static void openURL(String url) {
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(url));
			}
		} catch (Exception e) {
			errorWindow("Cannot open " + url + " in the navigator");
		}
	}

	public static void mailTo(String mail) {
		try {
			if (Desktop.isDesktopSupported() && (Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
				Desktop.getDesktop().mail(new URI("mailto:" + mail));
			}
		} catch (Exception e) {
			errorWindow("Cannot open " + mail + " in the default mail client.");
		}
	}

	public static FileChooser createFileChooser(String title, String initialFileName, String initialDirectory,
			FileChooser.ExtensionFilter... extensionFilters) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.setInitialFileName(initialFileName);
		fileChooser.setInitialDirectory(new File(initialDirectory));
		fileChooser.getExtensionFilters().addAll(extensionFilters);
		return fileChooser;
	}
}
