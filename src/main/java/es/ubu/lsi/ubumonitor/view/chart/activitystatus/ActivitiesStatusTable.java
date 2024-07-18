package es.ubu.lsi.ubumonitor.view.chart.activitystatus;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion.State;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.DateTimeWrapper;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabulator;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

public class ActivitiesStatusTable extends Tabulator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActivitiesStatusTable.class);
	private DateTimeWrapper dateTimeWrapper;
	private DatePicker datePickerStart;
	private DatePicker datePickerEnd;
	private ListView<CourseModule> listViewActivity;

	public ActivitiesStatusTable(MainController mainController, DatePicker datePickerStart, DatePicker datePickerEnd,
			ListView<CourseModule> listViewActivity) {
		super(mainController, ChartType.ACTIVITIES_TABLE);
		this.datePickerStart = datePickerStart;
		this.datePickerEnd = datePickerEnd;
		this.listViewActivity = listViewActivity;
		dateTimeWrapper = new DateTimeWrapper();

		useRangeDate = true;
	}

	public String createColumns(List<CourseModule> courseModules) {
		// users columns
		JSObject jsObject = new JSObject();
		JSArray array = new JSArray();

		jsObject.putWithQuote("title", I18n.get("chartlabel.name"));
		jsObject.put("tooltip", true);
		jsObject.put("field", "'name'");
		jsObject.put("frozen", true);
		array.add(jsObject);

		jsObject = new JSObject();
		jsObject.putWithQuote("title", I18n.get("chartlabel.progress"));
		jsObject.putWithQuote("field", "progress");
		jsObject.putWithQuote("formatter", "progress");
		jsObject.put("width", 100);
		jsObject.putWithQuote("frozen", true);
		jsObject.put("formatterParams", getProgressParam(courseModules.size()));
		array.add(jsObject.toString());

		// formatter de la imagen
		JSObject formatterParams = new JSObject();
		formatterParams.put("heigh", 16);
		formatterParams.put("width", 16);
		formatterParams.put("urlPrefix", "''");
		formatterParams.put("urlSuffix", "''");

		String stringFormatterParams = formatterParams.toString();

		for (CourseModule courseModule : courseModules) {
			// commit #132 when the user unselect options a null value is included in the list...
			if (courseModule != null) { // FIX BUG RMS
				jsObject = new JSObject();
				jsObject.putWithQuote("hozAlign", "center");
				jsObject.put("tooltip",
						"function(c){return c.getRow().getData().datetime" + courseModule.getCmid() + "}");

				jsObject.putWithQuote("formatter", "image");
				jsObject.put("topCalc",
						"function(n,r,c){var f=0;return n.forEach(function(n){n&&f++;}),f+'/'+n.length+' ('+(f/n.length||0).toLocaleString(locale,{style:'percent',maximumFractionDigits:2})+')';}");
				jsObject.put("formatterParams", stringFormatterParams);
				jsObject.put("sorter", "function(t,a,n,e,i,g,r){return t-a||(n.getData().instant"
						+ courseModule.getCmid() + "||0)-(e.getData().instant" + courseModule.getCmid() + "||0)}");
				jsObject.putWithQuote("title", courseModule.getModuleName());
				jsObject.putWithQuote("field", "ID" + courseModule.getCmid());

				array.add(jsObject.toString());
			}
		}

		return array.toString();
	}

	private String getProgressParam(int max) {
		JSObject jsObject = new JSObject();
		jsObject.put("min", 0);
		jsObject.put("max", max);

		jsObject.put("legend", String
				.format("function(value){return value+'/'+%s +' ('+Math.round(value/%s*100||0)+'%%)';}", max, max));

		jsObject.putWithQuote("hozAlign", "center");
		JSArray jsArray = new JSArray();

		jsArray.add(colorToRGB(getConfigValue("firstInterval")));
		jsArray.add(colorToRGB(getConfigValue("secondInterval")));
		jsArray.add(colorToRGB(getConfigValue("thirdInterval")));
		jsArray.add(colorToRGB(getConfigValue("fourthInterval")));
		jsObject.put("color", jsArray);
		return jsObject.toString();
	}

	public String createData(List<EnrolledUser> enrolledUsers, List<CourseModule> courseModules) {
		JSArray array = new JSArray();
		JSObject jsObject;
		Instant init = datePickerStart.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant();
		Instant end = datePickerEnd.getValue().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
		for (EnrolledUser enrolledUser : enrolledUsers) {
			jsObject = new JSObject();
			jsObject.putWithQuote("name", enrolledUser.getFullName());
			int progress = 0;

			for (CourseModule courseModule : courseModules) {

				ActivityCompletion activity = courseModule.getActivitiesCompletion().get(enrolledUser);

				String field = "ID" + courseModule.getCmid();

				if (activity != null) {

					String activityTracking = activity.getTracking() == null ? "0"
							: String.valueOf(activity.getTracking().ordinal());
					Instant timeCompleted = activity.getTimecompleted();
					if (timeCompleted != null && init.isBefore(timeCompleted) && end.isAfter(timeCompleted)) {
						progress++;
						String activityState = activity.getState() == null ? "0"
								: String.valueOf(activity.getState().ordinal());
						String overrideBy = activity.getOverrideby() == null ? "0" : "1";
						jsObject.put(field,
								"activityCompletionIcons[" + activityState + activityTracking + overrideBy + "]");
						jsObject.putWithQuote("datetime" + courseModule.getCmid(),
								dateTimeWrapper.format(timeCompleted));
						jsObject.put("instant" + courseModule.getCmid(), timeCompleted.getEpochSecond());
					} else {
						jsObject.put(field, "activityCompletionIcons[000]");
					}

				}

			}

			jsObject.put("progress", progress);
			array.add(jsObject.toString());
		}
		return array.toString();
	}

	@Override
	public void update() {
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();

		List<CourseModule> courseModules = listViewActivity.getSelectionModel().getSelectedItems();
		String columns = createColumns(courseModules);
		String tableData = createData(enrolledUsers, courseModules);
		JSObject data = new JSObject();
		data.put("columns", columns);
		data.put("tabledata", tableData);
		LOGGER.debug("Usuarios seleccionados:{}", enrolledUsers);
		LOGGER.debug("Columnas:{}", columns);
		LOGGER.debug("Datos de tabla:{}", data);
		webViewChartsEngine.executeScript(String.format("updateTabulator(%s, %s)", data, getOptions()));

	}

	@Override
	public void fillOptions(JSObject jsObject) {

		jsObject.put("invalidOptionWarnings", false);
		jsObject.put("height", "height");
		jsObject.put("tooltipsHeader", true);
		jsObject.put("virtualDom", true);
		jsObject.put("cellVertAlign", "'middle'");
		jsObject.putWithQuote("layout", "fitColumns");
		jsObject.put("rowClick", "function(e,row){javaConnector.dataPointSelection(row.getPosition());}");

	}

	@Override
	public void exportCSV(String path) throws IOException {
		Instant init = datePickerStart.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant();
		Instant end = datePickerEnd.getValue().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		List<CourseModule> courseModules = listViewActivity.getSelectionModel().getSelectedItems();
		List<String> header = new ArrayList<>();
		header.add("userid");
		header.add("fullname");
		for (CourseModule courseModule : courseModules) {
			header.add(courseModule.getModuleName());
			header.add("end date " + courseModule.getModuleName());
		}
		header.add("completed");
		header.add("nCourseModules");
		header.add("percentageCompleted");

		try (CSVPrinter printer = new CSVPrinter(getWritter(path),
				CSVFormat.DEFAULT.withHeader(header.toArray(new String[0])))) {
			for (EnrolledUser enrolledUser : enrolledUsers) {
				printer.print(enrolledUser.getId());
				printer.print(enrolledUser.getFullName());
				int completed = 0;
				for (CourseModule courseModule : courseModules) {

					ActivityCompletion activity = courseModule.getActivitiesCompletion().get(enrolledUser);
					State state = activity.getState();
					Instant timeCompleted = activity.getTimecompleted();

					if ((state == ActivityCompletion.State.COMPLETE || state == ActivityCompletion.State.COMPLETE_PASS)
							&& timeCompleted != null && init.isBefore(timeCompleted) && end.isAfter(timeCompleted)) {
						++completed;
						printer.print(activity.getState().ordinal());
						printer.print(dateTimeWrapper.format(timeCompleted));
					} else {
						printer.print(ActivityCompletion.State.INCOMPLETE.ordinal());
						printer.print(null);
					}

				}
				printer.print(completed);
				printer.print(courseModules.size());
				printer.print(completed / (double) courseModules.size() * 100);
				printer.println();

			}
		}

	}

}
