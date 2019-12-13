package controllers.charts;

import java.util.List;

import controllers.JavaConnector.ChartType;
import controllers.MainController;
import model.GradeItem;

public class Radar extends Chartjs {

	private static final GradeItem DUMMY = new GradeItem("");

	public Radar(MainController mainController) {
		super(mainController, ChartType.RADAR);

	}

	@Override
	public void update() {
		List<GradeItem> gradeItems = getSelectedGradeItems();
		for (int i = gradeItems.size(); i < 3; i++) {
			gradeItems.add(DUMMY);
		}
		String dataset = createDataset(getSelectedEnrolledUser(), gradeItems, stats, true, true);

		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", dataset, "radarOptions"));
	}

}
