package controllers.charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import controllers.charts.ChartType;
import controllers.MainController;
import controllers.ubulogs.GroupByAbstract;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebEngine;
import model.Component;
import model.ComponentEvent;
import model.CourseModule;
import model.EnrolledUser;
import model.GradeItem;
import model.Group;
import model.Section;
import model.Stats;
import util.UtilMethods;

public abstract class Chart {
	protected WebEngine webViewChartsEngine;
	protected ChoiceBox<Group> slcGroup;
	protected ListView<EnrolledUser> listParticipants;
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
	protected boolean useLegend;
	protected boolean useGeneralButton;
	protected boolean useGroupButton;
	protected String optionsVar;

	
	protected static final double OPACITY = 0.2;

	public Chart(MainController mainController, ChartType chartType) {

		this.webViewChartsEngine = mainController.getWebViewChartsEngine();
		this.slcGroup = mainController.getSlcGroup();
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
		this.choiceBoxDate = mainController.getChoiceBoxDate();
		this.datePickerStart = mainController.getDatePickerStart();
		this.datePickerEnd = mainController.getDatePickerEnd();
		this.stats = mainController.getStats();
		this.tvwGradeReport = mainController.getTvwGradeReport();
		this.chartType = chartType;
		
		this.useLegend = true;
	}

	public String getOptionsVar() {
		return optionsVar;
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

	public List<GradeItem> getSelectedGradeItems() {
		return tvwGradeReport.getSelectionModel().getSelectedItems().stream().filter(Objects::nonNull)
				.map(TreeItem::getValue).collect(Collectors.toList());
	}

	public abstract void update();

	public abstract void clear();

	public abstract void hideLegend();

	public abstract String export();
}
