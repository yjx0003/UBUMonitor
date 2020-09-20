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
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.Style;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.load.Connection;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import okhttp3.Response;

public class UtilMethods {
	private static final Random RANDOM = new Random();

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

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
		return dialogWindow(AlertType.ERROR, I18n.get("error"), contentText);
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

	public static ButtonType dialogWindow(AlertType alertType, String headerText, String contentText) {
		Alert alert = createAlert(alertType, headerText, contentText);
		alert.showAndWait();
		return alert.getResult();
	}

	public static Alert createAlert(AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);
		alert.initModality(Modality.APPLICATION_MODAL);
		Stage stageAlert = (Stage) alert.getDialogPane()
				.getScene()
				.getWindow();
		stageAlert.getIcons()
				.add(new Image("/img/logo_min.png"));
		return alert;
	}

	public static Alert createAlert(AlertType alertType, String headerText, String contentText) {
		Alert alert = createAlert(alertType);
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

	public static void createDialog(FXMLLoader loader, Stage ownerStage, Modality modality) {

		Scene newScene;
		try {
			newScene = new Scene(loader.load());
		} catch (IOException ex) {
			errorWindow("FXML file corrupted", ex);
			return;
		}
		Style.addStyle(ConfigHelper.getProperty("style"), newScene.getStylesheets());

		Stage stage = createStage(ownerStage, modality);
		stage.setScene(newScene);
		stage.setResizable(false);

		stage.show();
	}

	public static void createDialog(FXMLLoader loader, Stage ownerStage) {

		createDialog(loader, ownerStage, Modality.WINDOW_MODAL);
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
			if (Desktop.isDesktopSupported() && Desktop.getDesktop()
					.isSupported(Desktop.Action.MAIL)) {
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
		if (initialDirectory != null) {
			File dir = new File(initialDirectory);
			if (dir.isDirectory()) {
				fileChooser.setInitialDirectory(dir);
			}
		}

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
		String extension = fileChooser.getSelectedExtensionFilter()
				.getExtensions()
				.get(0)
				.substring(1);
		if (!file.getName()
				.toLowerCase()
				.endsWith(extension)) {
			file = new File(file.getAbsolutePath() + extension);
		}
		try {
			consumer.accept(file);
			if (confirmationWindow) {
				showExportedFile(file);
			}
		} catch (FileNotFoundException e) {
			errorWindow(e.getMessage());
		} catch (Exception e) {
			errorWindow(e.getMessage(), e);
		}

	}

	public static void showExportedFile(File file) {
		Alert alert = createAlert(AlertType.INFORMATION);
		Hyperlink hyperLink = new Hyperlink(file.getAbsolutePath());
		hyperLink.setOnAction(e -> {
			alert.close();
			openFileFolder(file);

		});
		TextFlow flow = new TextFlow(new Text(I18n.get("message.export") + "\n"), hyperLink);
		alert.getDialogPane()
				.setContent(flow);
		alert.show();
	}

	public static void showExportedDir(File dir) {
		Alert alert = createAlert(AlertType.INFORMATION);
		Hyperlink hyperLink = new Hyperlink(dir.getAbsolutePath());
		hyperLink.setOnAction(e -> {
			alert.close();
			openFileFolder(dir);
		});
		TextFlow flow = new TextFlow(new Text(I18n.get("message.exportdir") + "\n"), hyperLink);
		alert.getDialogPane()
				.setContent(flow);
		alert.show();
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
	 * 
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

	public static <E> AutoCompletionBinding<E> createAutoCompletionBinding(TextField textField,
			Collection<E> possibleSuggestions) {
		AutoCompletionBinding<E> autoCompletionBinding = TextFields.bindAutoCompletion(textField, possibleSuggestions);
		autoCompletionBinding.setDelay(0);
		return autoCompletionBinding;
	}

	public static void snapshotNode(File file, Node node) throws IOException {
		WritableImage image = node.snapshot(new SnapshotParameters(), null);

		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
		
	}

	public static void openFileFolder(File file) {
		File path = file.isFile() ? file.getParentFile() : file;
		try {
			switch (OSUtil.getOS()) {
			case WINDOWS:
				Runtime.getRuntime()
						.exec("explorer.exe /select," + file.getAbsolutePath());
				break;
			case LINUX:
				Runtime.getRuntime()
						.exec("xdg-open", new String[] { path.getAbsolutePath() });
				break;
			case MAC:

				Runtime.getRuntime()
						.exec("open", new String[] { path.getAbsolutePath() });
				break;
			default:
				if (Desktop.isDesktopSupported() && Desktop.getDesktop()
						.isSupported(Desktop.Action.OPEN)) {

					Desktop.getDesktop()
							.open(path);
				} else {
					throw new IllegalAccessError("This system operative is not supported to open file");
				}

				break;

			}
		} catch (Exception e) {
			errorWindow("Cannot open in this System operative", e);
		}

	}

	/**
	 * Descarga una imagen de moodle, necesario usar los cookies en versiones
	 * posteriores al 3.5
	 * 
	 * @param url url de la image
	 * @return array de bytes de la imagen o un array de byte vacío si la url es
	 *         null
	 * @throws IOException si hay algun problema al descargar la imagen
	 */
	public static byte[] downloadImage(String url) {
		if (url == null) {
			return EMPTY_BYTE_ARRAY;
		}

		try {
			return Connection.getResponse(url)
					.body()
					.bytes();
		} catch (Exception e) {
			return EMPTY_BYTE_ARRAY;
		}

	}

	/**
	 * Busca la zona horaria del servidor a partir del perfil del usuario.
	 * 
	 * @return zona horaria del servidor.
	 * @throws IOException si no puede conectarse con el servidor
	 */
	public static ZoneId findServerTimezone(String host) {

		try {
			// vamos a la edicion del perfil de usuario para ver la zona horaria del
			// servidor
			Document d = Jsoup.parse(Connection.getResponse(host + "/user/edit.php?lang=en")
					.body()
					.string());

			// timezone tendra una estructuctura parecida a: Server timezone (Europe/Madrid)
			// lo unico que nos interesa es lo que hay dentro de los parentesis:
			// Europe/Madrid
			String timezone = d.selectFirst("select#id_timezone option[value=99]")
					.text();

			String timezoneParsed = timezone.substring(17, timezone.length() - 1);
			return ZoneId.of(timezoneParsed);
		} catch (Exception e) {
			return ZoneId.of("UTC");
		}

	}

	public static JSONArray getJSONArrayResponse(WebService webService, WSFunctionAbstract webServiceFunction)
			throws IOException {
		try (Response response = webService.getResponse(webServiceFunction)) {
			String string = response.body()
					.string();

			if (string.startsWith("{")) {
				JSONObject jsonObject = new JSONObject(string);
				throw new IllegalStateException(webServiceFunction + "\n" + jsonObject.optString("exception") + "\n"
						+ jsonObject.optString("message"));
			}

			return new JSONArray(string);

		}

	}

	public static JSONObject getJSONObjectResponse(WebService webService, WSFunctionAbstract webServiceFunction)
			throws IOException {
		try (Response response = webService.getResponse(webServiceFunction)) {
			JSONObject jsonObject = new JSONObject(new JSONTokener(response.body()
					.byteStream()));
			if (jsonObject.has("exception")) {
				throw new IllegalStateException(webServiceFunction + "\n" + jsonObject.optString("exception") + "\n"
						+ jsonObject.optString("message"));
			}
			return jsonObject;
		}

	}

	public static String colorToRGB(Color color) {

		return String.format("'rgba(%s,%s,%s,%s)'", color.getRed(), color.getGreen(), color.getBlue(),
				color.getAlpha());
	}

	public static boolean containsTextField(String newValue, String element) {
		if (newValue == null || newValue.isEmpty()) {
			return true;
		}
		String textField = newValue.toLowerCase();
		return element.toLowerCase()
				.contains(textField);
	}
	
	public static Color toAwtColor(javafx.scene.paint.Color color) {
		 return new Color((float) color.getRed(),
                 (float) color.getGreen(),
                 (float) color.getBlue(),
                 (float) color.getOpacity());
	}

}
