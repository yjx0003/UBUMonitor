package es.ubu.lsi.ubumonitor.controllers.charts.logs;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.charts.Chart;
import es.ubu.lsi.ubumonitor.controllers.charts.ChartType;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSet;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.ubumonitor.controllers.ubulogs.TypeTimes;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;


public class TotalBar extends ChartjsLog {

	public TotalBar(MainController mainController) {
		super(mainController, ChartType.TOTAL_BAR);
		useRangeDate = true;
		useLegend = true;
	}


	@Override
	public String getOptions() {
		JSObject jsObject = getDefaultOptions();
		
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		boolean useHorizontal = mainConfiguration.getValue(getChartType(), "horizontalMode");
		jsObject.putWithQuote("typeGraph", useHorizontal? "horizontalBar": "bar");
		String xLabel = useHorizontal ? getYScaleLabel() :getXScaleLabel();
		String yLabel = useHorizontal ? getXScaleLabel() : getYScaleLabel();
		jsObject.put("scales", "{yAxes:[{" + yLabel + ",ticks:{stepSize:0}}],xAxes:[{" + xLabel + "}]}");
		jsObject.put("onClick", null);
		return jsObject.toString();
	}

	@Override
	public <E> String createData(List<E> typeLogs, DataSet<E> dataSet) {

		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = Controller.getInstance().getActualCourse().getLogStats().getByType(TypeTimes.DAY);
		Map<EnrolledUser, Map<E, List<Integer>>> map = dataSet.getUserCounts(groupBy, selectedUsers, typeLogs,
				dateStart, dateEnd);
		List<DescriptiveStatistics> result = Stream.generate(DescriptiveStatistics::new).limit(typeLogs.size())
				.collect(Collectors.toList());
		for (Map<E, List<Integer>> values : map.values()) {
			for (int i = 0; i < typeLogs.size(); i++) {
				List<Integer> counts = values.get(typeLogs.get(i));
				DescriptiveStatistics descriptiveStatistics = result.get(i);
				int sum = counts.stream().mapToInt(Integer::intValue).sum(); // sum all logs between days
				descriptiveStatistics.addValue(sum);
			}
		}

		JSObject data = new JSObject();
		
		JSObject dataset = new JSObject();
		dataset.putWithQuote("label", I18n.get("text.total"));
		JSArray labels = new JSArray();
		JSArray dataArray = new JSArray();
		JSArray backgrounColor = new JSArray();
		JSArray borderColor = new JSArray();
		
		for (int i = 0; i < typeLogs.size(); i++) {
			E typeLog = typeLogs.get(i);
			labels.addWithQuote(dataSet.translate(typeLog));
			dataArray.add(result.get(i).getSum());
			backgrounColor.add(rgba(typeLog, Chart.OPACITY));
			borderColor.add(hex(typeLog));
		}
		dataset.put("data", dataArray);
		dataset.put("backgroundColor", backgrounColor);
		dataset.put("borderColor", borderColor);
		dataset.put("borderWidth", 1);
		data.put("labels", labels);
		data.put("datasets", "[" + dataset + "]");
		return data.toString();
	}
	
	@Override
	public String getXAxisTitle() {
		return mainController.getTabPaneUbuLogs().getSelectionModel().getSelectedItem().getText();

	}

	@Override
	protected <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = Controller.getInstance().getActualCourse().getLogStats().getByType(TypeTimes.DAY);
		Map<EnrolledUser, Map<E, List<Integer>>> map = dataSet.getUserCounts(groupBy, selectedUsers, typeLogs,
				dateStart, dateEnd);
		
		List<DescriptiveStatistics> result = Stream.generate(DescriptiveStatistics::new).limit(typeLogs.size())
				.collect(Collectors.toList());
		for (Map<E, List<Integer>> values : map.values()) {
			for (int i = 0; i < typeLogs.size(); i++) {
				List<Integer> counts = values.get(typeLogs.get(i));
				DescriptiveStatistics descriptiveStatistics = result.get(i);
				int sum = counts.stream().mapToInt(Integer::intValue).sum(); // sum all logs between days
				descriptiveStatistics.addValue(sum);
			}
		}
		for (int i = 0; i < typeLogs.size(); i++) {
			E typeLog = typeLogs.get(i);
			printer.printRecord(typeLog.hashCode(), typeLog);
		}
		
	}


	@Override
	protected String[] getCSVHeader() {
		String selectedTab = mainController.getTabPaneUbuLogs().getSelectionModel().getSelectedItem().getText();
		return new String[] {selectedTab + "_id", selectedTab};
	}

	@Override
	protected <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs)
			throws IOException {
		List<EnrolledUser> selectedUsers = getSelectedEnrolledUser();

		LocalDate dateStart = datePickerStart.getValue();
		LocalDate dateEnd = datePickerEnd.getValue();
		GroupByAbstract<?> groupBy = Controller.getInstance().getActualCourse().getLogStats().getByType(TypeTimes.DAY);
		Map<EnrolledUser, Map<E, List<Integer>>> map = dataSet.getUserCounts(groupBy, selectedUsers, typeLogs,
				dateStart, dateEnd);
		for(EnrolledUser enrolledUser:selectedUsers) {
			for(E typeLog: typeLogs) {
				List<Integer> list = map.get(enrolledUser).get(typeLog);
				int sum = list.stream().mapToInt(Integer::intValue).sum();
				printer.printRecord(enrolledUser.getId(), enrolledUser.getFullName(), typeLog.hashCode(), typeLog, sum);
			}
		}
	}

	@Override
	protected String[] getCSVDesglosedHeader() {
		String selectedTab = mainController.getTabPaneUbuLogs().getSelectionModel().getSelectedItem().getText();
		return new String[] {"userid", "fullname", selectedTab + "_id", selectedTab, "logs"};
	}

}
