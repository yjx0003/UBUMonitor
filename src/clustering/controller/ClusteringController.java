package clustering.controller;

import java.io.ByteArrayInputStream;
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
import clustering.data.UserData;
import controllers.I18n;
import controllers.MainController;
import controllers.datasets.DataSetComponent;
import controllers.datasets.DataSetComponentEvent;
import controllers.datasets.DataSetSection;
import controllers.datasets.DatasSetCourseModule;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import model.EnrolledUser;
import util.JSArray;
import util.JSObject;

public class ClusteringController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringController.class);

	private MainController mainController;

	@FXML
	private PropertySheet propertySheet;

	@FXML
	private ListView<Algorithm> algorithmList;

	@FXML
	private Button buttonExecute;

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
	private TableColumn<UserData, Number> columnCluster;

	@FXML
	private CheckBox checkBoxReduce;

	@FXML
	private TextField textFieldReduce;

	private GradesCollector gradesCollector;
	private ActivityCollector activityCollector;

	private Connector connector;

	private static final Callback<Item, PropertyEditor<?>> DEFAULT_PROPERTY_EDITOR_FACTORY = new DefaultPropertyEditorFactory();

	public void init(MainController controller) {
		mainController = controller;
		buttonExecute.setOnAction(e -> execute());
		initAlgorithms();
		initCollectors();
		initTable();
		connector = new Connector(this);
		webView.setContextMenuEnabled(false);
		webEngine = webView.getEngine();

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
		columnCluster.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCluster()));
	}

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
		List<List<UserData>> clusters = algorithmExecuter.execute(dim);
		connector.setClusters(clusters);

		LOGGER.debug("Parametros: {}", algorithm.getParameters());
		LOGGER.debug(clusters.toString());

		updateTable(clusters);

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
		webEngine.executeScript("updateChart(" + getChartData(clusters) + ")");
	}

	private void updateTable(List<List<UserData>> clusters) {
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
				return value + "  (" + (n == null ? 0 : n) + "/" + clusters.size() + ")";
			}
		});
		checkComboBoxCluster.getCheckModel().getCheckedItems()
				.addListener((ListChangeListener.Change<? extends Integer> c) -> filteredList.setPredicate(
						o -> checkComboBoxCluster.getCheckModel().getCheckedItems().contains(o.getCluster())));
	}

	private String getChartData(List<List<UserData>> clusters) {
		List<Map<UserData, double[]>> points = AlgorithmExecuter.clustersTo2D(clusters);
		LOGGER.debug("Puntos: {}", points);
		JSObject root = new JSObject();
		JSArray datasets = new JSArray();
		for (int i = 0; i < points.size(); i++) {
			JSObject group = new JSObject();
			group.put("label", i);
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
		return root.toString();
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
	 * @return the buttonExecute
	 */
	public Button getButtonExecute() {
		return buttonExecute;
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
	public TableColumn<UserData, Number> getColumnCluster() {
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
