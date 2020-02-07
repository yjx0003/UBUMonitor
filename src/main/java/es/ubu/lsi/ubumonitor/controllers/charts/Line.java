package es.ubu.lsi.ubumonitor.controllers.charts;

import java.io.IOException;
import java.util.StringJoiner;

import es.ubu.lsi.ubumonitor.controllers.MainController;

public class Line extends ChartjsGradeItem {

	public Line(MainController mainController) {
		super(mainController, ChartType.LINE);
		

	}

	@Override
	public String getOptions() {
		StringJoiner jsObject = getDefaultOptions();
		addKeyValueWithQuote(jsObject, "typeGraph", "line");
		addKeyValue(jsObject, "scales", "{yAxes:[{" + getYScaleLabel() + "}],xAxes:[{" + getXScaleLabel() + "}]}");
		return jsObject.toString();
	}

	@Override
	public void exportCSV(String path) throws IOException {
		// TODO Auto-generated method stub
		
	}
	

}
