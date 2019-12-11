package controllers.charts;

import controllers.MainController;
import controllers.JavaConnector.ChartType;

public class Radar extends Chartjs {

	public Radar(MainController mainController) {
		super(mainController, ChartType.RADAR);
		
	}

	@Override
	public void update() {
		
		String dataset = createDataset(getSelectedEnrolledUser(), getSelectedGradeItems(),
				stats, false);
		
		webViewChartsEngine.executeScript("updateRadar("+dataset+")");
	}

}
