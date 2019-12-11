package controllers.charts;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.I18n;
import controllers.JavaConnector.ChartType;
import controllers.MainController;
import javafx.scene.control.TreeItem;
import model.EnrolledUser;
import model.GradeItem;
import model.Stats;
import util.UtilMethods;
/**
 * Bugged, apexchartd has bugs in line
 * @author Yi Peng Ji
 *
 */
public class LineApexchart extends ApexCharts {
	private static final Logger LOGGER = LoggerFactory.getLogger(LineApexchart.class);
	public LineApexchart(MainController mainController) {
		super(mainController, ChartType.LINE, "lineOptions");
	}

	@Override
	public void update() {
		
		List<GradeItem> gradeItems = tvwGradeReport.getSelectionModel().getSelectedItems()
		           .stream()
		           .map(TreeItem::getValue)
		           .collect(Collectors.toList());
		
		String series = createSeries(getSelectedEnrolledUser(), gradeItems , stats);
		
		String categories = createCategories(gradeItems);
		
		
		LOGGER.debug(series);
		LOGGER.debug(categories);
		
		webViewChartsEngine.executeScript(String.format("updateLine(%s,%s)", categories, series));
		

	}
	/**
 	 *	[
	 *	    {
	 *       name: "Series 1",
	 *       data: [45, 52, 38, 24, 33, 26, 21, 20, 6, 8, 15, 10]
	 *	    }
	 *	]
	 *
	 * @param <T>
	 * @param selectedUsers
	 * @param gradeItems
	 * @param stats
	 * @return
	 */
	public <T> String createSeries(List<EnrolledUser> selectedUsers, List<GradeItem> gradeItems, Stats stats) {

		StringBuilder stringBuilder = new StringBuilder();


		Map<GradeItem, DescriptiveStatistics> descriptiveStats = stats.getGeneralStats();
		stringBuilder.append("[");

		for (EnrolledUser selectedUser : selectedUsers) {
			stringBuilder.append("{name:'" + UtilMethods.escapeJavaScriptText(selectedUser.getFullName()) + "',data:[");

			for (GradeItem gradeItem : gradeItems) {
				double grade = gradeItem.getEnrolledUserPercentage(selectedUser);
				stringBuilder.append(grade == Double.NaN ? null : Math.round(grade * 10) / 100.0 + ",");
				

			}	
			stringBuilder.append("]},");
			
		}
		
		
		stringBuilder.append("{name:'" + I18n.get("chartlabel.generalMean") + "',data:[");
		for (GradeItem gradeItem : gradeItems) {
			String meanGrade = stats.getElementMean(descriptiveStats, gradeItem);
			stringBuilder.append(meanGrade+",");
		}	
		stringBuilder.append("]},");
		stringBuilder.append("]");
		return stringBuilder.toString();

	}
	
	public String createCategories(List<GradeItem> gradeItems) {
		
		String names = UtilMethods.joinWithQuotes(gradeItems);
		return "["+ names +"]";
		
	}

}
