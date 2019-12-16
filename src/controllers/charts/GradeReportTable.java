package controllers.charts;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import controllers.I18n;
import controllers.MainController;
import model.EnrolledUser;
import model.GradeItem;
import model.Group;
import util.UtilMethods;

public class GradeReportTable extends Tabulator {

	public GradeReportTable(MainController mainController) {
		super(mainController, ChartType.GRADE_REPORT_TABLE);
		useGeneralButton = false;
		useGroupButton = false;
		useLegend = false;
		optionsVar = "gradeReportTableOptions";
	}

	@Override
	public void update() {
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems();
		String columns = createColumns(gradeItems);
		String data = createData(enrolledUsers, gradeItems);
		System.out.println(columns);
		System.out.println(data);
		webViewChartsEngine.executeScript(String.format("updateTabulator(%s, %s, %s)", columns, data, optionsVar));
	}

	public String createColumns(List<GradeItem> gradeItems) {

		StringJoiner array = JSArray();
		StringJoiner jsObject = JSObject();

		addKeyValue(jsObject, "title", I18n.get("chartlabel.name"));
		addKeyValue(jsObject, "field", "name");
		array.add(jsObject.toString());
		for (GradeItem gradeItem : gradeItems) {
			jsObject = JSObject();
			jsObject.add("formatter:'progress'");
			jsObject.add("formatterParams:tabulatorProgressParams");
			jsObject.add("sorter:'number'");
			addKeyValue(jsObject, "title", gradeItem.getItemname());
			addKeyValue(jsObject, "field", "ID"+gradeItem.getId());

			array.add(jsObject.toString());
		}
		return array.toString();
	}

	public String createData(List<EnrolledUser> enrolledUsers, List<GradeItem> gradeItems) {
		StringJoiner array = JSArray();
		StringJoiner jsObject;
		for (EnrolledUser enrolledUser : enrolledUsers) {
			jsObject = JSObject();
			addKeyValue(jsObject, "name", enrolledUser.getFullName());
			for (GradeItem gradeItem : gradeItems) {
				addKeyValue(jsObject, "ID"+gradeItem.getId(), adjustTo10(gradeItem.getEnrolledUserPercentage(enrolledUser)));
			}
			array.add(jsObject.toString());
		}
		array.add(addStats(gradeItems, I18n.get("chartlabel.generalMean"), stats.getGeneralStats()));
		
		for (Group group:slcGroup.getItems()) {
			if(group!=null) {
				array.add(addStats(gradeItems, group.getGroupName(), stats.getGroupStats(group)));
				
			}
		}

		return array.toString();
	}

	private String addStats(List<GradeItem> gradeItems, String name, Map<GradeItem, DescriptiveStatistics> stats) {
		StringJoiner jsObject = JSObject();
		
		addKeyValue(jsObject, "name", name);
		for (GradeItem gradeItem : gradeItems) {
			addKeyValue(jsObject, "ID"+gradeItem.getId(), stats.get(gradeItem).getMean());
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
