package controllers.charts;

import controllers.MainController;

public abstract class ChartjsLog extends Chartjs{

	public ChartjsLog(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
		
	}
}
