package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import es.ubu.lsi.ubumonitor.clustering.algorithm.HierarchicalClustering;
import es.ubu.lsi.ubumonitor.clustering.chart.DendogramChart;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.ActivityCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.GradesCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.util.Tree;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.controllers.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;

public class HierarchicalController {

	@FXML
	private WebView webView;

	@FXML
	private CheckComboBox<DataCollector> checkComboBoxLogs;

	@FXML
	private CheckBox checkBoxLogs;

	@FXML
	private CheckBox checkBoxGrades;

	@FXML
	private CheckBox checkBoxActivity;

	@FXML
	private ChoiceBox<Distance> choiceBoxDistance;

	private GradesCollector gradesCollector;

	private ActivityCollector activityCollector;

	private ListView<EnrolledUser> listParticipants;

	private HierarchicalClustering hierarchical = new HierarchicalClustering();

	private DendogramChart dendogramChart;

	public void init(MainController mainController) {
		listParticipants = mainController.getListParticipants();

		choiceBoxDistance.getItems().setAll(Distance.values());
		choiceBoxDistance.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldValue, newValue) -> hierarchical.setDistance(newValue));
		choiceBoxDistance.getSelectionModel().selectFirst();

		gradesCollector = new GradesCollector(mainController);
		activityCollector = new ActivityCollector(mainController);
		List<DataCollector> list = new ArrayList<>();
		list.add(new LogCollector<>("component", mainController.getListViewComponents(), DataSetComponent.getInstance(),
				t -> t.name().toLowerCase()));
		list.add(new LogCollector<>("event", mainController.getListViewEvents(), DataSetComponentEvent.getInstance(),
				t -> t.getComponent().name().toLowerCase()));
		list.add(new LogCollector<>("section", mainController.getListViewSection(), DataSetSection.getInstance(),
				t -> t.isVisible() ? "visible" : "not_visible"));
		list.add(new LogCollector<>("coursemodule", mainController.getListViewCourseModule(),
				DatasSetCourseModule.getInstance(), t -> t.getModuleType().getModName()));
		checkComboBoxLogs.getItems().setAll(list);

		checkComboBoxLogs.disableProperty().bind(checkBoxLogs.selectedProperty().not());

		dendogramChart = new DendogramChart(webView);

	}

	@FXML
	private void execute() {
		List<EnrolledUser> users = listParticipants.getSelectionModel().getSelectedItems();

		List<DataCollector> collectors = new ArrayList<>();
		if (checkBoxLogs.isSelected()) {
			collectors.addAll(checkComboBoxLogs.getCheckModel().getCheckedItems());
		}
		if (checkBoxGrades.isSelected()) {
			collectors.add(gradesCollector);
		}
		if (checkBoxActivity.isSelected()) {
			collectors.add(activityCollector);
		}

		Tree<String> tree = hierarchical.execute(users, collectors);
		dendogramChart.updateChart(tree);
	}

}
