package es.ubu.lsi.ubumonitor.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.util.FileUtil;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ForumConnector;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class ForumController implements MainAction{
	@FXML
	private ProgressBar progressBar;
	@FXML
	private WebView webViewCharts;

	private WebEngine webViewChartsEngine;

	private Controller controller = Controller.getInstance();
	private ForumConnector javaConnector;
	private Tab tab;
	private MainController mainController;
	
	
	public void init(MainController mainController) {
		this.mainController = mainController;
		this.tab = mainController.getForumTab();
		initTabPaneWebView();
		initContextMenu();

	}

	
	private void initTabPaneWebView() {

		// Cargamos el html de los graficos y calificaciones
		webViewCharts.setContextMenuEnabled(false); // Desactiva el click derecho
		webViewChartsEngine = webViewCharts.getEngine();
		javaConnector = new ForumConnector(webViewCharts, Controller.getInstance().getMainConfiguration(), mainController);
		progressBar.progressProperty()
				.bind(webViewChartsEngine.getLoadWorker()
						.progressProperty());

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
					webViewCharts.toFront();
					javaConnector.inititDefaultValues();

					updateChart();

				});
		webViewChartsEngine.load(getClass().getResource("/graphics/Charts.html")
				.toExternalForm());

	}
	
	
	private void updateChart() {
		if(tab.isSelected()) {
			javaConnector.updateChart();
		}
		
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
	public void save() {

		UtilMethods.fileAction(String.format("%s_%s_%s", controller.getActualCourse()
				.getId(),
				LocalDateTime.now()
						.format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss")),
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
						.format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss")),
				javaConnector.getCurrentChart()
						.getChartType()),
				ConfigHelper.getProperty("csvFolderPath", "./"), controller.getStage(), FileUtil.FileChooserType.SAVE,
				file -> {
					javaConnector.getCurrentChart()
							.exportCSV(file.getAbsolutePath());
					ConfigHelper.setProperty("csvFolderPath", file.getParent());
				}, FileUtil.CSV);

	}

	@Override
	public void applyConfiguration() {
		updateChart();

	}
	
	public WebEngine getWebChartsEngine() {
		return webViewChartsEngine;
	}


	public WebView getWebViewCharts() {
		return webViewCharts;
	}


	public MainController getMainController() {
		return mainController;
	}
}
