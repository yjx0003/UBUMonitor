package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.algorithm.HierarchicalClustering;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.ActivityCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.GradesCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;
import es.ubu.lsi.ubumonitor.clustering.data.Distance;
import es.ubu.lsi.ubumonitor.clustering.util.Tree;
import es.ubu.lsi.ubumonitor.clustering.util.Tree.Node;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.controllers.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class HierarchicalController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalController.class);

	@FXML
	private WebView webView;
	private WebEngine webEngine;

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

	public void init(MainController mainController) {
		listParticipants = mainController.getListParticipants();

		choiceBoxDistance.getItems().setAll(Distance.values());
		choiceBoxDistance.getSelectionModel().selectFirst();
		choiceBoxDistance.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldValue, newValue) -> hierarchical.setDistance(newValue));

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

		webEngine = webView.getEngine();
		webEngine.load(getClass().getResource("/graphics/DendogramChart.html").toExternalForm());
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
		updateChart(tree);
	}

	private void updateChart(Tree<String> tree) {
		JSObject data = new JSObject();
		JSArray labels = new JSArray();
		JSArray datasets = new JSArray();

		JSArray points = new JSArray();
		Node<String> node = tree.getRoot();
		nodeToJS(points, labels, node);
		JSObject dataset = new JSObject();
		dataset.put("data", points);
		datasets.add(dataset);
		data.put("labels", labels);
		data.put("datasets", datasets);

		LOGGER.debug("Herarchical: {}", data);
		webEngine.executeScript("updateChart(" + data + ")");
	}

	private void nodeToJS(JSArray points, JSArray labels, Node<String> node) {
		labels.addWithQuote(node.getValue());

		JSObject object = new JSObject();
		object.putWithQuote("name", node.getValue());
		if (node.getParentNode() != null)
			object.putWithQuote("parent", node.getParentNode());

		JSArray users = new JSArray();
		addChildrens(users, node);
		object.put("users", users);

		points.add(object);

		for (Node<String> children : node.getChildrens()) {
			nodeToJS(points, labels, children);
		}
	}

	private void addChildrens(JSArray array, Node<String> node) {
		if (node.getChildrens().isEmpty()) {
			array.addWithQuote(node.getValue());
		}
		for (Node<String> children : node.getChildrens()) {
			if (children.getChildrens().isEmpty()) {
				array.addWithQuote(children.getValue());
			} else {
				addChildrens(array, children);
			}
		}
	}

}
