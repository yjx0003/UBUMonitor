package controllers.charts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.JavaConnector.ChartType;
import controllers.MainController;

public class Line extends Chartjs {

	private static final Logger LOGGER = LoggerFactory.getLogger(Line.class);

	public Line(MainController mainController) {
		super(mainController, ChartType.LINE);

	}

	@Override
	public void update() {

		String dataset = createDataset(listParticipants.getSelectionModel().getSelectedItems(), getSelectedGradeItems(),
				stats, true);
		LOGGER.debug(dataset);
		webViewChartsEngine.executeScript("updateLine("+dataset+")");
	}

}
