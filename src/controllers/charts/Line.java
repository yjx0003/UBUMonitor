package controllers.charts;

import controllers.JavaConnector.ChartType;
import controllers.MainController;

public class Line extends Chartjs {

	
	public Line(MainController mainController) {
		super(mainController, ChartType.LINE);

	}

	@Override
	public void update() {

		String dataset = createDataset(getSelectedEnrolledUser(), getSelectedGradeItems(),
				stats, true);
		
		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", dataset, "lineOptions"));
	}

}
