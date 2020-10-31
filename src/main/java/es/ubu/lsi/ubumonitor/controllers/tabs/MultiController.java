package es.ubu.lsi.ubumonitor.controllers.tabs;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.WebViewAction;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.LogStats;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.view.chart.bridge.JavaConnector;
import es.ubu.lsi.ubumonitor.view.chart.bridge.MultiConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class MultiController extends WebViewAction {

	private MultiConnector javaConnector;
	

	@FXML
	private GridPane gridPaneOptionLogs;
	@FXML
	private ChoiceBox<GroupByAbstract<?>> choiceBoxDate;
	
	
	@FXML
	private GridPane dateGridPane;
	@FXML
	private DatePicker datePickerStart;
	@FXML
	private DatePicker datePickerEnd;

	@Override
	public void init(MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration,
			Stage stage) {
		
		javaConnector = new MultiConnector(webViewController.getWebViewCharts(), mainConfiguration, mainController,
				actualCourse, this);
		init(tab, actualCourse, mainConfiguration, stage, javaConnector);
		mainController.getWebViewTabsController()
				.getVisualizationController()
				.bindDatePicker(this, datePickerStart, datePickerEnd);
		initOptions(mainController.getWebViewTabsController().getVisualizationController().getChoiceBoxDate());
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

		choiceBoxDate.valueProperty()
				.bindBidirectional(choiceBoxBind.valueProperty());
		choiceBoxDate.valueProperty()
				.addListener((ov, oldValue, newValue) -> updateChart());

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
	
	@Override
	public void updateTreeViewGradeItem() {
		updateChart();
	}
	
	@Override
	public void updateListViewActivity() {
		updateChart();

	}
	
	@Override
	public void onSetTabLogs() {
		updateChart();
		

	}

	

	@Override
	public void onSetTabGrades() {
		updateChart();

	}

	@Override
	public void onSetTabActivityCompletion() {
		updateChart();
	}

	@Override
	public void onSetSubTabLogs() {
		updateChart();

	}

	@Override
	public void updateListViewComponents() {
		updateChart();

	}

	@Override
	public void updateListViewEvents() {
		updateChart();

	}

	@Override
	public void updateListViewSection() {
		updateChart();

	}

	@Override
	public void updateListViewCourseModule() {
		updateChart();
	}

	@Override
	public JavaConnector getJavaConnector() {
		return javaConnector;
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

	public GridPane getGridPaneOptionLogs() {
		return gridPaneOptionLogs;
	}

	public ChoiceBox<GroupByAbstract<?>> getChoiceBoxDate() {
		return choiceBoxDate;
	}

}
