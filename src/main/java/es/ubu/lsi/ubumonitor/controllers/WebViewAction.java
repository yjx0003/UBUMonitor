package es.ubu.lsi.ubumonitor.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.util.FileUtil;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.Chart;
import es.ubu.lsi.ubumonitor.view.chart.JavaConnector;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public abstract class WebViewAction implements MainAction {

	private static final String CSV_FOLDER_PATH = "csvFolderPath";

	private static final DateTimeFormatter FILE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

	protected WebEngine webEngine;

	protected Tab tab;

	protected Stage stage;

	protected Course actualCourse;

	protected MainConfiguration mainConfiguration;
	@FXML
	protected WebViewController webViewController;

	protected void init(Tab tab, Course actualCourse, MainConfiguration mainConfiguration, Stage stage,
			JavaConnector javaConnector) {
		webEngine = webViewController.getWebViewCharts()
				.getEngine();
		this.tab = tab;
		this.actualCourse = actualCourse;
		this.mainConfiguration = mainConfiguration;
		this.stage = stage;
		initTabPaneWebView(javaConnector);
		initContextMenu();
	}

	public abstract Chart getCurrentChart();

	public abstract JavaConnector getJavaConnector();

	public void exportCSV() {
		UtilMethods.fileAction(String.format("%s_%s_%s", actualCourse.getId(), LocalDateTime.now()
				.format(FILE_FORMATTER), getCurrentChart().getChartType()),
				ConfigHelper.getProperty(CSV_FOLDER_PATH, "./"), stage, FileUtil.FileChooserType.SAVE, file -> {
					getCurrentChart().exportCSV(file.getAbsolutePath());
					ConfigHelper.setProperty(CSV_FOLDER_PATH, file.getParent());
				}, FileUtil.CSV);

	}

	public void exportCSVDesglosed() {
		UtilMethods.fileAction(String.format("%s_%s_%s", actualCourse.getId(), LocalDateTime.now()
				.format(FILE_FORMATTER), getCurrentChart().getChartType()),
				ConfigHelper.getProperty(CSV_FOLDER_PATH, "./"), stage, FileUtil.FileChooserType.SAVE, file -> {

					getCurrentChart().exportCSVDesglosed(file.getAbsolutePath());
					ConfigHelper.setProperty(CSV_FOLDER_PATH, file.getParent());

				}, true, FileUtil.CSV);

	}

	@Override
	public void saveImage() {
		UtilMethods.fileAction(String.format("%s_%s_%s", actualCourse.getId(), LocalDateTime.now()
				.format(FILE_FORMATTER), getCurrentChart().getChartType()),
				ConfigHelper.getProperty("imageFolderPath", "./"), stage, FileUtil.FileChooserType.SAVE, file -> {

					getCurrentChart().exportImage(file);
					ConfigHelper.setProperty("imageFolderPath", file.getParent());

				}, false, FileUtil.PNG);

	}

	public void initTabPaneWebView(JavaConnector javaConnector) {
		WebView webViewCharts = webViewController.getWebViewCharts();
		// Cargamos el html de los graficos y calificaciones
		webViewCharts.setContextMenuEnabled(false); // Desactiva el click derecho
		webEngine = webViewCharts.getEngine();

		webViewController.getProgressBar()
				.progressProperty()
				.bind(webEngine.getLoadWorker()
						.progressProperty());

		// Comprobamos cuando se carga la pagina para traducirla
		webEngine.getLoadWorker()
				.stateProperty()
				.addListener((ov, oldState, newState) -> {
					if (Worker.State.SUCCEEDED != newState)
						return;
					if (webEngine.getDocument() == null) {
						webEngine.reload();
						return;
					}
					webViewController.getProgressBar()
							.setVisible(false);
					JSObject window = (JSObject) webEngine.executeScript("window");
					window.setMember("javaConnector", javaConnector);
					webViewCharts.toFront();
					javaConnector.inititDefaultValues();

					javaConnector.updateChart();

				});
		webEngine.load(getClass().getResource("/graphics/Charts.html")
				.toExternalForm());

	}

	public ContextMenu initContextMenu() {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setAutoHide(true);

		MenuItem exportCSV = new MenuItem(I18n.get("text.exportcsv"));
		exportCSV.setOnAction(e -> exportCSV());

		MenuItem exportPNG = new MenuItem(I18n.get("text.exportpng"));
		exportPNG.setOnAction(e -> saveImage());

		contextMenu.getItems()
				.addAll(exportPNG, exportCSV);
		webViewController.getWebViewCharts()
				.setOnMouseClicked(e -> {
					if (e.getButton() == MouseButton.SECONDARY) {
						contextMenu.show(webViewController.getWebViewCharts(), e.getScreenX(), e.getScreenY());
					} else {
						contextMenu.hide();
					}
				});

		return contextMenu;

	}

	public void updateChart() {
		if (tab.isSelected()) {
			getJavaConnector().updateChart();
		}
	}

	public abstract void init(MainController mainController, Tab tab, Course actualCourse,
			MainConfiguration mainConfiguration, Stage stage);

}
