package es.ubu.lsi.ubumonitor.controllers.charts;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.scene.paint.Color;

public class CalificationBar extends ChartjsGradeItem {

	public CalificationBar(MainController mainController) {
		super(mainController, ChartType.CALIFICATION_BAR);
		useGeneralButton = false;
		useGroupButton = false;
	}

	@Override
	public String createDataset(List<EnrolledUser> selectedUser, List<GradeItem> selectedGradeItems) {

		StringJoiner data = JSObject();
		addKeyValue(data, "labels", "[" + UtilMethods.joinWithQuotes(selectedGradeItems) + "]");

		List<Integer> countNaN = new ArrayList<>();
		List<Integer> countLessCut = new ArrayList<>();
		List<Integer> countGreaterCut = new ArrayList<>();
		double cutGrade = Controller.getInstance().getMainConfiguration().getValue(MainConfiguration.GENERAL,
				"cutGrade");
		for (GradeItem gradeItem : selectedGradeItems) {
			int nan = 0;
			int less = 0;
			int greater = 0;
			for (EnrolledUser user : selectedUser) {
				double grade = adjustTo10(gradeItem.getEnrolledUserPercentage(user));
				if (Double.isNaN(grade)) {
					++nan;
				} else if (grade < cutGrade) {
					++less;
				} else {
					++greater;
				}
			}
			countNaN.add(nan);
			countLessCut.add(less);
			countGreaterCut.add(greater);
		}
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		StringJoiner datasets = JSArray();
		datasets.add(createData(I18n.get("text.empty"), countNaN,
				mainConfiguration.getValue(getChartType(), "emptyGradeColor")));
		datasets.add(createData(I18n.get("text.fail"), countLessCut,
				mainConfiguration.getValue(getChartType(), "failGradeColor")));
		datasets.add(createData(I18n.get("text.pass"), countGreaterCut,
				mainConfiguration.getValue(getChartType(), "passGradeColor")));
		addKeyValue(data, "datasets", datasets.toString());
		return data.toString();
	}

	private String createData(String label, List<Integer> data, Color color) {
		StringJoiner dataset = JSObject();
		addKeyValueWithQuote(dataset, "label", label);
		addKeyValue(dataset, "data", "[" + UtilMethods.join(data) + "]");
		addKeyValue(dataset, "backgroundColor", colorToRGB(color));
		// addKeyValueWithQuote(dataset, "borderColor", hexColor);
		// addKeyValue(dataset, "borderWidth", 2);
		return dataset.toString();
	}

	@Override
	public String getOptions() {
		StringJoiner jsObject = getDefaultOptions();
		addKeyValueWithQuote(jsObject, "typeGraph", "bar");
		addKeyValue(jsObject, "scales", "{xAxes:[{" + getXScaleLabel() + ",stacked: true}],yAxes:[{" + getYScaleLabel()
				+ ",stacked:true,ticks:{stepSize:0}}]}");
		addKeyValue(jsObject, "tooltips", "{mode:'label'}");
		addKeyValue(jsObject, "onClick", "function(event, array){}");
		addKeyValue(jsObject, "plugins",
				"{datalabels:{display:!0,font:{weight:\"bold\"},formatter:function(t,a){if(0===t)return\"\";let e=a.chart.data.datasets,l=0;for(i=0;i<e.length;i++)l+=e[i].data[a.dataIndex];return t+\"/\"+l+\" (\"+(t/l).toLocaleString(locale,{style:\"percent\",maximumFractionDigits:2})+\")\"}}}");
		return jsObject.toString();
	}

}
