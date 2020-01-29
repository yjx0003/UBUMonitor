package controllers.charts;

import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.Controller;
import controllers.I18n;
import controllers.MainController;
import controllers.configuration.MainConfiguration;
import model.ActivityCompletion;
import model.CourseModule;
import model.EnrolledUser;

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

	}

	public String createColumns(List<CourseModule> courseModules) {
		// users columns
		StringJoiner jsObject = JSObject();
		StringJoiner array = JSArray();

		addKeyValueWithQuote(jsObject, "title", I18n.get("chartlabel.name"));
		addKeyValueWithQuote(jsObject, "field", "name");
		addKeyValueWithQuote(jsObject, "frozen", true);
		array.add(jsObject.toString());

		StringJoiner formatterParams = JSObject();
		addKeyValue(formatterParams, "allowEmpty", true);
		addKeyValue(formatterParams, "allowTruthy", true);
		String stringFormatterParams = formatterParams.toString();

		StringJoiner sorterParams = JSObject();
		addKeyValueWithQuote(sorterParams, "format", datePattern.toUpperCase() + ", " + timePattern);
		addKeyValueWithQuote(sorterParams, "alignEmptyValues", "bottom");
		String stringsorterParams = sorterParams.toString();

		for (CourseModule courseModule : courseModules) {
			jsObject = JSObject();
			addKeyValueWithQuote(jsObject, "align", "center");
			addKeyValue(jsObject, "tooltip", true);

			addKeyValueWithQuote(jsObject, "formatter", "tickCross");

			addKeyValue(jsObject, "formatterParams", stringFormatterParams);
			addKeyValueWithQuote(jsObject, "sorter", "datetime");
			addKeyValue(jsObject, "sorterParams", stringsorterParams);
			addKeyValueWithQuote(jsObject, "title", courseModule.getModuleName());
			addKeyValueWithQuote(jsObject, "field", "ID" + courseModule.getCmid());

			array.add(jsObject.toString());
		}

		jsObject = JSObject();
		addKeyValueWithQuote(jsObject, "title", I18n.get("chartlabel.progress"));
		addKeyValueWithQuote(jsObject, "field", "progress");
		addKeyValueWithQuote(jsObject, "formatter", "progress");
		addKeyValueWithQuote(jsObject, "frozen", true);
		addKeyValue(jsObject, "formatterParams", getProgressParam(courseModules.size()));
		array.add(jsObject.toString());
		return array.toString();
	}

	private String getProgressParam(int max) {
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		StringJoiner jsObject = JSObject();
		addKeyValue(jsObject, "min", 0);
		addKeyValue(jsObject, "max", max);

		addKeyValue(jsObject, "legend", String
				.format("function(value){return value+'/'+%s +' ('+Math.round(value/%s*100||0)+'%%)';}", max, max));

		addKeyValueWithQuote(jsObject, "legendAlign", "center");
		StringJoiner jsArray = JSArray();

		jsArray.add(colorToRGB(mainConfiguration.getValue(getChartType(), "firstInterval")));
		jsArray.add(colorToRGB(mainConfiguration.getValue(getChartType(), "secondInterval")));
		jsArray.add(colorToRGB(mainConfiguration.getValue(getChartType(), "thirdInterval")));
		jsArray.add(colorToRGB(mainConfiguration.getValue(getChartType(), "fourthInterval")));
		jsArray.add(colorToRGB(mainConfiguration.getValue(getChartType(), "moreMax")));
		addKeyValue(jsObject, "color", String.format(Locale.ROOT, "function(e){return %s[e/%f|0]}", jsArray.toString(), max / 4.0));
		return jsObject.toString();
	}

	public String createData(List<EnrolledUser> enrolledUsers, List<CourseModule> courseModules) {
		StringJoiner array = JSArray();
		StringJoiner jsObject;

		for (EnrolledUser enrolledUser : enrolledUsers) {
			jsObject = JSObject();
			addKeyValueWithQuote(jsObject, "name", enrolledUser.getFullName());
			int progress = 0;
			for (CourseModule courseModule : courseModules) {
				ActivityCompletion activity = courseModule.getActivitiesCompletion().get(enrolledUser);
				String field = "ID" + courseModule.getCmid();
				if (activity == null || activity.getState() == null) {
					addKeyValueWithQuote(jsObject, field, "");
				} else {
					switch (activity.getState()) {
					case COMPLETE:
					case COMPLETE_PASS:
						progress++;
						addKeyValueWithQuote(jsObject, field, dateFormatter.format(activity.getTimecompleted()) + ", "
								+ timeFormatter.format(activity.getTimecompleted()));
						break;
					case COMPLETE_FAIL:
						addKeyValue(jsObject, field, false);
						break;

					case INCOMPLETE:
						addKeyValueWithQuote(jsObject, field, "");
						break;
					default:
						addKeyValueWithQuote(jsObject, field, "");
						break;

					}

				}

			}

			addKeyValue(jsObject, "progress", progress);
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

//		{
//	        invalidOptionWarnings: false,
//	        height: height,
//	        //placeholder: "No data",
//	        tooltipsHeader: true,
//	        virtualDom: true,
//	        layout: "fitColumns", //fit columns to width of table (optional)
//	        rowClick: function (e, row) {
//	            javaConnector.dataPointSelection(row.getPosition());
//
//	        },
//	    }
		StringJoiner jsObject = getDefaultOptions();
		addKeyValue(jsObject, "invalidOptionWarnings", false);
		addKeyValue(jsObject, "height", "height");
		addKeyValue(jsObject, "tooltipsHeader", true);
		addKeyValue(jsObject, "virtualDom", true);
		addKeyValueWithQuote(jsObject, "layout", "fitColumns");
		addKeyValue(jsObject, "rowClick", "function(e,row){javaConnector.dataPointSelection(row.getPosition());}");
		return jsObject.toString();
	}

}
