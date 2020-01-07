package controllers.charts;

import java.util.List;

import controllers.MainController;
import model.GradeItem;

public class Radar extends ChartjsGradeItem {

	private static final GradeItem DUMMY = new GradeItem("");

	public Radar(MainController mainController) {
		super(mainController, ChartType.RADAR);
		useGeneralButton = true;
		useGroupButton = true;
		optionsVar = "radarOptions";
	}

	@Override
	public List<GradeItem> getSelectedGradeItems() {
		List<GradeItem> gradeItems = super.getSelectedGradeItems();
		for (int i = gradeItems.size(); i < 3; i++) {
			gradeItems.add(DUMMY);
		}
		return gradeItems;
	}

}
