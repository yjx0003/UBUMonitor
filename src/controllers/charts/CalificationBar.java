package controllers.charts;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import controllers.I18n;
import controllers.MainController;
import model.EnrolledUser;
import model.GradeItem;
import util.UtilMethods;

public class CalificationBar extends ChartjsGradeItem {

	public CalificationBar(MainController mainController) {
		super(mainController, ChartType.CALIFICATION_BAR);
		useGeneralButton = false;
		useGroupButton = false;
		optionsVar = "calificationBarOptions";
	}

	@Override
	public String createDataset(List<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems) {

		StringJoiner data = JSObject();
		addKeyValue(data, "labels", "[" + UtilMethods.joinWithQuotes(selectedGradeItems) + "]");

		List<Integer> countNaN = new ArrayList<>();
		List<Integer> countLessCut = new ArrayList<>();
		List<Integer> countGreaterCut = new ArrayList<>();
		for (GradeItem gradeItem : selectedGradeItems) {
			int nan = 0;
			int less = 0;
			int greater = 0;
			for (EnrolledUser user : selectedUser) {
				double grade = adjustTo10(gradeItem.getEnrolledUserPercentage(user));
				if (Double.isNaN(grade)) {
					++nan;
				} else if (grade < Buttons.getInstance().getCutGrade()) {
					++less;
				} else {
					++greater;
				}
			}
			countNaN.add(nan);
			countLessCut.add(less);
			countGreaterCut.add(greater);
		}

		StringJoiner datasets = JSArray();
		datasets.add(createData(I18n.get("text.empty"), countNaN, Buttons.getInstance().getEmptyColor(), OPACITY));
		datasets.add(createData(I18n.get("text.fail"), countLessCut, Buttons.getInstance().getDangerColor(), OPACITY));
		datasets.add(createData(I18n.get("text.pass"), countGreaterCut, Buttons.getInstance().getPassColor(), OPACITY));
		addKeyValue(data, "datasets", datasets.toString());
		return data.toString();
	}

	private String createData(String label, List<Integer> data, String hexColor, double opacity) {
		StringJoiner dataset = JSObject();
		addKeyValueWithQuote(dataset, "label", label);
		addKeyValue(dataset, "data", "[" + UtilMethods.join(data) + "]");
		addKeyValue(dataset, "backgroundColor", hexToRGBA(hexColor, opacity));
		//addKeyValueWithQuote(dataset, "borderColor", hexColor);
		//addKeyValue(dataset, "borderWidth", 2);
		return dataset.toString();
	}

}
