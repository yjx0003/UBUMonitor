package controllers.charts;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import controllers.Controller;
import controllers.I18n;
import controllers.MainController;
import controllers.configuration.MainConfiguration;
import model.EnrolledUser;
import model.GradeItem;
import model.Group;
import util.UtilMethods;

public class Violin extends ChartjsGradeItem {

	public Violin(MainController mainController) {
		super(mainController, ChartType.VIOLIN);
	}

	@Override
	public String createDataset(List<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems) {
		StringBuilder stringBuilder = new StringBuilder();
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		stringBuilder.append("{labels:[");
		stringBuilder.append(UtilMethods.joinWithQuotes(selectedGradeItems));
		stringBuilder.append("],datasets:[");
		if (selectedUser.size() > 0) {
			createData(selectedUser, selectedGradeItems, stringBuilder, I18n.get("text.selectedUsers"), false);

		}
		if (useGeneralButton) {
			createData(Controller.getInstance().getActualCourse().getEnrolledUsers(), selectedGradeItems, stringBuilder,
					I18n.get("text.all"), !(boolean) mainConfiguration.getValue("General", "generalActive"));
		}
		if (useGroupButton) {
			for (Group group : slcGroup.getCheckModel().getCheckedItems()) {

				createData(group.getEnrolledUsers(), selectedGradeItems, stringBuilder, group.getGroupName(),
						!(boolean) mainConfiguration.getValue("General", "groupActive"));

			}

		}

		stringBuilder.append("]}");

		return stringBuilder.toString();
	}

	private void createData(Collection<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems,
			StringBuilder stringBuilder, String text, boolean hidden) {
		stringBuilder.append("{label:'" + text + "',");
		stringBuilder.append("borderColor:" + rgba(text, 0.7) + ",");
		stringBuilder.append("backgroundColor:" + rgba(text, OPACITY) + ",");

		stringBuilder.append("padding: 10,");
		stringBuilder.append("itemRadius: 2,");
		stringBuilder.append("itemStyle: 'circle',");
		stringBuilder.append("itemBackgroundColor:" + hex(text) + ",");
		stringBuilder.append("outlierColor:" + hex(text) + ",");
		stringBuilder.append("borderWidth: 1,");
		stringBuilder.append("outlierRadius : 5,");
		stringBuilder.append("hidden:" + hidden + ",");
		stringBuilder.append("data:[");

		for (GradeItem gradeItem : selectedGradeItems) {
			stringBuilder.append("[");
			boolean hasNonNaN = false;
			for (EnrolledUser user : selectedUser) {
				double grade = gradeItem.getEnrolledUserPercentage(user);
				if (!Double.isNaN(grade)) {
					stringBuilder.append(adjustTo10(grade) + ",");
					hasNonNaN = true;
				}
			}

			if (!hasNonNaN) {
				stringBuilder.append(-100000);
			}
			stringBuilder.append("],");
		}
		stringBuilder.append("]},");
	}

	@Override
	public int onClick(int index) {
		return -1; // do nothing at the moment
	}

	@Override
	public String getOptions() {
		StringJoiner jsObject = getDefaultOptions();
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		boolean useHorizontal = mainConfiguration.getValue(getChartType(), "horizontalMode");
		int tooltipDecimals = mainConfiguration.getValue(getChartType(), "tooltipDecimals");
		addKeyValueWithQuote(jsObject, "typeGraph", useHorizontal ? "horizontalViolin" : "violin");
		addKeyValue(jsObject, "tooltipDecimals", tooltipDecimals);
		addKeyValue(jsObject, "scales", "{yAxes:[{ticks:{min:0}}],xAxes:[{ticks:{min:0}}]}");
		return jsObject.toString();
	}
}
