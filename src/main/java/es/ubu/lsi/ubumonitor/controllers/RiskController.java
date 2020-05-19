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
import es.ubu.lsi.ubumonitor.view.chart.RiskJavaConnector;
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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import netscape.javascript.JSObject;

public class RiskController implements MainAction {

	@FXML
	private ProgressBar progressBar;
	@FXML
	private WebView webViewCharts;

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

	private WebEngine webViewChartsEngine;
	private MainController mainController;
	private Controller controller = Controller.getInstance();

	private RiskJavaConnector javaConnector;

	public void init(MainController mainController) {
		this.mainController = mainController;
		initOptions();
		initTabPaneWebView();
		initContextMenu();

	}

	private void initTabPaneWebView() {

		// Cargamos el html de los graficos y calificaciones
		webViewCharts.setContextMenuEnabled(false); // Desactiva el click derecho
		webViewChartsEngine = webViewCharts.getEngine();
		javaConnector = new RiskJavaConnector(this);
		progressBar.progressProperty()
				.bind(webViewChartsEngine.getLoadWorker()
						.progressProperty());

		// Comprobamos cuando se carga la pagina para traducirla
		webViewChartsEngine.getLoadWorker()
				.stateProperty()
				.addListener((ov, oldState, newState) -> {
					if (Worker.State.SUCCEEDED != newState)
						return;

					progressBar.setVisible(false);
					JSObject window = (JSObject) webViewChartsEngine.executeScript("window");
					window.setMember("javaConnector", javaConnector);
					webViewCharts.toFront();
					javaConnector.setDefaultValues();

					updateChart();

				});
		webViewChartsEngine.load(getClass().getResource("/graphics/RiskCharts.html")
				.toExternalForm());

	}

	private void initOptions() {
		
		

		datePickerStart.setValue(controller.getActualCourse()
				.getStart());
		datePickerEnd.setValue(controller.getActualCourse()
				.getEnd());

		datePickerStart.setOnAction(event -> updateChart());
		datePickerEnd.setOnAction(event -> updateChart());

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
		
		LogStats logStats = controller.getActualCourse()
				.getLogStats();
		TypeTimes typeTime = controller.getMainConfiguration()
				.getValue(MainConfiguration.GENERAL, "initialTypeTimes");
		// añadimos los elementos de la enumeracion en el choicebox
		ObservableList<GroupByAbstract<?>> typeTimes = FXCollections.observableArrayList(
				logStats.getByType(TypeTimes.DAY), logStats.getByType(TypeTimes.YEAR_WEEK),
				logStats.getByType(TypeTimes.YEAR_MONTH));
		choiceBoxDate.setItems(typeTimes);
		choiceBoxDate.getSelectionModel()
				.select(logStats.getByType(typeTime));

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


		optionsUbuLogs.managedProperty()
				.bind(optionsUbuLogs.visibleProperty());
		datePickerStart.valueProperty()
				.bindBidirectional(mainController.getVisualizationController()
						.getDatePickerStart()
						.valueProperty());
		datePickerEnd.valueProperty()
				.bindBidirectional(mainController.getVisualizationController()
						.getDatePickerEnd()
						.valueProperty());

		choiceBoxDate.valueProperty()
				.bindBidirectional(mainController.getVisualizationController()
						.getChoiceBoxDate()
						.valueProperty());
		choiceBoxDate.valueProperty()
				.addListener((ov, oldValue, newValue) -> updateChart());

	}

	private void initContextMenu() {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setAutoHide(true);

		MenuItem exportCSV = new MenuItem(I18n.get("text.exportcsv"));
		exportCSV.setOnAction(e -> exportCSV());

		MenuItem exportPNG = new MenuItem(I18n.get("text.exportpng"));
		exportPNG.setOnAction(e -> save());

		contextMenu.getItems()
				.addAll(exportPNG, exportCSV);
		webViewCharts.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				contextMenu.show(webViewCharts, e.getScreenX(), e.getScreenY());
			} else {
				contextMenu.hide();
			}
		});

	}

	public void updateChart() {
		if (mainController.getRiskTab()
				.isSelected()) {
			javaConnector.updateChart();
		}

	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public WebView getWebViewCharts() {
		return webViewCharts;
	}

	public WebEngine getWebViewChartsEngine() {
		return webViewChartsEngine;
	}

	public RiskJavaConnector getJavaConnector() {
		return javaConnector;
	}

	@Override
	public void onWebViewTabChange() {

		javaConnector.updateTabImages();
		javaConnector.updateChart();

	}

	@Override
	public void updateTreeViewGradeItem() {
		// do nothing

	}

	@Override
	public void updateListViewEnrolledUser() {
		updateChart();

	}

	@Override
	public void updatePredicadeEnrolledList() {
		updateChart();
	}

	@Override
	public void updateListViewActivity() {
		// do nothing

	}

	@Override
	public void onSetTabLogs() {
		// do nothing

	}

	@Override
	public void onSetTabGrades() {
		// do nothing

	}

	@Override
	public void onSetTabActivityCompletion() {
		// do nothing

	}

	@Override
	public void onSetSubTabLogs() {
		// do nothing

	}

	@Override
	public void updateListViewComponents() {
		// do nothing

	}

	@Override
	public void updateListViewEvents() {
		// do nothing

	}

	@Override
	public void updateListViewSection() {
		// do nothing

	}

	@Override
	public void updateListViewCourseModule() {
		// do nothing

	}

	@Override
	public void save() {

		UtilMethods.fileAction(String.format("%s_%s_%s", controller.getActualCourse()
				.getId(),
				LocalDateTime.now()
						.format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss")),
				javaConnector.getCurrentType()
						.getChartType()),
				ConfigHelper.getProperty("imageFolderPath", "./"), controller.getStage(), FileUtil.FileChooserType.SAVE,
				file -> {
					if (file != null) {
						String str = javaConnector.export(file);
						if (str == null)
							return;
						javaConnector.saveImage(str);
						ConfigHelper.setProperty("imageFolderPath", file.getParent());

					}
				}, false, FileUtil.PNG);

	}

	public void exportCSV() {
		UtilMethods.fileAction(String.format("%s_%s_%s", controller.getActualCourse()
				.getId(),
				LocalDateTime.now()
						.format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss")),
				javaConnector.getCurrentType()
						.getChartType()),
				ConfigHelper.getProperty("csvFolderPath", "./"), controller.getStage(), FileUtil.FileChooserType.SAVE,
				file -> {
					javaConnector.getCurrentType()
							.exportCSV(file.getAbsolutePath());
					ConfigHelper.setProperty("csvFolderPath", file.getParent());
				}, FileUtil.CSV);

	}

	@Override
	public void applyConfiguration() {
		updateChart();

	}

	public MainController getMainController() {
		return mainController;
	}

	public GridPane getOptionsUbuLogs() {
		return optionsUbuLogs;
	}

	public ChoiceBox<GroupByAbstract<?>> getChoiceBoxDate() {
		return choiceBoxDate;
	}

	public GridPane getDateGridPane() {
		return dateGridPane;
	}

	public GridPane getGridPaneOptionLogs() {
		return gridPaneOptionLogs;
	}

	public DatePicker getDatePickerStart() {
		return datePickerStart;
	}

	public DatePicker getDatePickerEnd() {
		return datePickerEnd;
	}

	public Controller getController() {
		return controller;
	}


}
