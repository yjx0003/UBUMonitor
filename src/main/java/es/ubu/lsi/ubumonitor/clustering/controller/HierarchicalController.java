package es.ubu.lsi.ubumonitor.clustering.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.algorithm.HierarchicalAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.ActivityCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.GradesCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.LinkageMeasure;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.util.ExportUtil;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.controllers.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.controllers.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import smile.clustering.HierarchicalClustering;
import smile.math.distance.ChebyshevDistance;
import smile.math.distance.Distance;
import smile.math.distance.EuclideanDistance;
import smile.math.distance.ManhattanDistance;
import smile.plot.swing.Canvas;
import smile.plot.swing.Dendrogram;

public class HierarchicalController extends AbstractController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalController.class);

	private static final Controller CONTROLLER = Controller.getInstance();

	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

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
	private CheckBox checkBoxExportGrades;

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
	
	@FXML
	private Button buttonExport;

	private GradesCollector gradesCollector;

	private ActivityCollector activityCollector;

	private ListView<EnrolledUser> listParticipants;

	private HierarchicalAlgorithm hierarchicalAlgorithm = new HierarchicalAlgorithm();

	private ClusteringTable clusteringTable;

	private HierarchicalClustering hierarchicalClustering;

	private List<ClusterWrapper> clusters;

	private MainController mainController;

	@SuppressWarnings("unchecked")
	public void init(MainController mainController) {
		this.mainController = mainController;

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

		spinnerClusters.setValueFactory(new IntegerSpinnerValueFactory(1, 9999, 3));
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

		checkBoxExportGrades.disableProperty()
				.bind(mainController.getTvwGradeReport().getSelectionModel().selectedItemProperty().isNull());

		initContextMenu(imageView);
	}

	private void initContextMenu(ImageView imageView) {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setAutoHide(true);

		MenuItem exportPNG = new MenuItem(I18n.get("text.exportpng"));
		exportPNG.setOnAction(e -> exportPNG());

		contextMenu.getItems().setAll(exportPNG);
		imageView.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				contextMenu.show(imageView, e.getScreenX(), e.getScreenY());
			} else {
				contextMenu.hide();
			}
		});
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
		try {
			hierarchicalClustering = hierarchicalAlgorithm.execute(users, collectors);
			setImage();
			buttonExecute.setDisable(false);
			spinnerClusters.setDisable(false);
			IntegerSpinnerValueFactory isvf = (IntegerSpinnerValueFactory) spinnerClusters.getValueFactory();
			isvf.setMax(users.size());
		} catch (IllegalStateException e) {
			UtilMethods.infoWindow(I18n.get(e.getMessage()));
		}

	}

	@FXML
	private void executePartition() {
		int k = spinnerClusters.getValue();
		List<UserData> usersData = hierarchicalAlgorithm.getUsersData();
		int[] partition = hierarchicalClustering.partition(k);
		List<ClusterWrapper> clusterWarpers = new ArrayList<>();
		List<Cluster<UserData>> clusterList = new ArrayList<>();

		for (int i = 0; i < k; i++) {
			clusterList.add(new Cluster<>());
		}
		for (int i = 0; i < partition.length; i++) {
			int n = partition[i];
			clusterList.get(n).addPoint(usersData.get(i));
		}
		for (int i = 0; i < k; i++) {
			ClusterWrapper clusterWarpper = new ClusterWrapper(i, clusterList.get(i));
			clusterWarpers.add(clusterWarpper);
			clusterWarpper.forEach(u -> u.setCluster(clusterWarpper));
		}
		this.clusters = clusterWarpers;
		clusteringTable.updateTable(clusterWarpers);
		buttonExport.setDisable(false);
	}

	private void exportPNG() {
		try {
			String fileName = String.format("%s_%s_HIERARCHICAL.png", CONTROLLER.getActualCourse().getId(),
					LocalDateTime.now().format(DTF));

			FileChooser fileChooser = UtilMethods.createFileChooser(I18n.get("text.exportpng"), fileName,
					ConfigHelper.getProperty("csvFolderPath", "./"), new ExtensionFilter("PNG (*.png)", "*.png"));

			File file = fileChooser.showSaveDialog(CONTROLLER.getStage());
			if (file != null) {
				exportImage(file);
				UtilMethods.infoWindow(I18n.get("message.export_png") + file.getAbsolutePath());
			}
		} catch (IOException e) {
			UtilMethods.errorWindow(I18n.get("error.savechart"), e);
		}
	}

	private void exportImage(File file) throws IOException {
		WritableImage image = imageView.snapshot(new SnapshotParameters(), null);
		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	}

	@FXML
	private void exportTable() {
		try {
			FileChooser fileChooser = UtilMethods.createFileChooser(I18n.get("text.exportcsv"),
					String.format("%s_%s_CLUSTERING_TABLE.csv", CONTROLLER.getActualCourse().getId(),
							LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss"))),
					ConfigHelper.getProperty("csvFolderPath", "./"), new ExtensionFilter("CSV (*.csv)", "*.csv"));
			File file = fileChooser.showSaveDialog(CONTROLLER.getStage());
			if (file != null) {
				boolean exportGrades = checkBoxExportGrades.isSelected();
				if (exportGrades) {
					List<TreeItem<GradeItem>> treeItems = mainController.getTvwGradeReport().getSelectionModel()
							.getSelectedItems();
					List<GradeItem> grades = treeItems.stream().map(TreeItem::getValue).collect(Collectors.toList());
					ExportUtil.exportClustering(file, clusters, grades.toArray(new GradeItem[0]));
				} else {
					ExportUtil.exportClustering(file, clusters);
				}
				UtilMethods.infoWindow(I18n.get("message.export_csv") + file.getAbsolutePath());
			}
		} catch (Exception e) {
			LOGGER.error("Error al exportar el fichero CSV.", e);
			UtilMethods.errorWindow(I18n.get("error.savecsvfiles"), e);
		}
	}

}
