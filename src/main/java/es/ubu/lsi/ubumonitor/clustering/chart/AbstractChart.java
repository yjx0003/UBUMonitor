package es.ubu.lsi.ubumonitor.clustering.chart;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Clase base de gestiona de gráficas.
 * 
 * @author Xing Long Ji
 *
 */
public abstract class AbstractChart {

	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
	private static final String FILE_NAME_CSV = "%s_%s_CLUSTERING.csv";
	private static final String FILE_NAME_PNG = "%s_%s_CLUSTERING.png";
	private static final ExtensionFilter EXTENSION_CSV = new ExtensionFilter("CSV (*.csv)", "*.csv");
	private static final ExtensionFilter EXTENSION_PNG = new ExtensionFilter("PNG (*.png)", "*.png");

	private Controller controller;
	private WebView webView;
	private WebEngine webEngine;

	/**
	 * Constructor a traves de un WebView.
	 * 
	 * @param webView WebView que contiene la gráfica
	 */
	protected AbstractChart(WebView webView) {
		this.webView = webView;
		webEngine = webView.getEngine();
		controller = Controller.getInstance();
		webView.setContextMenuEnabled(false);
		initContextMenu(webView);
	}

	private void initContextMenu(WebView webView) {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setAutoHide(true);

		MenuItem exportCSV = new MenuItem(I18n.get("text.exportcsv"));
		exportCSV.setOnAction(e -> exportCSV());
		MenuItem exportPNG = new MenuItem(I18n.get("text.exportpng"));
		exportPNG.setOnAction(e -> exportPNG());

		contextMenu.getItems().setAll(exportCSV, exportPNG);
		webView.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				contextMenu.show(webView, e.getScreenX(), e.getScreenY());
			} else {
				contextMenu.hide();
			}
		});
	}

	private void exportCSV() {
		try {
			String fileName = getFileName(FILE_NAME_CSV);

			FileChooser fileChooser = UtilMethods.createFileChooser(I18n.get("text.exportcsv"), fileName,
					ConfigHelper.getProperty("csvFolderPath", "./"), EXTENSION_CSV);

			File file = fileChooser.showSaveDialog(controller.getStage());
			if (file != null) {
				exportData(file);
				UtilMethods.infoWindow(I18n.get("message.export_csv") + file.getAbsolutePath());
			}
		} catch (IOException e) {
			UtilMethods.errorWindow(I18n.get("error.savecsvfiles"), e);
		}
	}

	private String getFileName(String nameFormat) {
		return String.format(nameFormat, controller.getActualCourse().getId(), LocalDateTime.now().format(DTF));
	}

	/**
	 * Exporta los datos de la gráfica a un fichero CSV.
	 * 
	 * @param file fichero
	 * @throws IOException si hay un error de E/S
	 */
	protected abstract void exportData(File file) throws IOException;

	private void exportPNG() {
		try {
			String fileName = getFileName(FILE_NAME_PNG);

			FileChooser fileChooser = UtilMethods.createFileChooser(I18n.get("text.exportpng"), fileName,
					ConfigHelper.getProperty("csvFolderPath", "./"), EXTENSION_PNG);

			File file = fileChooser.showSaveDialog(controller.getStage());
			if (file != null) {
				exportImage(file);
				UtilMethods.infoWindow(I18n.get("message.export_png") + file.getAbsolutePath());
			}
		} catch (IOException e) {
			UtilMethods.errorWindow(I18n.get("error.savechart"), e);
		}
	}

	private void exportImage(File file) throws IOException {
		WritableImage image = webView.snapshot(new SnapshotParameters(), null);
		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	}

	/**
	 * Devuelve el motor web.
	 * 
	 * @return WebEngine
	 */
	protected WebEngine getWebEngine() {
		return webEngine;
	}

}
