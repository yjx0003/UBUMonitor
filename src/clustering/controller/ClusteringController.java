package clustering.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import model.EnrolledUser;

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

	private GradesCollector gradesCollector;
	private ActivityCollector activityCollector;

	private static final Callback<Item, PropertyEditor<?>> DEFAULT_PROPERTY_EDITOR_FACTORY = new DefaultPropertyEditorFactory();

	public void init(MainController controller) {
		mainController = controller;
		buttonExecute.setOnAction(e -> execute());
		initAlgorithms();
		initCollectors();
		initTable();
	}

	private void initAlgorithms() {
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
		checkComboBoxCluster.setConverter(new IntegerStringConverter() {
			@Override
			public String toString(Integer value) {
				if (value.equals(-1))
					return I18n.get("text.all");
				return super.toString(value);
			}
		});

		columnImage.setCellValueFactory(c -> new SimpleObjectProperty<>(new ImageView(new Image(
				new ByteArrayInputStream(c.getValue().getEnrolledUser().getImageBytes()), 50, 50, true, false))));
		columnName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEnrolledUser().getFullName()));
		columnCluster.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCluster()));
	}

	private void execute() {
		List<EnrolledUser> users = mainController.getListParticipants().getSelectionModel().getSelectedItems();
		Algorithm algorithm = algorithmList.getSelectionModel().getSelectedItem();
		Clusterer<UserData> clusterer = algorithm.getClusterer();

		AlgorithmExecuter executer = new AlgorithmExecuter(clusterer, users);
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
		List<UserData> clusters = executer.execute(collectors);
		LOGGER.debug("Parametros: {}", algorithm.getParameters());

		ObservableList<Integer> items = checkComboBoxCluster.getItems();

		items.setAll(IntStream.range(-1, executer.getNumClusters()).boxed().collect(Collectors.toList()));
		checkComboBoxCluster.getCheckModel().checkAll();
		checkComboBoxCluster.getItemBooleanProperty(0).addListener((obs, oldValue, newValue) -> {
			if (newValue.booleanValue()) {
				checkComboBoxCluster.getCheckModel().checkAll();
			} else {
				checkComboBoxCluster.getCheckModel().clearChecks();
			}
		});
		updateTable(new FilteredList<>(FXCollections.observableList(clusters)));
	}

	private void updateTable(FilteredList<UserData> clusters) {
		SortedList<UserData> sortedList = new SortedList<>(clusters);
		sortedList.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedList);
		checkComboBoxCluster.getCheckModel().getCheckedItems()
				.addListener((ListChangeListener.Change<? extends Integer> c) -> clusters.setPredicate(
						o -> checkComboBoxCluster.getCheckModel().getCheckedItems().contains(o.getCluster())));
	}
}
