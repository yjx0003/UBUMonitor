package es.ubu.lsi.ubumonitor.view.chart;

import es.ubu.lsi.ubumonitor.controllers.MainController;

public abstract class Tabulator extends Chart{

	public Tabulator(MainController mainController, ChartType chartType, Tabs tabName) {
		super(mainController, chartType, tabName);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideLegend() {
		// do nothing
		
	}

	@Override
	public String export() {
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
