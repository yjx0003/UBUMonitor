package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.controlsfx.control.CheckComboBox;

import es.ubu.lsi.ubumonitor.clustering.algorithm.HierarchicalAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.ActivityCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.GradesCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.LinkageMeasure;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.controllers.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import smile.clustering.HierarchicalClustering;
import smile.math.distance.ChebyshevDistance;
import smile.math.distance.Distance;
import smile.math.distance.EuclideanDistance;
import smile.math.distance.ManhattanDistance;
import smile.plot.swing.Canvas;
import smile.plot.swing.Dendrogram;

public class HierarchicalController {

	@FXML
	private ImageView imageView;

	@FXML
	private Pane pane;

	@FXML
	private CheckComboBox<DataCollector> checkComboBoxLogs;

	@FXML
	private CheckBox checkBoxLogs;

	@FXML
	private CheckBox checkBoxGrades;

	@FXML
	private CheckBox checkBoxActivity;

	@FXML
	private ChoiceBox<Distance<double[]>> choiceBoxDistance;

	@FXML
	private ChoiceBox<LinkageMeasure> choiceBoxLinkage;

	/* Tabla */

	@FXML
	private Spinner<Integer> spinnerClusters;

	@FXML
	private Button buttonExecute;

	@FXML
	private TableView<UserData> tableView;

	@FXML
	private TableColumn<UserData, ImageView> columnImage;

	@FXML
	private TableColumn<UserData, String> columnName;

	@FXML
	private TableColumn<UserData, String> columnCluster;

	@FXML
	private CheckComboBox<Integer> checkComboBoxCluster;

	private GradesCollector gradesCollector;

	private ActivityCollector activityCollector;

	private ListView<EnrolledUser> listParticipants;

	private HierarchicalAlgorithm hierarchicalAlgorithm = new HierarchicalAlgorithm();

	private ClusteringTable clusteringTable;

	private HierarchicalClustering hierarchicalClustering;

	@SuppressWarnings("unchecked")
	public void init(MainController mainController) {

		clusteringTable = new ClusteringTable(tableView, columnImage, columnName, columnCluster,
				mainController.getTvwGradeReport(), checkComboBoxCluster);

		listParticipants = mainController.getListParticipants();

		choiceBoxDistance.getItems().setAll(new EuclideanDistance(), new ManhattanDistance(), new ChebyshevDistance());
		choiceBoxDistance.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldValue, newValue) -> hierarchicalAlgorithm.setDistance(newValue));
		choiceBoxDistance.getSelectionModel().selectFirst();

		choiceBoxLinkage.getItems().setAll(LinkageMeasure.values());
		choiceBoxLinkage.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldValue, newValue) -> hierarchicalAlgorithm.setLinkageMeasure(newValue));
		choiceBoxLinkage.getSelectionModel().selectFirst();

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
		checkComboBoxLogs.getCheckModel().checkAll();
		checkComboBoxLogs.disableProperty().bind(checkBoxLogs.selectedProperty().not());

		imageView.fitWidthProperty().bind(pane.widthProperty());
		imageView.fitHeightProperty().bind(pane.heightProperty());

		spinnerClusters.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 3));
		spinnerClusters.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue.matches("^[1-9]\\d{0,4}")) {
				spinnerClusters.getEditor().setText(oldValue);
			} else {
				spinnerClusters.getValueFactory().setValue(Integer.valueOf(newValue));
			}
		});

		ChangeListener<? super Number> listener = (obs, newValue, oldValue) -> setImage();
		pane.widthProperty().addListener(listener);
		pane.heightProperty().addListener(listener);
	}

	private void setImage() {
		if (hierarchicalClustering == null)
			return;

		Dendrogram dendrogram = new Dendrogram(hierarchicalClustering.getTree(), hierarchicalClustering.getHeight());
		Canvas canvas = dendrogram.canvas();
		canvas.setMargin(0.05);
		Image image = SwingFXUtils
				.toFXImage(canvas.toBufferedImage((int) pane.getWidth() + 1, (int) pane.getHeight() + 1), null);
		imageView.setImage(image);
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
		hierarchicalClustering = hierarchicalAlgorithm.execute(users, collectors);
		setImage();
		buttonExecute.setDisable(false);
	}

	@FXML
	private void executePartition() {
		int k = spinnerClusters.getValue();
		List<UserData> usersData = hierarchicalAlgorithm.getUsersData();
		int[] partition = hierarchicalClustering.partition(k);
		List<ClusterWrapper> clusterWarpers = new ArrayList<>();
		List<Cluster<UserData>> clusters = new ArrayList<>();

		for (int i = 0; i < k; i++) {
			clusters.add(new Cluster<>());
		}
		for (int i = 0; i < partition.length; i++) {
			int n = partition[i];
			clusters.get(n).addPoint(usersData.get(i));
		}
		for (int i = 0; i < k; i++) {
			ClusterWrapper clusterWarpper = new ClusterWrapper(i, clusters.get(i));
			clusterWarpers.add(clusterWarpper);
			clusterWarpper.forEach(u -> u.setCluster(clusterWarpper));
		}

		clusteringTable.updateTable(clusterWarpers);
	}

}
