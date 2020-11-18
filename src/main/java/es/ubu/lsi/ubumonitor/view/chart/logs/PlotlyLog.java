package es.ubu.lsi.ubumonitor.view.chart.logs;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;

public abstract class PlotlyLog extends ChartLogs {


	public PlotlyLog(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
	}

	@Override
	protected String getJSFunction(String dataset, String options) {
		return "updatePlotly(" + dataset + "," + options + ")";
	}


	@Override
	public JSObject getOptions(JSObject jsObject) {
		Plotly.fillOptions(jsObject, Plotly.createConfig());
		return jsObject;
	}

	@Override
	public void clear() {
		Plotly.clear(webViewChartsEngine);

	}
	
	@Override
	public int onClick(int userid) {
		EnrolledUser user = Controller.getInstance()
				.getDataBase()
				.getUsers()
				.getById(userid);
		return getUsers().indexOf(user);
	}


}
