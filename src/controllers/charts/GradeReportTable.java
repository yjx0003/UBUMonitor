package controllers.charts;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import controllers.I18n;
import controllers.MainController;
import controllers.configuration.MainConfiguration;
import model.EnrolledUser;
import model.GradeItem;
import model.Group;
import util.UtilMethods;

public class GradeReportTable extends Tabulator {
	private static final Logger LOGGER = LoggerFactory.getLogger(GradeReportTable.class);

	public GradeReportTable(MainController mainController) {
		super(mainController, ChartType.GRADE_REPORT_TABLE, Tabs.GRADES);
		useGeneralButton = true;
		useGroupButton = true;

	}

	@Override
	public void update() {
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems();
		String columns = createColumns(gradeItems);
		String data = createData(enrolledUsers, gradeItems);
		LOGGER.debug("Usuarios seleccionados:{}", enrolledUsers);
		LOGGER.debug("Columnas:{}", columns);
		LOGGER.debug("Datos de tabla:{}", data);
		webViewChartsEngine.executeScript(String.format("updateTabulator(%s, %s, %s)", columns, data, getOptions()));

	}

	public String createColumns(List<GradeItem> gradeItems) {

		// if type is selected user or stats
		StringJoiner array = JSArray();
		StringJoiner jsObject = JSObject();
		addKeyValueWithQuote(jsObject, "title", "Type");
		addKeyValueWithQuote(jsObject, "field", "type");
		jsObject.add("visible:false");

		// users columns
		jsObject = JSObject();
		addKeyValueWithQuote(jsObject, "title", I18n.get("chartlabel.name"));
		addKeyValueWithQuote(jsObject, "field", "name");
		addKeyValueWithQuote(jsObject, "frozen", "true");
		array.add(jsObject.toString());
		String progressParams = getProgressParam();
		// grade items columns
		for (GradeItem gradeItem : gradeItems) {
			jsObject = JSObject();
			jsObject.add("formatter:'progress'");
			addKeyValue(jsObject, "formatterParams", progressParams);
			jsObject.add("sorter:'number'");
			addKeyValueWithQuote(jsObject, "title", gradeItem.getItemname());
			addKeyValueWithQuote(jsObject, "field", "ID" + gradeItem.getId());

			array.add(jsObject.toString());
		}
		return array.toString();
	}

	public String createData(List<EnrolledUser> enrolledUsers, List<GradeItem> gradeItems) {
		StringJoiner array = JSArray();
		StringJoiner jsObject;
		String stringSelectedUsers = I18n.get("text.selectedUsers");
		for (EnrolledUser enrolledUser : enrolledUsers) {
			jsObject = JSObject();
			addKeyValueWithQuote(jsObject, "name", enrolledUser.getFullName());
			addKeyValueWithQuote(jsObject, "type", stringSelectedUsers);
			for (GradeItem gradeItem : gradeItems) {
				addKeyValue(jsObject, "ID" + gradeItem.getId(),
						adjustTo10(gradeItem.getEnrolledUserPercentage(enrolledUser)));
			}
			array.add(jsObject.toString());
		}
		if (useGeneralButton && (boolean)Controller.getInstance().getMainConfiguration().getValue("General", "generalActive")) {
			array.add(addStats(gradeItems, I18n.get("chartlabel.generalMean"), stats.getGeneralStats()));
		}
		if (useGroupButton && (boolean)Controller.getInstance().getMainConfiguration().getValue("General", "groupActive")) {
			for (Group group : slcGroup.getItems()) {
				if (group != null) {
					array.add(addStats(gradeItems,
							UtilMethods.escapeJavaScriptText(I18n.get("chart.mean")) + " " + group.getGroupName(),
							stats.getGroupStats(group)));

				}
			}
		}

		return array.toString();
	}

	private String addStats(List<GradeItem> gradeItems, String name, Map<GradeItem, DescriptiveStatistics> stats) {
		StringJoiner jsObject = JSObject();

		addKeyValueWithQuote(jsObject, "name", name);
		addKeyValueWithQuote(jsObject, "type", I18n.get("text.stats"));
		for (GradeItem gradeItem : gradeItems) {
			addKeyValue(jsObject, "ID" + gradeItem.getId(), adjustTo10(stats.get(gradeItem).getMean()));
		}
		return jsObject.toString();
	}

	@Override
	public String getOptions() {
//		 var gradeReportTableOptions = {
//
//			        tab: "grade_tabs",
//			        button: "GRADE_REPORT_TABLE",
//			        invalidOptionWarnings: false,
//			        height: height,
//			        groupBy: "type",
//			        //placeholder: "No data",
//			        virtualDom: true,
//			        tooltipsHeader: true,
//			        layout: "fitColumns", //fit columns to width of table (optional)
//			        rowClick: function (e, row) {
//			            javaConnector.dataPointSelection(row.getPosition());
//
//			        },
//			    }

		StringJoiner jsObject = getDefaultOptions();
		addKeyValue(jsObject, "invalidOptionWarnings", false);
		addKeyValue(jsObject, "height", "height");
		addKeyValue(jsObject, "tooltipsHeader", true);
		addKeyValueWithQuote(jsObject, "groupBy", "type");
		addKeyValue(jsObject, "virtualDom", true);
		addKeyValueWithQuote(jsObject, "layout", "fitColumns");
		addKeyValue(jsObject, "rowClick", "function(e,row){javaConnector.dataPointSelection(row.getPosition());}");
		return jsObject.toString();
	}

	private String getProgressParam() {
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		StringJoiner jsObject = JSObject();
		addKeyValue(jsObject, "min", 0);
		addKeyValue(jsObject, "max", 10);
		addKeyValue(jsObject, "legend", true);
		addKeyValueWithQuote(jsObject, "legendAlign", "center");
		
		addKeyValue(jsObject, "color",
				"function(e){return e<" + mainConfiguration.getValue("General", "cutGrade") + "?"
						+ colorToRGB(mainConfiguration.getValue(getChartType(), "failGradeColor")) + ":"
						+ colorToRGB(mainConfiguration.getValue(getChartType(), "passGradeColor")) + "}");
		return jsObject.toString();
	}

}
