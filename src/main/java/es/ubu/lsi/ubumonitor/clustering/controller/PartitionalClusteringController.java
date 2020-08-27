package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.RangeSlider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.apache.DBSCAN;
import es.ubu.lsi.ubumonitor.clustering.algorithm.apache.FuzzyKMeans;
import es.ubu.lsi.ubumonitor.clustering.algorithm.apache.KMeansPlusPlus;
import es.ubu.lsi.ubumonitor.clustering.algorithm.apache.MultiKMeansPlusPlus;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.DBSCANSmile;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.DENCLUE;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.GMeans;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.KMeans;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.XMeans;
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
import es.ubu.lsi.ubumonitor.clustering.exception.IllegalParamenterException;
import es.ubu.lsi.ubumonitor.clustering.util.JavaFXUtils;
import es.ubu.lsi.ubumonitor.clustering.util.SimplePropertySheetItem;
import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.model.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;

/**
 * Controlador del clustering particional.
 * 
 * @author Xing Long Ji
 *
 */
public class PartitionalClusteringController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PartitionalClusteringController.class);

	private MainController mainController;

	@FXML
	private ClusteringTable clusteringTableController;

	/* Componentes de seleccion */

	@FXML
	private ListView<Algorithm> algorithmList;

	@FXML
	private PropertySheet propertySheet;

	@FXML
	private CheckComboBox<LogCollector<?>> checkComboBoxLogs;

	@FXML
	private CheckBox checkBoxLogs;

	@FXML
	private CheckBox checkBoxGrades;

	@FXML
	private CheckBox checkBoxActivity;

	@FXML
	private CheckBox checkBoxReduce;

	@FXML
	private Spinner<Integer> spinnerReduce;

	@FXML
	private RangeSlider rangeSlider;

	@FXML
	private ChoiceBox<AnalysisFactory> choiceBoxAnalyze;

	@FXML
	private Spinner<Integer> spinnerIterations;

	@FXML
	private CheckBox checkBoxFilter;

	@FXML
	private Button buttonExecute;

	@FXML
	private ProgressIndicator progressExecute;
	
	@FXML
	private DatePicker datePickerStart;
	
	@FXML
	private DatePicker datePickerEnd;

	/* Graficas */

	@FXML
	private WebView webViewScatter;

	@FXML
	private WebView webView3DScatter;

	@FXML
	private WebView webViewSilhouette;

	private GradesCollector gradesCollector;

	private ActivityCollector activityCollector;

	private List<ClusterWrapper> clusters;

	private Scatter2DChart graph;

	private SilhouetteChart silhouette;

	private Scatter3DChart graph3D;

	private Service<Void> service;

	/**
	 * Inicializa el controlador.
	 * 
	 * @param controller controlador general
	 */
	public void init(MainController controller) {
		mainController = controller;
		clusteringTableController.init(controller);
		graph = new Scatter2DChart(this);
		silhouette = new SilhouetteChart(this);
		graph3D = new Scatter3DChart(this);

		rangeSlider.setHighValue(10.0);

		choiceBoxAnalyze.getItems().setAll(new ElbowFactory(), new SilhouetteFactory());
		choiceBoxAnalyze.getSelectionModel().selectFirst();

		initAlgorithms();
		initCollectors();
		initService();
	}

	private void initAlgorithms() {
		spinnerReduce.disableProperty().bind(checkBoxReduce.selectedProperty().not());
		spinnerReduce.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999));
		spinnerReduce.getEditor().textProperty().addListener(JavaFXUtils.getSpinnerListener(spinnerReduce));

		spinnerIterations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 20));
		spinnerIterations.getEditor().textProperty().addListener(JavaFXUtils.getSpinnerListener(spinnerIterations));

		checkComboBoxLogs.disableProperty().bind(checkBoxLogs.selectedProperty().not());

		algorithmList.setCellFactory(callback -> new ListCell<Algorithm>() {
			@Override
			public void updateItem(Algorithm algorithm, boolean empty) {
				super.updateItem(algorithm, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					setText(algorithm.getName() + " (" + algorithm.getLibrary() + ")");
					try {
						Image image = new Image(AppInfo.IMG_DIR + algorithm.getLibrary().toLowerCase() + ".png", 24, 24,
								false, true);
						ImageView imageView = new ImageView(image);
						setGraphic(imageView);
					} catch (Exception e) {
						setGraphic(null);
					}
				}
			}
		});

		List<Algorithm> algorithms = Arrays.asList(new KMeansPlusPlus(), new FuzzyKMeans(), new DBSCAN(),
				new MultiKMeansPlusPlus(), new KMeans(), new XMeans(), new GMeans(), new DBSCANSmile(), new DENCLUE()
		/* ,new Spectral(), new DeterministicAnnealing() */);
		algorithmList.getItems().setAll(algorithms);
		algorithmList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> propertySheet
				.getItems().setAll(newValue.getParameters().getPropertyItems()));
		algorithmList.getSelectionModel().selectFirst();
	}

	private void initCollectors() {
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
		
		JavaFXUtils.initDatePickers(datePickerStart, datePickerEnd, checkBoxLogs);
	}

	private void initService() {
		service = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						List<EnrolledUser> users = mainController.getSelectionUserController().getListParticipants().getSelectionModel()
								.getSelectedItems();
						Algorithm algorithm = algorithmList.getSelectionModel().getSelectedItem();

						List<DataCollector> collectors = getSelectedCollectors();

						AlgorithmExecuter algorithmExecuter = new AlgorithmExecuter(algorithm, users, collectors);

						int dim = checkBoxReduce.isSelected() ? spinnerReduce.getValue() : 0;
						int iter = spinnerIterations.getValue();
						clusters = algorithmExecuter.execute(iter, dim, checkBoxFilter.isSelected());

						silhouette
								.setDistanceType(algorithm.getParameters().getValue(ClusteringParameter.DISTANCE_TYPE));
						LOGGER.debug("Parametros: {}", algorithm.getParameters());
						LOGGER.debug("Clusters: {}", clusters);
						return null;
					}
				};
			}
		};
		buttonExecute.disableProperty().bind(service.runningProperty());
		progressExecute.visibleProperty().bind(service.runningProperty());
		service.setOnSucceeded(e -> {
			silhouette.updateChart(clusters);
			clusteringTableController.updateTable(clusters);
			updateRename();
			graph.updateChart(clusters);
			graph3D.updateChart(clusters);
			service.reset();
		});
		service.setOnFailed(e -> {
			Throwable exception = service.getException();
			UtilMethods.infoWindow(exception instanceof IllegalParamenterException ? exception.getMessage()
					: I18n.get(exception.getMessage()));
			service.reset();
		});
	}

	/**
	 * Ejecuta el algoritmo de clustering.
	 */
	public void executeClustering() {
		service.start();
	}

	private void updateRename() {
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
			graph.rename(clusters);
			graph3D.rename(clusters);
			silhouette.rename(clusters);
		});
	}

	/**
	 * Ejecuta el analisis.
	 */
	public void executeAnalysis() {
		Algorithm algorithm = algorithmList.getSelectionModel().getSelectedItem();
		if (algorithm.getParameters().getValue(ClusteringParameter.NUM_CLUSTER) == null) {
			UtilMethods.errorWindow(I18n.get("clustering.invalid"));
			return;
		}
		int start = (int) rangeSlider.getLowValue();
		int end = (int) rangeSlider.getHighValue();

		List<EnrolledUser> users = mainController.getSelectionUserController().getListParticipants().getSelectionModel().getSelectedItems();
		List<DataCollector> collectors = getSelectedCollectors();
		AnalysisMethod analysisMethod = choiceBoxAnalyze.getValue().createAnalysis(algorithm);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/OptimalClusters.fxml"));
		UtilMethods.createDialog(loader, Controller.getInstance().getStage());
		AnalysisController controller = loader.getController();
		controller.setUp(analysisMethod, users, collectors, start, end);
	}

	private List<DataCollector> getSelectedCollectors() {
		List<DataCollector> collectors = new ArrayList<>();
		if (checkBoxLogs.isSelected()) {
			List<LogCollector<?>> logCollectors = checkComboBoxLogs.getCheckModel().getCheckedItems();
			logCollectors.forEach(c -> c.setDate(datePickerStart.getValue(), datePickerEnd.getValue()));
			collectors.addAll(logCollectors);
		}
		if (checkBoxGrades.isSelected()) {
			collectors.add(gradesCollector);
		}
		if (checkBoxActivity.isSelected()) {
			collectors.add(activityCollector);
		}
		return collectors;
	}

	/**
	 * @return the clusteringTable
	 */
	public ClusteringTable getClusteringTable() {
		return clusteringTableController;
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
	 * @return the checkComboBoxLogs
	 */
	public CheckComboBox<LogCollector<?>> getCheckComboBoxLogs() {
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
	 * @return the checkBoxReduce
	 */
	public CheckBox getCheckBoxReduce() {
		return checkBoxReduce;
	}

	/**
	 * @return the spinnerReduce
	 */
	public Spinner<Integer> getSpinnerReduce() {
		return spinnerReduce;
	}

	/**
	 * @return the rangeSlider
	 */
	public RangeSlider getRangeSlider() {
		return rangeSlider;
	}

	/**
	 * @return the choiceBoxAnalyze
	 */
	public ChoiceBox<AnalysisFactory> getChoiceBoxAnalyze() {
		return choiceBoxAnalyze;
	}

	/**
	 * @return the spinnerIterations
	 */
	public Spinner<Integer> getSpinnerIterations() {
		return spinnerIterations;
	}

	/**
	 * @return the buttonExecute
	 */
	public Button getButtonExecute() {
		return buttonExecute;
	}

	/**
	 * @return the progressExecute
	 */
	public ProgressIndicator getProgressExecute() {
		return progressExecute;
	}

	/**
	 * @return the webViewScatter
	 */
	public WebView getWebViewScatter() {
		return webViewScatter;
	}

	/**
	 * @return the webView3DScatter
	 */
	public WebView getWebView3DScatter() {
		return webView3DScatter;
	}

	/**
	 * @return the webViewSilhouette
	 */
	public WebView getWebViewSilhouette() {
		return webViewSilhouette;
	}

}
