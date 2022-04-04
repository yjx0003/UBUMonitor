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
	public static final String DEFAULT_ON_CLICK_FUNCTION = "function(n){if(n!==undefined&&n.points!==undefined){let t=n.points[counter++%n.points.length];if(t!==undefined&&t.data!==undefined&&t.data.userids){javaConnector.dataPointSelection(t.data.userids[t.pointIndex]||t.data.userids||-100);}}}";
	public static final String MULTIPLE_USER_ON_CLICK_FUNCTION = "function(n){if(n!==undefined&&n.points!==undefined){let c=counter++;let t=n.points[c%n.points.length];if(t!==undefined&&t.data!==undefined&&t.data.userids){javaConnector.dataPointSelection(t.data.userids[t.pointIndex][c%t.data.userids[t.pointIndex].length]);}}}";

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
		JSObject options = getOptions();
		plot.put("data", data);
		plot.put("layout", layout);
		plot.put("frames", frames);
		LOGGER.debug("Plotly:{}\nOptions plotly:{}", plot, options);
		try {
			webViewChartsEngine.executeScript("updatePlotly(" + plot + "," + options + ")");
		} catch (JSException e) {
			
			LOGGER.debug("Probably updating too fast plotly: {}", e);
			

		}

	}

	public abstract void createData(JSArray data);

	public abstract void createLayout(JSObject layout);

	public void createFrames(JSArray frames) {
	}

	@Override
	public int onClick(int userid) {
		EnrolledUser user = Controller.getInstance()
				.getDataBase()
				.getUsers()
				.getById(userid);
		return getFilteredUsers().indexOf(user);
	}

	@Override
	public void fillOptions(JSObject jsObject) {

		fillOptions(jsObject, createConfig(), getOnClickFunction());
	}

	public static void fillOptions(JSObject jsObject, JSObject config, String onClickFunction) {
		jsObject.put("onClick", onClickFunction);

		jsObject.put("config", config);

	}

	public String getOnClickFunction() {
		return DEFAULT_ON_CLICK_FUNCTION;
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
		horizontalMode(layout, new JSObject(), new JSObject(), ticktext, horizontalMode, xAxisTitle, yAxisTitle, range);
	}

	public static void horizontalMode(JSObject layout, JSObject xaxis, JSObject yaxis, JSArray ticktext,
			boolean horizontalMode, String xAxisTitle, String yAxisTitle, String range) {

		if (horizontalMode) {

			String temp = xAxisTitle;
			xAxisTitle = yAxisTitle;
			yAxisTitle = temp;
			
			if (ticktext != null) {
				List<Integer> tickvals = IntStream.range(0, ticktext.size())
						.boxed()
						.collect(Collectors.toList());
				createCategoryAxis(yaxis, tickvals, ticktext);
			}
			yaxis.put("autorange", "'reversed'");
			defaultAxisValues(xaxis, xAxisTitle, range);
			defaultAxisValues(yaxis, yAxisTitle, "");
		} else {

			if (ticktext != null) {
				List<Integer> tickvals = IntStream.range(0, ticktext.size())
						.boxed()
						.collect(Collectors.toList());
				createCategoryAxis(xaxis, tickvals, ticktext);
			}

			defaultAxisValues(xaxis, xAxisTitle, "");
			defaultAxisValues(yaxis, yAxisTitle, range);
		}
		
		layout.put("xaxis", xaxis);
		layout.put("yaxis", yaxis);

	}

	public static void defaultAxisValues(JSObject axis, String title, String range) {
		axis.put("zeroline", false);
		axis.put("automargin", true);
		if (title != null)
			axis.putWithQuote("title", title);
		if (range != null) {

			if (range.isEmpty()) {
				axis.put("rangemode", "'tozero'");
			} else {
				axis.put("range", range);
			}
		}

	}

	public static void createCategoryAxis(JSObject axis, Collection<?> tickvals, Collection<Object> ticktext) {
		axis.put("type", "'category'");
		axis.put("tickvals", tickvals);
		axis.put("tickmode", "'array'");
		// axis.put("nticks", 20);
		axis.put("ticktext", ticktext);
	}

	public static void createCategoryAxis(JSObject axis, Collection<Object> ticktext) {

		createCategoryAxis(axis, IntStream.range(0, ticktext.size())
				.boxed()
				.collect(Collectors.toList()), ticktext);
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

}
