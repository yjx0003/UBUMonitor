package es.ubu.lsi.ubumonitor.util;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.Style;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class UtilMethods {
	private static final Random RANDOM = new Random();

	private UtilMethods() {
	}

	/**
	 * Selecciona colores pseudo-aleatorios a partir del HSV
	 * 
	 * @param <E>
	 */
	public static <E> Map<E, Color> getRandomColors(List<E> typeLogs) {
		Map<E, Color> colors = new HashMap<>();

		for (int i = 0; i < typeLogs.size(); i++) {

			// generamos un color a partir de HSB, el hue(H) es el color, saturacion (S), y
			// brillo(B)
			Color c = new Color(Color.HSBtoRGB(i / (float) typeLogs.size(), 0.8f, 1.0f));
			colors.put(typeLogs.get(i), c);
		}
		return colors;

	}

	/**
	 * Escapa las comillas simples de un texto añadiendo un \
	 * 
	 * @param input texto
	 * @return texto escapado
	 */
	public static String escapeJavaScriptText(String input) {

		return input.replace("'", "\\\'")
				.replaceAll("\\R", "");

	}

	/**
	 * Convierte una lista de elementos en string separados por comas
	 * 
	 * @param datasets
	 * @return
	 */
	public static <E> String join(List<E> datasets) {
		return datasets.stream()
				.map(E::toString)
				.collect(Collectors.joining(","));
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
		return list.stream()
				.map(s -> "'" + escapeJavaScriptText(s.toString()) + "'")
				.collect(Collectors.joining(","));
	}

	/**
	 * Removes reserved character from Windows filename.
	 * 
	 * @param stringToRemove need to remove reserved character
	 * @return without reserved character
	 */
	public static String removeReservedChar(String stringToRemove) {
		return stringToRemove.replaceAll(":|\\\\|/|\\?|\\*|\\|", "");
	}

	/**
	 * Muestra una ventana de error.
	 * 
	 * @param mensaje El mensaje que se quiere mostrar.
	 * @return
	 */
	public static ButtonType errorWindow(String contentText) {
		return dialogWindow(AlertType.ERROR,I18n.get("error"), contentText);
	}

	/**
	 * Muestra una ventana de información.
	 * 
	 * @param message El mensaje que se quiere mostrar.
	 * @param exit    Indica si se quiere mostar el boton de salir o no.
	 * @return
	 */
	public static ButtonType infoWindow(String contentText) {

		return dialogWindow(AlertType.INFORMATION, I18n.get("information"), contentText);
	}

	public static ButtonType confirmationWindow(String contentText) {
		return dialogWindow(AlertType.CONFIRMATION, I18n.get("confirmation"), contentText);
	}

	public static ButtonType warningWindow(String contentText) {
		return dialogWindow(AlertType.WARNING, I18n.get("warning"), contentText);
	}

	public static ButtonType errorWindow(String contentText, Throwable ex) {

		Alert alert = createAlert(AlertType.ERROR, "Error", contentText);

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		Label label = new Label(I18n.get("exceptionTrace"));

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
		alert.getDialogPane()
				.setExpandableContent(expContent);
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
		Stage stageAlert = (Stage) alert.getDialogPane()
				.getScene()
				.getWindow();
		stageAlert.getIcons()
				.add(new Image("/img/logo_min.png"));
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
			stage.close();

			stage.setScene(new Scene(root));

			if (showStage) {
				stage.show();
			}
		} catch (IOException e) {
			errorWindow("error loading fxml: " + sceneFXML, e);
			throw new IllegalArgumentException("Invalid fxml", e);
		}

	}

	public static void createDialog(FXMLLoader loader, Stage ownerStage) {

		Scene newScene;
		try {
			newScene = new Scene(loader.load());
		} catch (IOException ex) {
			errorWindow("FXML file corrupted", ex);
			return;
		}
		Style.addStyle(ConfigHelper.getProperty("style"), newScene.getStylesheets());

		Stage stage = createStage(ownerStage, Modality.WINDOW_MODAL);
		stage.setScene(newScene);
		stage.setResizable(false);

		stage.show();
	}

	public static void openURL(String url) {
		// from
		// http://www.java2s.com/Code/Java/Development-Class/LaunchBrowserinMacLinuxUnix.htm
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL", String.class);
				openURL.invoke(null, url);
			} else if (osName.startsWith("Windows")) {
				Runtime.getRuntime()
						.exec("rundll32 url.dll,FileProtocolHandler " + url);
			}

			else { // assume Unix or Linux
				String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime()
							.exec(new String[] { "which", browsers[count] })
							.waitFor() == 0)
						browser = browsers[count];
				if (browser == null)
					throw new IllegalStateException("Could not find web browser");
				else
					Runtime.getRuntime()
							.exec(new String[] { browser, url });
			}
		} catch (Exception e) {
			errorWindow("Cannot open " + url + " in web browser", e);
		}
	}

	public static void mailTo(String mail) {
		try {
			if (Desktop.isDesktopSupported() && (Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
				Desktop.getDesktop()
						.mail(new URI("mailto:" + mail));
			}
		} catch (Exception e) {
			errorWindow("Cannot open " + mail + " in the default mail client.");
		}
	}

	public static FileChooser createFileChooser(String initialFileName, String initialDirectory,
			FileChooser.ExtensionFilter... extensionFilters) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);
		fileChooser.setInitialFileName(initialFileName);
		fileChooser.setInitialDirectory(new File(initialDirectory));
		fileChooser.getExtensionFilters()
				.addAll(extensionFilters);
		return fileChooser;
	}

	public static void fileAction(String initialFileName, String initialDirectory, Window owner,
			FileUtil.FileChooserType fileChooserType, FileUtil.ThrowingConsumer<File, IOException> consumer,
			boolean confirmationWindow, FileChooser.ExtensionFilter... extensionFilters) {
		FileChooser fileChooser = createFileChooser(initialFileName, initialDirectory, extensionFilters);
		File file = fileChooserType.getFile(fileChooser, owner);
		if (file == null) {
			return;
		}

		try {
			consumer.accept(file);
			if (confirmationWindow) {
				infoWindow(I18n.get("message.export") + file.getAbsolutePath());
			}
		} catch (FileNotFoundException e) {
			errorWindow(e.getMessage());
		} catch (Exception e) {
			errorWindow(e.getMessage(), e);
		}

	}

	public static void fileAction(String initialFileName, String initialDirectory, Window owner,
			FileUtil.FileChooserType fileChooserType, FileUtil.ThrowingConsumer<File, IOException> consumer,
			FileChooser.ExtensionFilter... extensionFilters) {
		fileAction(initialFileName, initialDirectory, owner, fileChooserType, consumer, true, extensionFilters);

	}

	/**
	 * Devuelve la diferencia entre dos instantes, por dias, hora, minutos o
	 * segundos siempre y cuando no sean 0. Si la diferencias de dias es 0 se busca
	 * por horas, y asi consecutivamente.
	 * 
	 * @param lastCourseAccess fecha inicio
	 * @param lastLogInstant   fecha fin
	 * @return texto con el numero y el tipo de tiempo
	 */
	public static String formatDates(Instant lastCourseAccess, Instant lastLogInstant) {

		if (lastCourseAccess == null || lastCourseAccess.getEpochSecond() == -1L) {
			return " " + I18n.get("label.never");
		}

		if (lastCourseAccess.getEpochSecond() == 0L) {
			return " " + I18n.get("text.never");
		}

		long time;

		if ((time = betweenDates(ChronoUnit.DAYS, lastCourseAccess, lastLogInstant)) != 0L) {
			return (time < 0 ? 0 : time) + " " + I18n.get("text.days");
		}
		if ((time = betweenDates(ChronoUnit.HOURS, lastCourseAccess, lastLogInstant)) != 0L) {
			return (time < 0 ? 0 : time) + " " + I18n.get("text.hours");
		}
		if ((time = betweenDates(ChronoUnit.MINUTES, lastCourseAccess, lastLogInstant)) != 0L) {
			return (time < 0 ? 0 : time) + " " + I18n.get("text.minutes");
		}
		time = betweenDates(ChronoUnit.SECONDS, lastCourseAccess, lastLogInstant);
		long timeSeconds = time < 0 ? 0 : time;
		return timeSeconds + " " + I18n.get("text.seconds");
	}

	/**
	 * Devuelve la difernecia absoluta entre dos instantes dependiendo de que sea el
	 * tipo
	 * 
	 * @param type             dias, horas, minutos
	 * @param lastCourseAccess instante inicio
	 * @param lastLogInstant   instante fin
	 * @return numero de diferencia absoluta
	 */
	private static long betweenDates(ChronoUnit type, Instant lastCourseAccess, Instant lastLogInstant) {
		return type.between(lastCourseAccess, lastLogInstant);
	}

	public static Stage createStage(Window owner, Modality modality) {
		Stage stage = new Stage();

		stage.initOwner(owner);
		stage.initModality(modality);

		stage.getIcons()
				.add(new Image("/img/logo_min.png"));
		stage.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);
		return stage;
	}

	/**
	 * https://www.baeldung.com/java-random-string
	 * @return
	 */
	public static String ranmdomAlphanumeric() {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;

		return RANDOM.ints(leftLimit, rightLimit + 1)
			      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
			      .limit(targetStringLength)
			      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			      .toString();

	}
	
	public static <E> AutoCompletionBinding<E> createAutoCompletionBinding(TextField textField, Collection<E> possibleSuggestions){
		AutoCompletionBinding<E> autoCompletionBinding = TextFields.bindAutoCompletion(textField, possibleSuggestions);
		autoCompletionBinding.setDelay(0);
		return autoCompletionBinding;
	}
	
}
