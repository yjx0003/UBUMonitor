package es.ubu.lsi.controllers.charts;

import java.util.StringJoiner;

import es.ubu.lsi.controllers.MainController;

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
	

}