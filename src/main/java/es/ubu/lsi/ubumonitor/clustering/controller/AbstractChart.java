package es.ubu.lsi.ubumonitor.clustering.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public abstract class AbstractChart {

	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
	private static final String FILE_NAME_CSV = "%s_%s_CLUSTERING.csv";
	private static final String FILE_NAME_PNG = "%s_%s_CLUSTERING.png";
	private static final ExtensionFilter EXTENSION_CSV = new ExtensionFilter("CSV (*.csv)", "*.csv");
	private static final ExtensionFilter EXTENSION_PNG = new ExtensionFilter("PNG (*.png)", "*.png");

	private Controller controller;
	private WebEngine webEngine;

	protected AbstractChart(WebView webView) {
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

	public void rename(List<ClusterWrapper> clusters) {
		JSArray names = new JSArray();
		clusters.forEach(c -> names.addWithQuote(c.getName()));
		webEngine.executeScript("rename(" + names + ")");
	}

	private void exportCSV() {
		try {
			String fileName = String.format(FILE_NAME_CSV, controller.getActualCourse().getId(),
					LocalDateTime.now().format(DTF));

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

	protected abstract void exportData(File file) throws IOException;

	private void exportPNG() {
		try {
			String fileName = String.format(FILE_NAME_PNG, controller.getActualCourse().getId(),
					LocalDateTime.now().format(DTF));

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
		String str = (String) webEngine.executeScript("exportGraphic()");
		byte[] imgdata = DatatypeConverter.parseBase64Binary(str.substring(str.indexOf(',') + 1));
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imgdata));
		ImageIO.write(bufferedImage, "png", file);
	}

	protected WebEngine getWebEngine() {
		return webEngine;
	}

}
