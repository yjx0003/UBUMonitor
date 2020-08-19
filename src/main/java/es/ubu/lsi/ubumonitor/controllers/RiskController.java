package es.ubu.lsi.ubumonitor.controllers;

import java.time.LocalDate;

import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.LogStats;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.view.chart.Chart;
import es.ubu.lsi.ubumonitor.view.chart.RiskJavaConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class RiskController extends WebViewController {

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

	private RiskJavaConnector javaConnector;
	
	@Override
	public void init(MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration,
			Stage stage) {
		javaConnector = new RiskJavaConnector(webViewCharts, mainConfiguration, mainController, this, actualCourse);
		init(tab, actualCourse, mainConfiguration, stage, javaConnector);

		VisualizationController visualizationController = mainController.getVisualizationController();
		initOptions(visualizationController.getDatePickerStart(), visualizationController.getDatePickerEnd(),
				visualizationController.getChoiceBoxDate());

	}

	private void initOptions(DatePicker datePickerStartBind, DatePicker datePickerEndBind,
			ChoiceBox<GroupByAbstract<?>> choiceBoxBind) {

		datePickerStart.setValue(actualCourse.getStart());
		datePickerEnd.setValue(actualCourse.getEnd());

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

		LogStats logStats = actualCourse.getLogStats();
		TypeTimes typeTime = mainConfiguration.getValue(MainConfiguration.GENERAL, "initialTypeTimes");
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
				.bindBidirectional(datePickerStartBind.valueProperty());
		datePickerEnd.valueProperty()
				.bindBidirectional(datePickerEndBind.valueProperty());

		choiceBoxDate.valueProperty()
				.bindBidirectional(choiceBoxBind.valueProperty());
		choiceBoxDate.valueProperty()
				.addListener((ov, oldValue, newValue) -> updateChart());

	}

	public RiskJavaConnector getJavaConnector() {
		return javaConnector;
	}

	@Override
	public void onWebViewTabChange() {

		javaConnector.updateOptionsImages();
		javaConnector.updateChart();

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
	public void applyConfiguration() {
		updateChart();

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

	@Override
	public Chart getCurrentChart() {
		return javaConnector.getCurrentChart();
	}

}
