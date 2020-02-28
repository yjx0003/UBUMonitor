package es.ubu.lsi.ubumonitor.controllers.charts.activitystatus;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.charts.ChartType;
import es.ubu.lsi.ubumonitor.controllers.charts.Tabs;
import es.ubu.lsi.ubumonitor.controllers.charts.Tabulator;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.ActivityCompletion;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;

public class ActivitiesStatusTable extends Tabulator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActivitiesStatusTable.class);

	private String datePattern;
	private String timePattern;
	private DateTimeFormatter dateFormatter;
	private DateTimeFormatter timeFormatter;

	public ActivitiesStatusTable(MainController mainController) {
		super(mainController, ChartType.ACTIVITIES_TABLE, Tabs.ACTIVITY_COMPLETION);

		datePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null,
				IsoChronology.INSTANCE, Locale.getDefault());
		timePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, FormatStyle.SHORT,
				IsoChronology.INSTANCE, Locale.getDefault());
		dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withZone(ZoneId.systemDefault());
		timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault());
		useRangeDate = true;
	}

	public String createColumns(List<CourseModule> courseModules) {
		// users columns
		JSObject jsObject = new JSObject();
		JSArray array = new JSArray();

		jsObject.putWithQuote("title", I18n.get("chartlabel.name"));
		jsObject.putWithQuote("field", "name");
		jsObject.putWithQuote("frozen", true);
		array.add(jsObject.toString());

		JSObject formatterParams = new JSObject();
		formatterParams.put("allowEmpty", true);
		formatterParams.put("allowTruthy", true);
		String stringFormatterParams = formatterParams.toString();

		JSObject sorterParams = new JSObject();
		sorterParams.putWithQuote("format", datePattern.toUpperCase() + ", " + timePattern);
		sorterParams.putWithQuote("alignEmptyValues", "bottom");
		String stringsorterParams = sorterParams.toString();

		for (CourseModule courseModule : courseModules) {
			jsObject = new JSObject();
			jsObject.putWithQuote("align", "center");
			jsObject.put("tooltip", true);

			jsObject.putWithQuote("formatter", "tickCross");
			jsObject.put("topCalc",
					"function(n,r,c){var f=0;return n.forEach(function(n){n&&f++;}),f+'/'+n.length+' ('+(f/n.length||0).toLocaleString(locale,{style:'percent',maximumFractionDigits:2})+')';}");
			jsObject.put("formatterParams", stringFormatterParams);
			jsObject.putWithQuote("sorter", "datetime");
			jsObject.put("sorterParams", stringsorterParams);
			jsObject.putWithQuote("title", courseModule.getModuleName());
			jsObject.putWithQuote("field", "ID" + courseModule.getCmid());

			array.add(jsObject.toString());
		}

		jsObject = new JSObject();
		jsObject.putWithQuote("title", I18n.get("chartlabel.progress"));
		jsObject.putWithQuote("field", "progress");
		jsObject.putWithQuote("formatter", "progress");
		jsObject.putWithQuote("frozen", true);
		jsObject.put("formatterParams", getProgressParam(courseModules.size()));
		array.add(jsObject.toString());
		return array.toString();
	}

	private String getProgressParam(int max) {
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		JSObject jsObject = new JSObject();
		jsObject.put("min", 0);
		jsObject.put("max", max);

		jsObject.put("legend", String
				.format("function(value){return value+'/'+%s +' ('+Math.round(value/%s*100||0)+'%%)';}", max, max));

		jsObject.putWithQuote("legendAlign", "center");
		JSArray jsArray = new JSArray();

		jsArray.add(colorToRGB(mainConfiguration.getValue(getChartType(), "firstInterval")));
		jsArray.add(colorToRGB(mainConfiguration.getValue(getChartType(), "secondInterval")));
		jsArray.add(colorToRGB(mainConfiguration.getValue(getChartType(), "thirdInterval")));
		jsArray.add(colorToRGB(mainConfiguration.getValue(getChartType(), "fourthInterval")));
		jsArray.add(colorToRGB(mainConfiguration.getValue(getChartType(), "moreMax")));
		jsObject.put("color",
				String.format(Locale.ROOT, "function(e){return %s[e/%f|0]}", jsArray.toString(), max / 4.0));
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
				if (activity == null || activity.getState() == null) {
					jsObject.putWithQuote(field, "");
				} else {
					switch (activity.getState()) {
					case COMPLETE:
					case COMPLETE_PASS:
						Instant timeCompleted = activity.getTimecompleted();
						if (timeCompleted != null && init.isBefore(timeCompleted) && end.isAfter(timeCompleted)) {
							progress++;
							jsObject.putWithQuote(field, dateFormatter.format(activity.getTimecompleted()) + ", "
									+ timeFormatter.format(activity.getTimecompleted()));
						}

						break;
					case COMPLETE_FAIL:
						jsObject.put(field, false);
						break;

					case INCOMPLETE:
						jsObject.putWithQuote(field, "");
						break;
					default:
						jsObject.putWithQuote(field, "");
						break;

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

		List<CourseModule> courseModules = mainController.getListViewActivity().getSelectionModel().getSelectedItems();
		String columns = createColumns(courseModules);
		String data = createData(enrolledUsers, courseModules);
		LOGGER.debug("Usuarios seleccionados:{}", enrolledUsers);
		LOGGER.debug("Columnas:{}", columns);
		LOGGER.debug("Datos de tabla:{}", data);
		webViewChartsEngine.executeScript(String.format("updateTabulator(%s, %s, %s)", columns, data, getOptions()));

	}

	@Override
	public String getOptions() {

		JSObject jsObject = getDefaultOptions();
		jsObject.put("invalidOptionWarnings", false);
		jsObject.put("height", "height");
		jsObject.put("tooltipsHeader", true);
		jsObject.put("virtualDom", true);
		jsObject.putWithQuote("layout", "fitColumns");
		jsObject.put("rowClick", "function(e,row){javaConnector.dataPointSelection(row.getPosition());}");
		return jsObject.toString();
	}

	@Override
	public void exportCSV(String path) throws IOException {
		List<EnrolledUser> enrolledUsers = getSelectedEnrolledUser();
		List<CourseModule> courseModules = mainController.getListViewActivity().getSelectionModel().getSelectedItems();
		List<String> header = new ArrayList<>();
		header.add("userid");
		header.add("fullname");
		for (CourseModule courseModule : courseModules) {
			header.add(courseModule.getModuleName());
			header.add("end date " + courseModule.getModuleName());
		}
		
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader(header.toArray(new String[0])))) {
			for (EnrolledUser enrolledUser : enrolledUsers) {
				printer.print(enrolledUser.getId());
				printer.print(enrolledUser.getFullName());
				for (CourseModule courseModule : courseModules) {

					ActivityCompletion activity = courseModule.getActivitiesCompletion().get(enrolledUser);
					printer.print(activity.getState().ordinal());
					printer.print(dateFormatter.format(activity.getTimecompleted()) + ", "
							+ timeFormatter.format(activity.getTimecompleted()));
				}
				printer.println();

			}
		}

	}

}
