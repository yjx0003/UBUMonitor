package es.ubu.lsi.ubumonitor.controllers.charts;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;

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
		int borderLength = mainConfiguration.getValue(MainConfiguration.GENERAL, "borderLength");
		int borderSpace = mainConfiguration.getValue(MainConfiguration.GENERAL, "borderSpace");
		JSObject data = new JSObject();

		data.put("labels", "[" + UtilMethods.joinWithQuotes(selectedGradeItems) + "]");
		JSArray datasets = new JSArray();

		for (EnrolledUser user : selectedUser) {
			JSObject dataset = new JSObject();
			dataset.putWithQuote("label", user.getFullName());
			dataset.put("borderColor", hex(user.getId()));
			dataset.put("backgrounColor", rgba(user.getId(), OPACITY));
			JSArray dataArray = new JSArray();
			for (GradeItem gradeItem : selectedGradeItems) {
				double grade = gradeItem.getEnrolledUserPercentage(user);

				dataArray.add(adjustTo10(grade));

			}
			dataset.put("data", dataArray);
			datasets.add(dataset);
		}
		
		if (useGeneralButton) {
			JSObject dataset = new JSObject();
			dataset.putWithQuote("label", I18n.get("chartlabel.generalMean"));
			dataset.put("borderColor", hex(I18n.get("chartlabel.generalMean")));
			dataset.put("backgroundColor", rgba(I18n.get("chartlabel.generalMean"), OPACITY));
			dataset.put("hidden", !(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive"));
			dataset.put("borderDash", "["+ borderLength + "," + borderSpace +"]");
			
			JSArray dataArray = new JSArray();
			Map<GradeItem, DescriptiveStatistics> descriptiveStats = stats.getGeneralStats();
			for (GradeItem gradeItem : selectedGradeItems) {
				double grade = descriptiveStats.get(gradeItem).getMean();
				dataArray.add(adjustTo10(grade));
			}
			dataset.put("data", dataArray);
			datasets.add(dataset);
		}

		if (useGroupButton) {
			for (Group group : slcGroup.getCheckModel().getCheckedItems()) {
				if (group == null)
					continue;
				JSObject dataset = new JSObject();
				dataset.putWithQuote("label", I18n.get("chart.mean") + " " + group.getGroupName());
			
				dataset.put("borderColor", hex(I18n.get("chartlabel.generalMean")));
				dataset.put("backgroundColor", rgba(I18n.get("chartlabel.generalMean"), OPACITY));
				dataset.put("hidden", !(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL,"groupActive"));
				dataset.put("borderDash", "["+ borderLength + "," + borderSpace +"]");
				JSArray dataArray = new JSArray();
				Map<GradeItem, DescriptiveStatistics> descriptiveStats = stats.getGroupStats(group);
				for (GradeItem gradeItem : selectedGradeItems) {
					double grade = descriptiveStats.get(gradeItem).getMean();
					dataArray.add(adjustTo10(grade));
				}
				datasets.add(dataset);
			}

		}

		data.put("datasets", datasets);

		return data.toString();

	}

	@Override
	public void update() {
		String dataset = createDataset(getSelectedEnrolledUser(), getSelectedGradeItems());
		String options = getOptions();
		LOGGER.debug(dataset);
		LOGGER.debug(options);
		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", dataset, options));

	}

}
