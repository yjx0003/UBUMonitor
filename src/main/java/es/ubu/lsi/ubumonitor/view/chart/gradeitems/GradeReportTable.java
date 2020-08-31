package es.ubu.lsi.ubumonitor.view.chart.gradeitems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabulator;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;

public class GradeReportTable extends Tabulator {
	private static final Logger LOGGER = LoggerFactory.getLogger(GradeReportTable.class);
	private TreeView<GradeItem> treeViewGradeItem;

	public GradeReportTable(MainController mainController, TreeView<GradeItem> treeViewGradeItem, WebView webView) {
		super(mainController, ChartType.GRADE_REPORT_TABLE, webView);
		this.treeViewGradeItem = treeViewGradeItem;
		useGeneralButton = true;
		useGroupButton = true;

	}

	@Override
	public void update() {
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		String columns = createColumns(gradeItems);
		String tableData = createData(enrolledUsers, gradeItems);
		JSObject data = new JSObject();
		data.put("columns", columns);
		data.put("tabledata", tableData);
		LOGGER.debug("Usuarios seleccionados:{}", enrolledUsers);
		LOGGER.debug("Columnas:{}", columns);
		LOGGER.debug("Datos de tabla:{}", data);
		webViewChartsEngine.executeScript(String.format("updateTabulator(%s, %s)", data, getOptions()));

	}

	public String createColumns(List<GradeItem> gradeItems) {

		// if type is selected user or stats
		JSArray array = new JSArray();
		JSObject jsObject = new JSObject();
		jsObject.putWithQuote("title", "Type");
		jsObject.putWithQuote("field", "type");
		jsObject.put("visible", false);

		// users columns
		jsObject = new JSObject();
		jsObject.putWithQuote("title", I18n.get("chartlabel.name"));
		jsObject.putWithQuote("field", "name");
		jsObject.putWithQuote("frozen", "true");
		array.add(jsObject.toString());
		String progressParams = getProgressParam();
		// grade items columns
		for (GradeItem gradeItem : gradeItems) {
			jsObject = new JSObject();
			jsObject.put("formatter", "'progress'");
			jsObject.put("formatterParams", progressParams);
			jsObject.put("sorter", "'number'");
			jsObject.putWithQuote("title", gradeItem.getItemname());
			jsObject.putWithQuote("field", "ID" + gradeItem.getId());

			array.add(jsObject.toString());
		}
		return array.toString();
	}

	public String createData(List<EnrolledUser> enrolledUsers, List<GradeItem> gradeItems) {
		JSArray array = new JSArray();
		JSObject jsObject;
		String stringSelectedUsers = I18n.get("text.selectedUsers");
		for (EnrolledUser enrolledUser : enrolledUsers) {
			jsObject = new JSObject();
			jsObject.putWithQuote("name", enrolledUser.getFullName());
			jsObject.putWithQuote("type", stringSelectedUsers);
			for (GradeItem gradeItem : gradeItems) {
				double grade = adjustTo10(gradeItem.getEnrolledUserPercentage(enrolledUser));
				jsObject.put("ID" + gradeItem.getId(), Double.isNaN(grade) ? -1 : grade);
			}
			array.add(jsObject.toString());
		}
		if (useGeneralButton && (boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive")) {

			array.add(addStats(gradeItems, I18n.get("chartlabel.generalMean"), stats.getGeneralStats()));
		}
		if (useGroupButton && (boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive")) {
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
		JSObject jsObject = new JSObject();

		jsObject.putWithQuote("name", name);
		jsObject.putWithQuote("type", I18n.get("text.stats"));
		for (GradeItem gradeItem : gradeItems) {
			jsObject.put("ID" + gradeItem.getId(), adjustTo10(stats.get(gradeItem)
					.getMean()));
		}
		return jsObject.toString();
	}

	@Override
	public String getOptions(JSObject jsObject) {

		jsObject.put("invalidOptionWarnings", false);
		jsObject.put("height", "height");
		jsObject.put("tooltipsHeader", true);
		jsObject.putWithQuote("groupBy", "type");
		jsObject.put("virtualDom", true);
		jsObject.putWithQuote("layout", "fitColumns");
		jsObject.put("rowClick", "function(e,row){javaConnector.dataPointSelection(row.getPosition());}");
		return jsObject.toString();
	}

	private String getProgressParam() {
		
		JSObject jsObject = new JSObject();
		jsObject.put("min", 0.0);
		jsObject.put("max", 10);
		jsObject.put("legend", "function(n){return 0==n?'0':n>0?n:void 0}");
		jsObject.putWithQuote("legendAlign", "center");

		jsObject.put("color",
				"function(e){return e<" + mainConfiguration.getValue(MainConfiguration.GENERAL, "cutGrade") + "?"
						+ colorToRGB(mainConfiguration.getValue(getChartType(), "failGradeColor")) + ":"
						+ colorToRGB(mainConfiguration.getValue(getChartType(), "passGradeColor")) + "}");
		return jsObject.toString();
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<String> header = new ArrayList<>();
		header.add("userid");
		header.add("fullname");
		List<GradeItem> gradeItems = getSelectedGradeItems(treeViewGradeItem);
		for (GradeItem gradeItem : gradeItems) {
			header.add(gradeItem.getItemname());
		}

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader(header.toArray(new String[0])))) {
			for (EnrolledUser enrolledUser : getSelectedEnrolledUser()) {
				printer.print(enrolledUser.getId());
				printer.print(enrolledUser.getFullName());
				for (GradeItem gradeItem : gradeItems) {
					printer.print(adjustTo10(gradeItem.getEnrolledUserPercentage(enrolledUser)));
				}
				printer.println();
			}
		}

	}

}
