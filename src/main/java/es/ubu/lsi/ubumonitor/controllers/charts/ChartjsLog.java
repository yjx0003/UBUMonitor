package es.ubu.lsi.ubumonitor.controllers.charts;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSet;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.controllers.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.UtilMethods;

public abstract class ChartjsLog extends Chartjs {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartjsLog.class);

	private String max;

	public ChartjsLog(MainController mainController, ChartType chartType) {
		super(mainController, chartType, Tabs.LOGS);

	}

	public JSArray createLabels(List<String> rangeDates) {
		JSArray labels = new JSArray();
		for (String date : rangeDates) {
			labels.add("'" + UtilMethods.escapeJavaScriptText(date) + "'");
		}
		return labels;
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
		LOGGER.info("Dataset en JS: {}", dataset);
		LOGGER.info("Opciones en JS: {}", options);
		webViewChartsEngine.executeScript(String.format("updateChartjs(%s,%s)", dataset, options));

	}

	@Override
	public String getMax() {
		return max;
	}

	@Override
	public void setMax(String max) {
		this.max = max;
	}

	public abstract <E> String createData(List<E> typeLogs, DataSet<E> dataSet);

}
