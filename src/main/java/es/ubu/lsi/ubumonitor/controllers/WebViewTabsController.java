package es.ubu.lsi.ubumonitor.controllers;

import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.tabs.CalendarEventController;
import es.ubu.lsi.ubumonitor.controllers.tabs.ClusteringController;
import es.ubu.lsi.ubumonitor.controllers.tabs.EnrollmentController;
import es.ubu.lsi.ubumonitor.controllers.tabs.ForumController;
import es.ubu.lsi.ubumonitor.controllers.tabs.MultiController;
import es.ubu.lsi.ubumonitor.controllers.tabs.RiskController;
import es.ubu.lsi.ubumonitor.controllers.tabs.VisualizationController;
import es.ubu.lsi.ubumonitor.model.Course;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class WebViewTabsController {
	@FXML
	private TabPane tabPane;

	@FXML
	private Tab visualizationTab;

	@FXML
	private VisualizationController visualizationController;

	@FXML
	private Tab riskTab;
	@FXML
	private RiskController riskController;

	@FXML
	private Tab forumTab;
	@FXML
	private ForumController forumController;

	@FXML
	private Tab clusteringTab;

	@FXML
	private ClusteringController clusteringController;
	
	@FXML
	private Tab calendarEventTab;
	
	@FXML
	private CalendarEventController calendarEventController;
	
	@FXML
	private Tab multiTab;
	
	@FXML
	private MultiController multiController;
	
	
	@FXML
	private Tab enrollmentTab;
	
	@FXML
	private EnrollmentController enrollmentController;

	private MainController mainController;
	
	public void init(MainController mainController, Course actualCourse, MainConfiguration mainConfiguration, Stage stage) {
		this.mainController = mainController;
		
		
		initWebViewTabs(actualCourse, mainConfiguration, stage);
	}
	
	
	private void initWebViewTabs(Course actualCourse, MainConfiguration mainConfiguration, Stage stage) {
		clusteringController.init(mainController);
		tabPane.getSelectionModel()
				.select(ConfigHelper.getProperty("webViewTab", tabPane.getSelectionModel()
						.getSelectedIndex()));
		tabPane.getSelectionModel()
				.selectedItemProperty()
				.addListener((ob, old, newValue) -> {
					mainController.getActions().onWebViewTabChange();
					SelectionController selectionController = mainController.getSelectionController();
					if (selectionController.getTabUbuLogs()
							.isSelected()) {
						mainController.getActions().onSetTabLogs();
					} else if (selectionController.getTabUbuGrades()
							.isSelected()) {
						mainController.getActions().onSetTabGrades();
					} else if (selectionController.getTabActivity()
							.isSelected()) {
						mainController.getActions().onSetTabActivityCompletion();
					}
				});


		add(visualizationController, mainController, visualizationTab, actualCourse, mainConfiguration, stage);
		add(riskController, mainController, riskTab, actualCourse, mainConfiguration, stage);
		add(forumController, mainController, forumTab, actualCourse, mainConfiguration, stage);
		add(calendarEventController, mainController, calendarEventTab, actualCourse, mainConfiguration, stage);
		add(multiController, mainController, multiTab, actualCourse, mainConfiguration, stage);
		add(enrollmentController, mainController, enrollmentTab, actualCourse, mainConfiguration, stage);
	}

	private void add(WebViewAction webViewAction, MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration, Stage stage) {
		webViewAction.init(mainController, tab, actualCourse, mainConfiguration, stage);
		mainController.getTabMap().put(tab, webViewAction);
	}

	public TabPane getTabPane() {
		return tabPane;
	}


	public Tab getVisualizationTab() {
		return visualizationTab;
	}


	public VisualizationController getVisualizationController() {
		return visualizationController;
	}


	public Tab getRiskTab() {
		return riskTab;
	}


	public RiskController getRiskController() {
		return riskController;
	}


	public Tab getForumTab() {
		return forumTab;
	}


	public ForumController getForumController() {
		return forumController;
	}


	public Tab getClusteringTab() {
		return clusteringTab;
	}


	public ClusteringController getClusteringController() {
		return clusteringController;
	}


	public MainController getMainController() {
		return mainController;
	}


	public Tab getCalendarEventTab() {
		return calendarEventTab;
	}


	public CalendarEventController getCalendarEventController() {
		return calendarEventController;
	}
	
	public Tab getMultiTab() {
		return multiTab;
	}

	public MultiController getMultiController() {
		return multiController;
	}


	/**
	 * @return the enrollementTab
	 */
	public Tab getEnrollmentTab() {
		return enrollmentTab;
	}


	/**
	 * @return the enrollmentController
	 */
	public EnrollmentController getEnrollmentController() {
		return enrollmentController;
	}
}
