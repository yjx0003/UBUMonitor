package clustering.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clustering.algorithm.Algorithm;
import clustering.algorithm.Algorithms;
import clustering.controller.collector.ActivityCollector;
import clustering.controller.collector.DataCollector;
import clustering.controller.collector.GradesCollector;
import clustering.controller.collector.LogCollector;
import clustering.data.ClusterWrapper;
import clustering.data.UserData;
import clustering.util.CSVClustering;
import clustering.util.SimplePropertySheetItem;
import controllers.Controller;
import controllers.I18n;
import controllers.MainController;
import controllers.configuration.Config;
import controllers.datasets.DataSetComponent;
import controllers.datasets.DataSetComponentEvent;
import controllers.datasets.DataSetSection;
import controllers.datasets.DatasSetCourseModule;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import model.EnrolledUser;
import util.JSArray;
import util.JSObject;
import util.UtilMethods;

public class ClusteringController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringController.class);

	private MainController mainController;

	@FXML
	private PropertySheet propertySheet;

	@FXML
	private ListView<Algorithm> algorithmList;

	@FXML
	private WebView webView;
	private WebEngine webEngine;

	@FXML
	private TableView<UserData> tableView;

	@FXML
	private CheckComboBox<DataCollector> checkComboBoxLogs;

	@FXML
	private CheckBox checkBoxLogs;

	@FXML
	private CheckBox checkBoxGrades;

	@FXML
	private CheckBox checkBoxActivity;

	@FXML
	private CheckComboBox<Integer> checkComboBoxCluster;

	@FXML
	private TableColumn<UserData, ImageView> columnImage;

	@FXML
	private TableColumn<UserData, String> columnName;

	@FXML
	private TableColumn<UserData, String> columnCluster;

	@FXML
	private CheckBox checkBoxReduce;

	@FXML
	private TextField textFieldReduce;

	@FXML
	private PropertySheet propertySheetRename;

	@FXML
	private Button buttonRename;

	private GradesCollector gradesCollector;
	private ActivityCollector activityCollector;

	private Connector connector;

	private List<ClusterWrapper> clusters;

	private MenuItem exportCSV;
	private MenuItem exportPNG;

	private static final Callback<Item, PropertyEditor<?>> DEFAULT_PROPERTY_EDITOR_FACTORY = new DefaultPropertyEditorFactory();

	public void init(MainController controller) {
		mainController = controller;
		initAlgorithms();
		initCollectors();
		initTable();
		initContextMenu();
		webView.setContextMenuEnabled(false);
		webEngine = webView.getEngine();

		connector = new Connector(this);
		webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
			if (Worker.State.SUCCEEDED != newState)
				return;
			netscape.javascript.JSObject window = (netscape.javascript.JSObject) webEngine.executeScript("window");
			window.setMember("javaConnector", connector);
		});
		webEngine.load(getClass().getResource("/graphics/ClusterChart.html").toExternalForm());

	}

	private void initAlgorithms() {
		textFieldReduce.disableProperty().bind(checkBoxReduce.selectedProperty().not());
		textFieldReduce.textProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue.isEmpty() && !newValue.matches("[1-9]\\d*")) {
				textFieldReduce.setText(oldValue);
			}
		});
		checkComboBoxLogs.disableProperty().bind(checkBoxLogs.selectedProperty().not());
		algorithmList.getItems().setAll(Algorithms.getAlgorithms());
		algorithmList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> propertySheet
				.getItems().setAll(newValue.getParameters().getPropertyItems()));
		algorithmList.getSelectionModel().selectFirst();
		StringConverter<DistanceMeasure> stringConverter = new StringConverter<DistanceMeasure>() {

			@Override
			public String toString(DistanceMeasure object) {
				return I18n.get("clustering." + object.getClass().getSimpleName());
			}

			@Override
			public DistanceMeasure fromString(String string) {
				return null;
			}
		};

		propertySheet.setPropertyEditorFactory(item -> {
			if (item.getValue() instanceof DistanceMeasure) {
				@SuppressWarnings("unchecked")
				AbstractPropertyEditor<DistanceMeasure, ComboBox<DistanceMeasure>> editor = (AbstractPropertyEditor<DistanceMeasure, ComboBox<DistanceMeasure>>) Editors
						.createChoiceEditor(item, Algorithms.DISTANCES_LIST);
				editor.getEditor().setConverter(stringConverter);
				return editor;
			}
			return DEFAULT_PROPERTY_EDITOR_FACTORY.call(item);
		});
	}

	private void initCollectors() {
		gradesCollector = new GradesCollector(mainController);
		activityCollector = new ActivityCollector(mainController);
		List<DataCollector> list = new ArrayList<>();
		list.add(new LogCollector<>("component", mainController.getListViewComponents(),
				DataSetComponent.getInstance()));
		list.add(new LogCollector<>("event", mainController.getListViewEvents(), DataSetComponentEvent.getInstance()));
		list.add(new LogCollector<>("section", mainController.getListViewSection(), DataSetSection.getInstance()));
		list.add(new LogCollector<>("coursemodule", mainController.getListViewCourseModule(),
				DatasSetCourseModule.getInstance()));
		checkComboBoxLogs.getItems().setAll(list);
	}

	private void initTable() {
		columnImage.setCellValueFactory(c -> new SimpleObjectProperty<>(new ImageView(new Image(
				new ByteArrayInputStream(c.getValue().getEnrolledUser().getImageBytes()), 50, 50, true, false))));
		columnName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEnrolledUser().getFullName()));
		columnCluster
				.setCellValueFactory(c -> new SimpleStringProperty(clusters.get(c.getValue().getCluster()).getName()));

		tableView.setRowFactory(tv -> {
			TableRow<UserData> row = new TableRow<>();
			row.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && !row.isEmpty())
					showUserDataInfo(row.getItem());
			});
			return row;
		});
	}

	private void showUserDataInfo(UserData userData) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClusteringInfo.fxml"));
			Scene scene = new Scene(loader.load());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(Controller.getInstance().getStage());
			stage.setTitle(userData.getEnrolledUser().getFullName());
			UserDataController controller = loader.getController();
			controller.init(userData);
			stage.show();
		} catch (IOException e) {
			LOGGER.error("Error", e);
		}
	}

	private void initContextMenu() {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setAutoHide(true);

		exportCSV = new MenuItem("Export CSV");
		exportCSV.setOnAction(e -> exportPoints());
		exportPNG = new MenuItem("Export PNG");
		exportPNG.setOnAction(e -> exportPNG());
		contextMenu.getItems().setAll(exportCSV, exportPNG);
		webView.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY)
				contextMenu.show(webView, e.getScreenX(), e.getScreenY());
		});
	}

	@FXML
	private void execute() {
		List<EnrolledUser> users = mainController.getListParticipants().getSelectionModel().getSelectedItems();
		Algorithm algorithm = algorithmList.getSelectionModel().getSelectedItem();
		Clusterer<UserData> clusterer = algorithm.getClusterer();

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

		AlgorithmExecuter algorithmExecuter = new AlgorithmExecuter(clusterer, users, collectors);

		int dim = checkBoxReduce.isSelected() ? Integer.valueOf(textFieldReduce.getText()) : 0;
		clusters = algorithmExecuter.execute(dim);
		connector.setClusters(clusters);

		LOGGER.debug("Parametros: {}", algorithm.getParameters());
		LOGGER.debug("Clusters: {}", clusters);

		updateTable();
		updateRename();

		ObservableList<Integer> items = checkComboBoxCluster.getItems();
		items.setAll(IntStream.range(-1, clusters.size()).boxed().collect(Collectors.toList()));
		checkComboBoxCluster.getCheckModel().checkAll();
		checkComboBoxCluster.getItemBooleanProperty(0).addListener((obs, oldValue, newValue) -> {
			if (newValue.booleanValue()) {
				checkComboBoxCluster.getCheckModel().checkAll();
			} else {
				checkComboBoxCluster.getCheckModel().clearChecks();
			}
		});
		updateChart();
	}

	private void updateRename() {
		List<PropertySheet.Item> items = IntStream.range(0, clusters.size())
				.mapToObj(i -> new SimplePropertySheetItem(String.valueOf(i), String.valueOf(i)))
				.collect(Collectors.toList());
		propertySheetRename.getItems().setAll(items);
		buttonRename.setOnAction(e -> {
			for (int i = 0; i < items.size(); i++) {
				clusters.get(i).setName(items.get(i).getValue().toString());
			}
			updateChart();
			updateTable();
		});
	}

	private void updateTable() {
		List<UserData> users = clusters.stream().flatMap(List::stream).collect(Collectors.toList());
		FilteredList<UserData> filteredList = new FilteredList<>(FXCollections.observableList(users));
		SortedList<UserData> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedList);
		Map<Integer, Long> count = users.stream()
				.collect(Collectors.groupingBy(UserData::getCluster, Collectors.counting()));
		checkComboBoxCluster.setConverter(new IntegerStringConverter() {
			@Override
			public String toString(Integer value) {
				if (value.equals(-1))
					return I18n.get("text.all");
				Long n = count.get(value);
				return clusters.get(value).getName() + "  (" + (n == null ? 0 : n) + "/"
						+ count.values().stream().mapToLong(Long::longValue).sum() + ")";
			}
		});
		checkComboBoxCluster.getCheckModel().getCheckedItems()
				.addListener((ListChangeListener.Change<? extends Integer> c) -> filteredList.setPredicate(
						o -> checkComboBoxCluster.getCheckModel().getCheckedItems().contains(o.getCluster())));
		tableView.refresh();
	}

	private void updateChart() {
		List<Map<UserData, double[]>> points = AlgorithmExecuter.clustersTo2D(clusters);
		LOGGER.debug("Puntos: {}", points);
		JSObject root = new JSObject();
		JSArray datasets = new JSArray();
		for (int i = 0; i < points.size(); i++) {
			JSObject group = new JSObject();
			group.putWithQuote("label", clusters.get(i).getName());
			group.put("backgroundColor", "colorHash.hex(" + i * i + ")");
			group.put("pointRadius", 6);
			group.put("pointHoverRadius", 8);
			JSArray data = new JSArray();
			for (Map.Entry<UserData, double[]> userEntry : points.get(i).entrySet()) {
				JSObject coord = new JSObject();
				coord.putWithQuote("user", userEntry.getKey().getEnrolledUser().getFullName());
				coord.put("x", userEntry.getValue()[0]);
				coord.put("y", userEntry.getValue()[1]);
				data.add(coord);
			}
			group.put("data", data);
			datasets.add(group);
		}
		root.put("datasets", datasets);
		LOGGER.debug("Data: {}", root);
		webEngine.executeScript("updateChart(" + root + ")");

	}

	@FXML
	private void exportTable() {

		try {
			File file = selectFile(new ExtensionFilter("CSV (*.csv)", "*.csv"));
			if (file != null) {
				CSVClustering.exportTable(clusters, file.toPath());
				UtilMethods.infoWindow(I18n.get("message.export_csv_success") + file.getAbsolutePath());
			}
		} catch (Exception e) {
			LOGGER.error("Error al exportar ficheros CSV.", e);
			UtilMethods.errorWindow(I18n.get("error.savecsvfiles"), e);
		}
	}

	private void exportPoints() {
		try {
			File file = selectFile(new ExtensionFilter("CSV (*.csv)", "*.csv"));
			if (file != null) {
				CSVClustering.exportPoints(clusters, file.toPath());
				UtilMethods.infoWindow(I18n.get("message.export_csv_success") + file.getAbsolutePath());
			}
		} catch (Exception e) {
			LOGGER.error("Error al exportar ficheros CSV.", e);
			UtilMethods.errorWindow(I18n.get("error.savecsvfiles"), e);
		}
	}

	private void exportPNG() {
		try {
			File file = selectFile(new ExtensionFilter("PNG (*.png)", "*.png"));
			if (file != null) {
				connector.export(file);
				UtilMethods.infoWindow(I18n.get("message.export_csv_success") + file.getAbsolutePath());
			}
		} catch (IOException e) {
			LOGGER.error("Error al exportar ficheros CSV.", e);
			UtilMethods.errorWindow(I18n.get("error.savecsvfiles"), e);
		}
	}

	private File selectFile(ExtensionFilter extension) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(extension);
		File file = new File(Config.getProperty("csvFolderPath", "./"));
		if (file.exists() && file.isDirectory()) {
			fileChooser.setInitialDirectory(file);
		}
		fileChooser.setInitialFileName("clustering_" + CSVClustering.DTF.format(LocalDateTime.now()));
		return fileChooser.showSaveDialog(mainController.getController().getStage());
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
	 * @return the webView
	 */
	public WebView getWebView() {
		return webView;
	}

	/**
	 * @return the webEngine
	 */
	public WebEngine getWebEngine() {
		return webEngine;
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
