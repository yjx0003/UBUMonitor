package es.ubu.lsi.ubumonitor.clustering.controller;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.controlsfx.control.CheckComboBox;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.I18n;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.converter.IntegerStringConverter;

public class ClusteringTable {

	private TableView<UserData> tableView;
	private TableColumn<UserData, ImageView> columnImage;
	private TableColumn<UserData, String> columnName;
	private TableColumn<UserData, String> columnCluster;
	private TreeView<GradeItem> gradeItem;
	private CheckComboBox<Integer> checkComboBoxCluster;

	public ClusteringTable(ClusteringController controller) {
		tableView = controller.getTableView();
		columnImage = controller.getColumnImage();
		columnName = controller.getColumnName();
		columnCluster = controller.getColumnCluster();
		gradeItem = controller.getMainController().getTvwGradeReport();
		checkComboBoxCluster = controller.getCheckComboBoxCluster();
		init();
	}

	private void init() {
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

	private void initGradeColumns() {
		List<Color> colors = Arrays.asList(Color.RED, Color.ORANGE, Color.GREEN, Color.PURPLE);
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

	public void showUserDataInfo(UserData userData) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ClusteringInfo.fxml"));
		UtilMethods.createDialog(loader, Controller.getInstance().getStage());
		UserDataController controller = loader.getController();
		controller.init(userData, tableView);
	}

}
