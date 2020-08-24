package es.ubu.lsi.ubumonitor.view.chart;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebView;

public abstract class VisChart extends Chart {
	private WebView webView;
	public VisChart(MainController mainController, ChartType chartType, WebView webView) {
		super(mainController, chartType);
		this.webView =webView;
	}


	@Override
	public void clear() {
		webViewChartsEngine.executeScript("clearVisChart()");

	}

	@Override
	public void hideLegend() {
		//do nothing

	}

	@Override
	public void exportImage(File file) throws IOException {
		WritableImage image = webView.snapshot(new SnapshotParameters(), null);

		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

	}

}
