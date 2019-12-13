package controllers.charts;

import controllers.JavaConnector.ChartType;
import model.EnrolledUser;
import model.GradeItem;
import model.Stats;
import util.UtilMethods;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.I18n;
import controllers.MainController;

public abstract class Chartjs extends Chart{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Chartjs.class);

	
	
	public Chartjs(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
	}
	
	@Override
	public void clear() {
		webViewChartsEngine.executeScript("clearChartjs()");

	}

	@Override
	public void hideLegend() {
		webViewChartsEngine.executeScript("hideLegendChartjs()");
		
	}
	
	@Override
	public String export() {
		return (String) webViewChartsEngine.executeScript("exportChartjs()");
	}
	
	public String createDataset(List<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems, Stats stats, boolean withMean, boolean withGroupMean) {
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
		if (withMean) {
			stringBuilder.append("{label:'" + UtilMethods.escapeJavaScriptText(I18n.get("chartlabel.generalMean")) + "',");
			stringBuilder.append("borderColor:" + hex(I18n.get("chartlabel.generalMean")) + ",");
			stringBuilder.append("backgroundColor:" + rgba(I18n.get("chartlabel.generalMean"), OPACITY) + ",");
			stringBuilder.append("hidden: "+!Buttons.getInstance().getShowMean()+ ",");
			stringBuilder.append("data:[");
			Map<GradeItem, DescriptiveStatistics> descriptiveStats = stats.getGeneralStats();
			for (GradeItem gradeItem : selectedGradeItems) {
				double grade = descriptiveStats.get(gradeItem).getMean();
				stringBuilder.append(adjustTo10(grade) + ",");
			}
			stringBuilder.append("]},");
		}
		
		if (withGroupMean && slcGroup.getValue() != null) {
			stringBuilder.append("{label:'" + UtilMethods.escapeJavaScriptText(I18n.get("chartlabel.groupMean")) + "',");
			stringBuilder.append("borderColor:" + hex(I18n.get("chartlabel.groupMean")) + ",");
			stringBuilder.append("backgroundColor:" + rgba(I18n.get("chartlabel.groupMean"), OPACITY) + ",");
			stringBuilder.append("hidden: "+!Buttons.getInstance().getShowGroupMean()+ ",");
			stringBuilder.append("data:[");
			Map<GradeItem, DescriptiveStatistics> descriptiveStats = stats.getGroupStats(slcGroup.getValue());
			for (GradeItem gradeItem : selectedGradeItems) {
				double grade = descriptiveStats.get(gradeItem).getMean();
				stringBuilder.append(adjustTo10(grade) + ",");
			}
			stringBuilder.append("]}");
		}
		
		stringBuilder.append("]}");
		LOGGER.debug(stringBuilder.toString());
		return stringBuilder.toString();

	}
	
	public double adjustTo10(double value) {
		return Double.isNaN(value) ? value : Math.round(value*10)  / 100.0;
	}
	


}
