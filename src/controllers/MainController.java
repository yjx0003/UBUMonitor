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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.webkit.WebConsoleListener;

import controllers.datasets.StackedBarDataSetComponent;
import controllers.datasets.StackedBarDataSetComponentEvent;
import controllers.ubulogs.GroupByAbstract;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Component;
import model.ComponentEvent;
import model.EnrolledUser;
import model.GradeItem;
import model.Group;
import model.Role;
import model.Stats;
import model.mod.ModuleType;
import netscape.javascript.JSException;

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

	private final static Image ERROR_ICON = new Image("/img/error.png");

	private StackedBarDataSetComponent stackedBarDatasetComponent = StackedBarDataSetComponent.getInstance();

	private StackedBarDataSetComponentEvent stackedBarDatasetComponentEvent = StackedBarDataSetComponentEvent
			.getInstance();

	private Controller controller = Controller.getInstance();

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
	public TreeView<GradeItem> tvwGradeReport;
	List<GradeItem> gradeReportList;

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

	@FXML
	private TabPane tabPane;

	@FXML
	private Tab tabUbuGrades;

	@FXML
	private Tab tabUbuLogs;

	@FXML
	private Tab tabUbuLogsComponent;

	@FXML
	private Tab tabUbuLogsEvent;

	@FXML
	private TextField componentTextField;

	@FXML
	private TextField componentEventTextField;

	@FXML
	private GridPane optionsUbuLogs;

	@FXML
	private TextField textFieldMax;

	@FXML
	private ChoiceBox<GroupByAbstract<?>> choiceBoxDate;
	private GroupByAbstract<?> selectedChoiceBoxDate;

	@FXML
	private DatePicker datePickerStart;
	private LocalDate dateStart;
	@FXML
	private DatePicker datePickerEnd;
	private LocalDate dateEnd;

	@FXML
	private Button filterLogButton;

	@FXML
	private ListView<Component> listViewComponents;

	@FXML
	private ListView<ComponentEvent> listViewEvents;

	private Stats stats;

	/**
	 * Muestra los usuarios matriculados en el curso, así como las actividades de
	 * las que se compone.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			logger.info(
					"Completada la carga del curso '" + controller.getActualCourse().getFullName() + ".");

			stats = controller.getStats();

			// Cargamos el html de los graficos y calificaciones
			webViewCharts.setContextMenuEnabled(false); // Desactiva el click derecho
			webViewChartsEngine = webViewCharts.getEngine();

			webViewChartsEngine.load(getClass().getResource("/graphics/Charts.html").toExternalForm());
			// Comprobamos cuando se carga la pagina para traducirla
			webViewChartsEngine.getLoadWorker().stateProperty()
					.addListener((ov, oldState, newState) -> webViewChartsEngine.executeScript(
							"setLanguage('" + I18n.getResourceBundle().getLocale() + "')"));

			// Guardamos en el logger los errores de consola que se generan en el JS
			WebConsoleListener.setDefaultListener(new WebConsoleListener() {
				@Override
				public void messageAdded(WebView webView, String message, int lineNumber, String sourceId) {
					logger.error("Error en la consola de JS: " + message + " [" + sourceId + ":" + lineNumber + "] ");
				}
			});

			initLogOptionsFilter();

			initTabGrades();
			initTabLogs();
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
			Set<Role> rolesList = controller.getActualCourse().getRoles();
			// Convertimos la lista a una lista de MenuItems para el MenuButton
			List<MenuItem> rolesItemsList = new ArrayList<>();
			// En principio se mostrarán todos los usuarios con cualquier rol
			MenuItem mi = new MenuItem(TODOS);
			// Añadimos el manejador de eventos al primer MenuItem
			mi.setOnAction(actionRole);
			rolesItemsList.add(mi);

			for (Role role : rolesList) {
				mi = new MenuItem(role.toString());
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
			Set<Group> groupsList = controller.getActualCourse().getGroups();
			// Convertimos la lista a una lista de MenuItems para el MenuButton
			ArrayList<MenuItem> groupsItemsList = new ArrayList<>();
			// En principio mostrarán todos los usuarios en cualquier grupo
			mi = new MenuItem(TODOS);
			// Añadimos el manejador de eventos al primer MenuItem
			mi.setOnAction(actionGroup);
			groupsItemsList.add(mi);

			for (Group group : groupsList) {
				mi = new MenuItem(group.toString());
				// Añadimos el manejador de eventos a cada MenuItem
				mi.setOnAction(actionGroup);
				groupsItemsList.add(mi);
			}
			// Asignamos la lista de MenuItems al MenuButton "Grupo"
			slcGroup.getItems().addAll(groupsItemsList);
			slcGroup.setText(TODOS);

			// Almacenamos todos participantes en una lista
			Set<EnrolledUser> users = controller.getActualCourse().getEnrolledUsers();
			////////////////////////////////////////////////////////
			// Añadimos todos los participantes a la lista de visualización

			enrList = FXCollections.observableArrayList(users);
			enrList.sort(Comparator.comparing(EnrolledUser::getLastname, String.CASE_INSENSITIVE_ORDER)
					.thenComparing(EnrolledUser::getFirstname, String.CASE_INSENSITIVE_ORDER));

			//////////////////////////////////////////////////////////////////////////
			// Manejo de actividades (TreeView<GradeItem>):
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
			// Cargamos una lista de los tipos de modulos
			Set<ModuleType> modulesTypes = controller.getActualCourse().getUniqueModuleTypes();
			// Convertimos la lista a una lista de MenuItems para el MenuButton
			List<MenuItem> nameActivityItemsList = new ArrayList<>();
			// En principio se van a mostrar todos los participantes en
			// cualquier grupo
			mi = new MenuItem(TODOS);
			// Añadimos el manejador de eventos al primer MenuItem
			mi.setOnAction(actionActivity);
			nameActivityItemsList.add(mi);
			for (ModuleType moduleType : modulesTypes) {

				mi = new MenuItem(moduleType.toString());
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
			controller.getStage().widthProperty().addListener(stageSizeListener);
			controller.getStage().heightProperty().addListener(stageSizeListener);

			// Activamos la selección múltiple en la lista de participantes
			listParticipants.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			// Asignamos el manejador de eventos de la lista
			// Al clickar en la lista, se recalcula el nº de elementos seleccionados
			// Generamos el gráfico con los elementos selecionados

			listParticipants.getSelectionModel().getSelectedItems()
					.addListener(
							(Change<? extends EnrolledUser> usersSelected) -> {
								if (tabUbuGrades.isSelected()) {
									updateGradesChart();
								} else if (tabUbuLogs.isSelected()) {
									updateLogsChart();
								}
							});

			/// Mostramos la lista de participantes
			listParticipants.setItems(enrList);

			listParticipants.setCellFactory(callback -> new ListCell<EnrolledUser>() {
				@Override
				public void updateItem(EnrolledUser user, boolean empty) {
					super.updateItem(user, empty);
					if (empty) {
						setText(null);
						setGraphic(null);
					} else {
						setText(user.toString());
						try {
							Image image = new Image(new ByteArrayInputStream(user.getImageBytes()));
							setGraphic(new ImageView(image));
						} catch (Exception e) {
							logger.error("No se ha podido cargar la imagen de: " + user);
							setGraphic(new ImageView(new Image("/img/default_user.png")));
						}
					}
				}
			});

			// Establecemos la estructura en árbol del calificador
			GradeItem grcl = controller.getActualCourse().getRootGradeItem();
			// Establecemos la raiz del Treeview
			TreeItem<GradeItem> root = new TreeItem<>(grcl);
			MainController.setIcon(root);
			// Llamamos recursivamente para llenar el Treeview
			for (int k = 0; k < grcl.getChildren().size(); k++) {
				TreeItem<GradeItem> item = new TreeItem<>(grcl.getChildren().get(k));
				MainController.setIcon(item);
				root.getChildren().add(item);
				root.setExpanded(true);
				setTreeview(item, grcl.getChildren().get(k));
			}
			// Establecemos la raiz en el TreeView
			tvwGradeReport.setRoot(root);
			tvwGradeReport.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			// Asignamos el manejador de eventos de la lista
			// Al clickar en la lista, se recalcula el nº de elementos seleccionados
			// Generamos el gráfico con los elementos selecionados
			tvwGradeReport.getSelectionModel().getSelectedItems()
					.addListener((Change<? extends TreeItem<GradeItem>> g) -> updateGradesChart());

			// Mostramos nº participantes
			lblCountParticipants.setText(I18n.get("label.participants")
					+ controller.getActualCourse().getEnrolledUsersCount());

			// Mostramos Usuario logeado y su imagen
			lblActualUser.setText(
					I18n.get("label.user") + " " + controller.getUser().getFullName());
			userPhoto.setImage(controller.getUser().getUserPhoto());

			// Mostramos Curso actual
			lblActualCourse.setText(I18n.get("label.course") + " "
					+ controller.getActualCourse().getFullName());

			// Mostramos Host actual
			lblActualHost.setText(I18n.get("label.host") + " " + controller.getHost());
		} catch (Exception e) {
			logger.error("Error en la inicialización.", e);
		}
	}

	public void initLogOptionsFilter() {

		textFieldMax.textProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue.matches("\\d+")) {
				updateMaxScale(Long.parseLong(newValue));
			} else if (newValue.isEmpty()) {
				updateMaxScale(1L);
			} else {
				textFieldMax.setText(oldValue);
			}
		});

		// añadimos los elementos de la enumeracion en el choicebox
		ObservableList<GroupByAbstract<?>> typeTimes = FXCollections
				.observableArrayList(controller.getActualCourse().getLogStats().getList());
		choiceBoxDate.setItems(typeTimes);
		choiceBoxDate.getSelectionModel().selectFirst();
		selectedChoiceBoxDate = choiceBoxDate.getValue();

		choiceBoxDate.valueProperty().addListener((ov, oldValue, newValue) -> applyFilterLogs(null));

		// traduccion de los elementos del choicebox
		choiceBoxDate.setConverter(new StringConverter<GroupByAbstract<?>>() {
			@Override
			public GroupByAbstract<?> fromString(String typeTimes) {
				return null;// no se va a usar en un choiceBox.
			}

			@Override
			public String toString(GroupByAbstract<?> typeTimes) {
				return I18n.get("choiceBox." + typeTimes);
			}
		});

		LocalDate lastLogDate = controller.getActualCourse().getLogs().getLastDatetime().toLocalDate();
		datePickerStart.setValue(lastLogDate.minusWeeks(1));
		datePickerEnd.setValue(lastLogDate);

		dateStart = datePickerStart.getValue();
		dateEnd = datePickerEnd.getValue();

		datePickerStart.valueProperty()
				.addListener((ov, oldValue, newValue) -> enableFilterLogButton(dateStart, newValue, dateEnd,
						datePickerEnd.getValue()));

		datePickerEnd.valueProperty()
				.addListener((ov, oldValue, newValue) -> enableFilterLogButton(dateStart, datePickerStart.getValue(),
						dateEnd, newValue));

		filterLogButton.setDisable(true);

		optionsUbuLogs.setVisible(false);
		optionsUbuLogs.setManaged(false);

	}

	private void updateMaxScale(long value) {

		webViewChartsEngine.executeScript("changeYMaxStackedBar(" + value + ")");

	}

	private void enableFilterLogButton(
			LocalDate dateStartOld, LocalDate dateStartNew, LocalDate dateEndOld, LocalDate dateEndNew) {
		if (dateStartOld.equals(dateStartNew)
				&& dateEndOld.equals(dateEndNew)) {
			filterLogButton.setDisable(true);
		} else {
			filterLogButton.setDisable(false);
		}
	}

	/**
	 * Inicializa la lista de componentes de la pestaña Registros
	 */
	public void initTabLogs() {

		tabUbuLogs.setOnSelectionChanged(event -> setTablogs(event));

		tabUbuLogsComponent.setOnSelectionChanged(event -> {
			updateLogsChart();
			findMax();
		});

		tabUbuLogsEvent.setOnSelectionChanged(event -> {
			updateLogsChart();
			findMax();
		});

		initListViewComponents();
		initListViewComponentsEvents();
	}

	public void initListViewComponents() {

		listViewComponents.getSelectionModel().getSelectedItems()
				.addListener((Change<? extends Component> c) -> {
					updateLogsChart();
					findMax();
				});

		// Cambiamos el nombre de los elementos en funcion de la internacionalizacion y
		// ponemos un icono
		listViewComponents.setCellFactory(callback -> new ListCell<Component>() {
			@Override
			public void updateItem(Component component, boolean empty) {
				super.updateItem(component, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					setText(I18n.get(component));
					try {
						Image image = new Image("/img/" + component + ".png");
						setGraphic(new ImageView(image));
					} catch (Exception e) {
						setGraphic(null);
					}
				}
			}
		});

		List<Component> uniqueComponents = controller.getActualCourse().getUniqueComponents();

		// Ordenamos los componentes segun los nombres internacionalizados
		uniqueComponents.sort(Comparator.comparing((Component c) -> I18n.get(c)));

		ObservableList<Component> observableListComponents = FXCollections.observableArrayList(uniqueComponents);
		FilteredList<Component> filterComponents = new FilteredList<>(observableListComponents);
		listViewComponents.setItems(filterComponents);
		listViewComponents.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// ponemos un listener al cuadro de texto para que se filtre el list view en
		// tiempo real
		componentTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
			filterComponents.setPredicate(component -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				String textField = newValue.toLowerCase();
				return I18n.get(component).toLowerCase().contains(textField);
			});
		}));

	}

	public void initListViewComponentsEvents() {
		listViewEvents.getSelectionModel().getSelectedItems()
				.addListener((Change<? extends ComponentEvent> c) -> {
					updateLogsChart();
					findMax();
				});

		// Cambiamos el nombre de los elementos en funcion de la internacionalizacion y
		// ponemos un icono
		listViewEvents.setCellFactory(callback -> new ListCell<ComponentEvent>() {

			@Override
			public void updateItem(ComponentEvent componentEvent, boolean empty) {
				super.updateItem(componentEvent, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					setText(I18n.get(componentEvent.getComponent()) + " - "
							+ I18n.get(componentEvent.getEventName()));
					try {
						Image image = new Image("/img/" + componentEvent.getComponent() + ".png");
						setGraphic(new ImageView(image));
					} catch (Exception e) {
						setGraphic(null);
					}
				}
			}
		});

		List<ComponentEvent> uniqueComponentsEvents = controller.getActualCourse().getUniqueComponentsEvents();

		// Ordenamos los componentes segun los nombres internacionalizados
		uniqueComponentsEvents
				.sort(Comparator.comparing((ComponentEvent c) -> I18n.get(c.getComponent()))
						.thenComparing((ComponentEvent c) -> I18n.get(c.getEventName())));

		ObservableList<ComponentEvent> observableListComponents = FXCollections
				.observableArrayList(uniqueComponentsEvents);
		FilteredList<ComponentEvent> filterComponentsEvents = new FilteredList<>(observableListComponents);
		listViewEvents.setItems(filterComponentsEvents);
		listViewEvents.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		componentEventTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
			filterComponentsEvents.setPredicate(componentEvent -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				String textField = newValue.toLowerCase();
				return I18n.get(componentEvent.getComponent()).toLowerCase().contains(textField)
						|| I18n.get(componentEvent.getEventName()).toLowerCase().contains(textField);
			});
		}));

	}

	private void updateLogsChart() {
		if (tabUbuLogsComponent.isSelected()) {
			updateComponentsChart();
		} else if (tabUbuLogsEvent.isSelected()) {
			updateComponentsEventsChart();
		}

	}

	private void updateComponentsChart() {

		String stackedbardataset = stackedBarDatasetComponent.createData(
				listParticipants.getSelectionModel().getSelectedItems(),
				listViewComponents.getSelectionModel().getSelectedItems(), selectedChoiceBoxDate, dateStart, dateEnd);
		logger.info("Dataset para el stacked bar de componentes solo en JS: " + stackedbardataset);
		webViewChartsEngine.executeScript("updateChart('stackedBar'," + stackedbardataset + ")");
	}

	private void updateComponentsEventsChart() {
		String stackedbardataset = stackedBarDatasetComponentEvent.createData(
				listParticipants.getSelectionModel().getSelectedItems(),
				listViewEvents.getSelectionModel().getSelectedItems(), selectedChoiceBoxDate, dateStart, dateEnd);

		logger.info("Dataset para el stacked bar de componentes y eventos en JS: " + stackedbardataset);
		webViewChartsEngine.executeScript("updateChart('stackedBar'," + stackedbardataset + ")");

	}

	public void setTablogs(Event event) {
		if (!tabUbuLogs.isSelected()) {
			optionsUbuLogs.setVisible(false);
			optionsUbuLogs.setManaged(false);
			return;
		}
		optionsUbuLogs.setVisible(true);
		optionsUbuLogs.setManaged(true);

		updateLogsChart();
		findMax();
		webViewChartsEngine.executeScript("manageLogsButtons()");

	}

	private void findMax() {
		long maxYAxis = 0L;
		if (tabUbuLogsComponent.isSelected()) {
			maxYAxis = selectedChoiceBoxDate.getMaxComponent(listViewComponents.getSelectionModel().getSelectedItems(),
					dateStart, dateEnd);
		} else if (tabUbuLogsEvent.isSelected()) {
			maxYAxis = selectedChoiceBoxDate
					.getMaxComponentEvent(listViewEvents.getSelectionModel().getSelectedItems(), dateStart, dateEnd);
		}
		textFieldMax.setText(Long.toString(maxYAxis));
	}

	public void initTabGrades() {
		tabPane.getSelectionModel().select(tabUbuGrades);
		tabUbuGrades.setOnSelectionChanged(event -> setTabGrades(event));

	}

	public void setTabGrades(Event event) {
		if (!tabUbuGrades.isSelected()) {
			return;
		}

		updateGradesChart();
		webViewChartsEngine.executeScript("manageGradesButtons()");

	}

	@FXML
	public void applyFilterLogs(ActionEvent event) {

		LocalDate start = datePickerStart.getValue();
		LocalDate end = datePickerEnd.getValue();

		if (start == null || end == null || end.isBefore(start)) {
			errorWindow("La fecha de fin es anterior a la fecha de inicio.", false);
			return;
		}

		filterLogButton.setDisable(true);
		selectedChoiceBoxDate = choiceBoxDate.getSelectionModel().getSelectedItem();
		dateStart = start;
		dateEnd = end;

		updateLogsChart();
		findMax();
	}

	/**
	 * Filtra los participantes según el rol, el grupo y el patrón indicados
	 */
	public void filterParticipants() {
		try {
			boolean roleYes;
			boolean groupYes;
			boolean patternYes;
			Set<EnrolledUser> users = controller.getActualCourse().getEnrolledUsers();
			// Cargamos la lista de los roles
			List<EnrolledUser> nameUsers = new ArrayList<>();
			// Obtenemos los participantes que tienen el rol elegido
			for (EnrolledUser user : users) {
				// Filtrado por rol:
				roleYes = false;
				Set<Role> roles = user.getRoles();
				// Si no tiene rol
				if (roles.isEmpty() && filterRole.equals(TODOS)) {
					roleYes = true;
				} else {
					for (Role role : roles) {
						// Comprobamos si el usuario pasa el filtro de "rol"
						if (role.getName().equals(filterRole) || filterRole.equals(TODOS)) {
							roleYes = true;
						}
					}
				}
				// Filtrado por grupo:
				groupYes = false;
				Set<Group> groups = user.getGroups();
				if (groups.isEmpty() && filterGroup.equals(TODOS)) {
					groupYes = true;
				} else {
					for (Group group : groups) {
						// Comprobamos si el usuario pasa el filtro de "grupo"
						if (group.getName().equals(filterGroup) || filterGroup.equals(TODOS)) {
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
					Matcher match = pattern.matcher(user.getFullName().toLowerCase());
					if (match.find()) {
						patternYes = true;
					}
				}
				// Si el usuario se corresponde con los filtros
				if (groupYes && roleYes && patternYes)
					nameUsers.add(user);
			}
			enrList = FXCollections.observableArrayList(nameUsers);
		} catch (Exception e) {
			logger.error("Error al filtrar los participantes: {}", e);
		}
		listParticipants.setItems(enrList);
		// Actualizamos los gráficos al cambiar el grupo
		updateGradesChart();
	}

	/**
	 * Rellena el árbol de actividades (GradeItems). Obtiene los hijos de la línea
	 * pasada por parámetro, los transforma en treeitems y los establece como hijos
	 * del elemento treeItem equivalente de line
	 * 
	 * @param parent
	 *            El padre al que añadir los elementos.
	 * @param line
	 *            La linea con los elementos a añadir.
	 */
	public void setTreeview(TreeItem<GradeItem> parent, GradeItem line) {
		for (int j = 0; j < line.getChildren().size(); j++) {
			TreeItem<GradeItem> item = new TreeItem<>(line.getChildren().get(j));
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
	public static void setIcon(TreeItem<GradeItem> item) {
		String path = null;
		try {
			path = "/img/" + item.getValue().getItemModule().getModName() + ".png";
			item.setGraphic(new ImageView(new Image(path)));
		} catch (Exception e) {
			item.setGraphic(new ImageView(ERROR_ICON));
			logger.error("No se ha podido cargar la imagen del elemento " + item + "en la ruta " + path + ") : {}", e);
		}
	}

	/**
	 * Filtra la lista de actividades del calificador según el tipo y el patrón
	 * introducidos.
	 */
	public void filterCalifications() {
		try {
			GradeItem root = controller.getActualCourse().getRootGradeItem();
			Set<GradeItem> gradeItems = controller.getActualCourse().getGradeItems();
			// Establecemos la raiz del Treeview
			TreeItem<GradeItem> treeItemRoot = new TreeItem<>(root);
			MainController.setIcon(treeItemRoot);
			// Llamamos recursivamente para llenar el Treeview
			if (filterType.equals(TODOS) && patternCalifications.equals("")) {
				// Sin filtro y sin patrón
				for (int k = 0; k < root.getChildren().size(); k++) {
					TreeItem<GradeItem> item = new TreeItem<>(root.getChildren().get(k));
					MainController.setIcon(item);
					treeItemRoot.getChildren().add(item);
					treeItemRoot.setExpanded(true);
					setTreeview(item, root.getChildren().get(k));
				}
			} else { // Con filtro
				for (GradeItem gradeItem : gradeItems) {
					TreeItem<GradeItem> item = new TreeItem<>(gradeItem);
					boolean activityYes = false;
					if (filterType.equals(gradeItem.getItemModule().toString()) || filterType.equals(TODOS)) {
						activityYes = true;
					}
					Pattern pattern = Pattern.compile(patternCalifications.toLowerCase());
					Matcher match = pattern.matcher(gradeItem.getItemname().toLowerCase());
					boolean patternYes = false;
					if (patternCalifications.equals("") || match.find()) {
						patternYes = true;
					}
					if (activityYes && patternYes) {
						MainController.setIcon(item);
						treeItemRoot.getChildren().add(item);
					}
					treeItemRoot.setExpanded(true);
				}
			}
			// Establecemos la raiz del treeview
			tvwGradeReport.setRoot(treeItemRoot);
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
	 *             excepción
	 */
	public void saveChart(ActionEvent actionEvent) throws Exception {
		File file = new File("chart.png");

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Guardar gráfico");

		fileChooser.setInitialFileName("chart.png");
		fileChooser.setInitialDirectory(file.getParentFile());
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(".png", "*.png"));
		try {
			file = fileChooser.showSaveDialog(controller.getStage());
			if (file != null) {
				String str = (String) webViewChartsEngine.executeScript("exportCurrentElemet()");
				byte[] imgdata = DatatypeConverter.parseBase64Binary(str.substring(str.indexOf(',') + 1));
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imgdata));
				ImageIO.write(bufferedImage, "png", file);
			}
		} catch (Exception e) {
			logger.error("Error al guardar el gráfico: {}", e);
			errorWindow(I18n.get("error.savechart"), false);
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
		File file = fileChooser.showSaveDialog(controller.getStage());
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
				String generalDataSet = generateGradesDataSet();
				out.println(
						"\r\nvar userLang = \"" + I18n.getResourceBundle().getLocale().toString() + "\";\r\n");
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
				errorWindow(I18n.get("error.saveallcharts"), false);
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
	 *             exception
	 */
	public void changeCourse(ActionEvent actionEvent) throws Exception {
		logger.info("Cambiando de asignatura...");
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
			FXMLLoader loader = new FXMLLoader(sceneFXML, I18n.getResourceBundle());
			Parent root = loader.load();
			Scene scene = new Scene(root);
			controller.getStage().close();
			controller.setStage(new Stage());
			controller.getStage().setScene(scene);
			controller.getStage().getIcons().add(new Image("/img/logo_min.png"));
			controller.getStage().setTitle(Controller.APP_NAME);
			controller.getStage().show();
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
			Desktop.getDesktop().browse(new URL("https://github.com/yjx0003/UBUMonitor").toURI());
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
		controller.getStage().close();
	}

	/**
	 * Genera el dataset para la tabal de calificaciones.
	 * 
	 * @return El dataset.
	 */
	private String generateTableData() {
		// Lista de alumnos y calificaciones seleccionadas
		ObservableList<EnrolledUser> selectedParticipants = listParticipants.getSelectionModel().getSelectedItems();
		ObservableList<TreeItem<GradeItem>> selectedGRL = tvwGradeReport.getSelectionModel().getSelectedItems();

		StringBuilder tableData = new StringBuilder();
		tableData.append("[['" + I18n.get("chartlabel.name") + "'");
		Boolean firstElement = true;

		// Por cada ítem seleccionado lo añadimos como label
		for (TreeItem<GradeItem> structTree : selectedGRL) {
			tableData.append(",'" + escapeJavaScriptText(structTree.getValue().toString()) + "'");
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
			for (TreeItem<GradeItem> structTree : selectedGRL) {
				GradeItem actualLine = structTree.getValue();
				double grade = actualLine.getEnrolledUserGrade(actualUser);
				// Si es numérico lo graficamos y lo mostramos en la tabla
				if (!Double.isNaN(grade)) {
					grade = Math.round(grade * 100.0) / 100.0;
					// Añadimos la nota al gráfico
					tableData.append(",{v:" + grade + ", f:'" + grade + "/" + actualLine.getGrademax() + "'}");
				} else {
					tableData.append(",{v:0, f:'" + grade + "'}");
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
		tableData.append(",['" + I18n.get("chartlabel.tableMean") + "'");
		for (TreeItem<GradeItem> structTree : tvwGradeReport.getSelectionModel().getSelectedItems()) {
			String grade = stats.getElementMean(stats.getGeneralStats(), structTree.getValue());
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
				tableData.append(",['" + I18n.get("chartlabel.tableGroupMean") + " "
						+ grupo.getText() + "'");
				for (TreeItem<GradeItem> structTree : tvwGradeReport.getSelectionModel().getSelectedItems()) {
					String grade = stats.getElementMean(stats.getGroupStats(grupo.getText()),
							structTree.getValue());
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
	private String generateGradesDataSet() {
		// Lista de alumnos y calificaciones seleccionadas

		List<EnrolledUser> selectedParticipants = new ArrayList<>(
				listParticipants.getSelectionModel().getSelectedItems());
		List<TreeItem<GradeItem>> selectedGRL = new ArrayList<>(tvwGradeReport.getSelectionModel().getSelectedItems());
		int countA = 0;
		boolean firstUser = true;
		boolean firstGrade = true;
		StringBuilder dataSet = new StringBuilder();
		StringBuilder labels = new StringBuilder();

		logger.debug("Selected participant: {}", selectedParticipants.size());
		// Por cada usuario seleccionado
		for (EnrolledUser actualUser : selectedParticipants) {
			// BUG when we deselect the penultimate student, some student can have null
			// value. TODO
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
			for (TreeItem<GradeItem> structTree : selectedGRL) {
				countA++;
				try {
					if (structTree != null) {
						GradeItem actualLine = structTree.getValue();
						double calculatedGrade;
						if (countA == countB) {
							countB++;
							// Añadidimos el nombre del elemento como label
							if (firstGrade) {
								labels.append("'" + escapeJavaScriptText(structTree.getValue().toString()) + "'");
							} else {
								labels.append(",'" + escapeJavaScriptText(structTree.getValue().toString()) + "'");
							}
						}
						calculatedGrade = Math.round(actualLine.adjustTo10(actualUser) * 100) / (double) 100;

						if (firstGrade) {
							dataSet.append(calculatedGrade);
							firstGrade = false;
						} else {
							dataSet.append("," + calculatedGrade);
						}
					}
				} catch (Exception e) {
					logger.error("Error en la construcción del dataset.", e);
					errorWindow(I18n.get("error.generatedataset"), false);
				}
			}
			dataSet.append("]," + "backgroundColor: 'red'," + "borderColor: 'red'," + "pointBorderColor: 'red',"
					+ "pointBackgroundColor: 'red'," + "borderWidth: 3," + "fill: false}");
		}

		return "{ labels:[" + labels + "],datasets: [" + dataSet + "]}";
	}

	/**
	 * Escape the commas in the text. For example 'Law D'Hont' is changed to 'Law
	 * D\'Hont'.
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
		Map<GradeItem, DescriptiveStatistics> boxPlotStats;
		if (group.equals(TODOS)) {
			boxPlotStats = stats.getGeneralStats();
		} else {
			boxPlotStats = stats.getGroupStats(group);
		}

		StringBuilder labels = new StringBuilder();
		StringBuilder upperLimit = new StringBuilder("{label:'" + I18n.get("chartlabel.upperlimit") + "',data: [");
		StringBuilder median = new StringBuilder("{label:'" + I18n.get("chartlabel.median") + "',data: [");
		StringBuilder lowerLimit = new StringBuilder("{label:'" + I18n.get("chartlabel.lowerlimit") + "',data: [");
		StringBuilder firstQuartile = new StringBuilder(
				"{label:'" + I18n.get("chartlabel.firstquartile") + "',data: [");
		StringBuilder thirdQuartile = new StringBuilder(
				"{label:'" + I18n.get("chartlabel.thirdquartile") + "',data: [");
		boolean firstLabel = true;
		boolean firstGrade = true;
		GradeItem gradeItem;

		ObservableList<TreeItem<GradeItem>> selectedGRL = tvwGradeReport.getSelectionModel().getSelectedItems();

		for (TreeItem<GradeItem> structTree : selectedGRL) {
			if (firstLabel) {
				labels.append("'" + escapeJavaScriptText(structTree.getValue().toString()) + "'");
				firstLabel = false;
			} else {
				labels.append(",'" + escapeJavaScriptText(structTree.getValue().toString()) + "'");
			}

			gradeItem = structTree.getValue();
			if (firstGrade) {
				upperLimit.append(stats.getUpperLimit(boxPlotStats, gradeItem));
				median.append(stats.getMedian(boxPlotStats, gradeItem));
				lowerLimit.append(stats.getLowerLimit(boxPlotStats, gradeItem));
				firstQuartile.append(stats.getElementPercentile(boxPlotStats, gradeItem, 25));
				thirdQuartile.append(stats.getElementPercentile(boxPlotStats, gradeItem, 75));
				firstGrade = false;
			} else {
				upperLimit.append("," + stats.getUpperLimit(boxPlotStats, gradeItem));
				median.append("," + stats.getMedian(boxPlotStats, gradeItem));
				lowerLimit.append("," + stats.getLowerLimit(boxPlotStats, gradeItem));
				firstQuartile.append("," + stats.getElementPercentile(boxPlotStats, gradeItem, 25));
				thirdQuartile.append("," + stats.getElementPercentile(boxPlotStats, gradeItem, 75));
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

	private String generateAtypicalValuesDataSet(Map<GradeItem, DescriptiveStatistics> statistics) {
		StringBuilder dataset = new StringBuilder();
		GradeItem gradeItem;

		ObservableList<TreeItem<GradeItem>> selectedGRL = tvwGradeReport.getSelectionModel().getSelectedItems();

		for (int i = 0; i < selectedGRL.size(); i++) {
			gradeItem = selectedGRL.get(i).getValue();
			List<String> atypicalValues = stats.getAtypicalValues(statistics, gradeItem);

			if (dataset.length() != 0 && !atypicalValues.isEmpty()) {
				dataset.append(",");
			}

			for (int j = 0; j < atypicalValues.size(); j++) {
				if (j != 0) {
					dataset.append(",");
				}
				dataset.append(
						"{label:'" + I18n.get("chartlabel.atypicalValue")
								+ "',data: [");
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
		Map<GradeItem, DescriptiveStatistics> meanStats;
		Boolean firstElement = true;
		StringBuilder meanDataset = new StringBuilder();
		String color;

		if (group.equals(TODOS)) {
			meanStats = stats.getGeneralStats();
			meanDataset.append(
					"{label:'" + I18n.get("chartlabel.generalMean") + "',data:[");
			color = "rgba(255, 152, 0, ";
		} else {
			meanStats = stats.getGroupStats(group);
			meanDataset
					.append("{label:'" + I18n.get("chartlabel.groupMean") + "',data:[");
			color = "rgba(0, 150, 136, ";
		}

		for (TreeItem<GradeItem> structTree : tvwGradeReport.getSelectionModel().getSelectedItems()) {
			if (firstElement) {
				meanDataset.append(stats.getElementMean(meanStats, structTree.getValue()));
				firstElement = false;
			} else {
				meanDataset.append("," + stats.getElementMean(meanStats, structTree.getValue()));
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
			errorWindow(I18n.get("error.generateCharts"), true);
		}
	}

	/**
	 * Actualiza los gráficos.
	 */
	private void updateGradesChart() {
		try {
			String data = generateGradesDataSet();
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
			errorWindow(I18n.get("error.generateCharts"), false); // FIX RMS Review true
																	// or false
		} catch (Exception e) {
			logger.error("Error general al generar los gráficos.", e);
			errorWindow(I18n.get("error.generateCharts"), false);
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
	private void errorWindow(String mensaje, boolean exit) {
		Alert alert = new Alert(AlertType.ERROR);

		alert.setTitle(Controller.APP_NAME);
		alert.setHeaderText("Error");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(controller.getStage());
		alert.getDialogPane().setContentText(mensaje);

		if (exit) {
			alert.getButtonTypes().setAll(ButtonType.CLOSE);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.CLOSE)
				controller.getStage().close();
		} else {
			alert.showAndWait();
		}

	}

}