package es.ubu.lsi.ubumonitor.clustering.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.util.CSVClustering;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.concurrent.Worker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ClusteringGraph {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringGraph.class);
	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

	private WebView webView;
	private WebEngine webEngine;
	private Connector connector;
	private List<ClusterWrapper> clusters;
	private Controller controller;

	public ClusteringGraph(ClusteringController clusteringController) {
		webView = clusteringController.getWebView();
		webEngine = webView.getEngine();
		controller = Controller.getInstance();
		connector = new Connector(clusteringController, webEngine);
		init();
	}

	private void init() {
		webView.setContextMenuEnabled(false);
		webEngine = webView.getEngine();

		webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
			if (Worker.State.SUCCEEDED != newState)
				return;
			netscape.javascript.JSObject window = (netscape.javascript.JSObject) webEngine.executeScript("window");
			window.setMember("javaConnector", connector);
		});
		webEngine.load(getClass().getResource("/graphics/ClusterChart.html").toExternalForm());
		initContextMenu();
	}

	private void initContextMenu() {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setAutoHide(true);

		MenuItem exportCSV = new MenuItem(I18n.get("text.exportcsv"));
		exportCSV.setOnAction(e -> exportPoints());
		MenuItem exportPNG = new MenuItem(I18n.get("text.exportpng"));
		exportPNG.setOnAction(e -> exportPNG());
		contextMenu.getItems().setAll(exportCSV, exportPNG);
		webView.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY && clusters != null) {
				contextMenu.show(webView, e.getScreenX(), e.getScreenY());
			} else {
				contextMenu.hide();
			}
		});
	}

	private void exportPoints() {
		try {
			FileChooser fileChooser = UtilMethods.createFileChooser(I18n.get("text.exportcsv"),
					String.format("%s_%s_CLUSTERING.csv", controller.getActualCourse().getId(),
							LocalDateTime.now().format(DTF)),
					ConfigHelper.getProperty("csvFolderPath", "./"), new ExtensionFilter("CSV (*.csv)", "*.csv"));
			File file = fileChooser.showSaveDialog(controller.getStage());
			if (file != null) {
				CSVClustering.exportPoints(clusters, file.toPath());
				UtilMethods.infoWindow(I18n.get("message.export_csv_success") + file.getAbsolutePath());
			}
		} catch (Exception e) {
			LOGGER.error("Error al exportar el fichero CSV.", e);
			UtilMethods.errorWindow(I18n.get("error.savecsvfiles"), e);
		}
	}

	private void exportPNG() {
		try {
			FileChooser fileChooser = UtilMethods.createFileChooser(I18n.get("text.exportcsv"),
					String.format("%s_%s_CLUSTERING.png", controller.getActualCourse().getId(),
							LocalDateTime.now().format(DTF)),
					ConfigHelper.getProperty("csvFolderPath", "./"), new ExtensionFilter("PNG (*.png)", "*.png"));
			File file = fileChooser.showSaveDialog(controller.getStage());
			if (file != null) {
				connector.export(file);
				UtilMethods.infoWindow(I18n.get("message.export_csv_success") + file.getAbsolutePath());
			}
		} catch (IOException e) {
			LOGGER.error("Error al exportar el fichero PNG.", e);
			UtilMethods.errorWindow(I18n.get("error.savechart"), e);
		}
	}

	public void updateChart(List<ClusterWrapper> clusters) {
		this.clusters = clusters;
		connector.setClusters(clusters);
		List<Map<UserData, double[]>> points = AlgorithmExecuter.clustersTo2D(clusters);
		LOGGER.debug("Puntos: {}", points);
		JSObject root = new JSObject();
		JSArray datasets = new JSArray();
		for (int i = 0; i < points.size(); i++) {
			JSObject group = new JSObject();
			group.putWithQuote("label", clusters.get(i).getName());
			group.put("backgroundColor", "colorHash.hex(" + i * i + ")");
			group.put("pointRadius", 6);
			group.put("pointHoverRadius", 8);
			JSArray data = new JSArray();
			for (Map.Entry<UserData, double[]> userEntry : points.get(i).entrySet()) {
				JSObject coord = new JSObject();
				coord.putWithQuote("user", userEntry.getKey().getEnrolledUser().getFullName());
				coord.put("x", userEntry.getValue()[0]);
				coord.put("y", userEntry.getValue()[1]);
				data.add(coord);
			}
			group.put("data", data);
			datasets.add(group);
		}
		root.put("datasets", datasets);
		LOGGER.debug("Data: {}", root);
		webEngine.executeScript("updateChart(" + root + ")");
	}

}
