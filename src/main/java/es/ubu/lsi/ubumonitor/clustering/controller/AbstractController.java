package es.ubu.lsi.ubumonitor.clustering.controller;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public abstract class AbstractController implements Initializable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractController.class);
	
	private Controller controller = Controller.getInstance();

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
	
	protected List<ClusterWrapper> clusters;

	protected MainController mainController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initLabels();
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
