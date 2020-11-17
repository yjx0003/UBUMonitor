package es.ubu.lsi.ubumonitor.view.chart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import javafx.scene.web.WebEngine;

public abstract class Plotly extends Chart {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Plotly.class);
	

	public Plotly(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
	}

	@Override
	public void clear() {
		clear(webViewChartsEngine);
	}

	public static void clear(WebEngine webEngine) {
		webEngine.executeScript("Plotly.purge('plotlyDiv')");
	}

	@Override
	public void update() {
		JSArray data = new JSArray();
		JSObject layout = new JSObject();
		JSArray frames = new JSArray();
		createData(data);
		createLayout(layout);
		createFrames(frames);
		
		JSObject plot = new JSObject();

		plot.put("data", data);
		plot.put("layout", layout);
		plot.put("frames", frames);
		LOGGER.debug("Plotly:{}",plot);
		
		webViewChartsEngine.executeScript("updatePlotly(" + plot + "," + getOptions() + ")");
		
	}


	public abstract void createData(JSArray data);

	public void createLayout(JSObject layout) {
	}

	public void createFrames(JSArray frames) {
	}
	
	@Override
	public int onClick(int userid) {
		EnrolledUser user = Controller.getInstance()
				.getDataBase()
				.getUsers()
				.getById(userid);
		return getUsers().indexOf(user);
	}

	@Override
	public JSObject getOptions(JSObject jsObject) {
		jsObject.put("onClick",
				"function(n){if(n!==undefined&&n.points!==undefined){let t=n.points[counter++%n.points.length];if(t!==undefined&&t.data!==undefined&&t.data.userids!==undefined)javaConnector.dataPointSelection(t.data.userids[t.pointIndex]||-100)}}");
		return jsObject;
	}
	
	@Override
	public String getYAxisTitle() {
		return "<b>" + super.getYAxisTitle() + "</b>";
	}
	
	@Override
	public String getXAxisTitle() {
		return "<b>" + super.getXAxisTitle() + "</b>";
	}
	
	public void horizontalMode(JSObject layout, JSArray tickvals, JSArray ticktext) {
		JSObject xaxis = new JSObject();
		JSObject yaxis = new JSObject();
		xaxis.put("zeroline", false);
		yaxis.put("zeroline", false);
		boolean useHorizontal = getConfigValue("horizontalMode");
		if (useHorizontal) {
			xaxis.putWithQuote("title", getYAxisTitle());
			xaxis.put("range", "[-0.5,10.5]");
			yaxis.putWithQuote("title", getXAxisTitle());
			yaxis.put("tickvals", tickvals);
			yaxis.put("ticktext", ticktext);
			yaxis.put("tickmode", "'array'");

		} else {
			xaxis.putWithQuote("title", getXAxisTitle());
			yaxis.putWithQuote("title", getYAxisTitle());
			yaxis.put("range", "[-0.5,10.5]");
			xaxis.put("tickvals", tickvals);
			xaxis.put("ticktext", ticktext);
			xaxis.put("tickmode", "'array'");
		}

		layout.put("xaxis", xaxis);
		layout.put("yaxis", yaxis);
	}
}
