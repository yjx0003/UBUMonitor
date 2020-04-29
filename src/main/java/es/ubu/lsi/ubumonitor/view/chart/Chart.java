package es.ubu.lsi.ubumonitor.view.chart;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.SelectionController;
import es.ubu.lsi.ubumonitor.controllers.SelectionUserController;
import es.ubu.lsi.ubumonitor.controllers.VisualizationController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Component;
import es.ubu.lsi.ubumonitor.model.ComponentEvent;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.Section;
import es.ubu.lsi.ubumonitor.model.Stats;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.Charsets;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;

public abstract class Chart implements ExportableChart {

	protected WebEngine webViewChartsEngine;
	protected CheckComboBox<Group> slcGroup;
	protected ListView<EnrolledUser> listParticipants;
	protected TabPane tabPaneUbuLogs;
	protected Tab tabUbuLogs;
	protected Tab tabUbuLogsComponent;
	protected Tab tabUbuLogsEvent;
	protected Tab tabUbuLogsSection;
	protected Tab tabUbuLogsCourseModule;
	protected ListView<Component> listViewComponents;
	protected ListView<ComponentEvent> listViewEvents;
	protected ListView<Section> listViewSection;
	protected ListView<CourseModule> listViewCourseModule;
	protected ChoiceBox<GroupByAbstract<?>> choiceBoxDate;
	protected DatePicker datePickerStart;
	protected DatePicker datePickerEnd;
	protected TreeView<GradeItem> tvwGradeReport;
	protected Stats stats;
	protected ChartType chartType;
	private Tabs tabName;
	protected boolean useLegend;
	protected boolean useGeneralButton;
	protected boolean useGroupButton;
	protected boolean useGroupBy;

	protected boolean useRangeDate;

	protected boolean useNegativeValues;
	protected boolean useOptions;
	protected VisualizationController visualizationController;
	protected Controller controller = Controller.getInstance();
	protected MainController mainController;
	protected SelectionController selectionController;
	protected SelectionUserController selectionUserController;
	protected static final double OPACITY = 0.2;

	public Chart(MainController mainController, ChartType chartType, Tabs tabName) {
		this.visualizationController = mainController.getVisualizationController();
		this.selectionController = mainController.getSelectionController();
		this.selectionUserController = mainController.getSelectionUserController();
		this.slcGroup = selectionUserController.getCheckComboBoxGroup();
		this.listParticipants = selectionUserController.getListParticipants();
		this.tabUbuLogs = selectionController.getTabUbuLogs();
		this.tabUbuLogsComponent = selectionController.getTabUbuLogsComponent();
		this.tabUbuLogsEvent = selectionController.getTabUbuLogsEvent();
		this.tabUbuLogsSection = selectionController.getTabUbuLogsSection();
		this.tabUbuLogsCourseModule = selectionController.getTabUbuLogsCourseModule();
		this.listViewComponents = selectionController.getListViewComponents();
		this.listViewEvents = selectionController.getListViewEvents();
		this.listViewSection = selectionController.getListViewSection();
		this.listViewCourseModule = selectionController.getListViewCourseModule();
		this.choiceBoxDate = visualizationController.getChoiceBoxDate();
		this.datePickerStart = visualizationController.getDatePickerStart();
		this.datePickerEnd = visualizationController.getDatePickerEnd();
		this.stats = mainController.getStats();
		this.tvwGradeReport = selectionController.getTvwGradeReport();
		this.tabPaneUbuLogs = selectionController.getTabPaneUbuLogs();
		this.webViewChartsEngine = visualizationController.getWebViewChartsEngine();

		this.mainController = mainController;
		this.chartType = chartType;
		this.tabName = tabName;

	}

	public boolean isUseLegend() {
		return useLegend;
	}

	public boolean isUseGeneralButton() {
		return useGeneralButton;
	}

	public boolean isUseGroupButton() {
		return useGroupButton;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	public List<EnrolledUser> getSelectedEnrolledUser() {
		List<EnrolledUser> selectedUsers = new ArrayList<>(listParticipants.getSelectionModel()
				.getSelectedItems());
		selectedUsers.removeAll(Collections.singletonList(null));
		return selectedUsers;
	}

	public double adjustTo10(double value) {
		return Double.isNaN(value) ? value : Math.round(value * 10) / 100.0;
	}

	public int onClick(int index) {

		if (listParticipants.getSelectionModel()
				.getSelectedItems()
				.size() < index) {
			return -1;
		}

		EnrolledUser selectedUser = listParticipants.getSelectionModel()
				.getSelectedItems()
				.get(index);
		return listParticipants.getItems()
				.indexOf(selectedUser);
	}

	public <T> String rgba(T hash, double alpha) {
		return "colorRGBA('" + UtilMethods.escapeJavaScriptText(hash.toString()) + "'," + alpha + ")";
	}

	public <T> String rgb(T hash) {
		return "colorRGB('" + UtilMethods.escapeJavaScriptText(hash.toString()) + "')";
	}

	public <T> String hex(T hash) {
		return "colorHEX('" + UtilMethods.escapeJavaScriptText(hash.toString()) + "')";
	}

	public String colorToRGB(Color color) {

		return colorToRGB(color, color.getOpacity());
	}

	public String colorToRGB(Color color, double opacity) {

		return String.format("'rgba(%s,%s,%s,%s)'", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255), opacity);
	}

	public List<GradeItem> getSelectedGradeItems() {
		return tvwGradeReport.getSelectionModel()
				.getSelectedItems()
				.stream()
				.filter(Objects::nonNull)
				.map(TreeItem::getValue)
				.collect(Collectors.toList());
	}

	public abstract String getOptions();

	public abstract void update();

	public abstract void clear();

	public abstract void hideLegend();

	public abstract String export();

	public JSObject getDefaultOptions() {
		MainConfiguration mainConfiguration = Controller.getInstance()
				.getMainConfiguration();
		JSObject jsObject = new JSObject();
		jsObject.put("chartBackgroundColor", colorToRGB(mainConfiguration.getValue(MainConfiguration.GENERAL, "chartBackgroundColor") ));
		jsObject.put("useLegend", useLegend);
		jsObject.put("useGroup", useGroupButton);
		jsObject.put("useGeneral", useGeneralButton);
		jsObject.putWithQuote("tab", tabName);
		jsObject.putWithQuote("button", getChartType().name());
		jsObject.put("legendActive", mainConfiguration.getValue(MainConfiguration.GENERAL, "legendActive"));
		jsObject.put("generalActive", mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive"));
		jsObject.put("groupActive", mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive"));

		return jsObject;

	}

	public String calculateMax() {
		return null;
	}

	public boolean isUseNegativeValues() {
		return useNegativeValues;
	}

	public boolean isCalculateMaxActivated() {
		return Controller.getInstance()
				.getMainConfiguration()
				.getValue(getChartType(), "calculateMax", false);
	}

	public long getSuggestedMax() {
		String maxString = visualizationController.getTextFieldMax()
				.getText();
		if (maxString == null || maxString.isEmpty()) {
			return 0;
		}
		return Long.valueOf(maxString);

	}

	public void setUseNegativeValues(boolean useNegativeValues) {
		this.useNegativeValues = useNegativeValues;
	}

	public String getYAxisTitle() {
		return I18n.get(getChartType() + ".yAxisTitle");

	}

	public String getXAxisTitle() {
		return I18n.get(getChartType() + ".xAxisTitle");

	}

	public String getMax() {
		return null;
	}

	public void setMax(String max) {
		// do nothing

	}

	public boolean isUseGroupBy() {
		return useGroupBy;
	}

	public boolean isUseRangeDate() {
		return useRangeDate;
	}

	@Override
	public Writer getWritter(String path) throws IOException {
		Charsets charset = Controller.getInstance()
				.getMainConfiguration()
				.getValue(MainConfiguration.GENERAL, "charset");
		return new OutputStreamWriter(new FileOutputStream(path), charset.get());
	}

	public void setWebViewChartsEngine(WebEngine webViewChartsEngine) {
		this.webViewChartsEngine = webViewChartsEngine;
	}

	public WebEngine getWebViewChartsEngine() {
		return webViewChartsEngine;
	}

	public CheckComboBox<Group> getSlcGroup() {
		return slcGroup;
	}

	public ListView<EnrolledUser> getListParticipants() {
		return listParticipants;
	}

	public TabPane getTabPaneUbuLogs() {
		return tabPaneUbuLogs;
	}

	public Tab getTabUbuLogs() {
		return tabUbuLogs;
	}

	public Tab getTabUbuLogsComponent() {
		return tabUbuLogsComponent;
	}

	public Tab getTabUbuLogsEvent() {
		return tabUbuLogsEvent;
	}

	public Tab getTabUbuLogsSection() {
		return tabUbuLogsSection;
	}

	public Tab getTabUbuLogsCourseModule() {
		return tabUbuLogsCourseModule;
	}

	public ListView<Component> getListViewComponents() {
		return listViewComponents;
	}

	public ListView<ComponentEvent> getListViewEvents() {
		return listViewEvents;
	}

	public ListView<Section> getListViewSection() {
		return listViewSection;
	}

	public ListView<CourseModule> getListViewCourseModule() {
		return listViewCourseModule;
	}

	public ChoiceBox<GroupByAbstract<?>> getChoiceBoxDate() {
		return choiceBoxDate;
	}

	public DatePicker getDatePickerStart() {
		return datePickerStart;
	}

	public DatePicker getDatePickerEnd() {
		return datePickerEnd;
	}

	public TreeView<GradeItem> getTvwGradeReport() {
		return tvwGradeReport;
	}

	public Stats getStats() {
		return stats;
	}

	public Tabs getTabName() {
		return tabName;
	}

	public VisualizationController getVisualizationController() {
		return visualizationController;
	}

	public Controller getController() {
		return controller;
	}

	public MainController getMainController() {
		return mainController;
	}

	public SelectionController getSelectionController() {
		return selectionController;
	}

	public SelectionUserController getSelectionUserController() {
		return selectionUserController;
	}

	public static double getOpacity() {
		return OPACITY;
	}

	public boolean isUseOptions() {
		return useOptions;
	}

}
