package controllers.charts;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.I18n;
import controllers.MainController;
import model.EnrolledUser;
import model.GradeItem;
import model.Group;
import util.UtilMethods;

public class GradeReportTable extends Tabulator {
	private static final Logger LOGGER = LoggerFactory.getLogger(GradeReportTable.class);

	public GradeReportTable(MainController mainController) {
		super(mainController, ChartType.GRADE_REPORT_TABLE);
		useGeneralButton = true;
		useGroupButton = true;
		useLegend = false;
		optionsVar = "gradeReportTableOptions";
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
		webViewChartsEngine.executeScript(String.format("updateTabulator(%s, %s, %s)", columns, data, optionsVar));

	}

	public String createColumns(List<GradeItem> gradeItems) {

		//if type is selected user or stats
		StringJoiner array = JSArray();
		StringJoiner jsObject = JSObject();
		addKeyValueWithQuote(jsObject, "title", "Type");
		addKeyValueWithQuote(jsObject, "field", "type");
		jsObject.add("visible:false");
		
		//users columns
		jsObject = JSObject();
		addKeyValueWithQuote(jsObject, "title", I18n.get("chartlabel.name"));
		addKeyValueWithQuote(jsObject, "field", "name");
		addKeyValueWithQuote(jsObject, "frozen", "true");
		array.add(jsObject.toString());
		
		//grade items columns
		for (GradeItem gradeItem : gradeItems) {
			jsObject = JSObject();
			jsObject.add("formatter:'progress'");
			jsObject.add("formatterParams:tabulatorProgressParams");
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
		if (useGeneralButton && Buttons.getInstance().getShowMean()) {
			array.add(addStats(gradeItems, I18n.get("chartlabel.generalMean"), stats.getGeneralStats()));
		}
		if (useGroupButton && Buttons.getInstance().getShowGroupMean()) {
			for (Group group : slcGroup.getItems()) {
				if (group != null) {
					array.add(addStats(gradeItems, UtilMethods.escapeJavaScriptText(I18n.get("chart.mean")) + " " + group.getGroupName(),
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


}
