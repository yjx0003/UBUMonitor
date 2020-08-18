package es.ubu.lsi.ubumonitor.view.chart;

import java.io.File;
import java.io.IOException;

public interface JavaConnector {
	public void updateChart();

	public boolean toggleLegend();

	public boolean toggleGeneral();

	public boolean toggleGroup();

	public void updateOptionsImages();

	public void exportImage(File file) throws IOException;

	public void inititDefaultValues();

	public void showErrorWindow(String errorMessage);
	public void hideLegend();

	public void dataPointSelection(int selectedIndex);

	public Chart getCurrentChart();

	public void clear();

	public void updateCharts(String typeChart);

	public void setCurrentChart(Chart chart);

}
