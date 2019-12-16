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

		StringJoiner array = JSArray();
		StringJoiner jsObject = JSObject();
		addKeyValue(jsObject, "title", "Type");
		addKeyValue(jsObject, "field", "type");
		jsObject.add("visible:false");
		jsObject = JSObject();
		addKeyValue(jsObject, "title", I18n.get("chartlabel.name"));
		addKeyValue(jsObject, "field", "name");
		addKeyValue(jsObject, "frozen", "true");
		array.add(jsObject.toString());
		for (GradeItem gradeItem : gradeItems) {
			jsObject = JSObject();
			jsObject.add("formatter:'progress'");
			jsObject.add("formatterParams:tabulatorProgressParams");
			jsObject.add("sorter:'number'");
			addKeyValue(jsObject, "title", gradeItem.getItemname());
			addKeyValue(jsObject, "field", "ID" + gradeItem.getId());

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
			addKeyValue(jsObject, "name", enrolledUser.getFullName());
			addKeyValue(jsObject, "type", stringSelectedUsers);
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
					array.add(addStats(gradeItems, I18n.get("chartlabel.groupMean") + " " + group.getGroupName(),
							stats.getGroupStats(group)));

				}
			}
		}

		return array.toString();
	}

	private String addStats(List<GradeItem> gradeItems, String name, Map<GradeItem, DescriptiveStatistics> stats) {
		StringJoiner jsObject = JSObject();

		addKeyValue(jsObject, "name", name);
		addKeyValue(jsObject, "type", "Stats");
		for (GradeItem gradeItem : gradeItems) {
			addKeyValue(jsObject, "ID" + gradeItem.getId(), adjustTo10(stats.get(gradeItem).getMean()));
		}
		return jsObject.toString();
	}

	public StringJoiner JSArray() {
		return new StringJoiner(",", "[", "]");
	}

	public StringJoiner JSObject() {
		return new StringJoiner(",", "{", "}");
	}

	public <T> void addKeyValue(StringJoiner jsObject, T key, int value) {
		jsObject.add(key + ":" + value);
	}

	public <T> void addKeyValue(StringJoiner jsObject, T key, double value) {
		jsObject.add(key + ":" + value);
	}

	public <T> void addKeyValue(StringJoiner jsObject, T key, String value) {
		jsObject.add(key + ":'" + UtilMethods.escapeJavaScriptText(value.toString()) + "'");
	}
}
