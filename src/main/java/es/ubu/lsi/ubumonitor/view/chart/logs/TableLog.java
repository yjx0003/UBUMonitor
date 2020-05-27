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

public class TableLog extends TabulatorLogs {

	private static final String DATETIME = "a";
	private static final String NAME = "b";
	private static final String COMPONENT = "c";
	private static final String EVENT = "d";
	private static final String COURSE_MODULE = "e";
	private static final String ORIGIN = "f";
	private static final String IP = "g";
	private static final String ID = "h";

	private DateTimeWrapper dateTimeWrapper;

	public TableLog(MainController mainController) {
		super(mainController, ChartType.TABLE_LOG);
		dateTimeWrapper = new DateTimeWrapper();
		useRangeDate = true;
	}

	private <E> List<LogLine> createLogLines(List<EnrolledUser> users, List<E> typeLogs, DataSet<E> dataSet) {

		GroupByAbstract<?> groupBy = Controller.getInstance()
				.getActualCourse()
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
				.sorted(Comparator.comparing(LogLine::getTime))
				.collect(Collectors.toList());

	}

	private JSArray createColumns() {
		JSArray jsArray = new JSArray();
		JSObject jsObject = createColumn(jsArray, I18n.get("text.datetime"), DATETIME, "datetime");
		jsObject.put("sorterParams",
				"{format:'" + UtilMethods.escapeJavaScriptText(dateTimeWrapper.getPattern()) + "'}");
		jsObject.put("topCalc", "'count'" );
		createColumn(jsArray, I18n.get("chartlabel.name"), NAME);
		createColumn(jsArray, I18n.get("text.component"), COMPONENT);
		createColumn(jsArray, I18n.get("text.event"), EVENT);
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
		jsObject.put("align", "'center'");
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
			}
			jsObject.putWithQuote(ORIGIN, logLine.getOrigin());
			jsObject.putWithQuote(IP, logLine.getIPAdress());
			jsArray.add(jsObject);
		}
		data.put("tabledata", jsArray);

		return data.toString();

	}

	@Override
	public String getOptions(JSObject jsObject) {

		jsObject.put("invalidOptionWarnings", false);
		jsObject.put("tooltipsHeader", true);
		jsObject.put("virtualDom", true);
		jsObject.putWithQuote("headerFilterPlaceholder", I18n.get("label.filter"));
		jsObject.putWithQuote("sort", DATETIME);
		jsObject.putWithQuote("layout", "fitDataFill");
		jsObject.put("rowClick", "function(e,row){javaConnector.dataPointSelection(row._row.data." + ID + ");}");
		return jsObject.toString();
	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {

	}

	@Override
	protected String[] getCSVHeader() {

		return null;
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {

	}

	@Override
	protected String[] getCSVDesglosedHeader() {

		return null;
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
