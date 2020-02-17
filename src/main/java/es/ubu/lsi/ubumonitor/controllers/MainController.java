package es.ubu.lsi.ubumonitor.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.configuration.Config;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigurationController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.export.CSVBuilderAbstract;
import es.ubu.lsi.ubumonitor.export.CSVExport;
import es.ubu.lsi.ubumonitor.model.Component;
import es.ubu.lsi.ubumonitor.model.ComponentEvent;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.Group;
import es.ubu.lsi.ubumonitor.model.LastActivity;
import es.ubu.lsi.ubumonitor.model.LastActivityFactory;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.model.Role;
import es.ubu.lsi.ubumonitor.model.Section;
import es.ubu.lsi.ubumonitor.model.Stats;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

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

	@FXML
	private SplitPane splitPaneLeft;

	@FXML
	private StatusBar statusBar;

	@FXML
	private Label lblCountParticipants;
	@FXML
	private ListView<EnrolledUser> listParticipants;
	FilteredList<EnrolledUser> filteredEnrolledList;

	@FXML
	private CheckComboBox<Role> checkComboBoxRole;

	@FXML
	private CheckComboBox<Group> checkComboBoxGroup;

	@FXML
	private CheckComboBox<LastActivity> checkComboBoxActivity;

	@FXML
	private TextField tfdParticipants;

	@FXML
	private TreeView<GradeItem> tvwGradeReport;

	@FXML
	private TextField tfdItems;

	@FXML
	private ChoiceBox<ModuleType> slcType;

	@FXML
	private SplitPane splitPane;

	@FXML
	private TabPane tabPane;

	@FXML
	private TabPane tabPaneUbuLogs;

	@FXML
	private Tab tabUbuGrades;

	@FXML
	private Tab tabUbuLogs;

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
	private CheckComboBox<ModuleType> checkComboBoxCourseModule;

	@FXML
	private CheckBox checkBoxSection;

	@FXML
	private CheckBox checkBoxCourseModule;
	@FXML
	private CheckBox checkBoxActivityCompleted;

	@FXML
	private Tab tabActivity;

	@FXML
	private TextField activityTextField;

	@FXML
	private CheckBox checkBoxActivity;

	@FXML
	private ListView<CourseModule> listViewActivity;

	@FXML
	private CheckComboBox<ModuleType> checkComboBoxModuleType;

	@FXML
	private MenuItem updateCourse;

	private Stats stats;

	@FXML
	private TabPane webViewTabPane;

	@FXML
	private Tab visualizationTab;

	@FXML
	private VisualizationController visualizationController;
	private Map<Tab, MainAction> tabMap = new HashMap<>();

	@FXML
	private ImageView userPhoto;

	@FXML
	private Menu menuTheme;

	private AutoCompletionBinding<EnrolledUser> autoCompletionBinding;

	/**
	 * Muestra los usuarios matriculados en el curso, así como las actividades de
	 * las que se compone.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			LOGGER.info("Completada la carga del curso {}", controller.getActualCourse().getFullName());

			controller.getStage().setOnHiding(event -> onClose());

			updateCourse.setDisable(controller.isOfflineMode());
			stats = controller.getStats();

			controller.setMainConfiguration(new MainConfiguration());
			ConfigurationController.loadConfiguration(controller.getMainConfiguration(),
					controller.getConfiguration(controller.getActualCourse()));
			initWebViewTabs();
			tabPane.getSelectionModel()
					.select(Config.getProperty("tabPane", tabPane.getSelectionModel().getSelectedIndex()));
			initTabGrades();
			initTabLogs();
			initTabActivityCompletion();

			initEnrolledUsers();

			initiGradeItems();

			initStatusBar();
			initMenuBar();
			initUserPhoto();
		} catch (Exception e) {
			LOGGER.error("Error en la inicialización.", e);
		}
	}

	private void initMenuBar() {
		ToggleGroup group = new ToggleGroup();
		for (Entry<String, String> entry : Style.STYLES.entrySet()) {
			String key = entry.getKey();
			String path = entry.getValue();
			RadioMenuItem menuItem = new RadioMenuItem();
			menuItem.setText(key);
			menuItem.setToggleGroup(group);

			if (key.equals(Config.getProperty("style", "Modena"))) {
				menuItem.setSelected(true);
			}
			menuItem.setOnAction(event -> {

				controller.getStage().getScene().getStylesheets().clear();
				if (path != null) {
					controller.getStage().getScene().getStylesheets().add(path);
				}
				Config.setProperty("style", key);
			});
			menuTheme.getItems().add(menuItem);
		}
	}

	private void initUserPhoto() throws IOException {
		InputStream in = new ByteArrayInputStream(controller.getDataBase().getMoodleUser().getUserPhoto());
		Image image = new Image(in);
		in.close();
		userPhoto.setImage(image);

		ContextMenu menu = new ContextMenu();
		MenuItem user = new MenuItem(controller.getDataBase().getMoodleUser().getFullName(), new ImageView(image));
		MenuItem logout = new MenuItem(I18n.get("menu.logout"));
		MenuItem exit = new MenuItem(I18n.get("menu.exit"));

		logout.setOnAction(this::logOut);
		exit.setOnAction(this::closeApplication);
		menu.getItems().addAll(user, logout, exit);
		menu.setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
		menu.setAutoHide(true);

		userPhoto.setOnMouseClicked(e -> menu.show(userPhoto, e.getScreenX(), e.getScreenY()));

	}

	private void initStatusBar() {

		// Mostramos Host actual
		Hyperlink actualHost = new Hyperlink(controller.getUrlHost().toString());
		ImageView linkImage = new ImageView("/img/link.png");
		linkImage.setFitHeight(20);
		linkImage.setFitWidth(20);
		actualHost.setGraphic(linkImage);
		actualHost.setOnAction(event -> UtilMethods.openURL(actualHost.getText()));
		// Mostramos Curso actual
		Label lblActualCourse = new Label(controller.getActualCourse().getFullName());

		ImageView online = new ImageView(
				controller.isOfflineMode() ? "/img/circle_offline.png" : "/img/circle_online.png");
		online.setFitHeight(20);
		online.setFitWidth(20);
		Tooltip.install(online, new Tooltip(I18n.get("text.online_" + !controller.isOfflineMode())));
		HBox left = new HBox();
		left.setAlignment(Pos.CENTER);
		left.setSpacing(5);
		left.getChildren().addAll(online, new Separator(Orientation.VERTICAL), lblActualCourse,
				new Separator(Orientation.VERTICAL), actualHost);

		statusBar.getLeftItems().add(left);

		HBox right = new HBox();
		right.setAlignment(Pos.CENTER);
		right.setSpacing(5);
		ZonedDateTime lastLogDateTime = controller.getActualCourse().getLogs().getLastDatetime();
		Label lblLastUpdate = new Label(
				I18n.get("label.lastupdate") + " " + lastLogDateTime.format(Controller.DATE_TIME_FORMATTER));
		right.getChildren().addAll(lblLastUpdate);
		statusBar.getRightItems().add(right);

	}

	private void initWebViewTabs() {

		webViewTabPane.getSelectionModel()
				.select(Config.getProperty("webViewTab", webViewTabPane.getSelectionModel().getSelectedIndex()));
		webViewTabPane.getSelectionModel().selectedItemProperty().addListener((ob, old, newValue) -> {
			if (tabUbuLogs.isSelected()) {
				getActions().onSetTabLogs();
			} else if (tabUbuGrades.isSelected()) {
				getActions().onSetTabGrades();
			} else if (tabActivity.isSelected()) {
				getActions().onSetTabActivityCompletion();
			}
		});
		visualizationController.init(this);

		tabMap.put(visualizationTab, visualizationController);

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
				.addListener((Change<? extends TreeItem<GradeItem>> g) -> updateTreeViewGradeItem());
	}

	private void initEnrolledUsers() {
		// Mostramos nº participantes

		tfdParticipants.setOnAction(event -> filterParticipants());

		initEnrolledUsersListView();
		autoCompletionBinding = TextFields.bindAutoCompletion(tfdParticipants, filteredEnrolledList);
		checkComboBoxGroup.getItems().addAll(controller.getActualCourse().getGroups());
		ObservableList<Group> groups = controller.getMainConfiguration().getValue(MainConfiguration.GENERAL,
				"initialGroups");
		if (groups != null) {
			groups.forEach(checkComboBoxGroup.getCheckModel()::check);
		}

		checkComboBoxGroup.getCheckModel().getCheckedItems()
				.addListener((Change<? extends Group> g) -> filterParticipants());

		checkComboBoxRole.getItems().addAll(controller.getActualCourse().getRoles());
		ObservableList<Role> roles = controller.getMainConfiguration().getValue(MainConfiguration.GENERAL,
				"initialRoles");
		if (roles != null) {
			roles.forEach(checkComboBoxRole.getCheckModel()::check);
		}
		checkComboBoxRole.getCheckModel().getCheckedItems()
				.addListener((Change<? extends Role> r) -> filterParticipants());

		checkComboBoxActivity.getItems().addAll(LastActivityFactory.getAllLastActivity());
		ObservableList<LastActivity> lastActivities = controller.getMainConfiguration()
				.getValue(MainConfiguration.GENERAL, "initialLastActivity");
		if (lastActivities != null) {
			lastActivities.forEach(checkComboBoxActivity.getCheckModel()::check);
		}

		checkComboBoxActivity.getCheckModel().getCheckedItems()
				.addListener((Change<? extends LastActivity> l) -> filterParticipants());

		checkComboBoxActivity.setConverter(getActivityConverter());
		lblCountParticipants.textProperty().bind(Bindings.size(filteredEnrolledList).asString());
		filterParticipants();
	}

	public StringConverter<LastActivity> getActivityConverter() {
		return new StringConverter<LastActivity>() {
			@Override
			public LastActivity fromString(String role) {
				return null;// no se va a usar en un choiceBox.
			}

			@Override
			public String toString(LastActivity lastActivity) {
				return MessageFormat.format(I18n.get("text.betweendates"), lastActivity.getPreviusDays(),
						lastActivity.getLimitDaysConnection() == Integer.MAX_VALUE ? "∞"
								: lastActivity.getLimitDaysConnection() - 1);
			}
		};
	}

	/**
	 * Inicializa la lista de usuarios.
	 */
	private void initEnrolledUsersListView() {

		Set<EnrolledUser> users = controller.getActualCourse().getEnrolledUsers();

		ObservableList<EnrolledUser> observableUsers = FXCollections.observableArrayList(users);
		observableUsers.sort(EnrolledUser.NAME_COMPARATOR);
		filteredEnrolledList = new FilteredList<>(observableUsers);
		filteredEnrolledList.predicateProperty().addListener(p -> updatePredicadeEnrolledList());
		// Activamos la selección múltiple en la lista de participantes
		listParticipants.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		listParticipants.getSelectionModel().getSelectedItems()
				.addListener((Change<? extends EnrolledUser> usersSelected) -> updateListViewEnrolledUser());

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
					setText(user + "\n" + I18n.get("label.course") + formatDates(lastCourseAccess, lastLogInstant)
							+ " | " + I18n.get("text.moodle") + formatDates(lastAccess, lastLogInstant));

					setTextFill(LastActivityFactory.getColorActivity(lastCourseAccess, lastLogInstant));

					try (InputStream in = new ByteArrayInputStream(user.getImageBytes())) {
						Image image = new Image(in, 50, 50, false, false);
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
	 * @param lastCourseAccess fecha inicio
	 * @param lastLogInstant fecha fin
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
	 * @param type dias, horas, minutos
	 * @param lastCourseAccess instante inicio
	 * @param lastLogInstant instante fin
	 * @return numero de diferencia absoluta
	 */
	private long betweenDates(ChronoUnit type, Instant lastCourseAccess, Instant lastLogInstant) {
		return type.between(lastCourseAccess, lastLogInstant);
	}

	private void initTabActivityCompletion() {
		tabActivity.setOnSelectionChanged(this::onSetTabActivityCompletion);

		// cada vez que se seleccione nuevos elementos del list view actualizamos la
		// grafica y la escala
		listViewActivity.getSelectionModel().getSelectedItems()
				.addListener((Change<? extends CourseModule> courseModule) -> {
					updateListViewActivity();
				});

		listViewActivity.setCellFactory(getListCellCourseModule());

		Set<CourseModule> courseModules = controller.getActualCourse().getModules();
		Set<CourseModule> courseModuleWithActivityCompletion = new LinkedHashSet<>();
		for (CourseModule courseModule : courseModules) {
			if (!courseModule.getActivitiesCompletion().isEmpty()) {
				courseModuleWithActivityCompletion.add(courseModule);
			}
		}

		ObservableList<CourseModule> observableListComponents = FXCollections
				.observableArrayList(courseModuleWithActivityCompletion);
		FilteredList<CourseModule> filterCourseModules = new FilteredList<>(observableListComponents);
		listViewActivity.setItems(filterCourseModules);
		listViewActivity.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		ObservableList<ModuleType> observableListModuleTypes = FXCollections.observableArrayList();

		observableListModuleTypes.addAll(controller.getActualCourse().getUniqueCourseModulesTypes());
		observableListModuleTypes.sort(Comparator.nullsFirst(Comparator.comparing(I18n::get)));
		if (!observableListModuleTypes.isEmpty()) {
			observableListModuleTypes.add(0, ModuleType.DUMMY);
		}

		checkComboBoxModuleType.getItems().addAll(observableListModuleTypes);
		checkComboBoxModuleType.getCheckModel().checkAll();

		checkComboBoxModuleType.setConverter(new StringConverter<ModuleType>() {
			@Override
			public ModuleType fromString(String moduleType) {
				return null;// no se va a usar en un choiceBox.
			}

			@Override
			public String toString(ModuleType moduleType) {
				if (moduleType == null || moduleType == ModuleType.DUMMY) {
					return I18n.get("text.selectall");
				}
				return I18n.get(moduleType);
			}
		});

		// ponemos un listener al cuadro de texto para que se filtre el list view en
		// tiempo real
		activityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			filterCourseModules.setPredicate(getActivityPredicade());
			listViewActivity.setCellFactory(getListCellCourseModule());
		});

		checkBoxActivity.selectedProperty().addListener(c -> {
			filterCourseModules.setPredicate(getActivityPredicade());
			listViewActivity.setCellFactory(getListCellCourseModule());
		});
		checkComboBoxModuleType.getItemBooleanProperty(0).addListener((observable, oldValue, newValue) -> {

			if (newValue.booleanValue()) {
				checkComboBoxModuleType.getCheckModel().checkAll();
			} else {
				checkComboBoxModuleType.getCheckModel().clearChecks();

			}

		});
		checkComboBoxModuleType.getCheckModel().getCheckedItems().addListener((Change<? extends ModuleType> c) -> {

			filterCourseModules.setPredicate(getActivityPredicade());
			listViewActivity.setCellFactory(getListCellCourseModule());

		});

		checkBoxActivity.selectedProperty().addListener(c -> {
			filterCourseModules.setPredicate(getActivityPredicade());
			listViewActivity.setCellFactory(getListCellCourseModule());
		});
		filterCourseModules.setPredicate(getActivityPredicade());
	}

	private Predicate<? super CourseModule> getActivityPredicade() {
		return cm -> containsTextField(activityTextField.getText(), cm.getModuleName())
				&& (checkBoxActivity.isSelected() || cm.isVisible())
				&& (checkComboBoxModuleType.getCheckModel().getCheckedItems().contains(cm.getModuleType()));
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
					onSetSubTabLogs();

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
		listViewComponents.getSelectionModel().getSelectedItems().addListener((Change<? extends Component> c) -> {
			updateListViewComponents();

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
				.addListener((Change<? extends ComponentEvent> c) -> updateListViewEvents());

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
					setText(I18n.get(componentEvent.getComponent()) + " - " + I18n.get(componentEvent.getEventName()));
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
		uniqueComponentsEvents.sort(Comparator.comparing((ComponentEvent c) -> I18n.get(c.getComponent()))
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
				.addListener((Change<? extends Section> section) -> updateListViewSection());

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
		sectionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			filterSections.setPredicate(getSectionPredicate());
			listViewSection.setCellFactory(getListCellSection());
		});

		checkBoxSection.selectedProperty().addListener(section -> {
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
				.addListener((Change<? extends CourseModule> courseModule) -> updateListViewCourseModule());

		listViewCourseModule.setCellFactory(getListCellCourseModule());

		Set<CourseModule> courseModules = controller.getActualCourse().getModules();

		ObservableList<CourseModule> observableListComponents = FXCollections.observableArrayList(courseModules);

		FilteredList<CourseModule> filterCourseModules = new FilteredList<>(observableListComponents);
		listViewCourseModule.setItems(filterCourseModules);
		listViewCourseModule.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		ObservableList<ModuleType> observableListModuleTypes = FXCollections
				.observableArrayList(controller.getActualCourse().getUniqueCourseModulesTypes());
		observableListModuleTypes.sort(Comparator.nullsFirst(Comparator.comparing(I18n::get)));
		if (!observableListModuleTypes.isEmpty()) {
			observableListModuleTypes.add(0, ModuleType.DUMMY);
		}

		checkComboBoxCourseModule.getItems().addAll(observableListModuleTypes);
		checkComboBoxCourseModule.getCheckModel().checkAll();
		checkComboBoxCourseModule.getItemBooleanProperty(0).addListener((observable, oldValue, newValue) -> {

			if (newValue.booleanValue()) {
				checkComboBoxCourseModule.getCheckModel().checkAll();
			} else {
				checkComboBoxCourseModule.getCheckModel().clearChecks();

			}

		});
		checkComboBoxCourseModule.setConverter(new StringConverter<ModuleType>() {
			@Override
			public ModuleType fromString(String moduleType) {
				return null;// no se va a usar en un choiceBox.
			}

			@Override
			public String toString(ModuleType moduleType) {
				if (moduleType == null || moduleType == ModuleType.DUMMY) {
					return I18n.get("text.selectall");
				}
				return I18n.get(moduleType);
			}
		});

		// ponemos un listener al cuadro de texto para que se filtre el list view en
		// tiempo real
		courseModuleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			filterCourseModules.setPredicate(getCourseModulePredicate());
			listViewCourseModule.setCellFactory(getListCellCourseModule());
		});

		checkBoxCourseModule.selectedProperty().addListener(c -> {
			filterCourseModules.setPredicate(getCourseModulePredicate());
			listViewCourseModule.setCellFactory(getListCellCourseModule());
		});

		checkComboBoxCourseModule.getCheckModel().getCheckedItems().addListener((Change<? extends ModuleType> c) -> {
			c.next();
			if (c.wasAdded() && c.getAddedSubList().get(0) == null) {
				for (int i = 1; i < checkComboBoxCourseModule.getItems().size(); i++) {
					checkComboBoxCourseModule.getCheckModel().check(i);
				}

			} else if (c.wasRemoved() && c.getRemoved().get(0) == null) {
				for (int i = 1; i < checkComboBoxCourseModule.getItems().size(); i++) {
					checkComboBoxCourseModule.getCheckModel().clearCheck(i);
				}
			} else {
				filterCourseModules.setPredicate(getCourseModulePredicate());
				listViewCourseModule.setCellFactory(getListCellCourseModule());
			}

		});

		checkBoxActivityCompleted.selectedProperty().addListener(c -> {
			filterCourseModules.setPredicate(getCourseModulePredicate());
			listViewCourseModule.setCellFactory(getListCellCourseModule());
		});
		filterCourseModules.setPredicate(getCourseModulePredicate());
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

		return cm -> containsTextField(courseModuleTextField.getText(), cm.getModuleName())
				&& (checkBoxCourseModule.isSelected() || cm.isVisible())
				&& (checkComboBoxCourseModule.getCheckModel().getCheckedItems().contains(cm.getModuleType()))
				&& (!checkBoxActivityCompleted.isSelected() || !cm.getActivitiesCompletion().isEmpty());
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
	 * @param event evento
	 */
	public void setTablogs(Event event) {
		if (!tabUbuLogs.isSelected()) {
			return;
		}
		onSetTabLogs();
	}

	public CheckBox getCheckBoxActivityCompleted() {
		return checkBoxActivityCompleted;
	}

	public void setCheckBoxActivityCompleted(CheckBox checkBoxActivityCompleted) {
		this.checkBoxActivityCompleted = checkBoxActivityCompleted;
	}

	public Tab getTabActivity() {
		return tabActivity;
	}

	public void setTabActivity(Tab tabActivity) {
		this.tabActivity = tabActivity;
	}

	public TextField getActivityTextField() {
		return activityTextField;
	}

	public void setActivityTextField(TextField activityTextField) {
		this.activityTextField = activityTextField;
	}

	public CheckBox getCheckBoxActivity() {
		return checkBoxActivity;
	}

	public void setCheckBoxActivity(CheckBox checkBoxActivity) {
		this.checkBoxActivity = checkBoxActivity;
	}

	public ListView<CourseModule> getListViewActivity() {
		return listViewActivity;
	}

	public void setListViewActivity(ListView<CourseModule> listViewActivity) {
		this.listViewActivity = listViewActivity;
	}

	public MenuItem getUpdateCourse() {
		return updateCourse;
	}

	public void setUpdateCourse(MenuItem updateCourse) {
		this.updateCourse = updateCourse;
	}

	public static String getAll() {
		return ALL;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public static Image getNoneIcon() {
		return NONE_ICON;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public void setSplitPaneLeft(SplitPane splitPaneLeft) {
		this.splitPaneLeft = splitPaneLeft;
	}

	public void setLblCountParticipants(Label lblCountParticipants) {
		this.lblCountParticipants = lblCountParticipants;
	}

	public void setListParticipants(ListView<EnrolledUser> listParticipants) {
		this.listParticipants = listParticipants;
	}

	public void setFilteredEnrolledList(FilteredList<EnrolledUser> filteredEnrolledList) {
		this.filteredEnrolledList = filteredEnrolledList;
	}

	public void setTfdParticipants(TextField tfdParticipants) {
		this.tfdParticipants = tfdParticipants;
	}

	public void setTvwGradeReport(TreeView<GradeItem> tvwGradeReport) {
		this.tvwGradeReport = tvwGradeReport;
	}

	public void setTfdItems(TextField tfdItems) {
		this.tfdItems = tfdItems;
	}

	public void setSlcType(ChoiceBox<ModuleType> slcType) {
		this.slcType = slcType;
	}

	public void setSplitPane(SplitPane splitPane) {
		this.splitPane = splitPane;
	}

	public void setTabPane(TabPane tabPane) {
		this.tabPane = tabPane;
	}

	public void setTabUbuGrades(Tab tabUbuGrades) {
		this.tabUbuGrades = tabUbuGrades;
	}

	public void setTabUbuLogs(Tab tabUbuLogs) {
		this.tabUbuLogs = tabUbuLogs;
	}

	public void setTabUbuLogsComponent(Tab tabUbuLogsComponent) {
		this.tabUbuLogsComponent = tabUbuLogsComponent;
	}

	public void setTabUbuLogsEvent(Tab tabUbuLogsEvent) {
		this.tabUbuLogsEvent = tabUbuLogsEvent;
	}

	public void setTabUbuLogsSection(Tab tabUbuLogsSection) {
		this.tabUbuLogsSection = tabUbuLogsSection;
	}

	public void setTabUbuLogsCourseModule(Tab tabUbuLogsCourseModule) {
		this.tabUbuLogsCourseModule = tabUbuLogsCourseModule;
	}

	public void setComponentTextField(TextField componentTextField) {
		this.componentTextField = componentTextField;
	}

	public void setComponentEventTextField(TextField componentEventTextField) {
		this.componentEventTextField = componentEventTextField;
	}

	public void setSectionTextField(TextField sectionTextField) {
		this.sectionTextField = sectionTextField;
	}

	public void setCourseModuleTextField(TextField courseModuleTextField) {
		this.courseModuleTextField = courseModuleTextField;
	}

	public void setListViewComponents(ListView<Component> listViewComponents) {
		this.listViewComponents = listViewComponents;
	}

	public void setListViewEvents(ListView<ComponentEvent> listViewEvents) {
		this.listViewEvents = listViewEvents;
	}

	public void setListViewSection(ListView<Section> listViewSection) {
		this.listViewSection = listViewSection;
	}

	public void setListViewCourseModule(ListView<CourseModule> listViewCourseModule) {
		this.listViewCourseModule = listViewCourseModule;
	}

	public void setCheckBoxSection(CheckBox checkBoxSection) {
		this.checkBoxSection = checkBoxSection;
	}

	public void setCheckBoxCourseModule(CheckBox checkBoxCourseModule) {
		this.checkBoxCourseModule = checkBoxCourseModule;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
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
	 * @param event evento
	 */
	public void setTabGrades(Event event) {
		if (!tabUbuGrades.isSelected()) {
			return;
		}
		onSetTabGrades();
	}

	/**
	 * Filtra los participantes según el rol, el grupo y el patrón indicados
	 */
	public void filterParticipants() {
		List<Role> rol = checkComboBoxRole.getCheckModel().getCheckedItems();
		List<Group> group = checkComboBoxGroup.getCheckModel().getCheckedItems();
		List<LastActivity> lastActivity = checkComboBoxActivity.getCheckModel().getCheckedItems();
		String textField = tfdParticipants.getText().toLowerCase();
		Instant lastLogInstant = controller.getActualCourse().getLogs().getLastDatetime().toInstant();
		filteredEnrolledList.setPredicate(e -> (checkUserHasRole(rol, e)) && (checkUserHasGroup(group, e))
				&& (textField.isEmpty() || e.getFullName().toLowerCase().contains(textField))
				&& (lastActivity.contains(LastActivityFactory.getActivity(e.getLastcourseaccess(), lastLogInstant))));
		autoCompletionBinding.dispose();
		autoCompletionBinding = TextFields.bindAutoCompletion(tfdParticipants, filteredEnrolledList);
	}

	private boolean checkUserHasGroup(List<Group> groups, EnrolledUser user) {
		if (groups.isEmpty())
			return true;
		for (Group group : groups) {
			if (group.contains(user)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkUserHasRole(List<Role> roles, EnrolledUser user) {
		for (Role rol : roles) {
			if (rol.contains(user)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Rellena el árbol de actividades (GradeItems). Obtiene los hijos de la línea
	 * pasada por parámetro, los transforma en treeitems y los establece como hijos
	 * del elemento treeItem equivalente de line
	 * 
	 * @param parent El padre al que añadir los elementos.
	 * @param line La linea con los elementos a añadir.
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
	 * @param item El item al que añadir el icono.
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
	 * @param actionEvent El ActionEvent.
	 * @throws IOException excepción
	 */
	public void saveChart(ActionEvent actionEvent) throws IOException {

		getActions().save();
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
			File file = new File(Config.getProperty("csvFolderPath", "./"));
			if (file.exists() && file.isDirectory()) {
				dir.setInitialDirectory(file);
			}

			File selectedDir = dir.showDialog(controller.getStage());
			if (selectedDir != null) {
				CSVBuilderAbstract.setPath(selectedDir.toPath());
				CSVExport.run();
				UtilMethods.infoWindow(I18n.get("message.export_csv_success") + selectedDir.getAbsolutePath());
				Config.setProperty("csvFolderPath", selectedDir.getAbsolutePath());
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
	 * Vuelve a la ventana de login de usuario.
	 * 
	 * @param actionEvent El ActionEvent.
	 */
	public void logOut(ActionEvent actionEvent) {
		LOGGER.info("Cerrando sesión de usuario");

		changeScene(getClass().getResource("/view/Login.fxml"));
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

	private void changeScene(URL sceneFXML) {
		changeScene(sceneFXML, null);
	}

	/**
	 * Deja de seleccionar los participantes/actividades y borra el gráfico.
	 * 
	 * @param actionEvent El ActionEvent.
	 */
	public void clearSelection(ActionEvent actionEvent) {
		listParticipants.getSelectionModel().clearSelection();
		tvwGradeReport.getSelectionModel().clearSelection();
		listViewComponents.getSelectionModel().clearSelection();
		listViewEvents.getSelectionModel().clearSelection();
		listViewSection.getSelectionModel().clearSelection();
		listViewCourseModule.getSelectionModel().clearSelection();
	}

	public void changeConfiguration() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Configuration.fxml"),
				I18n.getResourceBundle());

		Scene newScene;
		try {
			newScene = new Scene(loader.load());
		} catch (IOException ex) {
			LOGGER.error("Error", ex);
			return;
		}
		Style.addStyle(Config.getProperty("style"), newScene.getStylesheets());
		ConfigurationController configurationController = loader.getController();
		Stage stage = new Stage();
		configurationController.setMainController(this);
		stage.setOnHiding(event -> configurationController.onClose());
		stage.setScene(newScene);
		stage.initModality(Modality.NONE);
		stage.getIcons().add(new Image("/img/logo_min.png"));
		stage.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);

		stage.show();

	}

	public void importConfiguration() {
		FileChooser fileChooser = UtilMethods.createFileChooser(I18n.get("menu.importconfig"), null,
				Config.getProperty("configurationFolderPath", "./"),
				new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"));

		File file = fileChooser.showOpenDialog(controller.getStage());
		if (file != null) {
			Config.setProperty("configurationFolderPath", file.getParent());
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
				Config.getProperty("configurationFolderPath", "./"),
				new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"));

		File file = fileChooser.showSaveDialog(controller.getStage());
		if (file != null) {
			ConfigurationController.saveConfiguration(controller.getMainConfiguration(), file.toPath());
			Config.setProperty("configurationFolderPath", file.getParent());
		}
	}

	/**
	 * Abre en el navegador el repositorio del proyecto.
	 * 
	 * @param actionEvent El ActionEvent.
	 */
	public void aboutApp(ActionEvent actionEvent) {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AboutApp.fxml"), I18n.getResourceBundle());

		Scene newScene;
		try {
			newScene = new Scene(loader.load());
		} catch (IOException ex) {
			LOGGER.error("Error", ex);
			return;
		}
		Style.addStyle(Config.getProperty("style"), newScene.getStylesheets());

		Stage stage = new Stage();
		stage.setScene(newScene);
		stage.setResizable(false);
		stage.initOwner(controller.getStage());
		stage.initModality(Modality.WINDOW_MODAL);

		stage.getIcons().add(new Image("/img/logo_min.png"));
		stage.setTitle(AppInfo.APPLICATION_NAME_WITH_VERSION);

		stage.show();

	}

	public void moreInfo() {
		UtilMethods.openURL(AppInfo.GITHUB);
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

	private void onSetTabActivityCompletion(Event event) {
		if (!tabActivity.isSelected()) {
			return;
		}
		onSetTabActivityCompletion();

	}

	public Controller getController() {
		return controller;
	}

	public SplitPane getSplitPaneLeft() {
		return splitPaneLeft;
	}

	public Label getLblCountParticipants() {
		return lblCountParticipants;
	}

	public ListView<EnrolledUser> getListParticipants() {
		return listParticipants;
	}

	public FilteredList<EnrolledUser> getFilteredEnrolledList() {
		return filteredEnrolledList;
	}

	public TextField getTfdParticipants() {
		return tfdParticipants;
	}

	public TreeView<GradeItem> getTvwGradeReport() {
		return tvwGradeReport;
	}

	public TextField getTfdItems() {
		return tfdItems;
	}

	public ChoiceBox<ModuleType> getSlcType() {
		return slcType;
	}

	public SplitPane getSplitPane() {
		return splitPane;
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	public Tab getTabUbuGrades() {
		return tabUbuGrades;
	}

	public Tab getTabUbuLogs() {
		return tabUbuLogs;
	}

	public Tab getTabUbuLogsComponent() {
		return tabUbuLogsComponent;
	}

	public Tab getTabUbuLogsEvent() {
		return tabUbuLogsEvent;
	}

	public Tab getTabUbuLogsSection() {
		return tabUbuLogsSection;
	}

	public Tab getTabUbuLogsCourseModule() {
		return tabUbuLogsCourseModule;
	}

	public TextField getComponentTextField() {
		return componentTextField;
	}

	public TextField getComponentEventTextField() {
		return componentEventTextField;
	}

	public TextField getSectionTextField() {
		return sectionTextField;
	}

	public TextField getCourseModuleTextField() {
		return courseModuleTextField;
	}

	public ListView<Component> getListViewComponents() {
		return listViewComponents;
	}

	public ListView<ComponentEvent> getListViewEvents() {
		return listViewEvents;
	}

	public ListView<Section> getListViewSection() {
		return listViewSection;
	}

	public ListView<CourseModule> getListViewCourseModule() {
		return listViewCourseModule;
	}

	public CheckBox getCheckBoxSection() {
		return checkBoxSection;
	}

	public CheckBox getCheckBoxCourseModule() {
		return checkBoxCourseModule;
	}

	public Stats getStats() {
		return stats;
	}

	public TabPane getTabPaneUbuLogs() {
		return tabPaneUbuLogs;
	}

	public void setTabPaneUbuLogs(TabPane tabPaneUbuLogs) {
		this.tabPaneUbuLogs = tabPaneUbuLogs;
	}

	public CheckComboBox<Role> getCheckComboBoxRole() {
		return checkComboBoxRole;
	}

	public void setCheckComboBoxRole(CheckComboBox<Role> checkComboBoxRole) {
		this.checkComboBoxRole = checkComboBoxRole;
	}

	public CheckComboBox<Group> getCheckComboBoxGroup() {
		return checkComboBoxGroup;
	}

	public void setCheckComboBoxGroup(CheckComboBox<Group> checkComboBoxGroup) {
		this.checkComboBoxGroup = checkComboBoxGroup;
	}

	public CheckComboBox<LastActivity> getCheckComboBoxActivity() {
		return checkComboBoxActivity;
	}

	public void setCheckComboBoxActivity(CheckComboBox<LastActivity> checkComboBoxActivity) {
		this.checkComboBoxActivity = checkComboBoxActivity;
	}

	public VisualizationController getVisualizationTabPageController() {
		return visualizationController;
	}

	public void setVisualizationTabPageController(VisualizationController visualizationTabPageController) {
		this.visualizationController = visualizationTabPageController;
	}

	public CheckComboBox<ModuleType> getCheckComboBoxCourseModule() {
		return checkComboBoxCourseModule;
	}

	public void setCheckComboBoxCourseModule(CheckComboBox<ModuleType> checkComboBoxCourseModule) {
		this.checkComboBoxCourseModule = checkComboBoxCourseModule;
	}

	public CheckComboBox<ModuleType> getCheckComboBoxModuleType() {
		return checkComboBoxModuleType;
	}

	public void setCheckComboBoxModuleType(CheckComboBox<ModuleType> checkComboBoxModuleType) {
		this.checkComboBoxModuleType = checkComboBoxModuleType;
	}

	public TabPane getWebViewTabPane() {
		return webViewTabPane;
	}

	public void setWebViewTabPane(TabPane webViewTabPane) {
		this.webViewTabPane = webViewTabPane;
	}

	public Tab getVisualizationTab() {
		return visualizationTab;
	}

	public void setVisualizationTab(Tab visualizationTab) {
		this.visualizationTab = visualizationTab;
	}

	public Map<Tab, MainAction> getTabMap() {
		return tabMap;
	}

	public void setTabMap(Map<Tab, MainAction> tabMap) {
		this.tabMap = tabMap;
	}

	private MainAction getActions() {
		return tabMap.getOrDefault(webViewTabPane.getSelectionModel().getSelectedItem(), NullMainAction.getInstance());
	}

	private void updateTreeViewGradeItem() {
		getActions().updateTreeViewGradeItem();
	}

	private void updateListViewEnrolledUser() {
		getActions().updateListViewEnrolledUser();
	}

	private void updatePredicadeEnrolledList() {
		getActions().updatePredicadeEnrolledList();
	}

	private void updateListViewActivity() {
		getActions().updateListViewActivity();

	}

	private void onSetTabLogs() {
		getActions().onSetTabLogs();

	}

	private void onSetTabGrades() {
		getActions().onSetTabGrades();
	}

	private void onSetTabActivityCompletion() {
		getActions().onSetTabActivityCompletion();
	}

	private void onSetSubTabLogs() {
		getActions().onSetSubTabLogs();
	}

	private void updateListViewComponents() {
		getActions().updateListViewComponents();
	}

	private void updateListViewEvents() {
		getActions().updateListViewEvents();
	}

	private void updateListViewSection() {
		getActions().updateListViewSection();
	}

	private void updateListViewCourseModule() {
		getActions().updateListViewCourseModule();
	}

	public void applyConfiguration() {
		getActions().applyConfiguration();

	}

	private void onClose() {
		Config.setProperty("webViewTab", webViewTabPane.getSelectionModel().getSelectedIndex());
		Config.setProperty("tabPane", tabPane.getSelectionModel().getSelectedIndex());
	}

}