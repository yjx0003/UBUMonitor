package controllers.charts;

import java.util.StringJoiner;

import controllers.MainController;

public class Line extends ChartjsGradeItem {

	public Line(MainController mainController) {
		super(mainController, ChartType.LINE);
		

	}

	@Override
	public String getOptions() {
		StringJoiner jsObject = getDefaultOptions();
		addKeyValueWithQuote(jsObject, "typeGraph", "line");
		return jsObject.toString();
	}
	

}
