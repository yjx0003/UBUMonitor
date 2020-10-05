package es.ubu.lsi.ubumonitor.view.chart.bridge;

import es.ubu.lsi.ubumonitor.view.chart.Chart;

public interface JavaConnector {
	public void updateChart();

	public boolean toggleLegend();

	public boolean toggleGeneral();

	public boolean toggleGroup();

	public void updateOptionsImages();

	public void inititDefaultValues();

	public void showErrorWindow(String errorMessage);

	public void dataPointSelection(int selectedIndex);

	public Chart getCurrentChart();

	public void clear();

	public void updateCharts(String typeChart);

	public void setCurrentChart(Chart chart);

	public void addChart(Chart chart);

	public default void updateChartFromJS() {

	}

	public default void manageOptions() {
	};

}
