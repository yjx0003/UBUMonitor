package es.ubu.lsi.ubumonitor.controllers;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Component;
import es.ubu.lsi.ubumonitor.model.ComponentEvent;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.GradeItem;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.model.Section;
import es.ubu.lsi.ubumonitor.model.datasets.DataSet;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.model.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.model.log.FirstGroupBy;
import es.ubu.lsi.ubumonitor.model.log.GroupByAbstract;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.LogAction;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class SelectionController {

	public static final String TEXT_SELECTALL = "text.selectall";

	private static final String ALL = "text.all";

	private static final Logger LOGGER = LoggerFactory.getLogger(SelectionController.class);

	private static final Image NONE_ICON = new Image("/img/manual.png");

	@FXML
	private TreeView<GradeItem> tvwGradeReport;

	@FXML
	private TextField tfdItems;

	@FXML
	private ChoiceBox<ModuleType> slcType;

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
	private TabPane tabPane;
	@FXML
	private ListView<CourseModule> listViewActivity;

	@FXML
	private CheckComboBox<ModuleType> checkComboBoxModuleType;

	@FXML
	private CheckComboBox<Section> checkComboBoxSection;

	@FXML
	private CheckComboBox<Section> checkComboBoxSectionAc;

	private MainController mainController;

	public void init(MainController mainController, Course actualCourse) {
		this.mainController = mainController;

		// bind the content to visualization, multi or clustering tab
		tabPane.visibleProperty()
				.bind(mainController.getWebViewTabsController()
						.getVisualizationTab()
						.selectedProperty()
						.or(mainController.getWebViewTabsController()
								.getMultiTab()
								.selectedProperty())
						.or(mainController.getWebViewTabsController()
								.getClusteringTab()
								.selectedProperty()));

		tabPane.getSelectionModel()
				.select(ConfigHelper.getProperty("tabPane", tabPane.getSelectionModel()
						.getSelectedIndex()));
		initTabGrades();
		initTabLogs(actualCourse);
		initTabActivityCompletion(actualCourse);

		initiGradeItems(actualCourse);

		// remove Visualizarion Tab if has not Logs, Grades and Activity Completion
		// graph
		if (tabPane.getTabs()
				.isEmpty()) {
			mainController.getWebViewTabsController()
					.getTabPane()
					.getTabs()
					.removeAll(mainController.getWebViewTabsController()
							.getVisualizationTab(),
							mainController.getWebViewTabsController()
									.getClusteringTab());

		}

	}

	private void initiGradeItems(Course actualCourse) {

		if (actualCourse.getUpdatedGradeItem() == null) {
			tabPane.getTabs()
					.remove(tabUbuGrades);
			return;
		}
		ObservableList<ModuleType> observableListModules = FXCollections.observableArrayList();
		observableListModules.add(null); // opción por si no se filtra por grupo
		observableListModules.addAll(actualCourse.getUniqueGradeModuleTypes());

		slcType.setItems(observableListModules);
		slcType.getSelectionModel()
				.selectFirst(); // seleccionamos el nulo
		slcType.valueProperty()
				.addListener((ov, oldValue, newValue) -> filterCalifications(actualCourse));

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
		tfdItems.setOnAction((ActionEvent event) -> filterCalifications(actualCourse));
		// Establecemos la estructura en árbol del calificador
		GradeItem grcl = actualCourse.getRootGradeItem();
		// Establecemos la raiz del Treeview

		if (grcl != null) {
			TreeItem<GradeItem> root = new TreeItem<>(grcl);
			root.setExpanded(true);
			setIcon(root);
			// Llamamos recursivamente para llenar el Treeview
			int limitLevel = Controller.getInstance()
					.getMainConfiguration()
					.getValue(MainConfiguration.GENERAL, "limitLevelGradeItem");
			setTreeview(root, grcl, 1, Math.max(limitLevel, 0));

			// Establecemos la raiz en el TreeView
			tvwGradeReport.setRoot(root);
			tvwGradeReport.getSelectionModel()
					.setSelectionMode(SelectionMode.MULTIPLE);
			// Asignamos el manejador de eventos de la lista
			// Al clickar en la lista, se recalcula el nº de elementos seleccionados
			// Generamos el gráfico con los elementos selecionados
			tvwGradeReport.getSelectionModel()
					.getSelectedItems()
					.addListener((Change<? extends TreeItem<GradeItem>> g) -> mainController.getActions()
							.updateTreeViewGradeItem());
		}

	}

	private void onSetTabActivityCompletion(Event event) {
		if (!tabActivity.isSelected()) {
			return;
		}
		mainController.getActions()
				.onSetTabActivityCompletion();
	}

	private void initTabActivityCompletion(Course actualCourse) {
		if (actualCourse.getUpdatedActivityCompletion() == null) {
			tabPane.getTabs()
					.remove(tabActivity);
			return;
		}
		tabActivity.setOnSelectionChanged(this::onSetTabActivityCompletion);

		// cada vez que se seleccione nuevos elementos del list view actualizamos la
		// grafica y la escala
		listViewActivity.getSelectionModel()
				.getSelectedItems()
				.addListener((Change<? extends CourseModule> courseModule) -> mainController.getActions()
						.updateListViewActivity());

		listViewActivity.setCellFactory(getListCellCourseModule());

		Set<CourseModule> courseModules = actualCourse.getModules();
		Set<CourseModule> courseModuleWithActivityCompletion = new LinkedHashSet<>();
		for (CourseModule courseModule : courseModules) {
			if (!courseModule.getActivitiesCompletion()
					.isEmpty()) {
				courseModuleWithActivityCompletion.add(courseModule);
			}
		}

		ObservableList<CourseModule> observableListComponents = FXCollections
				.observableArrayList(courseModuleWithActivityCompletion);
		FilteredList<CourseModule> filterCourseModules = new FilteredList<>(observableListComponents);
		listViewActivity.setItems(filterCourseModules);
		listViewActivity.getSelectionModel()
				.setSelectionMode(SelectionMode.MULTIPLE);

		ObservableList<ModuleType> observableListModuleTypes = FXCollections.observableArrayList();

		observableListModuleTypes.addAll(actualCourse.getModules()
				.stream()
				.filter(c -> !c.getActivitiesCompletion()
						.isEmpty())
				.map(CourseModule::getModuleType)
				.distinct()
				.collect(Collectors.toList()));
		observableListModuleTypes.sort(Comparator.nullsFirst(Comparator.comparing(I18n::get, Collator.getInstance())));
		if (!observableListModuleTypes.isEmpty()) {
			observableListModuleTypes.add(0, ModuleType.DUMMY);
			checkComboBoxModuleType.getItems()
					.addAll(observableListModuleTypes);
			checkComboBoxModuleType.getCheckModel()
					.checkAll();
			checkComboBoxModuleType.getItemBooleanProperty(0)
					.addListener((observable, oldValue, newValue) -> {

						if (newValue.booleanValue()) {
							checkComboBoxModuleType.getCheckModel()
									.checkAll();
						} else {
							checkComboBoxModuleType.getCheckModel()
									.clearChecks();

						}

					});
			checkComboBoxModuleType.getCheckModel()
					.getCheckedItems()
					.addListener((Change<? extends ModuleType> c) -> {

						filterCourseModules.setPredicate(getActivityPredicade());
						listViewActivity.setCellFactory(getListCellCourseModule());

					});
		}

		checkComboBoxModuleType.setConverter(new StringConverter<ModuleType>() {
			@Override
			public ModuleType fromString(String moduleType) {
				return null;// no se va a usar en un choiceBox.
			}

			@Override
			public String toString(ModuleType moduleType) {
				if (moduleType == null || moduleType == ModuleType.DUMMY) {
					return I18n.get(TEXT_SELECTALL);
				}
				return I18n.get(moduleType);
			}
		});

		// ponemos un listener al cuadro de texto para que se filtre el list view en
		// tiempo real
		activityTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					filterCourseModules.setPredicate(getActivityPredicade());
					listViewActivity.setCellFactory(getListCellCourseModule());
				});

		checkBoxActivity.selectedProperty()
				.addListener(c -> {
					filterCourseModules.setPredicate(getActivityPredicade());
					listViewActivity.setCellFactory(getListCellCourseModule());
				});

		checkBoxActivity.selectedProperty()
				.addListener(c -> {
					filterCourseModules.setPredicate(getActivityPredicade());
					listViewActivity.setCellFactory(getListCellCourseModule());
				});

		if (!actualCourse.getSections()
				.isEmpty()) {

			SelectionCourseModuleController.DUMMY_SECTION.setName(I18n.get(TEXT_SELECTALL));
			checkComboBoxSectionAc.getItems()
					.add(SelectionCourseModuleController.DUMMY_SECTION);
			checkComboBoxSectionAc.getItems()
					.addAll(actualCourse.getSections());
			checkComboBoxSectionAc.getCheckModel()
					.checkAll();
			checkComboBoxSectionAc.getItemBooleanProperty(0)
					.addListener((observable, oldValue, newValue) -> {

						if (newValue.booleanValue()) {
							checkComboBoxSectionAc.getCheckModel()
									.checkAll();
						} else {
							checkComboBoxSectionAc.getCheckModel()
									.clearChecks();

						}

					});
			checkComboBoxSectionAc.getCheckModel()
					.getCheckedItems()
					.addListener((Change<? extends Section> c) -> {
						filterCourseModules.setPredicate(getActivityPredicade());
						listViewActivity.setCellFactory(getListCellCourseModule());
					});
		}
		filterCourseModules.setPredicate(getActivityPredicade());

	}

	private Predicate<? super CourseModule> getActivityPredicade() {
		return cm -> UtilMethods.containsTextField(activityTextField.getText(), cm.getModuleName())
				&& (checkBoxActivity.isSelected() || cm.isVisible()) && (checkComboBoxModuleType.getCheckModel()
						.getCheckedItems()
						.contains(cm.getModuleType()))
				&& (checkComboBoxSectionAc.getCheckModel()
						.getCheckedItems()
						.contains(cm.getSection()));
	}

	/**
	 * Inicializa la lista de componentes de la pestaña Registros
	 */
	public void initTabLogs(Course actualCourse) {
		if (actualCourse.getUpdatedLog() == null) {
			tabPane.getTabs()
					.remove(tabUbuLogs);
			return;
		}
		tabUbuLogs.setOnSelectionChanged(this::setTablogs);

		initTabLogs(tabUbuLogsComponent, tabUbuLogsEvent, tabUbuLogsSection, tabUbuLogsCourseModule);

		initListViewComponents(actualCourse);
		initListViewComponentsEvents(actualCourse);
		initListViewSections(actualCourse);
		initListViewCourseModules(actualCourse);

	}

	private void initTabLogs(Tab... tabs) {
		for (Tab tab : tabs) {
			tab.setOnSelectionChanged(event -> {
				if (tab.isSelected()) {
					mainController.getActions()
							.onSetSubTabLogs();

				}
			});
		}

	}

	/**
	 * Inicializa el listado de componentes de la pestaña Componentes en
	 */
	public void initListViewComponents(Course actualCourse) {
		// cada vez que se seleccione nuevos elementos del list view actualizamos la
		// grafica y la escala
		listViewComponents.getSelectionModel()
				.getSelectedItems()
				.addListener((Change<? extends Component> c) -> mainController.getActions()
						.updateListViewComponents()

				);

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
						Image image = new Image(AppInfo.IMG_DIR + component.name()
								.toLowerCase() + ".png");
						setGraphic(new ImageView(image));
					} catch (Exception e) {
						setGraphic(null);
					}
				}
			}
		});

		List<Component> uniqueComponents = actualCourse.getUniqueComponents();

		// Ordenamos los componentes segun los nombres internacionalizados
		uniqueComponents.sort(Comparator.comparing(I18n::get, Collator.getInstance()));

		ObservableList<Component> observableListComponents = FXCollections.observableArrayList(uniqueComponents);
		FilteredList<Component> filterComponents = new FilteredList<>(observableListComponents);
		listViewComponents.setItems(filterComponents);
		listViewComponents.getSelectionModel()
				.setSelectionMode(SelectionMode.MULTIPLE);

		// ponemos un listener al cuadro de texto para que se filtre el list view en
		// tiempo real
		componentTextField.textProperty()
				.addListener(((observable, oldValue, newValue) -> filterComponents.setPredicate(component -> {
					if (newValue == null || newValue.isEmpty()) {
						return true;
					}
					String textField = newValue.toLowerCase();
					return I18n.get(component)
							.toLowerCase()
							.contains(textField);
				})));

	}

	/**
	 * Inicializa los elementos de la pestaña eventos.
	 */
	public void initListViewComponentsEvents(Course actualCourse) {
		listViewEvents.getSelectionModel()
				.getSelectedItems()
				.addListener((Change<? extends ComponentEvent> c) -> mainController.getActions()
						.updateListViewEvents());

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
						Image image = new Image(AppInfo.IMG_DIR + componentEvent.getComponent()
								.name()
								.toLowerCase() + ".png");
						setGraphic(new ImageView(image));
					} catch (Exception e) {
						setGraphic(null);
					}
				}
			}
		});

		List<ComponentEvent> uniqueComponentsEvents = actualCourse.getUniqueComponentsEvents();

		// Ordenamos los componentes segun los nombres internacionalizados
		uniqueComponentsEvents
				.sort(Comparator.comparing((ComponentEvent c) -> I18n.get(c.getComponent()), Collator.getInstance())
						.thenComparing((ComponentEvent c) -> I18n.get(c.getEventName()), Collator.getInstance()));

		ObservableList<ComponentEvent> observableListComponents = FXCollections
				.observableArrayList(uniqueComponentsEvents);
		FilteredList<ComponentEvent> filterComponentsEvents = new FilteredList<>(observableListComponents);
		listViewEvents.setItems(filterComponentsEvents);
		listViewEvents.getSelectionModel()
				.setSelectionMode(SelectionMode.MULTIPLE);

		componentEventTextField.textProperty()
				.addListener(
						((observable, oldValue, newValue) -> filterComponentsEvents.setPredicate(componentEvent -> {
							if (newValue == null || newValue.isEmpty()) {
								return true;
							}
							String textField = newValue.toLowerCase();
							return I18n.get(componentEvent.getComponent())
									.toLowerCase()
									.contains(textField)
									|| I18n.get(componentEvent.getEventName())
											.toLowerCase()
											.contains(textField);
						})));

	}

	/**
	 * Inicializa el listado de componentes de la pestaña Componentes en
	 */
	public void initListViewSections(Course actualCourse) {
		// cada vez que se seleccione nuevos elementos del list view actualizamos la
		// grafica y la escala
		listViewSection.getSelectionModel()
				.getSelectedItems()
				.addListener((Change<? extends Section> section) -> mainController.getActions()
						.updateListViewSection());

		// Cambiamos el nombre de los elementos en funcion de la internacionalizacion y
		// ponemos un icono
		listViewSection.setCellFactory(getListCellSection());

		Set<Section> sections = actualCourse.getSections();

		ObservableList<Section> observableListComponents = FXCollections.observableArrayList(sections);
		FilteredList<Section> filterSections = new FilteredList<>(observableListComponents, getSectionPredicate());
		listViewSection.setItems(filterSections);
		listViewSection.getSelectionModel()
				.setSelectionMode(SelectionMode.MULTIPLE);

		// ponemos un listener al cuadro de texto para que se filtre el list view en
		// tiempo real
		sectionTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					filterSections.setPredicate(getSectionPredicate());
					listViewSection.setCellFactory(getListCellSection());
				});

		checkBoxSection.selectedProperty()
				.addListener(section -> {
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

				setText(section.getName());

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
		return s -> UtilMethods.containsTextField(sectionTextField.getText(), s.getName())
				&& (checkBoxSection.isSelected() || s.isVisible());
	}

	/**
	 * Inicializa el listado de componentes de la pestaña Componentes en
	 */
	public void initListViewCourseModules(Course actualCourse) {

		// cada vez que se seleccione nuevos elementos del list view actualizamos la
		// grafica y la escala
		listViewCourseModule.getSelectionModel()
				.getSelectedItems()
				.addListener((Change<? extends CourseModule> courseModule) -> mainController.getActions()
						.updateListViewCourseModule());

		listViewCourseModule.setCellFactory(getListCellCourseModule());

		Set<CourseModule> courseModules = actualCourse.getModules();

		ObservableList<CourseModule> observableListComponents = FXCollections.observableArrayList(courseModules);

		FilteredList<CourseModule> filterCourseModules = new FilteredList<>(observableListComponents);
		listViewCourseModule.setItems(filterCourseModules);
		listViewCourseModule.getSelectionModel()
				.setSelectionMode(SelectionMode.MULTIPLE);

		ObservableList<ModuleType> observableListModuleTypes = FXCollections
				.observableArrayList(actualCourse.getUniqueCourseModulesTypes());
		observableListModuleTypes.sort(Comparator.nullsFirst(Comparator.comparing(I18n::get, Collator.getInstance())));
		if (!observableListModuleTypes.isEmpty()) {
			observableListModuleTypes.add(0, ModuleType.DUMMY);
			checkComboBoxCourseModule.getItems()
					.addAll(observableListModuleTypes);
			checkComboBoxCourseModule.getCheckModel()
					.checkAll();
			checkComboBoxCourseModule.getItemBooleanProperty(0)
					.addListener((observable, oldValue, newValue) -> {

						if (newValue.booleanValue()) {
							checkComboBoxCourseModule.getCheckModel()
									.checkAll();
						} else {
							checkComboBoxCourseModule.getCheckModel()
									.clearChecks();

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
						return I18n.get(TEXT_SELECTALL);
					}
					return I18n.get(moduleType);
				}
			});

			checkComboBoxCourseModule.getCheckModel()
					.getCheckedItems()
					.addListener((Change<? extends ModuleType> c) -> {
						filterCourseModules.setPredicate(getCourseModulePredicate());
						listViewCourseModule.setCellFactory(getListCellCourseModule());

					});

			if (!actualCourse.getSections()
					.isEmpty()) {

				SelectionCourseModuleController.DUMMY_SECTION.setName(I18n.get(TEXT_SELECTALL));
				checkComboBoxSection.getItems()
						.add(SelectionCourseModuleController.DUMMY_SECTION);
				checkComboBoxSection.getItems()
						.addAll(actualCourse.getSections());

				checkComboBoxSection.getCheckModel()
						.checkAll();
				checkComboBoxSection.getItemBooleanProperty(0)
						.addListener((observable, oldValue, newValue) -> {

							if (newValue.booleanValue()) {
								checkComboBoxSection.getCheckModel()
										.checkAll();
							} else {
								checkComboBoxSection.getCheckModel()
										.clearChecks();

							}

						});
				checkComboBoxSection.getCheckModel()
						.getCheckedItems()
						.addListener((Change<? extends Section> c) -> {
							filterCourseModules.setPredicate(getCourseModulePredicate());
							listViewCourseModule.setCellFactory(getListCellCourseModule());
						});
			}

		}

		// ponemos un listener al cuadro de texto para que se filtre el list view en
		// tiempo real
		courseModuleTextField.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					filterCourseModules.setPredicate(getCourseModulePredicate());
					listViewCourseModule.setCellFactory(getListCellCourseModule());
				});

		checkBoxCourseModule.selectedProperty()
				.addListener(c -> {
					filterCourseModules.setPredicate(getCourseModulePredicate());
					listViewCourseModule.setCellFactory(getListCellCourseModule());
				});

		checkBoxActivityCompleted.selectedProperty()
				.addListener(c -> {
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
						Image image = new Image(AppInfo.IMG_DIR + courseModule.getModuleType()
								.getModName() + ".png");
						setGraphic(new ImageView(image));
					} catch (Exception e) {
						setGraphic(null);
					}
				}
			}
		};
	}

	private Predicate<CourseModule> getCourseModulePredicate() {

		return cm -> UtilMethods.containsTextField(courseModuleTextField.getText(), cm.getModuleName())
				&& (checkBoxCourseModule.isSelected() || cm.isVisible()) && (checkComboBoxCourseModule.getCheckModel()
						.getCheckedItems()
						.contains(cm.getModuleType()))
				&& (!checkBoxActivityCompleted.isSelected() || !cm.getActivitiesCompletion()
						.isEmpty())
				&& (checkComboBoxSection.getCheckModel()
						.getCheckedItems()
						.contains(cm.getSection()));
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
		mainController.getActions()
				.onSetTabLogs();
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
		mainController.getActions()
				.onSetTabGrades();
	}

	/**
	 * Rellena el árbol de actividades (GradeItems). Obtiene los hijos de la línea
	 * pasada por parámetro, los transforma en treeitems y los establece como hijos
	 * del elemento treeItem equivalente de line
	 * 
	 * @param parent El padre al que añadir los elementos.
	 * @param line   La linea con los elementos a añadir.
	 */
	public void setTreeview(TreeItem<GradeItem> parent, GradeItem line, int level, int limitLevel) {
		for (int j = 0; j < line.getChildren()
				.size(); j++) {
			TreeItem<GradeItem> item = new TreeItem<>(line.getChildren()
					.get(j));
			setIcon(item);
			parent.getChildren()
					.add(item);

			item.setExpanded(level <= limitLevel);

			setTreeview(item, line.getChildren()
					.get(j), level + 1, limitLevel);
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
			path = AppInfo.IMG_DIR + item.getValue()
					.getItemModule()
					.getModName() + ".png";
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
	public void filterCalifications(Course actualCourse) {
		try {
			GradeItem root = actualCourse.getRootGradeItem();
			Set<GradeItem> gradeItems = actualCourse.getGradeItems();
			// Establecemos la raiz del Treeview
			TreeItem<GradeItem> treeItemRoot = new TreeItem<>(root);
			setIcon(treeItemRoot);
			// Llamamos recursivamente para llenar el Treeview
			ModuleType filterType = slcType.getValue();
			String patternCalifications = tfdItems.getText();

			if (filterType == null && patternCalifications.isEmpty()) {
				// Sin filtro y sin patrón
				for (int k = 0; k < root.getChildren()
						.size(); k++) {
					TreeItem<GradeItem> item = new TreeItem<>(root.getChildren()
							.get(k));
					setIcon(item);
					treeItemRoot.getChildren()
							.add(item);
					treeItemRoot.setExpanded(true);
					setTreeview(item, root.getChildren()
							.get(k), 0, 2);
				}
			} else { // Con filtro
				for (GradeItem gradeItem : gradeItems) {
					TreeItem<GradeItem> item = new TreeItem<>(gradeItem);
					boolean activityYes = false;
					if (filterType == null || filterType.equals(gradeItem.getItemModule())) {
						activityYes = true;
					}
					Pattern pattern = Pattern.compile(patternCalifications.toLowerCase());
					Matcher match = pattern.matcher(gradeItem.getItemname()
							.toLowerCase());
					boolean patternYes = false;
					if (patternCalifications.isEmpty() || match.find()) {
						patternYes = true;
					}
					if (activityYes && patternYes) {
						setIcon(item);
						treeItemRoot.getChildren()
								.add(item);
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

	public <T> T typeLogsAction(LogAction<T> logAction) {
		if (tabUbuLogsComponent.isSelected()) {

			return logAction.action(listViewComponents.getSelectionModel()
					.getSelectedItems(), DataSetComponent.getInstance(), GroupByAbstract::getComponents);
		} else if (tabUbuLogsEvent.isSelected()) {

			return logAction.action(listViewEvents.getSelectionModel()
					.getSelectedItems(), DataSetComponentEvent.getInstance(), GroupByAbstract::getComponentsEvents);
		} else if (tabUbuLogsSection.isSelected()) {

			return logAction.action(listViewSection.getSelectionModel()
					.getSelectedItems(), DataSetSection.getInstance(), GroupByAbstract::getSections);
		} else if (tabUbuLogsCourseModule.isSelected()) {

			return logAction.action(listViewCourseModule.getSelectionModel()
					.getSelectedItems(), DatasSetCourseModule.getInstance(), GroupByAbstract::getCourseModules);
		}
		throw new IllegalStateException("Need other tab");
	}

	public List<String> getSelectedLogTypeTransLated() {
		return typeLogsAction(new LogAction<List<String>>() {

			@Override
			public <E extends Serializable, T extends Serializable> List<String> action(List<E> logType,
					DataSet<E> dataSet, Function<GroupByAbstract<?>, FirstGroupBy<E, T>> function) {
				List<String> list = new ArrayList<>();
				for (E e : logType) {
					list.add(dataSet.translate(e));
				}
				return list;
			}
		});

	}

	public List<GradeItem> getSelectedGradeItems() {
		return UtilMethods.getSelectedGradeItems(tvwGradeReport);
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

	public TabPane getTabPaneUbuLogs() {
		return tabPaneUbuLogs;
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

	public CheckComboBox<ModuleType> getCheckComboBoxCourseModule() {
		return checkComboBoxCourseModule;
	}

	public CheckBox getCheckBoxSection() {
		return checkBoxSection;
	}

	public CheckBox getCheckBoxCourseModule() {
		return checkBoxCourseModule;
	}

	public CheckBox getCheckBoxActivityCompleted() {
		return checkBoxActivityCompleted;
	}

	public Tab getTabActivity() {
		return tabActivity;
	}

	public TextField getActivityTextField() {
		return activityTextField;
	}

	public CheckBox getCheckBoxActivity() {
		return checkBoxActivity;
	}

	public ListView<CourseModule> getListViewActivity() {
		return listViewActivity;
	}

	public CheckComboBox<ModuleType> getCheckComboBoxModuleType() {
		return checkComboBoxModuleType;
	}

	public MainController getMainController() {
		return mainController;
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	public void selectAllComponents() {
		listViewComponents.getSelectionModel()
				.selectAll();
	}

	public void selectAllComponentEvents() {
		listViewEvents.getSelectionModel()
				.selectAll();
	}

	public void selectAllSections() {
		listViewSection.getSelectionModel()
				.selectAll();
	}

	public void selectAllCourseModules() {
		listViewCourseModule.getSelectionModel()
				.selectAll();
	}

	public void selectAllGradeItems() {
		tvwGradeReport.getSelectionModel()
				.selectAll();
	}

	public void selectAllActivityCompletion() {
		listViewActivity.getSelectionModel()
				.selectAll();
	}

}
