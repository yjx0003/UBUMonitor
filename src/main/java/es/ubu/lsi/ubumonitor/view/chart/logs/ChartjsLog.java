package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;

public abstract class ChartjsLog extends ChartLogs {
	

	public ChartjsLog(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
		
	}

	protected JSArray createLabels(List<String> rangeDates) {
		JSArray labels = new JSArray();
		for (String date : rangeDates) {
			labels.add("'" + UtilMethods.escapeJavaScriptText(date) + "'");
		}
		return labels;
	}



	@Override
	public void clear() {
		webViewChartsEngine.executeScript("clearChartjs()");

	}

	@Override
	public void hideLegend() {
		webViewChartsEngine.executeScript("hideLegendChartjs()");

	}

	@Override
	public void export(File file) throws IOException {
		String str = (String) webViewChartsEngine.executeScript("exportChartjs()");
		byte[] imgdata = DatatypeConverter.parseBase64Binary(str.substring(str.indexOf(',') + 1));
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imgdata));

		ImageIO.write(bufferedImage, "png", file);
		
		UtilMethods.showExportedFile(file);
	}
	

	public String getXScaleLabel() {
		MainConfiguration mainConfiguration = controller.getMainConfiguration();
		JSObject jsObject = new JSObject();
		jsObject.put("display", mainConfiguration.getValue(MainConfiguration.GENERAL, "displayXScaleTitle"));
		jsObject.putWithQuote("labelString", getXAxisTitle());
		jsObject.put("fontColor",
				colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "fontColorXScaleTitle")));
		jsObject.putWithQuote("fontStyle", "bold");

		return "scaleLabel:" + jsObject;

	}


	public String getYScaleLabel() {
		MainConfiguration mainConfiguration = controller.getMainConfiguration();
		JSObject jsObject = new JSObject();
		jsObject.put("display", mainConfiguration.getValue(MainConfiguration.GENERAL, "displayYScaleTitle"));
		jsObject.putWithQuote("labelString", getYAxisTitle());
		jsObject.putWithQuote("fontSize", 14);
		jsObject.put("fontColor",
				colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "fontColorYScaleTitle")));
		jsObject.putWithQuote("fontStyle", "bold");

		return "scaleLabel:" + jsObject;

	}

	@Override
	protected String getJSFunction(String dataset, String options) {
		return "updateChartjs" + "(" + dataset + "," + options + ")";
	}
}
