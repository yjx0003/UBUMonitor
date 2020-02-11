package es.ubu.lsi.controllers.charts;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.controllers.Controller;
import es.ubu.lsi.controllers.I18n;
import es.ubu.lsi.controllers.MainController;
import es.ubu.lsi.controllers.configuration.MainConfiguration;
import es.ubu.lsi.model.EnrolledUser;
import es.ubu.lsi.model.GradeItem;
import es.ubu.lsi.model.Group;
import es.ubu.lsi.util.UtilMethods;

public abstract class ChartjsGradeItem extends Chartjs {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartjsGradeItem.class);

	public ChartjsGradeItem(MainController mainController, ChartType chartType) {
		super(mainController, chartType, Tabs.GRADES);
		useGeneralButton = true;
		useLegend = true;
		useGroupButton = true;
	}

	public String createDataset(List<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems) {
		
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		int borderLength =  mainConfiguration.getValue(MainConfiguration.GENERAL, "borderLength");
		int borderSpace = mainConfiguration.getValue(MainConfiguration.GENERAL, "borderSpace");
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
			stringBuilder.append("hidden: " + !(boolean)mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive") + ",");
			stringBuilder.append(
					"borderDash:[" + borderLength + "," + borderSpace + "],");
			stringBuilder.append("data:[");
			Map<GradeItem, DescriptiveStatistics> descriptiveStats = stats.getGeneralStats();
			for (GradeItem gradeItem : selectedGradeItems) {
				double grade = descriptiveStats.get(gradeItem).getMean();
				stringBuilder.append(adjustTo10(grade) + ",");
			}
			stringBuilder.append("]},");
		}

		if (useGroupButton) {
			for (Group group : slcGroup.getCheckModel().getCheckedItems()) {
				if (group == null)
					continue;
				stringBuilder.append("{label:'" + UtilMethods
						.escapeJavaScriptText(I18n.get("chart.mean") + " " + group.getGroupName()) + "',");
				stringBuilder.append("borderColor:" + hex(group.getGroupId()) + ",");
				stringBuilder.append("backgroundColor:" + rgba(group.getGroupId(), OPACITY) + ",");
				stringBuilder.append("hidden: " + !(boolean)mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive") + ",");
				stringBuilder.append("borderDash:[" + borderLength + ","
						+ borderSpace + "],");
				stringBuilder.append("data:[");
				Map<GradeItem, DescriptiveStatistics> descriptiveStats = stats.getGroupStats(group);
				for (GradeItem gradeItem : selectedGradeItems) {
					double grade = descriptiveStats.get(gradeItem).getMean();
					stringBuilder.append(adjustTo10(grade) + ",");
				}
				stringBuilder.append("]}");
			}

		}

		stringBuilder.append("]}");

		return stringBuilder.toString();

	}

	@Override
	public void update() {
		String dataset = createDataset(getSelectedEnrolledUser(), getSelectedGradeItems());
		String options =  getOptions();
		LOGGER.debug(dataset);
		LOGGER.debug(options);
		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", dataset, options));

	}

}
