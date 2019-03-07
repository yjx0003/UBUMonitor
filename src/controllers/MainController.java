package controllers;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.EnrolledUser;
import model.GradeReportLine;
import model.Group;
import model.Role;
import model.Stats;
import model.UBUGrades;
import netscape.javascript.JSException;
import webservice.CourseWS;

/**
 * Clase controlador de la ventana principal
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class MainController implements Initializable {

	static final Logger logger = LoggerFactory.getLogger(MainController.class);

	private static final String TODOS = "Todos";

	private UBUGrades ubuGrades = UBUGrades.getInstance();

	@FXML // Curso actual
	public Label lblActualCourse;
	@FXML // Usuario actual
	public Label lblActualUser;
	@FXML // Host actual
	public Label lblActualHost;
	@FXML // Imagen del usuario
	public ImageView userPhoto;

	@FXML // Nº participantes
	public Label lblCountParticipants;
	@FXML // lista de participantes
	public ListView<EnrolledUser> listParticipants;
	ObservableList<EnrolledUser> enrList;

	@FXML // Botón filtro por rol
	public MenuButton slcRole;
	MenuItem[] roleMenuItems;
	String filterRole = TODOS;

	@FXML // Botón filtro por grupo
	public MenuButton slcGroup;
	MenuItem[] groupMenuItems;
	String filterGroup = TODOS;

	@FXML // Entrada de filtro de usuarios por patrón
	public TextField tfdParticipants;
	String patternParticipants = "";

	@FXML // Vista en árbol de actividades
	public TreeView<GradeReportLine> tvwGradeReport;
	ArrayList<GradeReportLine> gradeReportList;

	@FXML // Entrada de filtro de actividades por patrón
	public TextField tfdItems;
	String patternCalifications = "";

	@FXML // Botón filtro por tipo de actividad
	public MenuButton slcType;
	MenuItem[] typeMenuItems;
	String filterType = TODOS;

	@FXML // Gráfico de lineas
	private WebView webViewCharts;
	private WebEngine webViewChartsEngine;

	@FXML
	private SplitPane splitPane;

	private Stats stats;

	/**
	 * Muestra los usuarios matriculados en el curso, así como las actividades de
	 * las que se compone.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			logger.info(
					"Completada la carga del curso '" + ubuGrades.getSession().getActualCourse().getFullName() + ".");

			// Cargamos el html de los graficos y calificaciones
			webViewCharts.setContextMenuEnabled(false); // Desactiva el click derecho
			webViewChartsEngine = webViewCharts.getEngine();
			webViewChartsEngine.load(getClass().getResource("/graphics/Charts.html").toExternalForm());
			// Comprobamos cuando se carga la pagina para traducirla
			webViewChartsEngine.getLoadWorker().stateProperty()
					.addListener((ov, oldState, newState) -> webViewChartsEngine.executeScript(
							"setLanguage('" + ubuGrades.getResourceBundle().getLocale().toString() + "')"));

			// Almacenamos todos participantes en una lista
			ArrayList<EnrolledUser> users = (ArrayList<EnrolledUser>) ubuGrades.getSession().getActualCourse()
					.getEnrolledUsers();
			ArrayList<EnrolledUser> nameUsers = new ArrayList<>();

			stats = Stats.getStats(ubuGrades.getSession());

			//////////////////////////////////////////////////////////////////////////
			// Manejo de roles (MenuButton Rol):
			// Manejador de eventos para el botón de filtro por roles.
			EventHandler<ActionEvent> actionRole = ((ActionEvent event) -> {
				// Obtenemos el ítem que se ha seleccionado
				MenuItem mItem = (MenuItem) event.getSource();
				// Obtenemos el rol por el que se quiere filtrar
				filterRole = mItem.getText();
				logger.info("-> Filtrando participantes por rol: {}", filterRole);
				filterParticipants();
				slcRole.setText(filterRole);
			});
			// Cargamos una lista con los nombres de los roles
			ArrayList<String> rolesList = (ArrayList<String>) ubuGrades.getSession().getActualCourse().getRoles();
			// Convertimos la lista a una lista de MenuItems para el MenuButton
			ArrayList<MenuItem> rolesItemsList = new ArrayList<>();
			// En principio se mostrarán todos los usuarios con cualquier rol
			MenuItem mi = (new MenuItem(TODOS));
			// Añadimos el manejador de eventos al primer MenuItem
			mi.setOnAction(actionRole);
			rolesItemsList.add(mi);

			for (int i = 0; i < rolesList.size(); i++) {
				String rol = rolesList.get(i);
				mi = (new MenuItem(rol));
				mi.setOnAction(actionRole);
				// Añadimos el manejador de eventos a cada MenuItem
				rolesItemsList.add(mi);
			}

			// Asignamos la lista de MenuItems al MenuButton "Rol"
			slcRole.getItems().addAll(rolesItemsList);
			slcRole.setText(TODOS);

			//////////////////////////////////////////////////////////////////////////
			// Manejo de grupos (MenuButton Grupo):
			// Manejador de eventos para el botón de filtro por grupos.
			EventHandler<ActionEvent> actionGroup = ((ActionEvent event) -> {
				// Obtenemos el ítem que se ha seleccionado
				MenuItem mItem = (MenuItem) event.getSource();
				// Obtenemos el grupo por el que se quire filtrar
				filterGroup = mItem.getText();
				logger.info("-> Filtrando participantes por grupo: {}", filterGroup);
				filterParticipants();
				slcGroup.setText(filterGroup);
			});
			// Cargamos una lista de los nombres de los grupos
			ArrayList<String> groupsList = (ArrayList<String>) ubuGrades.getSession().getActualCourse().getGroups();
			// Convertimos la lista a una lista de MenuItems para el MenuButton
			ArrayList<MenuItem> groupsItemsList = new ArrayList<>();
			// En principio mostrarán todos los usuarios en cualquier grupo
			mi = (new MenuItem(TODOS));
			// Añadimos el manejador de eventos al primer MenuItem
			mi.setOnAction(actionGroup);
			groupsItemsList.add(mi);

			for (int i = 0; i < groupsList.size(); i++) {
				String group = groupsList.get(i);
				mi = (new MenuItem(group));
				// Añadimos el manejador de eventos a cada MenuItem
				mi.setOnAction(actionGroup);
				groupsItemsList.add(mi);
			}
			// Asignamos la lista de MenuItems al MenuButton "Grupo"
			slcGroup.getItems().addAll(groupsItemsList);
			slcGroup.setText(TODOS);

			////////////////////////////////////////////////////////
			// Añadimos todos los participantes a la lista de visualización
			for (int j = 0; j < users.size(); j++) {
				nameUsers.add(users.get(j));
			}
			enrList = FXCollections.observableArrayList(nameUsers);

			//////////////////////////////////////////////////////////////////////////
			// Manejo de actividades (TreeView<GradeReportLine>):
			// Manejador de eventos para las actividades.
			EventHandler<ActionEvent> actionActivity = ((ActionEvent event) -> {
				// Obtenemos el item que se ha seleccionado
				MenuItem mItem = (MenuItem) event.getSource();
				// Obtenemos el valor (rol) para filtrar la lista de
				// participantes
				filterType = mItem.getText();
				logger.info("-> Filtrando calificador por tipo: {}", filterType);
				filterCalifications();
				slcType.setText(filterType);
			});
			// Cargamos una lista de los nombres de los grupos
			List<String> nameActivityList = ubuGrades.getSession().getActualCourse().getActivities();
			// Convertimos la lista a una lista de MenuItems para el MenuButton
			ArrayList<MenuItem> nameActivityItemsList = new ArrayList<>();
			// En principio se van a mostrar todos los participantes en
			// cualquier grupo
			mi = (new MenuItem(TODOS));
			// Añadimos el manejador de eventos al primer MenuItem
			mi.setOnAction(actionActivity);
			nameActivityItemsList.add(mi);
			for (int i = 0; i < nameActivityList.size(); i++) {
				String nameActivity = nameActivityList.get(i);
				mi = (new MenuItem(nameActivity));
				// Añadimos el manejador de eventos a cada MenuItem
				mi.setOnAction(actionActivity);
				nameActivityItemsList.add(mi);
			}

			// Asignamos la lista de grupos al MenuButton "Grupo"
			slcType.getItems().addAll(nameActivityItemsList);
			slcType.setText(TODOS);

			// Inicializamos el listener del textField de participantes
			// Manejador de eventos para el textField de filtro de participantes.
			tfdParticipants.setOnAction((ActionEvent event) -> {
				patternParticipants = tfdParticipants.getText();
				logger.info("-> Filtrando participantes por nombre: {}", patternParticipants);
				filterParticipants();
			});

			// Inicializamos el listener del textField del calificador
			tfdItems.setOnAction((ActionEvent event) -> {
				patternCalifications = tfdItems.getText();
				logger.info("-> Filtrando calificador por nombre: {}", patternCalifications);
				filterCalifications();
			});

			// Coloca el divider a la izquierda al redimensionar la ventana
			ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> splitPane
					.setDividerPositions(0);
			ubuGrades.getStage().widthProperty().addListener(stageSizeListener);
			ubuGrades.getStage().heightProperty().addListener(stageSizeListener);

			// Activamos la selección múltiple en la lista de participantes
			listParticipants.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			// Asignamos el manejador de eventos de la lista
			// Al clickar en la lista, se recalcula el nº de elementos seleccionados
			// Generamos el gráfico con los elementos selecionados
			listParticipants.refresh(); // FIX RMS
			listParticipants.setOnMouseClicked((EventHandler<Event>) event -> updateChart());

			/// Mostramos la lista de participantes
			listParticipants.setItems(enrList);

			// Establecemos la estructura en árbol del calificador
			List<GradeReportLine> grcl = ubuGrades.getSession().getActualCourse().getGradeReportLines();
			// Establecemos la raiz del Treeview
			TreeItem<GradeReportLine> root = new TreeItem<>(grcl.get(0));
			MainController.setIcon(root);
			// Llamamos recursivamente para llenar el Treeview
			for (int k = 0; k < grcl.get(0).getChildren().size(); k++) {
				TreeItem<GradeReportLine> item = new TreeItem<>(grcl.get(0).getChildren().get(k));
				MainController.setIcon(item);
				root.getChildren().add(item);
				root.setExpanded(true);
				setTreeview(item, grcl.get(0).getChildren().get(k));
			}
			// Establecemos la raiz en el TreeView
			tvwGradeReport.setRoot(root);
			tvwGradeReport.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			// Asignamos el manejador de eventos de la lista
			// Al clickar en la lista, se recalcula el nº de elementos seleccionados
			// Generamos el gráfico con los elementos selecionados
			tvwGradeReport.setOnMouseClicked((EventHandler<Event>) event -> updateChart());

			// Mostramos nº participantes
			lblCountParticipants.setText(ubuGrades.getResourceBundle().getString("label.participants") + " "
					+ ubuGrades.getSession().getActualCourse().getEnrolledUsersCount());

			// Mostramos Usuario logeado y su imagen
			lblActualUser.setText(
					ubuGrades.getResourceBundle().getString("label.user") + " " + ubuGrades.getUser().getFullName());
			userPhoto.setImage(ubuGrades.getUser().getUserPhoto());

			// Mostramos Curso actual
			lblActualCourse.setText(ubuGrades.getResourceBundle().getString("label.course") + " "
					+ ubuGrades.getSession().getActualCourse().getFullName());

			// Mostramos Host actual
			lblActualHost.setText(ubuGrades.getResourceBundle().getString("label.host") + " " + ubuGrades.getHost());
		} catch (Exception e) {
			logger.error("Error en la inicialización.", e);
		}
	}

	/**
	 * Filtra los participantes según el rol, el grupo y el patrón indicados
	 */
	public void filterParticipants() {
		try {
			boolean roleYes;
			boolean groupYes;
			boolean patternYes;
			List<EnrolledUser> users = ubuGrades.getSession().getActualCourse().getEnrolledUsers();
			// Cargamos la lista de los roles
			ArrayList<EnrolledUser> nameUsers = new ArrayList<>();
			// Obtenemos los participantes que tienen el rol elegido
			for (int i = 0; i < users.size(); i++) {
				// Filtrado por rol:
				roleYes = false;
				List<Role> roles = users.get(i).getRoles();
				// Si no tiene rol
				if (roles.isEmpty() && filterRole.equals(TODOS)) {
					roleYes = true;
				} else {
					for (int j = 0; j < roles.size(); j++) {
						// Comprobamos si el usuario pasa el filtro de "rol"
						if (roles.get(j).getName().equals(filterRole) || filterRole.equals(TODOS)) {
							roleYes = true;
						}
					}
				}
				// Filtrado por grupo:
				groupYes = false;
				List<Group> groups = users.get(i).getGroups();
				if (groups.isEmpty() && filterGroup.equals(TODOS)) {
					groupYes = true;
				} else {
					for (int k = 0; k < groups.size(); k++) {
						// Comprobamos si el usuario pasa el filtro de "grupo"
						if (groups.get(k).getName().equals(filterGroup) || filterGroup.equals(TODOS)) {
							groupYes = true;
						}
					}
				}
				// Filtrado por patrón:
				patternYes = false;
				if (patternParticipants.equals("")) {
					patternYes = true;
				} else {
					Pattern pattern = Pattern.compile(patternParticipants.toLowerCase());
					Matcher match = pattern.matcher(users.get(i).getFullName().toLowerCase());
					if (match.find()) {
						patternYes = true;
					}
				}
				// Si el usuario se corresponde con los filtros
				if (groupYes && roleYes && patternYes)
					nameUsers.add(users.get(i));
			}
			enrList = FXCollections.observableArrayList(nameUsers);
		} catch (Exception e) {
			logger.error("Error al filtrar los participantes: {}", e);
		}
		listParticipants.setItems(enrList);
		// Actualizamos los gráficos al cambiar el grupo
		updateChart();
	}

	/**
	 * Rellena el árbol de actividades (GradeReportLines). Obtiene los hijos de la
	 * línea pasada por parámetro, los transforma en treeitems y los establece como
	 * hijos del elemento treeItem equivalente de line
	 * 
	 * @param parent
	 *            El padre al que añadir los elementos.
	 * @param line
	 *            La linea con los elementos a añadir.
	 */
	public void setTreeview(TreeItem<GradeReportLine> parent, GradeReportLine line) {
		for (int j = 0; j < line.getChildren().size(); j++) {
			TreeItem<GradeReportLine> item = new TreeItem<>(line.getChildren().get(j));
			MainController.setIcon(item);
			parent.getChildren().add(item);
			parent.setExpanded(true);
			setTreeview(item, line.getChildren().get(j));
		}
	}

	/**
	 * Añade un icono a cada elemento del árbol según su tipo de actividad
	 * 
	 * @param item
	 *            El item al que añadir el icono.
	 */
	public static void setIcon(TreeItem<GradeReportLine> item) {
		try {
			item.setGraphic((Node) new ImageView(new Image("/img/" + item.getValue().getNameType() + ".png")));
		} catch (Exception e) {
			logger.error("No se ha podido cargar la imagen del elemento " + item + " : {}", e);
		}
	}

	/**
	 * Filtra la lista de actividades del calificador según el tipo y el patrón
	 * introducidos.
	 */
	public void filterCalifications() {
		try {
			List<GradeReportLine> grcl = ubuGrades.getSession().getActualCourse().getGradeReportLines();
			// Establecemos la raiz del Treeview
			TreeItem<GradeReportLine> root = new TreeItem<>(grcl.get(0));
			MainController.setIcon(root);
			// Llamamos recursivamente para llenar el Treeview
			if (filterType.equals(TODOS) && patternCalifications.equals("")) {
				// Sin filtro y sin patrón
				for (int k = 0; k < grcl.get(0).getChildren().size(); k++) {
					TreeItem<GradeReportLine> item = new TreeItem<>(grcl.get(0).getChildren().get(k));
					MainController.setIcon(item);
					root.getChildren().add(item);
					root.setExpanded(true);
					setTreeview(item, grcl.get(0).getChildren().get(k));
				}
			} else { // Con filtro
				for (int k = 1; k < grcl.size(); k++) {
					TreeItem<GradeReportLine> item = new TreeItem<>(grcl.get(k));
					boolean activityYes = false;
					if (grcl.get(k).getNameType().equals(filterType) || filterType.equals(TODOS)) {
						activityYes = true;
					}
					Pattern pattern = Pattern.compile(patternCalifications.toLowerCase());
					Matcher match = pattern.matcher(grcl.get(k).getName().toLowerCase());
					boolean patternYes = false;
					if (patternCalifications.equals("") || match.find()) {
						patternYes = true;
					}
					if (activityYes && patternYes) {
						MainController.setIcon(item);
						root.getChildren().add(item);
					}
					root.setExpanded(true);
				}
			}
			// Establecemos la raiz del treeview
			tvwGradeReport.setRoot(root);
		} catch (Exception e) {
			logger.error("Error al filtrar los elementos del calificador: {}", e);
		}
		listParticipants.setItems(enrList);
	}

	/**
	 * Exporta el gráfico. Se exportara como imagen en formato png.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 * @throws Exception
	 */
	public void saveChart(ActionEvent actionEvent) throws Exception {
		File file = new File("chart.png");

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Guardar gráfico");

		fileChooser.setInitialFileName("chart.png");
		fileChooser.setInitialDirectory(file.getParentFile());
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".png", "*.png"));
		try {
			file = fileChooser.showSaveDialog(ubuGrades.getStage());
			if (file != null) {
				String str = (String) webViewChartsEngine.executeScript("exportCurrentElemet()");
				byte[] imgdata = DatatypeConverter.parseBase64Binary(str.substring(str.indexOf(',') + 1));
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imgdata));
				ImageIO.write(bufferedImage, "png", file);
			}
		} catch (Exception e) {
			logger.error("Error al guardar el gráfico: {}", e);
			errorWindow(ubuGrades.getResourceBundle().getString("error.savechart"), false);
		}
	}

	/**
	 * Exporta todos los gráficos a un html.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 */
	public void saveAll(ActionEvent actionEvent) {
		logger.info("Exportando los gráficos");
		PrintWriter out = null;

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Guardar Todo");
		fileChooser.setInitialFileName("Gráficos.html");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".html", "*.html"));
		File file = fileChooser.showSaveDialog(ubuGrades.getStage());
		// Copiamos la plantilla de exportacion, ExportChart.html al nuevo archivo
		if (file != null) {
			try (FileWriter fw = new FileWriter(file)) {
				InputStream fr = getClass().getResourceAsStream("/graphics/ExportCharts.html");
				int c = fr.read();
				while (c != -1) {
					fw.write(c);
					c = fr.read();
				}

				// Completamos el nuevo archivo con los dataSets de los gráficos
				out = new PrintWriter(new BufferedWriter(fw));
				String generalDataSet = generateDataSet();
				out.println("\r\nvar userLang = \"" + ubuGrades.getResourceBundle().getLocale().toString() + "\";\r\n");
				out.println("\r\nvar LineDataSet = " + generalDataSet + ";\r\n");
				out.println("var RadarDataSet = " + generalDataSet + ";\r\n");
				out.println("var BoxPlotGeneralDataSet = " + generateBoxPlotDataSet(TODOS) + ";\r\n");
				out.println("var BoxPlotGroupDataSet = " + generateBoxPlotDataSet(filterGroup) + ";\r\n");
				out.println("var TableDataSet = " + generateTableData() + ";\r\n");
				out.println("</script>\r\n</body>\r\n</html>");

				fr.close();
				out.close();
			} catch (IOException e) {
				logger.error("Error al exportar los gráficos.", e);
				errorWindow(ubuGrades.getResourceBundle().getString("error.saveallcharts"), false);
			}
		}
	}

	/**
	 * Cambia a la ventana de selección de asignatura.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 * 
	 * @throws Exception
	 */
	public void changeCourse(ActionEvent actionEvent) throws Exception {
		logger.info("Cambiando de asignatura...");
		Stats.removeStats();
		changeScene(getClass().getResource("/view/Welcome.fxml"));
	}

	/**
	 * Vuelve a la ventana de login de usuario.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 */
	public void logOut(ActionEvent actionEvent) {
		logger.info("Cerrando sesión de usuario");
		Stats.removeStats();
		changeScene(getClass().getResource("/view/Login.fxml"));
	}

	/**
	 * Permite cambiar la ventana actual.
	 * 
	 * @param sceneFXML
	 *            La ventanan a la que se quiere cambiar.
	 */
	private void changeScene(URL sceneFXML) {
		try {
			FXMLLoader loader = new FXMLLoader(sceneFXML, ubuGrades.getResourceBundle());
			Parent root = loader.load();
			Scene scene = new Scene(root);
			ubuGrades.getStage().close();
			ubuGrades.setStage(new Stage());
			ubuGrades.getStage().setScene(scene);
			ubuGrades.getStage().getIcons().add(new Image("/img/logo_min.png"));
			ubuGrades.getStage().setTitle("UBUGrades");
			ubuGrades.getStage().show();
		} catch (Exception e) {
			logger.error("Error al modifcar la ventana de JavaFX: {}", e);
		}
	}

	/**
	 * Deja de seleccionar los participantes/actividades y borra el gráfico.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 */
	public void clearSelection(ActionEvent actionEvent) {
		listParticipants.getSelectionModel().clearSelection();
		tvwGradeReport.getSelectionModel().clearSelection();
	}

	/**
	 * Abre en el navegador el repositorio del proyecto.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 */
	public void aboutUBUGrades(ActionEvent actionEvent) {
		try {
			Desktop.getDesktop().browse(new URL("https://github.com/huco95/UBUGrades").toURI());
		} catch (IOException | URISyntaxException e) {
			logger.error("Error al abir la pagina aboutUBUGrades: {}", e);
		}
	}

	/**
	 * Botón "Salir". Cierra la aplicación.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 */
	public void closeApplication(ActionEvent actionEvent) {
		logger.info("Cerrando aplicación");
		ubuGrades.getStage().close();
	}

	/**
	 * Genera el dataset para la tabal de calificaciones.
	 * 
	 * @return El dataset.
	 */
	private String generateTableData() {
		// Lista de alumnos y calificaciones seleccionadas
		listParticipants.refresh(); // FIX RMS
		tvwGradeReport.refresh(); // FIX RMS

		ObservableList<EnrolledUser> selectedParticipants = listParticipants.getSelectionModel().getSelectedItems();
		ObservableList<TreeItem<GradeReportLine>> selectedGRL = tvwGradeReport.getSelectionModel().getSelectedItems();

		StringBuilder tableData = new StringBuilder();
		tableData.append("[['" + ubuGrades.getResourceBundle().getString("chartlabel.name") + "'");
		Boolean firstElement = true;

		// Por cada ítem seleccionado lo añadimos como label
		for (TreeItem<GradeReportLine> structTree : selectedGRL) {
			tableData.append(",'" + escapeJavaScriptText(structTree.getValue().getName()) + "'");
		}
		tableData.append("],");

		// Por cada usuario seleccionado
		for (EnrolledUser actualUser : selectedParticipants) {
			// Añadimos el nombre del alumno al dataset
			if (firstElement) {
				firstElement = false;
				tableData.append("['" + escapeJavaScriptText(actualUser.getFullName()) + "'");
			} else {
				tableData.append(",['" + escapeJavaScriptText(actualUser.getFullName()) + "'");
			}
			// Por cada ítem seleccionado
			for (TreeItem<GradeReportLine> structTree : selectedGRL) {
				GradeReportLine actualLine = actualUser.getGradeReportLine(structTree.getValue().getId());
				String calculatedGrade = actualLine.getGrade();
				// Si es numérico lo graficamos y lo mostramos en la tabla
				if (!Float.isNaN(CourseWS.getFloat(calculatedGrade))) {
					Double grade = Math.round(CourseWS.getFloat(calculatedGrade) * 100.0) / 100.0;
					// Añadimos la nota al gráfico
					tableData.append(",{v:" + grade + ", f:'" + grade + "/" + actualLine.getRangeMax() + "'}");
				} else {
					tableData.append(",{v:0, f:'" + calculatedGrade + "'}");
				}
			}
			tableData.append("]");
		}
		// FIX RMS
		if (selectedParticipants.isEmpty()) {
			// Remove last comma, if there are not students selected in screen.
			tableData.deleteCharAt(tableData.length() - 1);
		}
		// Añadimos las medias
		tableData.append(generateTableMean());
		return tableData.toString();
	}

	/**
	 * Genera el dataset de las medias para la tabla de calificaciones.
	 * 
	 * @return El dataset.
	 */
	private String generateTableMean() {
		// Añadimos la media general
		StringBuilder tableData = new StringBuilder();
		tableData.append(",['" + ubuGrades.getResourceBundle().getString("chartlabel.tableMean") + "'");
		for (TreeItem<GradeReportLine> structTree : tvwGradeReport.getSelectionModel().getSelectedItems()) {
			String grade = stats.getElementMean(stats.getGeneralStats(), structTree.getValue().getId());
			if (grade.equals("NaN")) {
				tableData.append(",{v:0, f:'NaN'}");
			} else {
				tableData.append(",{v:" + grade + ", f:'" + grade + "/10'}");
			}
		}
		tableData.append("]");

		// Añadimos la media de los grupos
		for (MenuItem grupo : slcGroup.getItems()) {
			if (!grupo.getText().equals(TODOS)) {
				tableData.append(",['" + ubuGrades.getResourceBundle().getString("chartlabel.tableGroupMean") + " "
						+ grupo.getText() + "'");
				for (TreeItem<GradeReportLine> structTree : tvwGradeReport.getSelectionModel().getSelectedItems()) {
					String grade = stats.getElementMean(stats.getGroupStats(grupo.getText()),
							structTree.getValue().getId());
					if (grade.equals("NaN")) {
						tableData.append(",{v:0, f:'NaN'}");
					} else {
						tableData.append(",{v:" + grade + ", f:'" + grade + "/10'}");
					}
				}
				tableData.append("]");
			}
		}
		tableData.append("]");
		return tableData.toString();
	}

	/**
	 * 
	 * Metodo que genera el data set para los gráficos.
	 * 
	 * @return El data set.
	 */
	private String generateDataSet() {
		// Lista de alumnos y calificaciones seleccionadas
		listParticipants.refresh(); // FIX RMS
		tvwGradeReport.refresh(); // FIX RMS

		ObservableList<EnrolledUser> selectedParticipants = listParticipants.getSelectionModel().getSelectedItems();
		ObservableList<TreeItem<GradeReportLine>> selectedGRL = tvwGradeReport.getSelectionModel().getSelectedItems();
		int countA = 0;
		boolean firstUser = true;
		boolean firstGrade = true;
		StringBuilder dataSet = new StringBuilder();
		StringBuilder labels = new StringBuilder();

		logger.debug("Selected participant: {}", selectedParticipants.size());
		// Por cada usuario seleccionado
		for (EnrolledUser actualUser : selectedParticipants) {
			if (actualUser != null) { // BUG when we deselect the penultimate student, some student can have null value. TODO
				// TODO logger.debug("Enroller user: {}", actualUser.getFirstName());
				String actualUserFullName = actualUser.getFullName();
				// Añadimos el nombre del alumno al dataset
				if (firstUser) {
					dataSet.append("{label:'" + actualUserFullName + "',data: [");
					firstUser = false;
				} else {
					dataSet.append(",{label:'" + actualUserFullName + "',data: [");
				}
				int countB = 1;
				firstGrade = true;
				// Por cada ítem seleccionado
				for (TreeItem<GradeReportLine> structTree : selectedGRL) {
					countA++;
					try {
						GradeReportLine actualLine = actualUser.getGradeReportLine(structTree.getValue().getId());
						String calculatedGrade;
						if (countA == countB) {
							countB++;
							// Añadidimos el nombre del elemento como label							
							if (firstGrade) {								
								labels.append("'" + escapeJavaScriptText(structTree.getValue().getName()) + "'");
							} else {
								labels.append(",'" + escapeJavaScriptText(structTree.getValue().getName()) + "'");
							}
						}
						if (actualLine.getNameType().equals("Assignment")) {
							calculatedGrade = actualLine.getGradeWithScale(ubuGrades.getSession().getActualCourse());
						} else {
							calculatedGrade = actualLine.getGradeAdjustedTo10();
						}
						if (firstGrade) {
							dataSet.append(calculatedGrade);
							firstGrade = false;
						} else {
							dataSet.append("," + calculatedGrade);
						}
					} catch (Exception e) {
						logger.error("Error en la construcción del dataset.", e);
						errorWindow(ubuGrades.getResourceBundle().getString("error.generatedataset"), false);
					}
				}
				dataSet.append("]," + "backgroundColor: 'red'," + "borderColor: 'red'," + "pointBorderColor: 'red',"
						+ "pointBackgroundColor: 'red'," + "borderWidth: 3," + "fill: false}");
			}
		}
		return "{ labels:[" + labels + "],datasets: [" + dataSet + "]}";
	}
	
	
	/**
	 * Escape the commas in the text. For example 'Law D'Hont' is changed to 'Law D\'Hont'.
	 *
	 * @author Raúl Marticorena
	 * @since 1.5.3
	 */
	private static String escapeJavaScriptText(String input) {
		return input.replaceAll("'", "\\\\'");
	}

	/**
	 * Funcion que genera el DataSet para el boxplot.
	 * 
	 * @param group
	 *            El grupo sobre el que generar el dataset.
	 * 
	 * @return BoxPlot DataSet.
	 */
	private String generateBoxPlotDataSet(String group) {
		Map<Integer, DescriptiveStatistics> boxPlotStats;
		if (group.equals(TODOS)) {
			boxPlotStats = stats.getGeneralStats();
		} else {
			boxPlotStats = stats.getGroupStats(group);
		}

		ResourceBundle rs = ubuGrades.getResourceBundle();
		StringBuilder labels = new StringBuilder();
		StringBuilder upperLimit = new StringBuilder("{label:'" + rs.getString("chartlabel.upperlimit") + "',data: [");
		StringBuilder median = new StringBuilder("{label:'" + rs.getString("chartlabel.median") + "',data: [");
		StringBuilder lowerLimit = new StringBuilder("{label:'" + rs.getString("chartlabel.lowerlimit") + "',data: [");
		StringBuilder firstQuartile = new StringBuilder(
				"{label:'" + rs.getString("chartlabel.firstquartile") + "',data: [");
		StringBuilder thirdQuartile = new StringBuilder(
				"{label:'" + rs.getString("chartlabel.thirdquartile") + "',data: [");
		boolean firstLabel = true;
		boolean firstGrade = true;
		int gradeId;

		ObservableList<TreeItem<GradeReportLine>> selectedGRL = tvwGradeReport.getSelectionModel().getSelectedItems();

		for (TreeItem<GradeReportLine> structTree : selectedGRL) {
			if (firstLabel) {
				labels.append("'" + escapeJavaScriptText(structTree.getValue().getName()) + "'");
				firstLabel = false;
			} else {
				labels.append(",'" + escapeJavaScriptText(structTree.getValue().getName()) + "'");
			}

			gradeId = structTree.getValue().getId();
			if (firstGrade) {
				upperLimit.append(stats.getUpperLimit(boxPlotStats, gradeId));
				median.append(stats.getMedian(boxPlotStats, gradeId));
				lowerLimit.append(stats.getLowerLimit(boxPlotStats, gradeId));
				firstQuartile.append(stats.getElementPercentile(boxPlotStats, gradeId, 25));
				thirdQuartile.append(stats.getElementPercentile(boxPlotStats, gradeId, 75));
				firstGrade = false;
			} else {
				upperLimit.append("," + stats.getUpperLimit(boxPlotStats, gradeId));
				median.append("," + stats.getMedian(boxPlotStats, gradeId));
				lowerLimit.append("," + stats.getLowerLimit(boxPlotStats, gradeId));
				firstQuartile.append("," + stats.getElementPercentile(boxPlotStats, gradeId, 25));
				thirdQuartile.append("," + stats.getElementPercentile(boxPlotStats, gradeId, 75));
			}
		}

		upperLimit.append("]," + "backgroundColor: 'rgba(244,67,54,1)'," + "borderColor: 'rgba(244,67,54,1)',"
				+ "pointBorderColor: 'rgba(244,67,54,1)'," + "pointBackgroundColor: 'rgba(244,67,54,1)',"
				+ "borderWidth: 3," + "fill: false}");
		thirdQuartile.append("]," + "backgroundColor: 'rgba(255,152,0,0.3)" + "'," + "borderColor: 'rgba(255,152,0,1)"
				+ "'," + "pointBorderColor: 'rgba(255,152,0,1)" + "'," + "pointBackgroundColor: 'rgba(255,152,0,1)"
				+ "'," + "borderWidth: 3," + "fill: 3}");
		median.append("]," + "backgroundColor: 'rgba(0,150,136,1)'," + "borderColor: 'rgba(0,150,136,1)',"
				+ "pointBorderColor: 'rgba(0,150,136,1)'," + "pointBackgroundColor: 'rgba(0,150,136,1)',"
				+ "borderWidth: 3," + "fill: false}");
		firstQuartile.append("]," + "backgroundColor: 'rgba(255,152,0,0.3)'," + "borderColor: 'rgba(255,152,0,1)',"
				+ "pointBorderColor: 'rgba(255,152,0,1)'," + "pointBackgroundColor: 'rgba(255,152,0,1)',"
				+ "borderWidth: 3," + "fill: false}");
		lowerLimit.append("]," + "backgroundColor: 'rgba(81,45,168,1)'," + "borderColor: 'rgba(81,45,168,1)',"
				+ "pointBorderColor: 'rgba(81,45,168,1)'," + "pointBackgroundColor: 'rgba(81,45,168,1)',"
				+ "borderWidth: 3," + "fill: false}");

		return "{ labels:[" + labels + "]," + "datasets: [" + upperLimit + "," + thirdQuartile + "," + median + ","
				+ firstQuartile + "," + lowerLimit + "," + generateAtypicalValuesDataSet(boxPlotStats) + "]}";
	}

	private String generateAtypicalValuesDataSet(Map<Integer, DescriptiveStatistics> statistics) {
		StringBuilder dataset = new StringBuilder();
		int gradeId;

		ObservableList<TreeItem<GradeReportLine>> selectedGRL = tvwGradeReport.getSelectionModel().getSelectedItems();

		for (int i = 0; i < selectedGRL.size(); i++) {
			gradeId = selectedGRL.get(i).getValue().getId();
			List<String> atypicalValues = stats.getAtypicalValues(statistics, gradeId);

			if (dataset.length() != 0 && !atypicalValues.isEmpty()) {
				dataset.append(",");
			}

			for (int j = 0; j < atypicalValues.size(); j++) {
				if (j != 0) {
					dataset.append(",");
				}
				dataset.append(
						"{label:'" + ubuGrades.getResourceBundle().getString("chartlabel.atypicalValue") + "',data: [");
				for (int x = 0; x < selectedGRL.size(); x++) {
					if (x != 0) {
						dataset.append(",");
					}
					if (x == i) {
						dataset.append(atypicalValues.get(j));
					} else {
						dataset.append("NaN");
					}
				}
				dataset.append("]," + "backgroundColor: 'rgba(0, 97, 255, 1)'," + "borderColor: 'rgba(0, 97, 255, 1)',"
						+ "pointBorderColor: 'rgba(0, 97, 255, 1)'," + "pointBackgroundColor: 'rgba(0, 97, 255, 1)',"
						+ "borderWidth: 3," + "fill: false}");
			}
		}
		return dataset.toString();
	}

	/**
	 * Función que genera el dataSet de la media de todos los alumnos.
	 * 
	 * @param group
	 *            El grupo del que obtener la media.
	 * 
	 * @return Mean DataSet.
	 */
	private String generateMeanDataSet(String group) {
		Map<Integer, DescriptiveStatistics> meanStats;
		Boolean firstElement = true;
		StringBuilder meanDataset = new StringBuilder();
		String color;

		if (group.equals(TODOS)) {
			meanStats = stats.getGeneralStats();
			meanDataset.append(
					"{label:'" + ubuGrades.getResourceBundle().getString("chartlabel.generalMean") + "',data:[");
			color = "rgba(255, 152, 0, ";
		} else {
			meanStats = stats.getGroupStats(group);
			meanDataset
					.append("{label:'" + ubuGrades.getResourceBundle().getString("chartlabel.groupMean") + "',data:[");
			color = "rgba(0, 150, 136, ";
		}

		for (TreeItem<GradeReportLine> structTree : tvwGradeReport.getSelectionModel().getSelectedItems()) {
			if (firstElement) {
				meanDataset.append(stats.getElementMean(meanStats, structTree.getValue().getId()));
				firstElement = false;
			} else {
				meanDataset.append("," + stats.getElementMean(meanStats, structTree.getValue().getId()));
			}
		}

		meanDataset.append("]," + "backgroundColor:'" + color + "0.3)'," + "borderColor:'" + color + "1)',"
				+ "pointBackgroundColor:'" + color + "1)'," + "borderWidth: 3," + "fill: true}");

		return meanDataset.toString();
	}

	/**
	 * Actualiza la media y el box plot del grupo.
	 * 
	 * @param group
	 *            El grupo seleccionado.
	 */
	private void updateGroupData(String group) {
		try {
			if (group.equals(TODOS)) {
				webViewChartsEngine.executeScript("saveGroupMean('')");
			} else {
				webViewChartsEngine.executeScript("saveGroupMean(" + generateMeanDataSet(group) + ")");
				webViewChartsEngine.executeScript("updateChart('boxplotgroup'," + generateBoxPlotDataSet(group) + ")");
			}
		} catch (JSException e) {
			logger.error("Error al generar los gráficos.", e);
			errorWindow(ubuGrades.getResourceBundle().getString("error.generateCharts"), true);
		}
	}

	/**
	 * Actualiza los gráficos.
	 */
	private void updateChart() {
		try {
			String data = generateDataSet();
			logger.debug("Data: {}", data);
			updateGroupData(filterGroup);
			String tableData = generateTableData();
			logger.debug("Table data for chart: {}", tableData);
			webViewChartsEngine.executeScript("saveTableData(" + tableData + ")");
			webViewChartsEngine.executeScript("saveMean(" + generateMeanDataSet(TODOS) + ")");
			webViewChartsEngine.executeScript("updateChart('boxplot'," + generateBoxPlotDataSet(TODOS) + ")");
			webViewChartsEngine.executeScript("updateChart('line'," + data + ")");
			webViewChartsEngine.executeScript("updateChart('radar'," + data + ")");
		} catch (JSException e) {
			logger.error("Error al generar los gráficos.", e);
			errorWindow(ubuGrades.getResourceBundle().getString("error.generateCharts"), false); // FIX RMS Review true
																									// or false
		} catch (Exception e) {
			logger.error("Error general al generar los gráficos.", e);
			errorWindow(ubuGrades.getResourceBundle().getString("error.generateCharts"), false);
		}
	}

	/**
	 * Muestra una ventana de error.
	 * 
	 * @param mensaje
	 *            El mensaje que se quiere mostrar.
	 * @param exit
	 *            Indica si se quiere mostar el boton de salir o no.
	 */
	private void errorWindow(String mensaje, Boolean exit) {
		Alert alert = new Alert(AlertType.ERROR);

		alert.setTitle("UbuGrades");
		alert.setHeaderText("Error");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(ubuGrades.getStage());
		alert.getDialogPane().setContentText(mensaje);

		if (exit) {
			ButtonType buttonSalir = new ButtonType("Cerrar UBUGrades");
			alert.getButtonTypes().setAll(buttonSalir);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == buttonSalir)
				ubuGrades.getStage().close();
		}
	}

}