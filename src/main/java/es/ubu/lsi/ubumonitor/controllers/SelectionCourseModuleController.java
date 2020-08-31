package es.ubu.lsi.ubumonitor.controllers;


import java.text.Collator;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;

import org.controlsfx.control.CheckComboBox;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.CourseModule;
import es.ubu.lsi.ubumonitor.model.ModuleType;
import es.ubu.lsi.ubumonitor.model.Section;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class SelectionCourseModuleController {
	public static final Section DUMMY_SECTION = new Section(-1);
	public static final String TEXT_SELECTALL = "text.selectall";

	@FXML
	private ListView<CourseModule> listViewCourseModule;

	@FXML
	private CheckComboBox<ModuleType> checkComboBoxCourseModule;

	@FXML
	private CheckComboBox<Section> checkComboBoxSection;

	@FXML
	private TabPane tabPane;
	
	@FXML
	private CheckBox checkBoxActivityCompleted;
	
	@FXML
	private CheckBox checkBoxCourseModule;
	
	@FXML
	private TextField courseModuleTextField;

	public void init(MainController mainController, Course actualCourse) {
		tabPane.visibleProperty()
				.bind(mainController.getWebViewTabsController()
						.getCalendarEventTab()
						.selectedProperty());
		initListViewCourseModules(mainController, actualCourse);
	}

	/**
	 * Inicializa el listado de componentes de la pestaña Componentes en
	 */
	public void initListViewCourseModules(MainController mainController, Course actualCourse) {

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

				DUMMY_SECTION.setName(I18n.get(TEXT_SELECTALL));
				checkComboBoxSection.getItems()
						.add(DUMMY_SECTION);
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
	
	public void selectAllCourseModules() {
		listViewCourseModule.getSelectionModel()
				.selectAll();
	}
	
	public ListView<CourseModule> getListView(){
		return listViewCourseModule;
	}

}
