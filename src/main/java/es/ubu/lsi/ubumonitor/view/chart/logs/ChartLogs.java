package es.ubu.lsi.ubumonitor.view.chart.logs;

import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.SelectionController;
import es.ubu.lsi.ubumonitor.controllers.tabs.VisualizationController;
import es.ubu.lsi.ubumonitor.model.Component;
import es.ubu.lsi.ubumonitor.model.ComponentEvent;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.Section;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.model.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.Chart;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public abstract class ChartLogs extends Chart {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartLogs.class);
	protected DatePicker datePickerStart;
	protected DatePicker datePickerEnd;
	protected ChoiceBox<GroupByAbstract<?>> choiceBoxDate;
	protected TextField textFieldMax;
	protected TabPane tabPaneSelection;
	protected Tab tabComponent;
	protected Tab tabEvent;
	protected Tab tabSection;
	protected Tab tabCourseModule;
	protected ListView<Component> listViewComponent;
	protected ListView<ComponentEvent> listViewEvent;
	protected ListView<Section> listViewSection;
	protected ListView<CourseModule> listViewCourseModule;

	private String max;

	public ChartLogs(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
		SelectionController selectionController = mainController.getSelectionController();
		VisualizationController visualizationController = mainController.getWebViewTabsController().getVisualizationController();
		this.datePickerStart = visualizationController.getDatePickerStart();
		this.datePickerEnd = visualizationController.getDatePickerEnd();
		this.choiceBoxDate = visualizationController.getChoiceBoxDate();
		this.textFieldMax = visualizationController.getTextFieldMax();
		this.tabPaneSelection = selectionController.getTabPaneUbuLogs();
		this.tabComponent = selectionController.getTabUbuLogsComponent();
		this.tabEvent = selectionController.getTabUbuLogsEvent();
		this.tabSection = selectionController.getTabUbuLogsSection();
		this.tabCourseModule = selectionController.getTabUbuLogsCourseModule();
		this.listViewComponent = selectionController.getListViewComponents();
		this.listViewEvent = selectionController.getListViewEvents();
		this.listViewSection = selectionController.getListViewSection();
		this.listViewCourseModule = selectionController.getListViewCourseModule();
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

		if (tabComponent.isSelected()) {

			dataset = createData(listViewComponent.getSelectionModel()
					.getSelectedItems(), DataSetComponent.getInstance());
		} else if (tabEvent.isSelected()) {

			dataset = createData(listViewEvent.getSelectionModel()
					.getSelectedItems(), DataSetComponentEvent.getInstance());
		} else if (tabSection.isSelected()) {

			dataset = createData(listViewSection.getSelectionModel()
					.getSelectedItems(), DataSetSection.getInstance());
		} else if (tabCourseModule.isSelected()) {

			dataset = createData(listViewCourseModule.getSelectionModel()
					.getSelectedItems(), DatasSetCourseModule.getInstance());
		}

		JSObject options = getOptions();
		LOGGER.info("Dataset {} en JS: {}", chartType, dataset);
		LOGGER.info("Opciones {} en JS: {}", chartType, options);
		webViewChartsEngine.executeScript(getJSFunction(dataset, options.toString()));

	}

	@Override
	public void exportCSV(String path) throws IOException {
		String[] header = getCSVHeader();
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader(header))) {
			if (tabComponent.isSelected()) {
				exportCSV(printer, DataSetComponent.getInstance(), listViewComponent.getSelectionModel()
						.getSelectedItems());
			} else if (tabEvent.isSelected()) {
				exportCSV(printer, DataSetComponentEvent.getInstance(), listViewEvent.getSelectionModel()
						.getSelectedItems());
			} else if (tabSection.isSelected()) {
				exportCSV(printer, DataSetSection.getInstance(), listViewSection.getSelectionModel()
						.getSelectedItems());
			} else if (tabCourseModule.isSelected()) {
				exportCSV(printer, DatasSetCourseModule.getInstance(), listViewCourseModule.getSelectionModel()
						.getSelectedItems());
			}
		}

	}

	public boolean hasId() {
		return tabSection.isSelected() || tabCourseModule.isSelected();
	}

	@Override
	public void exportCSVDesglosed(String path) throws IOException {
		String[] header = getCSVDesglosedHeader();
		try (CSVPrinter printer = new CSVPrinter(getWritter(path), CSVFormat.DEFAULT.withHeader(header))) {
			if (tabComponent.isSelected()) {
				exportCSVDesglosed(printer, DataSetComponent.getInstance(), listViewComponent.getSelectionModel()
						.getSelectedItems());
			} else if (tabEvent.isSelected()) {
				exportCSVDesglosed(printer, DataSetComponentEvent.getInstance(), listViewEvent.getSelectionModel()
						.getSelectedItems());
			} else if (tabSection.isSelected()) {
				exportCSVDesglosed(printer, DataSetSection.getInstance(), listViewSection.getSelectionModel()
						.getSelectedItems());
			} else if (tabCourseModule.isSelected()) {
				exportCSVDesglosed(printer, DatasSetCourseModule.getInstance(), listViewCourseModule.getSelectionModel()
						.getSelectedItems());
			}
		}

	}

	@Override
	public boolean isCalculateMaxActivated() {
		return mainConfiguration.getValue(getChartType(), "calculateMax", false);
	}

	@Override
	public long getSuggestedMax(String maxString) {
		if (maxString == null || maxString.isEmpty()) {
			return 0;
		}
		return Long.valueOf(maxString);

	}

	protected abstract String getJSFunction(String dataset, String options);

	public abstract <E> String createData(List<E> typeLogs, DataSet<E> dataSet);

	protected abstract <E> void exportCSV(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs) throws IOException;

	protected abstract String[] getCSVHeader();

	protected abstract <E> void exportCSVDesglosed(CSVPrinter printer, DataSet<E> dataSet, List<E> typeLogs)
			throws IOException;

	protected abstract String[] getCSVDesglosedHeader();

}
