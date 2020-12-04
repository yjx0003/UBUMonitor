package es.ubu.lsi.ubumonitor.controllers.tabs;

import es.ubu.lsi.ubumonitor.controllers.DateController;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.WebViewAction;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.LogStats;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.view.chart.bridge.RiskJavaConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class RiskController extends WebViewAction {

	@FXML
	private GridPane optionsUbuLogs;
	@FXML
	private ChoiceBox<GroupByAbstract<?>> choiceBoxDate;
	
	@FXML
	private GridPane gridPaneOptionLogs;

	@FXML
	private DateController dateController;

	private RiskJavaConnector javaConnector;

	@Override
	public void init(MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration,
			Stage stage) {
		javaConnector = new RiskJavaConnector(webViewController.getWebViewCharts(), mainConfiguration, mainController,
				this, actualCourse);
		init(tab, actualCourse, mainConfiguration, stage, javaConnector);

		VisualizationController visualizationController = mainController.getWebViewTabsController()
				.getVisualizationController();
		initOptions(visualizationController.getChoiceBoxDate());
		visualizationController.bindDatePicker(this, getDatePickerStart(), getDatePickerEnd());
	}

	private void initOptions(ChoiceBox<GroupByAbstract<?>> choiceBoxBind) {

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
		return dateController.getDateGridPane();
	}

	public GridPane getGridPaneOptionLogs() {
		return gridPaneOptionLogs;
	}

	public DatePicker getDatePickerStart() {
		return dateController.getDatePickerStart();
	}

	public DatePicker getDatePickerEnd() {
		return dateController.getDatePickerEnd();
	}

}
