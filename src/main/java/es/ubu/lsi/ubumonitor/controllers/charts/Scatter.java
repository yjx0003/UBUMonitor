package es.ubu.lsi.ubumonitor.controllers.charts;

import java.io.IOException;

import es.ubu.lsi.ubumonitor.controllers.MainController;

public class Scatter extends Chartjs{

	public Scatter(MainController mainController, Tabs tabName) {
		super(mainController, ChartType.SCATTER, Tabs.LOGS);
		useLegend = true;
	}

	@Override
	public void exportCSV(String path) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getOptions() {
		return getDefaultOptions().toString();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	
}
