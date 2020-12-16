package es.ubu.lsi.ubumonitor.view.chart.bridge;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.SelectionController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.tabs.VisualizationController;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.view.chart.Chart;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import es.ubu.lsi.ubumonitor.view.chart.activitystatus.ActivitiesStatusTable;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.BoxPlot;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.CalificationBar;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.GradeReportTable;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.Line;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.Radar;
import es.ubu.lsi.ubumonitor.view.chart.gradeitems.Violin;
import es.ubu.lsi.ubumonitor.view.chart.logs.BoxplotLog;
import es.ubu.lsi.ubumonitor.view.chart.logs.BoxplotLogTime;
import es.ubu.lsi.ubumonitor.view.chart.logs.CumLine;
import es.ubu.lsi.ubumonitor.view.chart.logs.Heatmap;
import es.ubu.lsi.ubumonitor.view.chart.logs.MeanDiff;
import es.ubu.lsi.ubumonitor.view.chart.logs.Scatter;
import es.ubu.lsi.ubumonitor.view.chart.logs.ScatterUser;
import es.ubu.lsi.ubumonitor.view.chart.logs.SessionChart;
import es.ubu.lsi.ubumonitor.view.chart.logs.Stackedbar;
import es.ubu.lsi.ubumonitor.view.chart.logs.TableLog;
import es.ubu.lsi.ubumonitor.view.chart.logs.TotalBar;
import es.ubu.lsi.ubumonitor.view.chart.logs.ViolinLog;
import es.ubu.lsi.ubumonitor.view.chart.logs.ViolinLogTime;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;

public class VisualizationJavaConnector extends JavaConnectorAbstract {


	private Tab tabLogs;

	private Tab tabGrades;
	private Tab tabActivityCompletion;

	private Chart currentChartLogs;

	private Chart currentChartGrades;
	private Chart currentChartActivityCompletion;

	private VisualizationController visualizationController;

	public VisualizationJavaConnector(WebView webView, MainConfiguration mainConfiguration,
			MainController mainController, VisualizationController visualizationController, Course actualCourse) {
		super(webView, mainConfiguration, mainController, actualCourse);

		this.visualizationController = visualizationController;

		tabLogs = mainController.getSelectionController()
				.getTabUbuLogs();
		tabGrades = mainController.getSelectionController()
				.getTabUbuGrades();
		tabActivityCompletion = mainController.getSelectionController()
				.getTabActivity();

		DatePicker datePickerStart = visualizationController.getDatePickerStart();
		DatePicker datePickerEnd = visualizationController.getDatePickerEnd();
		SelectionController selectionController = mainController.getSelectionController();
		TreeView<GradeItem> treeViewGradeItem = selectionController.getTvwGradeReport();
		addChart(new Heatmap(mainController));
		addChart(new Stackedbar(mainController));
		addChart(new Line(mainController, treeViewGradeItem));
		addChart(new Radar(mainController, treeViewGradeItem));
		addChart(new Scatter(mainController));
		addChart(new ScatterUser(mainController));
		addChart(new BoxPlot(mainController, selectionController.getTvwGradeReport()));
		addChart(new TotalBar(mainController));
		addChart(new Violin(mainController, treeViewGradeItem));
		addChart(new GradeReportTable(mainController, treeViewGradeItem));
		addChart(new CumLine(mainController));
		addChart(new MeanDiff(mainController));
		addChart(new ActivitiesStatusTable(mainController, datePickerStart, datePickerEnd,
				selectionController.getListViewActivity()));
		addChart(new CalificationBar(mainController, treeViewGradeItem));
		addChart(new SessionChart(mainController));
		addChart(new BoxplotLogTime(mainController));
		addChart(new ViolinLogTime(mainController));
		addChart(new TableLog(mainController));
		addChart(new BoxplotLog(mainController));
		addChart(new ViolinLog(mainController));
		currentChart = charts.get(ChartType.getDefault(Tabs.LOGS));
	}

	@Override
	public void updateChartFromJS(String typeChart) {
		Chart chart = charts.get(ChartType.valueOf(typeChart));
		if (tabLogs.isSelected()) {
			currentChartLogs.setMax(visualizationController.getTextFieldMax()
					.getText());
			currentChartLogs = chart;

		} else if (tabGrades.isSelected()) {
			currentChartGrades = chart;
		} else if (tabActivityCompletion.isSelected()) {
			currentChartActivityCompletion = chart;
		}

		if (currentChart.getChartType() != chart.getChartType()) {
			currentChart.clear();
			currentChart = chart;
		}

		if (tabLogs.isSelected()) {
			if (currentChart.isCalculateMaxActivated()) {
				visualizationController.getTextFieldMax()
						.setText(currentChart.calculateMax());
			} else {
				visualizationController.getTextFieldMax()
						.setText(currentChart.getMax());
			}

		}
		manageOptions();
		currentChart.update();

	}
	@Override
	public void manageOptions() {
		if(currentChart==null) {
			return;
		}
		visualizationController.getOptionsUbuLogs()
				.setVisible(currentChart.isUseRangeDate() || currentChart.isUseGroupBy());
		visualizationController.getDateGridPane()
				.setVisible(currentChart.isUseRangeDate()
						|| currentChart.isUseGroupBy() && visualizationController.getChoiceBoxDate()
								.getValue()
								.useDatePicker());
		visualizationController.getGridPaneOptionLogs()
				.setVisible(currentChart.isUseGroupBy());
	}

	public void updateCharts(boolean calculateMax) {
		super.updateChart();
		if (calculateMax) {
			setMax();
		}
		manageOptions();
	}

	@Override
	public void updateChart() {
		updateCharts(true);

	}

	public Chart getChartLogs() {
		return currentChartLogs;
	}

	public void setChartLogs(Chart currentChartLogs) {
		this.currentChartLogs = currentChartLogs;
	}

	public Chart getChartGrades() {
		return currentChartGrades;
	}

	public void setChartGrades(Chart currentChartGrades) {
		this.currentChartGrades = currentChartGrades;
	}

	public void setChartGrades(ChartType chartType) {
		setChartGrades(charts.get(chartType));

	}

	public void setChartLogs(ChartType chartType) {
		setChartLogs(charts.get(chartType));

	}

	private void setChartActivityCompletion(ChartType chartType) {
		setChartActivityCompletion(charts.get(chartType));

	}

	private void setChartActivityCompletion(Chart chart) {
		this.currentChartActivityCompletion = chart;

	}

	@Override
	public void inititDefaultValues() {

		super.inititDefaultValues();
		setChartLogs(ChartType.getDefault(Tabs.LOGS));
		setChartGrades(ChartType.getDefault(Tabs.GRADES));
		setChartActivityCompletion(ChartType.getDefault(Tabs.ACTIVITY_COMPLETION));
		if (tabLogs.isSelected()) {

			setCurrentChart(getChartLogs());
		} else if (tabGrades.isSelected()) {

			setCurrentChart(getChartGrades());
		} else if (tabActivityCompletion.isSelected()) {
			setCurrentChart(getChartActivityCompletion());
		}
	}

	public void setMax() {
		if (!tabLogs.isSelected()) {
			return;
		}

		if (currentChart == null) {
			visualizationController.getTextFieldMax()
					.setText(null);
		} else if (currentChart.isCalculateMaxActivated()) {

			visualizationController.getTextFieldMax()
					.setText(currentChart.calculateMax());
		}

	}

	public Chart getChartActivityCompletion() {
		return currentChartActivityCompletion;
	}

	
}
