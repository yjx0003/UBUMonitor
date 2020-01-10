package controllers.charts;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.I18n;
import controllers.MainController;
import model.EnrolledUser;
import model.GradeItem;
import util.UtilMethods;

public abstract class ChartjsGradeItem extends Chartjs {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartjsGradeItem.class);

	public ChartjsGradeItem(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
	}

	public String createDataset(List<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("{labels:[");
		stringBuilder.append(UtilMethods.joinWithQuotes(selectedGradeItems));
		stringBuilder.append("],datasets:[");
		for (EnrolledUser user : selectedUser) {
			stringBuilder.append("{label:'" + UtilMethods.escapeJavaScriptText(user.getFullName()) + "',");
			stringBuilder.append("borderColor:" + hex(user.getId()) + ",");
			stringBuilder.append("backgroundColor:" + rgba(user.getId(), OPACITY) + ",");
			stringBuilder.append("data:[");
			for (GradeItem gradeItem : selectedGradeItems) {
				double grade = gradeItem.getEnrolledUserPercentage(user);

				stringBuilder.append(adjustTo10(grade) + ",");

			}
			stringBuilder.append("]},");
		}
		if (useGeneralButton) {
			stringBuilder
					.append("{label:'" + UtilMethods.escapeJavaScriptText(I18n.get("chartlabel.generalMean")) + "',");
			stringBuilder.append("borderColor:" + hex(I18n.get("chartlabel.generalMean")) + ",");
			stringBuilder.append("backgroundColor:" + rgba(I18n.get("chartlabel.generalMean"), OPACITY) + ",");
			stringBuilder.append("hidden: " + !Buttons.getInstance().getShowMean() + ",");
			stringBuilder.append(
					"borderDash:[" + Buttons.getInstance().getLength() + "," + Buttons.getInstance().getSpace() + "],");
			stringBuilder.append("data:[");
			Map<GradeItem, DescriptiveStatistics> descriptiveStats = stats.getGeneralStats();
			for (GradeItem gradeItem : selectedGradeItems) {
				double grade = descriptiveStats.get(gradeItem).getMean();
				stringBuilder.append(adjustTo10(grade) + ",");
			}
			stringBuilder.append("]},");
		}

		if (useGroupButton && slcGroup.getValue() != null) {
			stringBuilder
					.append("{label:'" + UtilMethods.escapeJavaScriptText(I18n.get("chartlabel.groupMean")) + "',");
			stringBuilder.append("borderColor:" + hex(I18n.get("chartlabel.groupMean")) + ",");
			stringBuilder.append("backgroundColor:" + rgba(I18n.get("chartlabel.groupMean"), OPACITY) + ",");
			stringBuilder.append("hidden: " + !Buttons.getInstance().getShowGroupMean() + ",");
			stringBuilder.append(
					"borderDash:[" + Buttons.getInstance().getLength() + "," + Buttons.getInstance().getSpace() + "],");
			stringBuilder.append("data:[");
			Map<GradeItem, DescriptiveStatistics> descriptiveStats = stats.getGroupStats(slcGroup.getValue());
			for (GradeItem gradeItem : selectedGradeItems) {
				double grade = descriptiveStats.get(gradeItem).getMean();
				stringBuilder.append(adjustTo10(grade) + ",");
			}
			stringBuilder.append("]}");
		}

		stringBuilder.append("]}");

		return stringBuilder.toString();

	}

	@Override
	public void update() {
		String dataset = createDataset(getSelectedEnrolledUser(), getSelectedGradeItems());
		LOGGER.debug(dataset);
		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", dataset, optionsVar));

	}

}
