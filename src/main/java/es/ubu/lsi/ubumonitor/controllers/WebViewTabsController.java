package es.ubu.lsi.ubumonitor.controllers;

import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.tabs.ForumController;
import es.ubu.lsi.ubumonitor.controllers.tabs.ClusteringController;
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

	private MainController mainController;
	
	public void init(MainController mainController, Course actualCourse, MainConfiguration mainConfiguration, Stage stage) {
		this.mainController = mainController;
		
		clusteringController.init(mainController);
		initWebViewTabs(actualCourse, mainConfiguration, stage);
	}
	
	
	private void initWebViewTabs(Course actualCourse, MainConfiguration mainConfiguration, Stage stage) {

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

		visualizationController.init(mainController,visualizationTab, actualCourse, mainConfiguration, stage);
		mainController.getTabMap().put(visualizationTab, visualizationController);

		riskController.init(mainController, riskTab, actualCourse, mainConfiguration, stage);
		
		mainController.getTabMap().put(riskTab, riskController);
		
		forumController.init(mainController, forumTab, actualCourse, mainConfiguration, stage);
		mainController.getTabMap().put(forumTab, forumController);
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

}
