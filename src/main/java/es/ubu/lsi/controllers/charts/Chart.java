package es.ubu.lsi.controllers.charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;

import es.ubu.lsi.controllers.Controller;
import es.ubu.lsi.controllers.Exportable;
import es.ubu.lsi.controllers.I18n;
import es.ubu.lsi.controllers.MainController;
import es.ubu.lsi.controllers.VisualizationController;
import es.ubu.lsi.controllers.configuration.MainConfiguration;
import es.ubu.lsi.controllers.ubulogs.GroupByAbstract;
import es.ubu.lsi.model.Component;
import es.ubu.lsi.model.ComponentEvent;
import es.ubu.lsi.model.CourseModule;
import es.ubu.lsi.model.EnrolledUser;
import es.ubu.lsi.model.GradeItem;
import es.ubu.lsi.model.Group;
import es.ubu.lsi.model.Section;
import es.ubu.lsi.model.Stats;
import es.ubu.lsi.util.UtilMethods;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;

public abstract class Chart implements Exportable{

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

	protected boolean useNegativeValues;
	protected VisualizationController visualizationController;
	protected Controller controller = Controller.getInstance();
	protected MainController mainController;
	protected static final double OPACITY = 0.2;

	public Chart(MainController mainController, ChartType chartType, Tabs tabName) {
		this.visualizationController = mainController.getVisualizationTabPageController();

		this.slcGroup = mainController.getCheckComboBoxGroup();
		this.listParticipants = mainController.getListParticipants();
		this.tabUbuLogs = mainController.getTabUbuLogs();
		this.tabUbuLogsComponent = mainController.getTabUbuLogsComponent();
		this.tabUbuLogsEvent = mainController.getTabUbuLogsEvent();
		this.tabUbuLogsSection = mainController.getTabUbuLogsSection();
		this.tabUbuLogsCourseModule = mainController.getTabUbuLogsCourseModule();
		this.listViewComponents = mainController.getListViewComponents();
		this.listViewEvents = mainController.getListViewEvents();
		this.listViewSection = mainController.getListViewSection();
		this.listViewCourseModule = mainController.getListViewCourseModule();
		this.choiceBoxDate = visualizationController.getChoiceBoxDate();
		this.datePickerStart = visualizationController.getDatePickerStart();
		this.datePickerEnd = visualizationController.getDatePickerEnd();
		this.stats = mainController.getStats();
		this.tvwGradeReport = mainController.getTvwGradeReport();
		this.tabPaneUbuLogs = mainController.getTabPaneUbuLogs();
		this.webViewChartsEngine = visualizationController.getWebViewChartsEngine();

		this.mainController = mainController;
		this.chartType = chartType;
		this.tabName = tabName;

		this.useLegend = false;
		this.useGeneralButton = false;
		this.useGroupButton = false;
		this.useNegativeValues = false;
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
		List<EnrolledUser> selectedUsers = new ArrayList<>(listParticipants.getSelectionModel().getSelectedItems());
		selectedUsers.removeAll(Collections.singletonList(null));
		return selectedUsers;
	}

	public double adjustTo10(double value) {
		return Double.isNaN(value) ? value : Math.round(value * 10) / 100.0;
	}

	public int onClick(int index) {

		if (listParticipants.getSelectionModel().getSelectedItems().size() < index) {
			return -1;
		}

		EnrolledUser selectedUser = listParticipants.getSelectionModel().getSelectedItems().get(index);
		return listParticipants.getItems().indexOf(selectedUser);
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

		return String.format("'rgba(%s,%s,%s,%s)'", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255), color.getOpacity());
	}

	public List<GradeItem> getSelectedGradeItems() {
		return tvwGradeReport.getSelectionModel().getSelectedItems().stream().filter(Objects::nonNull)
				.map(TreeItem::getValue).collect(Collectors.toList());
	}

	public StringJoiner JSArray() {
		return new StringJoiner(",", "[", "]");
	}

	public StringJoiner JSObject() {
		return new StringJoiner(",", "{", "}");
	}

	public <K, V> void addKeyValueWithQuote(StringJoiner jsObject, K key, V value) {
		jsObject.add(key + ":'" + UtilMethods.escapeJavaScriptText(value.toString()) + "'");
	}

	public <T> void addKeyValue(StringJoiner jsObject, T key, int value) {
		jsObject.add(key + ":" + value);
	}

	public <T> void addKeyValue(StringJoiner jsObject, T key, double value) {
		jsObject.add(key + ":" + value);
	}

	public <T> void addKeyValue(StringJoiner jsObject, T key, boolean value) {
		jsObject.add(key + ":" + value);
	}

	public <T> void addKeyValue(StringJoiner jsObject, T key, StringJoiner jsArray) {
		jsObject.add(key + ":" + jsArray.toString());
	}

	public <T> void addKeyValue(StringJoiner jsObject, T key, String value) {
		jsObject.add(key + ":" + value);
	}

	public abstract String getOptions();

	public abstract void update();

	public abstract void clear();

	public abstract void hideLegend();

	public abstract String export();

	public StringJoiner getDefaultOptions() {
		MainConfiguration mainConfiguration = Controller.getInstance().getMainConfiguration();
		StringJoiner jsObject = JSObject();
		addKeyValue(jsObject, "useLegend", useLegend);
		addKeyValue(jsObject, "useGroup", useGroupButton);
		addKeyValue(jsObject, "useGeneral", useGeneralButton);
		addKeyValueWithQuote(jsObject, "tab", tabName);
		addKeyValueWithQuote(jsObject, "button", getChartType().name());
		addKeyValue(jsObject, "legendActive",
				(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "legendActive"));
		addKeyValue(jsObject, "generalActive",
				(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "generalActive"));
		addKeyValue(jsObject, "groupActive",
				(boolean) mainConfiguration.getValue(MainConfiguration.GENERAL, "groupActive"));

		return jsObject;

	}

	public String calculateMax() {
		return null;
	}

	public boolean isUseNegativeValues() {
		return useNegativeValues;
	}

	public boolean isCalculateMaxActivated() {
		return Controller.getInstance().getMainConfiguration().getValue(getChartType(), "calculateMax", false);
	}

	public long getSuggestedMax() {
		String maxString = visualizationController.getTextFieldMax().getText();
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
		// TODO Auto-generated method stub

	}

	
}
