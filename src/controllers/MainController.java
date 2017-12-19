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
import java.util.HashMap;
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
import javafx.scene.control.Alert.AlertType;
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
			logger.info("Completada la carga del curso '" + UBUGrades.session.getActualCourse().getFullName() + ".");
			
			// Cargamos el html de los graficos y calificaciones
			webViewCharts.setContextMenuEnabled(false); // Desactiva el click derecho
			webViewChartsEngine = webViewCharts.getEngine();
			webViewChartsEngine.load(getClass().getResource("/graphics/Charts.html").toExternalForm());
			// Comprobamos cuando se carga la pagina para traducirla
			webViewChartsEngine.getLoadWorker().stateProperty().addListener(
					(ov, oldState, newState) ->
			webViewChartsEngine.executeScript("setLanguage('" + UBUGrades.resourceBundle.getLocale().toString() + "')"));
			

			// Almacenamos todos participantes en una lista
			ArrayList<EnrolledUser> users = (ArrayList<EnrolledUser>) UBUGrades.session.getActualCourse()
					.getEnrolledUsers();
			ArrayList<EnrolledUser> nameUsers = new ArrayList<>();

			stats = Stats.getStats();

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
			ArrayList<String> rolesList = (ArrayList<String>) UBUGrades.session.getActualCourse().getRoles();
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
			ArrayList<String> groupsList = (ArrayList<String>) UBUGrades.session.getActualCourse().getGroups();
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
			ArrayList<String> nameActivityList = (ArrayList<String>) UBUGrades.session.getActualCourse().getActivities();
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
			ChangeListener<Number> stageSizeListener =
					(observable, oldValue, newValue) -> splitPane.setDividerPositions(0);
			UBUGrades.stage.widthProperty().addListener(stageSizeListener);
			UBUGrades.stage.heightProperty().addListener(stageSizeListener);

			// Activamos la selección múltiple en la lista de participantes
			listParticipants.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			// Asignamos el manejador de eventos de la lista
			// Al clickar en la lista, se recalcula el nº de elementos seleccionados
			// Generamos el gráfico con los elementos selecionados
			listParticipants.setOnMouseClicked((EventHandler<Event>) event -> updateChart());
	
			/// Mostramos la lista de participantes
			listParticipants.setItems(enrList);
	
			// Establecemos la estructura en árbol del calificador
			ArrayList<GradeReportLine> grcl =  (ArrayList<GradeReportLine>) UBUGrades.session.getActualCourse()
					.getGradeReportLines();
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
			lblCountParticipants.setText(UBUGrades.resourceBundle.getString("label.participants") 
					+ " " + UBUGrades.session.getActualCourse().getEnrolledUsersCount());
	
			// Mostramos Usuario logeado y su imagen
			lblActualUser.setText(UBUGrades.resourceBundle.getString("label.user") + " " + UBUGrades.user.getFullName());
			userPhoto.setImage(UBUGrades.user.getUserPhoto());
	
			// Mostramos Curso actual
			lblActualCourse.setText(UBUGrades.resourceBundle.getString("label.course") + " " + UBUGrades.session.getActualCourse().getFullName());
	
			// Mostramos Host actual
			lblActualHost.setText(UBUGrades.resourceBundle.getString("label.host") + " " + UBUGrades.host);
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
			ArrayList<EnrolledUser> users = (ArrayList<EnrolledUser>) UBUGrades.session.getActualCourse()
					.getEnrolledUsers();
			// Cargamos la lista de los roles
			ArrayList<EnrolledUser> nameUsers = new ArrayList<>();
			// Obtenemos los participantes que tienen el rol elegido
			for (int i = 0; i < users.size(); i++) {
				// Filtrado por rol:
				roleYes = false;
				ArrayList<Role> roles = (ArrayList<Role>) users.get(i).getRoles();
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
				ArrayList<Group> groups = (ArrayList<Group>) users.get(i).getGroups();
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
	 * @param line
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
	 */
	public static void setIcon(TreeItem<GradeReportLine> item) {
		try {
			item.setGraphic((Node) new ImageView(new Image("/img/" + item.getValue().getNameType() + ".png")));
		} catch(Exception e) {
			logger.error("No se ha podido cargar la imagen del elemento " + item +" : {}", e);
		}
	}

	/**
	 * Filtra la lista de actividades del calificador según el tipo y el patrón
	 * introducidos.
	 */
	public void filterCalifications() {
		try {
			ArrayList<GradeReportLine> grcl = (ArrayList<GradeReportLine>) UBUGrades.session.getActualCourse()
					.getGradeReportLines();
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
					if (grcl.get(k).getNameType().equals(filterType)
							|| filterType.equals(TODOS)) {
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
	 * Cambia la asignatura actual y carga otra
	 * 
	 * @param actionEvent
	 * @throws Exception
	 */
	public void changeCourse(ActionEvent actionEvent) throws Exception {
		try {
			logger.info("Cambiando de asignatura...");
			// Borramos las estadisticas para esta asignatura
			Stats.removeStats();
			// Accedemos a la siguiente ventana
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Welcome.fxml"), UBUGrades.resourceBundle);
			UBUGrades.stage.close();
			logger.info("Accediendo a UBUGrades...");
			UBUGrades.stage = new Stage();
			Parent root = loader.load();
			Scene scene = new Scene(root);
			UBUGrades.stage.setScene(scene);
			UBUGrades.stage.getIcons().add(new Image("/img/logo_min.png"));
			UBUGrades.stage.setTitle("UBUGrades");
			UBUGrades.stage.show();
		} catch (Exception e) {
			logger.error("Error al cambiar el curso: {}", e);
		}
	}

	/**
	 * Exporta el gráfico. Se exportara como imagen en formato png.
	 * 
	 * @param actionEvent
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
			file = fileChooser.showSaveDialog(UBUGrades.stage);
			if (file != null) {
				String str = (String) webViewChartsEngine.executeScript("exportCurrentElemet()");
				byte[] imgdata = DatatypeConverter.parseBase64Binary(str.substring(str.indexOf(',') + 1));
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imgdata));
				ImageIO.write(bufferedImage, "png", file);
			}
		} catch (Exception e) {
			logger.error("Error al guardar el gráfico: {}", e);
			errorWindow("Error al guardar el gráfico", false);
		}
	}

	/**
	 * Exporta todos los gráficos a un html.
	 * 
	 * @param actionEvent
	 */
	public void saveAll(ActionEvent actionEvent) {
		logger.info("Exportando los gráficos");
		PrintWriter out = null;
		InputStream fr = null;
		FileWriter fw = null;

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Guardar Todo");
		fileChooser.setInitialFileName("Gráficos.html");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".html", "*.html"));
		File file = fileChooser.showSaveDialog(UBUGrades.stage);
		if (file != null) {
			try {
				// Copiamos la plantilla de exportacion, ExportChart.html al nuevo archivo
				fr = getClass().getResourceAsStream("/graphics/ExportCharts.html");
				fw = new FileWriter(file);
				int c = fr.read();
				while (c != -1) {
					fw.write(c);
					c = fr.read();
				}
				
				//Completamos el nuevo archivo con los dataSets de los gráficos
				out = new PrintWriter(new BufferedWriter(fw));
				String generalDataSet = generateDataSet();
				out.println("\r\nvar LineDataSet = " + generalDataSet + ";\r\n");
				out.println("var RadarDataSet = " + generalDataSet + ";\r\n");
				out.println("var BoxPlotGeneralDataSet = " + generateBoxPlotDataSet(TODOS) + ";\r\n");
				out.println("var BoxPlotGroupDataSet = " + generateBoxPlotDataSet(filterGroup) + ";\r\n");
				out.println("var TableDataSet = " + generateTableData() + ";\r\n");
				out.println("</script>\r\n</body>\r\n</html>");

			} catch (IOException e) {
				logger.error("Error al exportar los gráficos.", e);
				errorWindow("No se han podido exportar los gráficos.", false);
			} finally {
				if(out != null) {out.close();}
				try {
					if(fr != null) {fr.close();}
					if(fw != null) {fw.close();}
				} catch (IOException e) {
					logger.error("Error al cerrar FileReader y FileWriter", e);
				}
			}
		}
	}

	/**
	 * Vuelve a la ventana de login de usuario
	 * 
	 * @param actionEvent
	 * @throws Exception
	 */
	public void logOut(ActionEvent actionEvent) throws Exception {
		try {
			Stats.removeStats();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"), UBUGrades.resourceBundle);
			Parent root = loader.load();
			Scene scene = new Scene(root);
			UBUGrades.stage.close();
			logger.info("Cerrando sesión de usuario");
			UBUGrades.stage = new Stage();
			UBUGrades.stage.setScene(scene);
			UBUGrades.stage.getIcons().add(new Image("/img/logo_min.png"));
			UBUGrades.stage.setTitle("UBUGrades");
			UBUGrades.stage.show();
		}  catch (Exception e) {
			logger.error("Error al cerrar la sesión de usuario: {}", e);
		}
	}

	/**
	 * Deja de seleccionar los participantes/actividades y borra el gráfico.
	 * 
	 * @param actionEvent
	 * @throws Exception
	 */
	public void clearSelection(ActionEvent actionEvent) {
		listParticipants.getSelectionModel().clearSelection();
		tvwGradeReport.getSelectionModel().clearSelection();
	}

	/**
	 * Abre en el navegador el repositorio del proyecto.
	 * 
	 * @param actionEvent
	 * @throws Exception
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
	 * @throws Exception
	 */
	public void closeApplication(ActionEvent actionEvent) {
		logger.info("Cerrando aplicación");
		UBUGrades.stage.close();
	}

	private String generateTableData() {
		// Lista de alumnos y calificaciones seleccionadas
		ObservableList<EnrolledUser> selectedParticipants = listParticipants.getSelectionModel().getSelectedItems();
		ObservableList<TreeItem<GradeReportLine>> selectedGRL = tvwGradeReport.getSelectionModel().getSelectedItems();

		StringBuilder tableData = new StringBuilder();
		tableData.append("[['Nombre'");
		Boolean firstElement = true;

		// Por cada ítem seleccionado lo añadimos como label
		for (TreeItem<GradeReportLine> structTree : selectedGRL) {
			tableData.append(",'" + structTree.getValue().getName() + "'");
		}
		tableData.append("],");

		// Por cada usuario seleccionado
		for (EnrolledUser actualUser : selectedParticipants) {
			// Añadimos el nombre del alumno al dataset
			if (firstElement) {
				firstElement = false;
				tableData.append("['" + actualUser.getFullName() + "'");
			} else {
				tableData.append(",['" + actualUser.getFullName() + "'");
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
		// Añadimos las medias
		tableData.append(generateTableMean());
		return tableData.toString();
	}
	
	/**
	 * Genera el dataset de las medias para la tabla de calificaciones.
	 * @return
	 * 		El dataset.
	 */
	private String generateTableMean() {
		// Añadimos la media general
		StringBuilder tableData = new StringBuilder(); 
		tableData.append(",['Media'");
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
				tableData.append(",['Media grupo " + grupo.getText() + "'");
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
		ObservableList<EnrolledUser> selectedParticipants = listParticipants.getSelectionModel().getSelectedItems();
		ObservableList<TreeItem<GradeReportLine>> selectedGRL = tvwGradeReport.getSelectionModel().getSelectedItems();
		int countA = 0;
		boolean firstUser = true;
		boolean firstGrade = true;
		String dataSet = "";
		String labels = "";

		// Por cada usuario seleccionado
		for (EnrolledUser actualUser : selectedParticipants) {
			String actualUserFullName = actualUser.getFullName();
			// Añadimos el nombre del alumno al dataset		
			if (firstUser) {
				dataSet += "{label:'" + actualUserFullName + "',data: [";
				firstUser = false;
			} else {
				dataSet += ",{label:'" + actualUserFullName + "',data: [";
			}
			int countB = 1;
			firstGrade = true;
			// Por cada ítem seleccionado
			for (TreeItem<GradeReportLine> structTree : selectedGRL) {
				countA++;
				GradeReportLine actualLine = actualUser.getGradeReportLine(structTree.getValue().getId());
				try {
					String calculatedGrade;
					if (countA == countB) {
						countB++;
						// Añadidimos el nombre del elemento como label
						if (firstGrade) {
							labels += "'" + actualLine.getName() + "'";
						} else {
							labels += ",'" + actualLine.getName() + "'";
						}
					}
					if(actualLine.getNameType().equals("Assignment")) {
						calculatedGrade = actualLine.getGradeWithScale();
					} else {
						calculatedGrade = actualLine.getGradeAdjustedTo10();
					}
					if (firstGrade) {
						dataSet += calculatedGrade;
						firstGrade = false;
					} else {
						dataSet += "," + calculatedGrade;
					}
				} catch (Exception e) {
					logger.error("Error en la construcción del dataset.", e);
					errorWindow("Error en la construcción del dataset.", false);
				}
			}
			dataSet += "]," + "backgroundColor: 'red'," + "borderColor: 'red'," + "pointBorderColor: 'red',"
					+ "pointBackgroundColor: 'red'," + "borderWidth: 2," + "fill: false}";
		}
		return "{ labels:[" + labels + "],datasets: [" + dataSet + "]}";
	}

	/**
	 * Funcion que genera el DataSet para el boxplot.
	 * 
	 * @return BoxPlot DataSet.
	 */
	private String generateBoxPlotDataSet(String group) {
		HashMap<Integer, DescriptiveStatistics> boxPlotStats;
		if (group.equals(TODOS)) {
			boxPlotStats = stats.getGeneralStats();
		} else {
			boxPlotStats = stats.getGroupStats(group);
		}

		ObservableList<TreeItem<GradeReportLine>> selectedGRL = tvwGradeReport.getSelectionModel().getSelectedItems();

		String labels = "";
		String maximos = "{label:'Máximo',data: [";
		String medianas = "{label:'Mediana',data: [";
		String minimos = "{label:'Mínimo',data: [";
		String primerQuartil = "{label:'Primer Quartil',data: [";
		String tercerQuartil = "{label:'Tercer Quartil',data: [";
		boolean firstLabel = true;
		boolean firstGrade = true;
		int gradeId;

		for (TreeItem<GradeReportLine> structTree : selectedGRL) {
			if (firstLabel) {
				labels += "'" + structTree.getValue().getName() + "'";
				firstLabel = false;
			} else {
				labels += ",'" + structTree.getValue().getName() + "'";
			}

			gradeId = structTree.getValue().getId();
			if (firstGrade) {
				maximos += stats.getMaxmimum(boxPlotStats, gradeId);
				medianas += stats.getMedian(boxPlotStats, gradeId);
				minimos += stats.getMinimum(boxPlotStats, gradeId);
				primerQuartil += stats.getElementPercentile(boxPlotStats, gradeId, 25);
				tercerQuartil += stats.getElementPercentile(boxPlotStats, gradeId, 75);
				firstGrade = false;
			} else {
				maximos += "," + stats.getMaxmimum(boxPlotStats, gradeId);
				medianas += "," + stats.getMedian(boxPlotStats, gradeId);
				minimos += "," + stats.getMinimum(boxPlotStats, gradeId);
				primerQuartil += "," + stats.getElementPercentile(boxPlotStats, gradeId, 25);
				tercerQuartil += "," + stats.getElementPercentile(boxPlotStats, gradeId, 75);
			}
		}

		maximos += "]," + "backgroundColor: 'rgba(244,67,54,1)'," + "borderColor: 'rgba(244,67,54,1)',"
				+ "pointBorderColor: 'rgba(244,67,54,1)'," + "pointBackgroundColor: 'rgba(244,67,54,1)',"
				+ "borderWidth: 2," + "fill: false}";
		tercerQuartil += "]," + "backgroundColor: 'rgba(255,152,0,0.3)" + "'," + "borderColor: 'rgba(255,152,0,1)"
				+ "'," + "pointBorderColor: 'rgba(255,152,0,1)" + "'," + "pointBackgroundColor: 'rgba(255,152,0,1)"
				+ "'," + "borderWidth: 2," + "fill: 3}";
		medianas += "]," + "backgroundColor: 'rgba(0,150,136,1)'," + "borderColor: 'rgba(0,150,136,1)',"
				+ "pointBorderColor: 'rgba(0,150,136,1)'," + "pointBackgroundColor: 'rgba(0,150,136,1)',"
				+ "borderWidth: 2," + "fill: false}";
		primerQuartil += "]," + "backgroundColor: 'rgba(255,152,0,0.3)'," + "borderColor: 'rgba(255,152,0,1)',"
				+ "pointBorderColor: 'rgba(255,152,0,1)'," + "pointBackgroundColor: 'rgba(255,152,0,1)',"
				+ "borderWidth: 2," + "fill: false}";
		minimos += "]," + "backgroundColor: 'rgba(81,45,168,1)'," + "borderColor: 'rgba(81,45,168,1)',"
				+ "pointBorderColor: 'rgba(81,45,168,1)'," + "pointBackgroundColor: 'rgba(81,45,168,1)',"
				+ "borderWidth: 2," + "fill: false}";

		return "{ labels:[" + labels + "]," + "datasets: [" + maximos + "," + tercerQuartil + "," + medianas + ","
				+ primerQuartil + "," + minimos + "]}";
	}

	/**
	 * Función que genera el dataSet de la media de todos los alumnos.
	 * 
	 * @return Mean DataSet.
	 */
	private String generateMeanDataSet(String group) {
		HashMap<Integer, DescriptiveStatistics> meanStats;
		Boolean firstElement = true;
		String meanDataset;
		String color;

		if (group.equals(TODOS)) {
			meanStats = stats.getGeneralStats();
			meanDataset = "{label:'Media general',data:[";
			color = "rgba(255, 152, 0, ";
		} else {
			meanStats = stats.getGroupStats(group);
			meanDataset = "{label:'Media del grupo',data:[";
			color = "rgba(0, 150, 136, ";
		}

		for (TreeItem<GradeReportLine> structTree : tvwGradeReport.getSelectionModel().getSelectedItems()) {
			if (firstElement) {
				meanDataset += stats.getElementMean(meanStats, structTree.getValue().getId());
				firstElement = false;
			} else {
				meanDataset += "," + stats.getElementMean(meanStats, structTree.getValue().getId());
			}
		}

		meanDataset += "]," + "backgroundColor:'" + color + "0.3)'," + "borderColor:'" + color + "1)',"
				+ "pointBackgroundColor:'" + color + "1)'," + "borderWidth: 3," + "fill: true}";

		return meanDataset;
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
			errorWindow("No se han podido generar los gráficos.", true);
		}
	}

	/**
	 * Actualiza los gráficos.
	 */
	private void updateChart() {
		try {
			String data = generateDataSet();
			updateGroupData(filterGroup);
			webViewChartsEngine.executeScript("saveTableData(" + generateTableData() + ")");
			webViewChartsEngine.executeScript("saveMean(" + generateMeanDataSet(TODOS) + ")");
			webViewChartsEngine.executeScript("updateChart('boxplot'," + generateBoxPlotDataSet(TODOS) + ")");
			webViewChartsEngine.executeScript("updateChart('line'," + data + ")");
			webViewChartsEngine.executeScript("updateChart('radar'," + data + ")");
		} catch (JSException e) {
			logger.error("Error al generar los gráficos.", e);
			errorWindow("No se han podido generar los gráficos.", true);
		}
	}

	/**
	 * Muestra una ventana de error.
	 * 
	 * @param mensaje
	 *            El mensaje que se quiere mostrar.
	 * @param exit
	 * 			Indica si se quiere mostar el boton de salir o no.
	 */
	private void errorWindow(String mensaje, Boolean exit) {
		Alert alert = new Alert(AlertType.ERROR);

		alert.setTitle("UbuGrades");
		alert.setHeaderText("Error");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(UBUGrades.stage);
		alert.getDialogPane().setContentText(mensaje);
		
		if(exit) {
			ButtonType buttonSalir = new ButtonType("Cerrar UBUGrades");
			alert.getButtonTypes().setAll(buttonSalir);
	
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == buttonSalir)
				UBUGrades.stage.close();
		}
	}

}