package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.model.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.view.chart.Chart;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;

public abstract class ChartLogs extends Chart {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartLogs.class);

	private String max;

	public ChartLogs(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
	}

	@Override
	public String getMax() {
		return max;
	}

	@Override
	public void setMax(String max) {
		this.max = max;
	}

	@Override
	public void update() {
		String dataset = null;

		if (tabUbuLogsComponent.isSelected()) {

			dataset = createData(listViewComponents.getSelectionModel().getSelectedItems(),
					DataSetComponent.getInstance());
		} else if (tabUbuLogsEvent.isSelected()) {

			dataset = createData(listViewEvents.getSelectionModel().getSelectedItems(),
					DataSetComponentEvent.getInstance());
		} else if (tabUbuLogsSection.isSelected()) {

			dataset = createData(listViewSection.getSelectionModel().getSelectedItems(), DataSetSection.getInstance());
		} else if (tabUbuLogsCourseModule.isSelected()) {

			dataset = createData(listViewCourseModule.getSelectionModel().getSelectedItems(),
					DatasSetCourseModule.getInstance());
		}

		String options = getOptions();
		LOGGER.info("Dataset {} en JS: {}", chartType, dataset);
		LOGGER.info("Opciones {} en JS: {}", chartType, options);
		webViewChartsEngine.executeScript(getJSFunction(dataset, options));

	}

	@Override
	public void exportCSV(String path) throws IOException {
		String[] header = getCSVHeader();
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader(header))) {
			if (tabUbuLogsComponent.isSelected()) {
				exportCSV(printer, DataSetComponent.getInstance(),
						listViewComponents.getSelectionModel().getSelectedItems());
			} else if (tabUbuLogsEvent.isSelected()) {
				exportCSV(printer, DataSetComponentEvent.getInstance(),
						listViewEvents.getSelectionModel().getSelectedItems());
			} else if (tabUbuLogsSection.isSelected()) {
				exportCSV(printer, DataSetSection.getInstance(),
						listViewSection.getSelectionModel().getSelectedItems());
			} else if (tabUbuLogsCourseModule.isSelected()) {
				exportCSV(printer, DatasSetCourseModule.getInstance(),
						listViewCourseModule.getSelectionModel().getSelectedItems());
			}
		}

	}
	
	public boolean hasId() {
		return tabUbuLogsSection.isSelected() || tabUbuLogsCourseModule.isSelected();
	}

	@Override
	public void exportCSVDesglosed(String path) throws IOException {
		String[] header = getCSVDesglosedHeader();
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader(header))) {
			if (tabUbuLogsComponent.isSelected()) {
				exportCSVDesglosed(printer, DataSetComponent.getInstance(),
						listViewComponents.getSelectionModel().getSelectedItems());
			} else if (tabUbuLogsEvent.isSelected()) {
				exportCSVDesglosed(printer, DataSetComponentEvent.getInstance(),
						listViewEvents.getSelectionModel().getSelectedItems());
			} else if (tabUbuLogsSection.isSelected()) {
				exportCSVDesglosed(printer, DataSetSection.getInstance(),
						listViewSection.getSelectionModel().getSelectedItems());
			} else if (tabUbuLogsCourseModule.isSelected()) {
				exportCSVDesglosed(printer, DatasSetCourseModule.getInstance(),
						listViewCourseModule.getSelectionModel().getSelectedItems());
			}
		}

	}

	protected abstract String getJSFunction(String dataset, String options);

	public abstract <E> String createData(List<E> typeLogs, DataSet<E> dataSet);

	protected abstract <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException;

	protected abstract String[] getCSVHeader();

	protected abstract <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException;

	protected abstract String[] getCSVDesglosedHeader();

}
