package es.ubu.lsi.ubumonitor.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.LogStats;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.FileUtil;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.VisualizationJavaConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import netscape.javascript.JSObject;

public class VisualizationController implements MainAction {

	private static final String CSV_FOLDER_PATH = "csvFolderPath";
	private static final String YYYY_M_MDDHHMMSS = "yyyyMMddhhmmss";
	private Controller controller = Controller.getInstance();
	private MainController mainController;
	private VisualizationJavaConnector javaConnector;
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
	private GridPane gridPaneOptionLogs;

	@FXML
	private DatePicker datePickerStart;

	@FXML
	private DatePicker datePickerEnd;

	public void init(MainController mainController) {
		this.mainController = mainController;

		if (mainController.getSelectionController()
				.getTabPane()
				.getTabs()
				.isEmpty()) {
			mainController.getWebViewTabPane()
					.getTabs()
					.remove(mainController.getVisualizationTab());

		}
		initLogOptionsFilter();
		initTabPaneWebView();
		initContextMenu();

	}

	private void initTabPaneWebView() {
		// Cargamos el html de los graficos y calificaciones
		webViewCharts.setContextMenuEnabled(false); // Desactiva el click derecho
		webViewChartsEngine = webViewCharts.getEngine();
		javaConnector = new VisualizationJavaConnector(webViewCharts, Controller.getInstance()
				.getMainConfiguration(), mainController, this);
		progressBar.progressProperty()
				.bind(webViewChartsEngine.getLoadWorker()
						.progressProperty());
		webViewChartsEngine.getLoadWorker()
				.exceptionProperty()
				.addListener((ov, oldState, newState) -> {
					System.out.println(newState);
				});
		// Comprobamos cuando se carga la pagina para traducirla
		webViewChartsEngine.getLoadWorker()
				.stateProperty()
				.addListener((ov, oldState, newState) -> {
					if (Worker.State.SUCCEEDED != newState)
						return;
					if (webViewChartsEngine.getDocument() == null) {
						webViewChartsEngine.reload();
						return;
					}
					progressBar.setVisible(false);
					JSObject window = (JSObject) webViewChartsEngine.executeScript("window");
					window.setMember("javaConnector", javaConnector);
					javaConnector.inititDefaultValues();
					webViewCharts.toFront();

					javaConnector.updateChart();
				});
		webViewChartsEngine.load(getClass().getResource("/graphics/Charts.html")
				.toExternalForm());

	}

	/**
	 * Inicializa los elementos de las opciones de logs.
	 */
	public void initLogOptionsFilter() {

		textFieldMax.textProperty()
				.addListener((ov, oldValue, newValue) -> {
					if (newValue == null || newValue.isEmpty() || newValue.matches("^[1-9]\\d{0,5}$")) {
						updateMaxScale();

					} else { // si no es un numero volvemos al valor anterior
						textFieldMax.setText(oldValue);
					}
				});
		LogStats logStats = controller.getActualCourse()
				.getLogStats();
		TypeTimes typeTime = controller.getMainConfiguration()
				.getValue(MainConfiguration.GENERAL, "initialTypeTimes");
		// añadimos los elementos de la enumeracion en el choicebox
		ObservableList<GroupByAbstract<?>> typeTimes = FXCollections.observableArrayList(logStats.getList());
		choiceBoxDate.setItems(typeTimes);
		choiceBoxDate.getSelectionModel()
				.select(logStats.getByType(typeTime));

		choiceBoxDate.valueProperty()
				.addListener((ov, oldValue, newValue) -> applyFilterLogs());

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

		datePickerStart.setValue(controller.getActualCourse()
				.getStart());
		datePickerEnd.setValue(controller.getActualCourse()
				.getEnd());

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

		optionsUbuLogs.managedProperty()
				.bind(optionsUbuLogs.visibleProperty());

	}

	public GridPane getGridPaneOptionLogs() {
		return gridPaneOptionLogs;
	}

	private void initContextMenu() {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setAutoHide(true);

		MenuItem exportCSV = new MenuItem(I18n.get("text.exportcsv"));
		MenuItem exportCSVDesglosed = new MenuItem(I18n.get("text.exportcsvdesglosed"));
		exportCSV.setOnAction(e -> exportCSV());
		exportCSVDesglosed.setOnAction(e -> exportCSVDesglosed());
		exportCSVDesglosed.visibleProperty()
				.bind(mainController.getSelectionController()
						.getTabUbuLogs()
						.selectedProperty());

		MenuItem exportPNG = new MenuItem(I18n.get("text.exportpng"));
		exportPNG.setOnAction(e -> save());

		contextMenu.getItems()
				.addAll(exportPNG, exportCSV, exportCSVDesglosed);
		webViewCharts.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				contextMenu.show(webViewCharts, e.getScreenX(), e.getScreenY());
			} else {
				contextMenu.hide();
			}
		});

	}

	@Override
	public void save() {

		UtilMethods.fileAction(String.format("%s_%s_%s", controller.getActualCourse()
				.getId(),
				LocalDateTime.now()
						.format(DateTimeFormatter.ofPattern(YYYY_M_MDDHHMMSS)),
				javaConnector.getCurrentChart()
						.getChartType()),
				ConfigHelper.getProperty("imageFolderPath", "./"), controller.getStage(), FileUtil.FileChooserType.SAVE,
				file -> {

					javaConnector.exportImage(file);

					ConfigHelper.setProperty("imageFolderPath", file.getParent());

				}, false, FileUtil.PNG);

	}

	public void exportCSV() {

		UtilMethods.fileAction(String.format("%s_%s_%s", controller.getActualCourse()
				.getId(),
				LocalDateTime.now()
						.format(DateTimeFormatter.ofPattern(YYYY_M_MDDHHMMSS)),
				javaConnector.getCurrentChart()
						.getChartType()),
				ConfigHelper.getProperty(CSV_FOLDER_PATH, "./"), controller.getStage(), FileUtil.FileChooserType.SAVE,
				file -> {

					javaConnector.getCurrentChart()
							.exportCSV(file.getAbsolutePath());

					ConfigHelper.setProperty(CSV_FOLDER_PATH, file.getParent());

				}, true, FileUtil.CSV);

	}

	public void exportCSVDesglosed() {
		UtilMethods.fileAction(String.format("%s_%s_%s", controller.getActualCourse()
				.getId(),
				LocalDateTime.now()
						.format(DateTimeFormatter.ofPattern(YYYY_M_MDDHHMMSS)),
				javaConnector.getCurrentChart()
						.getChartType()),
				ConfigHelper.getProperty(CSV_FOLDER_PATH, "./"), controller.getStage(), FileUtil.FileChooserType.SAVE,
				file -> {

					javaConnector.getCurrentChart()
							.exportCSVDesglosed(file.getAbsolutePath());
					ConfigHelper.setProperty(CSV_FOLDER_PATH, file.getParent());

				}, true, FileUtil.CSV);

	}

	/**
	 * Actualiza la escala maxima del eje y de los graficos de logs.
	 * 
	 * @param value valor de escala maxima
	 */
	private void updateMaxScale() {
		if (webViewChartsEngine.getLoadWorker()
				.getState() == Worker.State.SUCCEEDED && textFieldMax.isFocused())
			javaConnector.updateChart(false);

	}

	/**
	 * Aplica los filtros de fecha a las graficas de log.
	 * 
	 * @param event evento
	 */
	public void applyFilterLogs() {
		if (mainController.getVisualizationTab()
				.isSelected()) {
			findMaxaAndUpdateChart();
		}

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
		javaConnector.updateChart(false);
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
		javaConnector.setCurrentChart(javaConnector.getChartLogs());
		findMaxaAndUpdateChart();

	}

	@Override
	public void onSetTabGrades() {
		javaConnector.setCurrentChart(javaConnector.getChartGrades());
		javaConnector.updateChart();

	}

	@Override
	public void onSetTabActivityCompletion() {
		javaConnector.setCurrentChart(javaConnector.getChartActivityCompletion());
		javaConnector.updateChart();
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
	public void onWebViewTabChange() {
		javaConnector.updateOptionsImages();

	}

	@Override
	public void applyConfiguration() {
		javaConnector.updateChart();

	}

	public MainController getMainController() {
		return mainController;
	}

	public VisualizationJavaConnector getJavaConnector() {
		return javaConnector;
	}

	public void setJavaConnector(VisualizationJavaConnector javaConnector) {
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
