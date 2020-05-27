package es.ubu.lsi.ubumonitor.view.chart;

import java.io.File;

import es.ubu.lsi.ubumonitor.controllers.MainController;

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
	public String export(File file) {
//		WritableImage image = mainController.getVisualizationController().getWebViewCharts().snapshot(new SnapshotParameters(), null);
//		 try {
//			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File("chart.png"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		webViewChartsEngine.executeScript("genericExport('tabulatorDiv')");
		return null;
	}

}
