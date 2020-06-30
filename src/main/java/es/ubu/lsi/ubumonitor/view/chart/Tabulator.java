package es.ubu.lsi.ubumonitor.view.chart;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

public abstract class Tabulator extends Chart {


	public Tabulator(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
	}

	@Override
	public void clear() {
		webViewChartsEngine.executeScript("clearTabulator()");

	}

	@Override
	public void hideLegend() {
		// do nothing

	}

	@Override
	public void export(File file) throws IOException {
		WritableImage image = webView.snapshot(new SnapshotParameters(), null);

		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

	}

}
