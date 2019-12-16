package controllers.charts;

import controllers.MainController;

public class Line extends Chartjs {

	public Line(MainController mainController) {
		super(mainController, ChartType.LINE);
		useGeneralButton = true;
		useGroupButton = true;
		optionsVar = "lineOptions";

	}

}
