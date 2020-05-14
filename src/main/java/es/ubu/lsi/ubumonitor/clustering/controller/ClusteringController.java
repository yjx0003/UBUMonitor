package es.ubu.lsi.ubumonitor.clustering.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.RangeSlider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithms;
import es.ubu.lsi.ubumonitor.clustering.analysis.AnalysisFactory;
import es.ubu.lsi.ubumonitor.clustering.analysis.ElbowFactory;
import es.ubu.lsi.ubumonitor.clustering.analysis.SilhouetteFactory;
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.AnalysisMethod;
import es.ubu.lsi.ubumonitor.clustering.chart.Scatter2DChart;
import es.ubu.lsi.ubumonitor.clustering.chart.Scatter3DChart;
import es.ubu.lsi.ubumonitor.clustering.chart.SilhouetteChart;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.ActivityCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.GradesCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.exception.IllegalParamenterException;
import es.ubu.lsi.ubumonitor.clustering.util.ExportUtil;
import es.ubu.lsi.ubumonitor.clustering.util.SimplePropertySheetItem;
import es.ubu.lsi.ubumonitor.clustering.util.TextFieldPropertyEditorFactory;
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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ClusteringController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringController.class);

	private Controller controller = Controller.getInstance();

	private MainController mainController;

	/* Componentes de seleccion */

	@FXML
	private ListView<Algorithm> algorithmList;

	@FXML
	private PropertySheet propertySheet;

	@FXML
	private CheckComboBox<DataCollector> checkComboBoxLogs;

	@FXML
	private CheckBox checkBoxLogs;

	@FXML
	private CheckBox checkBoxGrades;

	@FXML
	private CheckBox checkBoxActivity;

	@FXML
	private CheckBox checkBoxReduce;

	@FXML
	private TextField textFieldReduce;

	@FXML
	private RangeSlider rangeSlider;

	@FXML
	private ChoiceBox<AnalysisFactory> choiceBoxAnalyze;

	@FXML
	private TextField textFieldIterations;

	/* Graficas */

	@FXML
	private WebView webViewScatter;

	@FXML
	private WebView webView3DScatter;

	@FXML
	private WebView webViewSilhouette;

	/* Tabla */
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
	private CheckBox checkBoxExportGrades;

	/* Etiquetar */
	@FXML
	private PropertySheet propertySheetLabel;

	@FXML
	private Button buttonLabel;

	@FXML
	private ListView<String> listViewLabels;

	private GradesCollector gradesCollector;

	private ActivityCollector activityCollector;

	private List<ClusterWrapper> clusters;

	private ClusteringTable table;

	private Scatter2DChart graph;

	private SilhouetteChart silhouette;

	private TextFieldPropertyEditorFactory propertyEditorLabel;

	private Scatter3DChart graph3D;

	public void init(MainController controller) {
		mainController = controller;
		table = new ClusteringTable(this);
		graph = new Scatter2DChart(this);
		silhouette = new SilhouetteChart(this);
		graph3D = new Scatter3DChart(this);

		rangeSlider.setHighValue(10.0);

		choiceBoxAnalyze.getItems().setAll(new ElbowFactory(), new SilhouetteFactory());
		choiceBoxAnalyze.getSelectionModel().selectFirst();

		checkBoxExportGrades.disableProperty()
				.bind(controller.getTvwGradeReport().getSelectionModel().selectedItemProperty().isNull());

		initAlgorithms();
		initCollectors();
		initLabels();
	}

	private void initAlgorithms() {
		textFieldReduce.disableProperty().bind(checkBoxReduce.selectedProperty().not());
		textFieldReduce.textProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue.isEmpty() && !newValue.matches("[1-9]\\d*")) {
				textFieldReduce.setText(oldValue);
			}
		});
		textFieldIterations.textProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue.isEmpty() && !newValue.matches("[1-9]\\d*")) {
				textFieldIterations.setText(oldValue);
			}
		});
		checkComboBoxLogs.disableProperty().bind(checkBoxLogs.selectedProperty().not());
		algorithmList.getItems().setAll(Algorithms.getAlgorithms());
		algorithmList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> propertySheet
				.getItems().setAll(newValue.getParameters().getPropertyItems()));
		algorithmList.getSelectionModel().selectFirst();
	}

	private void initCollectors() {
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
	}

	private void initLabels() {

		propertyEditorLabel = new TextFieldPropertyEditorFactory(listViewLabels.getItems());
		propertySheetLabel.setPropertyEditorFactory(propertyEditorLabel);

		listViewLabels.getItems().setAll(ConfigHelper.getArray("labels"));
		FXCollections.sort(listViewLabels.getItems(), String.CASE_INSENSITIVE_ORDER);
		listViewLabels.getItems().addListener(new ListChangeListener<String>() {
			private boolean flag = true;

			@Override
			public void onChanged(Change<? extends String> c) {
				if (flag) {
					flag = false;
					FXCollections.sort(listViewLabels.getItems(), String.CASE_INSENSITIVE_ORDER);
					propertyEditorLabel.refresh();
					ConfigHelper.setArray("labels", listViewLabels.getItems());
					flag = true;
				}
			}
		});

		listViewLabels.setCellFactory(TextFieldListCell.forListView());
		listViewLabels.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	@FXML
	private void deleteLabels() {
		listViewLabels.getItems().removeAll(listViewLabels.getSelectionModel().getSelectedItems());
	}

	@FXML
	private void execute() {
		List<EnrolledUser> users = mainController.getListParticipants().getSelectionModel().getSelectedItems();
		Algorithm algorithm = algorithmList.getSelectionModel().getSelectedItem();

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
			AlgorithmExecuter algorithmExecuter = new AlgorithmExecuter(algorithm, users, collectors);

			int dim = checkBoxReduce.isSelected() ? Integer.valueOf(textFieldReduce.getText()) : 0;
			if (dim > users.size())
				throw new IllegalStateException("clustering.error.invalidDimension");

			int iter = 10;
			if (!textFieldIterations.getText().isEmpty())
				iter = Integer.valueOf(textFieldIterations.getText());
			clusters = algorithmExecuter.execute(iter, dim);

			LOGGER.debug("Parametros: {}", algorithm.getParameters());
			LOGGER.debug("Clusters: {}", clusters);

			silhouette.setDistanceType(algorithm.getParameters().getValue(ClusteringParameter.DISTANCE_TYPE));
			silhouette.updateChart(clusters);
			table.updateTable(clusters);
			updateRename();
			graph.updateChart(clusters);
			graph3D.updateChart(clusters);

		} catch (IllegalParamenterException e) {
			UtilMethods.errorWindow(e.getMessage());
		} catch (IllegalStateException e) {
			UtilMethods.errorWindow(I18n.get(e.getMessage()));
		} catch (Exception e) {
			UtilMethods.errorWindow("Error", e);
			LOGGER.error("Error en la ejecucion", e);
		}
	}

	private void updateRename() {
		List<PropertySheet.Item> items = IntStream.range(0, clusters.size())
				.mapToObj(i -> new SimplePropertySheetItem(String.valueOf(i), String.valueOf(i)))
				.collect(Collectors.toList());
		propertySheetLabel.getItems().setAll(items);
		buttonLabel.setOnAction(e -> {
			for (int i = 0; i < items.size(); i++) {
				String name = items.get(i).getValue().toString();
				clusters.get(i).setName(name);
				propertyEditorLabel.add(name);
			}
			graph.rename(clusters);
			table.updateTable(clusters);
			graph3D.rename(clusters);
			silhouette.rename(clusters);
		});
	}

	@FXML
	private void exportTable() {
		try {
			FileChooser fileChooser = UtilMethods.createFileChooser(I18n.get("text.exportcsv"),
					String.format("%s_%s_CLUSTERING_TABLE.csv", controller.getActualCourse().getId(),
							LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss"))),
					ConfigHelper.getProperty("csvFolderPath", "./"), new ExtensionFilter("CSV (*.csv)", "*.csv"));
			File file = fileChooser.showSaveDialog(controller.getStage());
			if (file != null) {
				boolean exportGrades = checkBoxExportGrades.isSelected();
				if (exportGrades) {
					List<TreeItem<GradeItem>> treeItems = mainController.getTvwGradeReport().getSelectionModel()
							.getSelectedItems();
					List<GradeItem> grades = treeItems.stream().map(TreeItem<GradeItem>::getValue)
							.collect(Collectors.toList());
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

	@FXML
	private void executeOptimal() {
		Algorithm algorithm = algorithmList.getSelectionModel().getSelectedItem();
		if (algorithm.getParameters().getValue(ClusteringParameter.NUM_CLUSTER) == null) {
			UtilMethods.errorWindow(I18n.get("clustering.invalid"));
			return;
		}
		int start = (int) rangeSlider.getLowValue();
		int end = (int) rangeSlider.getHighValue();

		List<EnrolledUser> users = mainController.getListParticipants().getSelectionModel().getSelectedItems();
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

			AnalysisMethod analysisMethod = choiceBoxAnalyze.getValue().createAnalysis(algorithm);
			List<Double> points = analysisMethod.analyze(start, end, users, collectors);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/OptimalClusters.fxml"));
			UtilMethods.createDialog(loader, Controller.getInstance().getStage());
			AnalysisController controller = loader.getController();
			controller.updateChart(points, start);
		} catch (IllegalParamenterException e) {
			UtilMethods.errorWindow(e.getMessage());
		} catch (IllegalStateException e) {
			UtilMethods.errorWindow(I18n.get(e.getMessage()));
		} catch (Exception e) {
			UtilMethods.errorWindow("Error", e);
			LOGGER.error("Error en la ejecucion", e);
		}
	}

	/**
	 * @return the mainController
	 */
	public MainController getMainController() {
		return mainController;
	}

	/**
	 * @return the propertySheet
	 */
	public PropertySheet getPropertySheet() {
		return propertySheet;
	}

	/**
	 * @return the algorithmList
	 */
	public ListView<Algorithm> getAlgorithmList() {
		return algorithmList;
	}

	/**
	 * @return the webViewScatter
	 */
	public WebView getWebViewScatter() {
		return webViewScatter;
	}

	public WebView getWebView3DScatter() {
		return webView3DScatter;
	}

	/**
	 * @return the webViewSilhouette
	 */
	public WebView getwebViewSilhouette() {
		return webViewSilhouette;
	}

	/**
	 * @return the tableView
	 */
	public TableView<UserData> getTableView() {
		return tableView;
	}

	/**
	 * @return the checkComboBoxLogs
	 */
	public CheckComboBox<DataCollector> getCheckComboBoxLogs() {
		return checkComboBoxLogs;
	}

	/**
	 * @return the checkBoxLogs
	 */
	public CheckBox getCheckBoxLogs() {
		return checkBoxLogs;
	}

	/**
	 * @return the checkBoxGrades
	 */
	public CheckBox getCheckBoxGrades() {
		return checkBoxGrades;
	}

	/**
	 * @return the checkBoxActivity
	 */
	public CheckBox getCheckBoxActivity() {
		return checkBoxActivity;
	}

	/**
	 * @return the checkComboBoxCluster
	 */
	public CheckComboBox<Integer> getCheckComboBoxCluster() {
		return checkComboBoxCluster;
	}

	/**
	 * @return the columnImage
	 */
	public TableColumn<UserData, ImageView> getColumnImage() {
		return columnImage;
	}

	/**
	 * @return the columnName
	 */
	public TableColumn<UserData, String> getColumnName() {
		return columnName;
	}

	/**
	 * @return the columnCluster
	 */
	public TableColumn<UserData, String> getColumnCluster() {
		return columnCluster;
	}

	/**
	 * @return the checkBoxReduce
	 */
	public CheckBox getCheckBoxReduce() {
		return checkBoxReduce;
	}

	/**
	 * @return the textFieldReduce
	 */
	public TextField getTextFieldReduce() {
		return textFieldReduce;
	}

	/**
	 * @return the gradesCollector
	 */
	public GradesCollector getGradesCollector() {
		return gradesCollector;
	}

	/**
	 * @return the activityCollector
	 */
	public ActivityCollector getActivityCollector() {
		return activityCollector;
	}

}
