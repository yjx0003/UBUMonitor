package controllers;

import java.awt.Desktop;
import java.awt.Desktop.Action;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
import controllers.datasets.StackedBarDataSetSection;
import controllers.datasets.StackedBarDatasSetCourseModule;
import controllers.ubulogs.GroupByAbstract;
import export.CSVExport;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Component;
import model.ComponentEvent;
import model.CourseModule;
import model.EnrolledUser;
import model.GradeItem;
import model.Group;
import model.ModuleType;
import model.Role;
import model.Section;
import model.Stats;
import netscape.javascript.JSException;

/**
 * Clase controlador de la ventana principal
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class MainController implements Initializable {

	private static final String ALL = "text.all";

	private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

	private static final Image NONE_ICON = new Image("/img/manual.png");

	private Controller controller = Controller.getInstance();

	@FXML // Curso actual
	private Label lblActualCourse;
	@FXML // Usuario actual
	private Label lblActualUser;
	@FXML // Host actual
	private Label lblActualHost;
	@FXML // Imagen del usuario
	private ImageView userPhoto;

	@FXML // Nº participantes
	private Label lblCountParticipants;
	@FXML // lista de participantes
	private ListView<EnrolledUser> listParticipants;
	private FilteredList<EnrolledUser> filteredEnrolledList;

	@FXML // Botón filtro por rol
	private ChoiceBox<Role> slcRole;

	@FXML // Botón filtro por grupo
	private ChoiceBox<Group> slcGroup;

	@FXML // Entrada de filtro de usuarios por patrón
	private TextField tfdParticipants;

	@FXML // Vista en árbol de actividades
	private TreeView<GradeItem> tvwGradeReport;

	@FXML // Entrada de filtro de actividades por patrón
	private TextField tfdItems;

	@FXML // Botón filtro por tipo de modulo
	private ChoiceBox<ModuleType> slcType;

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
	private TabPane tabPaneUbuLogs;

	@FXML
	private Tab tabUbuLogsComponent;

	@FXML
	private Tab tabUbuLogsEvent;

	@FXML
	private Tab tabUbuLogsSection;

	@FXML
	private Tab tabUbuLogsCourseModule;

	@FXML
	private TextField componentTextField;

	@FXML
	private TextField componentEventTextField;

	@FXML
	private TextField sectionTextField;
	@FXML
	private TextField courseModuleTextField;

	@FXML
	private ListView<Component> listViewComponents;

	@FXML
	private ListView<ComponentEvent> listViewEvents;

	@FXML
	private ListView<Section> listViewSection;
	@FXML
	private ListView<CourseModule> listViewCourseModule;

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

	private Stats stats;

	/**
	 * Muestra los usuarios matriculados en el curso, así como las actividades de
	 * las que se compone.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			LOGGER.info(
					"Completada la carga del curso {}", controller.getActualCourse().getFullName());

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
			WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) ->

			LOGGER.error("Error en la consola de JS: {} [{} : {}] ", message, sourceId, lineNumber)

			);

			initLogOptionsFilter();

			initTabGrades();
			initTabLogs();

			initEnrolledUsers();

			initiGradeItems();

			// Mostramos Usuario logeado y su imagen
			lblActualUser.setText(
					I18n.get("label.user") + " " + controller.getUser().getFullName());
			userPhoto.setImage(controller.getUser().getUserPhoto());

			// Mostramos Curso actual
			lblActualCourse.setText(I18n.get("label.course") + " "
					+ controller.getActualCourse().getFullName());

			// Mostramos Host actual
			lblActualHost.setText(I18n.get("label.host") + " " + controller.getUrlHost());
		} catch (Exception e) {
			LOGGER.error("Error en la inicialización.", e);
		}
	}

	private void initiGradeItems() {

		ObservableList<ModuleType> observableListModules = FXCollections.observableArrayList();
		observableListModules.add(null); // opción por si no se filtra por grupo
		observableListModules.addAll(controller.getActualCourse().getUniqueModuleTypes());

		slcType.setItems(observableListModules);
		slcType.getSelectionModel().selectFirst(); // seleccionamos el nulo
		slcType.valueProperty().addListener((ov, oldValue, newValue) -> filterCalifications());

		slcType.setConverter(new StringConverter<ModuleType>() {
			@Override
			public ModuleType fromString(String moduleType) {
				return null;// no se va a usar en un choiceBox.
			}

			@Override
			public String toString(ModuleType moduleType) {
				if (moduleType == null) {
					return I18n.get(ALL);
				}
				return I18n.get(moduleType);
			}
		});

		// Inicializamos el listener del textField del calificador
		tfdItems.setOnAction((ActionEvent event) -> filterCalifications());
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
	}

	private void initEnrolledUsers() {
		// Mostramos nº participantes
		lblCountParticipants.setText(I18n.get("label.participants")
				+ controller.getActualCourse().getEnrolledUsersCount());

		ObservableList<Group> observableListGroups = FXCollections.observableArrayList();
		observableListGroups.add(null); // opción por si no se filtra por grupo
		observableListGroups.addAll(controller.getActualCourse().getGroups());

		slcGroup.setItems(observableListGroups);
		slcGroup.getSelectionModel().selectFirst(); // seleccionamos el nulo
		slcGroup.valueProperty().addListener((ov, oldValue, newValue) -> filterParticipants());

		slcGroup.setConverter(new StringConverter<Group>() {
			@Override
			public Group fromString(String typeTimes) {
				return null;// no se va a usar en un choiceBox.
			}

			@Override
			public String toString(Group group) {
				if (group == null) {
					return I18n.get(ALL);
				}
				return group.getGroupName();
			}
		});

		ObservableList<Role> observableListRoles = FXCollections.observableArrayList();
		observableListRoles.add(null); // opción por si no se filtra por grupo
		observableListRoles.addAll(controller.getActualCourse().getRoles());

		slcRole.setItems(observableListRoles);
		slcRole.getSelectionModel().selectFirst(); // seleccionamos el nulo
		slcRole.valueProperty().addListener((ov, oldValue, newValue) -> filterParticipants());

		slcRole.setConverter(new StringConverter<Role>() {
			@Override
			public Role fromString(String role) {
				return null;// no se va a usar en un choiceBox.
			}

			@Override
			public String toString(Role role) {
				if (role == null) {
					return I18n.get(ALL);
				}
				return role.getRoleName().isEmpty() ? role.getRoleShortName() : role.getRoleName();
			}
		});

		tfdParticipants.setOnAction((ActionEvent event) -> filterParticipants());

		initEnrolledUsersListView();

	}

	/**
	 * Inicializa la lista de usuarios.
	 */
	private void initEnrolledUsersListView() {

		Set<EnrolledUser> users = controller.getActualCourse().getEnrolledUsers();

		ObservableList<EnrolledUser> observableUsers = FXCollections.observableArrayList(users);
		observableUsers.sort(EnrolledUser.NAME_COMPARATOR);
		filteredEnrolledList = new FilteredList<>(observableUsers);

		listParticipants.setPlaceholder(new Label(I18n.get("text.nousers")));

		// Activamos la selección múltiple en la lista de participantes
		listParticipants.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		listParticipants.getSelectionModel().getSelectedItems()
				.addListener(
						(Change<? extends EnrolledUser> usersSelected) -> {
							updateGradesChart();
							updateLogsChart();
						});

		/// Mostramos la lista de participantes
		listParticipants.setItems(filteredEnrolledList);

		listParticipants.setCellFactory(callback -> new ListCell<EnrolledUser>() {
			@Override
			public void updateItem(EnrolledUser user, boolean empty) {
				super.updateItem(user, empty);
				if (empty || user == null) {
					setText(null);
					setGraphic(null);
				} else {
					// Instant lastCourseAccess = user.getLastcourseaccess(); // cuando moodle ya
					// devuelva la fecha de ultimo acceso al curso.
					Instant lastCourseAccess = user.getLastaccess();
					Instant lastLogInstant = Instant.now();
					setText(user + "\n" + I18n.get("text.lastaccess")
							+ formatDates(lastCourseAccess, lastLogInstant));
					try {
						Image image = new Image(new ByteArrayInputStream(user.getImageBytes()), 50, 50, false,
								false);
						setGraphic(new ImageView(image));
					} catch (Exception e) {
						LOGGER.error("No se ha podido cargar la imagen de: {}", user);
						setGraphic(new ImageView(new Image("/img/default_user.png")));
					}
				}
			}

		});
	}

	/**
	 * Devuelve la diferencia entre dos instantes, por dias, hora, minutos o
	 * segundos siempre y cuando no sean 0. Si la diferencias de dias es 0 se busca
	 * por horas, y asi consecutivamente.
	 * 
	 * @param lastCourseAccess
	 *            fecha inicio
	 * @param lastLogInstant
	 *            fecha fin
	 * @return texto con el numero y el tipo de tiempo
	 */
	private String formatDates(Instant lastCourseAccess, Instant lastLogInstant) {

		if (lastCourseAccess == null || lastCourseAccess.getEpochSecond() == -1L) {
			return " " + I18n.get("label.never");
		}

		if (lastCourseAccess.getEpochSecond() == 0L) {
			return " " + I18n.get("text.never");
		}

		long time;

		if ((time = betweenDates(ChronoUnit.DAYS, lastCourseAccess, lastLogInstant)) != 0L) {
			return time + " " + I18n.get("text.days");
		}
		if ((time = betweenDates(ChronoUnit.HOURS, lastCourseAccess, lastLogInstant)) != 0L) {
			return time + " " + I18n.get("text.hours");
		}
		if ((time = betweenDates(ChronoUnit.MINUTES, lastCourseAccess, lastLogInstant)) != 0L) {
			return time + " " + I18n.get("text.minutes");
		}

		return betweenDates(ChronoUnit.SECONDS, lastCourseAccess, lastLogInstant) + " " + I18n.get("text.seconds");
	}

	/**
	 * Devuelve la difernecia absoluta entre dos instantes dependiendo de que sea el
	 * tipo
	 * 
	 * @param type
	 *            dias, horas, minutos
	 * @param lastCourseAccess
	 *            instante inicio
	 * @param lastLogInstant
	 *            instante fin
	 * @return numero de diferencia absoluta
	 */
	private long betweenDates(ChronoUnit type, Instant lastCourseAccess, Instant lastLogInstant) {
		return type.between(lastCourseAccess, lastLogInstant);
	}

	/**
	 * Inicializa los elementos de las opciones de logs.
	 */
	public void initLogOptionsFilter() {

		textFieldMax.textProperty().addListener((ov, oldValue, newValue) -> {
			if (newValue.matches("\\d+")) {
				updateMaxScale(Long.parseLong(newValue));
			} else if (newValue.isEmpty()) {
				updateMaxScale(1L);
			} else { // si no es un numero volvemos al valor anterior
				textFieldMax.setText(oldValue);
			}
		});

		// añadimos los elementos de la enumeracion en el choicebox
		ObservableList<GroupByAbstract<?>> typeTimes = FXCollections
				.observableArrayList(controller.getActualCourse().getLogStats().getList());
		choiceBoxDate.setItems(typeTimes);
		choiceBoxDate.getSelectionModel().selectFirst();
		selectedChoiceBoxDate = choiceBoxDate.getValue();

		choiceBoxDate.valueProperty().addListener((ov, oldValue, newValue) -> {
			applyFilterLogs();
			boolean useDatePicker = newValue.useDatePicker();
			datePickerStart.setDisable(!useDatePicker);
			datePickerEnd.setDisable(!useDatePicker);

		});

		// traduccion de los elementos del choicebox
		choiceBoxDate.setConverter(new StringConverter<GroupByAbstract<?>>() {
			@Override
			public GroupByAbstract<?> fromString(String typeTimes) {
				return null;// no se va a usar en un choiceBox.
			}

			@Override
			public String toString(GroupByAbstract<?> typeTimes) {
				return I18n.get(typeTimes.getTypeTime());
			}
		});

		LocalDate lastLogDate = controller.getActualCourse().getLogs().getLastDatetime().toLocalDate();
		datePickerStart.setValue(lastLogDate.minusWeeks(1));
		datePickerEnd.setValue(lastLogDate);

		datePickerStart.setOnAction(event -> applyFilterLogs());
		datePickerEnd.setOnAction(event -> applyFilterLogs());
		dateStart = datePickerStart.getValue();
		dateEnd = datePickerEnd.getValue();

		datePickerStart.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.isAfter(dateEnd) || date.isAfter(LocalDate.now()));
			}
		});

		datePickerEnd.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.isBefore(dateStart) || date.isAfter(LocalDate.now()));
			}
		});

		optionsUbuLogs.setVisible(false);
		optionsUbuLogs.setManaged(false);

	}

	/**
	 * Actualiza la escala maxima del eje y de los graficos de logs.
	 * 
	 * @param value
	 *            valor de escala maxima
	 */
	private void updateMaxScale(long value) {

		webViewChartsEngine.executeScript("changeYMaxStackedBar(" + value + ")");

	}

	/**
	 * Inicializa la lista de componentes de la pestaña Registros
	 */
	public void initTabLogs() {

		tabUbuLogs.setOnSelectionChanged(this::setTablogs);

		initTabLogs(tabUbuLogsComponent, tabUbuLogsEvent, tabUbuLogsSection, tabUbuLogsCourseModule);

		initListViewComponents();
		initListViewComponentsEvents();
		initListViewSections();
		initListViewCourseModules();

	}

	private void initTabLogs(Tab... tabs) {
		for (Tab tab : tabs) {
			tab.setOnSelectionChanged(event -> {
				if (tab.isSelected()) {
					updateLogsChart();
					findMax();
				}
			});
		}

	}

	/**
	 * Inicializa el listado de componentes de la pestaña Componentes en
	 */
	public void initListViewComponents() {
		// cada vez que se seleccione nuevos elementos del list view actualizamos la
		// grafica y la escala
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
						Image image = new Image(AppInfo.IMG_DIR + component + ".png");
						setGraphic(new ImageView(image));
					} catch (Exception e) {
						setGraphic(null);
					}
				}
			}
		});

		List<Component> uniqueComponents = controller.getActualCourse().getUniqueComponents();

		// Ordenamos los componentes segun los nombres internacionalizados
		uniqueComponents.sort(Comparator.comparing(I18n::get));

		ObservableList<Component> observableListComponents = FXCollections.observableArrayList(uniqueComponents);
		FilteredList<Component> filterComponents = new FilteredList<>(observableListComponents);
		listViewComponents.setItems(filterComponents);
		listViewComponents.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// ponemos un listener al cuadro de texto para que se filtre el list view en
		// tiempo real
		componentTextField.textProperty()
				.addListener(((observable, oldValue, newValue) -> filterComponents.setPredicate(component -> {
					if (newValue == null || newValue.isEmpty()) {
						return true;
					}
					String textField = newValue.toLowerCase();
					return I18n.get(component).toLowerCase().contains(textField);
				})));

	}

	/**
	 * Inicializa los elementos de la pestaña eventos.
	 */
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
						Image image = new Image(AppInfo.IMG_DIR + componentEvent.getComponent() + ".png");
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

		componentEventTextField.textProperty().addListener(
				((observable, oldValue, newValue) -> filterComponentsEvents.setPredicate(componentEvent -> {
					if (newValue == null || newValue.isEmpty()) {
						return true;
					}
					String textField = newValue.toLowerCase();
					return I18n.get(componentEvent.getComponent()).toLowerCase().contains(textField)
							|| I18n.get(componentEvent.getEventName()).toLowerCase().contains(textField);
				})));

	}

	/**
	 * Inicializa el listado de componentes de la pestaña Componentes en
	 */
	public void initListViewSections() {
		// cada vez que se seleccione nuevos elementos del list view actualizamos la
		// grafica y la escala
		listViewSection.getSelectionModel().getSelectedItems()
				.addListener((Change<? extends Section> section) -> {
					updateLogsChart();
					findMax();
				});

		// Cambiamos el nombre de los elementos en funcion de la internacionalizacion y
		// ponemos un icono
		listViewSection.setCellFactory(callback -> new ListCell<Section>() {
			@Override
			public void updateItem(Section section, boolean empty) {
				super.updateItem(section, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					String sectionName = section.getName();
					setText(sectionName == null || sectionName.isEmpty() ? I18n.get("text.sectionplaceholder") : sectionName);
					
					
					try {
						if (!section.isVisible()) {
							setTextFill(Color.GRAY);
						}
						
						String visible = section.isVisible() ? "visible" : "not_visible";
						
						Image image = new Image(AppInfo.IMG_DIR + visible + ".png");
						setGraphic(new ImageView(image));
					} catch (Exception e) {
						setGraphic(null);
					}
				}
			}
		});

		Set<Section> sections = controller.getActualCourse().getSections();

		ObservableList<Section> observableListComponents = FXCollections.observableArrayList(sections);
		FilteredList<Section> filterComponents = new FilteredList<>(observableListComponents);
		listViewSection.setItems(filterComponents);
		listViewSection.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// ponemos un listener al cuadro de texto para que se filtre el list view en
		// tiempo real
		sectionTextField.textProperty()
				.addListener(((observable, oldValue, newValue) -> filterComponents.setPredicate(section -> {
					if (newValue == null || newValue.isEmpty()) {
						return true;
					}
					String textField = newValue.toLowerCase();
					return section.getName().toLowerCase().contains(textField);
				})));

	}

	/**
	 * Inicializa el listado de componentes de la pestaña Componentes en
	 */
	public void initListViewCourseModules() {
		// cada vez que se seleccione nuevos elementos del list view actualizamos la
		// grafica y la escala
		listViewCourseModule.getSelectionModel().getSelectedItems()
				.addListener((Change<? extends CourseModule> courseModule) -> {
					updateLogsChart();
					findMax();
				});

		// Cambiamos el nombre de los elementos en funcion de la internacionalizacion y
		// ponemos un icono
		listViewCourseModule.setCellFactory(callback -> new ListCell<CourseModule>() {
			@Override
			public void updateItem(CourseModule courseModule, boolean empty) {
				super.updateItem(courseModule, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					setText(courseModule.getModuleName());
					if (!courseModule.isVisible()) {
						setTextFill(Color.GRAY);
					}
					try {
						Image image = new Image(AppInfo.IMG_DIR + courseModule.getModuleType().getModName() + ".png");
						setGraphic(new ImageView(image));
					} catch (Exception e) {
						setGraphic(null);
					}
				}
			}
		});

		Set<CourseModule> courseModules = controller.getActualCourse().getModules();

		ObservableList<CourseModule> observableListComponents = FXCollections.observableArrayList(courseModules);
		FilteredList<CourseModule> filterComponents = new FilteredList<>(observableListComponents);
		listViewCourseModule.setItems(filterComponents);
		listViewCourseModule.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// ponemos un listener al cuadro de texto para que se filtre el list view en
		// tiempo real
		courseModuleTextField.textProperty()
				.addListener(((observable, oldValue, newValue) -> filterComponents.setPredicate(cm -> {
					if (newValue == null || newValue.isEmpty()) {
						return true;
					}
					String textField = newValue.toLowerCase();
					return cm.getModuleName().toLowerCase().contains(textField);
				})));

	}

	/**
	 * Actualiza los graficos de log en funcion de que subpestaña esta seleccionada
	 */
	private void updateLogsChart() {
		if (!tabUbuLogs.isSelected()) {
			return;
		}

		String stackedbardataset = null;

		if (tabUbuLogsComponent.isSelected()) {
			stackedbardataset = StackedBarDataSetComponent.getInstance().createData(listParticipants.getItems(),
					listParticipants.getSelectionModel().getSelectedItems(),
					listViewComponents.getSelectionModel().getSelectedItems(), selectedChoiceBoxDate, dateStart,
					dateEnd);
		} else if (tabUbuLogsEvent.isSelected()) {
			stackedbardataset = StackedBarDataSetComponentEvent.getInstance().createData(listParticipants.getItems(),
					listParticipants.getSelectionModel().getSelectedItems(),
					listViewEvents.getSelectionModel().getSelectedItems(), selectedChoiceBoxDate, dateStart, dateEnd);
		} else if (tabUbuLogsSection.isSelected()) {
			stackedbardataset = StackedBarDataSetSection.getInstance().createData(listParticipants.getItems(),
					listParticipants.getSelectionModel().getSelectedItems(),
					listViewSection.getSelectionModel().getSelectedItems(), selectedChoiceBoxDate, dateStart, dateEnd);
		} else if (tabUbuLogsCourseModule.isSelected()) {
			stackedbardataset = StackedBarDatasSetCourseModule.getInstance().createData(listParticipants.getItems(),
					listParticipants.getSelectionModel().getSelectedItems(),
					listViewCourseModule.getSelectionModel().getSelectedItems(), selectedChoiceBoxDate, dateStart,
					dateEnd);
		}

		LOGGER.info("Dataset para el stacked bar en JS: {}", stackedbardataset);
		webViewChartsEngine.executeScript("updateChart('stackedBar'," + stackedbardataset + ")");

	}

	/**
	 * Metodo que se activa cuando se modifica la pestaña de logs o calificaciones
	 * 
	 * @param event
	 *            evento
	 */
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

	/**
	 * Busca el maximo de la escala Y y lo modifica.
	 */
	private void findMax() {
		if (!tabUbuLogs.isSelected()) {
			return;
		}

		long maxYAxis = 1L;
		if (tabUbuLogsComponent.isSelected()) {
			maxYAxis = selectedChoiceBoxDate.getComponents().getMaxElement(listParticipants.getItems(),
					listViewComponents.getSelectionModel().getSelectedItems(),
					dateStart, dateEnd);
		} else if (tabUbuLogsEvent.isSelected()) {
			maxYAxis = selectedChoiceBoxDate.getComponentsEvents().getMaxElement(
					listParticipants.getItems(),
					listViewEvents.getSelectionModel().getSelectedItems(), dateStart, dateEnd);
		} else if (tabUbuLogsSection.isSelected()) {
			maxYAxis = selectedChoiceBoxDate.getSections().getMaxElement(
					listParticipants.getItems(),
					listViewSection.getSelectionModel().getSelectedItems(), dateStart, dateEnd);
		} else if (tabUbuLogsCourseModule.isSelected()) {
			maxYAxis = selectedChoiceBoxDate.getCourseModules().getMaxElement(
					listParticipants.getItems(),
					listViewCourseModule.getSelectionModel().getSelectedItems(), dateStart, dateEnd);
		}
		textFieldMax.setText(Long.toString(maxYAxis));
	}

	/**
	 * Inicializa los elementos de las graficas de calificacion.
	 */
	public void initTabGrades() {
		tabPane.getSelectionModel().select(tabUbuGrades);
		tabUbuGrades.setOnSelectionChanged(this::setTabGrades);

	}

	/**
	 * Se activa cuando se cambia de pestaña de registros o calificaciones
	 * 
	 * @param event
	 *            evento
	 */
	public void setTabGrades(Event event) {
		if (!tabUbuGrades.isSelected()) {
			return;
		}

		updateGradesChart();
		webViewChartsEngine.executeScript("manageGradesButtons()");

	}

	/**
	 * Aplica los filtros de fecha a las graficas de log.
	 * 
	 * @param event
	 *            evento
	 */
	public void applyFilterLogs() {

		LocalDate start = datePickerStart.getValue();
		LocalDate end = datePickerEnd.getValue();

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

		Role rol = slcRole.getValue();
		Group group = slcGroup.getValue();
		String textField = tfdParticipants.getText().toLowerCase();

		filteredEnrolledList.setPredicate(
				e -> (rol == null || rol.contains(e)) && (group == null || group.contains(e)) &&
						(textField.isEmpty() || e.getFullName().toLowerCase().contains(textField)));
		// Mostramos nº participantes
		lblCountParticipants.setText(I18n.get("label.participants") + filteredEnrolledList.size());
		// Actualizamos los graficos
		updateGradesChart();
		updateLogsChart();
		findMax();
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
			path = AppInfo.IMG_DIR + item.getValue().getItemModule().getModName() + ".png";
			item.setGraphic(new ImageView(new Image(path)));
		} catch (Exception e) {
			item.setGraphic(new ImageView(NONE_ICON));
			LOGGER.warn("No se ha podido cargar la imagen del elemento " + item + "en la ruta " + path + ") : {}", e);
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
			ModuleType filterType = slcType.getValue();
			String patternCalifications = tfdItems.getText();

			if (filterType == null && patternCalifications.isEmpty()) {
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
					if (filterType == null || filterType.equals(gradeItem.getItemModule())) {
						activityYes = true;
					}
					Pattern pattern = Pattern.compile(patternCalifications.toLowerCase());
					Matcher match = pattern.matcher(gradeItem.getItemname().toLowerCase());
					boolean patternYes = false;
					if (patternCalifications.isEmpty() || match.find()) {
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
			LOGGER.error("Error al filtrar los elementos del calificador: {}", e);
		}
	}

	/**
	 * Exporta el gráfico. Se exportara como imagen en formato png.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 * @throws IOException
	 *             excepción
	 */
	public void saveChart(ActionEvent actionEvent) throws IOException {
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
		} catch (IOException e) {
			LOGGER.error("Error al guardar el gráfico: {}", e);
			errorWindow(I18n.get("error.savechart"), false);
		}
	}

	/**
	 * Exporta todos los datos actuales en formato CSV.
	 * 
	 * @param actionEvent
	 *            evento
	 * @since 2.4.0.0
	 */
	public void exportCSV(ActionEvent actionEvent) {
		LOGGER.info("Exportando ficheros CSV");
		try {
			CSVExport.run();
			// Si todo va con éxito, informamos.
			infoWindow(I18n.get("message.export_csv_success"), false);
		} catch (Exception e) {
			LOGGER.error("Error al exportar ficheros CSV.", e);
			errorWindow(I18n.get("error.savecsvfiles"), false);
		}
	}

	/**
	 * Exporta todos los gráficos a un html.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 */
	public void saveAll(ActionEvent actionEvent) {
		LOGGER.info("Exportando los gráficos");
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
				out.println("var BoxPlotGeneralDataSet = " + generateBoxPlotDataSet(null) + ";\r\n");
				out.println("var BoxPlotGroupDataSet = " + generateBoxPlotDataSet(slcGroup.getValue())
						+ ";\r\n");
				out.println("var TableDataSet = " + generateTableData() + ";\r\n");
				out.println("</script>\r\n</body>\r\n</html>");

				fr.close();
				out.close();
			} catch (IOException e) {
				LOGGER.error("Error al exportar los gráficos.", e);
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
	 */
	public void changeCourse(ActionEvent actionEvent) {
		LOGGER.info("Cambiando de asignatura...");
		changeScene(getClass().getResource("/view/Welcome.fxml"));
	}

	/**
	 * Vuelve a la ventana de login de usuario.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 */
	public void logOut(ActionEvent actionEvent) {
		LOGGER.info("Cerrando sesión de usuario");
		controller.cancelTimer();
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
			controller.getStage().setTitle(AppInfo.APPLICATION_NAME);
			controller.getStage().show();
		} catch (Exception e) {
			LOGGER.error("Error al modifcar la ventana de JavaFX: {}", e);
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
	public void aboutApp(ActionEvent actionEvent) {
		try {
			if (Desktop.getDesktop().isSupported(Action.BROWSE)) {
				LOGGER.info("El sistema operativo soporta la API de desktop.");
				Desktop.getDesktop().browse(new URL(AppInfo.GITHUB).toURI());
			}
		} catch (IOException | URISyntaxException e) {
			LOGGER.error("Error al abir la pagina aboutApp: {}", e);
		}
	}

	/**
	 * Botón "Salir". Cierra la aplicación.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 */
	public void closeApplication(ActionEvent actionEvent) {
		LOGGER.info("Cerrando aplicación");
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
			if ("NaN".equals(grade)) {
				tableData.append(",{v:0, f:'NaN'}");
			} else {
				tableData.append(",{v:" + grade + ", f:'" + grade + "/10'}");
			}
		}
		tableData.append("]");

		// Añadimos la media de los grupos
		for (Group grupo : slcGroup.getItems()) {
			if (grupo != null) {
				tableData.append(",['" + I18n.get("chartlabel.tableGroupMean") + " "
						+ escapeJavaScriptText(grupo.getGroupName()) + "'");
				for (TreeItem<GradeItem> structTree : tvwGradeReport.getSelectionModel().getSelectedItems()) {
					String grade = stats.getElementMean(stats.getGroupStats(grupo),
							structTree.getValue());
					if ("NaN".equals(grade)) {
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

		LOGGER.debug("Selected participant: {}", selectedParticipants.size());
		// Por cada usuario seleccionado
		for (EnrolledUser actualUser : selectedParticipants) {
			if (actualUser == null) {
				continue;
			}
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
						calculatedGrade = Math.round(actualLine.getEnrolledUserPercentage(actualUser) * 10) / 100.0;

						if (firstGrade) {
							dataSet.append(calculatedGrade);
							firstGrade = false;
						} else {
							dataSet.append("," + calculatedGrade);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Error en la construcción del dataset.", e);
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
	private String generateBoxPlotDataSet(Group group) {
		Map<GradeItem, DescriptiveStatistics> boxPlotStats;
		if (group == null) {
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
	private String generateMeanDataSet(Group group) {
		Map<GradeItem, DescriptiveStatistics> meanStats;
		Boolean firstElement = true;
		StringBuilder meanDataset = new StringBuilder();
		String color;

		if (group == null) {
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
	private void updateGroupData(Group group) {
		try {
			if (group == null) {
				webViewChartsEngine.executeScript("saveGroupMean('')");
			} else {
				webViewChartsEngine.executeScript("saveGroupMean(" + generateMeanDataSet(group) + ")");
				webViewChartsEngine.executeScript("updateChart('boxplotgroup'," + generateBoxPlotDataSet(group) + ")");
			}
		} catch (JSException e) {
			LOGGER.error("Error al generar los gráficos.", e);
			errorWindow(I18n.get("error.generateCharts"), false);
		}
	}

	/**
	 * Actualiza los gráficos.
	 */
	private void updateGradesChart() {
		if (!tabUbuGrades.isSelected()) {
			return;
		}

		try {
			String data = generateGradesDataSet();
			LOGGER.debug("Data: {}", data);
			updateGroupData(slcGroup.getValue());
			String tableData = generateTableData();
			LOGGER.debug("Table data for chart: {}", tableData);
			webViewChartsEngine.executeScript("saveTableData(" + tableData + ")");
			webViewChartsEngine.executeScript("saveMean(" + generateMeanDataSet(null) + ")");
			webViewChartsEngine.executeScript("updateChart('boxplot'," + generateBoxPlotDataSet(null) + ")");
			webViewChartsEngine.executeScript("updateChart('line'," + data + ")");
			webViewChartsEngine.executeScript("updateChart('radar'," + data + ")");
		} catch (JSException e) {
			LOGGER.error("Error al generar los gráficos.", e);
			errorWindow(I18n.get("error.generateCharts"), false); // FIX RMS Review true
																	// or false
		} catch (Exception e) {
			LOGGER.error("Error general al generar los gráficos.", e);
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

		alert.setTitle(AppInfo.APPLICATION_NAME);
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

	/**
	 * Muestra una ventana de información.
	 * 
	 * @param mensaje
	 *            El mensaje que se quiere mostrar.
	 * @param exit
	 *            Indica si se quiere mostar el boton de salir o no.
	 */
	private void infoWindow(String mensaje, boolean exit) {
		Alert alert = new Alert(AlertType.INFORMATION);

		alert.setTitle(AppInfo.APPLICATION_NAME);
		alert.setHeaderText("Information");
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