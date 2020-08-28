package es.ubu.lsi.ubumonitor.clustering.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PropertySheet;
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
import es.ubu.lsi.ubumonitor.clustering.util.JavaFXUtils;
import es.ubu.lsi.ubumonitor.clustering.util.SimplePropertySheetItem;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.model.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.StringConverter;
import smile.clustering.HierarchicalClustering;
import smile.math.distance.ChebyshevDistance;
import smile.math.distance.Distance;
import smile.math.distance.EuclideanDistance;
import smile.math.distance.ManhattanDistance;
import smile.plot.swing.Canvas;
import smile.plot.swing.Dendrogram;

/**
 * Controlador del clustering jerárquico.
 * 
 * @author Xing Long Ji
 *
 */
public class HierarchicalController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HierarchicalController.class);

	private static final Controller CONTROLLER = Controller.getInstance();

	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

	@FXML
	private ClusteringTable clusteringTableController;

	@FXML
	private ImageView imageView;

	@FXML
	private Pane pane;

	@FXML
	private CheckComboBox<LogCollector<?>> checkComboBoxLogs;

	@FXML
	private CheckBox checkBoxLogs;

	@FXML
	private CheckBox checkBoxGrades;

	@FXML
	private CheckBox checkBoxActivity;
	
	@FXML
	private DatePicker datePickerStart;

	@FXML
	private DatePicker datePickerEnd;

	@FXML
	private ChoiceBox<Distance<double[]>> choiceBoxDistance;

	@FXML
	private ChoiceBox<LinkageMeasure> choiceBoxLinkage;

	@FXML
	private Spinner<Integer> spinnerClusters;

	@FXML
	private Button buttonExecute;

	private GradesCollector gradesCollector;

	private ActivityCollector activityCollector;

	private ListView<EnrolledUser> listParticipants;

	private HierarchicalAlgorithm hierarchicalAlgorithm = new HierarchicalAlgorithm();

	private HierarchicalClustering hierarchicalClustering;

	/**
	 * Inicializa el controllador.
	 * 
	 * @param mainController controllador general
	 */
	public void init(MainController mainController) {

		clusteringTableController.init(mainController);

		listParticipants = mainController.getSelectionUserController().getListParticipants();
		choiceBoxDistance.setConverter(new StringConverter<Distance<double[]>>() {

			@Override
			public String toString(Distance<double[]> object) {
				return I18n.get("clustering.distance." + object.toString().replace(' ', '_').toLowerCase());
			}

			@Override
			public Distance<double[]> fromString(String string) {
				return null;
			}
		});
		List<Distance<double[]>> distances = Arrays.asList(new EuclideanDistance(), new ManhattanDistance(),
				new ChebyshevDistance());
		choiceBoxDistance.getItems().setAll(distances);
		choiceBoxDistance.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldValue, newValue) -> hierarchicalAlgorithm.setDistance(newValue));
		choiceBoxDistance.getSelectionModel().selectFirst();

		choiceBoxLinkage.getItems().setAll(LinkageMeasure.values());
		choiceBoxLinkage.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldValue, newValue) -> hierarchicalAlgorithm.setLinkageMeasure(newValue));
		choiceBoxLinkage.getSelectionModel().selectFirst();

		gradesCollector = new GradesCollector(mainController);
		activityCollector = new ActivityCollector(mainController);
		List<LogCollector<?>> list = new ArrayList<>();
		list.add(new LogCollector<>("component", mainController.getSelectionController().getListViewComponents(), DataSetComponent.getInstance(),
				t -> t.name().toLowerCase()));
		list.add(new LogCollector<>("event", mainController.getSelectionController().getListViewEvents(), DataSetComponentEvent.getInstance(),
				t -> t.getComponent().name().toLowerCase()));
		list.add(new LogCollector<>("section", mainController.getSelectionController().getListViewSection(), DataSetSection.getInstance(),
				t -> t.isVisible() ? "visible" : "not_visible"));
		list.add(new LogCollector<>("coursemodule", mainController.getSelectionController().getListViewCourseModule(),
				DatasSetCourseModule.getInstance(), t -> t.getModuleType().getModName()));

		checkComboBoxLogs.getItems().setAll(list);
		checkComboBoxLogs.getCheckModel().checkAll();
		checkComboBoxLogs.disableProperty().bind(checkBoxLogs.selectedProperty().not());

		spinnerClusters.setValueFactory(new IntegerSpinnerValueFactory(2, 9999, 2));
		spinnerClusters.getEditor().textProperty().addListener(JavaFXUtils.getSpinnerListener(spinnerClusters));

		ChangeListener<? super Number> listener = (obs, newValue, oldValue) -> setImage();
		pane.widthProperty().addListener(listener);
		pane.heightProperty().addListener(listener);

		initContextMenu(imageView);
		JavaFXUtils.initDatePickers(datePickerStart, datePickerEnd, checkBoxLogs);
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

	/**
	 * Ejecuta el clustering jerárquico.
	 */
	public void executeClustering() {
		List<EnrolledUser> users = listParticipants.getSelectionModel().getSelectedItems();

		List<DataCollector> collectors = new ArrayList<>();
		if (checkBoxLogs.isSelected()) {
			List<LogCollector<?>> logCollectors = checkComboBoxLogs.getCheckModel().getCheckedItems();
			logCollectors.forEach(c -> c.setDate(datePickerStart.getValue(), datePickerEnd.getValue()));
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

	/**
	 * Ejecuta la partición.
	 */
	public void executePartition() {
		int k = spinnerClusters.getValue();
		LOGGER.debug("Partition, number of clusters: {}", k);
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

		LOGGER.debug("Partition, clusters: {}", clusterWarpers);
		clusteringTableController.updateTable(clusterWarpers);
		updateRename(clusterWarpers);
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

	private void exportPNG() {
		try {
			String fileName = String.format("%s_%s_HIERARCHICAL.png", CONTROLLER.getActualCourse().getId(),
					LocalDateTime.now().format(DTF));

			FileChooser fileChooser = UtilMethods.createFileChooser(fileName,
					ConfigHelper.getProperty("csvFolderPath", "./"), new ExtensionFilter("PNG (*.png)", "*.png"));

			File file = fileChooser.showSaveDialog(CONTROLLER.getStage());
			if (file != null) {
				exportImage(file);
				UtilMethods.infoWindow(I18n.get("message.export_png") + file.getAbsolutePath());
			}
		} catch (IOException e) {
			LOGGER.error("Error in export", e);
			UtilMethods.errorWindow(I18n.get("error.savechart"), e);
		}
	}

	private void exportImage(File file) throws IOException {
		WritableImage image = imageView.snapshot(new SnapshotParameters(), null);
		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	}

	private void updateRename(List<ClusterWrapper> clusters) {
		List<PropertySheet.Item> items = IntStream.range(0, clusters.size())
				.mapToObj(i -> new SimplePropertySheetItem(String.valueOf(i), String.valueOf(i)))
				.collect(Collectors.toList());
		clusteringTableController.getPropertySheetLabel().getItems().setAll(items);
		clusteringTableController.getButtonLabel().setOnAction(e -> {
			for (int i = 0; i < items.size(); i++) {
				String name = items.get(i).getValue().toString();
				clusters.get(i).setName(name);
				clusteringTableController.getPropertyEditorLabel().add(name);
			}
			clusteringTableController.updateTable(clusters);
		});
	}

}
