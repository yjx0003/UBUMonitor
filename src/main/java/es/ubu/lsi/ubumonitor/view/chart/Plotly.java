package es.ubu.lsi.ubumonitor.view.chart;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSException;

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
		try {
			webViewChartsEngine.executeScript("updatePlotly(" + plot + "," + getOptions() + ")");
		}catch (JSException e) {
			LOGGER.info("Probably updating too fast plotly: {}", e.getMessage());
		}

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
	public void fillOptions(JSObject jsObject) {

		fillOptions(jsObject, createConfig());
	}

	public static void fillOptions(JSObject jsObject, JSObject config) {
		jsObject.put("onClick",
				"function(n){if(n!==undefined&&n.points!==undefined){let t=n.points[counter++%n.points.length];if(t!==undefined&&t.data!==undefined&&t.data.userids){javaConnector.dataPointSelection(t.data.userids[t.pointIndex]||t.data.userids||-100);}}}");

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

		List<Integer> tickvals = IntStream.range(0, ticktext.size())
				.boxed()
				.collect(Collectors.toList());

		if (horizontalMode) {

			if (range == null) {
				xaxis.put("rangemode", "'tozero'");
			} else {
				xaxis.put("range", range);
			}
			String temp = xAxisTitle;
			xAxisTitle = yAxisTitle;
			yAxisTitle = temp;

			createCategoryAxis(yaxis, tickvals, ticktext);

		} else {

			if (range == null) {
				yaxis.put("rangemode", "'tozero'");
			} else {
				yaxis.put("range", range);
			}

			createCategoryAxis(xaxis, tickvals, ticktext);
		}

		defaultAxisValues(xaxis, xAxisTitle);
		defaultAxisValues(yaxis, yAxisTitle);

		layout.put("xaxis", xaxis);
		layout.put("yaxis", yaxis);

	}

	public static void defaultAxisValues(JSObject axis, String title) {
		axis.put("zeroline", false);
		axis.put("automargin", true);
		if (title != null)
			axis.putWithQuote("title", title);

	}

	public static void createCategoryAxis(JSObject axis, Collection<?> tickvals, Collection<Object> ticktext) {
		axis.put("type", "'category'");
		axis.put("tickvals", tickvals);
		axis.put("ticktext", ticktext);
	}
	
	
	public static void createAxisValuesHorizontal(boolean horizontalMode, JSObject trace, Collection<?> x,
			 Collection<?> y) {
		if (horizontalMode) {
			trace.put("y", x);
			trace.put("x", y);
			trace.put("orientation", "'h'");
		} else {
			trace.put("x", x);
			trace.put("y", y);
		}
	}

	public static String getHorizontalModeHoverTemplate(boolean useHorizontal) {
		return "'<b>%{" + (useHorizontal ? "y" : "x") + "}<br>%{text}: </b>%{"
				+ (useHorizontal ? "x" : "y") + ":.2f}<extra></extra>'";
	}
}
