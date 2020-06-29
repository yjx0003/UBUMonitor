package es.ubu.lsi.ubumonitor.clustering.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PropertySheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.util.ExportUtil;
import es.ubu.lsi.ubumonitor.clustering.util.TextFieldPropertyEditorFactory;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.converter.IntegerStringConverter;

public class ClusteringTable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringTable.class);

	private static final Controller CONTROLLER = Controller.getInstance();
	
	private static final ObservableList<String> LABELS_LIST = FXCollections.observableArrayList(ConfigHelper.getArray("labels"));

	private MainController mainController;

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

	@FXML
	private Button buttonExport;

	/* Etiquetar */

	@FXML
	private PropertySheet propertySheetLabel;

	@FXML
	private Button buttonLabel;

	@FXML
	private ListView<String> listViewLabels;

	private TextFieldPropertyEditorFactory propertyEditorLabel;

	private List<ClusterWrapper> clusters;

	public void init(MainController controller) {
		this.mainController = controller;
		checkBoxExportGrades.disableProperty()
				.bind(controller.getTvwGradeReport().getSelectionModel().selectedItemProperty().isNull());

		initTable();
		initLabels();
	}

	private void initTable() {
		columnImage.setCellValueFactory(c -> new SimpleObjectProperty<>(new ImageView(new Image(
				new ByteArrayInputStream(c.getValue().getEnrolledUser().getImageBytes()), 50, 50, true, false))));
		columnName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEnrolledUser().getFullName()));
		columnCluster.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCluster().getName()));

		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setAutoHide(true);
		MenuItem info = new MenuItem();
		contextMenu.getItems().setAll(info);

		tableView.setRowFactory(tv -> {
			TableRow<UserData> row = new TableRow<>();
			row.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && !row.isEmpty()) {
					showUserDataInfo(row.getItem());
				} else if (e.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
					info.setOnAction(ev -> showUserDataInfo(row.getItem()));
					info.setText(I18n.get("text.see") + row.getItem().getEnrolledUser().getFullName());
					contextMenu.show(row, e.getScreenX(), e.getScreenY());
				} else {
					contextMenu.hide();
				}
			});
			return row;
		});
		initGradeColumns();
	}

	private void initLabels() {
		listViewLabels.setItems(LABELS_LIST);
		
		propertyEditorLabel = new TextFieldPropertyEditorFactory(listViewLabels.getItems());
		propertySheetLabel.setPropertyEditorFactory(propertyEditorLabel);

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

	private void initGradeColumns() {
		List<Color> colors = Arrays.asList(Color.RED, Color.ORANGE, Color.GREEN, Color.PURPLE);
		TreeView<GradeItem> gradeItem = mainController.getTvwGradeReport();
		gradeItem.getSelectionModel().getSelectedItems().addListener((Change<?> change) -> {
			List<TreeItem<GradeItem>> selected = gradeItem.getSelectionModel().getSelectedItems();
			ObservableList<TableColumn<UserData, ?>> columns = tableView.getColumns();
			columns.remove(3, columns.size());
			for (TreeItem<GradeItem> treeItem : selected) {
				if (treeItem == null)
					continue;

				GradeItem item = treeItem.getValue();
				TableColumn<UserData, Number> column = new TableColumn<>(item.getItemname());
				column.setCellValueFactory(
						c -> new SimpleDoubleProperty(item.getEnrolledUserPercentage(c.getValue().getEnrolledUser())));

				column.setCellFactory(c -> new TableCell<UserData, Number>() {
					@Override
					protected void updateItem(Number item, boolean empty) {
						super.updateItem(item, empty);
						setStyle("-fx-alignment: center-right");
						if (empty || item == null) {
							setText(null);
							setGraphic(null);
						} else {
							setText(String.format("%.2f", item));
							setTextFill(colors.get(item.intValue() / 26));
						}
					}
				});
				columns.add(column);
			}
		});
	}

	public void updateTable(List<ClusterWrapper> clusters) {
		this.clusters = clusters;
		buttonExport.setDisable(false);

		List<UserData> users = clusters.stream().flatMap(List::stream).collect(Collectors.toList());
		FilteredList<UserData> filteredList = new FilteredList<>(FXCollections.observableList(users));
		SortedList<UserData> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedList);
		Map<Integer, Long> count = users.stream().map(UserData::getCluster)
				.collect(Collectors.groupingBy(ClusterWrapper::getId, Collectors.counting()));
		checkComboBoxCluster.setConverter(new IntegerStringConverter() {
			@Override
			public String toString(Integer value) {
				if (value.equals(-1))
					return I18n.get("text.selectall");
				Long n = count.get(value);
				return String.format("%s  (%d/%d)", clusters.get(value).getName(), n == null ? 0 : n,
						count.values().stream().mapToLong(Long::longValue).sum());
			}
		});
		checkComboBoxCluster.getCheckModel().getCheckedItems()
				.addListener((Change<? extends Integer> c) -> filteredList.setPredicate(
						o -> checkComboBoxCluster.getCheckModel().getCheckedItems().contains(o.getCluster().getId())));
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
		tableView.refresh();
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

	public void showUserDataInfo(UserData userData) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClusteringInfo.fxml"),
				I18n.getResourceBundle());
		UtilMethods.createDialog(loader, Controller.getInstance().getStage());
		UserDataController userDataController = loader.getController();
		userDataController.init(userData, tableView);
	}

	@FXML
	private void deleteLabels() {
		listViewLabels.getItems().removeAll(listViewLabels.getSelectionModel().getSelectedItems());
	}

	/**
	 * @return the tableView
	 */
	public TableView<UserData> getTableView() {
		return tableView;
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
	 * @return the checkComboBoxCluster
	 */
	public CheckComboBox<Integer> getCheckComboBoxCluster() {
		return checkComboBoxCluster;
	}

	/**
	 * @return the checkBoxExportGrades
	 */
	public CheckBox getCheckBoxExportGrades() {
		return checkBoxExportGrades;
	}

	/**
	 * @return the buttonExport
	 */
	public Button getButtonExport() {
		return buttonExport;
	}

	/**
	 * @return the propertySheetLabel
	 */
	public PropertySheet getPropertySheetLabel() {
		return propertySheetLabel;
	}

	/**
	 * @return the buttonLabel
	 */
	public Button getButtonLabel() {
		return buttonLabel;
	}

	/**
	 * @return the listViewLabels
	 */
	public ListView<String> getListViewLabels() {
		return listViewLabels;
	}

	/**
	 * @return the propertyEditorLabel
	 */
	public TextFieldPropertyEditorFactory getPropertyEditorLabel() {
		return propertyEditorLabel;
	}
}
