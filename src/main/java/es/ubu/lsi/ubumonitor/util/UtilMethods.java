package es.ubu.lsi.ubumonitor.util;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.Style;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.configuration.RemoteConfiguration;
import es.ubu.lsi.ubumonitor.controllers.load.Connection;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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
import javafx.util.Pair;
import okhttp3.Response;

public class UtilMethods {

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

		return join(datasets, ",");
	}

	public static <E> String join(Collection<E> collection, String joinCharacter) {
		if (collection == null || collection.isEmpty() || joinCharacter == null) {
			return "";
		}
		return collection.stream()
				.map(E::toString)
				.collect(Collectors.joining(joinCharacter));
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
		String newString = stringToRemove.replaceAll("\"|\\||<|>|:|\\\\|/|\\?|\\*|\\|", ""); // FIX #139 added more dangerous characters 
		return newString.trim();
		
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

	public static void errorWindow(String contentText, Throwable ex) {

		Alert alert = createAlert(AlertType.ERROR);

		Hyperlink hyperLink = new Hyperlink(I18n.get("label.microsoftFormPolicy"));
		hyperLink.setOnAction(e -> openURL(I18n.get("url.microsoftFormPolicy"))

		);
		Text text = new Text(contentText + "\n\n" + I18n.get("askSendReport"));

		TextFlow flow = new TextFlow(text, hyperLink);
		flow.setPrefWidth(600);

		alert.getDialogPane()
				.setContent(flow);

		ButtonType sendReport = new ButtonType(I18n.get("button.sendReport"), ButtonData.YES);

		alert.getButtonTypes()
				.setAll(sendReport, ButtonType.NO);

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

		Button btOk = (Button) alert.getDialogPane()
				.lookupButton(sendReport);
		btOk.addEventFilter(ActionEvent.ACTION, event -> {
			RemoteConfiguration remoteConfiguration = RemoteConfiguration.getInstance();
			JSONObject errorReportConfiguration = remoteConfiguration.getJSONObject("errorReport");
			MicrosoftForms microsoftForm = new MicrosoftForms(errorReportConfiguration.getString("url"));
			microsoftForm.addAnswer(errorReportConfiguration.getString("messageQuestionId"),
					Instant.now() + "\n" + contentText);
			String trace = sw.toString();

			microsoftForm.addAnswer(errorReportConfiguration.getString("traceQuestionId"), trace);
			microsoftForm.addAnswer(errorReportConfiguration.getString("traceQuestionId2"),
					trace.substring(Math.max(trace.length() - MicrosoftForms.LIMIT_CHARACTERS, 0), trace.length()));

			try (Response response = Connection.getResponse(microsoftForm.getRequest())) {
				if (!response.isSuccessful()) {
					throw new IllegalStateException(response.body()
							.string());
				}
				infoWindow(I18n.get("ok.reportSent"));
			} catch (Exception e) {
				errorWindow(I18n.get("error.cantSendReport") + ":\n" + e.getMessage());
				event.consume();
			}
		});

		alert.showAndWait();

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

	public static Stage createDialog(FXMLLoader loader, Stage ownerStage, Modality modality) {

		Scene newScene;
		try {
			newScene = new Scene(loader.load());
		} catch (IOException ex) {
			errorWindow("FXML file corrupted", ex);
			return null;
		}
		Style.addStyle(ConfigHelper.getProperty("style"), newScene.getStylesheets());

		Stage stage = createStage(ownerStage, modality);
		stage.setScene(newScene);
		stage.setResizable(false);

		stage.show();
		return stage;
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
		try {
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

			consumer.accept(file);
			if (confirmationWindow) {
				showExportedFile(file);
			}
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
		return new Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(),
				(float) color.getOpacity());
	}

	public static <T> void fillCheckComboBox(T dummy, Collection<T> values, CheckComboBox<T> checkComboBox) {
		if (values == null || values.isEmpty()) {
			return;
		}

		checkComboBox.getItems()
				.add(dummy);
		checkComboBox.getItems()
				.addAll(values);
		checkComboBox.getCheckModel()
				.checkAll();

		checkComboBox.getItemBooleanProperty(0)
				.addListener((observable, oldValue, newValue) -> {
					if (newValue.booleanValue()) {
						checkComboBox.getCheckModel()
								.checkAll();
					} else {
						checkComboBox.getCheckModel()
								.clearChecks();
					}

				});

	}

	public static <T, V extends Comparable<V>> Map<T, Integer> ranking(Map<T, V> map) {
		return ranking(map, Function.identity());
	}

	public static <T, V, E extends Comparable<E>> Map<T, Integer> ranking(Map<T, V> map, Function<V, E> function) {

		if (map.isEmpty()) {
			return Collections.emptyMap();
		}

		List<Pair<T, V>> scores = new ArrayList<>(map.size());
		map.forEach((k, v) -> scores.add(new Pair<>(k, v)));
		scores.sort(Comparator.comparing(Pair::getValue, Comparator.comparing(function)
				.reversed()));

		Map<T, Integer> ranking = new HashMap<>();
		ranking.put(scores.get(0)
				.getKey(), 1);
		int actualRank = 1;
		for (int i = 1; i < scores.size(); i++) {
			actualRank = function.apply(scores.get(i)
					.getValue())
					.equals(function.apply(scores.get(i - 1)
							.getValue())) ? actualRank : i + 1;
			ranking.put(scores.get(i)
					.getKey(), actualRank);
		}
		return ranking;
	}

	public static <T, V extends Comparable<V>> Map<T, Double> rankingStatistics(Map<T, V> map) {
		return rankingStatistics(map, Function.identity());
	}

	public static <T, V, E extends Comparable<E>> Map<T, Double> rankingStatistics(Map<T, V> map,
			Function<V, E> function) {
		Map<T, Double> ranking = new HashMap<>();
		for (Map.Entry<T, V> entry : map.entrySet()) {
			V actualValue = entry.getValue();
			int lessThaActualValue = 0;
			int equal = 1;
			for (V value : map.values()) {
				int compare = function.apply(actualValue)
						.compareTo(function.apply(value));
				if (compare < 0) {
					++lessThaActualValue;
				} else if (compare == 0) {
					++equal;
				}
			}
			ranking.put(entry.getKey(), lessThaActualValue + equal / 2.0);

		}

		return ranking;
	}

	public static List<GradeItem> getSelectedGradeItems(TreeView<GradeItem> treeView) {
		return treeView.getSelectionModel()
				.getSelectedItems()
				.stream()
				.filter(Objects::nonNull)
				.map(TreeItem::getValue)
				.collect(Collectors.toList());
	}

	public static String getDifferenceTime(Instant start, Instant end) {
		if (start != null && end != null && start.getEpochSecond() != 0) {
			return Controller.DATE_TIME_FORMATTER.format(start.atZone(ZoneId.systemDefault())) + " ("
					+ UtilMethods.formatDates(start, end) + ")";
		}

		return I18n.get("text.never");
	}

	public static <T extends Number & Comparable<T>> Number getMax(Collection<T> values) {
		if (values == null || values.isEmpty()) {
			return 0;
		}
		return Collections.max(values);
	}
	
	public static void noGradeValues(double grade, DescriptiveStatistics descriptiveStatistics, boolean noGrade) {
		if(!Double.isNaN(grade)) {
			descriptiveStatistics.addValue(grade);
			
		} else if(noGrade){
			descriptiveStatistics.addValue(0);
		}
	}
	
	public static void noGradeValues(double grade, JSArray jsArray, boolean noGrade) {
		if(!Double.isNaN(grade)) {
			jsArray.add(grade);
			
		} else if(noGrade){
			jsArray.add(0);
		}
	}

}
