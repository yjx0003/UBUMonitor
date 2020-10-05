package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVPrinter;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.LogLine;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.model.log.TypeTimes;
import es.ubu.lsi.ubumonitor.util.DateTimeWrapper;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import javafx.scene.web.WebView;

public class TableLog extends TabulatorLogs {

	private static final String DATETIME = "a";
	private static final String NAME = "b";
	private static final String COMPONENT = "c";
	private static final String EVENT = "d";
	private static final String COURSE_MODULE = "e";
	private static final String ORIGIN = "f";
	private static final String IP = "g";
	private static final String ID = "h";
	private static final String SECTION = "i";

	private DateTimeWrapper dateTimeWrapper;

	public TableLog(MainController mainController, WebView webView) {
		super(mainController, ChartType.TABLE_LOG, webView);
		dateTimeWrapper = new DateTimeWrapper();
		useRangeDate = true;
	}

	private <E> List<LogLine> createLogLines(List<EnrolledUser> users, List<E> typeLogs, DataSet<E> dataSet) {

		GroupByAbstract<?> groupBy = actualCourse
				.getLogStats()
				.getByType(TypeTimes.DAY);

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		Map<EnrolledUser, Map<E, List<LogLine>>> map = dataSet.getUserLogs(groupBy, users, typeLogs, dateStart,
				dateEnd);
		return map.values()
				.stream()
				.flatMap(m -> m.values()
						.stream())
				.flatMap(List::stream)
				.sorted(Comparator.comparing(LogLine::getTime).reversed())
				.collect(Collectors.toList());

	}

	private JSArray createColumns() {
		JSArray jsArray = new JSArray();
		JSObject jsObject = createColumn(jsArray, I18n.get("text.datetime"), DATETIME, "datetime");
		jsObject.put("sorterParams",
				"{format:'" + UtilMethods.escapeJavaScriptText(dateTimeWrapper.getPattern()) + "'}");
		jsObject.put("headerSortStartingDir", "'asc'");
		jsObject.put("topCalc", "'count'");
		createColumn(jsArray, I18n.get("chartlabel.name"), NAME);
		createColumn(jsArray, I18n.get("text.component"), COMPONENT);
		createColumn(jsArray, I18n.get("text.event"), EVENT);
		createColumn(jsArray, I18n.get("text.section"), SECTION);
		createColumn(jsArray, I18n.get("text.coursemodule"), COURSE_MODULE);
		createColumn(jsArray, I18n.get("text.origin"), ORIGIN);
		createColumn(jsArray, I18n.get("text.ip"), IP);
		return jsArray;

	}

	private JSObject createColumn(JSArray jsArray, String title, String field) {
		return createColumn(jsArray, title, field, "string");
	}

	private JSObject createColumn(JSArray jsArray, String title, String field, String sorter) {
		JSObject jsObject = new JSObject();
		jsObject.putWithQuote("title", title);
		jsObject.putWithQuote("field", field);
		jsObject.putWithQuote("sorter", sorter);
		jsObject.put("headerFilter", true);
		jsObject.put("hozAlign", "'center'");
		jsArray.add(jsObject);
		return jsObject;
	}

	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {

		List<LogLine> logLines = createLogLines(getSelectedEnrolledUser(), typeLogs, dataSet);
		JSObject data = new JSObject();
		JSArray jsArray = new JSArray();
		data.put("columns", createColumns());

		for (LogLine logLine : logLines) {
			JSObject jsObject = new JSObject();

			jsObject.put(ID, logLine.getUser()
					.getId());

			jsObject.putWithQuote(DATETIME, dateTimeWrapper.format(logLine.getTime()));

			jsObject.putWithQuote(NAME, logLine.getUser()
					.getFullName());
			jsObject.putWithQuote(COMPONENT, I18n.get(logLine.getComponent()));
			jsObject.putWithQuote(EVENT, I18n.get(logLine.getEventName()));

			if (logLine.getCourseModule() != null) {
				jsObject.putWithQuote(COURSE_MODULE, logLine.getCourseModule()
						.getModuleName());
				if (logLine.getCourseModule()
						.getSection() != null) {
					jsObject.putWithQuote(SECTION, logLine.getCourseModule()
							.getSection()
							.getName());
				}

			}
			jsObject.putWithQuote(ORIGIN, logLine.getOrigin());
			jsObject.putWithQuote(IP, logLine.getIPAdress());
			jsArray.add(jsObject);
		}
		data.put("tabledata", jsArray);

		return data.toString();

	}

	@Override
	public JSObject getOptions(JSObject jsObject) {

		jsObject.put("invalidOptionWarnings", false);
		jsObject.put("tooltipsHeader", true);
		jsObject.put("virtualDom", true);
		jsObject.putWithQuote("headerFilterPlaceholder", I18n.get("label.filter"));
		jsObject.putWithQuote("sort", DATETIME);
		jsObject.putWithQuote("layout", "fitDataStretch");
		jsObject.putWithQuote("layoutColumnsOnNewData", true);
		jsObject.put("rowClick", "function(e,row){javaConnector.dataPointSelection(row._row.data." + ID + ");}");
		return jsObject;
	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		for (LogLine logLine : createLogLines(getSelectedEnrolledUser(), typeLogs, dataSet)) {
			printer.print(Controller.DATE_TIME_FORMATTER.format(logLine.getTime()));
			printer.print(logLine.getUser()
					.getFullName());
			printer.print(I18n.get(logLine.getComponent()));
			printer.print(I18n.get(logLine.getEventName()));
			if (logLine.getCourseModule() != null) {
				printer.print(logLine.getCourseModule()
						.getSection()
						.getName());
				printer.print(logLine.getCourseModule()
						.getModuleName());
			} else {
				printer.print(null);
				printer.print(null);
			}
			printer.print(logLine.getOrigin());
			printer.print(logLine.getIPAdress());
			printer.println();
		}

	}

	@Override
	protected String[] getCSVHeader() {
		return new String[] { "dateTime", "username", "component", "event", "section", "courseModule", "origin",
				"ipAdress" };
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		for (LogLine logLine : createLogLines(getSelectedEnrolledUser(), typeLogs, dataSet)) {
			printer.print(Controller.DATE_TIME_FORMATTER.format(logLine.getTime()));
			printer.print(logLine.getUser()
					.getId());
			printer.print(logLine.getUser()
					.getFullName());
			printer.print(I18n.get(logLine.getComponent()));
			printer.print(I18n.get(logLine.getEventName()));
			CourseModule cm = logLine.getCourseModule();
			if (cm != null) {
				printer.print(cm.getSection()
						.getId());
				printer.print(cm.getSection()
						.getName());
				printer.print(cm.getCmid());
				printer.print(cm.getModuleName());
			} else {
				printer.print(null);
				printer.print(null);
				printer.print(null);
				printer.print(null);
			}
			printer.print(logLine.getOrigin());
			printer.print(logLine.getIPAdress());
			printer.println();
		}
	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		return new String[] { "dateTime", "userid", "username", "component", "event", "sectionid", "sectionName",
				"courseModuleId", "courseModuleName", "origin", "ipAdress" };
	}

	@Override
	public int onClick(int userid) {
		EnrolledUser user = Controller.getInstance()
				.getDataBase()
				.getUsers()
				.getById(userid);
		return getUsers().indexOf(user);
	}

}
