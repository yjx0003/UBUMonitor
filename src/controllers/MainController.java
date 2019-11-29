package controllers;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.webkit.WebConsoleListener;

import controllers.ubulogs.GroupByAbstract;
import export.CSVExport;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Worker;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
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
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.Component;
import model.ComponentEvent;
import model.CourseModule;
import model.EnrolledUser;
import model.GradeItem;
import model.Group;
import model.LastActivity;
import model.LastActivityFactory;
import model.ModuleType;
import model.Role;
import model.Section;
import model.Stats;
import netscape.javascript.JSObject;

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

	@FXML SplitPane splitPaneLeft;
	
	@FXML Label lblActualCourse;
	@FXML Label lblActualUser;
	@FXML Label lblActualHost;

	@FXML Label lblLastUpdate;
	@FXML ImageView userPhoto;

	@FXML Label lblCountParticipants;
	@FXML ListView<EnrolledUser> listParticipants;
	FilteredList<EnrolledUser> filteredEnrolledList;

	@FXML ChoiceBox<Role> slcRole;

	@FXML ChoiceBox<Group> slcGroup;

	@FXML ChoiceBox<LastActivity> slcActivity;

	@FXML TextField tfdParticipants;

	@FXML TreeView<GradeItem> tvwGradeReport;

	@FXML TextField tfdItems;

	@FXML ChoiceBox<ModuleType> slcType;

	@FXML WebView webViewCharts;
	WebEngine webViewChartsEngine;

	@FXML SplitPane splitPane;

	@FXML TabPane tabPane;

	@FXML Tab tabUbuGrades;

	@FXML Tab tabUbuLogs;

	@FXML Tab tabUbuLogsComponent;

	@FXML Tab tabUbuLogsEvent;

	@FXML Tab tabUbuLogsSection;

	@FXML
	Tab tabUbuLogsCourseModule;

	@FXML
	TextField componentTextField;

	@FXML
	TextField componentEventTextField;

	@FXML
	TextField sectionTextField;
	@FXML
	TextField courseModuleTextField;

	@FXML
	ListView<Component> listViewComponents;

	@FXML
	ListView<ComponentEvent> listViewEvents;

	@FXML
	ListView<Section> listViewSection;
	@FXML
	ListView<CourseModule> listViewCourseModule;

	@FXML
	ChoiceBox<ModuleType> choiceBoxCourseModule;

	@FXML
	CheckBox checkBoxSection;

	@FXML
	CheckBox checkBoxCourseModule;

	@FXML
	GridPane optionsUbuLogs;

	@FXML
	TextField textFieldMax;

	@FXML
	ChoiceBox<GroupByAbstract<?>> choiceBoxDate;

	@FXML
	DatePicker datePickerStart;

	@FXML
	DatePicker datePickerEnd;
	
	
	@FXML ProgressBar progressBar;
	
	
	private Stats stats;
	
	private JavaConnector javaConnector;


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
			initTabPaneWebView();
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
			ZonedDateTime lastLogDateTime = controller.getActualCourse().getLogs().getLastDatetime();
			lblLastUpdate.setText(
					I18n.get("label.lastupdate") + " " +
							lastLogDateTime.format(Controller.DATE_TIME_FORMATTER));
		} catch (Exception e) {
			LOGGER.error("Error en la inicialización.", e);
		}
	}

	private void initTabPaneWebView() {
		// Cargamos el html de los graficos y calificaciones
		webViewCharts.setContextMenuEnabled(false); // Desactiva el click derecho
		webViewChartsEngine = webViewCharts.getEngine();
		javaConnector = new JavaConnector(this);
		
	
		progressBar.progressProperty().bind(webViewChartsEngine.getLoadWorker().progressProperty());
		splitPaneLeft.disableProperty().bind(webViewChartsEngine.getLoadWorker().runningProperty());;
		
		
		WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> {
		    LOGGER.error("{} [{} at {}] ", message,sourceId,lineNumber);
		});
		// Comprobamos cuando se carga la pagina para traducirla
		webViewChartsEngine.getLoadWorker().stateProperty()
				.addListener((ov, oldState, newState) -> {
					if (Worker.State.SUCCEEDED != newState)
						return;
					JSObject window = (JSObject) webViewChartsEngine.executeScript("window");
					window.setMember("javaConnector", javaConnector);
					webViewChartsEngine.executeScript("setLanguage('" + I18n.getResourceBundle().getLocale() + "')");
					webViewCharts.toFront();
					webViewChartsEngine.executeScript("manageButtons('"+"log"+"')");
					javaConnector.updateLogsChart();
				});
		webViewChartsEngine.load(getClass().getResource("/graphics/Charts.html").toExternalForm());


	}

	private void initiGradeItems() {

		ObservableList<ModuleType> observableListModules = FXCollections.observableArrayList();
		observableListModules.add(null); // opción por si no se filtra por grupo
		observableListModules.addAll(controller.getActualCourse().getUniqueGradeModuleTypes());

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
				.addListener((Change<? extends TreeItem<GradeItem>> g) -> javaConnector.updateGradesChart());
	}

	private void initEnrolledUsers() {
		// Mostramos nº participantes
		lblCountParticipants.setText(I18n.get("label.participants")
				+ controller.getActualCourse().getEnrolledUsersCount());
		tfdParticipants.setOnAction(event -> filterParticipants());
		initEnrolledUsersListView();

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

		slcRole.valueProperty().addListener((ov, oldValue, newValue) -> filterParticipants());

		Role studentRole = controller.getActualCourse().getStudentRole();
		if (studentRole == null) {
			slcRole.getSelectionModel().selectFirst(); // seleccionamos el nulo
		} else {
			slcRole.getSelectionModel().select(studentRole);
		}

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

		ObservableList<LastActivity> observableListActivity = FXCollections.observableArrayList();
		observableListActivity.add(null);
		observableListActivity.addAll(LastActivityFactory.getAllLastActivity());
		slcActivity.setItems(observableListActivity);
		slcActivity.getSelectionModel().selectFirst(); // seleccionamos el nulo
		slcActivity.valueProperty().addListener((ov, oldValue, newValue) -> filterParticipants());

		slcActivity.setConverter(new StringConverter<LastActivity>() {
			@Override
			public LastActivity fromString(String role) {
				return null;// no se va a usar en un choiceBox.
			}

			@Override
			public String toString(LastActivity lastActivity) {
				if (lastActivity == null) {
					return I18n.get(ALL);
				}
				return MessageFormat.format(I18n.get("text.betweendates"), lastActivity.getPreviusDays(),
						lastActivity.getLimitDaysConnection() == Integer.MAX_VALUE ? "∞"
								: lastActivity.getLimitDaysConnection() - 1);
			}
		});
	}

	/**
	 * Inicializa la lista de usuarios.
	 */
	private void initEnrolledUsersListView() {

		Set<EnrolledUser> users = controller.getActualCourse().getEnrolledUsers();

		ObservableList<EnrolledUser> observableUsers = FXCollections.observableArrayList(users);
		observableUsers.sort(EnrolledUser.NAME_COMPARATOR);
		filteredEnrolledList = new FilteredList<>(observableUsers);

		// Activamos la selección múltiple en la lista de participantes
		listParticipants.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		listParticipants.getSelectionModel().getSelectedItems()
				.addListener(
						(Change<? extends EnrolledUser> usersSelected) -> {
							javaConnector.updateGradesChart();
							javaConnector.updateLogsChart();
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
					Instant lastCourseAccess = user.getLastcourseaccess();
					Instant lastAccess = user.getLastaccess();
					Instant lastLogInstant = controller.getActualCourse().getLogs().getLastDatetime().toInstant();
					setText(user + "\n"
							+ I18n.get("label.course") + formatDates(lastCourseAccess, lastLogInstant) +
							" | " + I18n.get("text.moodle") + formatDates(lastAccess, lastLogInstant));

					setTextFill(LastActivityFactory.getColorActivity(lastCourseAccess, lastLogInstant));

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
			return (time < 0 ? 0 : time) + " " + I18n.get("text.days");
		}
		if ((time = betweenDates(ChronoUnit.HOURS, lastCourseAccess, lastLogInstant)) != 0L) {
			return (time < 0 ? 0 : time) + " " + I18n.get("text.hours");
		}
		if ((time = betweenDates(ChronoUnit.MINUTES, lastCourseAccess, lastLogInstant)) != 0L) {
			return (time < 0 ? 0 : time) + " " + I18n.get("text.minutes");
		}
		time = betweenDates(ChronoUnit.SECONDS, lastCourseAccess, lastLogInstant);
		long timeSeconds = time < 0 ? 0 : time;
		return timeSeconds + " " + I18n.get("text.seconds");
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
		choiceBoxDate.getSelectionModel().select(controller.getActualCourse().getLogStats().getByType());

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

		datePickerStart.setValue(controller.getActualCourse().getStart());
		datePickerEnd.setValue(controller.getActualCourse().getEnd());

		datePickerStart.setOnAction(event -> applyFilterLogs());
		datePickerEnd.setOnAction(event -> applyFilterLogs());

		datePickerStart.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.isAfter(datePickerEnd.getValue()));
			}
		});

		datePickerEnd.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.isBefore(datePickerStart.getValue()) || date.isAfter(LocalDate.now()));
			}
		});
		
		optionsUbuLogs.visibleProperty().bind(tabUbuLogs.selectedProperty());
		optionsUbuLogs.managedProperty().bind(tabUbuLogs.selectedProperty());
		
	}

	/**
	 * Actualiza la escala maxima del eje y de los graficos de logs.
	 * 
	 * @param value
	 *            valor de escala maxima
	 */
	private void updateMaxScale(long value) {
		if (webViewChartsEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED)
			javaConnector.updateMaxY(value);

	}

	/**
	 * Inicializa la lista de componentes de la pestaña Registros
	 */
	public void initTabLogs() {
		tabPane.getSelectionModel().select(tabUbuLogs);
		
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
					javaConnector.updateLogsChart();
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
					javaConnector.updateLogsChart();
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
						Image image = new Image(AppInfo.IMG_DIR + component.name().toLowerCase() + ".png");
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
					javaConnector.updateLogsChart();
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
						Image image = new Image(
								AppInfo.IMG_DIR + componentEvent.getComponent().name().toLowerCase() + ".png");
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
					javaConnector.updateLogsChart();
					findMax();
				});

		// Cambiamos el nombre de los elementos en funcion de la internacionalizacion y
		// ponemos un icono
		listViewSection.setCellFactory(getListCellSection());

		Set<Section> sections = controller.getActualCourse().getSections();

		ObservableList<Section> observableListComponents = FXCollections.observableArrayList(sections);
		FilteredList<Section> filterSections = new FilteredList<>(observableListComponents, getSectionPredicate());
		listViewSection.setItems(filterSections);
		listViewSection.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// ponemos un listener al cuadro de texto para que se filtre el list view en
		// tiempo real
		sectionTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					filterSections.setPredicate(getSectionPredicate());
					listViewSection.setCellFactory(getListCellSection());
				});

		checkBoxSection.selectedProperty().addListener(
				section -> {
					filterSections.setPredicate(getSectionPredicate());
					listViewSection.setCellFactory(getListCellSection());
				});
	}

	private Callback<ListView<Section>, ListCell<Section>> getListCellSection() {
		return callback -> new ListCell<Section>() {
			@Override
			public void updateItem(Section section, boolean empty) {
				super.updateItem(section, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
					return;
				}
				String sectionName = section.getName();
				setText(sectionName == null || sectionName.trim().isEmpty() ? I18n.get("text.sectionplaceholder")
						: sectionName);

				try {
					if (!section.isVisible()) {
						setTextFill(Color.GRAY);
					} else {
						setTextFill(Color.BLACK);
					}

					String visible = section.isVisible() ? "visible" : "not_visible";

					Image image = new Image(AppInfo.IMG_DIR + visible + ".png");
					setGraphic(new ImageView(image));
				} catch (Exception e) {
					setGraphic(null);
				}
			}

		};
	}

	private Predicate<Section> getSectionPredicate() {
		return s -> containsTextField(sectionTextField.getText(), s.getName())
				&& (checkBoxSection.isSelected() || s.isVisible());
	}

	/**
	 * Inicializa el listado de componentes de la pestaña Componentes en
	 */
	public void initListViewCourseModules() {

		// cada vez que se seleccione nuevos elementos del list view actualizamos la
		// grafica y la escala
		listViewCourseModule.getSelectionModel().getSelectedItems()
				.addListener((Change<? extends CourseModule> courseModule) -> {
					javaConnector.updateLogsChart();
					findMax();
				});

		listViewCourseModule.setCellFactory(getListCellCourseModule());

		Set<CourseModule> courseModules = controller.getActualCourse().getModules();

		ObservableList<CourseModule> observableListComponents = FXCollections.observableArrayList(courseModules);
		FilteredList<CourseModule> filterCourseModules = new FilteredList<>(observableListComponents,
				getCourseModulePredicate());
		listViewCourseModule.setItems(filterCourseModules);
		listViewCourseModule.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		ObservableList<ModuleType> observableListModuleTypes = FXCollections.observableArrayList();
		observableListModuleTypes.add(null); // opción por si no se filtra por grupo
		observableListModuleTypes.addAll(controller.getActualCourse().getUniqueCourseModulesTypes());
		observableListModuleTypes.sort(Comparator.nullsFirst(Comparator.comparing(I18n::get)));

		choiceBoxCourseModule.setItems(observableListModuleTypes);
		choiceBoxCourseModule.getSelectionModel().selectFirst(); // seleccionamos el nulo

		choiceBoxCourseModule.setConverter(new StringConverter<ModuleType>() {
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

		// ponemos un listener al cuadro de texto para que se filtre el list view en
		// tiempo real
		courseModuleTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					filterCourseModules.setPredicate(getCourseModulePredicate());
					listViewCourseModule.setCellFactory(getListCellCourseModule());
				});

		checkBoxCourseModule.selectedProperty().addListener(c -> {
			filterCourseModules.setPredicate(getCourseModulePredicate());
			listViewCourseModule.setCellFactory(getListCellCourseModule());
		});

		choiceBoxCourseModule.valueProperty()
				.addListener(c -> {
					filterCourseModules.setPredicate(getCourseModulePredicate());
					listViewCourseModule.setCellFactory(getListCellCourseModule());
				});

	}

	private Callback<ListView<CourseModule>, ListCell<CourseModule>> getListCellCourseModule() {
		return callback -> new ListCell<CourseModule>() {
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
					} else {
						setTextFill(Color.BLACK);
					}
					try {
						Image image = new Image(AppInfo.IMG_DIR + courseModule.getModuleType().getModName() + ".png");
						setGraphic(new ImageView(image));
					} catch (Exception e) {
						setGraphic(null);
					}
				}
			}
		};
	}

	private Predicate<CourseModule> getCourseModulePredicate() {
		return cm -> containsTextField(courseModuleTextField.getText(),
				cm.getModuleName())
				&& (checkBoxCourseModule.isSelected() || cm.isVisible())
				&& (choiceBoxCourseModule.getValue() == null || choiceBoxCourseModule.getValue() == cm.getModuleType());
	}

	private boolean containsTextField(String newValue, String element) {
		if (newValue == null || newValue.isEmpty()) {
			return true;
		}
		String textField = newValue.toLowerCase();
		return element.toLowerCase().contains(textField);
	}



	/**
	 * Metodo que se activa cuando se modifica la pestaña de logs o calificaciones
	 * 
	 * @param event
	 *            evento
	 */
	public void setTablogs(Event event) {
		if (!tabUbuLogs.isSelected()) {
			return;
		}

		javaConnector.updateLogsChart();
		findMax();
		webViewChartsEngine.executeScript("manageButtons('log')");
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
			maxYAxis = choiceBoxDate.getValue().getComponents().getMaxElement(listParticipants.getItems(),
					listViewComponents.getSelectionModel().getSelectedItems(),
					datePickerStart.getValue(), datePickerEnd.getValue());
		} else if (tabUbuLogsEvent.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getComponentsEvents().getMaxElement(
					listParticipants.getItems(),
					listViewEvents.getSelectionModel().getSelectedItems(), datePickerStart.getValue(), datePickerEnd.getValue());
		} else if (tabUbuLogsSection.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getSections().getMaxElement(
					listParticipants.getItems(),
					listViewSection.getSelectionModel().getSelectedItems(), datePickerStart.getValue(), datePickerEnd.getValue());
		} else if (tabUbuLogsCourseModule.isSelected()) {
			maxYAxis = choiceBoxDate.getValue().getCourseModules().getMaxElement(
					listParticipants.getItems(),
					listViewCourseModule.getSelectionModel().getSelectedItems(), datePickerStart.getValue(), datePickerEnd.getValue());
		}
		textFieldMax.setText(Long.toString(maxYAxis));
	}

	/**
	 * Inicializa los elementos de las graficas de calificacion.
	 */
	public void initTabGrades() {
		
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

		javaConnector.updateGradesChart();
		webViewChartsEngine.executeScript("manageButtons('grade')");

	}

	/**
	 * Aplica los filtros de fecha a las graficas de log.
	 * 
	 * @param event
	 *            evento
	 */
	public void applyFilterLogs() {

		javaConnector.updateLogsChart();
		findMax();
	}

	/**
	 * Filtra los participantes según el rol, el grupo y el patrón indicados
	 */
	public void filterParticipants() {
		
		Role rol = slcRole.getValue();
		Group group = slcGroup.getValue();
		LastActivity lastActivity = slcActivity.getValue();
		String textField = tfdParticipants.getText().toLowerCase();
		Instant lastLogInstant = controller.getActualCourse().getLogs().getLastDatetime().toInstant();
		filteredEnrolledList.setPredicate(
				e -> (rol == null || rol.contains(e))
						&& (group == null || group.contains(e))
						&& (textField.isEmpty() || e.getFullName().toLowerCase().contains(textField))
						&& (lastActivity == null || LastActivityFactory.getColorActivity(e.getLastcourseaccess(),
								lastLogInstant) == lastActivity.getColor()));
		// Mostramos nº participantes
		lblCountParticipants.setText(I18n.get("label.participants") + filteredEnrolledList.size());
		javaConnector.updateGradesChart();
		javaConnector.updateLogsChart();
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
	 * Exporta todos los gráficos a un html.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 */
	public void saveAll(ActionEvent actionEvent) {
		
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
			infoWindow(I18n.get("message.export_csv_success"), false);
		} catch (Exception e) {
			LOGGER.error("Error al exportar ficheros CSV.", e);
			errorWindow(I18n.get("error.savecsvfiles"), false);
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
			controller.getStage().resizableProperty().setValue(Boolean.FALSE);
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
		listViewComponents.getSelectionModel().clearSelection();
		listViewEvents.getSelectionModel().clearSelection();
		listViewSection.getSelectionModel().clearSelection();
		listViewCourseModule.getSelectionModel().clearSelection();
	}

	/**
	 * Abre en el navegador el repositorio del proyecto.
	 * 
	 * @param actionEvent
	 *            El ActionEvent.
	 */
	public void aboutApp(ActionEvent actionEvent) {
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
				LOGGER.info("El sistema operativo soporta la API de desktop.");
				Desktop.getDesktop().browse(new URI(AppInfo.GITHUB));
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