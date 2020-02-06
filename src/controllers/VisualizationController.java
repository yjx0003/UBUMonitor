package controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.webkit.WebConsoleListener;

import controllers.charts.Tabs;
import controllers.configuration.Config;
import controllers.configuration.MainConfiguration;
import controllers.ubulogs.GroupByAbstract;
import controllers.ubulogs.TypeTimes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import model.LogStats;
import netscape.javascript.JSObject;
import util.UtilMethods;

public class VisualizationController implements MainAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(VisualizationController.class);

	private Controller controller = Controller.getInstance();
	private MainController mainController;
	private JavaConnector javaConnector;
	@FXML
	private WebView webViewCharts;
	private WebEngine webViewChartsEngine;
	@FXML
	private TextField textFieldMax;
	@FXML
	private ProgressBar progressBar;

	@FXML
	private GridPane optionsUbuLogs;
	@FXML
	private ChoiceBox<GroupByAbstract<?>> choiceBoxDate;

	@FXML
	private GridPane dateGridPane;

	@FXML
	private DatePicker datePickerStart;

	@FXML
	private DatePicker datePickerEnd;

	public void init(MainController mainController) {
		this.mainController = mainController;
		initLogOptionsFilter();
		initTabPaneWebView();
		
	}

	private void initTabPaneWebView() {
		// Cargamos el html de los graficos y calificaciones
		webViewCharts.setContextMenuEnabled(false); // Desactiva el click derecho
		webViewChartsEngine = webViewCharts.getEngine();
		javaConnector = new JavaConnector(this);

		progressBar.progressProperty().bind(webViewChartsEngine.getLoadWorker().progressProperty());

		WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> {
			LOGGER.error("{} [{} at {}] ", message, sourceId, lineNumber);
			// errorWindow(message + "[" + sourceId + " at " + lineNumber, false);
		});
		// Comprobamos cuando se carga la pagina para traducirla
		webViewChartsEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
			if (Worker.State.SUCCEEDED != newState)
				return;
			JSObject window = (JSObject) webViewChartsEngine.executeScript("window");
			window.setMember("javaConnector", javaConnector);
			webViewChartsEngine.executeScript("setLanguage()");
			webViewCharts.toFront();
			javaConnector.setDefaultValues();

			javaConnector.updateChart();
		});
		webViewChartsEngine.load(getClass().getResource("/graphics/Charts.html").toExternalForm());

	}

	/**
	 * Inicializa los elementos de las opciones de logs.
	 */
	public void initLogOptionsFilter() {

		textFieldMax.textProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue == null || newValue.isEmpty() || newValue.matches("\\d+")) {
				updateMaxScale();

			} else { // si no es un numero volvemos al valor anterior
				textFieldMax.setText(oldValue);
			}
		});
		LogStats logStats = controller.getActualCourse().getLogStats();
		TypeTimes typeTime = controller.getMainConfiguration().getValue(MainConfiguration.GENERAL, "initialTypeTimes");
		// añadimos los elementos de la enumeracion en el choicebox
		ObservableList<GroupByAbstract<?>> typeTimes = FXCollections.observableArrayList(logStats.getList());
		choiceBoxDate.setItems(typeTimes);
		choiceBoxDate.getSelectionModel().select(logStats.getByType(typeTime));

		choiceBoxDate.valueProperty().addListener((ov, oldValue, newValue) -> {
			applyFilterLogs();
			boolean useDatePicker = newValue.useDatePicker();
			dateGridPane.setVisible(useDatePicker);

		});

		// traduccion de los elementos del choicebox
		choiceBoxDate.setConverter(new StringConverter<GroupByAbstract<?>>() {
			@Override
			public GroupByAbstract<?> fromString(String typeTimes) {
				return null;// no se va a usar en un choiceBox.
			}

			@Override
			public String toString(GroupByAbstract<?> typeTimes) {
				return I18n.get(typeTimes.getTypeTime());
			}
		});

		datePickerStart.setValue(controller.getActualCourse().getStart());
		datePickerEnd.setValue(controller.getActualCourse().getEnd());

		datePickerStart.setOnAction(event -> applyFilterLogs());
		datePickerEnd.setOnAction(event -> applyFilterLogs());

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

		optionsUbuLogs.visibleProperty().bind(mainController.getTabUbuLogs().selectedProperty());
		optionsUbuLogs.managedProperty().bind(mainController.getTabUbuLogs().selectedProperty());
	}

	/**
	 * Actualiza la escala maxima del eje y de los graficos de logs.
	 * 
	 * @param value valor de escala maxima
	 */
	private void updateMaxScale() {
		if (webViewChartsEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED && textFieldMax.isFocused())
			javaConnector.updateChart(false);

	}

	/**
	 * Aplica los filtros de fecha a las graficas de log.
	 * 
	 * @param event evento
	 */
	public void applyFilterLogs() {
		findMaxaAndUpdateChart();

	}

	/**
	 * Busca el maximo de la escala Y y lo modifica.
	 */
	private void findMax() {
		javaConnector.setMax();
	}

	@Override
	public void updateTreeViewGradeItem() {
		javaConnector.updateChart();
	}

	@Override
	public void updateListViewEnrolledUser() {
		javaConnector.updateChart();
	}

	@Override
	public void updatePredicadeEnrolledList() {
		findMaxaAndUpdateChart();

	}

	@Override
	public void updateListViewActivity() {
		javaConnector.updateChart();

	}

	@Override
	public void onSetTabLogs() {
		javaConnector.setCurrentType(javaConnector.getCurrentTypeLogs());
		findMaxaAndUpdateChart();

		webViewChartsEngine.executeScript("manageButtons('" + Tabs.LOGS + "')");

	}

	@Override
	public void onSetTabGrades() {
		javaConnector.setCurrentType(javaConnector.getCurrentTypeGrades());
		javaConnector.updateChart();
		webViewChartsEngine.executeScript("manageButtons('" + Tabs.GRADES + "')");
	}

	@Override
	public void onSetTabActivityCompletion() {
		javaConnector.setCurrentType(javaConnector.getCurrentTypeActivityCompletion());
		javaConnector.updateChart();
		webViewChartsEngine.executeScript("manageButtons('" + Tabs.ACTIVITY_COMPLETION + "')");
	}

	@Override
	public void onSetSubTabLogs() {
		findMaxaAndUpdateChart();

	}

	@Override
	public void updateListViewComponents() {
		findMaxaAndUpdateChart();

	}

	@Override
	public void updateListViewEvents() {
		findMaxaAndUpdateChart();

	}

	@Override
	public void updateListViewSection() {
		findMaxaAndUpdateChart();

	}

	@Override
	public void updateListViewCourseModule() {
		findMaxaAndUpdateChart();
	}

	private void findMaxaAndUpdateChart() {
		findMax();
		javaConnector.updateChart();
	}

	@Override
	public void save() {
		FileChooser fileChooser = new FileChooser();

		fileChooser.setInitialFileName(String.format("%s_%s_%s.png", controller.getActualCourse().getId(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss")),
				javaConnector.getCurrentType().getChartType()));
		fileChooser.setInitialDirectory(new File(Config.getProperty("imageFolderPath", "./")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".png", "*.png"));
		try {
			File file = fileChooser.showSaveDialog(controller.getStage());
			if (file != null) {
				String str = javaConnector.export(file);
				if (str == null)
					return;
				javaConnector.saveImage(str);
				Config.setProperty("imageFolderPath", file.getParent());
			}
		} catch (IOException e) {
			LOGGER.error("Error al guardar el gráfico: {}", e);
			UtilMethods.errorWindow(I18n.get("error.savechart"), e);
		}

	}

	@Override
	public void applyConfiguration() {
		javaConnector.updateChart();

	}

	public MainController getMainController() {
		return mainController;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public JavaConnector getJavaConnector() {
		return javaConnector;
	}

	public void setJavaConnector(JavaConnector javaConnector) {
		this.javaConnector = javaConnector;
	}

	public WebView getWebViewCharts() {
		return webViewCharts;
	}

	public void setWebViewCharts(WebView webViewCharts) {
		this.webViewCharts = webViewCharts;
	}

	public WebEngine getWebViewChartsEngine() {
		return webViewChartsEngine;
	}

	public void setWebViewChartsEngine(WebEngine webViewChartsEngine) {
		this.webViewChartsEngine = webViewChartsEngine;
	}

	public TextField getTextFieldMax() {
		return textFieldMax;
	}

	public void setTextFieldMax(TextField textFieldMax) {
		this.textFieldMax = textFieldMax;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public GridPane getOptionsUbuLogs() {
		return optionsUbuLogs;
	}

	public void setOptionsUbuLogs(GridPane optionsUbuLogs) {
		this.optionsUbuLogs = optionsUbuLogs;
	}

	public ChoiceBox<GroupByAbstract<?>> getChoiceBoxDate() {
		return choiceBoxDate;
	}

	public void setChoiceBoxDate(ChoiceBox<GroupByAbstract<?>> choiceBoxDate) {
		this.choiceBoxDate = choiceBoxDate;
	}

	public GridPane getDateGridPane() {
		return dateGridPane;
	}

	public void setDateGridPane(GridPane dateGridPane) {
		this.dateGridPane = dateGridPane;
	}

	public DatePicker getDatePickerStart() {
		return datePickerStart;
	}

	public void setDatePickerStart(DatePicker datePickerStart) {
		this.datePickerStart = datePickerStart;
	}

	public DatePicker getDatePickerEnd() {
		return datePickerEnd;
	}

	public void setDatePickerEnd(DatePicker datePickerEnd) {
		this.datePickerEnd = datePickerEnd;
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

}
