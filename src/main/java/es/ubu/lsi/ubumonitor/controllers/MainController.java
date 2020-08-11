package es.ubu.lsi.ubumonitor.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.controller.GeneralController;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigurationController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Stats;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Clase controlador de la ventana principal
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class MainController implements Initializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

	private Controller controller = Controller.getInstance();

	@FXML
	private StatusBar statusBar;

	@FXML
	private SplitPane splitPane;

	@FXML
	private SplitPane splitPaneLeft;

	private Stats stats;

	@FXML
	private TabPane webViewTabPane;

	@FXML
	private Tab visualizationTab;

	@FXML
	private VisualizationController visualizationController;

	@FXML
	private Tab riskTab;
	@FXML
	private RiskController riskController;

	private Map<Tab, MainAction> tabMap = new HashMap<>();

	@FXML
	private MenuController menuController;

	@FXML
	private SelectionMainController selectionMainController;

	@FXML
	private SelectionUserController selectionUserController;
	
	@FXML
	private Tab forumTab;
	@FXML
	private ForumController forumController;
	
	@FXML
	private Tab clusteringTab;
	
	@FXML
	private GeneralController clusteringController;

	/**
	 * Muestra los usuarios matriculados en el curso, así como las actividades de
	 * las que se compone.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		CourseStatsController.logStatistics(controller.getActualCourse());
		try {
			LOGGER.info("Completada la carga del curso {}", controller.getActualCourse()
					.getFullName());

			controller.getStage()
					.setOnCloseRequest(event -> onClose());

			stats = controller.getStats();

			controller.setMainConfiguration(new MainConfiguration());
			ConfigurationController.loadConfiguration(controller.getMainConfiguration(),
					controller.getConfiguration(controller.getActualCourse()));

			menuController.init(this);
			selectionUserController.init(this);
			selectionMainController.init(this);
			clusteringController.init(this);
			
			
			initWebViewTabs();
			initStatusBar();
			
		

		} catch (Exception e) {
			LOGGER.error("Error en la inicialización.", e);
		}
	}

	private void initWebViewTabs() {

		webViewTabPane.getSelectionModel()
				.select(ConfigHelper.getProperty("webViewTab", webViewTabPane.getSelectionModel()
						.getSelectedIndex()));
		webViewTabPane.getSelectionModel()
				.selectedItemProperty()
				.addListener((ob, old, newValue) -> {
					onWebViewTabChange();
					SelectionController selectionController = selectionMainController.getSelectionController();
					if (selectionController.getTabUbuLogs()
							.isSelected()) {
						onSetTabLogs();
					} else if (selectionController.getTabUbuGrades()
							.isSelected()) {
						onSetTabGrades();
					} else if (selectionController.getTabActivity()
							.isSelected()) {
						onSetTabActivityCompletion();
					}
				});

		visualizationController.init(this);
		tabMap.put(visualizationTab, visualizationController);

		riskController.init(this);
		
		tabMap.put(riskTab, riskController);
		
		forumController.init(this);
		tabMap.put(forumTab, forumController);
	}

	private void initStatusBar() {

		// Mostramos Host actual
		Hyperlink actualHost = new Hyperlink(controller.getUrlHost()
				.toString());
		ImageView linkImage = new ImageView("/img/link.png");
		linkImage.setFitHeight(20);
		linkImage.setFitWidth(20);
		actualHost.setGraphic(linkImage);
		actualHost.setOnAction(event -> UtilMethods.openURL(actualHost.getText()));
		// Mostramos Curso actual
		Label lblActualCourse = new Label(controller.getActualCourse()
				.getFullName());

		ImageView online = new ImageView(
				controller.isOfflineMode() ? "/img/circle_offline.png" : "/img/circle_online.png");
		online.setFitHeight(20);
		online.setFitWidth(20);
		Tooltip.install(online, new Tooltip(I18n.get("text.online_" + !controller.isOfflineMode())));
		HBox left = new HBox();
		left.setAlignment(Pos.CENTER);
		left.setSpacing(5);
		left.getChildren()
				.addAll(online, new Separator(Orientation.VERTICAL), lblActualCourse,
						new Separator(Orientation.VERTICAL), actualHost);

		statusBar.getLeftItems()
				.add(left);

		HBox right = new HBox();
		right.setAlignment(Pos.CENTER);
		right.setSpacing(5);
		ZonedDateTime lastLogDateTime = controller.getUpdatedCourseData();
		Label lblLastUpdate = new Label(
				I18n.get("label.lastupdate") + " " + lastLogDateTime.format(Controller.DATE_TIME_FORMATTER));
		right.getChildren()
				.addAll(lblLastUpdate);
		statusBar.getRightItems()
				.add(right);

	}

	/**
	 * Exporta el gráfico. Se exportara como imagen en formato png.
	 * 
	 * @param actionEvent El ActionEvent.
	 * @throws IOException excepción
	 */
	public void saveChart(ActionEvent actionEvent) throws IOException {

		getActions().save();
	}

	public SplitPane getSplitPaneLeft() {
		return splitPaneLeft;
	}

	public Stats getStats() {
		return stats;
	}

	public RiskController getRiskController() {
		return riskController;
	}

	public TabPane getWebViewTabPane() {
		return webViewTabPane;
	}

	public Tab getVisualizationTab() {
		return visualizationTab;
	}

	public Map<Tab, MainAction> getTabMap() {
		return tabMap;
	}

	private MainAction getActions() {
		return tabMap.getOrDefault(webViewTabPane.getSelectionModel()
				.getSelectedItem(), NullMainAction.getInstance());
	}

	public void updateTreeViewGradeItem() {
		getActions().updateTreeViewGradeItem();
	}

	public void updateListViewEnrolledUser() {
		getActions().updateListViewEnrolledUser();
	}

	public void updatePredicadeEnrolledList() {
		getActions().updatePredicadeEnrolledList();
	}

	public void updateListViewActivity() {
		getActions().updateListViewActivity();

	}

	public void onSetTabLogs() {
		getActions().onSetTabLogs();

	}

	public void onSetTabGrades() {
		getActions().onSetTabGrades();
	}

	public void onSetTabActivityCompletion() {
		getActions().onSetTabActivityCompletion();
	}

	public void onSetSubTabLogs() {
		getActions().onSetSubTabLogs();
	}

	public void updateListViewComponents() {
		getActions().updateListViewComponents();
	}

	public void updateListViewEvents() {
		getActions().updateListViewEvents();
	}

	public void updateListViewSection() {
		getActions().updateListViewSection();
	}

	public void updateListViewCourseModule() {
		getActions().updateListViewCourseModule();
	}

	public void onWebViewTabChange() {
		getActions().onWebViewTabChange();
	}

	public void applyConfiguration() {
		getActions().applyConfiguration();

	}

	private void onClose() {
		try {
			ConfigHelper.setProperty("webViewTab", webViewTabPane.getSelectionModel()
					.getSelectedIndex());
			ConfigHelper.setProperty("tabPane", selectionMainController.getSelectionController()
					.getTabPane()
					.getSelectionModel()
					.getSelectedIndex());
			ConfigHelper.setProperty("dividerPosition", splitPane.getDividerPositions()[0]);
		} catch (Exception e) {
			LOGGER.warn("Cannot save the properties onClose", e);
		}

	}

	public StatusBar getStatusBar() {
		return statusBar;
	}

	public SplitPane getSplitPane() {
		return splitPane;
	}

	public VisualizationController getVisualizationController() {
		return visualizationController;
	}

	public MenuController getMenuController() {
		return menuController;
	}

	public SelectionController getSelectionController() {
		return selectionMainController.getSelectionController();
	}

	public SelectionUserController getSelectionUserController() {
		return selectionUserController;
	}

	public Tab getRiskTab() {
		return riskTab;
	}

	public ForumController getForumController() {
		return forumController;
	}

	public Tab getForumTab() {
		return forumTab;
	}

	public SelectionMainController getSelectionMainController() {
		return selectionMainController;
	}

	public Tab getClusteringTab() {
		return clusteringTab;
	}

	public GeneralController getClusteringController() {
		return clusteringController;
	}

}