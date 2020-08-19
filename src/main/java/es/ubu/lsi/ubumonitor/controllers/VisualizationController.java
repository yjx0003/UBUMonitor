package es.ubu.lsi.ubumonitor.controllers;

import java.time.LocalDate;

import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.LogStats;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.view.chart.Chart;
import es.ubu.lsi.ubumonitor.view.chart.JavaConnector;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class VisualizationController extends WebViewController {

	@FXML
	private TextField textFieldMax;

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

	private VisualizationJavaConnector javaConnector;

	private SelectionController selectionController;

	
	@Override
	public void init(MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration,
			Stage stage) {
		this.selectionController = mainController.getSelectionController();
		javaConnector = new VisualizationJavaConnector(webViewCharts, mainConfiguration, mainController, this, actualCourse);
		init(tab, actualCourse, mainConfiguration, stage, javaConnector);
		
		if (mainController.getSelectionController()
				.getTabPane()
				.getTabs()
				.isEmpty()) {
			mainController.getWebViewTabPane()
					.getTabs()
					.remove(mainController.getVisualizationTab());

		}
		initLogOptionsFilter();
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
		LogStats logStats = actualCourse.getLogStats();
		TypeTimes typeTime = mainConfiguration.getValue(MainConfiguration.GENERAL, "initialTypeTimes");
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

		datePickerStart.setValue(actualCourse.getStart());
		datePickerEnd.setValue(actualCourse.getEnd());

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

	@Override
	public ContextMenu initContextMenu() {
		ContextMenu contextMenu = super.initContextMenu();

		MenuItem exportCSVDesglosed = new MenuItem(I18n.get("text.exportcsvdesglosed"));
		exportCSVDesglosed.setOnAction(e -> exportCSVDesglosed());
		exportCSVDesglosed.visibleProperty()
				.bind(selectionController.getTabUbuLogs()
						.selectedProperty());

		contextMenu.getItems()
				.add(exportCSVDesglosed);
		return contextMenu;

	}

	/**
	 * Actualiza la escala maxima del eje y de los graficos de logs.
	 * 
	 * @param value valor de escala maxima
	 */
	private void updateMaxScale() {
		if (webEngine.getLoadWorker()
				.getState() == Worker.State.SUCCEEDED && textFieldMax.isFocused())
			javaConnector.updateChart(false);

	}

	/**
	 * Aplica los filtros de fecha a las graficas de log.
	 * 
	 * @param event evento
	 */
	public void applyFilterLogs() {
		if (tab.isSelected()) {
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

	@Override
	public JavaConnector getJavaConnector() {
		return javaConnector;
	}

	public TextField getTextFieldMax() {
		return textFieldMax;
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

	public DatePicker getDatePickerStart() {
		return datePickerStart;
	}

	public DatePicker getDatePickerEnd() {
		return datePickerEnd;
	}

	@Override
	public Chart getCurrentChart() {
		return javaConnector.getCurrentChart();
	}

	

}
