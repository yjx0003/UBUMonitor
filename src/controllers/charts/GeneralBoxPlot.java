package controllers.charts;

import java.util.Collection;
import java.util.List;

import controllers.Controller;
import controllers.I18n;
import controllers.JavaConnector.ChartType;
import controllers.MainController;
import model.EnrolledUser;
import model.GradeItem;
import model.Stats;
import util.UtilMethods;

public class GeneralBoxPlot extends Chartjs {

	public GeneralBoxPlot(MainController mainController) {
		super(mainController, ChartType.GENERAL_BOXPLOT);
	}

	@Override
	public void update() {
		String dataset = createDataset(getSelectedEnrolledUser(), getSelectedGradeItems(), stats, true, true);
		System.out.println(String.format("updateChartjs(%s,%s)", dataset, "boxplotOptions"));
		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", dataset, "boxplotOptions"));

	}

	@Override
	public String createDataset(List<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems, Stats stats,
			boolean withMean, boolean withGroupMean) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("{labels:[");
		stringBuilder.append(UtilMethods.joinWithQuotes(selectedGradeItems));
		stringBuilder.append("],datasets:[");

		createData(selectedUser, selectedGradeItems, stringBuilder, I18n.get("text.selectedUsers"), false);
		if (withMean) {
			createData(Controller.getInstance().getActualCourse().getEnrolledUsers(), selectedGradeItems, stringBuilder, I18n.get("text.all"), !Buttons.getInstance().getShowMean());
		}
		if(withGroupMean && slcGroup.getValue() != null) {
			createData(slcGroup.getValue().getEnrolledUsers(), selectedGradeItems, stringBuilder, slcGroup.getValue().getGroupName(), !Buttons.getInstance().getShowGroupMean());

		}
		

		stringBuilder.append("]}");
		System.out.println(stringBuilder.toString());
		return stringBuilder.toString();
	}

	private void createData(Collection<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems,
			StringBuilder stringBuilder, String text, boolean hidden) {
		stringBuilder.append("{label:'" + text+"',");
		stringBuilder.append("borderColor:" + rgba(text, 0.7) + ",");
		stringBuilder.append("backgroundColor:" + rgba(text, OPACITY) + ",");

		stringBuilder.append("padding: 10,");
		stringBuilder.append("itemRadius: 2,");
		stringBuilder.append("itemStyle: 'circle',");
		stringBuilder.append("itemBackgroundColor:" + hex(text) + ",");
		stringBuilder.append("outlierColor: 'blue',");
		stringBuilder.append("borderWidth: 1,");
		stringBuilder.append("outlierRadius : 5,");
		stringBuilder.append("hidden:"+hidden+",");
		stringBuilder.append("data:[");

		for (GradeItem gradeItem : selectedGradeItems) {
			stringBuilder.append("[");
			for (EnrolledUser user : selectedUser) {
				double grade = gradeItem.getEnrolledUserPercentage(user);
				if (!Double.isNaN(grade))
					stringBuilder.append(adjustTo10(grade) + ",");
			}
			stringBuilder.append("],");
		}
		stringBuilder.append("]},");
	}
	
	@Override
	public int onClick(int index) {
		return -1; //do nothing at the moment
	}

}
