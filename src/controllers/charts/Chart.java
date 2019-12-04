package controllers.charts;

import controllers.JavaConnector.ChartType;
import controllers.MainController;
import controllers.ubulogs.GroupByAbstract;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.web.WebEngine;
import model.Component;
import model.ComponentEvent;
import model.CourseModule;
import model.EnrolledUser;
import model.Section;

public abstract class Chart {
	protected WebEngine webViewChartsEngine;

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
	
	protected ChartType chartType;
	
	public Chart(MainController mainController, ChartType chartType) {
		
		this.webViewChartsEngine = mainController.getWebViewChartsEngine();
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
		this.chartType = chartType;
	}
	
	
	public ChartType getChartType() {
		return chartType;
	}


	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}


	public abstract void update();
	public abstract void clear();
	public abstract void hideLegend();
	public abstract String export();
}
