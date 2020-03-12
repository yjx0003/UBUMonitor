package es.ubu.lsi.ubumonitor.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map.Entry;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigurationController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.export.CSVBuilderAbstract;
import es.ubu.lsi.ubumonitor.export.CSVExport;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.Charsets;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.PopupWindow.AnchorLocation;

public class MenuController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);

	private Controller controller = Controller.getInstance();
	@FXML
	private MenuItem updateCourse;
	@FXML
	private Menu menuTheme;
	@FXML
	private ImageView userPhoto;

	private MainController mainController;

	public void init(MainController mainController) {
		this.mainController = mainController;
		updateCourse.setDisable(controller.isOfflineMode());
		initMenuBar();
		initUserPhoto();
	}

	private void initMenuBar() {

		ToggleGroup group = new ToggleGroup();
		for (Entry<String, String> entry : Style.STYLES.entrySet()) {
			String key = entry.getKey();
			String path = entry.getValue();
			RadioMenuItem menuItem = new RadioMenuItem();
			menuItem.setText(key);
			menuItem.setToggleGroup(group);

			if (key.equals(ConfigHelper.getProperty("style", "Modena"))) {
				menuItem.setSelected(true);
			}
			menuItem.setOnAction(event -> {

				controller.getStage().getScene().getStylesheets().clear();
				if (path != null) {
					controller.getStage().getScene().getStylesheets().add(path);
				}
				ConfigHelper.setProperty("style", key);
			});
			menuTheme.getItems().add(menuItem);

		}
	}

	private void initUserPhoto() {
		Image image = new Image(new ByteArrayInputStream(controller.getDataBase().getUserPhoto()));
		userPhoto.setImage(image);

		ContextMenu menu = new ContextMenu();
		MenuItem user = new MenuItem(controller.getDataBase().getFullName(), new ImageView(image));
		MenuItem logout = new MenuItem(I18n.get("menu.logout"));
		MenuItem exit = new MenuItem(I18n.get("menu.exit"));

		logout.setOnAction(this::logOut);
		exit.setOnAction(this::closeApplication);
		menu.getItems().addAll(user, logout, exit);
		menu.setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
		menu.setAutoHide(true);

		userPhoto.setOnMouseClicked(e -> menu.show(userPhoto, e.getScreenX(), e.getScreenY()));

	}

	/**
	 * Vuelve a la ventana de login de usuario.
	 * 
	 * @param actionEvent El ActionEvent.
	 */
	public void logOut(ActionEvent actionEvent) {
		LOGGER.info("Cerrando sesión de usuario");

		changeScene(getClass().getResource("/view/Login.fxml"));
	}

	private void changeScene(URL sceneFXML) {
		changeScene(sceneFXML, null);
	}

	/**
	 * Exporta todos los gráficos a un html.
	 * 
	 * @param actionEvent El ActionEvent.
	 */
	public void updateCourse(ActionEvent actionEvent) {
		if (controller.isOfflineMode()) {
			UtilMethods.errorWindow(I18n.get("error.updateofflinemode"));
		} else {
			changeScene(getClass().getResource("/view/Welcome.fxml"), new WelcomeController(true));
		}
	}

	/**
	 * Exporta todos los datos actuales en formato CSV.
	 * 
	 * @param actionEvent evento
	 * @since 2.4.0.0
	 */
	public void exportCSV(ActionEvent actionEvent) {
		LOGGER.info("Exportando ficheros CSV");
		try {
			DirectoryChooser dir = new DirectoryChooser();
			File file = new File(ConfigHelper.getProperty("csvFolderPath", "./"));
			if (file.exists() && file.isDirectory()) {
				dir.setInitialDirectory(file);
			}

			File selectedDir = dir.showDialog(controller.getStage());
			if (selectedDir != null) {
				CSVBuilderAbstract.setPath(selectedDir.toPath());
				Charsets charset = controller.getMainConfiguration().getValue(MainConfiguration.GENERAL, "charset");
				CSVExport.run(charset.get());
				UtilMethods.infoWindow(I18n.get("message.export_csv_success") + selectedDir.getAbsolutePath());
				ConfigHelper.setProperty("csvFolderPath", selectedDir.getAbsolutePath());
			}

		} catch (Exception e) {
			LOGGER.error("Error al exportar ficheros CSV.", e);
			UtilMethods.errorWindow(I18n.get("error.savecsvfiles"), e);
		}
	}

	/**
	 * Cambia a la ventana de selección de asignatura.
	 * 
	 * @param actionEvent El ActionEvent.
	 * 
	 */
	public void changeCourse(ActionEvent actionEvent) {
		LOGGER.info("Cambiando de asignatura...");
		if (controller.isOfflineMode()) {
			changeScene(getClass().getResource("/view/WelcomeOffline.fxml"), new WelcomeOfflineController());
		} else {
			changeScene(getClass().getResource("/view/Welcome.fxml"), new WelcomeController());
		}

	}

	/**
	 * Permite cambiar la ventana actual.
	 * 
	 * @param sceneFXML La ventanan a la que se quiere cambiar.
	 */
	private void changeScene(URL sceneFXML, Object controllerObject) {
		try {
			UtilMethods.changeScene(sceneFXML, controller.getStage(), controllerObject);
			controller.getStage().setResizable(false);
			controller.getStage().setMaximized(false);

		} catch (Exception e) {
			LOGGER.error("Error al modifcar la ventana de JavaFX: {}", e);
		}
	}

	/**
	 * Deja de seleccionar los participantes/actividades y borra el gráfico.
	 * 
	 * @param actionEvent El ActionEvent.
	 */
	public void clearSelection(ActionEvent actionEvent) {
		SelectionController selectionController = mainController.getSelectionController();
		mainController.getSelectionUserController().getListParticipants().getSelectionModel().clearSelection();
		selectionController.getTvwGradeReport().getSelectionModel().clearSelection();
		selectionController.getListViewComponents().getSelectionModel().clearSelection();
		selectionController.getListViewEvents().getSelectionModel().clearSelection();
		selectionController.getListViewSection().getSelectionModel().clearSelection();
		selectionController.getListViewCourseModule().getSelectionModel().clearSelection();
	}

	public void changeConfiguration() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Configuration.fxml"),
				I18n.getResourceBundle());

		UtilMethods.createDialog(loader, controller.getStage());
		ConfigurationController configurationController = loader.getController();
		configurationController.setMainController(mainController);
		configurationController.setOnClose();

	}

	public void importConfiguration() {
		FileChooser fileChooser = UtilMethods.createFileChooser(I18n.get("menu.importconfig"), null,
				ConfigHelper.getProperty("configurationFolderPath", "./"),
				new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"));

		File file = fileChooser.showOpenDialog(controller.getStage());
		if (file != null) {
			ConfigHelper.setProperty("configurationFolderPath", file.getParent());
			try {
				ConfigurationController.loadConfiguration(controller.getMainConfiguration(), file.toPath());
				changeConfiguration();
			} catch (RuntimeException e) {
				UtilMethods.errorWindow(I18n.get("error.filenotvalid"), e);
			}

		}

	}

	public void exportConfiguration() {
		FileChooser fileChooser = UtilMethods.createFileChooser(I18n.get("menu.exportconfig"),
				UtilMethods.removeReservedChar(controller.getActualCourse().getFullName()) + ".json",
				ConfigHelper.getProperty("configurationFolderPath", "./"),
				new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"));

		File file = fileChooser.showSaveDialog(controller.getStage());
		if (file != null) {
			ConfigurationController.saveConfiguration(controller.getMainConfiguration(), file.toPath());
			ConfigHelper.setProperty("configurationFolderPath", file.getParent());
		}
	}

	/**
	 * Abre en el navegador el repositorio del proyecto.
	 * 
	 * @param actionEvent El ActionEvent.
	 */
	public void aboutApp() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AboutApp.fxml"), I18n.getResourceBundle());

		UtilMethods.createDialog(loader, controller.getStage());

	}

	public void moreInfo() {
		UtilMethods.openURL(AppInfo.GITHUB);
	}

	public void courseStats() {
		UtilMethods.createDialog(
				new FXMLLoader(getClass().getResource("/view/CourseStats.fxml"), I18n.getResourceBundle()),
				controller.getStage());
	}

	public void exportEnrolledUsersPhoto() throws IOException, InvalidFormatException {
		List<EnrolledUser> enrolledUsers = mainController.getSelectionUserController().getListParticipants()
				.getSelectionModel().getSelectedItems();
		int columns = 5;
		Course course = controller.getActualCourse();
		FileChooser fileChooser = UtilMethods.createFileChooser(I18n.get("menu.exportconfig"),
				UtilMethods.removeReservedChar(course.getFullName()+"-"+course.getId())+".docx",
				ConfigHelper.getProperty("configurationFolderPath", "./"),new FileChooser.ExtensionFilter("Word (*.docx)", "*.docx"));

		File file = fileChooser.showSaveDialog(controller.getStage());
		if (file == null)
			return;

		try (FileOutputStream out = new FileOutputStream(file);
				XWPFDocument document = new XWPFDocument();) {

			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null)
				headerFooterPolicy = document.createHeaderFooterPolicy();

			// create header start
			XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

			XWPFParagraph paragraph = header.createParagraph();

			XWPFRun run = paragraph.createRun();
			run.setText(controller.getActualCourse().getFullName());
			paragraph = header.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.RIGHT);

			run = paragraph.createRun();
			run.setText(LocalDateTime.now().format(Controller.DATE_TIME_FORMATTER));

			XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);

			paragraph = footer.getParagraphArray(0);
			if (paragraph == null)
				paragraph = footer.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			run = paragraph.createRun();

			paragraph.getCTP().addNewFldSimple().setInstr("PAGE \\* ARABIC MERGEFORMAT");
			run = paragraph.createRun();
			run.setText("/");
			paragraph.getCTP().addNewFldSimple().setInstr("NUMPAGES \\* ARABIC MERGEFORMAT");

			XWPFTable tab = document.createTable((int) Math.ceil(enrolledUsers.size() / (double) columns), columns);
			tab.setCellMargins(50, 0, 0, 0);

			for (int i = 0; i < enrolledUsers.size(); i++) {

				XWPFTableRow row = tab.getRow(i / columns);
				XWPFTableCell cell = row.getCell(i % columns);
				paragraph = cell.getParagraphArray(0);
				paragraph.setAlignment(ParagraphAlignment.CENTER);
				run = paragraph.createRun();
				run.addPicture(new ByteArrayInputStream(enrolledUsers.get(i).getImageBytes()),
						Document.PICTURE_TYPE_PNG, enrolledUsers.get(i).getId() + ".png", Units.pixelToEMU(80),
						Units.pixelToEMU(80));
				run.addBreak();
				run.setText(enrolledUsers.get(i).getFullName());

			}
			document.write(out);

		}
		UtilMethods.confirmationWindow("Exported");

	}

	/**
	 * Botón "Salir". Cierra la aplicación.
	 * 
	 * @param actionEvent El ActionEvent.
	 */
	public void closeApplication(ActionEvent actionEvent) {
		LOGGER.info("Cerrando aplicación");
		controller.getStage().close();
	}

}
