package es.ubu.lsi.ubumonitor.view.chart;

import java.util.Arrays;
import java.util.stream.IntStream;

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
		LOGGER.debug("Plotly:{}", plot);

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
		
		 fillOptions(jsObject, createConfig());
		 return jsObject;
	}
	
	public static void fillOptions(JSObject jsObject, JSObject config) {
		jsObject.put("onClick",
				"function(n){if(n!==undefined&&n.points!==undefined){let t=n.points[counter++%n.points.length];if(t!==undefined&&t.data!==undefined&&t.data.userids!==undefined){javaConnector.dataPointSelection(t.data.userids[t.pointIndex]||-100);}}}");

		jsObject.put("config", config);

	}

	public static JSObject createConfig() {
		JSObject config = new JSObject();
		config.put("displaylogo", false);
		config.put("responsive", true);
		config.put("modeBarButtonsToRemove", "['toImage']");
		config.put("scrollZoom", true);
		return config;
	}

	@Override
	public String getYAxisTitle() {
		return "<b>" + super.getYAxisTitle() + "</b>";
	}

	@Override
	public String getXAxisTitle() {
		return "<b>" + super.getXAxisTitle() + "</b>";
	}

	public static void horizontalMode(JSObject layout, JSArray ticktext, boolean horizontalMode, String xAxisTitle,
			String yAxisTitle, String range) {
		JSObject xaxis = new JSObject();
		JSObject yaxis = new JSObject();
		xaxis.put("zeroline", false);
		yaxis.put("zeroline", false);
		xaxis.put("automargin", true);
		yaxis.put("automargin", true);
		int[] tickvals = IntStream.range(0, ticktext.size())
				.toArray();

		if (horizontalMode) {
			xaxis.putWithQuote("title", yAxisTitle);
			if (range == null) {
				xaxis.put("rangemode", "'tozero'");
			} else {
				xaxis.put("range", range);
			}

			yaxis.putWithQuote("title", xAxisTitle);
			yaxis.put("tickvals", Arrays.toString(tickvals));
			yaxis.put("ticktext", ticktext);


		} else {
			xaxis.putWithQuote("title", xAxisTitle);
			yaxis.putWithQuote("title", yAxisTitle);
			if (range == null) {
				yaxis.put("rangemode", "'tozero'");
			} else {
				yaxis.put("range", range);
			}
			xaxis.put("tickvals", Arrays.toString(tickvals));
			xaxis.put("ticktext", ticktext);
		}

		layout.put("xaxis", xaxis);
		layout.put("yaxis", yaxis);
		
	}
}
